package server.WebProtocol;

import java.util.StringTokenizer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import server.WebClient;

public class WebPacketDecoder implements ProtocolDecoder {

	@Override
	public synchronized void decode(IoSession session, IoBuffer buffer,
			ProtocolDecoderOutput out) {
		try {
			int size = buffer.limit();
			if (size > 2048) {
				StringTokenizer st = new StringTokenizer(session
						.getRemoteAddress().toString().substring(1), ":");
				String ip = st.nextToken();
				System.out.println("웹서버 패킷길이 2048byte이상 절단. [ip] :" + ip);
				return;
			}

			byte[] data = buffer.array();

			WebClient wc = new WebClient(data);
			// wc.clear();
			wc = null;
			session.close();
			/*
			 * 시세 창에서 버튼 클릭시 1byte(opcode)0x00 유저 시세검색후 텔타기위해 버튼 클릭 (data1) 클릭한
			 * 유저이름(string) 끝에 0x00붙임 (data2) 클릭한 버튼의 판매자 이름(string) 끝에 0x00붙임
			 * 
			 * 혈맹 창에서 가입 신청 버튼 클릭시 1byte(opcode)0x01 가입을 위해 버튼을 클릭 1byte(type)
			 * 0x00 (data1) 클릭한 유저이름(string) 끝에 0x00붙임 (data2) 클릭한 버튼의 혈맹
			 * 이름(string) 끝에 0x00붙임
			 * 
			 * 
			 * 혈맹 신청 창에서 신청취소 버튼 클릭시 1byte(opcode)0x01 신청 취소를 위해 버튼을 클릭
			 * 1byte(type) 0x01 (data1) 클릭한 유저이름(string) 끝에 0x00붙임 (data2) 클릭한
			 * 버튼의 혈맹 이름(string) 끝에 0x00붙임
			 */
			// System.out.println("[server]\r\n"+printData(data, size));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose(IoSession client) throws Exception {

	}

	@Override
	public void finishDecode(IoSession client, ProtocolDecoderOutput output)
			throws Exception {

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
}
