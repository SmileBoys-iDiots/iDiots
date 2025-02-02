/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FIT^ESS VOR A PARTICULAR PURPOSE.  See the
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

import java.util.logging.Logger;

import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_FishCancel extends ClientBasePacket {

	private static final String C_FISH_CANCEL = "[C] C_FishCancel";
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(C_UsePetItem.class.getName());

	public C_FishCancel(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);

		try {
			L1PcInstance pc = clientthread.getActiveChar();
			if (!pc.isFishing())
				return;
			pc.setFishingTime(0);
			pc.setFishingReady(false);
			pc.setFishing(false);
			pc.setFishingItem(null);
			pc.sendPackets(new S_CharVisualUpdate(pc), true);
			Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc), true);
			FishingTimeController.getInstance().removeMember(pc);
		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	@Override
	public String getType() {
		return C_FISH_CANCEL;
	}
}
