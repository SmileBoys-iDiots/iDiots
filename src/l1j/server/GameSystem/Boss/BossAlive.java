package l1j.server.GameSystem.Boss;

import java.util.ArrayList;
import java.util.HashMap;

public class BossAlive {
	public static BossAlive ins;

	public static BossAlive getInstance(){
		if(ins==null)
			ins = new BossAlive();
		return ins;
	}
	//MapID , 1생존 2죽음
	HashMap<Integer,Integer> isAlive = new HashMap<Integer,Integer>();
	
	public boolean isBossAlive(int mapid){
		boolean alive = false;
			if(isAlive.containsKey(mapid)){
				alive = true;
			}
		
		return alive;		
	}
	
	public void BossSpawn(int mapid){
		isAlive.put(mapid, 1);
	}
	public void BossDeath(int mapid){
		isAlive.remove(mapid);
	}
	//나중에 이거 HashMap으로 만들기
	public boolean is에르자베 = false;
	public long ezTime = -1;
	public void set에르자베타임(long s){
		ezTime = s;
	}
	public boolean is발록 = false;
	public long BLTime = -1;
	public void set발록타임(long s){
		BLTime = s;
	}
	public boolean is샌드웜 = false;
	public long sdTime = -1;
	public void set샌드웜타임(long s){
		sdTime = s;
	}
}
