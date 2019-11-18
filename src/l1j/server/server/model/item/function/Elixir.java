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

package l1j.server.server.model.item.function;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Elixir;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class Elixir extends L1ItemInstance
{
	private static final int ���Ǿٸ��� = 40033;
	private static final int ���Ǿٸ��� = 40034;
	private static final int �����Ǿٸ��� = 40035;
	private static final int ��Ʈ�Ǿٸ��� = 40036;
	private static final int �����Ǿٸ��� = 40037;
	private static final int ī�������Ǿٸ��� = 40038;
	
	public Elixir(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		try
		{
			if (cha instanceof L1PcInstance) 
			{
				L1PcInstance pc = (L1PcInstance) cha;
				L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
				int itemId = useItem.getItemId();
				int item_minlvl = ((L1EtcItem) useItem.getItem()).getMinLevel();
				int item_maxlvl = ((L1EtcItem) useItem.getItem()).getMaxLevel();
				if (item_minlvl != 0 && item_minlvl > pc.getLevel()	&& !pc.isGm())
				{
					pc.sendPackets(new S_ServerMessage(318, String.valueOf(item_minlvl)), true);
					// �� ��������%0���� �̻��� ���� ������ ����� �� �����ϴ�.
					return;
				}
				else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel() && !pc.isGm())
				{
					pc.sendPackets(new S_ServerMessage(673, String.valueOf(item_maxlvl)), true);
					// �� ��������%d���� �̻� ����� �� �ֽ��ϴ�.
					return;
				}
				
				// ������ ��� ���� ������ 50���� 1�� 3������ 1���� �߰�
				int ElixirUse = pc.getLevel() - 50;
				if(ElixirUse < 0)
				{
					pc.sendPackets(new S_ServerMessage(4473), true);
					pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv����  ���밡 �����մϴ� ."), true);
					return;
				}
				if(ElixirUse > 45)
					ElixirUse = 45;
				
				int ElixirMaxCount = ((ElixirUse / 3) + 1);
				int ElixirUseCount = ((ElixirUse / 3) + 1) - pc.getAbility().getElixirCount();				
				switch (itemId)
				{
					case ���Ǿٸ���:
						if (pc.getAbility().getStr() < 55 && pc.getAbility().getElixirCount() < ElixirMaxCount && ElixirUseCount >= 1)
						{						
							pc.getAbility().addStr((byte) 1);
							pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc), true);
							pc.save();
							pc.sendPackets(new S_Elixir(S_Elixir.Elixir, pc.getAbility().getElixirCount()));
						}
						else
						{
							pc.sendPackets(new S_ServerMessage(4473), true);
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv���� 3lv������ ���밡 �����մϴ� ."), true);
						}
						break;
					case ���Ǿٸ���:
						if (pc.getAbility().getCon() < 55 && pc.getAbility().getElixirCount() < ElixirMaxCount && ElixirUseCount >= 1)
						{
							pc.getAbility().addCon((byte) 1);
							pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc), true);
							pc.save();
							pc.sendPackets(new S_Elixir(S_Elixir.Elixir, pc.getAbility().getElixirCount()));
						} 
						else
						{
							pc.sendPackets(new S_ServerMessage(4473), true);
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv���� 3lv������ ���밡 �����մϴ� ."), true);
						}
						break;
					case �����Ǿٸ���:
						if (pc.getAbility().getDex() < 55 && pc.getAbility().getElixirCount() < ElixirMaxCount && ElixirUseCount >= 1)
						{
							pc.getAbility().addDex((byte) 1);
							pc.resetBaseAc();
							pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc), true);
							pc.sendPackets(
									new S_PacketBox(S_PacketBox.char_ER, pc
											.get_PlusEr()), true);
							pc.save();
							pc.sendPackets(new S_Elixir(S_Elixir.Elixir, pc.getAbility().getElixirCount()));
						}
						else
						{
							pc.sendPackets(new S_ServerMessage(4473), true);
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv���� 3lv������ ���밡 �����մϴ� ."), true);
						}
						break;
					case ��Ʈ�Ǿٸ���:
						if (pc.getAbility().getInt() < 55 && pc.getAbility().getElixirCount() < ElixirMaxCount && ElixirUseCount >= 1)
						{
							pc.getAbility().addInt((byte) 1);
							pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc), true);
							pc.save();
							pc.sendPackets(new S_Elixir(S_Elixir.Elixir, pc.getAbility().getElixirCount()));
						}
						else
						{
							pc.sendPackets(new S_ServerMessage(4473), true); 
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv���� 3lv������ ���밡 �����մϴ� ."), true);
						}
						break;
					case �����Ǿٸ���:
						if (pc.getAbility().getWis() < 55 && pc.getAbility().getElixirCount() < ElixirMaxCount && ElixirUseCount >= 1)
						{
							pc.getAbility().addWis((byte) 1);
							pc.resetBaseMr();
							pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc), true);
							pc.save();
							pc.sendPackets(new S_Elixir(S_Elixir.Elixir, pc.getAbility().getElixirCount()));
						}
						else
						{
							pc.sendPackets(new S_ServerMessage(4473), true);
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv���� 3lv������ ���밡 �����մϴ� ."), true);
						}
						break;
					case ī�������Ǿٸ���:
						if (pc.getAbility().getCha() < 55 && pc.getAbility().getElixirCount() < ElixirMaxCount && ElixirUseCount >= 1)
						{
							pc.getAbility().addCha((byte) 1);
							pc.getAbility().setElixirCount(pc.getAbility().getElixirCount() + 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.sendPackets(new S_OwnCharStatus2(pc), true);
							pc.save();
							pc.sendPackets(new S_Elixir(S_Elixir.Elixir, pc.getAbility().getElixirCount()));
						}
						else
						{
							pc.sendPackets(new S_ServerMessage(4473), true); 
							pc.sendPackets(new S_SystemMessage("\\aH�˸�: �������� 50lv���� 3lv������ ���밡 �����մϴ� ."), true);
						}
						break;
				}
			}
		}
		catch (Exception e)
		{
		}
	}
}
