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

import l1j.server.server.Opcodes;
import l1j.server.server.model.ItemClientCode;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.utils.BinaryOutputStream;

public class S_AddItem extends ServerBasePacket {

	private static final String S_ADD_ITEM = "[S] S_AddItem";
	
	private BinaryOutputStream os;

	public S_AddItem(L1ItemInstance item) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeC(0x4c);
		writeC(0x02);
		
		byte[] Status = null;
		os = new BinaryOutputStream();
		/** 잡템이라면 패킷 따로 돌리고 패킷이 아니라면 메세지 따로 설정 */
		if (item.getItem().getType2() == 0) {
			if(item.getItem().isMagicDoll()){
				os.writeC(0x50); 
				os.writeBit(0x02);
			}else if(!item.getItem().isTradable()){
				os.writeC(0x50); 
				os.writeBit(0x08);
			}
			
			os.writeC(0x58); 
			os.writeBit(0x00);
			
			os.writeC(0x70); 
			os.writeBit(item.getWarehouseType());
		}else{		
			/** 봉인템 테두리 특수창고 사용불가 */
			/** 0 (기본옵션) ++;
				1 (테두리 활성화)
				2 (특수창고사용불가)
				16 (장금 아이콘) */
			if(item.getBless() >= 128) {
				os.writeC(0x50); 
				os.writeC(0x16);
			}
			
			os.writeC(0x58); 
			os.writeBit(0x00);
			
			if(item.getEnchantLevel() >= 1){
				os.writeC(0x68); 
				os.writeBit(item.getEnchantLevel());
			}
			
			os.writeC(0x70); 
			os.writeBit(item.getWarehouseType());
			
			if(item.getAttrEnchantLevel() >= 1){
				int[] 속성값 = item.getAttrEnchant(item.getAttrEnchantLevel());
				os.writeC(0x80);
				os.writeC(0x01);
				os.writeBit(속성값[0]);
				os.writeC(0x88);
				os.writeC(0x01);
				os.writeBit(속성값[1]);
			}
		}
		int Type = item.getStatusType();
		int DescId = ItemClientCode.code(item.getItemId());
		int length = 13 + size7B(item.getItemId()) + size7B(DescId) + size7B(item.getId()) + size7B(Type) +
			size7B(item.getCount()) + size7B(item.get_gfxid()) + item.getViewName().getBytes().length + os.getBytes().length;
		if(item.getItem().getUseType() == 30) length += 2;
		if(item.isIdentified()) Status = item.getStatusBytes();
		byte[] DogCollar = null;
		if(Status != null){
			length += Status.length + 3;
			DogCollar = S_PetWindow.DogCollar(item);
			if(DogCollar != null) length += DogCollar.length;
		}
		writeC(0x0a);
		writeBit(length); /** 길이 */
			
		writeC(0x08); 
		writeBit(item.getId()); /** 아이템넘버 */
		
		writeC(0x10);
		writeBit(DescId); /** 데스크 넘버 */
		
		writeC(0x18); 
		writeBit(item.getItemId()); /** 아이템넘버 */
		
		writeC(0x20); 
		writeBit(item.getCount()); /** 아이템겟수 */
		
		writeC(0x28); 
		writeBit(item.getItem().getUseType()); /** 아이템 타입넘버 */
		
		/** 빈줌 신규 스킬 넘버 아이템 */
		if(item.getItem().getUseType() == 30){
			int skillid = item.getItemId() - 40859;
			writeC(0x30); 
			writeBit(skillid);
		}
	
		writeC(0x38); 
		writeBit(item.get_gfxid());
		
		writeC(0x40); 
		int Bless = item.getBless() >= 128 ? item.getBless() - 128 : item.getBless();
		writeBit(Bless);
		
		/** 교환 가능 불가능 세팅 */
		writeC(0x48); 
		write7B(Type);
		
		writeByte(os.getBytes());
		os = null;
		
		writeC(0x92); 
		writeC(0x01);
		writeBit(item.getViewName().getBytes().length);
		writeByte(item.getViewName().getBytes()); 
		if(Status != null){
			/** 확인 표기부분 */
			writeC(0x9a); 
			writeC(0x01);
			writeBit(Status.length);
			writeByte(Status);
			Status = null;
			if(DogCollar != null){
				writeByte(DogCollar);
				DogCollar = null;
			}
		}
		writeH(0); 
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
