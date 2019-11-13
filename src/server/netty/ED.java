package server.netty;

public class ED {
	private final static int _1 = 0x9c30d539;
	private final static int _2 = 0x930fd7e2;
	private final static int _3 = 0x7c72e993;
	private final static int _4 = 0x287effc3;
	private final byte[] eb = new byte[8];
	private final byte[] db = new byte[8];
	private final byte[] tb = new byte[4];

	public ED(final int key) {
		final int[] keys = { key ^ _1, _2 };
		keys[0] = Integer.rotateLeft(keys[0], 0x13);
		keys[1] ^= keys[0] ^ _3;
		for (int i = 0; i < keys.length; i++) {
			for (int j = 0; j < this.tb.length; j++) {
				this.eb[(i * 4) + j] = this.db[(i * 4) + j] = (byte) (keys[i] >> (j * 8) & 0xff);
			}
		}
	}

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

		byte buf1[] = new byte[size];
		System.arraycopy(data, 4, buf1, 0, size);

		this.update(this.db, buf1);
		return buf1;
	}

	public byte[] encrypt(final byte[] data) {

		// /fromArray
		for (int i = 0; i < this.tb.length; i++) {
			this.tb[i] = data[i];
		}

		// encrypt
		data[0] ^= this.eb[0];
		for (int i = 1; i < data.length; i++) {
			data[i] ^= data[i - 1] ^ this.eb[i & 7];
		}
		data[3] ^= this.eb[2];
		data[2] ^= this.eb[3] ^ data[3];
		data[1] ^= this.eb[4] ^ data[2];
		data[0] ^= this.eb[5] ^ data[1];

		// keySet
		this.update(this.eb, this.tb);
		return data;
	}

	private void update(final byte[] data, final byte[] ref) {
		for (int i = 0; i < this.tb.length; i++) {
			data[i] ^= ref[i];
		}
		final int int32 = (((data[7] & 0xFF) << 24) | ((data[6] & 0xFF) << 16)
				| ((data[5] & 0xFF) << 8) | (data[4] & 0xFF))
				+ _4;
		for (int i = 0; i < this.tb.length; i++) {
			data[i + 4] = (byte) (int32 >> (i * 8) & 0xff);
		}
	}
}
