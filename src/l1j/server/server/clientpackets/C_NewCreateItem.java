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

package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.Gamble.GambleInstance;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.GameSystem.SupportSystem.L1SupportMap;
import l1j.server.GameSystem.SupportSystem.SupportMapTable;
import l1j.server.GameSystem.UserRanking.UserRankingController;
import l1j.server.MJDShopSystem.MJDShopItem;
import l1j.server.MJDShopSystem.MJDShopStorage;
import l1j.server.server.Account;
import l1j.server.server.ActionCodes;
import l1j.server.server.BadNamesList;
import l1j.server.server.GMCommands;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.UserCommands;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.AttendanceTable;
import l1j.server.server.datatables.AttendanceTable.AttendanceItem;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExcludeLetterTable;
import l1j.server.server.datatables.ExcludeTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcShopSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1ExcludingLetterList;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ACTION_UI;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharMapTime;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_DollAlchemyInfo;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NewCreateItem;
import l1j.server.server.serverpackets.S_NewCreateItem_list;
import l1j.server.server.serverpackets.S_NewUI;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_PartyAssist;
import l1j.server.server.serverpackets.S_QuestTalkIsland;
import l1j.server.server.serverpackets.S_Revenge;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SupportSystem;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1QuestInfo;
import l1j.server.server.templates.L1UserRanking;
import l1j.server.server.utils.BinaryOutputStream;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.SQLUtil;
import server.LineageClient;
import server.controller.InvSwapController;
import server.manager.eva;

@SuppressWarnings("unused")
public class C_NewCreateItem extends ClientBasePacket {
	private boolean _idcheck;
	private static final String C_NEW_CREATEITEM = "[C] C_NewCreateItem";
	private static final String[] textFilter = { "시발" };

	private static final int RESTART_UI = 802;
	private static final int MONSTER_BOOKS_TELEPORT = 565;
	private static final int CHAT = 514;
	private static final int CRAFT_OK = 58;
	private static final int \uc720\uc800\ub7ad\ud0b9 = 135;
	private static final int CRAFT_ITEM = 54;
	private static final int CRAFT_ITEMLIST = 56;
	private static final int ENVIRONMENT_SETTING = 1002;
	private static final int START_PLAY_SUPPORT 	= 2101;
	private static final int FINISH_PLAY_SUPPORT 	= 2103;
	private static final int AINHASAD_DAILY_POINT 	= 2324;
	
	/**월드맵이동*/
	private static final int WorldMapTeleport 	= 829;
	/**월드맵이동*/
	private static final int 상태창랭킹 = 0x03fd;
	private Iterator<L1NpcInstance> localIterator;

	public C_NewCreateItem(byte[] decrypt, LineageClient client) {
		super(decrypt);
		try {
			int type = readH();

			L1PcInstance pc = client.getActiveChar();
			if (type != 0x01e4) {
				if (pc == null)
					return;
			}
			if (type == RESTART_UI) {

				pc.sendPackets(new S_CharMapTime(pc));
			} else if (type == ENVIRONMENT_SETTING) {
				if (pc == null)
					return;

				if (pc.isOneMinuteStart()) {
					resetAttendanceTime(pc.getAccount(), pc);
					getAttendanceHome(pc, pc.getAccount());
					getAttendancePCRoom(pc, pc.getAccount());
				}

				if (!pc.isOneMinuteStart()){
					pc.setOneMinuteStart(true);
				}
			} else if (type == CHAT) {
				int totallen = readH();// 전체길이
				패킷위치변경((byte) 0x10);// 위치이동
				int chattype = readC();// 채팅타입
				패킷위치변경((byte) 0x1a);// 위치이동
				int chatlen = readC();// 채팅길이
				BinaryOutputStream os = new BinaryOutputStream();
				for (int i = 0; i < chatlen; i++) {
					os.writeC(readC());
				}
				byte[] chat = os.getBytes();
				String chat2 = new String(chat, "EUC-KR");
				os.close();
				String chattext = "";
				String name = "";
				if (chattext.startsWith(Config._name)) {
					한문체크(true);
				}

				if (chattype != 1) {
					Chat(pc, chattype, totallen, chat, chat2, client);
					return;
				}

				패킷위치변경((byte) 0x2a);// 위치이동
				int namelen = readC();// 이름길이
				if (namelen != 0) {
					name = readS(namelen);
				}
				ChatWhisper(pc, chattype, totallen, chat, chat2, name);
				/*
				 * } else if (type == 0x032b) { readH(); //주간퀘스트보상 readC(); int sort = readC();
				 * // 0 1 2 (단계) readC(); int sort2 = readC(); // 1 2 3 (버튼위치) if (sort == 0) {
				 * if (sort2 == 1) { // pc.경험치보상(pc, 52, 0.01); L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 10); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(0, true); } else if (sort2 == 2) { // 다이아몬드 if
				 * (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 20); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(0, true); } } else if (sort2 == 3) { //
				 * 고급다아이몬드 if (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 50); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(0, true); } } } else if (sort == 1) { if
				 * (sort2 == 1) { // pc.경험치보상(pc, 52, 0.01); L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 10); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(1, true); } else if (sort2 == 2) { // 다이아몬드 if
				 * (pc.getInventory().consumeItem(437010, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 20); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(1, true); } } else if (sort2 == 3) { //
				 * 고급다아이몬드 if (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 50); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(1, true); } } } else if (sort == 2) { if
				 * (sort2 == 1) { // pc.경험치보상(pc, 52, 0.01); L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 10); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(2, true); } else if (sort2 == 2) { // 다이아몬드 if
				 * (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 20); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(2, true); } } else if (sort2 == 3) { //
				 * 고급다아이몬드 if (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 50); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(2, true); } } }
				 */
			} else if (type == 801) {
				readH();

				// readC();
				int index = readC();
				int code = readC();
				if (index == 0x08) {
					InvSwapController.getInstance().toSaveSet(pc, code);
				} else if (index == 0x10) {
					InvSwapController.getInstance().toChangeSet(pc, code);
				}
			} else if (type == WorldMapTeleport) {
				readC();
				readH();
				int chatlen = readC();
				BinaryOutputStream os = new BinaryOutputStream();
				for (int i = 0; i < chatlen; i++) {
					os.writeC(readC());
				}
				String code = new String(os.getBytes());
				for (L1NpcInstance tel_map_npc : L1World.getInstance().getAllTeleporter()) {
					if (tel_map_npc.getNpcId() == 50036) {
						L1Object npc = L1World.getInstance().findObject(tel_map_npc.getId());
						L1NpcAction action = NpcActionTable.getInstance().get(code, pc, npc);
						if (pc.getInventory().checkItem(140100)) {
							pc.getInventory().consumeItem(140100, 1);
							action.execute(code, pc, npc, null);
							//System.out.println("message" + code);
						}
					}
				}
			} else if (type == START_PLAY_SUPPORT) {
				if (SupportMapTable.getInstance().isSupportMap(pc.getMapId())) {
					L1SupportMap SM = SupportMapTable.getInstance().getSupportMap(pc.getMapId());
					if (SM != null) {
						if (pc.getLevel() > 1 && pc.getLevel() <= Config.클라우디아레벨) {
							L1QuestInfo info = pc.getQuestList(368);
							if (info != null && info.end_time == 0) {
								info.ck[0] = 1;
								pc.sendPackets(new S_QuestTalkIsland(pc, 368, info));
							}
						}
						pc.setAutoPlay(true);
						pc.sendPackets(new S_SupportSystem(pc, S_SupportSystem.SC_START_PLAY_SUPPORT_ACK), true);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("이 곳에서는 사용할 수 없습니다."), true);
				}
		    } else if (type == FINISH_PLAY_SUPPORT) {
				pc.setAutoPlay(false);
				pc.sendPackets(new S_SupportSystem(pc, S_SupportSystem.SC_FINISH_PLAY_SUPPORT_ACK), true);
//		    } else if (type == AINHASAD_DAILY_POINT) {
//				if(pc.getAinHasad() < 10000000 ) {
//					pc.setAinHasad(Config.아인하사드데일리포인트);
//					pc.setAinHasadDP(0);
//					pc.updateAinhasadBonus(pc.getId());
//					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.AINHASAD, pc), true);
//				} else {
//					pc.sendPackets(new S_SystemMessage("\\aH알림: 아인하사드 수치가 1000이하에서만 이용 가능합니다."), true);
//				}
			} else if (type == 상태창랭킹) {
				int 타입 = readH();
				readC();
				long 시간 = read4(read_size());
				L1UserRanking classRank = UserRankingController.getInstance().getClassRank(pc.getType(), pc.getName());
				L1UserRanking rank = UserRankingController.getInstance().getTotalRank(pc.getName());
				if(classRank == null && rank == null){
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.상태창랭킹, classRank,  rank, true));
				}else if(타입 == 2){
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.상태창랭킹, classRank,  rank, true));
				}else if(타입 == 6 && UserRankingController.랭킹갱신 >= 시간){
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.상태창랭킹, classRank,  rank, true));
				}else pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.상태창랭킹, classRank,  rank, false));
			} else if (type == 0x87) {
				readH();
				readC();
				int classId = readC();
				ArrayList<L1UserRanking> list = UserRankingController.getInstance().getList(classId);

				if (list.size() > 100) {
					List<L1UserRanking> cutlist = list.subList(0, 100);
					List<L1UserRanking> cutlist2 = list.subList(100, list.size() > 200 ? 200 : list.size());
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.유저랭킹, cutlist, classId, 2, 1));
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.유저랭킹, cutlist2, classId, 2, 2));
				} else {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.유저랭킹, list, classId, 1, 1));
				}
