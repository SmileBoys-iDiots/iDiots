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

package l1j.server.server.utils;

import java.util.Random;

import l1j.server.Config;

public class CalcStat {

	private static Random rnd = new Random(System.nanoTime());
	
	private static int[] strDamage = new int[128];
	private static int[] strHit = new int[128];
	private static int[] strCritical = new int[128];
	
	private static int[] dexDamage = new int[128];
	private static int[] dexHit = new int[128];
	private static int[] dexCritical = new int[128];
	private static int[] dexAC = new int[128];
	private static int[] dexER = new int[128];
	
	private static int[] intDamage = new int[128];
	private static int[] intHit = new int[128];
	private static int[] intCritical = new int[128];
	private static int[] intBonus = new int[128];
	
	private static int[] conHP���� = new int[128];
	private static int[] conHPȸ�� = new int[128];
	private static int[] con����ȸ�� = new int[128];
	
	private static int[] wisMP���� = new int[128];
	private static int[] wisMPȸ�� = new int[128];
	private static int[] wis����ȸ�� = new int[128];
	private static int[] wisMR = new int[128];

	private CalcStat()
	{
		ResetAbilityBonus();
	}
	
	
	private static void ResetAbilityBonus()
	{
		ResetAbilityBonus_STR();
		ResetAbilityBonus_DEX();
		ResetAbilityBonus_INT();
		ResetAbilityBonus_CON();
		ResetAbilityBonus_WIS();
	}
	private static void ResetAbilityBonus_STR()
	{
		// �ʱ�ȭ
		for (int i = 0; i < 128; i++)
		{
			strDamage[i] = 0;
			strHit[i] = 0;
			strCritical[i] = 0;
		}
		
		int dmg = 1; 
		for (int i = 8; i < 128; i++) // 8���� 2���� ���� 1�� ����
		{ 
			if (i % 2 == 0)
			{
				dmg++;
			}
			strDamage[i] = dmg;
		}

		int hit = 4;
		int addCount = 0;
		for (int i = 8; i < 128; i++) // 8���� �η��� �������� 1�� ����
		{
			if(addCount >= 2)
			{
				addCount = 0;
			}
			else
			{
				addCount++;
				hit++;
			}
			strHit[i] = hit;
		}

		int cri = 0; 
		for (int i = 40; i < 128; i++) // 40���� 10�������� 1�� ����
		{
			if (i % 10 == 0)
			{
				cri++;
			}
			strCritical[i] = cri;
		}
	}
	private static void ResetAbilityBonus_DEX()
	{
		// �ʱ�ȭ
		for (int i = 0; i < 128; i++)
		{
			dexDamage[i] = 0;
			dexHit[i] = -3;
			dexCritical[i] = 0;
			dexAC[i] = -1;
			dexER[i] = 0;
		}

		int dmg = 1; 
		for (int i = 6; i < 128; i++) // 6���� 3���� ���� 1�� ����
		{ 
			if (i % 3 == 0)
			{
				dmg++;
			}
			dexDamage[i] = dmg;
		}

		int hit = -3; 
		for (int i = 7; i < 128; i++) // 7���� 1���� ���� 1�� ����
		{ 
			dexHit[i] = hit;
			hit++;
		}

		int cri = 0; 
		for (int i = 40; i < 128; i++) // 40���� 10�������� 1�� ����
		{
			if (i % 10 == 0)
			{
				cri++;
			}
			dexCritical[i] = cri;
		}

		int ac = -1; 
		for (int i = 6; i < 128; i++) // 6���� 3���� ���� 1�� ����
		{
			if (i % 3 == 0)
			{
				ac--;
			}
			dexAC[i] = ac;
		}

		int er = 2; 
		for (int i = 6; i < 44; i++) // 6���� 2���� ���� 1�� ����
		{
			if (i % 2 == 0)
			{
				er++;
			}
			dexER[i] = er;
		}
		for (int i = 44; i < 128; i++) // 44���� 3���� ���� 1�� ����
		{
			if ((i - 44) % 3 == 0)
			{
				er++;
			}
			dexER[i] = er;
		}
	}
	private static void ResetAbilityBonus_INT()
	{
		// �ʱ�ȭ
		for (int i = 0; i < 128; i++)
		{
			dexDamage[i] = 0;
			dexHit[i] = -3;
			dexCritical[i] = 0;
			dexAC[i] = -1;
			dexER[i] = 0;
		}

		int dmg = 1; 
		for (int i = 6; i < 128; i++) // 6���� 3���� ���� 1�� ����
		{ 
			if (i % 3 == 0)
			{
				dmg++;
			}
			dexDamage[i] = dmg;
		}

		int hit = -3; 
		for (int i = 7; i < 128; i++) // 7���� 1���� ���� 1�� ����
		{ 
			dexHit[i] = hit;
			hit++;
		}

		int cri = 0; 
		for (int i = 40; i < 128; i++) // 40���� 10�������� 1�� ����
		{
			if (i % 10 == 0)
			{
				cri++;
			}
			dexCritical[i] = cri;
		}

		int ac = -1; 
		for (int i = 6; i < 128; i++) // 6���� 3���� ���� 1�� ����
		{
			if (i % 3 == 0)
			{
				ac--;
			}
			dexAC[i] = ac;
		}

		int er = 2; 
		for (int i = 6; i < 44; i++) // 6���� 2���� ���� 1�� ����
		{
			if (i % 2 == 0)
			{
				er++;
			}
			dexER[i] = er;
		}
		for (int i = 44; i < 128; i++) // 44���� 3���� ���� 1�� ����
		{
			if ((i - 44) % 3 == 0)
			{
				er++;
			}
			dexER[i] = er;
		}
	}
	private static void ResetAbilityBonus_CON()
	{
		// �ʱ�ȭ
		for (int i = 0; i < 128; i++)
		{
			conHP����[i] = 8;
			conHPȸ��[i] = 5;
			con����ȸ��[i] = 0;
		}

		for (int i = 10; i < 128; i++) // 8���� 1���� ���� 1�� ����
		{
			conHP����[i] = i - 2;
		}

		int HPȸ�� = 0; 
		for (int i = 10; i < 128; i++) // 10���� ���� / 2 ����
		{
			HPȸ�� = i / 2;
			if(i >= 40)
			{
				HPȸ�� += (i- 40) / 2;
			}
			conHPȸ��[i] = HPȸ��;
		}

		int ����ȸ�� = 0; 
		for (int i = 20; i < 128; i++) // 20���� 5�������� 1�� ����
		{
			if (i % 5 == 0)
			{
				����ȸ��++;
			}
			con����ȸ��[i] = ����ȸ��;
		}
	}
	private static void ResetAbilityBonus_WIS()
	{
		// �ʱ�ȭ
		for (int i = 0; i < 128; i++)
		{
			wisMP����[i] = 0;
			wisMPȸ��[i] = 1;
			wis����ȸ��[i] = 1;
			wisMR[i] = 0;
		}

		int MP���� = 0; 
		for (int i = 15; i < 128; i++) // 15���� 5���� ���� 1�� ����
		{ 
			if (i % 5 == 0)
			{
				MP����++;
			}
			wisMP����[i] = MP����;
		}

		int MPȸ�� = 1; 
		for (int i = 10; i < 128; i++) // 10���� 5���� ���� 1�� ����
		{ 
			if (i % 5 == 0)
			{
				MPȸ��++;
			}
			wisMPȸ��[i] = MPȸ��;
		}

		int ����ȸ�� = 1; 
		for (int i = 11; i < 128; i++) // 11���� 2�������� 1�� ����
		{
			if (i % 2 == 0)
			{
				����ȸ��++;
			}
			wis����ȸ��[i] = ����ȸ��;
		}

		for (int i = 10; i < 128; i++) // 10���� 1�������� 4�� ����
		{
			wisMR[i] = (i - 10) * 4;
		}
	}

	
	public static int[] ����������������(int charType, int wis) {
		int �ּҿ��� = 0;
		int �ִ뿥�� = 0;
		switch (charType) {
		case 0:
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				�ּҿ��� = 3;
				�ִ뿥�� = 4;
				break;
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 3;
				�ִ뿥�� = 5;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 4;
				�ִ뿥�� = 6;
				break;
			case 18:
			case 19:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 20:
				�ּҿ��� = 5;
				�ִ뿥�� = 7;
				break;
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 24:
				�ּҿ��� = 5;
				�ִ뿥�� = 9;
				break;
			case 25:
			case 26:
				�ּҿ��� = 6;
				�ִ뿥�� = 9;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
				break;
			case 33:
			case 34:
				�ּҿ��� = 7;
				�ִ뿥�� = 12;
				break;
			case 35:
				�ּҿ��� = 8;
				�ִ뿥�� = 12;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 8;
				�ִ뿥�� = 13;
				break;
			case 39:
				�ּҿ��� = 8;
				�ִ뿥�� = 14;
				break;
			case 40:
			case 41:
				�ּҿ��� = 9;
				�ִ뿥�� = 14;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 9;
				�ִ뿥�� = 15;
				break;
			case 45:
				�ּҿ��� = 10;
				�ִ뿥�� = 16;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 10;
				�ִ뿥�� = 16;
			}
			break;// ����
		case 1:// ���
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				�ּҿ��� = 0;
				�ִ뿥�� = 2;
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 1;
				�ִ뿥�� = 2;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 2;
				�ִ뿥�� = 3;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 2;
				�ִ뿥�� = 4;
				break;
			case 24:
				�ּҿ��� = 2;
				�ִ뿥�� = 5;
				break;
			case 25:
			case 26:
				�ּҿ��� = 3;
				�ִ뿥�� = 5;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 3;
				�ִ뿥�� = 6;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 4;
				�ִ뿥�� = 6;
				break;
			case 33:
			case 34:
			case 35:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				�ּҿ��� = 4;
				�ִ뿥�� = 8;
				break;
			case 40:
			case 41:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 5;
				�ִ뿥�� = 9;
				break;
			case 45:
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
			}
			break;

