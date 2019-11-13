package xnetwork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

final public class Acceptor implements SelectorObject {
	private ServerSocketChannel _ssc;
	private final SelectorThread _selector;
	private final int _listenPort;
	private final AcceptorHandler _handler;

	public Acceptor(int listenPort, SelectorThread ioThread,
			AcceptorHandler listener) {
		_selector = ioThread;
		_listenPort = listenPort;
		_handler = listener;
	}

	/*
	 * public boolean ChangePort(int port){ try{ _ssc.wait(); _ssc.bind(new
	 * InetSocketAddress(port)); System.out.println(":: Game 서버가 "+ port
	 * +"번 포트를 이용해서 재가동 되었습니다.  : Memory : " + SystemUtil.getUsedMemoryMB() +
	 * " MB"); return true; }catch(Exception e){ e.printStackTrace(); } return
	 * false; }
	 */

	public void startAccept() throws IOException {

		_ssc = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(_listenPort);
		_ssc.socket().bind(isa, 200);

		_selector.registerChannelLater(_ssc, SelectionKey.OP_ACCEPT, this,
				new CallbackErrorHandler() {
					public void handleError(Exception ex) {
						_handler.onError(Acceptor.this, ex);
					}
				});
	}

	public String toString() {
		return "ListenPort: " + _listenPort;
	}

	public void handleAccept() {

		SocketChannel sc = null;
		try {
			sc = _ssc.accept();
		} catch (IOException e) {
			_handler.onError(this, e);
		}

		try {
			_selector.addChannelInterestNow(_ssc, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			_handler.onError(this, e);
		}

		if (sc != null) {
			try {
				sc.socket().setReceiveBufferSize(2 * 1024);
				sc.socket().setSendBufferSize(2 * 1024);

				_handler.onAccept(this, sc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			_selector.invokeAndWait(new Runnable() {
				public void run() {
					if (_ssc != null) {
						try {
							_ssc.close();
						} catch (IOException e) {
						}
					}
				}
			});
		} catch (InterruptedException e) {
		}
	}
}