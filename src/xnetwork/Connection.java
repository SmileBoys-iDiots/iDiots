package xnetwork;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements SelectorObject, CallbackErrorHandler {
	protected final SelectorThread _selector;
	private final SocketChannel _sc;
	private ByteBuffer _inBuffer;
	private ByteBuffer _outBuffer;
	private ConcurrentLinkedQueue<byte[]> _reserveOutBuffer;
	private AtomicInteger _sendBufferCount;
	private AtomicInteger _sendCalled;

	private static final int BUFFER_SIZE = 64 * 1024; // 대략 BufferSize는 64k

	private final ConnectionHandler _handler;

	public SocketChannel getSocketChannel() {
		return _sc;
	}

	public String getHostAddress() {
		return _sc.socket().getInetAddress().getHostAddress();
	}

	public Connection(SocketChannel socketChannel, SelectorThread selector,
			ConnectionHandler handler) throws IOException {

		this._selector = selector;
		this._sc = socketChannel;
		this._handler = handler;

		_inBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		_outBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

		_outBuffer.flip();

		_reserveOutBuffer = new ConcurrentLinkedQueue<byte[]>();
		_sendBufferCount = new AtomicInteger(0);
		_sendCalled = new AtomicInteger(0);

		selector.registerChannelNow(_sc, 0, this);

	}

	public boolean isOpen() {
		return _sc.isOpen();
	}

	public void send(final byte[] buffer) {
		if (!_sc.isOpen()) {
			return;
		}

		_sendBufferCount.addAndGet(1);

		_reserveOutBuffer.add(buffer);

		if (!_sendCalled.compareAndSet(0, 1)) {
			return;
		}

		_selector.invokeLater(new Runnable() {
			@Override
			public void run() {
				send();
			}
		});
	}

	void send() {
		try {
			if (!_sc.isOpen()) {
				return;
			}

			byte[] buffer;

			while (true) {
				_outBuffer.clear();
				while (true) {
					buffer = _reserveOutBuffer.peek();

					if (buffer == null) {
						break;
					}

					if (_outBuffer.remaining() < buffer.length) {
						break;
					}

					_outBuffer.put(buffer);

					_reserveOutBuffer.remove();
					_sendBufferCount.decrementAndGet();
				}

				_outBuffer.flip();

				if (_outBuffer.hasRemaining()) {
					_sc.write(_outBuffer);

					if (_outBuffer.hasRemaining()) {
						requestWrite();
						return;
					}
				}

				if (_sendBufferCount.get() == 0) {
					_sendCalled.set(0);
				} else {
					continue;
				}

				if (_sendBufferCount.get() > 0) {
					if (!_sendCalled.compareAndSet(0, 1)) {
						break;
					}
				} else {
					break;
				}
			}
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			close();
		}
	}

	public void handleRead() {
		try {
			int readBytes = _sc.read(_inBuffer);
			if (readBytes == -1) {
				close();
				return;
			}

			if (readBytes == 0) {
				return;
			}

			_inBuffer.flip();
			_handler.onRecv(this, _inBuffer);

			if (_inBuffer.hasRemaining()) // 처리 안한거 있으면
			{
				byte[] buffer = new byte[_inBuffer.remaining()];
				_inBuffer.get(buffer);
				_inBuffer.clear();
				_inBuffer.put(buffer);
			} else {
				_inBuffer.clear();
			}

			_selector.addChannelInterestNow(_sc, SelectionKey.OP_READ);

		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			// Serious error. Close socket.
			close();
		}
	}

	public void handleWrite() {
		try {
			if (_outBuffer.hasRemaining()) {
				_sc.write(_outBuffer);
				if (_outBuffer.hasRemaining()) {
					requestWrite();
				} else {
					send();
				}
			} else {
				send();
			}
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			close();
		}
	}

	public void close() {
		_selector.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (!_sc.isOpen()) {
						return;
					}
					_sc.close();
					_handler.onDisconnect(Connection.this);
				} catch (Exception ex) {
					_log.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				}

			}
		});
	}

	private static Logger _log = Logger.getLogger(Connection.class.getName());

	public void resumeRecv() throws IOException {
		_selector.addChannelInterestLater(_sc, SelectionKey.OP_READ, this);
	}

	private void requestWrite() throws IOException {
		_selector.addChannelInterestNow(_sc, SelectionKey.OP_WRITE);
	}

	@Override
	public void handleError(Exception ex) {
		close();
	}
}