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

package l1j.server.server.clientpackets;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1AuctionBoardInstance;
import l1j.server.server.model.Instance.L1BoardInstance;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_Board

public class C_Board extends ClientBasePacket {

	private static final String C_BOARD = "[C] C_Board";

	private boolean isBoardInstance(L1Object obj) {
		return (obj instanceof L1BoardInstance || obj instanceof L1AuctionBoardInstance);
	}

	public C_Board(byte abyte0[], LineageClient client) {
		super(abyte0);
		try {
			int objectId = readD();
			L1Object obj = L1World.getInstance().findObject(objectId);
			if (!isBoardInstance(obj)) {
				return; // 부정 클라이언트가 아니면 있을 수 없겠지만···
			}
			obj.onAction(client.getActiveChar());
		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	@Override
	public String getType() {
		return C_BOARD;
	}

}