		case 2:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 18:
			case 19:
				�ּҿ��� = 5;
				�ִ뿥�� = 10;
				break;
			case 20:
				�ּҿ��� = 7;
				�ִ뿥�� = 10;
				break;
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
				break;
			case 24:
				�ּҿ��� = 7;
				�ִ뿥�� = 13;
				break;
			case 25:
			case 26:
				�ּҿ��� = 8;
				�ִ뿥�� = 13;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 8;
				�ִ뿥�� = 14;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 10;
				�ִ뿥�� = 16;
				break;
			case 33:
			case 34:
				�ּҿ��� = 10;
				�ִ뿥�� = 17;
				break;
			case 35:
				�ּҿ��� = 11;
				�ִ뿥�� = 17;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 11;
				�ִ뿥�� = 19;
				break;
			case 39:
				�ּҿ��� = 11;
				�ִ뿥�� = 20;
				break;
			case 40:
			case 41:
				�ּҿ��� = 13;
				�ִ뿥�� = 20;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 13;
				�ִ뿥�� = 21;
				break;
			case 45:
				�ּҿ��� = 14;
				�ִ뿥�� = 23;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 14;
				�ִ뿥�� = 23;
			}
			break;

		case 3:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 8;
				�ִ뿥�� = 12;
				break;
			case 18:
			case 19:
				�ּҿ��� = 8;
				�ִ뿥�� = 14;
				break;
			case 20:
				�ּҿ��� = 10;
				�ִ뿥�� = 14;
				break;
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 10;
				�ִ뿥�� = 16;
				break;
			case 24:
				�ּҿ��� = 10;
				�ִ뿥�� = 18;
				break;
			case 25:
			case 26:
				�ּҿ��� = 12;
				�ִ뿥�� = 18;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 12;
				�ִ뿥�� = 20;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 14;
				�ִ뿥�� = 22;
				break;
			case 33:
			case 34:
				�ּҿ��� = 14;
				�ִ뿥�� = 24;
				break;
			case 35:
				�ּҿ��� = 16;
				�ִ뿥�� = 24;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 16;
				�ִ뿥�� = 26;
				break;
			case 39:
				�ּҿ��� = 16;
				�ִ뿥�� = 28;
				break;
			case 40:
			case 41:
				�ּҿ��� = 18;
				�ִ뿥�� = 28;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 18;
				�ִ뿥�� = 30;
				break;
			case 45:
				�ּҿ��� = 20;
				�ִ뿥�� = 32;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 20;
				�ִ뿥�� = 32;
			}
			break;

		case 4:// �ٿ�
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				�ּҿ��� = 4;
				�ִ뿥�� = 5;
				break;
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 18:
			case 19:
				�ּҿ��� = 5;
				�ִ뿥�� = 10;
				break;
			case 20:
				�ּҿ��� = 7;
				�ִ뿥�� = 10;
				break;
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
				break;
			case 24:
				�ּҿ��� = 7;
				�ִ뿥�� = 13;
				break;
			case 25:
			case 26:
				�ּҿ��� = 8;
				�ִ뿥�� = 13;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 8;
				�ִ뿥�� = 14;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 10;
				�ִ뿥�� = 16;
				break;
			case 33:
			case 34:
				�ּҿ��� = 10;
				�ִ뿥�� = 17;
				break;
			case 35:
				�ּҿ��� = 11;
				�ִ뿥�� = 17;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 11;
				�ִ뿥�� = 19;
				break;
			case 39:
				�ּҿ��� = 11;
				�ִ뿥�� = 20;
				break;
			case 40:
			case 41:
				�ּҿ��� = 13;
				�ִ뿥�� = 20;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 13;
				�ִ뿥�� = 22;
				break;
			case 45:
				�ּҿ��� = 14;
				�ִ뿥�� = 23;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 14;
				�ִ뿥�� = 23;
			}
			break;

		case 5:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 2;
				�ִ뿥�� = 3;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 3;
				�ִ뿥�� = 4;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 3;
				�ִ뿥�� = 5;
				break;
			case 24:
				�ּҿ��� = 3;
				�ִ뿥�� = 6;
				break;
			case 25:
			case 26:
				�ּҿ��� = 4;
				�ִ뿥�� = 6;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 5;
				�ִ뿥�� = 9;
				break;
			case 39:
				�ּҿ��� = 5;
				�ִ뿥�� = 10;
				break;
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
				break;
			case 45:
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
			}
			break;
		case 6:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 6;
				�ִ뿥�� = 9;
				break;
			case 18:
			case 19:
				�ּҿ��� = 6;
				�ִ뿥�� = 11;
				break;
			case 20:
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
				break;
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 7;
				�ִ뿥�� = 12;
				break;
			case 24:
				�ּҿ��� = 7;
				�ִ뿥�� = 14;
				break;
			case 25:
			case 26:
				�ּҿ��� = 9;
				�ִ뿥�� = 14;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 9;
				�ִ뿥�� = 16;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 11;
				�ִ뿥�� = 18;
				break;
			case 33:
			case 34:
				�ּҿ��� = 11;
				�ִ뿥�� = 19;
				break;
			case 35:
				�ּҿ��� = 12;
				�ִ뿥�� = 19;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 12;
				�ִ뿥�� = 21;
				break;
			case 39:
				�ּҿ��� = 12;
				�ִ뿥�� = 23;
				break;
			case 40:
			case 41:
				�ּҿ��� = 14;
				�ִ뿥�� = 23;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 14;
				�ִ뿥�� = 24;
				break;
			case 45:
				�ּҿ��� = 16;
				�ִ뿥�� = 26;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 16;
				�ִ뿥�� = 26;
			}
			break;
		case 7:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				�ּҿ��� = 0;
				�ִ뿥�� = 1;
				break;
			case 9:
				�ּҿ��� = 0;
				�ִ뿥�� = 2;
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 1;
				�ִ뿥�� = 2;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 2;
				�ִ뿥�� = 3;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 2;
				�ִ뿥�� = 4;
				break;
			case 24:
				�ּҿ��� = 2;
				�ִ뿥�� = 5;
				break;
			case 25:
			case 26:
				�ּҿ��� = 3;
				�ִ뿥�� = 5;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 3;
				�ִ뿥�� = 6;
				break;
			case 30:
			case 31:
			case 32:
				�ּҿ��� = 4;
				�ִ뿥�� = 6;
				break;
			case 33:
			case 34:
			case 35:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				�ּҿ��� = 4;
				�ִ뿥�� = 8;
				break;
			case 40:
			case 41:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 5;
				�ִ뿥�� = 9;
				break;
			case 45:
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
				break;
			default:
				break;

			}
			;
			if (wis > 45) {
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
			}
			break;
		case 8:// �˻�
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				�ּҿ��� = 2;
				�ִ뿥�� = 3;
				break;
			case 15:
			case 16:
			case 17:
				�ּҿ��� = 3;
				�ִ뿥�� = 4;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				�ּҿ��� = 3;
				�ִ뿥�� = 5;
				break;
			case 24:
				�ּҿ��� = 3;
				�ִ뿥�� = 6;
				break;
			case 25:
			case 26:
				�ּҿ��� = 4;
				�ִ뿥�� = 6;
				break;
			case 27:
			case 28:
			case 29:
				�ּҿ��� = 4;
				�ִ뿥�� = 7;
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
				�ּҿ��� = 5;
				�ִ뿥�� = 8;
				break;
			case 36:
			case 37:
			case 38:
				�ּҿ��� = 5;
				�ִ뿥�� = 9;
				break;
			case 39:
				�ּҿ��� = 5;
				�ִ뿥�� = 10;
				break;
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
				�ּҿ��� = 6;
				�ִ뿥�� = 10;
				break;
			case 45:
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				�ּҿ��� = 7;
				�ִ뿥�� = 11;
			}
			break;
		default:
			break;
		}

		if (wis <= 0) {
			�ּҿ��� = 0;
			�ִ뿥�� = 1;
		}

		int[] bbb = { �ּҿ���, �ִ뿥�� };

		return bbb;
	}

	public static int ����������(int charType, int baseMaxMp, int wis) {
		int addmp = 0;
		switch (charType) {
		case 0:
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				addmp = rnd.nextInt(2) + 3;
				break;
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(3) + 3;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(3) + 4;
				break;
			case 18:
			case 19:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 20:
				addmp = rnd.nextInt(3) + 5;
				break;
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 24:
				addmp = rnd.nextInt(5) + 5;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(4) + 6;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(5) + 6;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(5) + 7;
				break;
			case 33:
			case 34:
				addmp = rnd.nextInt(6) + 7;
				break;
			case 35:
				addmp = rnd.nextInt(5) + 8;
				break;
			case 36:
			case 37:
			case 38:
				addmp = rnd.nextInt(6) + 8;
				break;
			case 39:
				addmp = rnd.nextInt(7) + 8;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(6) + 9;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(7) + 9;
				break;
			case 45:
				addmp = rnd.nextInt(7) + 10;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(7) + 10;
			break;// ����

		case 1:// ���
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				addmp = rnd.nextInt(3);
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(2) + 1;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(2) + 2;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(3) + 2;
				break;
			case 24:
				addmp = rnd.nextInt(4) + 2;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(3) + 3;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(4) + 3;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(3) + 4;
				break;
			case 33:
			case 34:
			case 35:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				addmp = rnd.nextInt(5) + 4;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(5) + 5;
				break;
			case 45:
				addmp = rnd.nextInt(5) + 6;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(5) + 6;
			break;

		case 2:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 18:
			case 19:
				addmp = rnd.nextInt(6) + 5;
				break;
			case 20:
				addmp = rnd.nextInt(4) + 7;
				break;
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(5) + 7;
				break;
			case 24:
				addmp = rnd.nextInt(7) + 7;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(6) + 8;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(7) + 8;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(7) + 10;
				break;
			case 33:
			case 34:
				addmp = rnd.nextInt(8) + 10;
				break;
			case 35:
				addmp = rnd.nextInt(7) + 11;
				break;
			case 36:
			case 37:
			case 38:
				addmp = rnd.nextInt(9) + 11;
				break;
			case 39:
				addmp = rnd.nextInt(10) + 11;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(8) + 13;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(10) + 13;
				break;
			case 45:
				addmp = rnd.nextInt(10) + 14;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(10) + 14;
			break;

		case 3:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(5) + 6;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(5) + 8;
				break;
			case 18:
			case 19:
				addmp = rnd.nextInt(7) + 8;
				break;
			case 20:
				addmp = rnd.nextInt(5) + 10;
				break;
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(7) + 10;
				break;
			case 24:
				addmp = rnd.nextInt(9) + 10;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(7) + 12;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(9) + 12;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(9) + 14;
				break;
			case 33:
			case 34:
				addmp = rnd.nextInt(11) + 14;
				break;
			case 35:
				addmp = rnd.nextInt(9) + 16;
				break;
			case 36:
			case 37:
			case 38:
				addmp = rnd.nextInt(11) + 16;
				break;
			case 39:
				addmp = rnd.nextInt(13) + 16;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(11) + 18;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(13) + 18;
				break;
			case 45:
				addmp = rnd.nextInt(13) + 20;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(13) + 20;
			break;

		case 4:// �ٿ�
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				addmp = rnd.nextInt(2) + 4;
				break;
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 18:
			case 19:
				addmp = rnd.nextInt(6) + 5;
				break;
			case 20:
				addmp = rnd.nextInt(4) + 7;
				break;
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(5) + 7;
				break;
			case 24:
				addmp = rnd.nextInt(7) + 7;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(6) + 8;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(7) + 8;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(7) + 10;
				break;
			case 33:
			case 34:
				addmp = rnd.nextInt(8) + 10;
				break;
			case 35:
				addmp = rnd.nextInt(7) + 11;
				break;
			case 36:
			case 37:
			case 38:
				addmp = rnd.nextInt(9) + 11;
				break;
			case 39:
				addmp = rnd.nextInt(10) + 11;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(8) + 13;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(10) + 13;
				break;
			case 45:
				addmp = rnd.nextInt(10) + 14;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(10) + 14;
			break;

		case 5:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(2) + 2;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(2) + 3;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(3) + 3;
				break;
			case 24:
				addmp = rnd.nextInt(4) + 3;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(3) + 4;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 36:
			case 37:
			case 38:
				addmp = rnd.nextInt(5) + 5;
				break;
			case 39:
				addmp = rnd.nextInt(6) + 5;
				break;
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(5) + 6;
				break;
			case 45:
				addmp = rnd.nextInt(5) + 7;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(5) + 7;
			break;
		case 6:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(4) + 6;
				break;
			case 18:
			case 19:
				addmp = rnd.nextInt(6) + 6;
				break;
			case 20:
				addmp = rnd.nextInt(5) + 7;
				break;
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(6) + 7;
				break;
			case 24:
				addmp = rnd.nextInt(8) + 7;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(6) + 9;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(8) + 9;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(8) + 11;
				break;
			case 33:
			case 34:
				addmp = rnd.nextInt(9) + 11;
				break;
			case 35:
				addmp = rnd.nextInt(8) + 12;
				break;
			case 36:
			case 37:
			case 38:
				addmp = rnd.nextInt(10) + 12;
				break;
			case 39:
				addmp = rnd.nextInt(12) + 12;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(10) + 14;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(11) + 14;
				break;
			case 45:
				addmp = rnd.nextInt(11) + 16;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(11) + 16;
			break;
		case 7:// ����
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				addmp = rnd.nextInt(2);
				break;
			case 9:
				addmp = rnd.nextInt(3);
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(2) + 1;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(2) + 2;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(3) + 2;
				break;
			case 24:
				addmp = rnd.nextInt(4) + 2;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(3) + 3;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(4) + 3;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(3) + 4;
				break;
			case 33:
			case 34:
			case 35:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				addmp = rnd.nextInt(5) + 4;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(5) + 5;
				break;
			case 45:
				addmp = rnd.nextInt(5) + 6;
				break;
			default:
				break;

			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(5) + 6;
			break;
		case 8:// �˻�
			switch (wis) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				addmp = rnd.nextInt(3);
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				addmp = rnd.nextInt(2) + 1;
				break;
			case 15:
			case 16:
			case 17:
				addmp = rnd.nextInt(2) + 2;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				addmp = rnd.nextInt(3) + 2;
				break;
			case 24:
				addmp = rnd.nextInt(4) + 2;
				break;
			case 25:
			case 26:
				addmp = rnd.nextInt(3) + 3;
				break;
			case 27:
			case 28:
			case 29:
				addmp = rnd.nextInt(4) + 3;
				break;
			case 30:
			case 31:
			case 32:
				addmp = rnd.nextInt(3) + 4;
				break;
			case 33:
			case 34:
			case 35:
				addmp = rnd.nextInt(4) + 4;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				addmp = rnd.nextInt(5) + 4;
				break;
			case 40:
			case 41:
				addmp = rnd.nextInt(4) + 5;
				break;
			case 42:
			case 43:
			case 44:
				addmp = rnd.nextInt(5) + 5;
				break;
			case 45:
				addmp = rnd.nextInt(5) + 6;
				break;
			default:
				break;
			}
			;
			if (wis > 45)
				addmp = rnd.nextInt(5) + 6;
			break;
		default:
			break;
		}

		if (wis <= 0) {
			addmp = rnd.nextInt(2);
		}

		switch (charType) {
		case 0:
			if (baseMaxMp + addmp > Config.PRINCE_MAX_MP) {
				addmp = (Config.PRINCE_MAX_MP - baseMaxMp);
			}
			break;// ����
		case 1:
			if (baseMaxMp + addmp > Config.KNIGHT_MAX_MP) {
				addmp = (Config.KNIGHT_MAX_MP - baseMaxMp);
			}
			break;// ���
		case 2:
			if (baseMaxMp + addmp > Config.ELF_MAX_MP) {
				addmp = (Config.ELF_MAX_MP - baseMaxMp);
			}
			break;// ����
		case 3:
			if (baseMaxMp + addmp > Config.WIZARD_MAX_MP) {
				addmp = (Config.WIZARD_MAX_MP - baseMaxMp);
			}
			break;// ����
		case 4:
			if (baseMaxMp + addmp > Config.DARKELF_MAX_MP) {
				addmp = (Config.DARKELF_MAX_MP - baseMaxMp);
			}
			break;// �ٿ�
		case 5:
			if (baseMaxMp + addmp > Config.DRAGONKNIGHT_MAX_MP) {
				addmp = (Config.DRAGONKNIGHT_MAX_MP - baseMaxMp);
			}
			break;// ����
		case 6:
			if (baseMaxMp + addmp > Config.BLACKWIZARD_MAX_MP) {
				addmp = (Config.BLACKWIZARD_MAX_MP - baseMaxMp);
			}
			break;// ȯ����
		case 7:
			if (baseMaxMp + addmp > Config.KNIGHT_MAX_MP) {
				addmp = (Config.KNIGHT_MAX_MP - baseMaxMp);
			}
			break;// ����
		case 8:
			if (baseMaxMp + addmp > Config.KNIGHT_MAX_MP) {
				addmp = (Config.KNIGHT_MAX_MP - baseMaxMp);
			}
			break;// �˻�
		default:
			break;
		}

		return addmp;
	}

	public static int ��������������(int charType, int totalCon) {
		int startcon = 0;
		int starthp = 0;
		int returnhp = 0;
		switch (charType) {
		case 0:
			startcon = 11;
			starthp = 12;
			break;// ����
		case 1:
			startcon = 16;
			starthp = 21;
			break;// ���
		case 2:
			startcon = 12;
			starthp = 10;
			break;// ����
		case 3:
			startcon = 12;
			starthp = 7;
			break;// ����
		case 4:
			startcon = 12;
			starthp = 11;
			break;// �ٿ�
		case 5:
			startcon = 14;
			starthp = 15;
			break;// ����
		case 6:
			startcon = 12;
			starthp = 9;
			break;// ȯ����
		case 7:
			startcon = 16;
			starthp = 21;
			break;// ����
		case 8:
			startcon = 15;
			starthp = 21;
			break;// �˻�
		default:
			break;
		}
		int _25�̻� = 0;
		int _25���� = 0;
		if (totalCon > 25) {
			_25���� = 25 - startcon;
			_25�̻� = (totalCon - 25) / 2;
		} else {
			_25���� = totalCon - startcon;
		}

		if (_25���� != 0) {
			returnhp += (starthp + _25����);
		}

		if (_25�̻� != 0) {
			returnhp += _25�̻�;
		}

		return returnhp;
	}

	public static int ��������(int charType, int baseMaxHp, int totalCon) {
		int startcon = 0;
		int starthp = 0;
		int returnhp = 0;
		switch (charType) {
		case 0:
			startcon = 11;
			starthp = 12;
			break;// ����
		case 1:
			startcon = 16;
			starthp = 21;
			break;// ���
		case 2:
			startcon = 12;
			starthp = 10;
			break;// ����
		case 3:
			startcon = 12;
			starthp = 7;
			break;// ����
		case 4:
			startcon = 12;
			starthp = 11;
			break;// �ٿ�
		case 5:
			startcon = 14;
			starthp = 15;
			break;// ����
		case 6:
			startcon = 12;
			starthp = 9;
			break;// ȯ����
		case 7:
			startcon = 16;
			starthp = 21;
			break;// ����
		case 8:
			startcon = 15;
			starthp = 21;
			break;// �˻�
		default:
			break;
		}

		int calcstat = totalCon - startcon;

		if (calcstat <= 0) {
			returnhp = starthp + rnd.nextInt(2);
		}
		returnhp = starthp + (calcstat + rnd.nextInt(2));

		switch (charType) {
		case 0:
			if (baseMaxHp + returnhp > Config.PRINCE_MAX_HP) {
				returnhp = (Config.PRINCE_MAX_HP - baseMaxHp);
			}
			break;// ����
		case 1:
			if (baseMaxHp + returnhp > Config.KNIGHT_MAX_HP) {
				returnhp = (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;// ���
		case 2:
			if (baseMaxHp + returnhp > Config.ELF_MAX_HP) {
				returnhp = (Config.ELF_MAX_HP - baseMaxHp);
			}
			break;// ����
		case 3:
			if (baseMaxHp + returnhp > Config.WIZARD_MAX_HP) {
				returnhp = (Config.WIZARD_MAX_HP - baseMaxHp);
			}
			break;// ����
		case 4:
			if (baseMaxHp + returnhp > Config.DARKELF_MAX_HP) {
				returnhp = (Config.DARKELF_MAX_HP - baseMaxHp);
			}
			break;// �ٿ�
		case 5:
			if (baseMaxHp + returnhp > Config.DRAGONKNIGHT_MAX_HP) {
				returnhp = (Config.DRAGONKNIGHT_MAX_HP - baseMaxHp);
			}
			break;// ����
		case 6:
			if (baseMaxHp + returnhp > Config.BLACKWIZARD_MAX_HP) {
				returnhp = (Config.BLACKWIZARD_MAX_HP - baseMaxHp);
			}
			break;// ȯ����
		case 7:
			if (baseMaxHp + returnhp > Config.KNIGHT_MAX_HP) {
				returnhp = (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;// ����
		case 8:
			if (baseMaxHp + returnhp > Config.KNIGHT_MAX_HP) {
				returnhp = (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;// �˻�
		default:
			break;
		}

		return returnhp;
	}


	public static int getMaxWeight(int str, int con)
	{
		return �ִ��������(str, con);
	}
	public static int �ִ��������(int str, int con)
	{
		int maxWeight = 1000;
		try
		{
			int stat = (str + con) / 2;
			if (stat <= 0)
				return maxWeight;
			maxWeight += stat * 100;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return maxWeight;
	}
	

	public static int �ٰŸ������(int str)
	{
		try
		{
			return strDamage[str];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int �ٰŸ�����(int str)
	{
		try
		{
			return strHit[str];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int �ٰŸ�ġ��Ÿ(int str)
	{
		try
		{
			return strCritical[str];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}

	
	public static int ���������(int inte)
	{
		try
		{
			return intDamage[inte];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int ��������(int inte)
	{
		try
		{
			return intHit[inte];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int ����ġ��Ÿ(int inte)
	{
		try
		{
			return intCritical[inte];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int �������ʽ�(int inte)
	{
		try
		{
			return intBonus[inte];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int ���Ҹ𰨼�(int inte)
	{
		try
		{
			if(inte > 55)
				inte = 55;
			return inte * 30 / 55;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}


	public static int ���Ÿ������(int dex)
	{
		try
		{
			return dexDamage[dex];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int ���Ÿ�����(int dex)
	{
		try
		{
			return dexHit[dex];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	public static int ���Ÿ�ġ��Ÿ(int dex)
	{
		try
		{
			return dexCritical[dex];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}

	public static int ��������(int dex)
	{
		try
		{
			return dexAC[dex];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 10;
		}
	}
	public static int ���Ÿ�ȸ��(int dex)
	{
		try
		{
			return dexER[dex];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -10;
		}
	}
	
	
	public static int ��ȸ��ƽ(int wis)
	{
		try
		{
			return wisMPȸ��[wis];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	}
	public static int ���ǹ���ȸ��(int wis)
	{
		try
		{
			return wis����ȸ��[wis];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	}
	public static int �������(int type, int wis)
	{
		try
		{
			int base = 0;
			switch (type)
			{
				case 0:
					base = 10;
					break;// ����
				case 1:
					base = 0;
					break;// ���
				case 2:
					base = 25;
					break;// ����
				case 3:
					base = 15;
					break;// ����
				case 4:
					base = 10;
					break;// �ٿ�
				case 5:
					base = 18;
					break;// ����
				case 6:
					base = 20;
					break;// ȯ����
				case 7:
					base = 0;
					break;// ����
				case 8:
					base = 14;
					break;// �˻�
				default:
					break;
			}
			return base + wisMR[wis];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	public static int ��ȸ��ƽ(int con)
	{
		try
		{
			return conHPȸ��[con];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 5;
		}
	}
	public static int ����ȸ������(int con) 
	{
		try
		{
			return con����ȸ��[con];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}
}
