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
	private static final String[] textFilter = { "�ù�" };

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
	
	/**������̵�*/
	private static final int WorldMapTeleport 	= 829;
	/**������̵�*/
	private static final int ����â��ŷ = 0x03fd;
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
				int totallen = readH();// ��ü����
				��Ŷ��ġ����((byte) 0x10);// ��ġ�̵�
				int chattype = readC();// ä��Ÿ��
				��Ŷ��ġ����((byte) 0x1a);// ��ġ�̵�
				int chatlen = readC();// ä�ñ���
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
					�ѹ�üũ(true);
				}

				if (chattype != 1) {
					Chat(pc, chattype, totallen, chat, chat2, client);
					return;
				}

				��Ŷ��ġ����((byte) 0x2a);// ��ġ�̵�
				int namelen = readC();// �̸�����
				if (namelen != 0) {
					name = readS(namelen);
				}
				ChatWhisper(pc, chattype, totallen, chat, chat2, name);
				/*
				 * } else if (type == 0x032b) { readH(); //�ְ�����Ʈ���� readC(); int sort = readC();
				 * // 0 1 2 (�ܰ�) readC(); int sort2 = readC(); // 1 2 3 (��ư��ġ) if (sort == 0) {
				 * if (sort2 == 1) { // pc.����ġ����(pc, 52, 0.01); L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 10); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(0, true); } else if (sort2 == 2) { // ���̾Ƹ�� if
				 * (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 20); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(0, true); } } else if (sort2 == 3) { //
				 * ��޴پ��̸�� if (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 50); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(0, true); } } } else if (sort == 1) { if
				 * (sort2 == 1) { // pc.����ġ����(pc, 52, 0.01); L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 10); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(1, true); } else if (sort2 == 2) { // ���̾Ƹ�� if
				 * (pc.getInventory().consumeItem(437010, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 20); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(1, true); } } else if (sort2 == 3) { //
				 * ��޴پ��̸�� if (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 50); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(1, true); } } } else if (sort == 2) { if
				 * (sort2 == 1) { // pc.����ġ����(pc, 52, 0.01); L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 10); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(2, true); } else if (sort2 == 2) { // ���̾Ƹ�� if
				 * (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
				 * pc.getInventory().storeItem(41159, 20); pc.sendPackets(new
				 * S_ServerMessage(403, give.getLogName())); pc.sendPackets(new
				 * S_WeekQuest(pc)); pc.setReward(2, true); } } else if (sort2 == 3) { //
				 * ��޴پ��̸�� if (pc.getInventory().consumeItem(40308, 1)) { L1ItemInstance give =
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
						if (pc.getLevel() > 1 && pc.getLevel() <= Config.Ŭ����Ʒ���) {
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
					pc.sendPackets(new S_SystemMessage("�� �������� ����� �� �����ϴ�."), true);
				}
		    } else if (type == FINISH_PLAY_SUPPORT) {
				pc.setAutoPlay(false);
				pc.sendPackets(new S_SupportSystem(pc, S_SupportSystem.SC_FINISH_PLAY_SUPPORT_ACK), true);
//		    } else if (type == AINHASAD_DAILY_POINT) {
//				if(pc.getAinHasad() < 10000000 ) {
//					pc.setAinHasad(Config.�����ϻ�嵥�ϸ�����Ʈ);
//					pc.setAinHasadDP(0);
//					pc.updateAinhasadBonus(pc.getId());
//					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.AINHASAD, pc), true);
//				} else {
//					pc.sendPackets(new S_SystemMessage("\\aH�˸�: �����ϻ�� ��ġ�� 1000���Ͽ����� �̿� �����մϴ�."), true);
//				}
			} else if (type == ����â��ŷ) {
				int Ÿ�� = readH();
				readC();
				long �ð� = read4(read_size());
				L1UserRanking classRank = UserRankingController.getInstance().getClassRank(pc.getType(), pc.getName());
				L1UserRanking rank = UserRankingController.getInstance().getTotalRank(pc.getName());
				if(classRank == null && rank == null){
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.����â��ŷ, classRank,  rank, true));
				}else if(Ÿ�� == 2){
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.����â��ŷ, classRank,  rank, true));
				}else if(Ÿ�� == 6 && UserRankingController.��ŷ���� >= �ð�){
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.����â��ŷ, classRank,  rank, true));
				}else pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.����â��ŷ, classRank,  rank, false));
			} else if (type == 0x87) {
				readH();
				readC();
				int classId = readC();
				ArrayList<L1UserRanking> list = UserRankingController.getInstance().getList(classId);

				if (list.size() > 100) {
					List<L1UserRanking> cutlist = list.subList(0, 100);
					List<L1UserRanking> cutlist2 = list.subList(100, list.size() > 200 ? 200 : list.size());
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.������ŷ, cutlist, classId, 2, 1));
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.������ŷ, cutlist2, classId, 2, 2));
				} else {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.������ŷ, list, classId, 1, 1));
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

					if (excludeType == 1) {// �߰�
						L1ExcludingList exList = pc.getExcludingList();
						L1ExcludingLetterList exletterList = pc.getExcludingLetterList();
						switch (subType) {// �Ϲ� 0 ���� 1
						case 0:
							if (exList.contains(name)) {
								/*
								 * String temp = exList.remove(name); S_PacketBox pb = new
								 * S_PacketBox(S_PacketBox.REM_EXCLUDE, temp, type); pc.sendPackets(pb, true);
								 * ExcludeTable.getInstance().delete(pc.getName( ), name);
								 */
							} else {
								if (exList.isFull()) {
									S_SystemMessage sm = new S_SystemMessage("���ܵ� ����ڰ� �ʹ� �����ϴ�.");
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
									S_SystemMessage sm = new S_SystemMessage("���ܵ� ����ڰ� �ʹ� �����ϴ�.");
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
					} else if (excludeType == 2) {// ����
						L1ExcludingList exList = pc.getExcludingList();
						L1ExcludingLetterList exletterList = pc.getExcludingLetterList();
						switch (subType) {// �Ϲ� 0 ���� 1
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
			} else if (type == 1006) { // �̰� ���������?�ƿ���� ���ǲ����� ����..
				try {
					if (pc == null) {
						return;
					}

					if (pc.hasSkillEffect(L1SkillId.�⼮üũ������)) {
						return;
					}

					pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.�⼮üũ������, 3000);
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
						System.out.println("�ڡ١� �߰��(�⼮üũ) �ǽ� ���� : [" + account.getName() + "] �ڡ١�");
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
						/** �⼮üũ ��� �Ϸ�� �ʱ�ȭ **/
						if (account.isAllHomeAttendCheck()) {
							byte[] b = new byte[42];
							account.setAttendanceHomeBytes(b);
						}

					} else {
						account.getAttendancePcBytes()[objectId - 1] = 2;
						account.updateAttendacePcDate();

						/** �⼮üũ ��� �Ϸ�� �ʱ�ȭ **/
						if (account.isAllPcAttendCheck()) {
							byte[] b = new byte[42];
							account.setAttendancePcBytes(b);
						}
					}

					pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.ATTENDANCE_ITEM_COMPLETE, objectId, size == 0 ? false : true,
							reward_item, reward_info._broadcast_item));

					long delay_time = reward_size >= 2 ? 7000 : 0;
					/**
					 * �귿�ð� ������ ����
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
			} else if (type == 0x013D) { // ���� ���� �� ǥ��
				//
			} else if (type == 0x013F) {// �ҼȾ׼�
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
					// 1��Ʈ 2��� 4��ũ���
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

					if (!player.isCrown()) { // ���� �̿�
						S_ServerMessage sm = new S_ServerMessage(478);
						player.sendPackets(sm, true);
						return;
					}
					if (clanId == 0) { // ũ���̼Ҽ�
						S_ServerMessage sm = new S_ServerMessage(272);
						player.sendPackets(sm, true);
						return;
					}
					L1Clan clan = L1World.getInstance().getClan(clanName);
					if (clan == null) { // ��ũ���� �߰ߵ��� �ʴ´�
						S_SystemMessage sm = new S_SystemMessage("��� ������ �߰ߵ��� �ʾҽ��ϴ�.");
						player.sendPackets(sm, true);
						return;
					}
					if (player.getId() != clan.getLeaderId()) { // ������
						S_ServerMessage sm = new S_ServerMessage(478);
						player.sendPackets(sm, true);
						return;
					}
					if (clanName.toLowerCase().equals(s.toLowerCase())) { // ��ũ����
																			// ����
						S_SystemMessage sm = new S_SystemMessage("�ڽ��� ���� ���� ������ �Ұ����մϴ�.");
						player.sendPackets(sm, true);
						return;
					}
					L1Clan enemyClan = null;
					String enemyClanName = null;
					for (L1Clan checkClan : L1World.getInstance().getAllClans()) { // ũ������
																					// üũ
						if (checkClan.getClanName().toLowerCase().equals(s.toLowerCase())) {
							enemyClan = checkClan;
							enemyClanName = checkClan.getClanName();
							break;
						}
					}
					if (enemyClan == null) { // ��� ũ���� �߰ߵ��� �ʾҴ�
						S_SystemMessage sm = new S_SystemMessage("��� ������ �߰ߵ��� �ʾҽ��ϴ�.");
						player.sendPackets(sm, true);
						return;
					}
					if (clan.getAlliance(enemyClan.getClanId()) == enemyClan) {
						S_ServerMessage sm = new S_ServerMessage(1205);
						player.sendPackets(sm, true);
						return;
					}
					List<L1War> warList = L1World.getInstance().getWarList(); // ����
																				// ����Ʈ��
																				// ���
					if (clan.getCastleId() != 0) { // ��ũ���� ����
						S_ServerMessage sm = new S_ServerMessage(474);
						player.sendPackets(sm, true);
						return;
					}
					if (enemyClan.getCastleId() != 0 && // ��� ũ���� ���ַ�, ��ĳ���Ͱ�
														// Lv25 �̸�
							player.getLevel() < 25) {
						S_ServerMessage sm = new S_ServerMessage(475);
						player.sendPackets(sm, true); // �������� �����Ϸ��� ���� 25�� �̸���
														// ������ �ȵ˴ϴ�.
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
								new S_SystemMessage("�������� ���� �������� " + Config.warmember + " �� �̻� �Ǿ�� ������ �����մϴ�."), true);
						return;
					}

					/*
					 * if (clan.getHouseId() > 0) { S_SystemMessage sm = new S_SystemMessage(
					 * "����Ʈ�� �ִ� ���¿����� ���� ���� �� �� �����ϴ�."); player.sendPackets(sm, true); return; }
					 */
					if (enemyClan.getCastleId() != 0) { // ��� ũ���� ����
						int castle_id = enemyClan.getCastleId();
						if (WarTimeController.getInstance().isNowWar(castle_id)) { // ����
																					// �ð���
							L1PcInstance clanMember[] = clan.getOnlineClanMember();
							for (int k = 0; k < clanMember.length; k++) {
								if (L1CastleLocation.checkInWarArea(castle_id, clanMember[k])) {
									// S_ServerMessage sm = new
									// S_ServerMessage(477);
									// player.sendPackets(sm, true); // ����� ������
									// ��� ���Ϳ��� ���� �ۿ� ������ ������ �������� ������ �� �����ϴ�.
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
								if (war.CheckClanInWar(enemyClanName)) { // ���
																			// ũ����
																			// �̹�
																			// ������
									war.DeclareWar(clanName, enemyClanName);
									war.AddAttackClan(clanName);
									enemyInWar = true;
									break;
								}
							}
							if (!enemyInWar) { // ��� ũ���� ������ �ܷ̿�, ��������
								L1War war = new L1War();
								war.handleCommands(1, clanName, enemyClanName); // ������
																				// ����
							}
						} else { // ���� �ð���
							S_ServerMessage sm = new S_ServerMessage(476);
							player.sendPackets(sm, true); // ���� �������� �ð��� �ƴմϴ�.
						}
					} else { // ��� ũ���� ���ִ� �ƴϴ�
						return;
					}
				} catch (Exception e) {
				}
			} else if (type == 0x0142) {// /���Ͱ���
				/** 2016.11.25 MJ �ۼ��� ���� **/
				int joinType = readC();
				readH();
				int length = readC();

				// �������� �ʴ� �����Դϴ�.
				if (pc.isCrown()) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 4), true);
					return;
				}

				// �̹� ���Ϳ� ������ ���� �Դϴ�.
				if (pc.getClanid() != 0) {
					pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 9), true);
					return;
				}

				// ���ָ� ���� ������ �ּ���.
				try {
					String clanname = new String(readByteL(length), 0, length, "MS949");
					L1Clan clan = L1World.getInstance().getClan(clanname);
					//
					if (clan == null) {
						pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 13), true);
						return;
					}
					L1PcInstance crown = clan.getonline����();
					switch (clan.getJoinType()) {
					case 1:
						if (crown == null) {
							pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_MESSAGE, 11), true);
							return;
						}

						crown.setTempID(pc.getId()); // ����� ������Ʈ ID�� ������ �д�
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
							C_Attr.���Ͱ���(crown, pc, clan);
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

			} else if (type == 0x0146) { // ���� ���Խ�û �ޱ� ����
				if (pc.getClanid() == 0 || (!pc.isCrown() && pc.getClanRank() != L1Clan.CLAN_RANK_GUARDIAN))
					return;
				readC();
				readH();
				int setting = readC();
				readC();
				int setting2 = readC();
				if (setting2 == 2) {
					pc.sendPackets(new S_SystemMessage("���� ��ȣ ���� �������� ������ �� �����ϴ�."), true);
					setting2 = 1;
				}

				pc.getClan().setJoinSetting(setting);
				pc.getClan().setJoinType(setting2);
				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, setting, setting2), true);
				ClanTable.getInstance().updateClan(pc.getClan());
				pc.sendPackets(new S_ServerMessage(3980), true);
			} else if (type == 0x014C) { // ���� ���� ����
				if (pc.getClanid() == 0)
					return;
				pc.sendPackets(new S_NewCreateItem(S_NewCreateItem.CLAN_JOIN_SETTING, pc.getClan().getJoinSetting(),
						pc.getClan().getJoinType()), true);
			} else if (type == 0x0152) { // ǥ�ļ���
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

					L1PcInstance ǥ��pc = null;

					// System.out.println(s);

					for (L1PcInstance player : pc.getParty().getMembers()) {
						// System.out.println(player.encobjid);
						if (s.equals(player.encobjid)) {
							player.setǥ��(subtype[0]);
							ǥ��pc = player;
						}
					}

					if (ǥ��pc != null) {
						for (L1PcInstance player : pc.getParty().getMembers()) {
							player.sendPackets(new S_NewUI(0x53, ǥ��pc));
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (type == 0x01e0) { // Ž �������
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
					pc.sendPackets(new S_SystemMessage("�ش� �ɸ��͸� ��Ҹ� �� �� �ֽ��ϴ�."));
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
			} else if (type == 0x01cc) { // ���� ���� ����
				pc.sendPackets(new S_NewCreateItem(pc.getAccountName(), S_NewCreateItem.TamPage));
			} else if (type == 0x84) { // ������ �ϴ�����

				if (!pc.PC��_����) {
					pc.sendPackets(new S_SystemMessage("PC�� �̿���� ����߿��� ��� ������ �ൿ�Դϴ�."), true);
					return;
				}
				if (pc.getMapId() == 99 || pc.getMapId() == 6202) {
					pc.sendPackets(new S_SystemMessage("������ ���¿����� �����̵��� ����� �� �����ϴ�."), true);
					return;
				}

				/*if (!pc.getMap().isTeleportable()) {
					pc.sendPackets(new S_SystemMessage("������ ���¿����� �����̵��� ����� �� �����ϴ�."), true);
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
				int totallen = readH();// ��ü����
				��Ŷ��ġ����((byte) 0x10);// ��ġ�̵�
				int chattype = readC();// ä��Ÿ��
				��Ŷ��ġ����((byte) 0x1a);// ��ġ�̵�
				int chatlen = readC();// ä�ñ���
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
					�ѹ�üũ(true);
				}

				��Ŷ��ġ����((byte) 0x2a);// ��ġ�̵�
				int namelen = readC();// �̸�����
				if (namelen != 0) {
					name = readS(namelen);
				}
				ChatWhisper(pc, chattype, totallen, chat, chat2, name);
				/*
				 * } else if (type == 0x7a) { // �����ռ�â if (pc.getInventory().calcWeightpercent()
				 * >= 90) { pc.sendPackets(new S_SystemMessage(
				 * "���� �������� �������� �ռ��� ������ �� �����ϴ�.")); return; } if (client.�����ռ���Ŷ������) return;
				 * 
				 * readH();// length readC(); readC(); int unknown = readD();
				 * 
				 * pc.sendPackets(new S_NewCreateItem(0x80, "00 00")); if (unknown != 0 &&
				 * !client.�����ռ���Ŷ����) { client.�����ռ���Ŷ���� = true; if (unknown != -1528525972)
				 * client.�����ռ���Ŷ���� = false; } if (client.�����ռ���Ŷ����) { pc.sendPackets(new
				 * S_NewCreateItem(0x7b, "08 03 00 00")); } else { client.�����ռ���Ŷ������ = true;
				 * client.�����ռ���Ŷ���� = true; GeneralThreadPool.getInstance().schedule( new
				 * Send_DollAlchemyInfo(client), 1); } } else if (type == 0x7c) { // �ռ��Ϸ�â //
				 * pc.sendPackets(new S_SystemMessage("������Ʈ �غ��� �Դϴ�.")); int bytelen =
				 * readH();// ������ readH();
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
				 * "�������� ������θ� �̿��� �ּ���.")); return; }
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
				 * "05"; } int rnd = _Random.nextInt(100) + 1; int suc = 1;// ����
				 * 
				 * boolean ����޼��� = false;
				 * 
				 * // ���� ���� �ܰ� �ƴҶ� ����. // ������ �κ��� ������ ����.
				 * 
				 * Collections.shuffle(_usedoll);
				 * 
				 * int itemid = _usedoll.get(0).getItemId(); int dollid = itemid; L1ItemInstance
				 * sucitem = null;
				 * 
				 * if (item_doll_code(itemid) == 1) { if (_usedoll.size() == 2) { if (rnd < 10)
				 * {// ���� suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// ���� suc
				 * = 0; } } else if (_usedoll.size() == 4) { if (rnd < 50) {// ���� suc = 0; } }
				 * if (suc == 0) { dollid = lv2doll[_Random.nextInt(lv2doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); } } else if
				 * (item_doll_code(itemid) == 2) { if (_usedoll.size() == 2) { if (rnd < 10) {//
				 * ���� suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// ���� suc =
				 * 0; } } else if (_usedoll.size() == 4) { if (rnd < 50) {// ���� suc = 0; } } if
				 * (suc == 0) { dollid = lv3doll[_Random.nextInt(lv3doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); // dollid = 176; } } else if
				 * (item_doll_code(itemid) == 3) { if (_usedoll.size() == 2) { if (rnd < 10) {//
				 * ���� suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// ���� suc =
				 * 0; } } else if (_usedoll.size() == 4) { if (rnd < 40) {// ���� suc = 0; } } if
				 * (suc == 0) { dollid = lv4doll[_Random.nextInt(lv4doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); ����޼��� = true; } } else if
				 * (item_doll_code(itemid) == 4) { if (_usedoll.size() == 2) { if (rnd < 10) {//
				 * ���� suc = 0; } } else if (_usedoll.size() == 3) { if (rnd < 20) {// ���� suc =
				 * 0; } } else if (_usedoll.size() == 4) { if (rnd < 40) {// ���� suc = 0; } } if
				 * (suc == 0) { dollid = lv5doll[_Random.nextInt(lv5doll.length)]; sucitem =
				 * ItemTable.getInstance() .createItem(dollid); ����޼��� = true; } }
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
				 * if (����޼���) { L1World.getInstance().broadcastPacketToAll( new
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
			} else if (type == 0x01e4) { // ĳ���� ����
				try {
					int length = readH();// ����
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
							break;// ��
						case 0x10:
							classtype = b[1] & 0xff;
							break;// Ŭ���� Ÿ��
						case 0x18:
							status = b[1] & 0xff;
							break;// �ʱ���� = 1 / ���Ⱥ������ = 8

						case 0x20:
							unknown2 = b[1] & 0xff;
							break;// ��
						case 0x28:
							unknown3 = b[1] & 0xff;
							break;// ��

						case 0x30:
							str = b[1] & 0xff;
							break;// ��
						case 0x38:
							inte = b[1] & 0xff;
							break;// ��Ʈ
						case 0x40:
							wis = b[1] & 0xff;
							break;// ����
						case 0x48:
							dex = b[1] & 0xff;
							break;// ��
						case 0x50:
							con = b[1] & 0xff;
							break;// ��
						case 0x58:
							cha = b[1] & 0xff;
							break;// ī��

						default:
							int i = 0;
							try {
								i = b[0] & 0xff;
							} catch (Exception e) {
							}
							System.out.println("[���Ȱ��� ���ǵ��� ���� ��Ŷ] op : " + i);
							break;
						}
					}

					if (str != 0 && unknown3 != 1) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, str, con, "��", classtype, null));
					}
					if (dex != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, dex, 0, "��", classtype, null));
					}
					if (con != 0 && unknown3 != 16) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, con, str, "��", classtype, null));
					}
					if (inte != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, inte, 0, "��Ʈ", classtype, null));
					}
					if (wis != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, wis, 0, "����", classtype, null));
					}
					if (cha != 0) {
						client.sendPacket(new S_NewCreateItem(0x01e3, status, cha, 0, "ī��", classtype, null));
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
		 * 41248 ���׺��� 41250 �����ΰ� 430000 ���� 430002 ũ����Ʈ�þ� 430004 ��Ƽ
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
			break;// ��
		/*
		 * 430001 ��� 41249 ��ť 430500 ��ī 500108 �ξ� 500109 �����
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
			break;// ��ٰ�
		/*
		 * 500205 ��ť�� 500204 ����� 500203 ���̾�Ʈ 60324 �巹��ũ
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
			break;// ���̾ư�
		/*
		 * 500202 ����Ŭ�ӽ� 5000035 ��ġ
		 */
		case 500202:
			doll = 4;
			break;
		case 5000035:
			doll = 4;
			break;
		case 600244:
			doll = 4;
			break;// �þ�
		case 600245:
			doll = 4;
			break;// ����
		case 142920:
			doll = 4;
			break;// ���̸���
		case 142921:
			doll = 4;
			break;// ����
		case 751:
			doll = 4;
			break;// �ӹ̷ε�
		case 600246:
			doll = 5;
			break;// ����
		case 600247:
			doll = 5;
			break;// ����
		case 142922:
			doll = 5;
			break;// �ٶ�ī
		case 752:
			doll = 5;
			break;// Ÿ��
		case 753:
			doll = 5;
			break;// ������Ʈ
		case 754:
			doll = 5;
			break;// ��������
		case 755:
			doll = 5;
			break;// Ŀ��
		default:
			break;
		}
		return doll;
	}

	public int doll_item_code(int doll) {
		int item = 0;
		switch (doll) {
		/*
		 * 150 ���׺��� 151 �����ΰ� 152 ���� 153 ũ����Ʈ�þ� 154 ��Ƽ
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
		 * 155 ��� 156 ��ť 157 ��ī 158 �ξ� 159 �����
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
		 * 160 ��ť�� 161 ����� 162 ���̾�Ʈ 163 �巹��ũ
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
		 * 164 ����Ŭ�ӽ� 165 ��ġ
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
			if (pc.ĳ������) {
				try {
					String chaName = chatText;
					if (pc.getClanid() > 0) {
						pc.sendPackets(new S_SystemMessage("����Ż���� ĳ������ �����Ҽ� �ֽ��ϴ�."));
						pc.ĳ������ = false;
						return;
					}
					if (!pc.getInventory().checkItem(467009, 1)) { // �ֳ� üũ
						pc.sendPackets(new S_SystemMessage("�ɸ��� ���� ������� �����ϼž� �����մϴ�."));
						pc.ĳ������ = false;
						return;
					}
					for (int i = 0; i < chaName.length(); i++) {
						if (chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��' || // �ѹ���(char)������
																// ��.
								chaName.charAt(i) == '��' || chaName.charAt(i) == '��' || chaName.charAt(i) == '��'
								|| chaName.charAt(i) == '��') {
							pc.sendPackets(new S_SystemMessage("����Ҽ����� �ɸ����Դϴ�."));
							pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
							pc.ĳ������ = false;
							return;
						}
					}
					if (chaName.getBytes().length > 12) {
						pc.sendPackets(new S_SystemMessage("�̸��� �ʹ� ��ϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (chaName.length() == 0) {
						pc.sendPackets(new S_SystemMessage("������ �ɸ����� �Է��ϼ���."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (BadNamesList.getInstance().isBadName(chaName)) {
						pc.sendPackets(new S_SystemMessage("����� �� ���� �ɸ����Դϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (isInvalidName(chaName)) {
						pc.sendPackets(new S_SystemMessage("����� �� ���� �ɸ����Դϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (CharacterTable.doesCharNameExist(chaName)) {
						pc.sendPackets(new S_SystemMessage("������ �ɸ����� �����մϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (CharacterTable.RobotNameExist(chaName)) {
						pc.sendPackets(new S_SystemMessage("������ �ɸ����� �����մϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (CharacterTable.RobotCrownNameExist(chaName)) {
						pc.sendPackets(new S_SystemMessage("������ �ɸ����� �����մϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (NpcShopSpawnTable.getInstance().getNpc(chaName) || npcshopNameCk(chaName)) {
						pc.sendPackets(new S_SystemMessage("������ �ɸ����� �����մϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}
					if (CharacterTable.somakname(chaName)) {
						pc.sendPackets(new S_SystemMessage("������ �ɸ����� �����մϴ�."));
						pc.sendPackets(new S_SystemMessage("ĳ���� ���� ������� �ٽ� Ŭ���� �̿��� �ּ���."));
						pc.ĳ������ = false;
						return;
					}

					pc.getInventory().consumeItem(467009, 1); // �Ҹ�

					String oldname = pc.getName();

					chaname(chaName, oldname);

					long sysTime = System.currentTimeMillis();
					logchangename(chaName, oldname, new Timestamp(sysTime));

					pc.sendPackets(new S_SystemMessage(chaName + " ���̵�� ���� �ϼ̽��ϴ�."));
					pc.sendPackets(new S_SystemMessage("������  �̿��� ���� Ŭ���̾�Ʈ�� ������ ���� �˴ϴ�."));

					Thread.sleep(1000);
					clientthread.kick();
				} catch (Exception e) {
				}
				return;
			}
			if (clientthread.AutoCheck) {
				if (chatText.equalsIgnoreCase(clientthread.AutoAnswer)) {
					pc.sendPackets(new S_SystemMessage("�ڵ� ���� ���� ���������� �Է��Ͽ����ϴ�."), true);
					while (pc.isTeleport() || pc.�ڴ��()) {
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
						pc.sendPackets(new S_SystemMessage("�ڵ� ���� ���� �߸� �Է��Ͽ����ϴ�."), true);
						while (pc.isTeleport() || pc.�ڴ��()) {
							Thread.sleep(100);
						}

						if (!GMCommands.autocheck_Tellist.contains(clientthread.getAccountName())) {
							GMCommands.autocheck_Tellist.add(clientthread.getAccountName());
						}

						L1Teleport.teleport(pc, 32928, 32864, (short) 6202, 5, true);

					} else {
						pc.sendPackets(new S_SystemMessage("�ڵ� ���� ���� �߸� �Է��Ͽ����ϴ�. ��ȸ�� ��3���Դϴ�."), true);
						// pc.sendPackets(new
						// S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						// "�ڵ� ���� : [ "+pc.getNetConnection().AutoQuiz+" ] ����
						// ä��â�� �Է����ּ���."),
						// true);
						// pc.sendPackets(new
						// S_SystemMessage("�ڵ� ���� : [
						// "+pc.getNetConnection().AutoQuiz+" ] ���� ä��â��
						// �Է����ּ���."),
						// true);
						pc.sendPackets(
								new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�ڵ� ���� : " + pc.getNetConnection().AutoQuiz),
								true);
						pc.sendPackets(new S_SystemMessage("�ڵ� ���� : " + pc.getNetConnection().AutoQuiz), true);
						return;
					}
					/*
					 * if(clientthread.AutoCheckCount >= 2){ clientthread.kick(); return; }
					 * pc.sendPackets(new S_SystemMessage("���� ���� �ڵ带 �߸� �Է��ϼ̽��ϴ�."), true);
					 * clientthread.AutoCheckCount++; Random _rnd = new Random(System.nanoTime());
					 * int x = _rnd.nextInt(30); int y = _rnd.nextInt(30); clientthread.AutoAnswer =
					 * ""+(x+y); pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
					 * "���� ���� �ڵ� [ "+x+" + "+y+" = ? ] ���� �Է����ּ���."), true); pc.sendPackets(new
					 * S_SystemMessage("���� ���� �ڵ� [ "+x+" + " +y +" = ? ] ���� �Է����ּ���."), true);
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
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) { // ä��
																								// ������
				S_ServerMessage sm = new S_ServerMessage(242);
				pc.sendPackets(sm); // ���� ä�� �������Դϴ�.
				sm = null;
				return;
			}

			if (pc.isDeathMatch() && !pc.isGhost() && !pc.isGm()) {
				S_ServerMessage sm = new S_ServerMessage(912);
				pc.sendPackets(sm); // ä���� �� �� �����ϴ�.
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
				if (chatText.startsWith(".�ð�")) {
					StringBuilder sb = null;
					sb = new StringBuilder();
					TimeZone kst = TimeZone.getTimeZone("GMT+9");
					Calendar cal = Calendar.getInstance(kst);
					sb.append("[Server Time]" + cal.get(Calendar.YEAR) + "�� " + (cal.get(Calendar.MONTH) + 1) + "�� "
							+ cal.get(Calendar.DATE) + "�� " + cal.get(Calendar.HOUR_OF_DAY) + ":"
							+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
					S_SystemMessage sm = new S_SystemMessage(sb.toString());
					pc.sendPackets(sm, true);
					sb = null;
					return;
				}
				// GMĿ���
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
				if (chatText.startsWith(".")) { // �����ڸ�Ʈ
					String cmd = chatText.substring(1);
					if (cmd == null) {
						return;
					}
					UserCommands.getInstance().handleCommands(pc, cmd);
					return;
				}

				if (chatText.startsWith("$")) { // ����ä��
					if (pc.isGm())
						chatWorld(pc, chatdata, chatType, chatcount, chatText);
					else
						chatWorld(pc, chatdata, 12, chatcount, chatText);
					if (!pc.isGm()) {
						pc.checkChatInterval();
					}
					return;
				}

				/** �ڷ� Ǯ�� **/
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
				// ���� ó��
				L1MonsterInstance mob = null;
				for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
					if (obj instanceof L1MonsterInstance) {
						mob = (L1MonsterInstance) obj;
						if (mob.getNpcTemplate().is_doppel() && mob.getName().equals(pc.getName())) {
							Broadcaster.broadcastPacket(mob, new S_NpcChatPacket(mob, chatText, 0), true);
						}
					}
				}
				eva.LogChatNormalAppend("[�Ϲ�]", pc.getName(), chatText);
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
				eva.LogChatNormalAppend("[�Ϲ�]", pc.getName(), chatText);
				// ���� ó��
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
				if (pc.getClanid() != 0) { // ũ�� �Ҽ���
					L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					S_NewCreateItem chat4 = new S_NewCreateItem(pc, 4, chatType, chatText, "");
					eva.LogChatClanAppend("[����]", pc.getName(), pc.getClanname(), chatText);
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(chat4);
						}
					}
				}
			}
				break;
			case 11: {
				if (pc.isInParty()) { // ��Ƽ��
					// S_NewCreateItem s_chatpacket = new
					// S_NewCreateItem(chatType, chatdata, chatcount, pc);
					S_NewCreateItem s_chatpacket = new S_NewCreateItem(pc, 4, chatType, chatText, "");
					for (L1PcInstance listner : pc.getParty().getMembers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(s_chatpacket);
						}
					}
				}
				eva.PartyChatAppend("[��Ƽ]", pc.getName(), chatText);
			}
				break;
			case 12:
				if (pc.isGm())
					chatWorld(pc, chatdata, chatType, chatcount, chatText);
				else
					chatWorld(pc, chatdata, 3, chatcount, chatText);
				break;
			case 13: { // ��ȣ��� ä��
				if (pc.getClanid() != 0) { // ���� �Ҽ���
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
				eva.PartyChatAppend("[����]", pc.getName(), chatText);
			}
				break;
			case 14: { // ä�� ��Ƽ
				if (pc.isInChatParty()) { // ä�� ��Ƽ��
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
			case 15: { // ����ä��
				if (pc.getClanid() != 0) { // ���� �Ҽ���
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
							} // �ڱ����� ���ۿ�

							for (int j = 0; j < allianceids.length; j++) {
								TargegClan = clan.getAlliance(allianceids[j]);
								if (TargegClan != null) {
									TargetClanName = TargegClan.getClanName();
									if (TargetClanName != null) {
										for (L1PcInstance alliancelistner : TargegClan.getOnlineClanMember()) {
											alliancelistner.sendPackets(s_chatpacket);
										} // �������� ���ۿ�
									}
								}

							}
						}

					}
				}
				break;
			}
			case 17:
				if (pc.getClanid() != 0) { // ���� �Ҽ���
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
				if (pc.get_food() >= 12) { // 5%����?
					S_PacketBox pb = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
					pc.sendPackets(pb, true);
					if (chatType == 3) {
						S_PacketBox pb2 = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
						pc.sendPackets(pb2, true);
						if (pc.isGm()) {
							L1World.getInstance().broadcastPacketToAll(new S_NewCreateItem(pc, 4, chatType, text, ""));
							L1World.getInstance()
									.broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[���] " + text));
							// eva.WorldChatAppend("[GM��ü����]", pc.getName(), text);
							return;
						}
						// eva.WorldChatAppend("[��ü]", pc.getName(), text);

					} else if (chatType == 12) {
						S_PacketBox pb3 = new S_PacketBox(S_PacketBox.FOOD, pc.get_food());
						pc.sendPackets(pb3, true);
						if (pc.isGm()) {
							L1World.getInstance()
									.broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "[���] " + text));
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
			// ä�� �������� ���
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
			 * if (!whisperFrom.isGm() && (targetName.compareTo("��Ƽ��") == 0)) {
			 * S_SystemMessage sm = new S_SystemMessage( "��ڴԲ��� �ӼӸ��� �� �� �����ϴ�.");
			 * whisperFrom.sendPackets(sm, true); return; }
			 */

			if (targetName.equalsIgnoreCase("***")) {
				S_SystemMessage sm = new S_SystemMessage("-> (***) " + text);
				whisperFrom.sendPackets(sm, true);
				return;
			}

			L1PcInstance whisperTo = L1World.getInstance().getPlayer(targetName);

			// ���忡 ���� ���
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
			// �ڱ� �ڽſ� ���� wis�� ���
			if (whisperTo.equals(whisperFrom)) {
				return;
			}

			if (whisperTo.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED)) {
				S_SystemMessage sm = new S_SystemMessage("ä�ñ������� �������� �ӼӸ��� �Ҽ� �����ϴ�.");
				whisperFrom.sendPackets(sm, true);
				return;
			}

			if (text.length() > 45) {
				S_SystemMessage sm = new S_SystemMessage("�Ӹ��� ���� �� �ִ� ���ڼ��� �ʰ��Ͽ����ϴ�.");
				whisperFrom.sendPackets(sm, true);
				return;
			}

			// ���ܵǰ� �ִ� ���
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
			eva.LogChatWisperAppend("[�Ӹ�]", whisperFrom.getName(), whisperTo.getName(), text, ">");

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
			pstm = con.prepareStatement("SELECT day FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // �ɸ���
																												// ���̺���
																												// ���ָ�
																												// ���ͼ�
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
			pstm = con.prepareStatement("SELECT objid FROM `tam` WHERE encobjid = ? order by id asc limit 1"); // �ɸ���
																												// ���̺���
																												// ���ָ�
																												// ���ͼ�
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

	public void �ѹ�üũ(boolean flag) {
		_idcheck = flag;
	}

	private void Gamble(L1PcInstance pc, String chatText) {

		if (pc.Gamble_Somak) { // �Ҹ�
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

	/** ���� �������� �˻��Ѵ� ���� **/
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

				// TODO �ڵ� ������ �޼ҵ� ����
				if (client == null /* || client.close */)
					return;

				client.sendPacket(new S_NewCreateItem(S_NewCreateItem_list.������Ŷ(i)), true);
				i++;
				if (i > 596) {
					// if(i > 347){
					client.��������Ŷ������ = false;
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

			String data = S_DollAlchemyInfo.������Ŷ(i++);

			if (data == null) {
				client.�����ռ���Ŷ������ = false;
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

//	/** 2016.11.24 MJ �ۼ��� �ü� **/
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
//						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0�� �����ų� �Ǵ� Ÿ�ο��� ������ ��
//																							// �� �����ϴ�.
//						isTrd = false;
//						return;
//					}
//					if (!item.getItem().isTradable()) {
//						isTrd = false;
//						pc.sendPackets(new S_ServerMessage(166, item.getItem().getName(), "�ŷ� �Ұ����մϴ�. "));
//						return;
//					}
//					pets = pc.getPetList().toArray();
//					for (Object petObject : pets) {
//						if (petObject instanceof L1PetInstance) {
//							L1PetInstance pet = (L1PetInstance) petObject;
//							if (item.getId() == pet.getItemObjId()) {
//								isTrd = false;
//								pc.sendPackets(new S_ServerMessage(166, item.getItem().getName(), "�ŷ� �Ұ����մϴ�. "));
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
//								pc.sendPackets(new S_ServerMessage(166, item.getItem().getName(), "�ŷ� �Ұ����մϴ�. "));
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
//			pc.sendPackets(new S_PacketBox(S_PacketBox.��������Ƚ��, pc.getNetConnection().getAccount().Shop_open_count),
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

	private boolean createNewItem3(L1PcInstance pc, int item_id, int count, int EnchantLevel, int AttrEnchantLevel) {// ������
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
		item.setEnchantLevel(EnchantLevel);
		item.setIdentified(true);
		item.setAttrEnchantLevel(AttrEnchantLevel);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // ���� �� ���� ���� ���鿡 ����߸��� ó���� ĵ���� ���� �ʴ´�(���� ����)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0�� �տ� �־����ϴ�.
			return true;
		} else {
			return false;
		}
	}// �߰�
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
//	/** 2016.11.24 MJ �ۼ��� �ü� **/
	
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
			pc.sendPackets(new S_ACTION_UI(S_ACTION_UI.ATTENDANCE_COMPLETE, account, pc.PC��_����));
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
		if (!pc.PC��_����)
			return;
		if (account.isAttendancePcHome())
			return;
		account.addAttendancePcHomeTime(1);
		if (account.getAttendancePcHomeTime() >= 60) {
			for (int i = 0; i < account.getAttendancePcBytes().length; i++) {
				if (account.getAttendancePcBytes()[i] == 0) {
					/*System.out.println("�Ϸ���Ŷ�� ��Ŷ ��ȣ : " + (i + 1));*/
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
							"�⼮ �������� �Ƶ� ������ ��� ��簡 " + _reward_item.getName() + " ��(��) ȹ���Ͽ����ϴ�."));
				}

				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(_pc, L1SkillId.�⼮üũ, _pc.getId(), _pc.getX(), _pc.getY(), null, 0,
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
