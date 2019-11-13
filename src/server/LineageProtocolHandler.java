package server;

import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.serverpackets.KeyPacket;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.utils.SystemUtil;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class LineageProtocolHandler extends IoHandlerAdapter {

	// 클라이언트가 서버에 접속하여 통신을 시작하기 직전임
	@Override
	public void sessionCreated(IoSession session) {
		try {
			StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
			String ip = st.nextToken();
			System.out.println("MINA : 소켓 접속 IP : " + ip + "  Thread.C : " + Thread.activeCount() + " UseMemory : " + SystemUtil.getUsedMemoryMB() + " MB");
			if (IpTable.getInstance().isBannedIp(ip)) {
				System.out.println("Banned Ip 접속시도 :" + ip);
				session.close(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * private static final byte[] key = { (byte) 0xB6, (byte) 0x8F, (byte)
	 * 0x9A, (byte) 0x6F, (byte) 0x05, (byte) 0x0A, (byte) 0x12, (byte) 0xF1,
	 * (byte) 0x29 };
	 */

	/***
	 * 소켓 티값 처리 부분 
	 */
	@Override
	public void sessionOpened(IoSession session) {
		try {
			if (!Config.shutdown && !session.isClosing()) {
				long seed = Config.SeedVal;
				KeyPacket key = new KeyPacket();
				LineageClient lc = new LineageClient(session, seed);
				session.write(key);
				session.setAttribute(LineageClient.CLIENT_KEY, lc);
			} else sessionClosed(session);
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+" sessionOpened(IoSession session)\r\n"+e.toString(),
			// Config.LOG.error);
		}
	}

	// 전송 요청한 메시지 개체가 바이트 버퍼로 변환되어 클라이언트에 전송되었음
	@Override
	public void messageSent(IoSession session, Object message) {
		if (Config.PACKET) {
			ServerBasePacket bp = (ServerBasePacket) message;
			byte[] data = bp.getBytes();
			System.out.println("[server]\r\n" + printData(data, data.length));
		}
	}

	// 클라이언트가 데이터를 전송하여 그 내용이 메시지 개체로 변환 되었음
	@Override
	public void messageReceived(IoSession session, Object message) {
		// 디코더 처리후 바로 패킷 처리로 가기 때문에 여긴 필요 없음.
	}

	// 클라이언트의 서버 사이의 접속이 해제되었음
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LineageClient lc = (LineageClient) session.getAttribute(LineageClient.CLIENT_KEY);
		if (lc != null) session.close(true);;
	}

	// 클라이언트와 접속이 된 상태이나 I/O가 없는 유휴 상태임
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// sessionClosed(session);
		// Logger.getInstance().error(getClass().toString()+" sessionIdle( IoSession session, IdleStatus status )\r\n"+status.toString(),
		// Config.LOG.system);
	}

	// 통신 도중 또는 이벤트 처리 도중 예외가 발생했음
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		try {
			sessionClosed(session);
			// Logger.getInstance().error(getClass().toString()+" exceptionCaught(IoSession session, Throwable cause)\r\n"+cause.toString(),
			// Config.LOG.system);
		} catch (Exception e) {
		}
	}

	public String printData(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}

		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}
		return result.toString();
	}

	private String fillHex(int data, int digits) {
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}
		return number;
	}

	class ip {
		public String ip;
		public int count;
		public long time;
		public boolean block;
	}
}
