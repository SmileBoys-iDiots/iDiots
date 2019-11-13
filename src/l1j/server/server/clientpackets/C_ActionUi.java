package l1j.server.server.clientpackets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import l1j.server.IND;
import l1j.server.IND_Q;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GMCommands;
import l1j.server.GameSystem.Dungeon.DungeonInfo;
import l1j.server.GameSystem.Dungeon.DungeonSystem;
import l1j.server.GameSystem.InterServer.InterServer;
import l1j.server.GameSystem.RotationNotice.RotationNoticeTable;
import l1j.server.GameSystem.RotationNotice.RotationNoticeTable.EventNotice;
import l1j.server.server.datatables.CharcterRevengeTable;
import l1j.server.server.datatables.ClanBlessHuntInfo;
import l1j.server.server.datatables.ClanBlessHuntInfo.HuntInfo;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.CraftInfoTable;
import l1j.server.server.datatables.CraftInfoTable.L1CraftInfo;
import l1j.server.server.datatables.CraftInfoTable.Material;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MonsterBookTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PetsSkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ClanBlessHuntUi;
import l1j.server.server.serverpackets.S_EventNotice;
import l1j.server.server.serverpackets.S_MonsterUi;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_PetWindow;
import l1j.server.server.serverpackets.S_Revenge;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ServerVersion;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1PetSkill;
import l1j.server.server.utils.BinaryOutputStream;
import l1j.server.server.utils.SQLUtil;
import server.LineageClient;
import server.LoginController;
import static l1j.server.server.model.skill.L1SkillId.RevengeTime;

public class C_ActionUi extends ClientBasePacket {

	Random _Random = new Random(System.nanoTime());

	private static final String C_ACTION_UI = "[C] C_ActionUi";
	private static final int CRAFT_ITEM = 0x36;
	private static final int CRAFT_ITEMLIST = 0x38; // 0x38;
	private static final int CRAFT_OK = 0x3a; // 첫번째 타입

	
	private static final int CRAFT_COUNT_OK = 0x5c;
	
	private static final int EventNotice = 0x8f; // 0x38
	
	/*private static final int 초보완료 = 0x0c; // 0x31;
	private static final int 초보이동 = 0x0f; // 0x31;
	private static final int 초보로드 = 0x08; // 0x31;
	private static final int 초보진행 = 0x0a; // 0x31; 
	*/
	
	/** 팻 관련 패킷 처리 */
	private static final int PetCommand = 0x07d4;
	
	/** 펫 스텟 정보 업데이트용 */
	private static final int Petstat = 0x07d3;
	
	/** 펫 관련 네임 변경 */
	private static final int PetName = 0x07d1;
	
	/** 펫 관련 스킬 변경 */
	private static final int PetSkill = 0x07d8;
	
	/** 펫 관련 스킬단계 변경 */
	private static final int PetSkillLevel  = 0x07d7;
	

	private static final int CLAN_BLESS_HUNT_CHANGE = 1016;
	private static final int CLAN_BLESS_HUNT_ALL_CHANGE = 1017;
	
	/** 접속 관련 패킷 */
	private static final int ServerLogin = 0X34;
	private static final int ServerToLogin = 0Xe5;
	private static final int ServerVersion = 0x0334;
	private static final int ServerInter   = 0x0075; ///03fd   1021
	
	/** 접속 관련 패킷 */
	private static final int InDungeon = 0x08b5;
	private static final int InDungeExit = 0x08b7;
	private static final int InDungeStart = 0x08b1;
	private static final int InDungeonOut = 0x08ba;
	private static final int InDungeonList = 0x08a3;
	private static final int InDungeonSlot = 0x08a9;
	private static final int InDungeonType = 0x08a7;
	private static final int InDungeonReady = 0x08af;
	private static final int InDungeonAccess = 0x08a5;
	private static final int InDungeoninvite = 0x08b3;
	private static final int InDungeonStancet = 0x08ac;
	
	/** 복수 관련 패킷 */
	private static final int RevengeBoard  = 1051;
	private static final int RevengeProvoke  = 1054;
	private static final int RevengeChase  = 1056;
	private static final int RevengeChase2  = 1058;
	
	
	/**도감관련*/
	private static final int MONSTERBOOK_CLEAR = 0x0233;
	private static final int MONSTERBOOK_TEL = 0x0235; // 몬스터 북 텔레포트
	private static final int BOOKWEEKQUEST_TEL = 0x032F;
	private static final int BOOKWEEKQUEST_CLEAR = 0x032B;
	/**도감관련*/

	private static final String REMOVE = "08 01 1a 04 08 00 10 00 85 32";
	private static final String ADD = "08 00 12 29 08 d3 7a 10 01 18 ff ff ff ff ff ff ff ff ff 01 20 00 28 01 30 00 38 01 42 06 24 31 30 39 34 37 48 00 50 00 58 b0 2d 62 00 9d 80";
	private static final int[] bigSuccessItem = { 220011, 203025, 222345, 222343, 222342, 222355, 222351, 222344, 20049,
			20050, 20057, 20109, 20200, 20178, 20186, 20216, 20076, 20152, 20040, 20025, 20018, 20029, 203025,
			203023, };

	public C_ActionUi(byte abyte0[], LineageClient client) throws IOException {
		super(abyte0);
		int type = readH();
		
		L1PcInstance pc = client.getActiveChar();
		if (client != null && (type == ServerLogin || type == ServerToLogin || type == ServerVersion || type == ServerInter)){
			if(type == ServerLogin){
				client.setDDosCheck(true);
				new C_AuthLogin(abyte0, client, true);
				return;
		    }else if(type == ServerToLogin){
				new C_AuthLogin(abyte0, client, true);
				return;
			}else if(type == ServerVersion){
				client.sendPacket(new S_ServerVersion(1907301001), true);
				return;
			}else if(type == ServerInter){
				try{
					int ServerInterTotal = readH();
					String Name = null;
					for (int i = 0; i < ServerInterTotal; i++) {
						if(readLength() == 0) break;
						int Type = readC(), Length = 0;
						if(Type == 0x08){
							read4(read_size());
						}else if(Type == 0x12){
							Length = readC();
							Name = readS(Length);
						}else break;
					}
					L1PcInstance Player = L1World.getInstance().getPlayer(Name);
					client.setInterServer(Player.getNetConnection().getInterServer());
					client.setInterServerType(Player.getNetConnection().getInterServerType());
					client.setInterServerName(Player.getName());	
					client.setInterServerParty(Player.getParty());
					client.setInterServerNotice(Player.get표식());
					
					client.setAccount(Player.getNetConnection().getAccount());
					LoginController.getInstance().login(client, client.getAccount());
					
					Player.getNetConnection().ServerInterKick();
					InterServer.RequestInterServer(client);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					/** 만약 에러나면 정보 리셋 */
					client.kick();
				}
			}
		}else if (pc == null || pc.isGhost()) return;
		
		 /*System.out.println("액션 > " + type);*/
		switch (type) {
		/** 방 정보 호출 */
		case InDungeon:{
			pc.getInventory().CheckItemSkill();
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeon), true);
			break;
		}
		
