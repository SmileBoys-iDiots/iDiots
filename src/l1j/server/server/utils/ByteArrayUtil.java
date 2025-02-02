/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.utils;

// 전부 static로 해도 괜찮을지도 모른다
public class ByteArrayUtil {
	private final byte[] _byteArray;

	public ByteArrayUtil(byte[] byteArray) {
		_byteArray = byteArray;
	}

	public String getTerminatedString(int i) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int j = i; j < _byteArray.length && _byteArray[j] != 0; j++) {
			stringbuffer.append((char) _byteArray[j]);
		}

		return stringbuffer.toString();
	}

	public String dumpToString() {
		StringBuffer stringbuffer = new StringBuffer();
		int j = 0;
		for (int k = 0; k < _byteArray.length; k++) {
			if (j % 16 == 0) {
				stringbuffer.append((new StringBuilder()).append(fillHex(k, 4))
						.append(": ").toString());
			}
			stringbuffer.append((new StringBuilder())
					.append(fillHex(_byteArray[k] & 0xff, 2)).append(" ")
					.toString());
			if (++j != 16) {
				continue;
			}
			stringbuffer.append("   ");
			int i1 = k - 15;
			for (int l1 = 0; l1 < 16; l1++) {
				byte byte0 = _byteArray[i1++];
				if (byte0 > 31 && byte0 < 128) {
					stringbuffer.append((char) byte0);
				} else {
					stringbuffer.append('.');
				}
			}

			stringbuffer.append("\n");
			j = 0;
		}

		int l = _byteArray.length % 16;
		if (l > 0) {
			for (int j1 = 0; j1 < 17 - l; j1++) {
				stringbuffer.append("   ");
			}

			int k1 = _byteArray.length - l;
			for (int i2 = 0; i2 < l; i2++) {
				byte byte1 = _byteArray[k1++];
				if (byte1 > 31 && byte1 < 128) {
					stringbuffer.append((char) byte1);
				} else {
					stringbuffer.append('.');
				}
			}

			stringbuffer.append("\n");
		}
		return stringbuffer.toString();
	}

	private String fillHex(int i, int j) {
		String s = Integer.toHexString(i);
		for (int k = s.length(); k < j; k++) {
			s = (new StringBuilder()).append("0").append(s).toString();
		}

		return s;
	}
}
