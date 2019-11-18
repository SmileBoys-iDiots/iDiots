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
	
	private static int[] conHP증가 = new int[128];
	private static int[] conHP회복 = new int[128];
	private static int[] con물약회복 = new int[128];
	
	private static int[] wisMP증가 = new int[128];
	private static int[] wisMP회복 = new int[128];
	private static int[] wis물약회복 = new int[128];
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
		// 초기화
		for (int i = 0; i < 128; i++)
		{
			strDamage[i] = 0;
			strHit[i] = 0;
			strCritical[i] = 0;
		}
		
		int dmg = 1; 
		for (int i = 8; i < 128; i++) // 8부터 2레벨 마다 1씩 증가
		{ 
			if (i % 2 == 0)
			{
				dmg++;
			}
			strDamage[i] = dmg;
		}

		int hit = 4;
		int addCount = 0;
		for (int i = 8; i < 128; i++) // 8부터 두레벨 구간마다 1씩 증가
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
		for (int i = 40; i < 128; i++) // 40부터 10레벨마다 1씩 증가
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
		// 초기화
		for (int i = 0; i < 128; i++)
		{
			dexDamage[i] = 0;
			dexHit[i] = -3;
			dexCritical[i] = 0;
			dexAC[i] = -1;
			dexER[i] = 0;
		}

		int dmg = 1; 
		for (int i = 6; i < 128; i++) // 6부터 3레벨 마다 1씩 증가
		{ 
			if (i % 3 == 0)
			{
				dmg++;
			}
			dexDamage[i] = dmg;
		}

		int hit = -3; 
		for (int i = 7; i < 128; i++) // 7부터 1레벨 마다 1씩 증가
		{ 
			dexHit[i] = hit;
			hit++;
		}

		int cri = 0; 
		for (int i = 40; i < 128; i++) // 40부터 10레벨마다 1씩 증가
		{
			if (i % 10 == 0)
			{
				cri++;
			}
			dexCritical[i] = cri;
		}

		int ac = -1; 
		for (int i = 6; i < 128; i++) // 6부터 3레벨 마다 1씩 증가
		{
			if (i % 3 == 0)
			{
				ac--;
			}
			dexAC[i] = ac;
		}

		int er = 2; 
		for (int i = 6; i < 44; i++) // 6부터 2레벨 마다 1씩 증가
		{
			if (i % 2 == 0)
			{
				er++;
			}
			dexER[i] = er;
		}
		for (int i = 44; i < 128; i++) // 44부터 3레벨 마다 1씩 증가
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
		// 초기화
		for (int i = 0; i < 128; i++)
		{
			dexDamage[i] = 0;
			dexHit[i] = -3;
			dexCritical[i] = 0;
			dexAC[i] = -1;
			dexER[i] = 0;
		}

		int dmg = 1; 
		for (int i = 6; i < 128; i++) // 6부터 3레벨 마다 1씩 증가
		{ 
			if (i % 3 == 0)
			{
				dmg++;
			}
			dexDamage[i] = dmg;
		}

		int hit = -3; 
		for (int i = 7; i < 128; i++) // 7부터 1레벨 마다 1씩 증가
		{ 
			dexHit[i] = hit;
			hit++;
		}

		int cri = 0; 
		for (int i = 40; i < 128; i++) // 40부터 10레벨마다 1씩 증가
		{
			if (i % 10 == 0)
			{
				cri++;
			}
			dexCritical[i] = cri;
		}

		int ac = -1; 
		for (int i = 6; i < 128; i++) // 6부터 3레벨 마다 1씩 증가
		{
			if (i % 3 == 0)
			{
				ac--;
			}
			dexAC[i] = ac;
		}

		int er = 2; 
		for (int i = 6; i < 44; i++) // 6부터 2레벨 마다 1씩 증가
		{
			if (i % 2 == 0)
			{
				er++;
			}
			dexER[i] = er;
		}
		for (int i = 44; i < 128; i++) // 44부터 3레벨 마다 1씩 증가
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
		// 초기화
		for (int i = 0; i < 128; i++)
		{
			conHP증가[i] = 8;
			conHP회복[i] = 5;
			con물약회복[i] = 0;
		}

		for (int i = 10; i < 128; i++) // 8부터 1레벨 마다 1씩 증가
		{
			conHP증가[i] = i - 2;
		}

		int HP회복 = 0; 
		for (int i = 10; i < 128; i++) // 10부터 레벨 / 2 증가
		{
			HP회복 = i / 2;
			if(i >= 40)
			{
				HP회복 += (i- 40) / 2;
			}
			conHP회복[i] = HP회복;
		}

		int 물약회복 = 0; 
		for (int i = 20; i < 128; i++) // 20부터 5레벨마다 1씩 증가
		{
			if (i % 5 == 0)
			{
				물약회복++;
			}
			con물약회복[i] = 물약회복;
		}
	}
	private static void ResetAbilityBonus_WIS()
	{
		// 초기화
		for (int i = 0; i < 128; i++)
		{
			wisMP증가[i] = 0;
			wisMP회복[i] = 1;
			wis물약회복[i] = 1;
			wisMR[i] = 0;
		}

		int MP증가 = 0; 
		for (int i = 15; i < 128; i++) // 15부터 5레벨 마다 1씩 증가
		{ 
			if (i % 5 == 0)
			{
				MP증가++;
			}
			wisMP증가[i] = MP증가;
		}

		int MP회복 = 1; 
		for (int i = 10; i < 128; i++) // 10부터 5레벨 마다 1씩 증가
		{ 
			if (i % 5 == 0)
			{
				MP회복++;
			}
			wisMP회복[i] = MP회복;
		}

		int 물약회복 = 1; 
		for (int i = 11; i < 128; i++) // 11부터 2레벨마다 1씩 증가
		{
			if (i % 2 == 0)
			{
				물약회복++;
			}
			wis물약회복[i] = 물약회복;
		}

		for (int i = 10; i < 128; i++) // 10부터 1레벨마다 4씩 증가
		{
			wisMR[i] = (i - 10) * 4;
		}
	}

	
	public static int[] 레벨업엠피증가량(int charType, int wis) {
		int 최소엠피 = 0;
		int 최대엠피 = 0;
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
				최소엠피 = 3;
				최대엠피 = 4;
				break;
			case 12:
			case 13:
			case 14:
				최소엠피 = 3;
				최대엠피 = 5;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 4;
				최대엠피 = 6;
				break;
			case 18:
			case 19:
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 20:
				최소엠피 = 5;
				최대엠피 = 7;
				break;
			case 21:
			case 22:
			case 23:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 24:
				최소엠피 = 5;
				최대엠피 = 9;
				break;
			case 25:
			case 26:
				최소엠피 = 6;
				최대엠피 = 9;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 6;
				최대엠피 = 10;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 7;
				최대엠피 = 11;
				break;
			case 33:
			case 34:
				최소엠피 = 7;
				최대엠피 = 12;
				break;
			case 35:
				최소엠피 = 8;
				최대엠피 = 12;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 8;
				최대엠피 = 13;
				break;
			case 39:
				최소엠피 = 8;
				최대엠피 = 14;
				break;
			case 40:
			case 41:
				최소엠피 = 9;
				최대엠피 = 14;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 9;
				최대엠피 = 15;
				break;
			case 45:
				최소엠피 = 10;
				최대엠피 = 16;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 10;
				최대엠피 = 16;
			}
			break;// 군주
		case 1:// 기사
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
				최소엠피 = 0;
				최대엠피 = 2;
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				최소엠피 = 1;
				최대엠피 = 2;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 2;
				최대엠피 = 3;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				최소엠피 = 2;
				최대엠피 = 4;
				break;
			case 24:
				최소엠피 = 2;
				최대엠피 = 5;
				break;
			case 25:
			case 26:
				최소엠피 = 3;
				최대엠피 = 5;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 3;
				최대엠피 = 6;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 4;
				최대엠피 = 6;
				break;
			case 33:
			case 34:
			case 35:
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				최소엠피 = 4;
				최대엠피 = 8;
				break;
			case 40:
			case 41:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 5;
				최대엠피 = 9;
				break;
			case 45:
				최소엠피 = 6;
				최대엠피 = 10;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 6;
				최대엠피 = 10;
			}
			break;

		case 2:// 요정
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
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 18:
			case 19:
				최소엠피 = 5;
				최대엠피 = 10;
				break;
			case 20:
				최소엠피 = 7;
				최대엠피 = 10;
				break;
			case 21:
			case 22:
			case 23:
				최소엠피 = 7;
				최대엠피 = 11;
				break;
			case 24:
				최소엠피 = 7;
				최대엠피 = 13;
				break;
			case 25:
			case 26:
				최소엠피 = 8;
				최대엠피 = 13;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 8;
				최대엠피 = 14;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 10;
				최대엠피 = 16;
				break;
			case 33:
			case 34:
				최소엠피 = 10;
				최대엠피 = 17;
				break;
			case 35:
				최소엠피 = 11;
				최대엠피 = 17;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 11;
				최대엠피 = 19;
				break;
			case 39:
				최소엠피 = 11;
				최대엠피 = 20;
				break;
			case 40:
			case 41:
				최소엠피 = 13;
				최대엠피 = 20;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 13;
				최대엠피 = 21;
				break;
			case 45:
				최소엠피 = 14;
				최대엠피 = 23;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 14;
				최대엠피 = 23;
			}
			break;

		case 3:// 법사
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
				최소엠피 = 6;
				최대엠피 = 10;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 8;
				최대엠피 = 12;
				break;
			case 18:
			case 19:
				최소엠피 = 8;
				최대엠피 = 14;
				break;
			case 20:
				최소엠피 = 10;
				최대엠피 = 14;
				break;
			case 21:
			case 22:
			case 23:
				최소엠피 = 10;
				최대엠피 = 16;
				break;
			case 24:
				최소엠피 = 10;
				최대엠피 = 18;
				break;
			case 25:
			case 26:
				최소엠피 = 12;
				최대엠피 = 18;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 12;
				최대엠피 = 20;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 14;
				최대엠피 = 22;
				break;
			case 33:
			case 34:
				최소엠피 = 14;
				최대엠피 = 24;
				break;
			case 35:
				최소엠피 = 16;
				최대엠피 = 24;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 16;
				최대엠피 = 26;
				break;
			case 39:
				최소엠피 = 16;
				최대엠피 = 28;
				break;
			case 40:
			case 41:
				최소엠피 = 18;
				최대엠피 = 28;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 18;
				최대엠피 = 30;
				break;
			case 45:
				최소엠피 = 20;
				최대엠피 = 32;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 20;
				최대엠피 = 32;
			}
			break;

		case 4:// 다엘
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
				최소엠피 = 4;
				최대엠피 = 5;
				break;
			case 12:
			case 13:
			case 14:
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 18:
			case 19:
				최소엠피 = 5;
				최대엠피 = 10;
				break;
			case 20:
				최소엠피 = 7;
				최대엠피 = 10;
				break;
			case 21:
			case 22:
			case 23:
				최소엠피 = 7;
				최대엠피 = 11;
				break;
			case 24:
				최소엠피 = 7;
				최대엠피 = 13;
				break;
			case 25:
			case 26:
				최소엠피 = 8;
				최대엠피 = 13;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 8;
				최대엠피 = 14;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 10;
				최대엠피 = 16;
				break;
			case 33:
			case 34:
				최소엠피 = 10;
				최대엠피 = 17;
				break;
			case 35:
				최소엠피 = 11;
				최대엠피 = 17;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 11;
				최대엠피 = 19;
				break;
			case 39:
				최소엠피 = 11;
				최대엠피 = 20;
				break;
			case 40:
			case 41:
				최소엠피 = 13;
				최대엠피 = 20;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 13;
				최대엠피 = 22;
				break;
			case 45:
				최소엠피 = 14;
				최대엠피 = 23;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 14;
				최대엠피 = 23;
			}
			break;

		case 5:// 용기사
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
				최소엠피 = 2;
				최대엠피 = 3;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 3;
				최대엠피 = 4;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				최소엠피 = 3;
				최대엠피 = 5;
				break;
			case 24:
				최소엠피 = 3;
				최대엠피 = 6;
				break;
			case 25:
			case 26:
				최소엠피 = 4;
				최대엠피 = 6;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 5;
				최대엠피 = 9;
				break;
			case 39:
				최소엠피 = 5;
				최대엠피 = 10;
				break;
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
				최소엠피 = 6;
				최대엠피 = 10;
				break;
			case 45:
				최소엠피 = 7;
				최대엠피 = 11;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 7;
				최대엠피 = 11;
			}
			break;
		case 6:// 용기사
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
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 6;
				최대엠피 = 9;
				break;
			case 18:
			case 19:
				최소엠피 = 6;
				최대엠피 = 11;
				break;
			case 20:
				최소엠피 = 7;
				최대엠피 = 11;
				break;
			case 21:
			case 22:
			case 23:
				최소엠피 = 7;
				최대엠피 = 12;
				break;
			case 24:
				최소엠피 = 7;
				최대엠피 = 14;
				break;
			case 25:
			case 26:
				최소엠피 = 9;
				최대엠피 = 14;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 9;
				최대엠피 = 16;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 11;
				최대엠피 = 18;
				break;
			case 33:
			case 34:
				최소엠피 = 11;
				최대엠피 = 19;
				break;
			case 35:
				최소엠피 = 12;
				최대엠피 = 19;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 12;
				최대엠피 = 21;
				break;
			case 39:
				최소엠피 = 12;
				최대엠피 = 23;
				break;
			case 40:
			case 41:
				최소엠피 = 14;
				최대엠피 = 23;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 14;
				최대엠피 = 24;
				break;
			case 45:
				최소엠피 = 16;
				최대엠피 = 26;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 16;
				최대엠피 = 26;
			}
			break;
		case 7:// 용기사
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
				최소엠피 = 0;
				최대엠피 = 1;
				break;
			case 9:
				최소엠피 = 0;
				최대엠피 = 2;
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				최소엠피 = 1;
				최대엠피 = 2;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 2;
				최대엠피 = 3;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				최소엠피 = 2;
				최대엠피 = 4;
				break;
			case 24:
				최소엠피 = 2;
				최대엠피 = 5;
				break;
			case 25:
			case 26:
				최소엠피 = 3;
				최대엠피 = 5;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 3;
				최대엠피 = 6;
				break;
			case 30:
			case 31:
			case 32:
				최소엠피 = 4;
				최대엠피 = 6;
				break;
			case 33:
			case 34:
			case 35:
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 36:
			case 37:
			case 38:
			case 39:
				최소엠피 = 4;
				최대엠피 = 8;
				break;
			case 40:
			case 41:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 42:
			case 43:
			case 44:
				최소엠피 = 5;
				최대엠피 = 9;
				break;
			case 45:
				최소엠피 = 6;
				최대엠피 = 10;
				break;
			default:
				break;

			}
			;
			if (wis > 45) {
				최소엠피 = 6;
				최대엠피 = 10;
			}
			break;
		case 8:// 검사
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
				최소엠피 = 2;
				최대엠피 = 3;
				break;
			case 15:
			case 16:
			case 17:
				최소엠피 = 3;
				최대엠피 = 4;
				break;
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				최소엠피 = 3;
				최대엠피 = 5;
				break;
			case 24:
				최소엠피 = 3;
				최대엠피 = 6;
				break;
			case 25:
			case 26:
				최소엠피 = 4;
				최대엠피 = 6;
				break;
			case 27:
			case 28:
			case 29:
				최소엠피 = 4;
				최대엠피 = 7;
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
				최소엠피 = 5;
				최대엠피 = 8;
				break;
			case 36:
			case 37:
			case 38:
				최소엠피 = 5;
				최대엠피 = 9;
				break;
			case 39:
				최소엠피 = 5;
				최대엠피 = 10;
				break;
			case 40:
			case 41:
			case 42:
			case 43:
			case 44:
				최소엠피 = 6;
				최대엠피 = 10;
				break;
			case 45:
				최소엠피 = 7;
				최대엠피 = 11;
				break;
			default:
				break;
			}
			;
			if (wis > 45) {
				최소엠피 = 7;
				최대엠피 = 11;
			}
			break;
		default:
			break;
		}

		if (wis <= 0) {
			최소엠피 = 0;
			최대엠피 = 1;
		}

		int[] bbb = { 최소엠피, 최대엠피 };

		return bbb;
	}

	public static int 레벨업엠피(int charType, int baseMaxMp, int wis) {
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
			break;// 군주

		case 1:// 기사
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

		case 2:// 요정
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

		case 3:// 법사
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

		case 4:// 다엘
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

		case 5:// 용기사
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
		case 6:// 용기사
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
		case 7:// 용기사
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
		case 8:// 검사
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
			break;// 군주
		case 1:
			if (baseMaxMp + addmp > Config.KNIGHT_MAX_MP) {
				addmp = (Config.KNIGHT_MAX_MP - baseMaxMp);
			}
			break;// 기사
		case 2:
			if (baseMaxMp + addmp > Config.ELF_MAX_MP) {
				addmp = (Config.ELF_MAX_MP - baseMaxMp);
			}
			break;// 요정
		case 3:
			if (baseMaxMp + addmp > Config.WIZARD_MAX_MP) {
				addmp = (Config.WIZARD_MAX_MP - baseMaxMp);
			}
			break;// 법사
		case 4:
			if (baseMaxMp + addmp > Config.DARKELF_MAX_MP) {
				addmp = (Config.DARKELF_MAX_MP - baseMaxMp);
			}
			break;// 다엘
		case 5:
			if (baseMaxMp + addmp > Config.DRAGONKNIGHT_MAX_MP) {
				addmp = (Config.DRAGONKNIGHT_MAX_MP - baseMaxMp);
			}
			break;// 용기사
		case 6:
			if (baseMaxMp + addmp > Config.BLACKWIZARD_MAX_MP) {
				addmp = (Config.BLACKWIZARD_MAX_MP - baseMaxMp);
			}
			break;// 환술사
		case 7:
			if (baseMaxMp + addmp > Config.KNIGHT_MAX_MP) {
				addmp = (Config.KNIGHT_MAX_MP - baseMaxMp);
			}
			break;// 전사
		case 8:
			if (baseMaxMp + addmp > Config.KNIGHT_MAX_MP) {
				addmp = (Config.KNIGHT_MAX_MP - baseMaxMp);
			}
			break;// 검사
		default:
			break;
		}

		return addmp;
	}

	public static int 레벨업피증가량(int charType, int totalCon) {
		int startcon = 0;
		int starthp = 0;
		int returnhp = 0;
		switch (charType) {
		case 0:
			startcon = 11;
			starthp = 12;
			break;// 군주
		case 1:
			startcon = 16;
			starthp = 21;
			break;// 기사
		case 2:
			startcon = 12;
			starthp = 10;
			break;// 요정
		case 3:
			startcon = 12;
			starthp = 7;
			break;// 법사
		case 4:
			startcon = 12;
			starthp = 11;
			break;// 다엘
		case 5:
			startcon = 14;
			starthp = 15;
			break;// 용기사
		case 6:
			startcon = 12;
			starthp = 9;
			break;// 환술사
		case 7:
			startcon = 16;
			starthp = 21;
			break;// 전사
		case 8:
			startcon = 15;
			starthp = 21;
			break;// 검사
		default:
			break;
		}
		int _25이상 = 0;
		int _25까지 = 0;
		if (totalCon > 25) {
			_25까지 = 25 - startcon;
			_25이상 = (totalCon - 25) / 2;
		} else {
			_25까지 = totalCon - startcon;
		}

		if (_25까지 != 0) {
			returnhp += (starthp + _25까지);
		}

		if (_25이상 != 0) {
			returnhp += _25이상;
		}

		return returnhp;
	}

	public static int 레벨업피(int charType, int baseMaxHp, int totalCon) {
		int startcon = 0;
		int starthp = 0;
		int returnhp = 0;
		switch (charType) {
		case 0:
			startcon = 11;
			starthp = 12;
			break;// 군주
		case 1:
			startcon = 16;
			starthp = 21;
			break;// 기사
		case 2:
			startcon = 12;
			starthp = 10;
			break;// 요정
		case 3:
			startcon = 12;
			starthp = 7;
			break;// 법사
		case 4:
			startcon = 12;
			starthp = 11;
			break;// 다엘
		case 5:
			startcon = 14;
			starthp = 15;
			break;// 용기사
		case 6:
			startcon = 12;
			starthp = 9;
			break;// 환술사
		case 7:
			startcon = 16;
			starthp = 21;
			break;// 전사
		case 8:
			startcon = 15;
			starthp = 21;
			break;// 검사
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
			break;// 군주
		case 1:
			if (baseMaxHp + returnhp > Config.KNIGHT_MAX_HP) {
				returnhp = (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;// 기사
		case 2:
			if (baseMaxHp + returnhp > Config.ELF_MAX_HP) {
				returnhp = (Config.ELF_MAX_HP - baseMaxHp);
			}
			break;// 요정
		case 3:
			if (baseMaxHp + returnhp > Config.WIZARD_MAX_HP) {
				returnhp = (Config.WIZARD_MAX_HP - baseMaxHp);
			}
			break;// 법사
		case 4:
			if (baseMaxHp + returnhp > Config.DARKELF_MAX_HP) {
				returnhp = (Config.DARKELF_MAX_HP - baseMaxHp);
			}
			break;// 다엘
		case 5:
			if (baseMaxHp + returnhp > Config.DRAGONKNIGHT_MAX_HP) {
				returnhp = (Config.DRAGONKNIGHT_MAX_HP - baseMaxHp);
			}
			break;// 용기사
		case 6:
			if (baseMaxHp + returnhp > Config.BLACKWIZARD_MAX_HP) {
				returnhp = (Config.BLACKWIZARD_MAX_HP - baseMaxHp);
			}
			break;// 환술사
		case 7:
			if (baseMaxHp + returnhp > Config.KNIGHT_MAX_HP) {
				returnhp = (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;// 전사
		case 8:
			if (baseMaxHp + returnhp > Config.KNIGHT_MAX_HP) {
				returnhp = (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;// 검사
		default:
			break;
		}

		return returnhp;
	}


	public static int getMaxWeight(int str, int con)
	{
		return 최대소지무게(str, con);
	}
	public static int 최대소지무게(int str, int con)
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
	

	public static int 근거리대미지(int str)
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
	public static int 근거리명중(int str)
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
	public static int 근거리치명타(int str)
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

	
	public static int 마법대미지(int inte)
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
	public static int 마법명중(int inte)
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
	public static int 마법치명타(int inte)
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
	public static int 마법보너스(int inte)
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
	public static int 엠소모감소(int inte)
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


	public static int 원거리대미지(int dex)
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
	public static int 원거리명중(int dex)
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
	public static int 원거리치명타(int dex)
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

	public static int 물리방어력(int dex)
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
	public static int 원거리회피(int dex)
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
	
	
	public static int 엠회복틱(int wis)
	{
		try
		{
			return wisMP회복[wis];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	}
	public static int 엠피물약회복(int wis)
	{
		try
		{
			return wis물약회복[wis];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	}
	public static int 마법방어(int type, int wis)
	{
		try
		{
			int base = 0;
			switch (type)
			{
				case 0:
					base = 10;
					break;// 군주
				case 1:
					base = 0;
					break;// 기사
				case 2:
					base = 25;
					break;// 요정
				case 3:
					base = 15;
					break;// 법사
				case 4:
					base = 10;
					break;// 다엘
				case 5:
					base = 18;
					break;// 용기사
				case 6:
					base = 20;
					break;// 환술사
				case 7:
					base = 0;
					break;// 전사
				case 8:
					base = 14;
					break;// 검사
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

	public static int 피회복틱(int con)
	{
		try
		{
			return conHP회복[con];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 5;
		}
	}
	public static int 물약회복증가(int con) 
	{
		try
		{
			return con물약회복[con];
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}
}
