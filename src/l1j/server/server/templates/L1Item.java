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
package l1j.server.server.templates;

import java.io.Serializable;

public abstract class L1Item implements Serializable {

	private static final long serialVersionUID = 1L;

	public L1Item() {
	}

	// ■■■■■■ L1EtcItem, L1Weapon, L1Armor 에 공통되는 항목 ■■■■■■

	private int _type2; // 0=L1EtcItem, 1=L1Weapon, 2=L1Armor

	/**
	 * @return 0 if L1EtcItem, 1 if L1Weapon, 2 if L1Armor
	 */
	public int getType2() {
		return _type2;
	}

	public void setType2(int type) {
		_type2 = type;
	}

	private int _itemId;

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int itemId) {
		_itemId = itemId;
	}

	private int _magiclv;

	public int getmagiclv() {
		return _magiclv;
	}

	public void setmagiclv(int lv) {
		_magiclv = lv;
	}

	private String _name;

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	private String _Magic_name;

	public String getMagicName() {
		return _Magic_name;
	}

	public void setMagicName(String name) {
		_Magic_name = name;
	}

	private String _nameId;

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(String nameid) {
		_nameId = nameid;
	}

	private int _type;

	/**
	 * 아이템의 종류를 돌려준다.<br>
	 * 
	 * @return <p>
	 *         [etcitem]<br>
	 *         0:arrow, 1:wand, 2:light, 3:gem, 4:totem, 5:firecracker,
	 *         6:potion, 7:food, 8:scroll, 9:questitem, 10:spellbook,
	 *         11:petitem, 12:other, 13:material, 14:event, 15:sting
	 *         </p>
	 *         <p>
	 *         [weapon]<br>
	 *         1:sword, 2:dagger, 3:tohandsword, 4:bow, 5:spear, 6:blunt,
	 *         7:staff, 8:throwingknife, 9:arrow, 10:gauntlet, 11:claw,
	 *         12:edoryu, 13:singlebow, 14:singlespear, 15:tohandblunt,
	 *         16:tohandstaff, 17:kiringku 18chainsword
	 *         </p>
	 *         <p>
	 *         [armor]<br>
	 *         1:helm, 2:armor, 3:T, 4:cloak, 5:glove, 6:boots, 7:shield,
	 *         8:amulet, 9:ring, 10:belt, 11:ring2, 12:earring, 13:garder,
	 *         14:ron[룬추가] , 15:각반
	 */
	public int getType() {
		return _type;
	}

	public void setType(int type) {
		_type = type;
	}

	private int _type1;

	/**
	 * 아이템의 종류를 돌려준다.<br>
	 * 
	 * @return <p>
	 *         [weapon]<br>
	 *         sword:4, dagger:46, tohandsword:50, bow:20, blunt:11, spear:24,
	 *         staff:40, throwingknife:2922, arrow:66, gauntlet:62, claw:58,
	 *         edoryu:54, singlebow:20, singlespear:24, tohandblunt:11,
	 *         tohandstaff:40, kiringku:58, chainsword:24
	 *         </p>
	 */
	public int getType1() {
		return _type1;
	}

	public void setType1(int type1) {
		_type1 = type1;
	}

	private int _material;

	/**
	 * 아이템의 소재를 돌려준다
	 * 
	 * @return 0:none 1:액체 2:web 3:식물성 4:동물성 5:지 6:포 7:피 8:목 9:골 10:룡의 린 11:철
	 *         12:강철 13:동 14:은 15:금 16:플라티나 17:미스릴 18:브락크미스릴 19:유리 20:보석 21:광물
	 *         22:오리하르콘
	 */
	public int getMaterial() {
		return _material;
	}

	public void setMaterial(int material) {
		_material = material;
	}

	private int _weight;

	public int getWeight() {
		return _weight;
	}

	public void setWeight(int weight) {
		_weight = weight;
	}

	private int _gfxId;

	public int getGfxId() {
		return _gfxId;
	}

	public void setGfxId(int gfxId) {
		_gfxId = gfxId;
	}

	private int _Mainid;

	public int getMainId() {
		return _Mainid;
	}

	public void setMainId(int m) {
		_Mainid = m;
	}

	private int _Mainid2;

	public int getMainId2() {
		return _Mainid2;
	}

	public void setMainId2(int m) {
		_Mainid2 = m;
	}

	private int _Mainid3;

	public int getMainId3() {
		return _Mainid3;
	}

	public void setMainId3(int m) {
		_Mainid3 = m;
	}

	private int _SetId;

	public int getSetId() {
		return _SetId;
	}

	public void setSetId(int m) {
		_SetId = m;
	}

	private int _groundGfxId;

	public int getGroundGfxId() {
		return _groundGfxId;
	}

	public void setGroundGfxId(int groundGfxId) {
		_groundGfxId = groundGfxId;
	}

	private int _minLevel;

	private int _itemDescId;

	/**
	 * 감정시에 표시되는 ItemDesc.tbl의 메세지 ID를 돌려준다.
	 */
	public int getItemDescId() {
		return _itemDescId;
	}

	public void setItemDescId(int descId) {
		_itemDescId = descId;
	}

	public int getMinLevel() {
		return _minLevel;
	}

	public void setMinLevel(int level) {
		_minLevel = level;
	}

	private int _maxLevel;

	public int getMaxLevel() {
		return _maxLevel;
	}

	public void setMaxLevel(int maxlvl) {
		_maxLevel = maxlvl;
	}

	private int _bless;

	public int getBless() {
		return _bless;
	}

	public void setBless(int i) {
		_bless = i;
	}

	private boolean _tradable;

	public boolean isTradable() {
		return _tradable;
	}

	public void setTradable(boolean flag) {
		_tradable = flag;
	}

	private boolean _cantDelete;

	public boolean isCantDelete() {
		return _cantDelete;
	}

	public void setCantDelete(boolean flag) {
		_cantDelete = flag;
	}

	private boolean _save_at_once;

	/**
	 * 아이템의 개수가 변화했을 때에 곧바로 DB에 기입해야할 것인가를 돌려준다.
	 */
	public boolean isToBeSavedAtOnce() {
		return _save_at_once;
	}

	public void setToBeSavedAtOnce(boolean flag) {
		_save_at_once = flag;
	}

	// ■■■■■■ L1EtcItem, L1Weapon 에 공통되는 항목 ■■■■■■

	private int _dmgSmall = 0;

	public int getDmgSmall() {
		return _dmgSmall;
	}

	public void setDmgSmall(int dmgSmall) {
		_dmgSmall = dmgSmall;
	}

	private int _dmgLarge = 0;

	public int getDmgLarge() {
		return _dmgLarge;
	}

	public void setDmgLarge(int dmgLarge) {
		_dmgLarge = dmgLarge;
	}

	// ■■■■■■ L1EtcItem, L1Armor 에 공통되는 항목 ■■■■■■

	// ■■■■■■ L1Weapon, L1Armor 에 공통되는 항목 ■■■■■■

	private int _safeEnchant = 0;

	public int get_safeenchant() {
		return _safeEnchant;
	}

	public void set_safeenchant(int safeenchant) {
		_safeEnchant = safeenchant;
	}

	private boolean _useRoyal = false;

	public boolean isUseRoyal() {
		return _useRoyal;
	}

	public void setUseRoyal(boolean flag) {
		_useRoyal = flag;
	}

	private boolean _useKnight = false;

	public boolean isUseKnight() {
		return _useKnight;
	}

	public void setUseKnight(boolean flag) {
		_useKnight = flag;
	}

	private boolean _useWarrior = false;

	public boolean isUseWarrior() {
		return _useWarrior;
	}

	public void setUseWarrior(boolean flag) {
		_useWarrior = flag;
	}
	
	/**검사추가*/
	private boolean _useFencer = false;

	public boolean isUseFencer() {
		return _useFencer;
	}

	public void setUseFencer(boolean flag) {
		_useFencer = flag;
	}
	/**검사추가*/

	private boolean _useElf = false;

	public boolean isUseElf() {
		return _useElf;
	}

	public void setUseElf(boolean flag) {
		_useElf = flag;
	}

	private boolean _useMage = false;

	public boolean isUseMage() {
		return _useMage;
	}

	public void setUseMage(boolean flag) {
		_useMage = flag;
	}

	private boolean _useDarkelf = false;

	public boolean isUseDarkelf() {
		return _useDarkelf;
	}

	public void setUseDarkelf(boolean flag) {
		_useDarkelf = flag;
	}

	private boolean _useDragonKnight = false;

	public boolean isUseDragonKnight() {
		return _useDragonKnight;
	}

	public void setUseDragonKnight(boolean flag) {
		_useDragonKnight = flag;
	}

	private boolean _useBlackwizard = false;

	public boolean isUseBlackwizard() {
		return _useBlackwizard;
	}

	public void setUseBlackwizard(boolean flag) {
		_useBlackwizard = flag;
	}

	private boolean _useHighPet = false;

	public boolean isUseHighPet() {
		return _useHighPet;
	}

	public void setUseHighPet(boolean flag) {
		_useHighPet = flag;
	}

	private byte _addstr = 0;

	public byte get_addstr() {
		return _addstr;
	}

	public void set_addstr(byte addstr) {
		_addstr = addstr;
	}

	private byte _adddex = 0;

	public byte get_adddex() {
		return _adddex;
	}

	public void set_adddex(byte adddex) {
		_adddex = adddex;
	}

	private byte _addcon = 0;

	public byte get_addcon() {
		return _addcon;
	}

	public void set_addcon(byte addcon) {
		_addcon = addcon;
	}

	private byte _addint = 0;

	public byte get_addint() {
		return _addint;
	}

	public void set_addint(byte addint) {
		_addint = addint;
	}

	private byte _addwis = 0;

	public byte get_addwis() {
		return _addwis;
	}

	public void set_addwis(byte addwis) {
		_addwis = addwis;
	}

	private byte _addcha = 0;

	public byte get_addcha() {
		return _addcha;
	}

	public void set_addcha(byte addcha) {
		_addcha = addcha;
	}

	private int _addhp = 0;

	public int get_addhp() {
		return _addhp;
	}

	public void set_addhp(int addhp) {
		_addhp = addhp;
	}

	private int _addmp = 0;

	public int get_addmp() {
		return _addmp;
	}

	public void set_addmp(int addmp) {
		_addmp = addmp;
	}

	private int _addhpr = 0;

	public int get_addhpr() {
		return _addhpr;
	}

	public void set_addhpr(int addhpr) {
		_addhpr = addhpr;
	}

	private int _addmpr = 0;

	public int get_addmpr() {
		return _addmpr;
	}

	public void set_addmpr(int addmpr) {
		_addmpr = addmpr;
	}

	private int _addsp = 0;

	public int get_addsp() {
		return _addsp;
	}

	public void set_addsp(int addsp) {
		_addsp = addsp;
	}
	
	private int _critical = 0;
	
	public void set_addcri(int i)
	{
		_critical = i;
	}
	
	public int get_addcri()
	{
		return _critical;
	}

	private int _mdef = 0;

	public int get_mdef() {
		return _mdef;
	}

	public void set_mdef(int i) {
		this._mdef = i;
	}

	private boolean _isHasteItem = false;

	public boolean isHasteItem() {
		return _isHasteItem;
	}

	public void setHasteItem(boolean flag) {
		_isHasteItem = flag;
	}

	private int _maxUseTime = 0;

	public int getMaxUseTime() {
		return _maxUseTime;
	}

	public void setMaxUseTime(int i) {
		_maxUseTime = i;
	}

	private int _useType;

	/**
	 * 사용했을 때의 리액션을 결정하는 타입을 돌려준다.
	 */
	public int getUseType() {
		return _useType;
	}

	public void setUseType(int useType) {
		_useType = useType;
	}

	private int _foodVolume;

	/**
	 * 고기등의 아이템으로 설정되어 있는 만복도를 돌려준다.
	 */
	public int getFoodVolume() {
		return _foodVolume;
	}

	public void setFoodVolume(int volume) {
		_foodVolume = volume;
	}

	/**
	 * 램프등의 아이템으로 설정되어 있는 밝음을 돌려준다.
	 */
	public int getLightRange() {
		if (_itemId == 40001) {
			return 11;
		} else if (_itemId == 40002 || _itemId == 60154) {
			return 14;
		} else if (_itemId == 40004) {
			return 14;
		} else if (_itemId == 7338) {
			return 19;
		} else if (_itemId == 40005) {
			return 8;
		} else {
			return 0;
		}
	}

	public int getLightFuel() {
		if (_itemId == 40001) {
			return 6000;
		} else if (_itemId == 40002 || _itemId == 60154) {
			return 12000;
		} else if (_itemId == 40003) {
			return 12000;
		} else if (_itemId == 40004) {
			return 0;
		} else if (_itemId == 40005) {
			return 600;
		} else {
			return 0;
		}
	}

	// ■■■■■■ L1EtcItem 로 오버라이드(override) 하는 항목 ■■■■■■
	public boolean isStackable() {
		return false;
	}

	public int get_locx() {
		return 0;
	}

	public int get_locy() {
		return 0;
	}

	public short get_mapid() {
		return 0;
	}

	public int get_delayid() {
		return 0;
	}

	public int get_delaytime() {
		return 0;
	}

	public int getMaxChargeCount() {
		return 0;
	}

	public int get_delayEffect() {
		return 0;
	}

	private int _skillnum = 0;
	private int _skillattr = 0;
	private int _skilllv = 0;

	public int getskillnum() {
		return _skillnum;
	}

	public void setskillnum(int i) {
		_skillnum = i;
	}

	public int getskillattr() {
		return _skillattr;
	}

	public void setskillattr(int i) {
		_skillattr = i;
	}

	public int getskilllv() {
		return _skilllv;
	}

	public void setskilllv(int i) {
		_skilllv = i;
	}

	private int logcheckitem = 0;

	public int getlogcheckitem() {
		return logcheckitem;
	}

	public void setlogcheckitem(int i) {
		logcheckitem = i;
	}

	public boolean isCanSeal() {
		return false;
	}

	// ■■■■■■ L1Weapon 로 오버라이드(override) 하는 항목 ■■■■■■
	public int getRange() {
		return 0;
	}

	public int getHitModifier() {
		return 0;
	}

	public int getDmgModifier() {
		return 0;
	}

	public int getDoubleDmgChance() {
		return 0;
	}

	public int getaddDmg() {
		  return 0;
		 }
	
	public int getMagicDmgModifier() {
		return 0;
	}

	public int get_canbedmg() {
		return 0;
	}
	
	public boolean isTwohandedWeapon() {
		return false;
	}
	
	

	// ■■■■■■ L1Armor 로 오버라이드(override) 하는 항목 ■■■■■■
	public int get_ac() {
		return 0;
	}

	public int getDamageReduction() {
		return 0;
	}

	public int getWeightReduction() {
		return 0;
	}

	public int getDmgup() {
		return 0;
	}

	public int getHitup() {
		return 0;
	}

	public int getBowHitup() {
		return 0;
	}

	public int getBowDmgup() {
		return 0;
	}

	public int get_defense_water() {
		return 0;
	}

	public int get_defense_fire() {
		return 0;
	}

	public int get_defense_earth() {
		return 0;
	}

	public int get_defense_wind() {
		return 0;
	}

	private int _grade = -1; // ● 장신구 단계

	public int getGrade() {
		return _grade;
	}
	/**PVP데미지감소**/
	  public int get_regist_calcPcDefense() {
	    return 0;
	  }

	  /**PVP추가데미지**/
	  public int get_regist_PVPweaponTotalDamage() {
	    return 0;
	  }
	public void setGrade(int grade) {
		_grade = grade;
	}

	private int _price; // ● 가격

	public int get_price() {
		return _price;
	}

	public void set_price(int price) {
		_price = price;
	}

	/* 버그베어 경주 관련 항목 */
	private String _BugName;

	// private double _Rate;

	public String get_BugName() {
		return _BugName;
	}

	public void set_BugName(String _BugName) {
		this._BugName = _BugName;
	}
	private int _enchant = 0;

	public int get_enchant() {
		return _enchant;
	}

	public void set_enchant(int enchant) {
		_enchant = enchant;
	}

	/*
	 * public void set_Rate(double _Rate){ this._Rate = _Rate; } public double
	 * get_Rate(){ return _Rate; }
	 */
	
	private int _regist_stun = 0;
	private int _regist_soul = 0;
	private int _regist_dragon = 0;
	private int _regist_horror = 0;
	private int _regist_all = 0;

	public int get_regist_stun() {
		return this._regist_stun;
	}

	public void set_regist_stun(int i) {
		_regist_stun = i;
	}
	
	public int get_regist_all() {
		return this._regist_all;
	}

	public void set_regist_all(int i) {
		_regist_all = i;
	}
	
	public int get_regist_soul() {
		return this._regist_soul;
	}

	public void set_regist_soul(int i) {
		_regist_soul = i;
	}
	
	public int get_regist_horror() {
		return this._regist_horror;
	}

	public void set_regist_horror(int i) {
		_regist_horror = i;
	}
	
	public int get_regist_dragon() {
		return this._regist_dragon;
	}
	
	public void set_regist_dragon(int i) {
		_regist_dragon = i;
	}
	
	private int _techniquehit;
	private int _spirithit;
	private int _dragonlanghit;
	private int _fearhit;
	private int _allhit;
	
	public int getTechniqueHit() {
		return _techniquehit;
	}

	public void setTechniqueHit(int i) {
		_techniquehit = i;
	}

	public int getSpiritHit() {
		return _spirithit;
	}

	public void setSpiritHit(int i) {
		_spirithit = i;
	}

	public int getDragonLangHit() {
		return _dragonlanghit;
	}

	public void setDragonLangHit(int i) {
		_dragonlanghit = i;
	}

	public int getFearHit() {
		return _fearhit;
	}

	public void setFearHit(int i) {
		_fearhit = i;
	}
	
	public int getAllHit() {
		return _allhit;
	}
	
	public void setAllHit(int i) {
		_allhit = i;
	}
	
	/** 인형 타입인지 체크해서 그거만 확인 할수잇게 세팅*/
	public boolean isMagicDoll() {
		if(getUseType() == 73) return true;	
		return false;
	}
	
	/** 창고 저장 **/
	private boolean _retrieve;

	public boolean isRetrieve() {
		return _retrieve;
	}

	public void setRetrieve(boolean flag) {
		_retrieve = flag;
	}
}
