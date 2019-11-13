package server.mina;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.serverpackets.ServerBasePacket;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import server.LineageClient;

public class LineagePacketEncoder implements ProtocolEncoder {

	public synchronized void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		try {
			// 동기화 해야함.
			if (message != null) {
				LineageClient client = (LineageClient) session
						.getAttribute(LineageClient.CLIENT_KEY);
				IoBuffer buffer = null;
				ServerBasePacket bp = (ServerBasePacket) message;
				if (bp != null) {
					// System.out.println(bp.getType());
					/*
					 * if(bp.getType().equalsIgnoreCase("[S] S_문장주시")){// ||
					 * bp.getType().equalsIgnoreCase("[S] S_OwnCharAttrDef")){
					 * System.out.println("리턴"); return; }
					 */
					int length = bp.getLength();
					if (length <= 2)
						return;
					// sp.requestWork(client, bp.getBytes(), out);
					// 13_05_10
					if (client != null) {
						 buffer = buffer(client.encryptE(bp.getBytes()), length);  // 조금전 검색했던 부분이 이 안으로 들어갔습니다.
					} else {
						buffer = buffer(bp.getBytes(), length);
					}
					out.write(buffer);
				}
				/*
				 * byte[] date = (byte[]) message;
				 * System.out.println(date.length); IoBuffer buffer =
				 * list.get(date.length-1); buffer.put(date); buffer.flip();
				 * out.write(buffer);
				 */
			}
		} catch (Exception e) {
			// e.printStackTrace();
			// Logger.getInstance().error(getClass().toString()+" putClient(LineageClient c)\r\n"+e.toString(),
			// Config.LOG.error);
		}
	}

	public class senderPacketer implements Runnable {
		private final Queue<cli> _queue;
		private boolean on = false;

		public senderPacketer() {
			_queue = new ConcurrentLinkedQueue<cli>();
		}

		public void requestWork(LineageClient client, byte data[],
				ProtocolEncoderOutput out) {
			cli c = new cli(client, data.clone(), out);
			_queue.offer(c);
			if (!on) {
				on = true;
				GeneralThreadPool.getInstance().execute(this);
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (this) {
						cli c = _queue.poll();
						if (c == null) {
							break;
						}
						System.out.println("처리");
						IoBuffer buffer;
						int length = c.data.length + 2;
						if (c.client != null) {
							buffer = buffer(c.client.encryptE(c.data), length);
						} else {
							buffer = buffer(c.data, length);
						}

						// System.out.println("C11 -> S \n"+PacketHandler.DataToPacket(buffer.array(),
						// length)); // 사용 처리
						// System.out.println(buffer.array()):
						c.out.write(buffer);
						// c.clear();
						// c = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("종료");
			on = false;
		}

		class cli {
			public LineageClient client = null;
			public byte[] data = null;
			public ProtocolEncoderOutput out = null;

			public cli(LineageClient c, byte[] d, ProtocolEncoderOutput p) {
				client = c;
				data = d;
				out = p;
			}

			public void clear() {
				client = null;
				data = null;
				out = null;
			}
		}
	}

	public void dispose(IoSession client) throws Exception {

	}

	private IoBuffer buffer(byte[] data, int length) {
		byte[] size = new byte[2];
		size[0] |= length & 0xff;
		size[1] |= length >> 8 & 0xff;
		IoBuffer buffer = IoBuffer.allocate(length, false);
		buffer.put(size);
		buffer.put(data);
		buffer.flip();
		size = null;
		return buffer;
	}

}
