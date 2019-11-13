/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.serverpackets;

import l1j.server.MJCTSystem.Loader.MJCTLoadManager;
import l1j.server.server.Opcodes;
import l1j.server.server.model.ItemClientCode;
import l1j.server.server.model.Instance.L1ItemInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_AddItemDoll extends ServerBasePacket {

	private static final String S_ADD_ITEM = "[S] S_AddItem";

	String s = "00 00 EB 00 01 01 00 00 00 81 "
			+ "24 35 30 38 00 06 17 14 02 00 00 00 12 00 00 00 "
			+ "00 00 00 DF F8 4F 96 00 00 00 00 00 00 00 00";

	/**
	 * 목록에 아이템을 1개 추가한다.
	 */
	public S_AddItemDoll(L1ItemInstance item) {
		writeC(Opcodes.S_ADD_INVENTORY);
		writeD(item.getId());
		/*
		 * if(ItemClientCode.code(item.getItemId()) == 0){
		 * writeH(item.getItem().getItemDescId()); }else{
		 * writeH(ItemClientCode.code(item.getItemId())); }
		 */
		writeH(ItemClientCode.code(item.getItemId()));

		if (item.getItemId() == 40008) {
			writeH(0x0827);
		} else if (item.getItemId() == 5559 || item.getItemId() == 5560 || item.getItemId() == MJCTLoadManager.CTSYSTEM_STORE_ID) {
			writeH(0x0044);
		} else {
			writeC(item.getItem().getUseType());
			if (item.getItemId() >= 40859 && item.getItemId() <= 40898) {
				writeC(item.getItemId() - 40859);
			} else {
				writeC(0);
			}
		}

		writeH(item.get_gfxid());
		writeC(item.getBless());
		writeD(item.getCount());

		// 2: 교환 불가 4: 삭제불가 8: 인첸불가 16: 창고보관가능 32: 봉인상태 64: 특수봉인상태

		// if(item.isIdentified()){
		if (item.getBless() >= 128 && item.getBless() <= 131) {
			writeC(32);
		} else {
			int type = 0;
			if (item.getItem().get_safeenchant() == -1)
				type += 8;
			if (item.getItem().isCantDelete())
				type += 4;
			if (!item.getItem().isTradable()) {
				type += 2;
				if ((item.getItemId() >= 437010 && item.getItemId() <= 437013)
						/**기사단 장비*/
						|| (item.getItemId() >= 60716 && item.getItemId() <= 60725)
						|| (item.getItemId() >= 30098 && item.getItemId() <= 30104)
						|| (item.getItemId() >= 20615 && item.getItemId() <= 20626)
						/**기사단장비*/
						|| item.getItemId() == 5000067
						|| item.getItemId() == 60104
						|| (item.getItemId() >= 21125 && item.getItemId() <= 21136)
						|| (item.getItemId() >= 20452 && item.getItemId() <= 20455)
						|| (item.getItemId() >= 421000 && item.getItemId() <= 421020)
						|| item.getItemId() == 560025 || item.getItemId() == 888819
						|| item.getItemId() == 560027
						|| item.getItemId() == 560028
						|| item.getItemId() == 1437010 || item.getItemId() == 1437011 || item.getItemId() == 60291
						|| item.getItemId() == 600369
						|| (item.getItemId() >= 21139 && item.getItemId() <= 21156)
						|| (item.getItemId() >= 60286 && item.getItemId() <= 60289)
						|| (item.getItemId() >= 60261 && item.getItemId() <= 60263)
						|| item.getItemId() == 60354 || item.getItemId() == 9096
						|| item.getItemId() == 60396
						|| item.getItemId() == 60398
						|| (item.getItemId() >= 66757 && item.getItemId() <= 66788)//룸티스냅퍼
						|| (item.getItemId() >= 60427 && item.getItemId() <= 60444)
						|| (item.getItemId() >= 60447 && item.getItemId() <= 60472)
						|| item.getItemId() == 21256
						|| item.getItemId() == 21257
						|| item.getItemId() == 60492) {
					type += 16;
				}
			}
			if (type == 0) // type = 129;
				type = 128;
			writeC(type);
		}
		// }else
		// writeC(0x00);
		writeS(item.getViewName());
		if (!item.isIdentified()) {
			// 미감정의 경우 스테이터스를 보낼 필요는 없다
			writeC(0);
		} else {
			byte[] status = item.getStatusBytes(null);// item.getStatusBytes();
			writeC(status.length);
			for (byte b : status) {
				writeC(b);
			}
		}
		writeD(0x17);
		writeH(0x00);
		writeC(item.getEnchantLevel());
		writeD(item.getId());

		writeD(0x20);
		writeD(0);
		if (!((item.getItemId() >= 437010 && item.getItemId() <= 437013)
				/**기사단 장비*/
				|| (item.getItemId() >= 60716 && item.getItemId() <= 60725)
				|| (item.getItemId() >= 30098 && item.getItemId() <= 30104)
				|| (item.getItemId() >= 20615 && item.getItemId() <= 20626)
				/**기사단장비*/
				|| item.getItemId() == 5000067 || item.getItemId() == 60104
				|| (item.getItemId() >= 21125 && item.getItemId() <= 21136)
				|| (item.getItemId() >= 20452 && item.getItemId() <= 20455)
				|| (item.getItemId() >= 421000 && item.getItemId() <= 421020)
				|| item.getItemId() == 560025 || item.getItemId() == 560027
				|| item.getItemId() == 560028 || item.getItemId() == 888819
				|| item.getItemId() == 1437010 || item.getItemId() == 1437011 || item.getItemId() == 60291
				|| (item.getItemId() >= 21139 && item.getItemId() <= 21156)
				|| (item.getItemId() >= 60286 && item.getItemId() <= 60289)
				|| (item.getItemId() >= 60261 && item.getItemId() <= 60263)
				|| item.getItemId() == 60354 || item.getItemId() == 60396
				|| item.getItemId() == 60398 || item.getItemId() == 9096
				|| item.getItemId() == 600369
				|| (item.getItemId() >= 60427 && item.getItemId() <= 60444)
				|| (item.getItemId() >= 60447 && item.getItemId() <= 60472)
				|| (item.getItemId() >= 66757 && item.getItemId() <= 66788)//룸티스냅퍼
				|| item.getItemId() == 21256 || item.getItemId() == 21257 || item
				.getItemId() == 60492) && !item.getItem().isTradable()) {
			writeC(0x00);
		} else
			writeC(7);
		writeD(0x00);
		/*
		 * 2d a3 fb 0b 39 05 06 00 d9 01 01 0d 00 00 00 83 24 33 32 39 36 20 28
		 * 31 33 29 00 06 17 05 08 00 00 00 17 00 00 00 00 00 00 a9 e1 f6 70 02
		 * 00 00 00 00 00 00 00 00 00 00 00 00
		 */
	}

	@Override
	public byte[] getContent() {
		return _bao.toByteArray();
	}

	@Override
	public String getType() {
		return S_ADD_ITEM;
	}
}
