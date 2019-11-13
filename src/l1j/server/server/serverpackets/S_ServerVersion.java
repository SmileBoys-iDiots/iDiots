package l1j.server.server.serverpackets;

import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.GameSystem.Dungeon.DungeonInfo;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.GameTimeClock;

public class S_ServerVersion extends ServerBasePacket {
	private static final String S_SERVER_VERSION = "[S] ServerVersion";
	public S_ServerVersion() {
		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		StringTokenizer St;
		String s = "35 03 08 00 10 18 "
				  +"18 8d 8e d6 ab 06 " 
				  +"20 8d 8e d6 ab 06 " 
				  +"28 fd ac ef c0 07 " 
				  +"30 8d 8e d6 ab 06 "
				  +"38 e6 99 9f c4 05 "
				  +"40 00 48 00 " 
				  +"50 8b fb ff a7 03 ";
		St = new StringTokenizer(s);
		while (St.hasMoreTokens()) {
			writeC(Integer.parseInt(St.nextToken(), 16));
		}
		writeC(0x58);
		write7B((int)(System.currentTimeMillis() / 1000));
		String s2 = "60 fc 97 87 48 "
				   +"68 95 cc e4 4c " 
				   +"70 94 bd e9 4c " 
				   +"78 da d8 8e ab 06 "
				   +"80 01 f4 89 c6 4c "
				   +"88 01 00 00 00";  
		St = new StringTokenizer(s2);
		while (St.hasMoreTokens()) {
			writeC(Integer.parseInt(St.nextToken(), 16));
		}
	}
	
	
	public S_ServerVersion(int version) {
		int time = GameTimeClock.getInstance().getGameTime().getSeconds();
		time = time - (time % 300);
		int uptime = (int) (System.currentTimeMillis() / 1000L);

		writeC(Opcodes.S_EXTENDED_PROTOBUF);
		writeH(0x0335); // SERVER_VERSION
		writeC(0x08);
		writeBit(0x00);
		writeC(0x10);
		writeBit(0x64);
		writeC(0x18);
		writeBit(version);
		writeC(0x20);
		writeBit(version);
		writeC(0x28);
		writeBit(2015090301);
		writeC(0x30);
		writeBit(version);
		writeC(0x38);
		writeBit(time);
		writeC(0x40);
		writeBit(0);
		writeC(0x48);
		writeBit(0);
		writeC(0x50);
		writeBit(889191819);
		writeC(0x58);
		writeBit(uptime);
		writeC(0x60);
		writeBit(151112700);
		writeC(0x68);
		writeBit(1906261103);
		writeC(0x70);
		writeBit(1906261104);
		writeC(0x78);
		writeBit(1906261102);
		writeH(0x0180);
		writeBit(1906261105);
		writeH(0x0188);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
	}

	public static final int ServerInterOpen = 0x0071;
	public static final int ServerInter = 0x0074;
	
  	public S_ServerVersion(int Type, int MapId, L1PcInstance pc) {
  		writeC(Opcodes.S_EXTENDED_PROTOBUF); 
  		writeH(Type);
		switch (Type) {
			case ServerInterOpen:
		  		/** ������ */
				long Result = 0, Ip;
				String ipAddress = Config.GAME_SERVER_HOST_NAME;
				String[] ipAddressInArray = ipAddress.split("\\.");
				for (int i = 0; i < ipAddressInArray.length; i++) {
					Ip = Integer.parseInt(ipAddressInArray[i]);
					Result += Ip * Math.pow(256, 3 - i);
				}
		  		writeC(0x08);
		  		writeBit(Result);
		  		
		  		/** ��Ʈ */
		  		writeC(0x10);
		  		writeBit(Config.GAME_SERVER_PORT);
		  		
		  		/** ���� �ѹ� */
		  		writeC(0x18);
		  		writeBit(pc.getId());
		  		
		  		/** �޼��� */
		  		writeC(0x22);
		  		writeC(pc.getName().getBytes().length);
		  		writeByte(pc.getName().getBytes());
		  		
		  		/** �� ī�ε� */
		  		writeC(0x28);
		  		writeC(MapId);
				break;
				
			case ServerInter:
		  		/** �� ī�ε� */
		  		writeC(0x08);
		  		writeC(MapId);
				break;
		}
		writeH(0x00);
  	}
  	
