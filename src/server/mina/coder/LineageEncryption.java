package server.mina.coder;

import server.mina.coder.types.UChar8;

public class LineageEncryption {
	private LineageBlowfish _LineageBlowfish;
	private UChar8 uc8;

	private long[] decodeKey = { 0, 0 };
	private byte[] client_HashKey = new byte[256]; // 서버 암호화에 이용

	private final byte[] db = new byte[8];

	public boolean le;

	public LineageEncryption() {
		uc8 = new UChar8();
		_LineageBlowfish = new LineageBlowfish();
	}

	public UChar8 getUChar8() {
		return uc8;
	}

	public void initKeys(long l) {
		long al[] = { l, 0x930FD7E2L };
		_LineageBlowfish.getSeeds(al);

		decodeKey[0] = al[0];
		decodeKey[1] = al[1];

		for (int i = 0; i < decodeKey.length; i++) {
			for (int j = 0; j < 4; j++) {
				this.db[(i * 4) + j] = (byte) (decodeKey[i] >> (j * 8) & 0xff);
			}
		}

		/* initiate the HashKey4Client */
		char[] tk = getUChar8().fromArray(al);

		init_enc_hashkey(tk);

		// System.arraycopy(hashkey, 0, client_HashKey, 0, 256);

		// keyMap4client.put(clientID, hashkey);
		/* HashKey4Client ended */

		le = true;
	}

	/**
	 * initiate hashkey for client updated 2012-11-28
	 */
	private void init_enc_hashkey(char[] currentkey) {

		int k = 0;
		// init hash key;

		for (int i = 0; i < 256; i++)
			client_HashKey[i] = (byte) i;

		for (int j = 0; j < 256; j++) {

			k = (client_HashKey[j] + k + currentkey[j % 8]) & 0xFF;

			byte tmp = client_HashKey[k];
			client_HashKey[k] = client_HashKey[j];
			client_HashKey[j] = tmp;
		}
	}

