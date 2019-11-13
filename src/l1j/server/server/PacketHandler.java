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

package l1j.server.server;

import static l1j.server.server.Opcodes.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
/*import l1j.server.server.clientpackets.C_NpcCraft;*/
import l1j.server.server.clientpackets.*;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharPass;
import l1j.server.server.serverpackets.S_Notice;
import server.Authorization;
import server.LineageClient;
//import static l1j.server.server.Opcodes.C_OPCODE_FISHCANCEL; //새로 선언해 줍니당.

//Referenced classes of package l1j.server.server:
//Opcodes, LoginController, ClientThread, Logins

public class PacketHandler {

	private static Logger _log = Logger
			.getLogger(PacketHandler.class.getName());

	public PacketHandler(LineageClient clientthread) {
		_client = clientthread;
	}

	private long createCharTime = 0;
	private long 액션시간 = -1;

	@SuppressWarnings("unused")
	public void handlePacket(byte abyte0[], L1PcInstance object)
			throws Exception {

		if (abyte0 == null || abyte0.length == 0)
			return;
		long now = System.currentTimeMillis();
		int i = 0;
		try {
			i = abyte0[0] & 0xff;
			if (Config.클라패킷출력) {
			System.out.println("c opcode : " + i);
			System.out.println("[C opocde] = " + i + "[Length] = " + abyte0.length);//본섭옵코추출
			System.out.println(DataToPacket(abyte0, abyte0.length));
			}
		} catch (Exception e) {
		}
		try {
			if (object != null) {
				if (object.window_active_time != -1) {
					if (object.window_active_time <= now) {
						object.window_noactive_count++;
						if (object.window_noactive_count >= 3) {
							object.window_noactive_count = 0;
							object.window_active_time = -1;
						}
					} else {
						object.window_noactive_count = 0;
					}
				} else {
				}
			}
			switch (i) {
			case C_EXTENDED_PROTOBUF:  //종합패킷 
				new C_ActionUi(abyte0, _client);
				new C_Craft(abyte0, _client);
				new C_NewCreateItem(abyte0, _client);
				new C_AuthLogin(abyte0, _client, true);
				if (object != null && object.getLevel() <= Config.클라우디아레벨) {
					new C_TalkIslandQuest(abyte0, _client);
				}
				break;
			case C_EXTENDED:
				new C_Adenshop(abyte0, _client);
				break;
			case C_READ_NOTICE:
				Authorization.getInstance().sendNotice(_client);
				break;
			case C_PLEDGE_WATCH:
				C_ClanMarkSee cms = new C_ClanMarkSee(abyte0, _client);
				;
				break;
			case C_SHUTDOWN:
				C_ClanNotice clannotice = new C_ClanNotice(abyte0, _client);
				break;
			case C_ATTACK_CONTINUE:
				C_AutoAttack autoattack = new C_AutoAttack(abyte0, _client);
				break;
			// case C_OPCODE_혈맹매칭: C_혈맹매칭 cm = new C_혈맹매칭(abyte0,
			// _client);break;
			case C_RETURN_SUMMON:
			case C_GOTO_PORTAL:
				C_SabuTeleport st = new C_SabuTeleport(abyte0, _client);
				break;
			case C_EXCLUDE:
				C_Exclude ex = new C_Exclude(abyte0, _client);
				break;
			case C_SAVEIO:
				C_CharcterConfig cc = new C_CharcterConfig(abyte0, _client);
				break;
			case C_OPEN:
				C_Door d = new C_Door(abyte0, _client);
				break;
			case C_TITLE:
				C_Title ct = new C_Title(abyte0, _client);
				break;
			case C_BOARD_DELETE:
				C_BoardDelete cbd = new C_BoardDelete(abyte0, _client);
				break;
			case C_WHO_PLEDGE:
				C_Pledge cp = new C_Pledge(abyte0, _client);
				break;
			case C_CHANGE_DIRECTION:
				C_ChangeHeading ch = new C_ChangeHeading(abyte0, _client);
				break;
			case C_HACTION:
				if (now < 액션시간) {
					return;
				}
				액션시간 = now + 400;
				C_NPCAction na = new C_NPCAction(abyte0, _client);
				break;
			case C_USE_SPELL:
				C_UseSkill us = new C_UseSkill(abyte0, _client);
				break;
			case C_UPLOAD_EMBLEM:
				if (_client.getActiveChar() == null
						|| !_client.getActiveChar().isCrown())
					return;
				C_Emblem em = new C_Emblem(abyte0, _client);
				break;
			case C_CANCEL_XCHG:
				C_TradeCancel tc = new C_TradeCancel(abyte0, _client);
				break;
			// case C_OPCODE_WARTIMELIST: C_WarTimeList wtl = new
			// C_WarTimeList(abyte0, _client);break;
			case C_BOOKMARK:
				C_AddBookmark ab = new C_AddBookmark(abyte0, _client);
				break;
			case C_CREATE_PLEDGE:
				C_CreateClan ccc = new C_CreateClan(abyte0, _client);
				break;
//			case C_VERSION:  // 170106 부터 없어짐 
//				C_ServerVersion csv = new C_ServerVersion(abyte0, _client);
//				break;
			case C_MARRIAGE:
				C_Propose cp2 = new C_Propose(abyte0, _client);
				break;
			case C_BUYABLE_SPELL:
				C_SkillBuy csb = new C_SkillBuy(abyte0, _client);
				break;
			case C_BOARD_LIST:
				C_BoardBack cbb = new C_BoardBack(abyte0, _client);
				break;
			case C_PERSONAL_SHOP:
				C_Shop csop = new C_Shop(abyte0, _client);
				break;
			case C_BOARD_READ:
				C_BoardRead cbr = new C_BoardRead(abyte0, _client);
				break;
			case C_ASK_XCHG:
				C_Trade ctra = new C_Trade(abyte0, _client);
				break;
			case C_DELETE_CHARACTER:
				if(Config.캐릭터비번사용여부 == true){
					if (!_client.getAccount().iscpwok()) {
						if (_client.getAccount().getCPW() == null) {
							_client.sendPacket(new S_CharPass(S_CharPass._비번생성창));
							return;
						} else {
							_client.getAccount().setwaitpacket(abyte0);
							_client.sendPacket(new S_CharPass(S_CharPass._비번입력창));
							return;
						}
					}
				}
				if (_client.getActiveChar() == null && createCharTime < System.currentTimeMillis()) {
					new C_DeleteChar(abyte0, _client);
					createCharTime = System.currentTimeMillis() + 2000;
				}
				break;
			case C_CREATE_CUSTOM_CHARACTER:
				if(Config.캐릭터비번사용여부 == true){
					if (!_client.getAccount().iscpwok()) {
						if (_client.getAccount().getCPW() == null) {
							_client.sendPacket(new S_CharPass(S_CharPass._비번생성창));
							return;
						} else {
							_client.getAccount().setwaitpacket(abyte0);
							_client.sendPacket(new S_CharPass(S_CharPass._비번입력창));
							return;
						}
					}
				}
				if (createCharTime < System.currentTimeMillis()) {
					C_CreateNewCharacter cnc = new C_CreateNewCharacter(abyte0,
							_client);
					cnc = null;
					createCharTime = System.currentTimeMillis() + 2000;
				}
				break;
			case C_ALIVE:
				C_KeepALIVE kal = new C_KeepALIVE(abyte0, _client);
				break;

			case C_ANSWER:
				C_Attr att = new C_Attr(abyte0, _client);
				break;

			/*
			 * case C_LOGIN: C_AuthLogin cal = new C_AuthLogin(abyte0, _client); break;
			 */
			case C_BUY_SELL:
				new C_ShopAndWarehouse(abyte0, _client);
				break;
			case C_DEPOSIT:
				C_Deposit cd = new C_Deposit(abyte0, _client);
				break;
			case C_WITHDRAW:
				C_Drawal cdwal = new C_Drawal(abyte0, _client);
				break;
			case C_ONOFF:
				C_LoginToServerOK clts = new C_LoginToServerOK(abyte0, _client);
				clts = null;
				break;
			case C_BUY_SPELL:
				C_SkillBuyOK csbok = new C_SkillBuyOK(abyte0, _client);
				csbok = null;
				break;
			case C_ADD_XCHG:
				C_TradeAddItem tai = new C_TradeAddItem(abyte0, _client);
				tai = null;
				break;
			case C_ADD_BUDDY:
				C_AddBuddy abdd = new C_AddBuddy(abyte0, _client);
				abdd = null;
				break;
			case C_LOGOUT:
				C_ReturnToLogin rtl = new C_ReturnToLogin(abyte0, _client);
				rtl = null;
				break;
			case C_SAY:
				// C_Macro chat = new C_Macro(abyte0, _client);
				// chat = null;
				// break;
			case C_ACCEPT_XCHG:
				C_TradeOK tok = new C_TradeOK(abyte0, _client);
				tok = null;
				break;
			case C_CHECK_PK:
				C_CheckPK ccpk = new C_CheckPK(abyte0, _client);
				ccpk = null;
				break;
			case C_TAX:
				C_TaxRate ctr = new C_TaxRate(abyte0, _client);
				ctr = null;
				break;
			case C_DEAD_RESTART:
				C_RestartAfterDie cra = new C_RestartAfterDie(abyte0, _client);
				cra = null;
				break;
			case C_RESTART:
				C_Restart crt = new C_Restart(abyte0, _client);
				crt = null;/* new C_NoticeClick(_client); */
				break;
			case C_QUERY_BUDDY:
				C_Buddy cb = new C_Buddy(abyte0, _client);
				cb = null;
				break;
			case C_DROP:
				C_DropItem cdu = new C_DropItem(abyte0, _client);
				cdu = null;
				break;
			case C_LEAVE_PARTY:
				C_LeaveParty clp = new C_LeaveParty(abyte0, _client);
				clp = null;
				break;
			case C_ATTACK:
			case C_FAR_ATTACK:
				C_Attack attc = new C_Attack(abyte0, _client);
				attc = null;
				break;
			// 캐릭터의 쇼트 컷이나 목록 상태가 플레이중에 변동했을 경우에
			// 쇼트 컷이나 목록 상태를 부가해 클라이언트로부터 송신되어 온다
			// 보내져 오는 타이밍은 클라이언트 종료시
			case C_QUIT:
				// =========== IP Check[#PacketHandler] ===========
				// if(Config.AUTH_CONNECT) {
				// LoginAuth authIP = new LoginAuth();
				// authIP.ConnectDelete(_client.getIp());
				// }
				// =========== IP Check[#PacketHandler] ===========
				break;
			case C_BAN_MEMBER:
				C_BanClan bc = new C_BanClan(abyte0, _client);
				bc = null;
				break;
			case C_PLATE:
				C_Board cbbb = new C_Board(abyte0, _client);
				cbbb = null;
				break;
			case C_DESTROY_ITEM:
				C_DeleteInventoryItem cdii = new C_DeleteInventoryItem(abyte0,
						_client);
				cdii = null;
				break;
			case C_TELL:
				C_ChatWhisper cw = new C_ChatWhisper(abyte0, _client);
				cw = null;
				break;
			/*case C_WHO_PARTY:
				C_Party cparty = new C_Party(abyte0, _client);
				cparty = null;
				break;*/
			case C_GET:
				C_PickUpItem cpui = new C_PickUpItem(abyte0, _client);
				cpui = null;
				break;
			case C_WHO:
				C_Who who = new C_Who(abyte0, _client);
				who = null;
				break;
			case C_GIVE:
				C_GiveItem cgi = new C_GiveItem(abyte0, _client);
				cgi = null;
				break;
			case C_MOVE:
				C_MoveChar cmc = new C_MoveChar(abyte0, _client);
				cmc = null;
				break;
			case C_DELETE_BOOKMARK:
				C_DeleteBookmark cdb = new C_DeleteBookmark(abyte0, _client);
				cdb = null;
				break;
			/*
			 * case C_OPCODE_RESTART: C_RestartAfterDie cra = new
			 * C_RestartAfterDie(abyte0, _client); cra = null; break;
			 */
			case C_LEAVE_PLEDGE:
				C_LeaveClan clc = new C_LeaveClan(abyte0, _client);
				clc = null;
				break;
			case C_DIALOG:
				C_NPCTalk cnt = new C_NPCTalk(abyte0, _client);
				cnt = null;
				break;
			/*case C_BANISH_PARTY:
				C_BanParty cbpp = new C_BanParty(abyte0, _client);
				cbpp = null;
				break;*/
			case C_REMOVE_BUDDY:
				C_DelBuddy cdbb = new C_DelBuddy(abyte0, _client);
				cdbb = null;
				break;
			case C_WAR:
				C_War war = new C_War(abyte0, _client);
				war = null;
				break;
			case C_ENTER_WORLD:
				 if(Config.캐릭터비번사용여부 == true){
					if (!_client.getAccount().iscpwok()) {
						if (_client.getAccount().getCPW() == null) {
							_client.sendPacket(new S_CharPass(S_CharPass._비번생성창));
							return;
						} else {
							_client.getAccount().setwaitpacket(abyte0);
							_client.sendPacket(new S_CharPass(S_CharPass._비번입력창));
							return;
						}
					}
				}
				C_SelectCharacter sc = new C_SelectCharacter(abyte0, _client);
				sc = null;
				break;
			case C_QUERY_PERSONAL_SHOP:
				C_ShopList csll = new C_ShopList(abyte0, _client);
				csll = null;
				break;
			 case C_CHAT:
				 C_Macro chat2 = new C_Macro(abyte0, _client);
			 chat2 = null;
			 break;
			case C_JOIN_PLEDGE:
				C_JoinClan cjc = new C_JoinClan(abyte0, _client);
				cjc = null;
				break;
			case C_READ_NEWS:
				if (S_Notice.NoticeCount(_client.getAccountName()) > 0) {
					S_Notice sn = new S_Notice(_client.getAccountName(),
							_client);
					_client.sendPacket(sn);
					sn = null;
				} else {
					C_NoticeClick cnc = new C_NoticeClick(_client);
					cnc = null;
				}
				break;

			case C_ACTION:
				C_ExtraCommand cec = new C_ExtraCommand(abyte0, _client);
				cec = null;
				break;
			case C_BOARD_WRITE:
				C_BoardWrite cbw = new C_BoardWrite(abyte0, _client);
				cbw = null;
				break;

			case C_USE_ITEM:
				new C_ItemUSe(abyte0, _client);
				new C_ItemUSe2(abyte0, _client);
				break;
			/*case C_INVITE_PARTY_TARGET:
				C_CreateParty ccp = new C_CreateParty(abyte0, _client);
				ccp = null;
				break;*/
			case C_ENTER_PORTAL:
				C_EnterPortal cep = new C_EnterPortal(abyte0, _client);
				cep = null;
				break;
			case C_HYPERTEXT_INPUT_RESULT:
				C_Amount camount = new C_Amount(abyte0, _client);
				camount = null;
				break;
			case C_FIXABLE_ITEM:
				C_FixWeaponList cfwl = new C_FixWeaponList(abyte0, _client);
				cfwl = null;
				break;
			// case C_OPCODE_SELECTLIST:
			case C_FIX:
				C_SelectList csl = new C_SelectList(abyte0, _client);
				csl = null;
				break;
			// case C_OPCODE_EXIT_GHOST: C_ExitGhost ceg = new
			// C_ExitGhost(abyte0, _client); ceg = null;break;
			case C_SUMMON:
				C_CallPlayer callp = new C_CallPlayer(abyte0, _client);
				callp = null;
				break;

			case C_THROW:
				new C_FishClick(abyte0, _client);
				break; // 수정
			// case C_OPCODE_FISHCLICK: C_FishClick fc = new C_FishClick(abyte0,
			// _client); fc = null;break;

			case C_SLAVE_CONTROL:
				C_SelectTarget cst = new C_SelectTarget(abyte0, _client);
				cst = null;
				break;
			case C_CHECK_INVENTORY:
				C_PetMenu petm = new C_PetMenu(abyte0, _client);
				petm = null;
				break;
			case C_NPC_ITEM_CONTROL:
				C_UsePetItem upt = new C_UsePetItem(abyte0, _client);
				upt = null;
				break;
			// case C_OPCODE_TELEPORT: C_Teleport cte = new C_Teleport(abyte0,
			// _client); cte = null;break;
			case C_RANK_CONTROL:
				C_Rank rank = new C_Rank(abyte0, _client);
				rank = null;
				break;
			/*case C_CHAT_PARTY_CONTROL:
				C_ChatParty cpart = new C_ChatParty(abyte0, _client);
				cpart = null;
				break;*/
			case C_DUEL:
				C_Fight figh = new C_Fight(abyte0, _client);
				figh = null;
				break;
			case C_GOTO_MAP:
				C_Ship ship = new C_Ship(abyte0, _client);
				ship = null;
				break;
			case C_MAIL:
				C_MailBox mb = new C_MailBox(abyte0, _client);
				mb = null;
				break;
			case C_VOICE_CHAT:
				C_ReturnStaus crs = new C_ReturnStaus(abyte0, _client);
				crs = null;
				break;
			case C_WAREHOUSE_CONTROL:
				C_WarehousePassword wp = new C_WarehousePassword(abyte0,
						_client);
				wp = null;
				break; // 창고 비번
			case C_EXCHANGEABLE_SPELL:
				C_Horun hor = new C_Horun(abyte0, _client);
				hor = null;
				break;
			case C_EXCHANGE_SPELL:
				C_HorunOK hok = new C_HorunOK(abyte0, _client);
				hok = null;
				break;
			case C_MERCENARYEMPLOY:
				C_SoldierBuy csb2 = new C_SoldierBuy(abyte0, _client);
				csb2 = null;
				break;
			// case C_______OPCODE_SOLDIERGIVE: //용병
			// C_SoldierGive csg2 = new C_SoldierGive(abyte0, _client);
			// csg2 = null;
			// break;
			// case C_OPCODE_SOLDIERGIVEOK:
			// C_SoldierGiveOK csg = new C_SoldierGiveOK(abyte0, _client);
			// csg = null;
			// break;
			// case C_OPCODE_WARTIMESET: C_WarTimeSet wts = new
			// C_WarTimeSet(abyte0, _client); wts = null;break;
			case C_EMBLEM:
				C_Clan clan = new C_Clan(abyte0, _client);
				clan = null;
				break;
			case C_QUERY_CASTLE_SECURITY:
				C_SecurityStatus css = new C_SecurityStatus(abyte0, _client);
				css = null;
				break;
			case C_CHANGE_CASTLE_SECURITY:
				C_SecurityStatusSet csss = new C_SecurityStatusSet(abyte0,
						_client);
				csss = null;
				break;
			case C_CHANNEL:
				C_Report rep = new C_Report(abyte0, _client);
				rep = null;
				break;
			default:
				break;
			}
			abyte0 = null;
		} catch (Exception e) {
			e.printStackTrace();
			_log.log(Level.SEVERE, e.getLocalizedMessage(), (i != 0 ? "[OP:"
					+ i + "]" : "")
					+ e);
		}
	}

	public static String DataToPacket(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(HexToDex(i, 4) + ": ");
			}
			result.append(HexToDex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}
		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}
			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}
			result.append("\n");
		}
		return result.toString();
	}

	private static String HexToDex(int data, int digits) {
		String number = Integer.toHexString(data);
		for (int i = number.length(); i < digits; i++)
			number = "0" + number;
		return number;
	}

	private final LineageClient _client;
}