	public static final int Test = 0x02df;
	public static final int Test2 = 0x02dd;
	public static final int Test3 = 0x02de;
  	
 	public S_ServerVersion(int Type, L1PcInstance Pc, boolean User, DungeonInfo DungeonInfo) {
 		writeC(Opcodes.S_EXTENDED_PROTOBUF);
  		writeH(Type);
		switch (Type) {
			case Test:
		  		/** ������ */
		  		writeC(0x08);
		  		writeC(User ? 0x15 : 0x0b);

		  		writeC(0x52);
		  		writeC(15 + Pc.getName().getBytes().length + size7B(Pc.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(Pc.getId());
		  		
		  		writeC(0x10);
		  		writeBit(0x01);
		  		
		  		/** �����ѹ� */
		  		writeC(0x18);
		  		writeBit(100);
		  		
		  		/** �ɸ��� ���� */
		  		writeC(0x22);
		  		writeC(Pc.getName().getBytes().length);
		  		writeByte(Pc.getName().getBytes());
		  		
		  		writeC(0x28);
		  		writeBit(Pc.getType());
		  		
		  		writeC(0x30);
		  		writeBit(Pc.get_sex());
		  		
		  		writeC(0x38);
		  		writeBit(0x01);
		  		
		  		writeC(0x40);
		  		writeBit(DungeonInfo.DungeonList.indexOf(DungeonInfo.isUser(Pc)) + 1);
		  		break;
		  		
			case Test2:
				for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset()){
			  		writeC(0x0a);
			  		writeC(15 + PcList.getName().getBytes().length + size7B(PcList.getId()));
			  		
			  		/** ������Ʈ */
			  		writeC(0x08);
			  		writeBit(PcList.getId());
			  		
			  		writeC(0x10);
			  		writeBit(0x01);
			  		
			  		/** �����ѹ� */
			  		writeC(0x18);
			  		writeBit(100);
			  		
			  		/** �ɸ��� ���� */
			  		writeC(0x22);
			  		writeC(PcList.getName().getBytes().length);
			  		writeByte(PcList.getName().getBytes());
			  		
			  		writeC(0x28);
			  		writeBit(PcList.getType());
			  		
			  		writeC(0x30);
			  		writeBit(PcList.get_sex());
			  		
			  		writeC(0x38);
			  		writeBit(0x01);
			  		
			  		writeC(0x40);
			  		writeBit(DungeonInfo.DungeonList.indexOf(DungeonInfo.isUser(PcList)) + 1);
				}
		  		
		  		/** �� Ÿ�� �ѹ� */
		  		writeC(0x10);
		  		writeBit(DungeonInfo.TypeNumber);
		  		
		  		/** ���� */
		  		writeC(0x18);
		  		writeBit(0x01);
		  		
		  		/** Ÿ�� �ð� */
		  		writeC(0x20);
		  		writeBit(900);
		  		
		  		/** Ÿ�� ������ */
		  		writeC(0x28);
		  		writeBit(60);
		  		break;
		  		 
			case Test3:
				if(User){
					for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset()){
						if(PcList == Pc) continue;
				  		/** ������Ʈ */
				  		writeC(0x0a);
				  		writeC(19 + size7B(PcList.getId()));
				  		
				  		/** ������Ʈ */
				  		writeC(0x08);
				  		writeBit(PcList.getId());
				  		
				  		/** KillCount */
				  		writeC(0x10);
				  		writeBit(0x00);
				  		
				  		/** Deathcount */
				  		writeC(0x18);
				  		writeBit(0x00);
				  		
				  		/** Hp �ۼ� ������ */
				  		writeC(0x20);
				  		writeBit(0x00);
				  		
				  		/** Mp �ۼ� ������ */
				  		writeC(0x28);
				  		writeBit(0x00);
				  		
				  		/** X ��ǥ */
				  		writeC(0x30);
				  		writeBit(0x00);
				  		
				  		/** Y ��ǥ */
				  		writeC(0x38);
				  		writeBit(0x00);
				  		
				  		/** Poisoned ���� */
				  		writeC(0x58);
				  		writeBit(0x00);
				  		
				  		/** Paralysed ����*/
				  		writeC(0x60);
				  		writeBit(0x00);
				  		
				  		writeC(0x70);
				  		writeBit(0x00);
					}
			  		/** ���� ���� */
			  		writeC(0x12);
			  		writeBit(0x0e);
			  		
			  		/** ������Ʈ */
			  		writeC(0x08);
			  		writeBit(0x00);
			  		
			  		/** ���� �÷��� ���� */
			  		writeC(0x10);
			  		writeBit(0x00);
			  		
			  		writeC(0x18);
			  		writeBit(0x00);
			  		
			  		writeC(0x2a);
			  		writeBit(0x06);
			  		
			  		/** ���� Ÿ�� ī���� */
			  		writeC(0x08);
			  		writeBit(0x01);
			  		
			  		writeC(0x10);
			  		writeBit(0x00);
			  		
			  		writeC(0x18);
			  		writeBit(0x00);
				}else{
			  		/** ������Ʈ */
			  		writeC(0x0a);
			  		writeC(18 + (size7B(Pc.getId()) * 2) + size7B(Pc.getX()) + size7B(Pc.getY()));
			  		
			  		/** ������Ʈ */
			  		writeC(0x08);
			  		writeBit(Pc.getId());
			  		
			  		/** KillCount */
			  		writeC(0x10);
			  		writeBit(0x00);
			  		
			  		/** Deathcount */
			  		writeC(0x18);
			  		writeBit(0x00);
			  		
			  		/** Hp �ۼ� ������ */
			  		writeC(0x20);
			  		writeBit((int)Math.round(((double)Pc.getCurrentHp() / Pc.getMaxHp() * 100)));
			  		
			  		/** Mp �ۼ� ������ */
			  		writeC(0x28);
			  		writeBit((int)Math.round(((double)Pc.getCurrentMp() / Pc.getMaxMp() * 100)));
			  		
			  		/** X ��ǥ */
			  		writeC(0x30);
			  		writeBit(Pc.getX());
			  		
			  		/** Y ��ǥ */
			  		writeC(0x38);
			  		writeBit(Pc.getY());
			  		
			  		/** Poisoned ���� */
			  		writeC(0x58);
			  		writeBit(0x00);
			  		
			  		/** Paralysed ����*/
			  		writeC(0x60);
			  		writeBit(0x00);
			  		
			  		/** �÷��� ���� */
			  		writeC(0x68);
			  		writeBit(0x01);
			  		
			  		writeC(0x70);
			  		writeBit(Pc.getId());
				}
		  		break;
		}
		writeH(0x00);
  	}
 	
