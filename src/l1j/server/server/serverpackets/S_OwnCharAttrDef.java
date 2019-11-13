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

package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_OwnCharAttrDef extends ServerBasePacket {

	private static final String S_OWNCHARATTRDEF = "[S] S_OwnCharAttrDef";
	private byte[] _byte = null;

	public S_OwnCharAttrDef(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_AC);
		writeD(pc.getAC().getAc());
		writeH(pc.getResistance().getFire());
		writeH(pc.getResistance().getWater());
		writeH(pc.getResistance().getWind());
		writeH(pc.getResistance().getEarth());
		int AcDg = ((pc.getAC().getAc() * -1) - 100) / 10;
		if(AcDg > 0){
			writeC(pc.getDg() + AcDg + pc.getINFIDg());
		}else{
			writeC(pc.getDg() + pc.getINFIDg());
		}
		
		if(AcDg > 0){
			writeC(pc.get_PlusEr() + AcDg);
		}else{
			writeC(pc.get_PlusEr());
		}
		writeH(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_OWNCHARATTRDEF;
	}
}