		case InDungeStart:{
			readH(); readC();
			int RoomNumber = read4(read_size());
			DungeonInfo DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
			
			DungeonInfo.InPlaygame = true;
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeStart, 1, RoomNumber), true);
			IND_Q.requestWork(new IND(pc.getName(), DungeonInfo.TypeNumber == 201 ? 3 : 4, DungeonInfo));
			 //리스타트 상태가 아니면 메세지 날리고 리턴  
			/*
			 * if (DungeonInfo == null) { pc.sendPackets(new
			 * S_EventNotice(S_EventNotice.InDungeStart, 7, RoomNumber), true); return;
			 * }else if(DungeonInfo.isDungeonInfoCheck()){ pc.sendPackets(new
			 * S_EventNotice(S_EventNotice.InDungeStart, 8, RoomNumber), true); return;
			 * }else if (DungeonInfo.DungeonList.size() < DungeonInfo.MinSize) {
			 * pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeStart, 6, RoomNumber),
			 * true); return; }else{ for(L1PcInstance PcList :
			 * DungeonInfo.isDungeonInfoUset()){
			 * if(!DungeonInfo.isDungeonInfoCheck(PcList)){ pc.sendPackets(new
			 * S_EventNotice(S_EventNotice.InDungeStart, 8, RoomNumber), true); return; } }
			 * DungeonInfo.InPlaygame = true; pc.sendPackets(new
			 * S_EventNotice(S_EventNotice.InDungeStart, 1, RoomNumber), true);
			 * IND_Q.requestWork(new IND(pc.getName(), DungeonInfo.TypeNumber == 201 ? 3 :
			 * 4, DungeonInfo)); }
			 */ 
			break;
		}
		
		case InDungeonOut:{
			readH(); readC();
			int RoomNumber = read4(read_size());
			DungeonInfo DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
			readC();
			L1PcInstance OutUse = DungeonInfo.isDungeonInfoCheckUset(read4(read_size()));
			OutUse.sendPackets(new S_EventNotice(S_EventNotice.InDungeExit, 1, RoomNumber), true);
			OutUse.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOut, OutUse.getId(), RoomNumber), true);
			OutUse.isDungeonTeleport(false);
			DungeonInfo.setUser(pc);
			for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset()){
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOutUse, DungeonInfo, OutUse), true);
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOutExit, DungeonInfo, OutUse), true);
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOpen, DungeonInfo, null), true);
			}
			break;
		}
		
		case InDungeoninvite:{
			int InDungeonTotal = readH();
			int RoomNumber = 0, Object = 0;
			String UseName = null;
			for (int i = 0; i < InDungeonTotal; i++) {
				if(readLength() == 0) break;
				int Type = readC(), Length = 0;
				if(Type == 0x08){
					RoomNumber = read4(read_size());
				}else if(Type == 0x10){
					Object = read4(read_size());
				}else if(Type == 0x1a){
					Length = readC();
					UseName = readS(Length);
				}else break;
			}
			L1PcInstance Player = null, PlayerUse = null;
			L1Object PlayerObject = L1World.getInstance().findObject(Object);
			if(PlayerObject != null) Player = (L1PcInstance) PlayerObject;
			if(UseName != null) PlayerUse = L1World.getInstance().getPlayer(UseName);
			if(Player != null && PlayerUse != null) 
				PlayerUse.sendPackets(new S_EventNotice(S_EventNotice.InDungeoninvite, RoomNumber, Player.getName(), PlayerUse.getName()), true);
			break;
		}
		
		/** 방 리스트 정리 */
		case InDungeonList:{
			int ListLength = readH();
			readH(); readC();
			/** 올 정보 갱신 */
			if(ListLength == 2){
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonList, 0, 0), true);
			/** 각 클레스 정보 갱신 */
			}else{
				int InDungeonNumber = read4(read_size());
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonList, InDungeonNumber, 0), true);
			}
			break;
		}
			
		/** 방 계설 정보 */
		case InDungeonType:{
			/** 열쇠가 있는지 체크 부분 열쇠가 없다면 메세지만 출력 */
			if (!pc.getInventory().checkItem(500021, 1)) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonType, 15, 0), true);
				return;
			}
			
			/** 낚시나 상점 상태라면 리턴 종료 해버리자 */
			if(pc.isPrivateShop() && pc.isFishing()) return;
			
			readH(); readC();
			/** 전체 사이즈 계산  */
			int InDungeonTotal = readC();
			DungeonInfo DungeonInfo = new DungeonInfo();
			DungeonInfo.RoomNumber = DungeonSystem.getDungeonInfo();
			for (int i = 0; i < InDungeonTotal; i++) {
				if(readLength() == 0) break;
				int Type = readC(), Length = 0;
				if(Type == 0x0a){
					Length = readC();
					DungeonInfo.Title = readS(Length);
					Length = Length + 1;
				}else if(Type == 0x10){
					DungeonInfo.Level = read4(read_size());
					Length = getBitSize(DungeonInfo.Level);
				/** 소비 아데나 */
				}else if(Type == 0x18){
					DungeonInfo.Adena = read4(read_size());
					Length = getBitSize(DungeonInfo.Adena);
				/** 이건 그냥 기본 0 으로만 뜸 */
				}else if(Type == 0x20){
					DungeonInfo.Type = read4(read_size()) == 0 ? 1 : 2;
					Length = getBitSize(DungeonInfo.Type);
				/** 자동 분배 */
				}else if(Type == 0x28){
					DungeonInfo.Division = read4(read_size());
					Length = getBitSize(DungeonInfo.Division);
				/** 공개 비공개 */
				}else if(Type == 0x30){
					DungeonInfo.Open = read4(read_size());
					Length = getBitSize(DungeonInfo.Open);
				/** 총 인원수 고정 6명 */
				}else if(Type == 0x38){
					DungeonInfo.MaxSize = read4(read_size());
					DungeonInfo.MinSize = 4;
					Length = getBitSize(DungeonInfo.MaxSize);
				/** 방 비밀 번호 없다면 0 */
				}else if(Type == 0x42){
					Length = readC();
					DungeonInfo.OpenPassword = readS(Length);
					Length = Length + 1;
				/** 방 타입 (고유 넘버)  */
				}else if(Type == 0x48){
					DungeonInfo.TypeNumber = read4(read_size());
					Length = getBitSize(DungeonInfo.TypeNumber);
				}
				i += Length;
			}
			/** 방 생성시 입장 패킷 에 대한 정보도 같이 갱신 */
			DungeonInfo.setDungeonLeadt(pc.getId());
			DungeonInfo.setDungeonInfoCheck(pc, true);
			DungeonInfo.setUser(pc);
			pc.isDungeonTeleport(true);
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonType, 1, DungeonInfo.RoomNumber), true);
			
			DungeonSystem.getDungeonInfo(DungeonInfo.RoomNumber, DungeonInfo);
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOpen, DungeonInfo, null), true);
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonSetUp, DungeonInfo, null), true);
			break;
		}
		
		/** 슬롯 정보 패킷 인듯 */
		case InDungeonSlot:{
			/** 전체 사이즈 계산  */
			int InDungeonTotal = readH();
			DungeonInfo DungeonInfo = null;
			for (int i = 0; i < InDungeonTotal; i++) {
				if(readLength() == 0) break;
				int Type = readC(), Length = 0;
				if(Type == 0x08){
					int RoomNumber = read4(read_size());
					DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
					Length = getBitSize(RoomNumber);
				}else if(Type == 0x12){
					Length = readC();
					DungeonInfo.Title = readS(Length);
					Length = Length + 1;
				}else if(Type == 0x18){
					DungeonInfo.Level = read4(read_size());
					Length = getBitSize(DungeonInfo.Level);
				}else if(Type == 0x20){
					DungeonInfo.Open = read4(read_size());
					Length = getBitSize(DungeonInfo.Open);
				}else if(Type == 0x2a){
					Length = readC();
					DungeonInfo.OpenPassword = readS(Length);
					Length = Length + 1;
				}else if(Type == 0x30){
					DungeonInfo.Division = read4(read_size());
					Length = getBitSize(DungeonInfo.Division);
				/** 총 인원수 고정 6명 */
				}else if(Type == 0x38){
					DungeonInfo.MaxSize = read4(read_size());
					Length = getBitSize(DungeonInfo.MaxSize);
				/** 방 타입 (고유 넘버)  */
				}else if(Type == 0x40){
					DungeonInfo.TypeNumber = read4(read_size());
					Length = getBitSize(DungeonInfo.TypeNumber);
				}
				i += Length;
			}
			/** 모든케릭터 리스정보 패스 */
			DungeonInfo.isDungeonReady();
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonList, 1, DungeonInfo.RoomNumber), true);
			for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset())
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOpen, DungeonInfo, null), true);
			break;
		}
			
		/** 방 입장 정보 */
		/** 51 a5 08 02 00 08 0d d2 */
		case InDungeonAccess:{
			readH(); readC();
			/** 방 넘버 체크  */
			int RoomNumber = read4(read_size());
			DungeonInfo DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonSetUp, DungeonInfo, null), true);
			break;
		}
		
		/** 51 ac 08 07 00 08 d9 1b 12 00 18 00 */
		case InDungeonStancet:{
			/** 낚시나 상점 상태라면 리턴 종료 해버리자 */
			if(pc.isPrivateShop() && pc.isFishing()) return;
			
			/** 인터서버라면 메세지후 입장 불가능하게 정리 */
			if(pc.getNetConnection().getInterServer()) {
				pc.sendPackets(new S_SystemMessage("인터서버(지배의탑,잊혀진섬)내에서는 입장이 불가능합니다."), true);
				return;
			/** 열쇠가 없을시 입장 불가능하게 하루 1회정도만 입장 가능하게 정리 */
			}else if (!pc.getInventory().checkItem(500021, 1)) {
				pc.sendPackets(new S_SystemMessage("인던 열쇠를 소유하지 않아 입장 할수 없습니다."), true);
				return;
			}
			
			readH(); readC();					
			/** 방 넘버 체크  */
			int RoomNumber = read4(read_size()); readC();
			int PasswordLength = readC();
			String Password = readS(PasswordLength);
			
			DungeonInfo DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
			if (DungeonInfo == null) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 7, RoomNumber), true);
				return;
			}else if (DungeonInfo.InPlaygame) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 3, RoomNumber), true);
				return;
			}else if (!pc.getInventory().checkItem(L1ItemId.ADENA, DungeonInfo.Adena)) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 4, RoomNumber), true);
				return;
			}else if (DungeonInfo.Open != 0 && !Password.contentEquals(DungeonInfo.OpenPassword)) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 5, RoomNumber), true);
				return;
			}else if (DungeonInfo.DungeonList.size() >= DungeonInfo.MaxSize) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 8, RoomNumber), true);
				return;
			}else if (DungeonInfo.DungeonList.contains(pc)) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 9, RoomNumber), true);
				return;
			}else if (DungeonSystem.isDungeonInfoCheck(pc)){
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 10, RoomNumber), true);
				return;
			}else if (DungeonInfo.Level > pc.getLevel()) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 11, RoomNumber), true);
				return;
			}else if (CharPosUtil.getZoneType(pc) != 1) {
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 12, RoomNumber), true);
				return;
			}
			DungeonInfo.setUser(pc);
			DungeonInfo.setDungeonInfoCheck(pc, false);
			pc.isDungeonTeleport(true);
			pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonStancet, 1, RoomNumber), true);
			for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset()){
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOpen, DungeonInfo, null), true);
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonRenewal, DungeonInfo, pc), true);
			}
			break;
		}
		
		case InDungeonReady:{
			readH(); readC();
			/** 방 넘버 체크  */
			int RoomNumber = read4(read_size()); readC();
			DungeonInfo DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
			if(DungeonInfo.isDungeonInfoCheck(pc)){
				DungeonInfo.setDungeonInfoCheck(pc, false);
			}else DungeonInfo.setDungeonInfoCheck(pc, true);
			for(L1PcInstance PcList : DungeonInfo.isDungeonInfoUset())
				PcList.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOpen, DungeonInfo, null), true);
			break;
		}
		
		case InDungeExit:{
			readH(); readC();
			int RoomNumber = read4(read_size());
			DungeonInfo DungeonInfo = DungeonSystem.isDungeonInfo(RoomNumber);
			if(DungeonInfo.isDungeonLeadt() == pc.getId()){
				for(L1PcInstance ListPc : DungeonInfo.isDungeonInfoUset()){
					ListPc.isDungeonTeleport(false);
					ListPc.sendPackets(new S_EventNotice(S_EventNotice.InDungeExit, 1, RoomNumber), true);
				}
				DungeonSystem.DungeonInfoRemove(RoomNumber);
			}else{
				DungeonInfo.setUser(pc);
				pc.isDungeonTeleport(false);
				pc.sendPackets(new S_EventNotice(S_EventNotice.InDungeExit, 1, RoomNumber), true);
				for(L1PcInstance ListPc : DungeonInfo.isDungeonInfoUset())
					ListPc.sendPackets(new S_EventNotice(S_EventNotice.InDungeonOpen, DungeonInfo, null), true);
			}
			break;
		}
		
		/*case 초보이동:
			readD(); // 쓰레기 처리
			int a = readC();
			if(a==1 && pc.cL==0){
			pc.sendPackets(new S_CreateItem(S_CreateItem.퀘스트진행, 1), true);
			pc.sendPackets(new S_CreateItem(S_CreateItem.퀘스트진행, 2), true);
			pc.sendPackets(new S_CreateItem(S_CreateItem.퀘스트진행, 3), true);
			}
			break;
			
		case 초보완료:
			readD(); // 쓰레기 처리
			int d = readC();
			pc.sendPackets(new S_CreateItem(S_CreateItem.퀘스트완료, d), true);
			pc.sendPackets(new S_OwnCharStatus(pc));
			break;
			
		case 초보로드:
			readD(); // 쓰레기 처리
			int q = readC();
			pc.sendPackets(new S_CreateItem(S_CreateItem.퀘스트로드, q), true);
			break;
			
		case 초보진행:
			readD(); // 쓰레기 처리
			int m = readC();
			pc.sendPackets(new S_CreateItem(S_CreateItem.퀘스트진행, m), true);
			break;*/
		case RevengeBoard:{
			readH();
			readC();
			CharcterRevengeTable revenge = CharcterRevengeTable.getInstance();
			ArrayList<Integer> objid = revenge.GetRevengeObj(pc.getId());
			if (objid.size() < 1) {
				return;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(RevengeTime)){
				int targetId = revenge.LoadChaserTarGet(pc.getId());
				L1Object target = L1World.getInstance().findObject(targetId);
				if (target != null && target instanceof L1PcInstance) {
					L1PcInstance Targetpc = (L1PcInstance) target;
					pc.sendPackets(new S_Revenge(S_Revenge.Revenge_Chase_Loc, Targetpc), true);
				} else {
					pc.sendPackets(new S_Revenge(S_Revenge.Revenge_List_Add, pc), true);
				}
			}
			pc.sendPackets(new S_Revenge(S_Revenge.Revenge_All_List, pc), true);
			break;
		}
		case RevengeProvoke:{
			readD();
			readC();
			int NameLength = readC();
			String Name = readS(NameLength);
			CharcterRevengeTable revenge = CharcterRevengeTable.getInstance();
			if(Name == null) {
				return;
			} else {
				revenge.UpdateRemainCount(pc.getId(), Name);
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, pc.getName() + "님이 " + Name + "님을 도발하였습니다"), true);
			}
			pc.sendPackets(new S_Revenge(S_Revenge.Revenge_List_Provoke, pc), true);
			break;
		}
		case RevengeChase:{
			readD();
			readH();
			readC();
			int NameLength = readC();
			String Name = readS(NameLength);
			CharcterRevengeTable revenge = CharcterRevengeTable.getInstance();
			String OBJT = revenge.TarObjFind(Name);
			int targetId = Integer.parseInt(OBJT);
			L1Object target = L1World.getInstance().findObject(targetId);
			if(target == null) {
				System.out.println("타겟찾기 오류1");
				return;
			}
			if (!pc.getInventory().checkItem(40308, 10000)) {
				pc.sendPackets(new S_SystemMessage("아데나가  부족합니다."), true);
				return;
			}
			if (target != null && target instanceof L1PcInstance) {
				L1PcInstance Targetpc = (L1PcInstance) target;
				if(revenge.RemainCounter(pc.getId(), Targetpc.getId()) > 0) {
					pc.getInventory().removeItem(40308, 10000);
					revenge.UpdateRemainCount(pc.getId(), Targetpc.getId());
					pc.getSkillEffectTimerSet().setSkillEffect(RevengeTime, 600 * 1000);
					revenge.StoreChaserTime(pc.getId(), target.getId(), 600);
					pc.sendPackets(new S_Revenge(S_Revenge.Revenge_Chase_Loc, Targetpc), true);
				} else {
					System.out.println("체이서 횟수 오류");
					return;
				}
			}
			pc.sendPackets(new S_Revenge(S_Revenge.Revenge_All_List, pc), true);
			break;
		}
		case RevengeChase2:{
			readD();
			readH();
			readC();
			int NameLength = readC();
			String Name = readS(NameLength);
			CharcterRevengeTable revenge = CharcterRevengeTable.getInstance();
			String OBJT = revenge.TarObjFind(Name);
			int targetId = Integer.parseInt(OBJT);
			L1Object target = L1World.getInstance().findObject(targetId);
			if(target == null) {
				System.out.println("타겟찾기 오류2");
				return;
			}			
			if (target != null && target instanceof L1PcInstance && pc.getSkillEffectTimerSet().hasSkillEffect(RevengeTime)) {
				L1PcInstance Targetpc = (L1PcInstance) target;
				pc.sendPackets(new S_Revenge(S_Revenge.Revenge_Chase_MinMap, Targetpc), true);
				revenge.StoreChaserTime(pc.getId(), target.getId(), pc.getSkillEffectTimerSet().getSkillEffectTimeSec(80020));
				System.out.println("정상전송 미니맵");
			}
			break;
		}
		case MONSTERBOOK_CLEAR: 
			readH();
			readC();
			int monNum = read4(read_size());
			int value = 0;
			if (monNum == 1)
				value = 1;
			else if (monNum == 2)
				value = 2;
			if (monNum >= 3) {
				value = monNum % 3;
			}
			switch (value) {//도감 1~3단계별로 아이템지급
			case 1:
				pc.addExp(50000);
				pc.getInventory().storeItem(60765, 10);
				L1Cooking.newEatCooking(pc, L1SkillId.천하장사버프, 1800);
				break;
			case 2:
				pc.getInventory().storeItem(60765, 20);
				L1Cooking.newEatCooking(pc, L1SkillId.천하장사버프, 1800);
				pc.addExp(500000);
				break;
			case 3:
				pc.getInventory().storeItem(60765, 30);
				L1Cooking.newEatCooking(pc, L1SkillId.천하장사버프, 1800);
				pc.addExp(5000000);
				break;
			}
			pc.sendPackets(new S_MonsterUi(S_MonsterUi.MONSTER_END, monNum));
			MonsterBookTable.getInstace().setMon_Quest(pc.getId(), monNum, 1);
			MonsterBookTable.getInstace().saveMonsterQuest(pc.getId());
			pc.sendPackets(new S_SkillSound(pc.getId(), 3944), true);
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944), true);
			break;
		case MONSTERBOOK_TEL:
			readH();
			readC();
			int monsternumber = read4(read_size()) / 3 + 1;
			MonsterBookTable Mui_gi = MonsterBookTable.getInstace();
			int mn = Mui_gi.getMonNum(monsternumber);
			if (mn != 0 && mn <= 557 && pc.getMap().isSafetyZone(pc.getX(), pc.getY())) {
				int itemId = Mui_gi.getMarterial(monsternumber);
				String itemName = ItemTable.getInstance().findItemIdByName(itemId);
				if (itemName != null) {
					int locx = Mui_gi.getLocX(monsternumber);
					int locy = Mui_gi.getLocY(monsternumber);
					int mapid = Mui_gi.getMapId(monsternumber);
					if (pc.getMap().isEscapable()) {
						if (pc.getInventory().consumeItem(itemId, 1)){
							pc.sendPackets(new S_SystemMessage("5초후 텔레포트됩니다."), true);
							L1Teleport.teleport(pc, locx, locy, (short) mapid, 5, true);
						}else {
							pc.sendPackets(new S_ServerMessage(4692, itemName));
							return;
						}
					} else {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(4726));
					}
				}
			}
			else if (mn >= 558 && mn <= 609 && pc.getMap().isSafetyZone(pc.getX(), pc.getY())) {
				int itemId = Mui_gi.getMarterial(monsternumber - 10);
				String itemName = ItemTable.getInstance().findItemIdByName(itemId);
				if (itemName != null) {
					int locx = Mui_gi.getLocX(monsternumber - 10);
					int locy = Mui_gi.getLocY(monsternumber - 10);
					int mapid = Mui_gi.getMapId(monsternumber - 10);
					if (pc.getMap().isEscapable()) {
						if (pc.getInventory().consumeItem(itemId, 1)){
							pc.sendPackets(new S_SystemMessage("5초후 텔레포트됩니다."), true);
							L1Teleport.teleport(pc, locx, locy, (short) mapid, 5, true);
						}else {
							pc.sendPackets(new S_ServerMessage(4692, itemName));
							return;
						}
					} else {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(4726));
					}
				}
			} else if (mn >= 610 && pc.getMap().isSafetyZone(pc.getX(), pc.getY())) {
				int itemId = Mui_gi.getMarterial(monsternumber - 11);
				String itemName = ItemTable.getInstance().findItemIdByName(itemId);
				if (itemName != null) {
					int locx = Mui_gi.getLocX(monsternumber - 11);
					int locy = Mui_gi.getLocY(monsternumber - 11);
					int mapid = Mui_gi.getMapId(monsternumber - 11);
					if (pc.getMap().isEscapable()) {
						if (pc.getInventory().consumeItem(itemId, 1)){
							pc.sendPackets(new S_SystemMessage("5초후 텔레포트됩니다."), true);
							L1Teleport.teleport(pc, locx, locy, (short) mapid, 5, true);
						}else {
							pc.sendPackets(new S_ServerMessage(4692, itemName));
							return;
						}
					} else {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(4726));
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage("\\aH알림: SafetyZone에서만 이용 가능합니다 ."), true);
			}
			break;
		case BOOKWEEKQUEST_TEL:
			if (pc.getWeekQuest() != null) {
				readH(); // 0x04, 0x00
				readC(); // 0x08,
				int difficulty = readC();
				readC(); // 0x10,
				int section = readC();
				pc.getWeekQuest().teleport(difficulty, section);
			}
			break;
		case BOOKWEEKQUEST_CLEAR:
			if (pc.getWeekQuest() != null) {
				readH(); // 0x04, 0x00
				readC(); // 0x08,
				int difficulty = readC();
				readC(); // 0x10,
				int section = readC();
				pc.getWeekQuest().complete(difficulty, section);
			}
			break;		
		case EventNotice:{
			readH(); // size;
			readC(); // dummy
			int Action = readC();
			
			/** 지엠룸 감옥등 곳에서는 사용불가하도록 세팅 */
			if (pc.getMapId() == 99 || (pc.getMapId() >= 2236 && pc.getMapId() <= 2237)) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할수 없습니다."), true);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}
		
			/** 귀환이 불가능한 지역에서는 귀환안되도록 세팅 하긔! */
			if (!pc.getMap().isEscapable()) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할수 없습니다."), true);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}
			
			
			EventNotice Notice = RotationNoticeTable.getInstance().getRotation(Action);
			int locx = Notice.Teleport[0], locy = Notice.Teleport[1], mapid = Notice.Teleport[2], Adena = Notice.Teleport[3];
			if (Action == 20) {
				if(!pc.getInventory().checkItem(60384) && pc.getQuest().get_step(L1Quest.QUEST_55_Roon) == 0){
					locx = 32882; locy = 32783; mapid = 4; Adena = 10000;
				}else if(!pc.getInventory().checkItem(60391) && pc.getQuest().get_step(L1Quest.QUEST_70_Roon) == 0 && pc.getLevel() >= 70){
					locx = 32869; locy = 32797; mapid = 4; Adena = 10000;
				}else if(!pc.getInventory().checkItem(60398) && pc.getQuest().get_step(L1Quest.QUEST_80_Roon) == 0 && pc.getLevel() >= 80){
					locx = 32869; locy = 32797; mapid = 4; Adena = 10000;
				}else if(!pc.getInventory().checkItem(60405) && pc.getQuest().get_step(L1Quest.QUEST_85_Roon) == 0 && pc.getLevel() >= 85){
					locx = 32869; locy = 32797; mapid = 4; Adena = 10000;
				}else if(!pc.getInventory().checkItem(60412) && pc.getQuest().get_step(L1Quest.QUEST_90_Roon) == 0 && pc.getLevel() >= 90){
					locx = 32869; locy = 32797; mapid = 4; Adena = 10000;
				}
			}
			L1Location loc = new L1Location(locx, locy, (short)mapid).randomLocation(Action == 16 ? 10 : 5, true);
			/** 아데나 체크후에 아데나가있다면 남은 시간 체크해서 시간이 남음 텔 가능하도록 한다 */
			if (!pc.getInventory().checkItem(40308, Adena)) {
				pc.sendPackets(new S_SystemMessage("아데나가  부족합니다."), true);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				return;
			}
			pc.getInventory().consumeItem(40308, Adena);
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
			L1Teleport.teleport(pc, loc.getX(), loc.getY(),(short) loc.getMapId(), 5, true, true, 5000);
			break;
		}
		
		/** 펫 스킬 관련 일단 임시로 작업 */
		case PetSkill:{
			readH(); // size;
			readC(); // dummy
			/** 스킬 넘버 받아옴 기본적으로 */
			int NameNumber = readC();
			L1PetInstance Pet = (L1PetInstance) pc.getPet();
			/** 확율 아이템 모두 삭제 해주고 정리해줘야 하는데 일단 패스 */
			if(Pet != null){
				/** 스킬 사용 가능한 패킷 처리 해야됨 
				 * 임시로 새팅 */
				int SkillLevel = 0;
				int[] Amount = null;
				if(NameNumber == 1 || NameNumber == 7 || NameNumber == 15 || NameNumber == 18 ||
				   NameNumber == 19 || NameNumber == 20 || NameNumber == 21 ||  NameNumber == 22){
					SkillLevel = 1;
					Amount = new int[]{ 3, 4, 5, 7, 10, 15, 25, 50, 100, 200 };
				}else if(NameNumber == 2 || NameNumber == 8 || NameNumber == 16){
					SkillLevel = 2;
					Amount = new int[]{ 13, 24, 35, 47, 60, 75, 95, 130, 190, 300};
				}else if(NameNumber == 24 || NameNumber == 14 || NameNumber == 12 ||
					NameNumber == 23 || NameNumber == 10 || NameNumber == 17){
					SkillLevel = 3;
					Amount = new int[]{ 28, 54, 80, 107, 135, 165, 200, 250, 325, 450};
				}else if(NameNumber == 4 || NameNumber == 9 || NameNumber == 11 || 
					NameNumber == 5 || NameNumber == 13){
					SkillLevel = 4;
					Amount = new int[]{ 48, 94, 140, 187, 235, 285, 340, 410, 505, 650};
				}else if(NameNumber == 3){
					SkillLevel = 5;
					Amount = new int[]{ 73, 144, 215, 287, 360, 435, 515, 610, 730, 900};
				}
						
				L1PetSkill PetSkill = Pet.getPetSkills(NameNumber);
				if (PetSkill != null && Pet.getFriendship() >= Amount[PetSkill.getSkillLevel()] &&
					pc.getInventory().consumeItem(43204, 1)){
					Pet.setFriendship(Pet.getFriendship() - Amount[PetSkill.getSkillLevel()]);
					pc.sendPackets(new S_PetWindow(S_PetWindow.Friendship, Pet), true);
					/** 스킬 성공 실패 확율 처리 */
					int Chance = _Random.nextInt(100) + 1;
					/** 높아서 성공하였다면 스킬 정보 패킷 처리 해서 완료 시켜준다 */
					if(50 - (PetSkill.getSkillLevel() * SkillLevel) >= Chance){
						PetSkill.setSkillLevel(PetSkill.getSkillLevel() + 1);
						/** npc정보 패킷 처리로 변환하여 도록 메소드를 하나 더 만들어서 돌리자 */
						Pet.SkillsUpdate(PetSkill.getSkillNumber(), PetSkill.getSkillLevel());
						pc.sendPackets(new S_PetWindow(PetSkill), true);
						pc.sendPackets(new S_PetWindow(NameNumber, true), true);
						/** 디비 저장 시스템  */
						PetsSkillsTable.SaveSkills(Pet, false);
					}else pc.sendPackets(new S_PetWindow(NameNumber, false), true);
				}else pc.sendPackets(new S_PetWindow(NameNumber, false), true);
				/** 변수들 초기화 시킬때 사용 */
				Arrays.fill(Amount, 0);
			}
			break;
		}
			
		/** 펫 관련 스킬 레벨 체크 */
		case PetSkillLevel:{
			/** 스킬 단계 계산해서 1단계인지 2단계인지 체크해야됨 */
			/** 1단계가 모두 완료고 레벨이 맞는지 체크해서 2단계로 넘겨줄수잇도록 새팅 */
			L1PetInstance Pet = (L1PetInstance) pc.getPet();
			/** 각 스킬 참인지 거짓인지 체크 */
			if(Pet != null){
				if(Pet.getPetSkills(Pet.getPetType().getSkillOneStep())){
					/** 메세지 링크 */
					pc.sendPackets(new S_ServerMessage(5314), true);
					/** 기본 스킬 배워지도록 */
					Pet.addPetSkills(Pet.getPetType().getSkillOneStep());
					pc.sendPackets(new S_PetWindow(Pet.ArrayPetSkills()), true);
					/** 디비 저장t 시스템  */
					PetsSkillsTable.SaveSkills(Pet, false);
				}else if(Pet.getPetSkills(Pet.getPetType().getSkillTwoStep())){
					/** 아데나가 없다면 리턴 */
					if(!pc.getInventory().consumeItem(40308, 100000)){
						pc.sendPackets(new S_SystemMessage("아데나가 부족하여 2단계 야성오픈을 할수없습니다."), true);
						return;
					}
					/** 메세지 링크 */
					pc.sendPackets(new S_ServerMessage(5314), true);
					/** 기본 스킬 배워지도록 */
					Pet.addPetSkills(Pet.getPetType().getSkillTwoStep());
					pc.sendPackets(new S_PetWindow(Pet.ArrayPetSkills()), true);
					/** 디비 저장t 시스템  */
					PetsSkillsTable.SaveSkills(Pet, false);
				}else if(Pet.getPetSkills(Pet.getPetType().getSkillThreeStep())){
					/** 아데나가 없다면 리턴 */
					if(!pc.getInventory().consumeItem(40308, 500000)){
						pc.sendPackets(new S_SystemMessage("아데나가 부족하여 3단계 야성오픈을 할수없습니다."), true);
						return;
					}
					/** 메세지 링크 */
					pc.sendPackets(new S_ServerMessage(5314), true);
					/** 기본 스킬 배워지도록 */
					Pet.addPetSkills(Pet.getPetType().getSkillThreeStep());
					pc.sendPackets(new S_PetWindow(Pet.ArrayPetSkills()), true);
					/** 디비 저장t 시스템  */
					PetsSkillsTable.SaveSkills(Pet, false);
				}else if(Pet.getPetSkills(Pet.getPetType().getSkillFourStep())){
					/** 아데나가 없다면 리턴 */
					if(!pc.getInventory().consumeItem(40308, 1000000)){
						pc.sendPackets(new S_SystemMessage("아데나가 부족하여 4단계 야성오픈을 할수없습니다."), true);
						return;
					}
					/** 메세지 링크 */
					pc.sendPackets(new S_ServerMessage(5314), true);
					/** 기본 스킬 배워지도록 */
					Pet.addPetSkills(Pet.getPetType().getSkillFourStep());
					pc.sendPackets(new S_PetWindow(Pet.ArrayPetSkills()), true);
					/** 디비 저장t 시스템  */
					PetsSkillsTable.SaveSkills(Pet, false);
				}else if(Pet.getPetSkills(Pet.getPetType().getSkillFiveStep())){
					/** 아데나가 없다면 리턴 */
					if(!pc.getInventory().consumeItem(40308, 5000000)){
						pc.sendPackets(new S_SystemMessage("아데나가 부족하여 5단계 야성오픈을 할수없습니다."), true);
						return;
					}
					/** 메세지 링크 */
					pc.sendPackets(new S_ServerMessage(5314), true);
					/** 기본 스킬 배워지도록 */
					Pet.addPetSkills(Pet.getPetType().getSkillFiveStep());
					pc.sendPackets(new S_PetWindow(Pet.ArrayPetSkills()), true);
					/** 디비 저장t 시스템  */
					PetsSkillsTable.SaveSkills(Pet, false);
				}else pc.sendPackets(new S_SystemMessage("스킬을 배울수 없습니다."), true);
			}
			break;
		}				
		
		case PetName:{
			readH(); // size;
			readC(); // size;
			int NameLength = readC();
			String Name = readS(NameLength);
			L1PetInstance Pet = (L1PetInstance) pc.getPet();
			if(Pet != null){
				if(Pet.isInvalidName(Name)){
					pc.sendPackets(new S_SystemMessage("사용할수 없는 이름 입니다."), true);
					return;
				}
				/** 같은 이름의 펫은 사용불가 */
				if (PetTable.isNameExists(Name)) {
					pc.sendPackets(new S_ServerMessage(327), true);
					return;
				}
				Pet.PetName(Name);
			}
			break;
		}
		
		case Petstat:{
			readH(); 
			int Temp[] = new int[3];
			L1PetInstance Pet = (L1PetInstance) pc.getPet();
			if(Pet != null && Pet.getBonusPoint() > 0){
				for (int i = 0; i < Temp.length; i++) {
					readC();
					Temp[i] = readC(); 
				}
				Pet.BonusPoint(Temp);
			}
			/** 변수들 초기화 시킬때 사용 */
			Arrays.fill(Temp, 0);
			break;
		}
			
		case PetCommand:{
			readH(); 
			readC(); 
			int Command = readC();
			L1PetInstance Pet = (L1PetInstance) pc.getPet();
			if(Pet != null){
				switch (Command) {	
					case 2: /** 공격 태세 */
						Pet.onFinalAction(1, null);
						break;
						
					case 3: /** 방어 태세 */
						Pet.onFinalAction(2, null);
						break;
						
					case 6: /** 수집 상태 */
						Pet.onFinalAction(8, null);
						break;
						
					case 7: /** 대상 공격 */
						readC();
						L1Object obj = L1World.getInstance().findObject(read4(read_size()));
						Pet.onFinalAction(9, obj);
						break;
						
					case 9: /** 태각 모든정보 리셋 */
						Pet.onFinalAction(10, null);
						break;
						
					case 101: /** 펫 액션 관련 패킷 처리인듯 */
						Pet.onFinalAction(67, null);
						break;
				}
			}
			break;
		}
		
		case CLAN_BLESS_HUNT_ALL_CHANGE: {
			if (pc.getClan() == null)
				return;

			if (pc.getClanRank() != L1Clan.CLAN_RANK_PRINCE && pc.getClanRank() != L1Clan.CLAN_RANK_GUARDIAN) {
				pc.sendPackets(new S_SystemMessage("혈맹의 군주나 수호기사만 사용가능합니다."));
				return;
			}

			if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 300000)) {
				pc.sendPackets(new S_ServerMessage(189));
				return;
			}
			ClanBlessHuntInfo.getInstance().settingClanBlessHuntMaps(pc.getClan());
			ClanTable.getInstance().updateClanBlessHunt(pc.getClan());
			pc.sendPackets(new S_ClanBlessHuntUi(S_ClanBlessHuntUi.CLAN_BLESS_HUNT_TELEPORT, pc.getClan()));
		}
			break;
		case CLAN_BLESS_HUNT_CHANGE: {
				if (pc.getClan() == null)
					return;
				/*if (pc.getClanRank() != L1Clan.CLAN_RANK_PRINCE && pc.getClanRank() !=L1Clan.CLAN_RANK_GUARDIAN) { 
				    pc.sendPackets(new S_SystemMessage("혈맹의 군주나 수호기사만 사용가능합니다."));
					return; 
					}*/
				if (pc.getClan().isHuntMapChoice()) {
					if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 1000)) {
						pc.sendPackets(new S_ServerMessage(189));
						return;
					}
				}

				readH();
				readC();
				readBit();
				readC();
				int mapNumber = readBit();
				readC();
				int code = readBit();
				ArrayList<Integer> mapList = ClanBlessHuntInfo.getInstance().getMapList(mapNumber);
				if (code == 1 || code == 3) {
					for (int i = 0; i < pc.getClan().getBlessHuntMaps().size(); i++) {
						if (pc.getClan().getBlessHuntMaps().get(i) == mapNumber) {
							pc.getClan().getBlessHuntMapsType().set(i, 2);
							pc.getClan().setBlessHuntMapIds(mapList);
						} else {
							pc.getClan().getBlessHuntMapsType().set(i, 3);
						}
					}
					pc.sendPackets(new S_ClanBlessHuntUi(S_ClanBlessHuntUi.CLAN_BLESS_HUNT_TELEPORT, pc.getClan()));
					pc.getClan().setHuntMapChoice(true);
					ClanTable.getInstance().updateClanBlessHunt(pc.getClan());
				} else {
					HuntInfo hi = ClanBlessHuntInfo.getInstance().getClanBlessHuntInfo(mapNumber);
					if (hi != null) {
						L1Teleport.teleport(pc, hi.getTeleportLocX(), hi.getTeleportLocY(),
								(short) hi.getTeleportMapId(), pc.getHeading(), true);
					}
				}
		}
			break;
		case CRAFT_ITEM: // 제작 시스템
			pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.CRAFT_ITEM));
			break;
		case CRAFT_ITEMLIST: // 0x38
			readH(); // size;
			readC(); // dummy
			int objectId = read4(read_size());
			L1Object obj = L1World.getInstance().findObject(objectId);
			if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				int[] craftList = CraftInfoTable.getIns().getCraftNpc(npc.getNpcId());
				if (craftList != null && craftList.length != 0) {
					pc.sendPackets(new S_ACTION_UI(craftList));
				} else {
					pc.sendPackets(new S_ACTION_UI(npc));
				}
			}
			break;
		case 92: {
			if (pc.getInventory().calcWeightpercent() >= 90) {
				pc.sendPackets(new S_SystemMessage("제작 실패: 무게 게이지 90% 이상 제작 불가."));
				return;
			}
			int len = readC() - 3;
			for (int i = 0; i < len; i++) {
				readC();
			}
			pc.sendPackets(new S_NewCreateItem(0X5D, "08 00 10 e3 03 56 ce"));// 1479
		}
			break;
		case CRAFT_OK: {// 0x3a // TODO 제작
			try {
				if (pc.getInventory().calcWeightpercent() >= 90) {
					pc.sendPackets(new S_SystemMessage("제작 실패: 무게 게이지 90% 이상 제작 불가."));
					return;
				}
				// 여기서 모든패킷을 한번 읽어드린다. 그리고 필요한 자료 갯수 여러정보를 가공한다 가져온다.
				if (!readCraftInfo(pc)) {
					craftErrMsg(pc, craftId, "!readCraftInfo(pc)");
					return;
				}

				if (materialDescIdList == null) {
					craftErrMsg(pc, craftId, "materialDescIdList == null");
					return;
				}

				L1CraftInfo ci = CraftInfoTable.getIns().getCraftInfo(craftId);
				if (ci == null) {
					craftErrMsg(pc, craftId, "ci == null");
					return;
				}
				L1Item item = ItemTable.getInstance().findDescId(ci.descId, ci.bless);
				if (item == null) {
					craftErrMsg(pc, craftId, "temp == null");
					return;
				}

				ArrayList<Material> materialList = ci.materialList;
				if (materialList == null) {
					craftErrMsg(pc, craftId, "materialList == null");
					return;
				}

				if (materialList.size() != materialDescIdList.size() && craftId != 3667 && craftId != 3599) {
					craftErrMsg(pc, craftId, "materialList.size() != materialDescIdList.size()");
					return;
				}

				HashMap<L1ItemInstance, Integer> deleteItemIntArrayList = new HashMap<L1ItemInstance, Integer>(); // 아이템,삭제할수
				L1Inventory pcIv = pc.getInventory();
				for (Material material : materialList) { // 하나의 재료아이템
					if (material == null) {
						craftErrMsg(pc, craftId, "material == null");
						return;
					}
					int[] descIds = material.descIds;
					if (descIds == null) {
						craftErrMsg(pc, craftId, "descIds == null");
						return;
					}
					if (material.enchant == null) {
						craftErrMsg(pc, craftId, "material.enchant == null");
						return;
					}
					if (material.count == null) {
						craftErrMsg(pc, craftId, "material.count == null");
						return;
					}
					if (material.bless == null) {
						craftErrMsg(pc, craftId, "material.bless == null");
						return;
					}
					boolean materialCk = false;
					for (int index = 0; index < descIds.length; index++) { // 하나의 재료아이템 대체아이템까지 검색.
						int descId = descIds[index];
						if (materialDescIdList.get(descId) == null)
							continue;
						materialCk = true;
						int enchant = materialDescIdList.get(descId);
						if (enchant != material.enchant[index]) {
							craftErrMsg(pc, craftId, "enchant != material.enchant[index]");
							return;
						}
						int count = material.count[index] * createItemCount;
						// int bless = material.bless[index];
						L1Item item1 = ItemTable.getInstance().getTemplateByDescId(descId);
						if (item1.isStackable()) { // 아이템이 스택커블일경우
							L1ItemInstance deletItemCk = pcIv.findCraftMaterialItem(descId, count);
							if (deletItemCk == null) {
								craftErrMsg(pc, craftId, "1deletItemCk == null");
								return;
							}
							if (deleteItemIntArrayList.containsKey(deletItemCk) && craftId != 3667 && craftId != 3599) {
								craftErrMsg(pc, craftId, "deleteItemIntArrayList.containsKey(deletItemCk)");
								return;
							}
							deleteItemIntArrayList.put(deletItemCk, count);
							break;
							/*
							 * }else if(craftId == 2607) { int ckCount = 0; for (L1ItemInstance itemIns :
							 * pcIv.getItems()) { if (itemIns != null && !itemIns.isEquipped() &&
							 * itemIns.getItem().getItemDescId() == descId && itemIns.getEnchantLevel() ==
							 * enchant) { if (deleteItemIntArrayList.containsKey(itemIns)) { craftErrMsg(pc,
							 * craftId, "deleteItemIntArrayList.containsKey(itemIns)"); return; }
							 * deleteItemIntArrayList.put(itemIns, 1); ckCount++; } } if (ckCount < count) {
							 * craftErrMsg(pc, craftId, "ckCount < count"); return; } else { break; }
							 */
						} else { // 일반 아이템일경우
							int ckCount = 0;
							for (L1ItemInstance itemIns : pcIv.getItems()) {
								if (ckCount >= count)
									break;
								if (itemIns != null && !itemIns.isEquipped()
										&& itemIns.getItem().getItemDescId() == descId
										&& itemIns.getEnchantLevel() == enchant) {
									if (deleteItemIntArrayList.containsKey(itemIns)) {
										craftErrMsg(pc, craftId, "deleteItemIntArrayList.containsKey(itemIns)");
										return;
									}
									deleteItemIntArrayList.put(itemIns, 1);
									ckCount++;
								}
							}
							if (ckCount < count) {
								craftErrMsg(pc, craftId, "ckCount < count");
								return;
							} else {
								break;
							}
						}
					}
					if (!materialCk) {
						craftErrMsg(pc, craftId, "!materialCk");
						return;
					}
				}
				int probability = ci.probability;
				boolean success = true;
				if (probability != 0) {
					if (_Random.nextInt(100) > probability) {
						success = false;
					}
				}
				int deleteItemId = 0;
				for (L1ItemInstance itemIns : deleteItemIntArrayList.keySet()) {
					if (itemIns == null) {
						craftErrMsg(pc, craftId, "itemIns == null");
						return;
					}
					if (!pcIv.consumeItem(itemIns, deleteItemIntArrayList.get(itemIns))) { // item, count
						craftErrMsg(pc, craftId, "!pcIv.consumeItem(itemIns, deleteItemIntArrayList.get(itemIns))");
						return;
					}
				}
				if (success) {
					L1ItemInstance createItem = null;
					for (int itemId : bigSuccessItem) {
						if (itemId == item.getItemId()) {
							if (_Random.nextInt(100) < 30) { // 10% 확률로 대성공아이템.
								createItem = pcIv.storeItem2(item.getItemId(), ci.makeCount * createItemCount,
										ci.enchant, ci.bless, ci.attr);
								createItem.setBless(0);
								L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 3599,"대성공 제작에 성공하여 축복받은 ", createItem), true);
								commit("대성공 아이템 제작^금속테이블 : " + item.getName(), "", 1);
								pc.sendPackets(new S_SkillSound(pc.getId(), 7976));
								pc.broadcastPacket(new S_SkillSound(pc.getId(), 7976));
								pc.sendPackets(new S_SkillSound(pc.getId(), 2047));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2047));
							}
							break;
						}
					}
					if (createItem == null)
						createItem = pcIv.storeItem2(item.getItemId(), ci.makeCount * createItemCount, ci.enchant,
								ci.bless, ci.attr);
					pc.sendPackets(new S_ServerMessage(403, createItem.getName()));
					pc.sendPackets(new S_NewCreateItem(0X3b, ADD)); // 성공 멘트
					pc.sendPackets(new S_SkillSound(pc.getId(), 2029)); // 폭죽
				} else {
					if (craftId >= 2652 && craftId <= 2653) { // 고대상자 시리즈
						pc.getInventory().storeItem(deleteItemId, _Random.nextInt(2) + 1);
					} else if (craftId >= 1043 && craftId <= 1048) { // 엘릭서 시리즈
						pc.getInventory().storeItem(500209, 30);
					} else if (craftId == 5892) { 
						pc.getInventory().storeItem(60765, 30);
					} else if (craftId == 2747) { // 축복받은 오림의 장신구 마법 주문서
						pc.getInventory().storeItem(7323, 1);
					} else if (craftId == 3599) { // 지배이반
						pc.getInventory().storeItem(20288, 1);

						// 2595,2596,2597,2598 ,2599,2600,2601,2602,2603,2604,2605,2606 인내력

					} else if (craftId == 2595) { // 6안타인내
						인첸트지급(pc, 420102, 1, 6);
					} else if (craftId == 2596) { // 6파푸인내
						인첸트지급(pc, 420106, 1, 6);
					} else if (craftId == 2597) { // 6린드인내
						인첸트지급(pc, 420110, 1, 6);
					} else if (craftId == 2598) { // 6발라인내
						인첸트지급(pc, 420112, 1, 6);

					} else if (craftId == 2599) { // 7안타인내
						인첸트지급(pc, 420102, 1, 7);
					} else if (craftId == 2600) { // 7파푸인내
						인첸트지급(pc, 420106, 1, 7);
					} else if (craftId == 2601) { // 7린드인내
						인첸트지급(pc, 420110, 1, 7);
					} else if (craftId == 2602) { // 7발라인내
						인첸트지급(pc, 420112, 1, 7);

					} else if (craftId == 2603) { // 8안타인내
						인첸트지급(pc, 420102, 1, 8);
					} else if (craftId == 2604) { // 8파푸인내
						인첸트지급(pc, 420106, 1, 8);
					} else if (craftId == 2605) { // 8린드인내
						인첸트지급(pc, 420110, 1, 8);
					} else if (craftId == 2606) { // 8발라인내
						인첸트지급(pc, 420112, 1, 8);

						// 2607,2608,2609,2610 ,2611,2612,2613,2614,2615,2616,2617,2618 예지력
					} else if (craftId == 2607) { // 6안타예지
						인첸트지급(pc, 420101, 1, 6);
					} else if (craftId == 2608) { // 6파푸예지
						인첸트지급(pc, 420105, 1, 6);
					} else if (craftId == 2609) { // 6린드예지
						인첸트지급(pc, 420109, 1, 6);
					} else if (craftId == 2610) { // 6발라예지
						인첸트지급(pc, 420113, 1, 6);

					} else if (craftId == 2611) { // 7안타예지
						인첸트지급(pc, 420101, 1, 7);
					} else if (craftId == 2612) { // 7파푸예지
						인첸트지급(pc, 420105, 1, 7);
					} else if (craftId == 2613) { // 7린드예지
						인첸트지급(pc, 420109, 1, 7);
					} else if (craftId == 2614) { // 7발라예지
						인첸트지급(pc, 420113, 1, 7);

					} else if (craftId == 2615) { // 8안타예지
						인첸트지급(pc, 420101, 1, 8);
					} else if (craftId == 2616) { // 8파푸예지
						인첸트지급(pc, 420105, 1, 8);
					} else if (craftId == 2617) { // 8린드예지
						인첸트지급(pc, 420109, 1, 8);
					} else if (craftId == 2618) { // 8발라예지
						인첸트지급(pc, 420113, 1, 8);

						// 2571,2572,2573,2574 ,2575,2576,2577,2578,2579,2580,2581,2582 완력
					} else if (craftId == 2571) { // 6안타완력
						인첸트지급(pc, 420100, 1, 6);
					} else if (craftId == 2572) { // 6파푸완력
						인첸트지급(pc, 420104, 1, 6);
					} else if (craftId == 2573) { // 6린드완력
						인첸트지급(pc, 420108, 1, 6);
					} else if (craftId == 2574) { // 6발라완력
						인첸트지급(pc, 420115, 1, 6);

					} else if (craftId == 2575) { // 7안타완력
						인첸트지급(pc, 420100, 1, 7);
					} else if (craftId == 2576) { // 7파푸완력
						인첸트지급(pc, 420104, 1, 7);
					} else if (craftId == 2577) { // 7린드완력
						인첸트지급(pc, 420108, 1, 7);
					} else if (craftId == 2578) { // 7발라완력
						인첸트지급(pc, 420115, 1, 7);

					} else if (craftId == 2579) { // 8안타완력
						인첸트지급(pc, 420100, 1, 8);
					} else if (craftId == 2580) { // 8파푸완력
						인첸트지급(pc, 420104, 1, 8);
					} else if (craftId == 2581) { // 8린드완력
						인첸트지급(pc, 420108, 1, 8);
					} else if (craftId == 2582) { // 8발라완력
						인첸트지급(pc, 420115, 1, 8);

						// 2583,2584,2585,2586 ,2587,2588,2589,2590,2591,2592,2593,2594 마력
					} else if (craftId == 2583) { // 6안타마력
						인첸트지급(pc, 420103, 1, 6);
					} else if (craftId == 2584) { // 6파푸마력
						인첸트지급(pc, 420107, 1, 6);
					} else if (craftId == 2585) { // 6린드마력
						인첸트지급(pc, 420111, 1, 6);
					} else if (craftId == 2586) { // 6발라마력
						인첸트지급(pc, 420114, 1, 6);

					} else if (craftId == 2587) { // 7안타마력
						인첸트지급(pc, 420103, 1, 7);
					} else if (craftId == 2588) { // 7파푸마력
						인첸트지급(pc, 420107, 1, 7);
					} else if (craftId == 2589) { // 7린드마력
						인첸트지급(pc, 420111, 1, 7);
					} else if (craftId == 2590) { // 7발라마력
						인첸트지급(pc, 420114, 1, 7);

					} else if (craftId == 2591) { // 8안타마력
						인첸트지급(pc, 420103, 1, 8);
					} else if (craftId == 2592) { // 8파푸마력
						인첸트지급(pc, 420107, 1, 8);
					} else if (craftId == 2593) { // 8린드마력
						인첸트지급(pc, 420111, 1, 8);
					} else if (craftId == 2594) { // 8발라마력
						인첸트지급(pc, 420114, 1, 8);
					} else if (craftId >= 3715 && craftId <= 3724) { // 집행인첸시
						인첸트지급2(pc, 61, 1, craftId - 3715, 5);
					} else if (craftId >= 3728 && craftId <= 3737) { // 바칼
						인첸트지급2(pc, 12, 1, craftId - 3728, 5);
					} else if (craftId >= 3741 && craftId <= 3750) { // 수결지
						인첸트지급2(pc, 134, 1, craftId - 3741, 10);
					} else if (craftId >= 3754 && craftId <= 3763) { // 붉이
						인첸트지급2(pc, 86, 1, craftId - 3754, 5);
					} else if (craftId >= 3767 && craftId <= 3776) { // 격노
						인첸트지급2(pc, 30082, 1, craftId - 3767, 15);
					} else if (craftId >= 3780 && craftId <= 3789) { // 크공
						인첸트지급2(pc, 30080, 1, craftId - 3780, 5);
					} else if (craftId >= 3793 && craftId <= 3802) { // 히페
						인첸트지급2(pc, 30081, 1, craftId - 3793, 5);
					} else if (craftId >= 3806 && craftId <= 3815) { // 타분
						인첸트지급2(pc, 30083, 1, craftId - 3806, 5);
					} else if (craftId >= 5893 && craftId <= 5902) { // 사신의검
						인첸트지급2(pc, 30110, 1, craftId - 5893, 5);
					} else if (craftId >= 5913 && craftId <= 5922) { // 아인하사드의섬광
						인첸트지급2(pc, 30112, 1, craftId - 5913, 5);
					} else if (craftId >= 5933 && craftId <= 5942) { // 그랑카인의 심판
						인첸트지급2(pc, 30111, 1, craftId - 5933, 5);
						/**사신의 숨결로 인첸트시*/
					} else if (craftId >= 4429 && craftId <= 4438) { // 집행인첸시
						인첸트지급2(pc, 61, 1, craftId - 4429, 5);
					} else if (craftId >= 4442 && craftId <= 4451) { // 바칼
						인첸트지급2(pc, 12, 1, craftId - 4442, 5);
					} else if (craftId >= 4455 && craftId <= 4464) { // 수결지
						인첸트지급2(pc, 134, 1, craftId - 4455, 10);
					} else if (craftId >= 4468 && craftId <= 4477) { // 붉이
						인첸트지급2(pc, 86, 1, craftId - 4468, 5);
					} else if (craftId >= 4481 && craftId <= 4490) { // 격노
						인첸트지급2(pc, 30082, 1, craftId - 4481, 15);
					} else if (craftId >= 4494 && craftId <= 4503) { // 크공
						인첸트지급2(pc, 30080, 1, craftId - 4494, 5);
					} else if (craftId >= 4507 && craftId <= 4516) { // 히페
						인첸트지급2(pc, 30081, 1, craftId - 4507, 5);
					} else if (craftId >= 4520 && craftId <= 4529) { // 타분
						인첸트지급2(pc, 30083, 1, craftId - 4520, 5);
					} else if (craftId >= 5903 && craftId <= 5912) { // 사신의검
						인첸트지급2(pc, 30110, 1, craftId - 5903, 5);
					} else if (craftId >= 5923 && craftId <= 5932) { // 아인하사드의섬광
						인첸트지급2(pc, 30112, 1, craftId - 5923, 5);
					} else if (craftId >= 5943 && craftId <= 5952) { // 그랑카인의 심판
						인첸트지급2(pc, 30111, 1, craftId - 5943, 5);
					}
					/**사신의 숨결로 인첸트시*/
					pc.sendPackets(new S_NewCreateItem(0X3b, REMOVE)); // 실패 멘트
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			break;
		}
	}

	private boolean 인첸트지급(L1PcInstance pc, int item_id, int count, int EnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				pc.sendPackets(new S_ServerMessage(82));
				// 무게 게이지가 부족하거나 인벤토리가 꽉차서 더 들 수 없습니다.
				return false;
			}
			// pc.sendPackets(new S_SystemMessage("아이템 제작에 성공했습니다."));
			pc.sendPackets(new S_ServerMessage(143, item.getLogName())); // %0를 손에 넣었습니다.
			// pc.sendPackets(new S_SkillSound(pc.getId(), 2032));
			// pc.broadcastPacket(new S_SkillSound(pc.getId(), 2032));
			return true;
		} else {
			return false;
		}
	}

	private boolean 인첸트지급2(L1PcInstance pc, int item_id, int count, int EnchantLevel, int AttrEnchantLevel) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			item.setEnchantLevel(EnchantLevel);
			item.setAttrEnchantLevel(AttrEnchantLevel);
			item.setIdentified(true);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				pc.sendPackets(new S_ServerMessage(82));
				// 무게 게이지가 부족하거나 인벤토리가 꽉차서 더 들 수 없습니다.
				return false;
			}
			// pc.sendPackets(new S_SystemMessage("아이템 제작에 성공했습니다."));
			pc.sendPackets(new S_ServerMessage(143, item.getLogName())); // %0를 손에 넣었습니다.
			// pc.sendPackets(new S_SkillSound(pc.getId(), 2032));
			// pc.broadcastPacket(new S_SkillSound(pc.getId(), 2032));
			return true;
		} else {
			return false;
		}
	}

	private int craftId;
	private int createItemCount;
	private HashMap<Integer, Integer> materialDescIdList; // descId, encahnt

	private boolean readCraftInfo(L1PcInstance pc) {
		materialDescIdList = new HashMap<Integer, Integer>();
		readP(3); // dummy //토탈사이즈 포함
		L1Object obj = L1World.getInstance().findObject(readBit());
		if (obj == null) {
			craftErrMsg(pc, 0, "obj == null");
			return false;
		}
		if (obj instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int difflocx = Math.abs(pc.getX() - npc.getX());
			int difflocy = Math.abs(pc.getY() - npc.getY());
			if (difflocx > 15 || difflocy > 15 || (pc.getMapId() != npc.getMapId())) {
				pc.sendPackets(new S_ServerMessage(3575), true); // 제작 NPC가 너무 멀리 있습니다.
				return false;
			}
		}
		readP(1); // dummy
		craftId = readBit();
		if (GMCommands.제작체크) {
			System.out.println("제작아이디 - craftId : " + craftId);
		}
		if (craftId == 0) {
			craftErrMsg(pc, craftId, "craftId == 0");
			return false;
		}
		readP(1); // dummy
		createItemCount = readBit();
		if (GMCommands.제작체크) {
			System.out.println("제작만들갯수 - createItemCount : " + createItemCount);
		}
		if (createItemCount == 0) {
			craftErrMsg(pc, craftId, "createItemCount == 0");
			return false;
		}
		while (isRead(1)) {
			int dummyType = readC();
			if (dummyType != 0x22)
				break;
			readP(4); // 재료템 길이, dummy 0x08 , 재료아이템 순번. dummy 0x10
			int descId = readBit();
			if (materialDescIdList.containsKey(descId) && craftId != 3410 && craftId != 3411 && craftId != 3412 && craftId != 3667 && craftId != 3599 && craftId != 2044) {
				craftErrMsg(pc, craftId, "materialDescIdList.containsKey(descId)");
				return false;
			}
			int enchant = 0;
			int isEnchant = readC();
			if (isEnchant == 0x20) { // 인첸트템.
				enchant = readC();
				readP(6); // dummy
			} else {
				readP(5); // dummy
			}
			if (GMCommands.제작체크) {
				System.out.println("제작재료템 - descId : " + descId + " / enchant : " + enchant);
			}
			materialDescIdList.put(descId, enchant);
		}
		return true;
	}

	private void craftErrMsg(L1PcInstance pc, int craftId, String msg) {
		System.out.println("제작 실패: " + msg + " / craftId : " + craftId + " / 사용자 : " + pc.getName());
	}

	public void commit(String com, String name, int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM request_log WHERE command=?");
			pstm.setString(1, com);
			rs = pstm.executeQuery();
			Connection con2 = null;
			PreparedStatement pstm2 = null;
			try {
				con2 = L1DatabaseFactory.getInstance().getConnection();
				if (rs.next()) {
					int amount = rs.getInt("count");
					pstm2 = con2.prepareStatement("UPDATE request_log SET count=? WHERE command=?");
					pstm2.setInt(1, amount + count);
					pstm2.setString(2, com);
				} else {
					pstm2 = con2.prepareStatement("INSERT INTO request_log SET command=?, count=?");
					pstm2.setString(1, com);
					pstm2.setInt(2, count);
				}
				pstm2.executeUpdate();
			} catch (SQLException e) {
			} finally {
				SQLUtil.close(pstm2);
				SQLUtil.close(con2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public String getType() {
		return C_ACTION_UI;
	}

}