 	/** ���� �÷��� ���� ���� ��Ŷ */
 	public S_ServerVersion(DungeonInfo DungeonInfo, L1PcInstance Pc) {
 		writeC(Opcodes.S_EXTENDED_PROTOBUF);
  		writeH(Test3);
  		
  		if(Pc == null){
			for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset()){
		  		/** ������Ʈ */
		  		writeC(0x0a);
		  		writeC(1 + size7B(PcList.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(PcList.getId());
			}
			
	  		/** ���� ���� */
	  		writeC(0x12);
	  		writeBit(0x04);
	  		
	  		/** ������Ʈ */
	  		writeC(0x08);
	  		writeBit(0x00);
	  		
	  		/** ���� �÷��� ���� */
	  		writeC(0x10);
	  		writeBit(0x01);
  		}else{
	  		/** ���� ���� */
	  		writeC(0x12);
	  		writeC(3 + size7B(Pc.getId()));
	  		
	  		/** ������Ʈ */
	  		writeC(0x08);
	  		writeBit(Pc.getId());
	  		
	  		/** ���� �÷��� ���� */
	  		writeC(0x10);
	  		writeBit(0x00);
  		}
  		
  		writeH(0x00);
 	}
 	
 	
	public static final int HpMp = 1;
	public static final int Poison  = 2;
	public static final int Harden  = 3;
	public static final int Death  = 4;
	
 	/** ������Ʈ�� ��Ŷ ó�� Hp Mp ���� */
 	public S_ServerVersion(int Type, L1PcInstance Pc) {
 		writeC(Opcodes.S_EXTENDED_PROTOBUF);
  		writeH(Test3);
		switch (Type) {
		 	case HpMp:
		  		/** ������Ʈ */
		  		writeC(0x0a);
		  		writeC(5 + size7B(Pc.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(Pc.getId());
		  		
		  		/** Hp �ۼ� ������ */
		  		writeC(0x20);
		  		writeBit((int)Math.round(((double)Pc.getCurrentHp() / Pc.getMaxHp() * 100)));
		  		
		  		/** Mp �ۼ� ������ */
		  		writeC(0x28);
		  		writeBit((int)Math.round(((double)Pc.getCurrentMp() / Pc.getMaxMp() * 100)));
		  		break;
		  		
		 	case Poison:
		  		/** ������Ʈ */
		  		writeC(0x0a);
		  		writeC(3 + size7B(Pc.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(Pc.getId());
		  		
		  		/** Poisoned ���� */
		  		writeC(0x58);
		  		writeBit(0x01);
		  		break;
		  		
		 	case Harden:
		  		/** ������Ʈ */
		  		writeC(0x0a);
		  		writeC(3 + size7B(Pc.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(Pc.getId());
		  		
		  		/** Paralysed ����*/
		  		writeC(0x60);
		  		writeBit(0x01);
		  		break;
		  		
		 	case Death:
		  		/** ������Ʈ */
		  		writeC(0x0a);
		  		writeC(3 + size7B(Pc.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(Pc.getId());
		  		
		  		/** �÷��� ���� */
		  		writeC(0x68);
		  		writeBit(0x00);
		  		break;
		}
		writeH(0x00);
 	}
 	
	public static final int PlayerOut = 12;
	public static final int PlayerDead = 13;
 	
 	public S_ServerVersion(int Type, L1PcInstance Pc, boolean User){
 		writeC(Opcodes.S_EXTENDED_PROTOBUF);
  		writeH(Type);
		switch (Type) {
			case Test:
		  		/** ������ */
		  		writeC(0x08);
		  		writeC(User ? PlayerOut : PlayerDead);

		  		writeC(0x52);
		  		writeC(13 + Pc.getName().getBytes().length + size7B(Pc.getId()));
		  		
		  		/** ������Ʈ */
		  		writeC(0x08);
		  		writeBit(Pc.getId());
		  		
		  		writeC(0x10);
		  		writeBit(0x01);
		  		
		  		/** �����ѹ� */
		  		writeC(0x18);
		  		writeBit(100);
		  		
		  		/** �ɸ��� ���� */
		  		writeC(0x22);
		  		writeC(Pc.getName().getBytes().length);
		  		writeByte(Pc.getName().getBytes());
		  		
		  		writeC(0x28);
		  		writeBit(Pc.getType());
		  		
		  		writeC(0x30);
		  		writeBit(Pc.get_sex());
		  		
		  		writeC(0x38);
		  		writeBit(0x01);
		  		
		  		writeC(0x40);
		  		writeBit(0x01);
		  		break;
		}
		writeH(0x00);
 	}
  	
	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SERVER_VERSION;
	}
}