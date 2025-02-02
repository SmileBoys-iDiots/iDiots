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
 

package l1j.server.server.clientpackets;

import l1j.server.server.model.L1Party;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_ServerMessage;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Party extends ClientBasePacket {

	private static final String C_PARTY = "[C] C_Party";

	public C_Party(byte abyte0[], LineageClient clientthread) {
		super(abyte0);
		try {
			L1PcInstance pc = clientthread.getActiveChar();
			if (pc.isGhost()) {
				return;
			}

			L1Party party = pc.getParty();
			if (pc.isInParty()) {
				S_Party sp = new S_Party("party", pc.getId(), party.getLeader()
						.getName(), party.getMembersNameList());
				pc.sendPackets(sp, true);
			} else {
				S_ServerMessage sm = new S_ServerMessage(425);
				pc.sendPackets(sm, true);
				// pc.sendPackets(new S_Party("party", pc.getId()));
			}
		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	@Override
	public String getType() {
		return C_PARTY;
	}

}
*/