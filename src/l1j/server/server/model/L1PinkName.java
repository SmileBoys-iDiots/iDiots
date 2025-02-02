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
package l1j.server.server.model;

import l1j.server.server.GeneralThreadPool;

// Referenced classes of package l1j.server.server.model:
// L1PinkName

public class L1PinkName {
	private L1PinkName() {
	}

	public static void onAction(L1Character pc, L1Character cha) {
		GeneralThreadPool.getInstance().execute(new L1PinkNameThread(pc, cha));
		/*
		 * if (pc == null || cha == null) return; if (!(cha instanceof
		 * L1PcInstance)) return;
		 * 
		 * L1PcInstance attacker = (L1PcInstance) cha; //if (pc.getId() ==
		 * attacker.getId()) return; if (attacker.getFightId() ==
		 * pc.getId())return;
		 * 
		 * boolean isNowWar = false; int castleId =
		 * L1CastleLocation.getCastleIdByArea(pc); if (castleId != 0) { isNowWar
		 * = WarTimeController.getInstance().isNowWar(castleId); }
		 * 
		 * if (CharPosUtil.getZoneType(pc) != 1 &&
		 * CharPosUtil.getZoneType(attacker) != 1 && isNowWar == false) {
		 * attacker.setPinkName(true); attacker.sendPackets(new
		 * S_PinkName(attacker.getId(), 30), true); //76762626
		 * //attacker.sendPackets(new S_SystemMessage("보라돌이 30초 패킷 보냄.")); if
		 * (!attacker.isGmInvis()) { Broadcaster.broadcastPacket(attacker, new
		 * S_PinkName(attacker.getId(), 30), true); }
		 * if(attacker.getSkillEffectTimerSet
		 * ().hasSkillEffect(L1SkillId.STATUS_PINK_NAME))
		 * attacker.getSkillEffectTimerSet
		 * ().killSkillEffectTimer(L1SkillId.STATUS_PINK_NAME);
		 * attacker.getSkillEffectTimerSet
		 * ().setSkillEffect(L1SkillId.STATUS_PINK_NAME, 30 * 1000); }
		 */
	}
}
