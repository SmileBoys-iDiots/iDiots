package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Skills;

public class S_NewAddSkill extends ServerBasePacket {

	private static final String S_NewAddSkill = "[S] S_NewAddSkill";
	private byte[] _byte = null;
	public static final int AddskillNew = 1041;

	public S_NewAddSkill(int type, L1PcInstance pc, int skillId) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(type);
		switch (type) {
		case AddskillNew: {
			writeC(0x0a);
			/**wizard and nomal skill 04, elf skill 05, knight skill 06, darkelf skill 07
			crown skill 08, dragon knight skill 09, illution and fencer skill 0a, warrior skill 0b*/
			if((skillId >= 1 && skillId <= 80)) {
				writeC(0x04);
			} else if ((skillId >= 87 && skillId <= 94) || skillId == 242) {
				writeC(0x06);
			} else if ((skillId >= 97 && skillId <= 112) || skillId == 234 || skillId == 244) {
				writeC(0x07);
			} else if ((skillId >= 113 && skillId <= 118) || (skillId >= 121 && skillId <= 123) || skillId == 241) {
				writeC(0x08);
			} else if ((skillId >= 129 && skillId <= 138) || (skillId >= 145 && skillId <= 179) || (skillId >= 235 && skillId <= 239)) {
				writeC(0x05);
			} else if ((skillId >= 181 && skillId <= 197) || skillId == 245) {
				writeC(0x09);
			} else if ((skillId >= 201 && skillId <= 220) || (skillId >= 222 && skillId <= 224) || skillId == 246 || skillId == 243) {
				writeC(0x0a);
			} else if ((skillId >= 228 && skillId <= 231) || skillId == 225 || skillId == 226 || skillId == 247) {
				writeC(0x0b);
			}
			writeC(0x08);
			if ((skillId >= 235 && skillId <= 239)) {
				writeBit(skillId + 4765);
			} else {
				writeBit(skillId - 1);
			}
			writeC(0x10);
			writeC(0x01);
			break;
		   }
		}
		writeH(0x00);
	}
	
	public S_NewAddSkill(int type, L1PcInstance pc, int skillId, boolean on) {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(type);
		switch (type) {
		case AddskillNew: {
			writeC(0x0a);
			/**wizard and nomal skill 04, elf skill 05, knight skill 06, darkelf skill 07
			crown skill 08, dragon knight skill 09, illution and fencer skill 0a, warrior skill 0b*/
			if((skillId >= 1 && skillId <= 80)) {
				writeC(0x04);
			} else if ((skillId >= 87 && skillId <= 94) || skillId == 242) {
				writeC(0x06);
			} else if ((skillId >= 97 && skillId <= 112) || skillId == 234 || skillId == 244) {
				writeC(0x07);
			} else if ((skillId >= 113 && skillId <= 118) || (skillId >= 121 && skillId <= 123) || skillId == 241) {
				writeC(0x08);
			} else if ((skillId >= 129 && skillId <= 138) || (skillId >= 145 && skillId <= 179) || (skillId >= 235 && skillId <= 239)) {
				writeC(0x05);
			} else if ((skillId >= 181 && skillId <= 197) || skillId == 245) {
				writeC(0x09);
			} else if ((skillId >= 201 && skillId <= 220) || (skillId >= 222 && skillId <= 224) || skillId == 246 || skillId == 243) {
				writeC(0x0a);
			} else if ((skillId >= 228 && skillId <= 231) || skillId == 225 || skillId == 226 || skillId == 247) {
				writeC(0x0b);
			}
			writeC(0x08);
			if ((skillId >= 235 && skillId <= 239)) {
				writeBit(skillId + 4765);
			} else {
				writeBit(skillId - 1);
			}
			writeC(0x10);
			if (on) {
				writeC(0x01);
			} else {
				writeC(0x00);
			}
			break;
		}
		}
		writeH(0x00);
	}
	
	

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_NewAddSkill;
	}
}