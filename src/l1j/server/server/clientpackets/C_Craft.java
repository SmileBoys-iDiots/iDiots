package l1j.server.server.clientpackets;

import java.io.IOException;
import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ACTION_UI2;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.CommonUtil;
import server.LineageClient;

public class C_Craft extends ClientBasePacket {

	//private static Logger _log = Logger.getLogger(C_Craft.class.getName());

	public static final int DOLL_START = 122; // 시작
	public static final int DOLL_RESULT = 124; // 클릭
	private static final int NewStat = 228;
	private static final int SEAL = 0x39; // 봉인 아이콘
	private static final int Party_Assist = 0x57;
	private static final int Party_Invitation = 0x3c;
	private static final int 개인상점 = 49;

	public C_Craft(byte[] data, LineageClient client) throws IOException {
		super(data);
		if (client == null) {
			return;
		}
		L1PcInstance pc = client.getActiveChar();

		int type = readC();
		if(type!=NewStat && pc==null)
			return;

		//System.out.println("크래프트 > " + type);
		switch (type) {
		case 개인상점:
			if (pc == null || pc.isGhost() || pc.isDead()) {
				return;
			}
			if (pc.isInvisble()) {
				pc.sendPackets(new S_ServerMessage(755), true);
				return;
			}
			if (pc.getMapId() != 800) {
				pc.sendPackets(new S_ServerMessage(3405)); //개설 불가 지역
				return;
			}

			if (Config.STANDBY_SERVER ){
				pc.sendPackets(new S_SystemMessage("오픈대기중에는 사용할 수 없습니다."), true);
				pc.sendPackets(new S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Idle));
				Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Idle));
				break;
			}
			
			for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
				if (target.getId() != pc.getId() 
						&& target.getAccountName().toLowerCase().equals(pc.getAccountName().toLowerCase()) 
						&& target.isPrivateShop()) {
					pc.sendPackets(new S_SystemMessage("\\aH알림: 이미 무인상점이 개설 되어 있습니다 ."), true);
					return;
				}
			}

			ArrayList<L1PrivateShopSellList> sellList = pc.getSellList();
			ArrayList<L1PrivateShopBuyList> buyList = pc.getBuyList();
			L1ItemInstance checkItem;
			boolean tradable = true;
			
			readC();
			int length = readBit();
			
			readC();
			readC();
			int shoptype = readC(); // 0 or 1

			if (shoptype == 0) { // 개시
				int sellTotalCount = 0;
				int buyTotalCount = 0;
				int sellObjectId = 0;
				int sellPrice = 0;
				int sellCount = 0;
				L1ItemInstance sellitem = null;
				Object[] petlist = null;
				L1ItemInstance buyitem = null;
				for (int i = 0; i < length; i++) {
					int code = readC();
					if(code == 0x12 || code == 0x1a){
						readC();
						for(int i2=0;i2<3;i2++ ){
							int code2 = readC();
							if (code2 == 0x08)
								sellObjectId = readBit();
							else if (code2 == 0x10)
								sellPrice = readBit();
							else if (code2 == 0x18)
								sellCount = readBit();							
						}
						// 거래 가능한 아이템이나 체크
						checkItem = pc.getInventory().getItem(sellObjectId);

						if (checkItem == null) {
							continue;
						}

						if (sellObjectId != checkItem.getId()) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\aH알림: 비정상 아이템 입니다. 다시 시도해주세요."), true);
						}
						if (!checkItem.isStackable() && sellCount != 1) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\aH알림: 비정상 아이템 입니다. 다시 시도해주세요."), true);
						}
						if (sellCount > checkItem.getCount()) {
							sellCount = checkItem.getCount();
						}
						if (checkItem.getCount() < sellCount || checkItem.getCount() <= 0 || sellCount <= 0) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\aH알림: 비정상 아이템 입니다. 다시 시도해주세요."), true);
						}
						if (checkItem.getBless() >= 128) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName())); //봉인상태
						}
						
						if (!checkItem.getItem().isTradable()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(941), true); //거래 불가 아이템입니다.
						}
						
						L1DollInstance 인형 = null;
						for (Object 인형오브젝트 : pc.getDollList()) {
							if (인형오브젝트 instanceof L1DollInstance) {
								인형 = (L1DollInstance) 인형오브젝트;
								if (checkItem.getId() == 인형.getItemObjId()) {
									tradable = false;
									pc.sendPackets(new S_ServerMessage(941), true); //거래 불가 아이템입니다.
								}
							}
						}

						petlist = pc.getPetList().toArray();
						for (Object petObject : petlist) {
							if (petObject instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) petObject;
								if (checkItem.getId() == pet.getItemObjId()) {
									tradable = false;
									pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "\\aH알림: 거래가 불가능 합니다."));
									return;
								}
							}
						}
						
						if (code == 0x12){
							if(sellTotalCount > 7){ 
								pc.sendPackets(new S_SystemMessage("\\aH알림: 물품은 7개 까지 등록 가능합니다.")); 
								return;
							}
							
							L1PrivateShopSellList pssl = new L1PrivateShopSellList();
							pssl.setItemObjectId(sellObjectId);
							pssl.setSellPrice(sellPrice);
							pssl.setSellTotalCount(sellCount);
							sellList.add(pssl);
							sellTotalCount++;
						} else if (code == 0x1a){
							if(buyTotalCount > 7){ 
								pc.sendPackets(new S_SystemMessage("\\aH알림: 물품은 7개 까지 등록 가능합니다.")); 
								return;
							}
							L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
							psbl.setItemObjectId(sellObjectId);
							psbl.setBuyPrice(sellPrice);
							psbl.setBuyTotalCount(sellCount);
							buyList.add(psbl);
							buyTotalCount++;
						}
					} else {
						break;
					}
				}

				if (sellTotalCount == 0 && buyTotalCount == 0) {
					pc.sendPackets(new S_ServerMessage(908), true);
					pc.setPrivateShop(false);
					pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					return;
				}

				if (!tradable) { // 거래 불가능한 아이템이 포함되어 있는 경우, 개인 상점 종료
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					return;
				}
				
				/** 수수료 부과 **/
				int shopOpenCount = pc.getNetConnection().getAccount().Shop_open_count;
				if (shopOpenCount >= 40) {
					int OpenAdena = 20000 + ((shopOpenCount - 40) * 1000);
					if (!pc.getInventory().consumeItem(40308, OpenAdena)) {
						sellList.clear();
						buyList.clear();
						pc.setPrivateShop(false);
						pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
						Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
						pc.sendPackets(new S_ServerMessage(189), true); //아데나가 부족합니다
						return;
					} else {					
				        pc.getInventory().consumeItem(40308, OpenAdena);
					}
				} else {
					if (!pc.getInventory().consumeItem(40308, 1000)) {
						sellList.clear();
						buyList.clear();
						pc.setPrivateShop(false);
						pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
						Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
						pc.sendPackets(new S_ServerMessage(189), true); //아데나가 부족합니다
						return;
					} else {					
				        pc.getInventory().consumeItem(40308, 1000);
					}
				}
				
				int l1 = readC();
		        byte[] chat = readByte(l1); 
		        
		        readC();
		        int l2 = readC();
		        String polynum = readS(l2); 
				
				pc.setShopChat(chat);
				
				pc.setPrivateShop(true);
				
				pc.sendPackets(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat), true);
				Broadcaster.broadcastPacket(pc, new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat), true);

				try {
					for (L1PrivateShopSellList pss : pc.getSellList()) {
						int sellp = pss.getSellPrice();
						int sellc = pss.getSellTotalCount();
						sellitem = pc.getInventory().getItem(pss.getItemObjectId());
						if (sellitem == null)
							continue;
						pc.임시SaveShop(pc, sellitem, sellp, sellc, 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					for (L1PrivateShopBuyList psb : pc.getBuyList()) {
						int buyp = psb.getBuyPrice();
						int buyc = psb.getBuyTotalCount();
						buyitem = pc.getInventory().getItem(psb.getItemObjectId());
						if (buyitem == null)
							continue;
						pc.임시SaveShop(pc, buyitem, buyp, buyc, 0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					int polyId = 0;
					if (polynum.equalsIgnoreCase("tradezone1"))
						polyId = 11326;
					else if (polynum.equalsIgnoreCase("tradezone2"))
						polyId = 11427;
					else if (polynum.equalsIgnoreCase("tradezone3"))
						polyId = 10047;
					else if (polynum.equalsIgnoreCase("tradezone4"))
						polyId = 9688;
					else if (polynum.equalsIgnoreCase("tradezone5"))
						polyId = 11322;
					else if (polynum.equalsIgnoreCase("tradezone6"))
						polyId = 10069;
					else if (polynum.equalsIgnoreCase("tradezone7"))
						polyId = 10034;
					else if (polynum.equalsIgnoreCase("tradezone8"))
						polyId = 10032;

					if (polyId != 0) {
						pc.getSkillEffectTimerSet().killSkillEffectTimer(67);
						L1PolyMorph.undoPoly(pc);
						L1ItemInstance weapon = pc.getWeapon();
						if (weapon != null)
							pc.getInventory().setEquipped(weapon, false, false, false);
						pc.getGfxId().setTempCharGfx(polyId);
						pc.sendPackets(new S_ChangeShape(pc.getId(), polyId, pc.getCurrentWeapon()));
						if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
							Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), polyId));
						}
						S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc, 0x46);
						pc.sendPackets(charVisual);
						Broadcaster.broadcastPacket(pc, charVisual);
					}
					pc.getNetConnection().getAccount().updateShopOpenCount();
					pc.sendPackets(new S_PacketBox(S_PacketBox.상점개설횟수, pc.getNetConnection().getAccount().Shop_open_count), true);
					pc.sendPackets(new S_ChatPacket(pc, "\\aH알림: .무인상점 입력시 무인상점 진행 ."));
				} catch (Exception e) {
					pc.상점아이템삭제(pc.getId());
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					return;
				}
				petlist = null;
			} else if (shoptype == 1) { // 종료
				if (isTwoLogin(pc)) {
					pc.sendPackets(new S_Disconnect());
				}
				sellList.clear();
				buyList.clear();
				pc.setPrivateShop(false);
				pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
				L1PolyMorph.undoPoly(pc);
				try {
					pc.상점아이템삭제(pc.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case SEAL: 	
			readH();
			readH();
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(read4(read_size()));
			if(l1iteminstance1.getItem().getType2()==0){ // etc 아이템이라면 
				pc.sendPackets(new S_ServerMessage(79)); // 아무일도 일어나지 않는다 (멘트)
				return;
			}
			if (l1iteminstance1.getBless() == 0 || l1iteminstance1.getBless() == 1
					|| l1iteminstance1.getBless() == 2 || l1iteminstance1.getBless() == 3) {
				int Bless = 0;
				switch (l1iteminstance1.getBless()) {
				case 0: Bless = 128; break; //축
				case 1: Bless = 129; break; //보통
				case 2: Bless = 130; break; //저주
				case 3: Bless = 131; break; //미확인
				}
				l1iteminstance1.setBless(Bless);
				int st = 0;
				if (l1iteminstance1.isIdentified()) st += 1;
				if (!l1iteminstance1.getItem().isTradable()) st += 2;
				if (l1iteminstance1.getItem().isCantDelete()) st += 4;
				if (l1iteminstance1.getItem().get_safeenchant() < 0) st += 8;
				if (l1iteminstance1.getBless() >= 128) {
					st = 32;
					if (l1iteminstance1.isIdentified()) {
						st += 15;
					} else {
						st += 14;
					}
				}
				pc.sendPackets(new S_PacketBox(S_PacketBox.ITEM_, l1iteminstance1, st));
				pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
				pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_IS_ID);
			} else
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
			break;
		case Party_Assist :
			readH();
			readH();
			int Object = read4(read_size());
			if (pc.isInParty()) {
				pc.getParty().updateAssist(pc, Object);
			}
			break;
		case Party_Invitation:	
			readD();
			int Type = readC();
			L1PcInstance Player = null;
			if(Type != 2 && Type != 4 && Type != 5 && Type != 7){
				readC();
				L1Object target = L1World.getInstance().findObject(readBit());
				if(target != null) Player = (L1PcInstance) target;
			}
			readC();
			int NameLength = readC();
			if(Player == null){
				Player = L1World.getInstance().getPlayer(readS(NameLength));
			}else readS(NameLength);
			if(Player == null) return;
			/** 0번 일반 파티 1번 분배파티 2번 채팅파티 5/초대 이름 6번 표식  7번추방 8번파티위임 */
			if (Type == 0 || Type == 1 || Type == 4 || Type == 5) {
				if (Player instanceof L1PcInstance) {
					if (pc.getId() == Player.getId()) return;
					if (Player.isInParty()) {
						/** 벌써 다른 파티에 소속해 있기 (위해)때문에 초대할 수 없습니다 */
						pc.sendPackets(new S_ServerMessage(415), true);
						return;
					}

					if (pc.isInParty()) {
						if (pc.getParty().isLeader(pc)) {
							Player.setPartyID(pc.getId());
							/** \f2%0\f>%s로부터 \fU파티 \f> 에 초대되었습니다. 응합니까? (Y/N) */
							Player.sendPackets(new S_Message_YN(953, pc.getName()), true);
						} else {
							/** 파티의 리더만을 초대할 수 있습니다. */
							pc.sendPackets(new S_ServerMessage(416), true);
						}
					} else {
						Player.setPartyID(pc.getId());
						switch (Type) {
						case 4:
						case 0:
							pc.setPartyType(0);
							/** \f2%0\f>%s로부터 \fU파티 \f> 에 초대되었습니다. 응합니까? (Y/N) */
							Player.sendPackets(new S_Message_YN(953,pc.getName()), true);
							break;
						case 5:
						case 1:
							pc.setPartyType(1);
							/** \f2%0\f>%s \fU자동분배파티\f> 초대하였습니다. 허락하시겠습니까? (Y/N) */
							Player.sendPackets(new S_Message_YN(954,pc.getName()), true);
							break;
						}
					}
				}
			} else if (Type == 2) { // 채팅 파티
				if (pc.getId() == Player.getId()) return;
				if (Player.isInChatParty()) {
					/** 벌써 다른 파티에 소속해 있기 (위해)때문에 초대할 수 없습니다 */
					pc.sendPackets(new S_ServerMessage(415), true);
					return;
				}

				if (pc.isInChatParty()) {
					if (pc.getChatParty().isLeader(pc)) {
						Player.setPartyID(pc.getId());
						/** \f2%0\f>%s로부터\fU채팅 파티 \f>에 초대되었습니다. 응합니까? (Y/N) */
						Player.sendPackets(new S_Message_YN(951, pc.getName()), true);
					} else {
						/** 파티의 리더만을 초대할 수 있습니다. */
						pc.sendPackets(new S_ServerMessage(416), true);
					}
				} else {
					Player.setPartyID(pc.getId());
					/** \f2%0\f>%s로부터\fU채팅 파티 \f>에 초대되었습니다. 응합니까? (Y/N) */
					Player.sendPackets(new S_Message_YN(951, pc.getName()), true);
				}
			} else if (Type == 3) { // 위임
				/** 파티장이 아니거나 같은사람이라면 안되도록 하자 */
				if (!pc.getParty().isLeader(pc) || pc.getId() == Player.getId()) {
					return;
				}
				/** 맵이 다르거나 10미터 이하라면 위임 불가능 하도록 하자 */
				/*if (!isDistance(pc.getX(), pc.getY(), pc.getMapId(), Player.getX(), Player.getY(), Player.getMapId(), 10)){
						pc.sendPackets(new S_ServerMessage(1695)); 
						return;
					}*/
				pc.getParty().passLeader(Player);
			}else if (Type == 6){
				readC();
				Player.set표식(readC());
				for (L1PcInstance player : pc.getParty().getMembers()) {
					player.sendPackets(new S_Party(0x6e, Player));
				}
			} else if (Type == 7) { // 추방
				if (!pc.getParty().isLeader(pc)) {
					/** 파티의 리더만을 추방할 수 있습니다. */
					pc.sendPackets(new S_ServerMessage(427)); 
					return;
				}
				pc.getParty().leaveMember(Player);
			}
			break;

		case DOLL_START: {
			readC();
			pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_READY));
			pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_START));
			try {
				for (L1DollInstance doll : pc.getDollList()) {
					doll.deleteDoll();
				}
			} catch (Exception e) {
			}
			break;
		}
		case DOLL_RESULT: {
			readC();
			int total = (readH() - 2) / 12;
			readC();
			int step = readC();
			int[] dollids = new int[total];
			L1ItemInstance item;
			for (int i = 0; i < total; i++) {
				readC();
				readD();
				read4(read_size());
				readC();
				int objid = read4(read_size());

				item = pc.getInventory().getItem(objid);
				if (item == null) {
					return;
				}
				dollids[i] = item.getItemId();
				pc.getInventory().removeItem(item);
			}
			int chance2 = ((total * Config.인형확률2)); // 인형합성 확률 설정
			int chance3 = ((total * Config.인형확률3)); // 인형합성 확률 설정
			int chance4 = ((total * Config.인형확률4)); // 인형합성 확률 설정
			int chance5 = ((total * Config.인형확률5)); // 인형합성 확률 설정
			switch (step) {
			case 1:
				if (CommonUtil.random(100) + 1 <= chance2) {
					dollids = new int[] { 
							430001, // 장로
							41249, // 서큐버스
							430500, // 코카트리스
							500109, // 눈사람(A) ?
							500108, // 인어
							600242 // 라바골렘
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, false, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 2:
				if (CommonUtil.random(100) + 1 <= chance3) {
					dollids = new int[] { 
							500205, // 서큐 퀸
							500204, // 흑장로
							500203, // 자이언트
							60324, // 드레이크
							500110, // 킹 버그베어
							600243 // 다이아몬드골렘
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							//L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4433, item.getItem().getNameId(), pc.getName()));
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, false, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 3:
				if (CommonUtil.random(100) + 1 <= chance4) {
					dollids = new int[] { 
							500202, // 사이클롭스
							5000035, // 리치
							600245, // 나이트발드
							600244, // 시어
							142920, // 아이리스
							142921, // 뱀파이어
							751 // 머미로드
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, false, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 4://마지막 나오는 단계
				if (CommonUtil.random(100) + 1 <= chance5) {
					dollids = new int[] { 
							600246, // 데몬
							600247, // 데스나이트 746
							142922, // 바란카
							752, // 타락
							753, //바포
							754, //얼녀
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, false, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 5:
				if (CommonUtil.random(100) + 1 <= 50) {
					dollids = new int[] {
							600321, // 데몬
							600322, // 데스나이트 746
							600323, // 바란카
							600324, // 타락
							600325, //바포
							600326, //얼녀
							600327, //커츠
							142922, // 바란카
							600327, //커츠
							142922, // 바란카
							600324, // 타락
							600325, //바포
							600327, //커츠
							142922, // 바란카
							600324, // 타락
							600325, //바포
							600327, //커츠
							142922, // 바란카
							600324, // 타락
							600325, //바포
							600327, //커츠
							142922, // 바란카
							600324, // 타락
							600325, //바포
							600327, //커츠
							142922, // 바란카
							600324, // 타락
							600325, //바포
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), true));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					dollids = new int[] {
							600246, // 데몬
							600247, // 데스나이트 746
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							142922, // 바란카
							752, // 타락
							753, //바포
							755, //커츠
							600259, // 안타
							600261, // 린드
							600260, // 파푸
							600262 // 발라
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				}
				break;
			case 6:
				if (CommonUtil.random(100) + 1 <= 50) {
					dollids = new int[] {
							600308, // 축흑장
							600309, // 축자이언트
							600310, // 축서큐
							600311, // 축드레끼
							600312, //축킹버그
							600313 //축다이아몬드골렘
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), true));
					pc.getInventory().storeItem(item);
				} else {
					dollids = new int[] { 
							500205, // 서큐 퀸
							500204, // 흑장로
							500203, // 자이언트
							60324, // 드레이크
							500110, // 킹 버그베어
							600243 // 다이아몬드골렘
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 7:
				if (CommonUtil.random(100) + 1 <= 40) {
					dollids = new int[] {
							600314, // 축리치
							600315, // 축시어
							600316, // 축나발
							600317, // 축시어
							600318, //축아이리스
							600319, //축뱀파
							600320 //축머미
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), true));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					dollids = new int[] {
							500202, // 사이클롭스
							5000035, // 리치
							600245, // 나이트발드
							600244, // 시어
							142920, // 아이리스
							142921, // 뱀파이어
							751, // 머미로드
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.리마월드메시지, 4433, item), true);
						} catch (Exception e) {
						}
					}
				}
				break;
			}
		}
		break;
		default:
			break;
		}
	}
	
	private boolean isTwoLogin(L1PcInstance c) {// 중복체크 변경 
		boolean bool = false;
		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.noPlayerCK || target.isRobot())
				continue;
			if (c.getId() != target.getId() && (!target.isPrivateShop())) {
				if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}


	public String getType() {
		return "[C] C_Craft";
	}
}