	public char[] encrypt(char[] buf) {
		int i;
		int k = 0;
		int j = 0;
		int m = 0;
		int o = 0;
		try {
			for (i = 0; i < buf.length; i++) {
				m = i + 1;
				o = m / 256;
				m = m - (o * 256);

				j = (m) & 0xFF;
				k += client_HashKey[m];
				k = k & 0xFF;
				// update HashKey by swapping position
				byte tk = client_HashKey[j];
				client_HashKey[j] = client_HashKey[k];
				client_HashKey[k] = tk;

				int b3 = client_HashKey[j];
				int b4 = client_HashKey[k];

				b3 += b4;
				b3 = b3 & 0xFF;

				byte b5 = client_HashKey[b3];
				byte b6 = (byte) buf[i];
				b6 = (byte) (b6 ^ b5);
				buf[i] = (char) b6;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return buf;
	}

	public byte[] encrypt_S(byte[] buf) {
		int i;
		int k = 0;
		int j = 0;
		int m = 0;
		int o = 0;
		try {
			for (i = 0; i < buf.length; i++) {
				m = i + 1;
				o = m / 256;
				m = m - (o * 256);

				j = (m) & 0xFF;
				k += client_HashKey[m];
				k = k & 0xFF;
				// update HashKey by swapping position
				byte tk = client_HashKey[j];
				client_HashKey[j] = client_HashKey[k];
				client_HashKey[k] = tk;

				int b3 = client_HashKey[j];
				int b4 = client_HashKey[k];

				b3 += b4;
				b3 = b3 & 0xFF;

				byte b5 = client_HashKey[b3];
				byte b6 = buf[i];
				b6 = (byte) (b6 ^ b5);
				buf[i] = b6;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return buf;
	}

	/*
	 * public byte[] encryptsabu(final byte[] data, final int len){ int lLen =
	 * data.length; try { if(Config.mainenc){ if(lLen < 2){return data;} int i1,
	 * j1; if(lLen < 8){ if(lLen < 4){ if(lLen == 2){ data[1] ^= data[0]; }else{
	 * for(i1 = 2; i1 < lLen; i1++){ data[i1] ^= data[i1 % 2]; } } }else{ for(i1
	 * = 4; i1 < lLen; i1++){ data[i1] ^= data[i1 % 4]; } } }else{ data[0] +=
	 * (byte) (Key1 & 0xFF); data[1] += (byte) ((Key1>>8) & 0xFF); data[2] +=
	 * (byte) ((Key1>>16) & 0xFF); data[3] += (byte) ((Key1>>24) & 0xFF);
	 * 
	 * data[4] += (byte) (Key2 & 0xFF); data[5] += (byte) ((Key2>>8) & 0xFF);
	 * data[6] += (byte) ((Key2>>16) & 0xFF); data[7] += (byte) ((Key2>>24) &
	 * 0xFF); for(i1 = 8; i1 < lLen; i1++){ data[i1] = (byte) (data[i1] ^
	 * data[i1 % 4]); } } } } catch (Exception e) { e.printStackTrace(); }
	 * return data; }
	 */

	public static final long toLong(byte[] byteArray, int offset, int len) {
		long val = 0;
		len = Math.min(len, 8);
		for (int i = (len - 1); i >= 0; i--) {
			val <<= 8;
			val |= (byteArray[offset + i] & 0x00FF);
		}
		return val;
	}

	public byte[] DecSabuData(final byte[] data, final int len, final int type) {
		int lLen = len;
		// if(Config.mainenc){
		if (lLen < 2) {
			return data;
		}
		int i, j;
		long dwKey1;
		long dwKey2;
		long dwKey3;
		long dwKey4;
		if (lLen <= 8) {
			if (lLen < 4) {
				if (lLen == 2) {
					data[1] ^= data[0];
				} else {
					for (i = 2; i < lLen; i++) {
						data[i] ^= data[i % 2];
					}
				}
			} else {
				for (i = 4; i < lLen; i++) {
					data[i] ^= data[i % 4];
				}
			}
		} else {
			// dwKey = (toLong(data, 0, data.length)) ^ (toLong(data, 0,
			// data.length) + 4);
			// dwKey = toLong(data, 0, 4);// ^ toLong(data, 4, 1);//data[4];
			dwKey1 = data[0] ^ data[4];
			dwKey2 = data[1] ^ data[5];
			dwKey3 = data[2] ^ data[6];
			dwKey4 = data[3] ^ data[7];

			// System.out.println("dwkey : "+ dwKey1);
			// System.out.println("Key2 : "+ Key2);
			// long Key2=0x1475C917; //4byte
			// long temp1 = toLong(data, 0, data.length);
			// long temp2 = toLong(data, 0, data.length)+4;
			// System.out.println(temp1);
			// System.out.println(temp2);
			for (i = 8; i < lLen; i++) {
				// data[i] ^= (data[0] ^ data[4]) >> 8 * (3 - (i % 4));
				if (3 - (i % 4) == 3) {// 8 ... 3
					// System.out.println(i);
					// data[i] ^= (byte)((dwKey >> 24) & 0xFF);
					// data[i] ^= data[3 - (i % 4)];//(byte)(dwKey & 0xFF);
					data[i] ^= (byte) ((dwKey4) & 0xFF);// >> 8
				} else if (3 - (i % 4) == 2) {// 9...2
					// data[i] ^= (byte)((dwKey >> 16) & 0xFF);
					data[i] ^= (byte) ((dwKey3) & 0xFF);// >> 16
				} else if (3 - (i % 4) == 1) {// 10...1
					// data[i] ^= (byte)((dwKey >> 8) & 0xFF);
					data[i] ^= (byte) ((dwKey2) & 0xFF);// >> 24
				} else {
					data[i] ^= (byte) (dwKey1 & 0xFF);
				}
				// data[i] ^= (byte)(dwKey >> 8 * (3 - (i % 4)));
				// data[i] ^= dwKey >> 8 * data[4];//3 -(i % 4)
			}//
				// (BYTE)(pbData + i) ^= (BYTE)(dwKey 8 (3 - (i % 4)));

			data[0] -= (byte) (Keys1[type] & 0xFF);
			data[1] -= (byte) ((Keys1[type] >> 8) & 0xFF);
			data[2] -= (byte) ((Keys1[type] >> 16) & 0xFF);
			data[3] -= (byte) ((Keys1[type] >> 24) & 0xFF);
			data[4] -= (byte) (Keys2[type] & 0xFF);
			data[5] -= (byte) ((Keys2[type] >> 8) & 0xFF);
			data[6] -= (byte) ((Keys2[type] >> 16) & 0xFF);
			data[7] -= (byte) ((Keys2[type] >> 24) & 0xFF);

		}
		// }
		return data;
	}

	final static long[] Keys1 = { 0xC4F44841, 0x48112477, 0x14D18411,
			0x04417170 };
	final static long[] Keys2 = { 0x115711E4, 0x02157844, 0x11305031,
			0x87777877 };

	public byte[] decrypt(final byte[] data) {
		data[0] ^= this.db[5] ^ data[1];
		data[1] ^= this.db[4] ^ data[2];
		data[2] ^= this.db[3] ^ data[3];
		data[3] ^= this.db[2];
		for (int i = data.length - 1; i >= 1; i--) {
			data[i] ^= data[i - 1] ^ this.db[i & 7];
		}
		data[0] ^= this.db[0];

		int size = data.length - 4;

		byte buf11[] = new byte[size];
		System.arraycopy(data, 4, buf11, 0, size);

		this.update(this.db, buf11);

		return buf11;
	}

	private void update(final byte[] data, final byte[] ref) {
		for (int i = 0; i < 4; i++) {
			data[i] ^= ref[i];
		}
		final int int32 = (((data[7] & 0xFF) << 24) | ((data[6] & 0xFF) << 16)
				| ((data[5] & 0xFF) << 8) | (data[4] & 0xFF)) + 0x287effc3;
		for (int i = 0; i < 4; i++) {
			data[i + 4] = (byte) (int32 >> (i * 8) & 0xff);
		}
	}

	public void clear() {
		_LineageBlowfish = null;
		uc8 = null;
		decodeKey = null;
		client_HashKey = null;
	}
	/*
	 * public char[] decrypt(char ac[], int size){ _decrypt(ac, size); char
	 * buf1[] = new char[size -4]; System.arraycopy(ac, 4, buf1, 0, ac.length
	 * -4); long l = ul32.fromArray(buf1); decodeKey[0] ^= l; decodeKey[1] =
	 * ul32.add(decodeKey[1], 0x287effc3L); return buf1; }
	 * 
	 * 
	 * 
	 * private char[] _decrypt(char ac[], int size){ Dac1 =
	 * uc8.fromArray(decodeKey, Dac1); char c = ac[3]; ac[3] ^= Dac1[2]; char c1
	 * = ac[2]; ac[2] ^= c ^ Dac1[3]; char c2 = ac[1]; ac[1] ^= c1 ^ Dac1[4];
	 * char c3 = (char)(ac[0] ^ c2 ^ Dac1[5]); ac[0] = (char)(c3 ^ Dac1[0]);
	 * for(int j = 1; j < size; j++){ char c4 = ac[j]; ac[j] ^= Dac1[j & 7] ^
	 * c3; c3 = c4; } return ac; }
	 */

}