//			} else if (type == 817) {
//				int length = readBit();
//				readC(); // 0x00
//				readC(); // 0x08
//				if (readC() == 0)
//					setPrivateShopType0(pc, length);
//				else
//					setPrivateShopType1(pc);
			} else if (type == 0x021F) {
				readH();
				readC();
				int excludeType = readC();
				readC();
				int subType = readC();
				int nameFlag = readC();
				String name = "";
				if (nameFlag == 0x1A) {
					int nameLength = readC();
					name = readS(nameLength);
				}

				try {
					if (name.isEmpty()) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.ADD_EXCLUDE2, pc.getExcludingList().getList(), 0),
								true);
						pc.sendPackets(
								new S_PacketBox(S_PacketBox.ADD_EXCLUDE2, pc.getExcludingLetterList().getList(), 1),
								true);
						return;
					}

					if (excludeType == 1) {// 추가
						L1ExcludingList exList = pc.getExcludingList();
						L1ExcludingLetterList exletterList = pc.getExcludingLetterList();
						switch (subType) {// 일반 0 편지 1
						case 0:
							if (exList.contains(name)) {
								/*
								 * String temp = exList.remove(name); S_PacketBox pb = new
								 * S_PacketBox(S_PacketBox.REM_EXCLUDE, temp, type); pc.sendPackets(pb, true);
								 * ExcludeTable.getInstance().delete(pc.getName( ), name);
								 */
							} else {
								if (exList.isFull()) {
									S_SystemMessage sm = new S_SystemMessage("차단된 사용자가 너무 많습니다.");
									pc.sendPackets(sm, true);
									return;
								}
								exList.add(name);
								S_PacketBox pb = new S_PacketBox(S_PacketBox.ADD_EXCLUDE, name, 0);
								pc.sendPackets(pb, true);
								ExcludeTable.getInstance().add(pc.getName(), name);
							}
							break;
						case 1:
							if (exletterList.contains(name)) {
							} else {
								if (exletterList.isFull()) {
									S_SystemMessage sm = new S_SystemMessage("차단된 사용자가 너무 많습니다.");
									pc.sendPackets(sm, true);
									return;
								}
								exletterList.add(name);
								S_PacketBox pb = new S_PacketBox(S_PacketBox.ADD_EXCLUDE, name, 1);
								pc.sendPackets(pb, true);
								ExcludeLetterTable.getInstance().add(pc.getName(), name);
							}
							break;
						default:
							break;
						}
					} else if (excludeType == 2) {// 삭제
						L1ExcludingList exList = pc.getExcludingList();
						L1ExcludingLetterList exletterList = pc.getExcludingLetterList();
						switch (subType) {// 일반 0 편지 1
						case 0:
							if (exList.contains(name)) {
								String temp = exList.remove(name);
								S_PacketBox pb = new S_PacketBox(S_PacketBox.REM_EXCLUDE, temp, 0);
								pc.sendPackets(pb, true);
								ExcludeTable.getInstance().delete(pc.getName(), name);
							}
							break;
						case 1:
							if (exletterList.contains(name)) {
								String temp = exletterList.remove(name);
								S_PacketBox pb = new S_PacketBox(S_PacketBox.REM_EXCLUDE, temp, 1);
								pc.sendPackets(pb, true);
								ExcludeLetterTable.getInstance().delete(pc.getName(), name);
							}
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
				}
			} else if (type == 1006) { // 이거 상관없나요?아여기다 쓰실꺼구나 ㅈㅅ..
				try {
					if (pc == null) {
						return;
					}

					if (pc.hasSkillEffect(L1SkillId.출석체크딜레이)) {
						return;
					}

					pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.출석체크딜레이, 3000);
					readH();
					readC();

					int objectId = readBit();
					readC();
					int size = readBit();
					Account account = pc.getAccount();
					AttendanceTable attend = AttendanceTable.getInstance();

					boolean check_one = size == 0 ? account.getAttendanceHomeBytes()[objectId - 1] != 1
							: account.getAttendancePcBytes()[objectId - 1] != 1;
					boolean check_two = size == 0 ? account.getAttendanceHomeBytes()[objectId - 1] == 2
							: account.getAttendancePcBytes()[objectId - 1] == 2;

					if (check_one || check_two) {
						System.out.println("★☆★ 중계기(출석체크) 의심 유저 : [" + account.getName() + "] ★☆★");
						return;
					}

					ArrayList<AttendanceItem> _item = size == 0 ? attend.getAttendHomeItem(objectId)
							: attend.getAttendPCItem(objectId);
					L1ItemInstance reward_item = null;
					AttendanceItem reward_info = null;
					int chance = CommonUtil.random(100);
					int reward_size = _item.size();

					for (int i = 0; i < reward_size; i++) {
						reward_info = _item.get(i);
						if (chance < reward_info._probability) {
							reward_item = ItemTable.getInstance().createItem(reward_info._itemId);
							if (reward_item != null) {
								if (reward_info._enchant != 0)
									reward_item.setEnchantLevel(reward_info._enchant);
								reward_item.setCount(reward_info._count);
								break;
							}
						}
					}

					if (size == 0) {
						account.getAttendanceHomeBytes()[objectId - 1] = 2;
						account.updateAttendaceDate();
						/** 출석체크 모두 완료시 초기화 **/
						if (account.isAllHomeAttendCheck()) {
							byte[] b = new byte[42];
							account.setAttendanceHomeBytes(b);
						}

					} else {
						account.getAttendancePcBytes()[objectId - 1] = 2;
						account.updateAttendacePcDate();

						/** 출석체크 모두 완료시 초기화 **/
						if (account.isAllPcAttendCheck()) {
							byte[] b = new byte[42];
							account.setAttendancePcBytes(b);
						}
					}

					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.ATTENDANCE_ITEM_COMPLETE, objectId, size == 0 ? false : true,
							reward_item, reward_info._broadcast_item));

					long delay_time = reward_size >= 2 ? 7000 : 0;
					/**
					 * 룰렛시간 지난후 적용
					 */
					AttendanceGiveItem agi = new AttendanceGiveItem(pc, reward_item, reward_info);
					GeneralThreadPool.getInstance().schedule(agi, delay_time);

					account.storeAttendBytes();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					clear();
				}
			} else if (type == 0x44) {

				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_WAIT, true), true);
				// System.out.println(itemcode+" > "+count);
			} else if (type == 0x013D) { // 세금 관련 맵 표시
				//
			} else if (type == 0x013F) {// 소셜액션
				readD();
				readC();
				int action = readC();
				if (action >= 1 && action <= 11) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.EMOTICON, action, pc.getId()), true);
					Broadcaster.broadcastPacket(pc, new S_NewCreateItem(S_NewCreateItem.EMOTICON, action, pc.getId()),
							true);
				}

			} else if (type == 0x45) {
				try {
					readH();
					readC();
					int castleType = readC();
					// 1켄트 2기란 4오크요새
					String s = "";
					for (L1Clan cc : L1World.getInstance().getAllClans()) {
						if (castleType == cc.getCastleId()) {
							s = cc.getClanName();
							break;
						}
					}

					if (s.equalsIgnoreCase("")) {
						return;
					}

					L1PcInstance player = pc;
					String clanName = player.getClanname();
					int clanId = player.getClanid();

					if (!player.isCrown()) { // 군주 이외
						S_ServerMessage sm = new S_ServerMessage(478);
						player.sendPackets(sm, true);
						return;
					}
					if (clanId == 0) { // 크란미소속
						S_ServerMessage sm = new S_ServerMessage(272);
						player.sendPackets(sm, true);
						return;
					}
					L1Clan clan = L1World.getInstance().getClan(clanName);
					if (clan == null) { // 자크란이 발견되지 않는다
						S_SystemMessage sm = new S_SystemMessage("대상 혈맹이 발견되지 않았습니다.");
						player.sendPackets(sm, true);
						return;
					}
					if (player.getId() != clan.getLeaderId()) { // 혈맹주
						S_ServerMessage sm = new S_ServerMessage(478);
						player.sendPackets(sm, true);
						return;
					}
					if (clanName.toLowerCase().equals(s.toLowerCase())) { // 자크란을
																			// 지정
						S_SystemMessage sm = new S_SystemMessage("자신의 혈에 공성 선포는 불가능합니다.");
						player.sendPackets(sm, true);
						return;
					}
					L1Clan enemyClan = null;
					String enemyClanName = null;
					for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // 크란명을
																					// 체크
						if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
							enemyClan = checkClan;
							enemyClanName = checkClan.getClanName();
							break;
						}
					}
					if (enemyClan == null) { // 상대 크란이 발견되지 않았다
						S_SystemMessage sm = new S_SystemMessage("대상 혈맹이 발견되지 않았습니다.");
						player.sendPackets(sm, true);
						return;
					}
					if (clan.getAlliance(enemyClan.getClanId()) == enemyClan) {
						S_ServerMessage sm = new S_ServerMessage(1205);
						player.sendPackets(sm, true);
						return;
					}
					List<L1War> warList = L1World.getInstance().getWarList(); // 전쟁
																				// 리스트를
																				// 취득
					if (clan.getCastleId() != 0) { // 자크란이 성주
						S_ServerMessage sm = new S_ServerMessage(474);
						player.sendPackets(sm, true);
						return;
					}
					if (enemyClan.getCastleId() != 0 && // 상대 크란이 성주로, 자캐릭터가
														// Lv25 미만
							player.getLevel() < 25) {
						S_ServerMessage sm = new S_ServerMessage(475);
						player.sendPackets(sm, true); // 공성전을 선언하려면 레벨 25에 이르지
														// 않으면 안됩니다.
						return;
					}

					int onLineMemberSize = 0;
					for (L1PcInstance onlineMember : clan.getOnlineClanMember()) {
						if (onlineMember.isPrivateShop())
							continue;
						onLineMemberSize++;
					}

					if (onLineMemberSize < Config.warmember) {
						player.sendPackets(
								new S_SystemMessage("접속중인 혈맹 구성원이 " + Config.warmember + " 명 이상 되어야 선포가 가능합니다."), true);
						return;
					}

					/*
					 * if (clan.getHouseId() > 0) { S_SystemMessage sm = new S_SystemMessage(
					 * "아지트가 있는 상태에서는 선전 포고를 할 수 없습니다."); player.sendPackets(sm, true); return; }
					 */
					if (enemyClan.getCastleId() != 0) { // 상대 크란이 성주
						int castle_id = enemyClan.getCastleId();
						if (WarTimeController.getInstance().isNowWar(castle_id)) { // 전쟁
																					// 시간내
							L1PcInstance clanMember[] = clan.getOnlineClanMember();
							for (int k = 0; k < clanMember.length; k++) {
								if (L1CastleLocation.checkInWarArea(castle_id, clanMember[k])) {
									// S_ServerMessage sm = new
									// S_ServerMessage(477);
									// player.sendPackets(sm, true); // 당신을 포함한
									// 모든 혈맹원이 성의 밖에 나오지 않으면 공성전은 선언할 수 없습니다.
									int[] loc = new int[3];
									Random _rnd = new Random(System.nanoTime());
									loc = L1CastleLocation.getGetBackLoc(castle_id);
									int locx = loc[0] + (_rnd.nextInt(4) - 2);
									int locy = loc[1] + (_rnd.nextInt(4) - 2);
									short mapid = (short) loc[2];
									L1Teleport.teleport(clanMember[k], locx, locy, mapid,
											clanMember[k].getMoveState().getHeading(), true);
								}
							}
							boolean enemyInWar = false;
							for (L1War war : warList) {
								if (war.CheckClanInWar(enemyClanName)) { // 상대
																			// 크란이
																			// 이미
																			// 전쟁중
									war.DeclareWar(clanName, enemyClanName);
									war.AddAttackClan(clanName);
									enemyInWar = true;
									break;
								}
							}
							if (!enemyInWar) { // 상대 크란이 전쟁중 이외로, 선전포고
								L1War war = new L1War();
								war.handleCommands(1, clanName, enemyClanName); // 공성전
																				// 개시
							}
						} else { // 전쟁 시간외
							S_ServerMessage sm = new S_ServerMessage(476);
							player.sendPackets(sm, true); // 아직 공성전의 시간이 아닙니다.
						}
					} else { // 상대 크란이 성주는 아니다
						return;
					}
				} catch (Exception e) {
				}
			} else if (type == 0x0142) {// /혈맹가입
				/** 2016.11.25 MJ 앱센터 혈맹 **/
				int joinType = readC();
				readH();
				int length = readC();

				// 존재하지 않는 혈맹입니다.
				if (pc.isCrown()) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 4), true);
					return;
				}

				// 이미 혈맹에 가입한 상태 입니다.
				if (pc.getClanid() != 0) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 9), true);
					return;
				}

				// 군주를 만나 가입해 주세요.
				try {
					String clanname = new String(readByteL(length), 0, length, "MS949");
					L1Clan clan = L1World.getInstance().getClan(clanname);
					//
					if (clan == null) {
						pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 13), true);
						return;
					}
					L1PcInstance crown = clan.getonline간부();
					switch (clan.getJoinType()) {
					case 1:
						if (crown == null) {
							pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 11), true);
							return;
						}

						crown.setTempID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
						S_Message_YN myn = new S_Message_YN(97, pc.getName());
						crown.sendPackets(myn, true);
						pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 1), true);
						return;
					case 2:
						readD();
						readC();
						int size = readC();
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < size; i++)
							sb.append(String.format("%02X", readC()));
						if (clan.getJoinPassword() == null || !clan.getJoinPassword().equalsIgnoreCase(sb.toString())) {
							pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 3), true);
							return;
						}
					case 0:
						if (crown != null) {
							C_Attr.혈맹가입(crown, pc, clan);
							pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 0), true);
						} else
							pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 1), true);
						break;
					default:
						pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 11), true);
						break;
					}
				} catch (Exception e) {
				}

			} else if (type == 0x0146) { // 혈맹 가입신청 받기 설정
				if (pc.getClanid() == 0 || (!pc.isCrown() && pc.getClanRank() != L1Clan.CLAN_RANK_GUARDIAN))
					return;
				readC();
				readH();
				int setting = readC();
				readC();
				int setting2 = readC();
				if (setting2 == 2) {
					pc.sendPackets(new S_SystemMessage("현재 암호 가입 유형으로 설정할 수 없습니다."), true);
					setting2 = 1;
				}

				pc.getClan().setJoinSetting(setting);
				pc.getClan().setJoinType(setting2);
				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, setting, setting2), true);
				ClanTable.getInstance().updateClan(pc.getClan());
				pc.sendPackets(new S_ServerMessage(3980), true);
			} else if (type == 0x014C) { // 혈맹 모집 셋팅
				if (pc.getClanid() == 0)
					return;
				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, pc.getClan().getJoinSetting(),
						pc.getClan().getJoinType()), true);
			} else if (type == 0x0152) { // 표식설정
				try {

					int length = readH();

					byte[] BYTE = readByte();
					byte[] objid = new byte[length - 3];
					byte[] subtype = new byte[1];

					if (pc.getParty() == null)
						return;
					if (!pc.getParty().isLeader(pc))
						return;

					System.arraycopy(BYTE, 1, objid, 0, objid.length);
					System.arraycopy(BYTE, length - 1, subtype, 0, 1);

					StringBuffer sb = new StringBuffer();

					for (byte zzz : objid) {
						sb.append(String.valueOf(zzz));
					}

					String s = sb.toString();

					L1PcInstance 표식pc = null;

					// System.out.println(s);

					for (L1PcInstance player : pc.getParty().getMembers()) {
						// System.out.println(player.encobjid);
						if (s.equals(player.encobjid)) {
							player.set표식(subtype[0]);
							표식pc = player;
						}
					}

					if (표식pc != null) {
						for (L1PcInstance player : pc.getParty().getMembers()) {
							player.sendPackets(new S_NewUI(0x53, 표식pc));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == 0x01e0) { // 탐 예약취소
				readC();
				readH();
				byte[] BYTE = readByte();
				byte[] temp = new byte[BYTE.length - 1];
				for (int i = 0; i < temp.length; i++) {
					temp[i] = BYTE[i];
				}
				StringBuffer sb = new StringBuffer();
				for (byte zzz : temp) {
					sb.append(String.valueOf(zzz));
				}
				int day = Nexttam(sb.toString());
				int charobjid = TamCharid(sb.toString());
				if (charobjid != pc.getId()) {
					pc.sendPackets(new S_SystemMessage("해당 케릭터만 취소를 할 수 있습니다."));
					return;
				}
				int itemid = 0;
				if (day != 0) {
					if (day == 7) {
						itemid = 5559;
					} else if (day == 30) {
						itemid = 5560;
					}
					L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(403, item.getName() + " (1)"), true);
						tamcancle(sb.toString());
						pc.sendPackets(new S_NewCreateItem(pc.getAccountName(), 0xcd));
					}
				}
			} else if (type == 0x01cc) { // 혈맹 모집 셋팅
				pc.sendPackets(new S_NewCreateItem(pc.getAccountName(), S_NewCreateItem.TamPage));
			} else if (type == 0x84) { // 수상한 하늘정원

				if (!pc.PC방_버프) {
					pc.sendPackets(new S_SystemMessage("PC방 이용권을 사용중에만 사용 가능한 행동입니다."), true);
					return;
				}
				if (pc.getMapId() == 99 || pc.getMapId() == 6202) {
					pc.sendPackets(new S_SystemMessage("주위의 마력에의해 순간이동을 사용할 수 없습니다."), true);
					return;
				}

				/*if (!pc.getMap().isTeleportable()) {
					pc.sendPackets(new S_SystemMessage("주위의 마력에의해 순간이동을 사용할 수 없습니다."), true);
					return;
				}*/

				int ran = _Random.nextInt(4);

				if (ran == 0) {
					L1Teleport.teleport(pc, 32779, 32825, (short) 622, pc.getMoveState().getHeading(), true);
				} else if (ran == 1) {
					L1Teleport.teleport(pc, 32761, 32819, (short) 622, pc.getMoveState().getHeading(), true);
				} else if (ran == 2) {
					L1Teleport.teleport(pc, 32756, 32837, (short) 622, pc.getMoveState().getHeading(), true);
				} else {
					L1Teleport.teleport(pc, 32770, 32839, (short) 622, pc.getMoveState().getHeading(), true);
				}

			} else if (type == 0x0202) {
				int totallen = readH();// 전체길이
				패킷위치변경((byte) 0x10);// 위치이동
				int chattype = readC();// 채팅타입
				패킷위치변경((byte) 0x1a);// 위치이동
				int chatlen = readC();// 채팅길이
				BinaryOutputStream os = new BinaryOutputStream();
				for (int i = 0; i < chatlen; i++) {
					os.writeC(readC());
				}
				byte[] chat = os.getBytes();
				String chat2 = new String(chat, "EUC-KR");
				os.close();
				String chattext = "";
				String name = "";
				if (chattext.startsWith(Config._name)) {
					한문체크(true);
				}

				패킷위치변경((byte) 0x2a);// 위치이동
				int namelen = readC();// 이름길이
				if (namelen != 0) {
					name = readS(namelen);
				}
				ChatWhisper(pc, chattype, totallen, chat, chat2, name);
				/*
				 * } else if (type == 0x7a) { // 인형합성창 if (pc.getInventory().calcWeightpercent()
				 * >= 90) { pc.sendPackets(new S_SystemMessage(
				 * "무게 게이지가 가득차서 합성을 진행할 수 없습니다.")); return; } if (client.인형합성패킷전송중) return;
				 * 
				 * readH();// length readC(); readC(); int unknown = readD();
				 * 
				 * pc.sendPackets(new S_NewCreateItem(0x80, "00 00")); if (unknown != 0 &&
				 * !client.인형합성패킷전송) { client.인형합성패킷전송 = true; if (unknown != -1528525972)
				 * client.인형합성패킷전송 = false; } if (client.인형합성패킷전송) { pc.sendPackets(new
				 * S_NewCreateItem(0x7b, "08 03 00 00")); } else { client.인형합성패킷전송중 = true;
				 * client.인형합성패킷전송 = true; GeneralThreadPool.getInstance().schedule( new
				 * Send_DollAlchemyInfo(client), 1); } } else if (type == 0x7c) { // 합성완료창 //
				 * pc.sendPackets(new S_SystemMessage("업데이트 준비중 입니다.")); int bytelen =
				 * readH();// 길이임 readH();
				 * 
				 * byte[] BYTE = readByte();
				 * 
				 * int a_len = 0; int _off = 0; a_len = BYTE[_off + 1];
				 * 
				 * for (int i = 0; i < BYTE.length; i++) { System.out.println(BYTE[i] & 0xff); }
				 * 
				 * // System.out.println("--------------------------------"); //
				 * System.out.println("--------------------------------"); StringBuffer sb =
				 * null; StringBuffer sb2 = null; byte[] temp = null; ArrayList<L1ItemInstance>
				 * _usedoll = new ArrayList<L1ItemInstance>(); L1ItemInstance item = null; while
				 * (bytelen > 7) { // System.out.println("a_len = "+a_len); //
				 * System.out.println("len = "+ bytelen); temp = new byte[a_len - 6];
				 * System.arraycopy(BYTE, _off + 8, temp, 0, a_len - 6); bytelen -= temp.length
				 * + 8; _off += temp.length + 8;
				 * 
				 * sb = new StringBuffer(); sb2 = new StringBuffer();
				 * 
				 * for (byte zzz : temp) { sb.append(HexToDex(zzz & 0xff, 2) + " ");
				 * sb2.append(String.valueOf(zzz)); }
				 * 
				 * item = pc.getInventory().findEncobj(sb2.toString());
				 * 
				 * if (item == null) { pc.sendPackets(new S_SystemMessage(
				 * "정상적인 방법으로만 이용해 주세요.")); return; }
				 * 
				 * _usedoll.add(item);
				 * 
				 * // System.out.println(sb.toString());
				 * 
				 * for (int i = 0; i < temp.length; i++) { System.out.println(temp[i] & 0xff); }
				 * 
				 * // System.out.println("--------------------------------"); }
				 * 
				 * try { String lll = "08"; if (temp.length == 5) { lll = "09"; } else if
				 * (temp.length == 4) { lll = "08"; } else if (temp.length == 3) { lll = "07"; }
				 * else if (temp.length == 2) { lll = "06"; } else if (temp.length == 1) { lll =
				 * "05"; } int rnd = _Random.nextInt(100) + 1; int suc = 1;// 실패
				 * 
				 * boolean 월드메세지 = false;
				 * 
				 * // 서로 같은 단계 아닐때 버그. // 실제로 인벤에 없을때 버그.
				 * 
				 * Collections.shuffle(_usedoll);
				 * 
				 * int itemid = _usedoll.get(0).getItemId(); int dollid = itemid; L1ItemInstance
				 * sucitem = null;
				 * 
				 * if (item_doll_code(itemid) == 1) { if (_usedoll.size() == 2) { if (rnd < 10)
				 * {// 성공 suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// 성공 suc
				 * = 0; } } else if (_usedoll.size() == 4) { if (rnd < 50) {// 성공 suc = 0; } }
				 * if (suc == 0) { dollid = lv2doll[_Random.nextInt(lv2doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); } } else if
				 * (item_doll_code(itemid) == 2) { if (_usedoll.size() == 2) { if (rnd < 10) {//
				 * 성공 suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// 성공 suc =
				 * 0; } } else if (_usedoll.size() == 4) { if (rnd < 50) {// 성공 suc = 0; } } if
				 * (suc == 0) { dollid = lv3doll[_Random.nextInt(lv3doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); // dollid = 176; } } else if
				 * (item_doll_code(itemid) == 3) { if (_usedoll.size() == 2) { if (rnd < 10) {//
				 * 성공 suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// 성공 suc =
				 * 0; } } else if (_usedoll.size() == 4) { if (rnd < 40) {// 성공 suc = 0; } } if
				 * (suc == 0) { dollid = lv4doll[_Random.nextInt(lv4doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); 월드메세지 = true; } } else if
				 * (item_doll_code(itemid) == 4) { if (_usedoll.size() == 2) { if (rnd < 10) {//
				 * 성공 suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// 성공 suc =
				 * 0; } } else if (_usedoll.size() == 4) { if (rnd < 40) {// 성공 suc = 0; } } if
				 * (suc == 0) { dollid = lv5doll[_Random.nextInt(lv5doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); 월드메세지 = true; } }
				 * 
				 * for (L1ItemInstance zzz : _usedoll) { pc.getInventory().removeItem(zzz); }
				 * 
				 * if (sucitem == null) { sucitem = ItemTable.getInstance().createItem(dollid);
				 * }
				 * 
				 * String ss = HexToDex(suc & 0xff, 2);
				 * 
				 * sucitem.setCount(1); // 0000: 6d e5 01 08 23 10 ad 12 18 dc 33 d9 ee //
				 * m...#.....3..
				 * 
				 * pc.sendPackets(new S_NewCreateItem(0x7D, "08 " + ss + " 12 " + lll + " 08",
				 * sucitem.getId(), "10 ", sucitem.get_gfxid()));
				 * 
				 * // pc.sendPackets(new //
				 * S_NewCreateItem(0x01E5,"08 23 10 ad 12 18 dc 33 00 00"));
				 * 
				 * if (suc == 0) { pc.getInventory().storeItem(sucitem, true);
				 * 
				 * // pc.sendPackets(new // S_NewCreateItem(0x01E5,"08 23 10 ad 12 18 dc 33 00
				 * // 00"));
				 * 
				 * if (월드메세지) { L1World.getInstance().broadcastPacketToAll( new
				 * S_ServerMessage(4433, sucitem.getItem() .getNameId(), pc.getName(), true)); }
				 * 
				 * } else { pc.getInventory().storeItem(sucitem); }
				 * 
				 * 
				 * if(suc==1){ L1ItemInstance faildoll = _usedoll.get(0); _usedoll.remove(0);
				 * for (L1ItemInstance zzz : _usedoll) { pc.getInventory().removeItem(zzz); }
				 * 
				 * //String sss = HexToDex(Config.test & 0xff, 2); String ss = HexToDex(suc &
				 * 0xff, 2);
				 * 
				 * pc.sendPackets(new S_NewCreateItem(0x7D,"08 "+ss+" 12 " +lll+" 08",
				 * faildoll.getId(), "10", faildoll.get_gfxid()));
				 * 
				 * }else{ for (L1ItemInstance zzz : _usedoll) {
				 * pc.getInventory().removeItem(zzz); }
				 * 
				 * //String sss = HexToDex(Config.test & 0xff, 2); String ss = HexToDex(suc &
				 * 0xff, 2);
				 * 
				 * L1ItemInstance sucitem = ItemTable.getInstance(). createItem(dollid);
				 * 
				 * sucitem.setCount(1);
				 * 
				 * pc.sendPackets(new S_NewCreateItem(0x7D,"08 "+ss+" 12 " +lll+" 08",
				 * sucitem.getId() , "10 ", sucitem.get_gfxid()));
				 * 
				 * pc.getInventory().storeItem(sucitem); }
				 * 
				 * 
				 * } catch (Exception e) { e.printStackTrace(); }
				 * 
				 * 
				 * 0e 00 08 01 10 00 18 08 20 01 1 28 10 16 30 0e 14 50 0b 11
				 * 
				 * 00
				 */
			} else if (type == 0x01e4) { // 캐릭터 생성
				try {
					int length = readH();// 길이
					ArrayList<byte[]> arrb = new ArrayList<byte[]>();
					for (int i = 0; i < length / 2; i++) {
						arrb.add(readByte(2));
					}
					int addstat, level, classtype = 0, status = 0, unknown2, unknown3 = 0, str = 0, cha = 0, inte = 0,
							dex = 0, con = 0, wis = 0;
					for (byte[] b : arrb) {
						switch (b[0]) {
						case 0x08:
							level = b[1] & 0xff;
							break;// 모름
						case 0x10:
							classtype = b[1] & 0xff;
							break;// 클래스 타입
						case 0x18:
							status = b[1] & 0xff;
							break;// 초기상태 = 1 / 스탯변경상태 = 8

						case 0x20:
							unknown2 = b[1] & 0xff;
							break;// 모름
						case 0x28:
							unknown3 = b[1] & 0xff;
							break;// 모름

						case 0x30:
							str = b[1] & 0xff;
							break;// 힘
						case 0x38:
							inte = b[1] & 0xff;
							break;// 인트
						case 0x40:
							wis = b[1] & 0xff;
							break;// 위즈
						case 0x48:
							dex = b[1] & 0xff;
							break;// 덱
						case 0x50:
							con = b[1] & 0xff;
							break;// 콘
						case 0x58:
							cha = b[1] & 0xff;
							break;// 카리

						default:
							int i = 0;
							try {
								i = b[0] & 0xff;
							} catch (Exception e) {
							}
							System.out.println("[스탯관련 정의되지 않은 패킷] op : " + i);
							break;
						}
					}

					if (str != 0 && unknown3 != 1) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, str, con, "힘", classtype, null));
					}
					if (dex != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, dex, 0, "덱", classtype, null));
					}
					if (con != 0 && unknown3 != 16) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, con, str, "콘", classtype, null));
					}
					if (inte != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, inte, 0, "인트", classtype, null));
					}
					if (wis != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, wis, 0, "위즈", classtype, null));
					}
					if (cha != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, cha, 0, "카리", classtype, null));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	private boolean isTwoLogin(L1PcInstance pc) {
		// TODO Auto-generated method stub
		return false;
	}

	public static final int[] hextable = { 0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x8b, 0x8c,
			0x8d, 0x8e, 0x8f, 0x90, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0x9b, 0x9c, 0x9d, 0x9e,
			0x9f, 0xa0, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb0,
			0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2,
			0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4,
			0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe0, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6,
			0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
			0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff };

	private static final int lv2doll[] = { 430001, 41249, 430500, 500108, 500109, 600242 };
	private static final int lv3doll[] = { 500205, 500204, 500203, 60324, 500110, 600243 };
	private static final int lv4doll[] = { 500202, 5000035, 600244, 600245, 142920, 142921, 751 };
	private static final int lv5doll[] = { 600246, 600247, 142922, 752, 753, 754, 755 };

	public int item_doll_code(int item) {
		int doll = 0;
		switch (item) {
		/*
		 * 41248 버그베어 41250 늑대인간 430000 돌골렘 430002 크러스트시안 430004 에티
		 */
		case 41248:
			doll = 1;
			break;
		case 41250:
			doll = 1;
			break;
		case 430000:
			doll = 1;
			break;
		case 430002:
			doll = 1;
			break;
		case 430004:
			doll = 1;
			break;
		case 600241:
			doll = 1;
			break;// 목각
		/*
		 * 430001 장로 41249 서큐 430500 코카 500108 인어 500109 눈사람
		 */
		case 430001:
			doll = 2;
			break;
		case 41249:
			doll = 2;
			break;
		case 430500:
			doll = 2;
			break;
		case 500108:
			doll = 2;
			break;
		case 500109:
			doll = 2;
			break;
		case 600242:
			doll = 2;
			break;// 라바골렘
		/*
		 * 500205 서큐퀸 500204 흑장로 500203 자이언트 60324 드레이크
		 */
		case 500205:
			doll = 3;
			break;
		case 500204:
			doll = 3;
			break;
		case 500203:
			doll = 3;
			break;
		case 60324:
			doll = 3;
			break;
		case 500110:
			doll = 3;
			break;
		case 600243:
			doll = 3;
			break;// 다이아골렘
		/*
		 * 500202 싸이클롭스 5000035 리치
		 */
		case 500202:
			doll = 4;
			break;
		case 5000035:
			doll = 4;
			break;
		case 600244:
			doll = 4;
			break;// 시어
		case 600245:
			doll = 4;
			break;// 나발
		case 142920:
			doll = 4;
			break;// 아이리스
		case 142921:
			doll = 4;
			break;// 뱀파
		case 751:
			doll = 4;
			break;// 머미로드
		case 600246:
			doll = 5;
			break;// 데몬
		case 600247:
			doll = 5;
			break;// 데스
		case 142922:
			doll = 5;
			break;// 바란카
		case 752:
			doll = 5;
			break;// 타락
		case 753:
			doll = 5;
			break;// 바포메트
		case 754:
			doll = 5;
			break;// 얼음여왕
		case 755:
			doll = 5;
			break;// 커츠
		default:
			break;
		}
		return doll;
	}

	public int doll_item_code(int doll) {
		int item = 0;
		switch (doll) {
		/*
		 * 150 버그베어 151 늑대인간 152 돌골렘 153 크러스트시안 154 에티
		 */
		case 150:
			item = 41248;
			break;
		case 151:
			item = 41250;
			break;
		case 152:
			item = 430000;
			break;
		case 153:
			item = 430002;
			break;
		case 154:
			item = 430004;
			break;

		/*
		 * 155 장로 156 서큐 157 코카 158 인어 159 눈사람
		 */
		case 155:
			item = 430001;
			break;
		case 156:
			item = 41249;
			break;
		case 157:
			item = 430500;
			break;
		case 158:
			item = 500108;
			break;
		case 159:
			item = 500109;
			break;

		/*
		 * 160 서큐퀸 161 흑장로 162 자이언트 163 드레이크
		 */
		case 160:
			item = 500205;
			break;
		case 161:
			item = 500204;
			break;
		case 162:
			item = 500203;
			break;
		case 163:
			item = 60324;
			break;
		case 176:
			item = 500110;
			break;
		/*
		 * 164 싸이클롭스 165 리치
		 */
		case 164:
			item = 500202;
			break;
		case 165:
			item = 5000035;
			break;
		default:
			break;
		}
		return item;
	}

	static final int div1 = 128 * 128 * 128 * 128;
	static final int div2 = 128 * 128 * 128;
	static final int div3 = 128 * 128;
	static final int div4 = 128;

	private static String HexToDex(int data, int digits) {
		String number = Integer.toHexString(data);
		for (int i = number.length(); i < digits; i++)
			number = "0" + number;
		return number;
	}

	private void Chat(L1PcInstance pc, int chatType, int chatcount, byte[] chatdata, String chatText,
			LineageClient clientthread) {
		try {
			if (pc.캐릭명변경) {
				try {
					String chaName = chatText;
					if (pc.getClanid() > 0) {
						pc.sendPackets(new S_SystemMessage("혈맹탈퇴후 캐릭명을 변경할수 있습니다."));
						pc.캐릭명변경 = false;
						return;
					}
					if (!pc.getInventory().checkItem(467009, 1)) { // 있나 체크
						pc.sendPackets(new S_SystemMessage("케릭명 변경 비법서를 소지하셔야 가능합니다."));
						pc.캐릭명변경 = false;
						return;
					}
					for (int i = 0; i < chaName.length(); i++) {
						if (chaName.charAt(i) == 'ㄱ' || chaName.charAt(i) == 'ㄲ' || chaName.charAt(i) == 'ㄴ'
								|| chaName.charAt(i) == 'ㄷ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㄸ' || chaName.charAt(i) == 'ㄹ' || chaName.charAt(i) == 'ㅁ'
								|| chaName.charAt(i) == 'ㅂ' || // 한문자(char)단위로
																// 비교
								chaName.charAt(i) == 'ㅃ' || chaName.charAt(i) == 'ㅅ' || chaName.charAt(i) == 'ㅆ'
								|| chaName.charAt(i) == 'ㅇ' || // 한문자(char)단위로
																// 비교
								chaName.charAt(i) == 'ㅈ' || chaName.charAt(i) == 'ㅉ' || chaName.charAt(i) == 'ㅊ'
								|| chaName.charAt(i) == 'ㅋ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㅌ' || chaName.charAt(i) == 'ㅍ' || chaName.charAt(i) == 'ㅎ'
								|| chaName.charAt(i) == 'ㅛ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㅕ' || chaName.charAt(i) == 'ㅑ' || chaName.charAt(i) == 'ㅐ'
								|| chaName.charAt(i) == 'ㅔ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㅗ' || chaName.charAt(i) == 'ㅓ' || chaName.charAt(i) == 'ㅏ'
								|| chaName.charAt(i) == 'ㅣ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㅠ' || chaName.charAt(i) == 'ㅜ' || chaName.charAt(i) == 'ㅡ'
								|| chaName.charAt(i) == 'ㅒ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㅖ' || chaName.charAt(i) == 'ㅢ' || chaName.charAt(i) == 'ㅟ'
								|| chaName.charAt(i) == 'ㅝ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == 'ㅞ' || chaName.charAt(i) == 'ㅙ' || chaName.charAt(i) == 'ㅚ'
								|| chaName.charAt(i) == 'ㅘ' || // 한문자(char)단위로
																// 비교.
								chaName.charAt(i) == '씹' || chaName.charAt(i) == '좃' || chaName.charAt(i) == '좆'
								|| chaName.charAt(i) == 'ㅤ') {
							pc.sendPackets(new S_SystemMessage("사용할수없는 케릭명입니다."));
							pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
							pc.캐릭명변경 = false;
							return;
						}
					}
					if (chaName.getBytes().length > 12) {
						pc.sendPackets(new S_SystemMessage("이름이 너무 깁니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (chaName.length() == 0) {
						pc.sendPackets(new S_SystemMessage("변경할 케릭명을 입력하세요."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (BadNamesList.getInstance().isBadName(chaName)) {
						pc.sendPackets(new S_SystemMessage("사용할 수 없는 케릭명입니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (isInvalidName(chaName)) {
						pc.sendPackets(new S_SystemMessage("사용할 수 없는 케릭명입니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (CharacterTable.doesCharNameExist(chaName)) {
						pc.sendPackets(new S_SystemMessage("동일한 케릭명이 존재합니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (CharacterTable.RobotNameExist(chaName)) {
						pc.sendPackets(new S_SystemMessage("동일한 케릭명이 존재합니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (CharacterTable.RobotCrownNameExist(chaName)) {
						pc.sendPackets(new S_SystemMessage("동일한 케릭명이 존재합니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (NpcShopSpawnTable.getInstance().getNpc(chaName) || npcshopNameCk(chaName)) {
						pc.sendPackets(new S_SystemMessage("동일한 케릭명이 존재합니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}
					if (CharacterTable.somakname(chaName)) {
						pc.sendPackets(new S_SystemMessage("동일한 케릭명이 존재합니다."));
						pc.sendPackets(new S_SystemMessage("캐릭명 변경 비법서를 다시 클릭후 이용해 주세요."));
						pc.캐릭명변경 = false;
						return;
					}

					pc.getInventory().consumeItem(467009, 1); // 소모

					String oldname = pc.getName();

					chaname(chaName, oldname);

					long sysTime = System.currentTimeMillis();
					logchangename(chaName, oldname, new Timestamp(sysTime));

					pc.sendPackets(new S_SystemMessage(chaName + " 아이디로 변경 하셨습니다."));
					pc.sendPackets(new S_SystemMessage("원할한  이용을 위해 클라이언트가 강제로 종료 됩니다."));

					Thread.sleep(1000);
					clientthread.kick();
				} catch (Exception e) {
				}
				return;
			}
			if (clientthread.AutoCheck) {
				if (chatText.equalsIgnoreCase(clientthread.AutoAnswer)) {
					pc.sendPackets(new S_SystemMessage("자동 방지 답을 성공적으로 입력하였습니다."), true);
					while (pc.isTeleport() || pc.텔대기()) {
						Thread.sleep(100);
					}
					if (pc.getMapId() == 6202 || pc.getMapId() == 2005) {
						if (pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(EARTH_BIND);
						}
					}
					if (pc.getMapId() == 6202) {
						// L1Teleport.teleport(pc, 32778, 32832, (short) 622, 5,
						// true);
						L1Teleport.teleport(pc, 33442, 32797, (short) 4, 5, true);
					}
					if (GMCommands.autocheck_iplist.contains(clientthread.getIp())) {
						GMCommands.autocheck_iplist.remove(clientthread.getIp());
					}
					if (GMCommands.autocheck_accountlist.contains(clientthread.getAccountName())) {
						GMCommands.autocheck_accountlist.remove(clientthread.getAccountName());
					}
				} else {
					if (clientthread.AutoCheckCount++ >= 2) {
						pc.sendPackets(new S_SystemMessage("자동 방지 답을 잘못 입력하였습니다."), true);
						while (pc.isTeleport() || pc.텔대기()) {
							Thread.sleep(100);
						}

						if (!GMCommands.autocheck_Tellist.contains(clientthread.getAccountName())) {
							GMCommands.autocheck_Tellist.add(clientthread.getAccountName());
						}

						L1Teleport.teleport(pc, 32928, 32864, (short) 6202, 5, true);

					} else {
						pc.sendPackets(new S_SystemMessage("자동 방지 답을 잘못 입력하였습니다. 기회는 총3번입니다."), true);
						// pc.sendPackets(new
						// S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						// "자동 방지 : [ "+pc.getNetConnection().AutoQuiz+" ] 답을
						// 채팅창에 입력해주세요."),
						// true);
						// pc.sendPackets(new
						// S_SystemMessage("자동 방지 : [
						// "+pc.getNetConnection().AutoQuiz+" ] 답을 채팅창에
						// 입력해주세요."),
						// true);
						pc.sendPackets(
								new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "자동 방지 : " + pc.getNetConnection().AutoQuiz),
								true);
						pc.sendPackets(new S_SystemMessage("자동 방지 : " + pc.getNetConnection().AutoQuiz), true);
						return;
					}
					/*
					 * if(clientthread.AutoCheckCount >= 2){ clientthread.kick(); return; }
					 * pc.sendPackets(new S_SystemMessage("오토 방지 코드를 잘못 입력하셨습니다."), true);
					 * clientthread.AutoCheckCount++; Random _rnd = new Random(System.nanoTime());
					 * int x = _rnd.nextInt(30); int y = _rnd.nextInt(30); clientthread.AutoAnswer =
					 * ""+(x+y); pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
					 * "오토 방지 코드 [ "+x+" + "+y+" = ? ] 답을 입력해주세요."), true); pc.sendPackets(new
					 * S_SystemMessage("오토 방지 코드 [ "+x+" + " +y +" = ? ] 답을 입력해주세요."), true);
					 */
				}
				clientthread.AutoCheck = false;
				clientthread.AutoCheckCount = 0;
				clientthread.AutoQuiz = "";
				clientthread.AutoAnswer = "";
				return;
			}

			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)
					|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
					|| pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
				return;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) { // 채팅
																								// 금지중
				S_ServerMessage sm = new S_ServerMessage(242);
				pc.sendPackets(sm); // 현재 채팅 금지중입니다.
				sm = null;
				return;
			}

			if (pc.isDeathMatch() && !pc.isGhost() && !pc.isGm()) {
				S_ServerMessage sm = new S_ServerMessage(912);
				pc.sendPackets(sm); // 채팅을 할 수 없습니다.
				sm = null;
				return;
			}

			if (!pc.isGm()) {
				for (String tt : textFilter) {
					int indexof = chatText.indexOf(tt);
					if (indexof != -1) {
						int count = 100;
						while ((indexof = chatText.indexOf(tt)) != -1) {
							if (count-- <= 0)
								break;
							char[] dd = chatText.toCharArray();
							chatText = "";
							for (int i = 0; i < dd.length; i++) {
								if (i >= indexof && i <= (indexof + tt.length() - 1)) {
									chatText = chatText + "  ";
								} else
									chatText = chatText + dd[i];
							}
						}
					}
				}
			}
			switch (chatType) {
			case 0: {
				if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
					return;
				}
				if (chatText.startsWith(".시각")) {
					StringBuilder sb = null;
					sb = new StringBuilder();
					TimeZone kst = TimeZone.getTimeZone("GMT+9");
					Calendar cal = Calendar.getInstance(kst);
					sb.append("[Server Time]" + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 "
							+ cal.get(Calendar.DATE) + "일 " + cal.get(Calendar.HOUR_OF_DAY) + ":"
							+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
					S_SystemMessage sm = new S_SystemMessage(sb.toString());
					pc.sendPackets(sm, true);
					sb = null;
					return;
				}
				// GM커멘드
				if (chatText.startsWith(".") && (pc.getAccessLevel() == Config.GMCODE || pc.getAccessLevel() == 7777)) {
					String cmd = chatText.substring(1);
					GMCommands.getInstance().handleCommands(pc, cmd);
					return;
				}

				if (chatText.startsWith("$")) {
					if (pc.isGm())
						chatWorld(pc, chatdata, chatType, chatcount, chatText);
					else
						chatWorld(pc, chatdata, 12, chatcount, chatText);
					if (!pc.isGm()) {
						pc.checkChatInterval();
					}
					return;
				}

				Gamble(pc, chatText);
				if (chatText.startsWith(".")) { // 유저코멘트
					String cmd = chatText.substring(1);
					if (cmd == null) {
						return;
					}
					UserCommands.getInstance().handleCommands(pc, cmd);
					return;
				}

				if (chatText.startsWith("$")) { // 월드채팅
					if (pc.isGm())
						chatWorld(pc, chatdata, chatType, chatcount, chatText);
					else
						chatWorld(pc, chatdata, 12, chatcount, chatText);
					if (!pc.isGm()) {
						pc.checkChatInterval();
					}
					return;
				}

				/** 텔렉 풀기 **/
				/*
				 * if (chatText.startsWith("119")) { try { L1Teleport.teleport(pc, pc.getX(),
				 * pc.getY(), pc.getMapId(), pc.getMoveState().getHeading(), false); } catch
				 * (Exception exception35) {} }
				 */
				// S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
				// Opcodes.S_SAY, 0);

				// pc.sendPackets(new S_NewCreateItem(chatType, chatdata,
				// chatcount, "",pc));
				// L1PcInstance pc, int type, int chat_type, String chat_text,
				// String target_name
				pc.sendPackets(new S_NewCreateItem(pc, 3, chatType, chatText, ""));
				// pc.sendPackets(new S_NewCreateItem(pc, 4, chatType, chatText,
				// ""));
				S_NewCreateItem s_chatpacket = new S_NewCreateItem(pc, 4, chatType, chatText, "");
				// new S_NewCreateItem(chatType, chatdata, chatcount, pc);
				if (!pc.getExcludingList().contains(pc.getName())) {
					if (pc.getMapId() != 2699) {
						pc.sendPackets(s_chatpacket);
					}
				}
				for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(pc)) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						if (listner.getMapId() == 2699) {
							continue;
						}
						listner.sendPackets(s_chatpacket);
					}
				}
				// 돕펠 처리
				L1MonsterInstance mob = null;
				for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
					if (obj instanceof L1MonsterInstance) {
						mob = (L1MonsterInstance) obj;
						if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
							Broadcaster.broadcastPacket(mob, new S_NpcChatPacket(mob, chatText, 0), true);
						}
					}
				}
				eva.LogChatNormalAppend("[일반]", pc.getName(), chatText);
			}
				break;
			case 2: {
				if (pc.isGhost()) {
					return;
				}
				// S_ChatPacket s_chatpacket = new S_ChatPacket(pc,
				// chatText,Opcodes.S_SAY, 2);
				// S_NewCreateItem chat5 = new S_NewCreateItem(chatType,
				// chatdata, chatcount, pc);
				S_NewCreateItem chat5 = new S_NewCreateItem(pc, 4, chatType, chatText, "");
				if (!pc.getExcludingList().contains(pc.getName())) {
					pc.sendPackets(chat5);
				}
				for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(pc, 50)) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(chat5);
					}
				}
				eva.LogChatNormalAppend("[일반]", pc.getName(), chatText);
				// 돕펠 처리
				L1MonsterInstance mob = null;
				for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
					if (obj instanceof L1MonsterInstance) {
						mob = (L1MonsterInstance) obj;
						if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
							for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(mob, 30)) {
								listner.sendPackets(new S_NpcChatPacket(mob, chatText, 2), true);
							}
						}
					}
				}
			}
				break;
			case 3:
				chatWorld(pc, chatdata, chatType, chatcount, chatText);
				break;
			case 4: {
				if (pc.getClanid() != 0) { // 크란 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					S_NewCreateItem chat4 = new S_NewCreateItem(pc, 4, chatType, chatText, "");
					eva.LogChatClanAppend("[혈맹]", pc.getName(), pc.getClanname(), chatText);
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(chat4);
						}
					}
				}
			}
				break;
			case 11: {
				if (pc.isInParty()) { // 파티중
					// S_NewCreateItem s_chatpacket = new
					// S_NewCreateItem(chatType, chatdata, chatcount, pc);
					S_NewCreateItem s_chatpacket = new S_NewCreateItem(pc, 4, chatType, chatText, "");
					for (L1PcInstance listner : pc.getParty().getMembers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(s_chatpacket);
						}
					}
				}
				eva.PartyChatAppend("[파티]", pc.getName(), chatText);
			}
				break;
			case 12:
				if (pc.isGm())
					chatWorld(pc, chatdata, chatType, chatcount, chatText);
				else
					chatWorld(pc, chatdata, 3, chatcount, chatText);
				break;
			case 13: { // 수호기사 채팅
				if (pc.getClanid() != 0) { // 혈맹 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					int rank = pc.getClanRank();
					if (clan != null && (rank == L1Clan.CLAN_RANK_GUARDIAN || rank == L1Clan.CLAN_RANK_SUBPRINCE
							|| rank == L1Clan.CLAN_RANK_PRINCE)) {
						// S_NewCreateItem chat1 = new S_NewCreateItem(chatType,
						// chatdata, chatcount, pc);
						S_NewCreateItem chat1 = new S_NewCreateItem(pc, 4, chatType, chatText, "");
						for (L1PcInstance listner : clan.getOnlineClanMember()) {
							int listnerRank = listner.getClanRank();
							if (!listner.getExcludingList().contains(pc.getName())
									&& (listnerRank == L1Clan.CLAN_RANK_GUARDIAN || rank == L1Clan.CLAN_RANK_SUBPRINCE
											|| listnerRank == L1Clan.CLAN_RANK_PRINCE)) {
								listner.sendPackets(chat1);
							}
						}
					}
				}
				eva.PartyChatAppend("[연합]", pc.getName(), chatText);
			}
				break;
			case 14: { // 채팅 파티
				if (pc.isInChatParty()) { // 채팅 파티중
					// S_ChatPacket s_chatpacket = new S_ChatPacket(pc,
					// chatText,Opcodes.S_SAY, 14);
					// S_NewCreateItem s_chatpacket = new
					// S_NewCreateItem(chatType, chatdata, chatcount, pc);
					S_NewCreateItem s_chatpacket = new S_NewCreateItem(pc, 4, chatType, chatText, "");
					for (L1PcInstance listner : pc.getChatParty().getMembers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(s_chatpacket);
						}
					}
				}
			}
				break;
			case 15: { // 동맹채팅
				if (pc.getClanid() != 0) { // 혈맹 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

					if (clan != null) {
						Integer allianceids[] = clan.Alliance();
						if (allianceids.length > 0) {
							String TargetClanName = null;
							L1Clan TargegClan = null;

							// S_NewCreateItem s_chatpacket = new
							// S_NewCreateItem(chatType, chatdata, chatcount,
							// pc);
							S_NewCreateItem s_chatpacket = new S_NewCreateItem(pc, 4, chatType, chatText, "");
							for (L1PcInstance listner : clan.getOnlineClanMember()) {
								int AllianceClan = listner.getClanid();
								if (pc.getClanid() == AllianceClan) {
									listner.sendPackets(s_chatpacket);
								}
							} // 자기혈맹 전송용

							for (int j = 0; j < allianceids.length; j++) {
								TargegClan = clan.getAlliance(allianceids[j]);
								if (TargegClan != null) {
									TargetClanName = TargegClan.getClanName();
									if (TargetClanName != null) {
										for (L1PcInstance alliancelistner : TargegClan.getOnlineClanMember()) {
											alliancelistner.sendPackets(s_chatpacket);
										} // 동맹혈맹 전송용
									}
								}

							}
						}

					}
				}
				break;
			}
			case 17:
				if (pc.getClanid() != 0) { // 혈맹 소속중
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					if (clan != null && (pc.isCrown() && pc.getId() == clan.getLeaderId())) {
						// S_ChatPacket s_chatpacket = new
						// S_ChatPacket(pc,chatText, Opcodes.S_MESSAGE, 17);
						// S_NewCreateItem s_chatpacket5 = new
						// S_NewCreateItem(chatType, chatdata, chatcount, pc);
						S_NewCreateItem s_chatpacket5 = new S_NewCreateItem(pc, 4, chatType, chatText, "");
						for (L1PcInstance listner : clan.getOnlineClanMember()) {
							if (!listner.getExcludingList().contains(pc.getName())) {
								listner.sendPackets(s_chatpacket5);
							}
						}
					}
				}
				break;

			}
			if (!pc.isGm()) {
				pc.checkChatInterval();
			}
		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	private void chatWorld(L1PcInstance pc, byte[] chatdata, int chatType, int chatcount, String text) {
		if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
			if (pc.isGm() || L1World.getInstance().isWorldChatElabled()) {
				if (pc.get_food() >= 12) { // 5%겟지?
					S_PacketBox pb = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
					pc.sendPackets(pb, true);
					if (chatType == 3) {
						S_PacketBox pb2 = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
						pc.sendPackets(pb2, true);
						if (pc.isGm()) {
							L1World.getInstance().broadcastPacketToAll(new S_NewCreateItem(pc, 4, chatType, text, ""));
							L1World.getInstance()
									.broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[운영자] " + text));
							// eva.WorldChatAppend("[GM전체공지]", pc.getName(), text);
							return;
						}
						// eva.WorldChatAppend("[전체]", pc.getName(), text);

					} else if (chatType == 12) {
						S_PacketBox pb3 = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
						pc.sendPackets(pb3, true);
						if (pc.isGm()) {
							L1World.getInstance()
									.broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[운영자] " + text));
							return;
						}

					}
					for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							if (listner.isShowTradeChat() && chatType == 12) {
								listner.sendPackets(new S_NewCreateItem(chatType, chatdata, chatcount, pc));
								listner.sendPackets(new S_NewCreateItem(pc, 4, chatType, text, ""));
							} else if (listner.isShowWorldChat() && chatType == 3) {
								listner.sendPackets(new S_NewCreateItem(pc, 4, chatType, text, ""));
							}
						}
					}

				} else {
					S_ServerMessage sm = new S_ServerMessage(462);
					pc.sendPackets(sm, true);
				}
			} else {
				S_ServerMessage sm = new S_ServerMessage(510);
				pc.sendPackets(sm, true);
			}
		} else {
			S_ServerMessage sm = new S_ServerMessage(195, String.valueOf(Config.GLOBAL_CHAT_LEVEL));
			pc.sendPackets(sm, true);
		}
	}

	private void ChatWhisper(L1PcInstance whisperFrom, int chatType, int chatcount, byte[] chatdata, String text,
			String targetName) {
		try {
			// 채팅 금지중의 경우
			if (whisperFrom.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
				S_ServerMessage sm = new S_ServerMessage(242);
				whisperFrom.sendPackets(sm, true);
				return;
			}
			if (whisperFrom.getLevel() < Config.WHISPER_CHAT_LEVEL) {
				S_ServerMessage sm = new S_ServerMessage(404, String.valueOf(Config.WHISPER_CHAT_LEVEL));
				whisperFrom.sendPackets(sm, true);
				return;
			}

			/*
			 * if (!whisperFrom.isGm() && (targetName.compareTo("메티스") == 0)) {
			 * S_SystemMessage sm = new S_SystemMessage( "운영자님께는 귓속말을 할 수 없습니다.");
			 * whisperFrom.sendPackets(sm, true); return; }
			 */

			if (targetName.equalsIgnoreCase("***")) {
				S_SystemMessage sm = new S_SystemMessage("-> (***) " + text);
				whisperFrom.sendPackets(sm, true);
				return;
			}

			L1PcInstance whisperTo = L1World.getInstance().getPlayer(targetName);

			// 월드에 없는 경우
			if (whisperTo == null) {
				L1NpcShopInstance npc = null;
				npc = L1World.getInstance().getNpcShop(targetName);
				if (npc != null) {
					// S_ChatPacket scp = new S_ChatPacket(npc,
					// text,Opcodes.S_MESSAGE, 9);
					S_NewCreateItem scp = new S_NewCreateItem(chatType, chatdata, chatcount, whisperFrom);
					whisperFrom.sendPackets(scp, true);
					// S_SystemMessage sm = new
					// S_SystemMessage("-> ("+targetName+") "+text);
					// whisperFrom.sendPackets(sm); sm.clear(); sm = null;
					return;
				}
				S_ServerMessage sm = new S_ServerMessage(73, targetName);
				whisperFrom.sendPackets(sm, true);
				return;
			}
			// 자기 자신에 대한 wis의 경우
			if (whisperTo.equals(whisperFrom)) {
				return;
			}

			if (whisperTo.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
				S_SystemMessage sm = new S_SystemMessage("채팅금지중인 유저에게 귓속말은 할수 없습니다.");
				whisperFrom.sendPackets(sm, true);
				return;
			}

			if (text.length() > 45) {
				S_SystemMessage sm = new S_SystemMessage("귓말로 보낼 수 있는 글자수를 초과하였습니다.");
				whisperFrom.sendPackets(sm, true);
				return;
			}

			// 차단되고 있는 경우
			if (whisperTo.getExcludingList().contains(whisperFrom.getName())) {
				S_ServerMessage sm = new S_ServerMessage(117, whisperTo.getName());
				whisperFrom.sendPackets(sm, true);
				return;
			}

			if (!whisperTo.isCanWhisper()) {
				S_ServerMessage sm = new S_ServerMessage(205, whisperTo.getName());
				whisperFrom.sendPackets(sm, true);
				return;
			}

			if (whisperTo instanceof L1RobotInstance) {
				// S_ChatPacket scp = new S_ChatPacket(whisperTo,
				// text,Opcodes.S_MESSAGE, 9);
				whisperFrom.sendPackets(
						new S_NewCreateItem(chatType, chatdata, chatcount, whisperTo.getName(), whisperFrom));
				return;
			}
			whisperFrom.sendPackets(new S_NewCreateItem(whisperFrom, 3, chatType, text, whisperTo.getName()));
			whisperTo.sendPackets(new S_NewCreateItem(whisperFrom, 4, chatType, text, whisperTo.getName()));
			eva.LogChatWisperAppend("[귓말]", whisperFrom.getName(), whisperTo.getName(), text, ">");

		} catch (Exception e) {

		} finally {
			clear();
		}
	}

	public int Nexttam(String encobj) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int day = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT day FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // 케릭터
																												// 테이블에서
																												// 군주만
																												// 골라와서
			pstm.setString(1, encobj);
			rs = pstm.executeQuery();
			while (rs.next()) {
				day = rs.getInt("Day");
			}
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return day;
	}

	public int TamCharid(String encobj) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int objid = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT objid FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // 케릭터
																												// 테이블에서
																												// 군주만
																												// 골라와서
			pstm.setString(1, encobj);
			rs = pstm.executeQuery();
			while (rs.next()) {
				objid = rs.getInt("objid");
			}
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return objid;
	}

	public void tamcancle(String objectId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("delete from tam where encobjid = ? order by id asc limit 1");
			pstm.setString(1, objectId);
			pstm.executeUpdate();
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
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

	public void 한문체크(boolean flag) {
		_idcheck = flag;
	}

	private void Gamble(L1PcInstance pc, String chatText) {

		if (pc.Gamble_Somak) { // 소막
			for (int i : GambleInstance.mobArray) {
				L1Npc npck = NpcTable.getInstance().getTemplate(i);
				String name = npck.get_name().replace(" ", "");
				if (name.equalsIgnoreCase(chatText) || npck.get_name().equalsIgnoreCase(chatText)
				/*
				 * || chatText.startsWith(npck.get_name())|| chatText.startsWith(name)
				 */) {
					pc.Gamble_Text = npck.get_name();
				}
			}
		}
	}

	private boolean npcshopNameCk(String name) {
		return NpcTable.getInstance().findNpcShopName(name);
	}

	private void logchangename(String chaName, String oldname, Timestamp datetime) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "INSERT INTO Log_Change_name SET Old_Name=?,New_Name=?, Time=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, oldname);
			pstm.setString(2, chaName);
			pstm.setTimestamp(3, datetime);
			pstm.executeUpdate();
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	Random _Random = new Random(System.nanoTime());

	/** 변경 가능한지 검사한다 시작 **/
	private void chaname(String chaName, String oldname) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET char_name=? WHERE char_name=?");
			pstm.setString(1, chaName);
			pstm.setString(2, oldname);
			pstm.executeUpdate();
		} catch (Exception e) {

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	class Send_createitemList implements Runnable {
		private LineageClient client = null;
		short i = 0;

		public Send_createitemList(LineageClient _client) {
			client = _client;
		}

		@Override
		public void run() {
			try {

				// TODO 자동 생성된 메소드 스텁
				if (client == null /* || client.close */)
					return;

				client.sendPacket(new S_NewCreateItem(S_NewCreateItem_list.제작패킷(i)), true);
				i++;
				if (i > 596) {
					// if(i > 347){
					client.제작템패킷전송중 = false;
					return;
				}
				GeneralThreadPool.getInstance().schedule(this, 2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class Send_DollAlchemyInfo implements Runnable {
		private LineageClient client = null;
		short i = 0;

		public Send_DollAlchemyInfo(LineageClient _client) {
			client = _client;
		}

		@Override
		public void run() {
			if (client == null)
				return;

			String data = S_DollAlchemyInfo.제작패킷(i++);

			if (data == null) {
				client.인형합성패킷전송중 = false;
				return;
			}

			client.sendPacket(new S_NewCreateItem(data), true);
			GeneralThreadPool.getInstance().schedule(this, 2);
		}
	}

	private static boolean isAlphaNumeric(String s) {
		boolean flag = true;
		char ac[] = s.toCharArray();
		int i = 0;
		do {
			if (i >= ac.length) {
				break;
			}
			if (!Character.isLetterOrDigit(ac[i])) {
				flag = false;
				break;
			}
			i++;
		} while (true);
		return flag;
	}

	private static boolean isInvalidName(String name) {
		int numOfNameBytes = 0;
		try {
			numOfNameBytes = name.getBytes("EUC-KR").length;
		} catch (UnsupportedEncodingException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return false;
		}

		if (isAlphaNumeric(name)) {
			return false;
		}
		if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
			return false;
		}

		if (BadNamesList.getInstance().isBadName(name)) {
			return false;
		}
		return true;
	}

//	/** 2016.11.24 MJ 앱센터 시세 **/
//	private void setPrivateShopType0(L1PcInstance pc, int size) {
//		boolean isTrd = true;
//		try {
//			int objid = 0;
//			int price = 0;
//			int count = 0;
//
//			Object[] pets = null;
//			L1PcInventory inv = pc.getInventory();
//			L1ItemInstance item = null;
//			for (int i = 0; i < size; i++) {
//				int tok = readC();
//				if (tok == 0x12 || tok == 0x1A) {
//					readP(1);
//					for (int j = 0; j < 3; j++) {
//						switch (readC()) {
//						case 0x08:
//							objid = readBit();
//							break;
//						case 0x10:
//							price = readBit();
//							break;
//						case 0x18:
//							count = readBit();
//							break;
//						}
//					}
//
//					item = inv.getItem(objid);
//					if (item == null || (!item.isStackable() && count != 1)) {
//						pc.sendPackets(new S_Disconnect());
//						isTrd = false;
//						return;
//					}
//
//					if (count > item.getCount())
//						count = item.getCount();
//
//					if (item.getCount() <= 0 || count <= 0) {
//						isTrd = false;
//						return;
//					}
//					if (item.getBless() >= 128) {
//						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할
//																							// 수 없습니다.
//						isTrd = false;
//						return;
//					}
//					if (!item.getItem().isTradable()) {
//						isTrd = false;
//						pc.sendPackets(new S_ServerMessage(166, item.getItem().getName(), "거래 불가능합니다. "));
//						return;
//					}
//					pets = pc.getPetList().toArray();
//					for (Object petObject : pets) {
//						if (petObject instanceof L1PetInstance) {
//							L1PetInstance pet = (L1PetInstance) petObject;
//							if (item.getId() == pet.getItemObjId()) {
//								isTrd = false;
//								pc.sendPackets(new S_ServerMessage(166, item.getItem().getName(), "거래 불가능합니다. "));
//								return;
//							}
//						}
//					}
//
//					Object[] dollList = pc.getDollList().toArray();
//					for (Object dollObject : dollList) {
//						if (dollObject instanceof L1DollInstance) {
//							L1DollInstance doll = (L1DollInstance) dollObject;
//							if (item.getId() == doll.getItemObjId()) {
//								isTrd = false;
//								pc.sendPackets(new S_ServerMessage(166, item.getItem().getName(), "거래 불가능합니다. "));
//								return;
//							}
//						}
//					}
//
//					if (tok == 0x12)
//						pc.addSellings(MJDShopItem.create(item, count, price, false));
//					else if (tok == 0x1a)
//						pc.addPurchasings(MJDShopItem.create(item, count, price, true));
//				} else
//					break;
//			}
//
//			int l1 = readC();
//			byte[] chat = readByteL(l1);
//			readC();
//			int l2 = readC();
//			String polynum = readS(l2);
//			int poly = 0;
//
//			pc.getNetConnection().getAccount().updateShopOpenCount();
//			pc.sendPackets(new S_PacketBox(S_PacketBox.상점개설횟수, pc.getNetConnection().getAccount().Shop_open_count),
//					true);
//			pc.setShopChat(chat);
//			pc.setPrivateShop(true);
//			pc.sendPackets(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
//			pc.broadcastPacket(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, chat));
//			if (polynum.equalsIgnoreCase("tradezone1"))
//				poly = 11479;
//			else if (polynum.equalsIgnoreCase("tradezone2"))
//				poly = 11483;
//			else if (polynum.equalsIgnoreCase("tradezone3"))
//				poly = 11480;
//			else if (polynum.equalsIgnoreCase("tradezone4"))
//				poly = 11485;
//			else if (polynum.equalsIgnoreCase("tradezone5"))
//				poly = 11482;
//			else if (polynum.equalsIgnoreCase("tradezone6"))
//				poly = 11486;
//			else if (polynum.equalsIgnoreCase("tradezone7"))
//				poly = 11481;
//			else if (polynum.equalsIgnoreCase("tradezone8")) {
//				poly = 11484;
//			}
//
//			if (poly != 0) {
//				pc.getSkillEffectTimerSet().killSkillEffectTimer(67);
//				L1PolyMorph.undoPoly(pc);
//				L1ItemInstance weapon = pc.getWeapon();
//				if (weapon != null)
//					pc.getInventory().setEquipped(weapon, false, false, false);
//				pc.getGfxId().setTempCharGfx(poly);
//				pc.sendPackets(new S_ChangeShape(pc.getId(), poly, pc.getCurrentWeapon()));
//				if ((!pc.isGmInvis()) && (!pc.isInvisble())) {
//					Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc.getId(), poly));
//				}
//				S_CharVisualUpdate charVisual = new S_CharVisualUpdate(pc, 0x46);
//				pc.sendPackets(charVisual);
//				Broadcaster.broadcastPacket(pc, charVisual);
//			}
//			GeneralThreadPool.getInstance().execute(new MJDShopStorage(pc, false));
//
//		} catch (Exception e) {
//			isTrd = false;
//			e.printStackTrace();
//		} finally {
//			if (!isTrd) {
//				pc.disposeShopInfo();
//				pc.setPrivateShop(false);
//				pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
//				pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
//			}
//		}
//	}

	private boolean createNewItem3(L1PcInstance pc, int item_id, int count, int EnchantLevel, int AttrEnchantLevel) {// 봉인템
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
		item.setEnchantLevel(EnchantLevel);
		item.setIdentified(true);
		item.setAttrEnchantLevel(AttrEnchantLevel);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를 손에 넣었습니다.
			return true;
		} else {
			return false;
		}
	}// 추가
//
//	private void setPrivateShopType1(L1PcInstance pc) {
//		pc.disposeShopInfo();
//		pc.setPrivateShop(false);
//		pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
//		pc.broadcastPacket(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
//		L1PolyMorph.undoPoly(pc);
//		GeneralThreadPool.getInstance().execute(new MJDShopStorage(pc, true));
//
//	}
//
//	/** 2016.11.24 MJ 앱센터 시세 **/
	
	private void resetAttendanceTime(Account account, L1PcInstance pc) {
		Calendar cal = Calendar.getInstance(Locale.KOREA);
		if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
			if(account.isAttendanceHome()) {
				account.setAttendanceHomeTime(0);
				account.setAttendanceHome(false);
			}
			if(account.isAttendancePcHome()){
				account.setAttendancePcHomeTime(0);
				account.setAttendancePcHome(false);
			}
			account.updateAttendanceTime();
			account.storeAttendCheck();
			updateAttendanceTime();
			pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.ATTENDANCE_COMPLETE, account, pc.PC방_버프));
		}
	}

	private void getAttendanceHome(L1PcInstance pc, Account account) {
		if (account.isAttendanceHome())
			return;
		account.addAttendanceHomeTime(1);
		if (account.getAttendanceHomeTime() >= 60) {
			for (int i = 0; i < account.getAttendanceHomeBytes().length; i++) {
				if (account.getAttendanceHomeBytes()[i] == 0) {
					account.getAttendanceHomeBytes()[i] = 1;
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.ATTENDANCE_TIMEOVER, i + 1, false));
					break;
				}
			}
			account.storeAttendBytes();
			account.setAttendanceHomeTime(0);
			account.setAttendanceHome(true);
			account.storeAttendCheck();
		}
	}

	private void getAttendancePCRoom(L1PcInstance pc, Account account) {
		if (!pc.PC방_버프)
			return;
		if (account.isAttendancePcHome())
			return;
		account.addAttendancePcHomeTime(1);
		if (account.getAttendancePcHomeTime() >= 60) {
			for (int i = 0; i < account.getAttendancePcBytes().length; i++) {
				if (account.getAttendancePcBytes()[i] == 0) {
					/*System.out.println("완료패킷될 패킷 번호 : " + (i + 1));*/
					account.getAttendancePcBytes()[i] = 1;
					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.ATTENDANCE_TIMEOVER, i + 1, true));
					break;
				}
			}
			account.storeAttendBytes();
			account.setAttendancePcHomeTime(0);
			account.setAttendancePcHome(true);
			account.storeAttendCheck();
		}
	}

	private void updateAttendanceTime() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE accounts SET attendanceHomeTime=0, attendancePcHomeTime=0, attendanceHome=0,attendancePcHome=0");
			pstm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private class AttendanceGiveItem implements Runnable {
		private L1PcInstance _pc;
		private L1ItemInstance _reward_item;
		private AttendanceItem _reward_info;

		public AttendanceGiveItem(L1PcInstance pc, L1ItemInstance reward_item, AttendanceItem reward_info) {
			_pc = pc;
			_reward_item = reward_item;
			_reward_info = reward_info;
		}

		@Override
		public void run() {
			try {
				_pc.getInventory().storeItem(_reward_item);
				_pc.sendPackets(new S_ServerMessage(403, _reward_item.getName() + "(" + _reward_item.getCount() + ")"));

				if (_reward_info._broadcast_item) {
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
							"출석 보상으로 아덴 월드의 어느 용사가 " + _reward_item.getName() + " 를(을) 획득하였습니다."));
				}

				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(_pc, L1SkillId.출석체크, _pc.getId(), _pc.getX(), _pc.getY(), null, 0,
						L1SkillUse.TYPE_GMBUFF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getType() {
		return C_NEW_CREATEITEM;
	}
}
