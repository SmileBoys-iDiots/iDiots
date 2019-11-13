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

	public static final int DOLL_START = 122; // ����
	public static final int DOLL_RESULT = 124; // Ŭ��
	private static final int NewStat = 228;
	private static final int SEAL = 0x39; // ���� ������
	private static final int Party_Assist = 0x57;
	private static final int Party_Invitation = 0x3c;
	private static final int ���λ��� = 49;

	public C_Craft(byte[] data, LineageClient client) throws IOException {
		super(data);
		if (client == null) {
			return;
		}
		L1PcInstance pc = client.getActiveChar();

		int type = readC();
		if(type!=NewStat && pc==null)
			return;

		//System.out.println("ũ����Ʈ > " + type);
		switch (type) {
		case ���λ���:
			if (pc == null || pc.isGhost() || pc.isDead()) {
				return;
			}
			if (pc.isInvisble()) {
				pc.sendPackets(new S_ServerMessage(755), true);
				return;
			}
			if (pc.getMapId() != 800) {
				pc.sendPackets(new S_ServerMessage(3405)); //���� �Ұ� ����
				return;
			}

			if (Config.STANDBY_SERVER ){
				pc.sendPackets(new S_SystemMessage("���´���߿��� ����� �� �����ϴ�."), true);
				pc.sendPackets(new S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Idle));
				Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(),ActionCodes.ACTION_Idle));
				break;
			}
			
			for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
				if (target.getId() != pc.getId() 
						&& target.getAccountName().toLowerCase().equals(pc.getAccountName().toLowerCase()) 
						&& target.isPrivateShop()) {
					pc.sendPackets(new S_SystemMessage("\\aH�˸�: �̹� ���λ����� ���� �Ǿ� �ֽ��ϴ� ."), true);
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

			if (shoptype == 0) { // ����
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
						// �ŷ� ������ �������̳� üũ
						checkItem = pc.getInventory().getItem(sellObjectId);

						if (checkItem == null) {
							continue;
						}

						if (sellObjectId != checkItem.getId()) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: ������ ������ �Դϴ�. �ٽ� �õ����ּ���."), true);
						}
						if (!checkItem.isStackable() && sellCount != 1) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: ������ ������ �Դϴ�. �ٽ� �õ����ּ���."), true);
						}
						if (sellCount > checkItem.getCount()) {
							sellCount = checkItem.getCount();
						}
						if (checkItem.getCount() < sellCount || checkItem.getCount() <= 0 || sellCount <= 0) {
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: ������ ������ �Դϴ�. �ٽ� �õ����ּ���."), true);
						}
						if (checkItem.getBless() >= 128) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, checkItem.getItem().getName())); //���λ���
						}
						
						if (!checkItem.getItem().isTradable()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(941), true); //�ŷ� �Ұ� �������Դϴ�.
						}
						
						L1DollInstance ���� = null;
						for (Object ����������Ʈ : pc.getDollList()) {
							if (����������Ʈ instanceof L1DollInstance) {
								���� = (L1DollInstance) ����������Ʈ;
								if (checkItem.getId() == ����.getItemObjId()) {
									tradable = false;
									pc.sendPackets(new S_ServerMessage(941), true); //�ŷ� �Ұ� �������Դϴ�.
								}
							}
						}

						petlist = pc.getPetList().toArray();
						for (Object petObject : petlist) {
							if (petObject instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) petObject;
								if (checkItem.getId() == pet.getItemObjId()) {
									tradable = false;
									pc.sendPackets(new S_ServerMessage(166, checkItem.getItem().getName(), "\\aH�˸�: �ŷ��� �Ұ��� �մϴ�."));
									return;
								}
							}
						}
						
						if (code == 0x12){
							if(sellTotalCount > 7){ 
								pc.sendPackets(new S_SystemMessage("\\aH�˸�: ��ǰ�� 7�� ���� ��� �����մϴ�.")); 
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
								pc.sendPackets(new S_SystemMessage("\\aH�˸�: ��ǰ�� 7�� ���� ��� �����մϴ�.")); 
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

				if (!tradable) { // �ŷ� �Ұ����� �������� ���ԵǾ� �ִ� ���, ���� ���� ����
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					return;
				}
				
				/** ������ �ΰ� **/
				int shopOpenCount = pc.getNetConnection().getAccount().Shop_open_count;
				if (shopOpenCount >= 40) {
					int OpenAdena = 20000 + ((shopOpenCount - 40) * 1000);
					if (!pc.getInventory().consumeItem(40308, OpenAdena)) {
						sellList.clear();
						buyList.clear();
						pc.setPrivateShop(false);
						pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
						Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle));
						pc.sendPackets(new S_ServerMessage(189), true); //�Ƶ����� �����մϴ�
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
						pc.sendPackets(new S_ServerMessage(189), true); //�Ƶ����� �����մϴ�
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
						pc.�ӽ�SaveShop(pc, sellitem, sellp, sellc, 1);
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
						pc.�ӽ�SaveShop(pc, buyitem, buyp, buyc, 0);
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
					pc.sendPackets(new S_PacketBox(S_PacketBox.��������Ƚ��, pc.getNetConnection().getAccount().Shop_open_count), true);
					pc.sendPackets(new S_ChatPacket(pc, "\\aH�˸�: .���λ��� �Է½� ���λ��� ���� ."));
				} catch (Exception e) {
					pc.���������ۻ���(pc.getId());
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPackets(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Idle), true);
					return;
				}
				petlist = null;
			} else if (shoptype == 1) { // ����
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
					pc.���������ۻ���(pc.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case SEAL: 	
			readH();
			readH();
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(read4(read_size()));
			if(l1iteminstance1.getItem().getType2()==0){ // etc �������̶�� 
				pc.sendPackets(new S_ServerMessage(79)); // �ƹ��ϵ� �Ͼ�� �ʴ´� (��Ʈ)
				return;
			}
			if (l1iteminstance1.getBless() == 0 || l1iteminstance1.getBless() == 1
					|| l1iteminstance1.getBless() == 2 || l1iteminstance1.getBless() == 3) {
				int Bless = 0;
				switch (l1iteminstance1.getBless()) {
				case 0: Bless = 128; break; //��
				case 1: Bless = 129; break; //����
				case 2: Bless = 130; break; //����
				case 3: Bless = 131; break; //��Ȯ��
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
				pc.sendPackets(new S_ServerMessage(79)); // \f1 �ƹ��͵� �Ͼ�� �ʾҽ��ϴ�.
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
			/** 0�� �Ϲ� ��Ƽ 1�� �й���Ƽ 2�� ä����Ƽ 5/�ʴ� �̸� 6�� ǥ��  7���߹� 8����Ƽ���� */
			if (Type == 0 || Type == 1 || Type == 4 || Type == 5) {
				if (Player instanceof L1PcInstance) {
					if (pc.getId() == Player.getId()) return;
					if (Player.isInParty()) {
						/** ���� �ٸ� ��Ƽ�� �Ҽ��� �ֱ� (����)������ �ʴ��� �� �����ϴ� */
						pc.sendPackets(new S_ServerMessage(415), true);
						return;
					}

					if (pc.isInParty()) {
						if (pc.getParty().isLeader(pc)) {
							Player.setPartyID(pc.getId());
							/** \f2%0\f>%s�κ��� \fU��Ƽ \f> �� �ʴ�Ǿ����ϴ�. ���մϱ�? (Y/N) */
							Player.sendPackets(new S_Message_YN(953, pc.getName()), true);
						} else {
							/** ��Ƽ�� �������� �ʴ��� �� �ֽ��ϴ�. */
							pc.sendPackets(new S_ServerMessage(416), true);
						}
					} else {
						Player.setPartyID(pc.getId());
						switch (Type) {
						case 4:
						case 0:
							pc.setPartyType(0);
							/** \f2%0\f>%s�κ��� \fU��Ƽ \f> �� �ʴ�Ǿ����ϴ�. ���մϱ�? (Y/N) */
							Player.sendPackets(new S_Message_YN(953,pc.getName()), true);
							break;
						case 5:
						case 1:
							pc.setPartyType(1);
							/** \f2%0\f>%s \fU�ڵ��й���Ƽ\f> �ʴ��Ͽ����ϴ�. ����Ͻðڽ��ϱ�? (Y/N) */
							Player.sendPackets(new S_Message_YN(954,pc.getName()), true);
							break;
						}
					}
				}
			} else if (Type == 2) { // ä�� ��Ƽ
				if (pc.getId() == Player.getId()) return;
				if (Player.isInChatParty()) {
					/** ���� �ٸ� ��Ƽ�� �Ҽ��� �ֱ� (����)������ �ʴ��� �� �����ϴ� */
					pc.sendPackets(new S_ServerMessage(415), true);
					return;
				}

				if (pc.isInChatParty()) {
					if (pc.getChatParty().isLeader(pc)) {
						Player.setPartyID(pc.getId());
						/** \f2%0\f>%s�κ���\fUä�� ��Ƽ \f>�� �ʴ�Ǿ����ϴ�. ���մϱ�? (Y/N) */
						Player.sendPackets(new S_Message_YN(951, pc.getName()), true);
					} else {
						/** ��Ƽ�� �������� �ʴ��� �� �ֽ��ϴ�. */
						pc.sendPackets(new S_ServerMessage(416), true);
					}
				} else {
					Player.setPartyID(pc.getId());
					/** \f2%0\f>%s�κ���\fUä�� ��Ƽ \f>�� �ʴ�Ǿ����ϴ�. ���մϱ�? (Y/N) */
					Player.sendPackets(new S_Message_YN(951, pc.getName()), true);
				}
			} else if (Type == 3) { // ����
				/** ��Ƽ���� �ƴϰų� ��������̶�� �ȵǵ��� ���� */
				if (!pc.getParty().isLeader(pc) || pc.getId() == Player.getId()) {
					return;
				}
				/** ���� �ٸ��ų� 10���� ���϶�� ���� �Ұ��� �ϵ��� ���� */
				/*if (!isDistance(pc.getX(), pc.getY(), pc.getMapId(), Player.getX(), Player.getY(), Player.getMapId(), 10)){
						pc.sendPackets(new S_ServerMessage(1695)); 
						return;
					}*/
				pc.getParty().passLeader(Player);
			}else if (Type == 6){
				readC();
				Player.setǥ��(readC());
				for (L1PcInstance player : pc.getParty().getMembers()) {
					player.sendPackets(new S_Party(0x6e, Player));
				}
			} else if (Type == 7) { // �߹�
				if (!pc.getParty().isLeader(pc)) {
					/** ��Ƽ�� �������� �߹��� �� �ֽ��ϴ�. */
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
			int chance2 = ((total * Config.����Ȯ��2)); // �����ռ� Ȯ�� ����
			int chance3 = ((total * Config.����Ȯ��3)); // �����ռ� Ȯ�� ����
			int chance4 = ((total * Config.����Ȯ��4)); // �����ռ� Ȯ�� ����
			int chance5 = ((total * Config.����Ȯ��5)); // �����ռ� Ȯ�� ����
			switch (step) {
			case 1:
				if (CommonUtil.random(100) + 1 <= chance2) {
					dollids = new int[] { 
							430001, // ���
							41249, // ��ť����
							430500, // ��īƮ����
							500109, // �����(A) ?
							500108, // �ξ�
							600242 // ��ٰ�
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
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
							500205, // ��ť ��
							500204, // �����
							500203, // ���̾�Ʈ
							60324, // �巹��ũ
							500110, // ŷ ���׺���
							600243 // ���̾Ƹ���
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							//L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4433, item.getItem().getNameId(), pc.getName()));
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
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
							500202, // ����Ŭ�ӽ�
							5000035, // ��ġ
							600245, // ����Ʈ�ߵ�
							600244, // �þ�
							142920, // ���̸���
							142921, // �����̾�
							751 // �ӹ̷ε�
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, false, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 4://������ ������ �ܰ�
				if (CommonUtil.random(100) + 1 <= chance5) {
					dollids = new int[] { 
							600246, // ����
							600247, // ��������Ʈ 746
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							754, //���
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
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
							600321, // ����
							600322, // ��������Ʈ 746
							600323, // �ٶ�ī
							600324, // Ÿ��
							600325, //����
							600326, //���
							600327, //Ŀ��
							142922, // �ٶ�ī
							600327, //Ŀ��
							142922, // �ٶ�ī
							600324, // Ÿ��
							600325, //����
							600327, //Ŀ��
							142922, // �ٶ�ī
							600324, // Ÿ��
							600325, //����
							600327, //Ŀ��
							142922, // �ٶ�ī
							600324, // Ÿ��
							600325, //����
							600327, //Ŀ��
							142922, // �ٶ�ī
							600324, // Ÿ��
							600325, //����
							600327, //Ŀ��
							142922, // �ٶ�ī
							600324, // Ÿ��
							600325, //����
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), true));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					dollids = new int[] {
							600246, // ����
							600247, // ��������Ʈ 746
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							142922, // �ٶ�ī
							752, // Ÿ��
							753, //����
							755, //Ŀ��
							600259, // ��Ÿ
							600261, // ����
							600260, // ��Ǫ
							600262 // �߶�
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
						} catch (Exception e) {
						}
					}
				}
				break;
			case 6:
				if (CommonUtil.random(100) + 1 <= 50) {
					dollids = new int[] {
							600308, // ������
							600309, // �����̾�Ʈ
							600310, // �༭ť
							600311, // ��巹��
							600312, //��ŷ����
							600313 //����̾Ƹ���
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), true));
					pc.getInventory().storeItem(item);
				} else {
					dollids = new int[] { 
							500205, // ��ť ��
							500204, // �����
							500203, // ���̾�Ʈ
							60324, // �巹��ũ
							500110, // ŷ ���׺���
							600243 // ���̾Ƹ���
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
				}
				break;
			case 7:
				if (CommonUtil.random(100) + 1 <= 40) {
					dollids = new int[] {
							600314, // �ฮġ
							600315, // ��þ�
							600316, // �೪��
							600317, // ��þ�
							600318, //����̸���
							600319, //�����
							600320 //��ӹ�
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), true));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
						} catch (Exception e) {
						}
					}
				} else {
					dollids = new int[] {
							500202, // ����Ŭ�ӽ�
							5000035, // ��ġ
							600245, // ����Ʈ�ߵ�
							600244, // �þ�
							142920, // ���̸���
							142921, // �����̾�
							751, // �ӹ̷ε�
					};
					item = ItemTable.getInstance().createItem(dollids[CommonUtil.random(dollids.length)]);
					pc.sendPackets(new S_ACTION_UI2(S_ACTION_UI2.DOLL_RESULT, true, item.getId(), item.get_gfxid(), false));
					pc.getInventory().storeItem(item);
					if (step >= 3) {
						try {
							Thread.sleep(10000);
							L1World.getInstance().broadcastPacketToAll(new S_ACTION_UI(S_ACTION_UI.��������޽���, 4433, item), true);
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
	
	private boolean isTwoLogin(L1PcInstance c) {// �ߺ�üũ ���� 
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
