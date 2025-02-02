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
package l1j.server.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GMCommands;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.RaceRecordTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1LittleBugInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1RaceTicket;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server.model:
// L1BugBearRace

public class L1BugBearRace {

	public final ArrayList<L1LittleBugInstance> _littleBug = new ArrayList<L1LittleBugInstance>();

	private final ArrayList<L1NpcInstance> _merchant = new ArrayList<L1NpcInstance>();

	// private final HashMap<Integer, L1RaceTicket> _ticketPrice = new
	// HashMap<Integer, L1RaceTicket>();
	private final ArrayList<L1RaceTicket> _ticketPrice = new ArrayList<L1RaceTicket>();
	public static final int STATUS_NONE = 0;
	public static final int STATUS_READY = 1;
	public static final int STATUS_PLAYING = 2;
	public boolean buyTickets = true;
	public static boolean racing_im = false; // 傾戚什亜 掻娃戚 角醸澗走

	private static final int[] startX = { 33522, 33520, 33518, 33516, 33514 };
	private static final int[] startY = { 32861, 32863, 32865, 32867, 32869 };

	private static final int[][] movingCount = { { 45, 4, 5, 6, 50 },
			{ 42, 6, 5, 7, 50 }, { 39, 8, 5, 8, 50 }, { 36, 10, 5, 9, 50 },
			{ 33, 12, 5, 10, 50 } };

	public static int[] bugStat = new int[5];

	private static final int[] heading = { 6, 7, 0, 1, 2 };

	private static final Random _random = new Random();

	private int[] _betting = new int[5];

	private int _round;

	private int _roundId;

	private int _bugRaceStatus;

	public boolean _goal;

	private static L1BugBearRace _instance;

	private static int racetime = 3;

	public static L1BugBearRace getInstance() {
		if (_instance == null) {
			_instance = new L1BugBearRace();
		}
		return _instance;
	}

	private L1BugBearRace() {
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (npc.getNpcTemplate().get_npcId() == 70041
						|| npc.getNpcTemplate().get_npcId() == 70035
						|| npc.getNpcTemplate().get_npcId() == 70042) {
					_merchant.add(npc);
				}
			}
		}
		race_loading();
		loadingGame();
	}

	public void loadingGame() {
		try {
			clearBug();
			setRoundId(ObjectIdFactory.getInstance().nextId());
			setRound(getRound() + 1);
			clearBetting();
			_goal = false;

			GeneralThreadPool.getInstance().schedule(new Startbug(), 10000);
			/*
			 * storeBug(); closeDoor(); setBugRaceStatus(STATUS_READY);
			 * //broadCastTime("$376 " + racetime + " $377");
			 * broadCastTime("井奄 獣拙 2 歳 穿!"); L1ReadyThread rt = new
			 * L1ReadyThread(); GeneralThreadPool.getInstance().execute(rt);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storeBug() {
		int arr[] = new int[5];
		for (int i = 0; i < 5; i++) {
			arr[i] = _random.nextInt(20);
			for (int j = 0; j < i; j++) {
				if (arr[i] == arr[j]) {
					arr[i] = _random.nextInt(20);
					i = i - 1;
					break;
				}
			}
		}

		L1Npc npcTemp = NpcTable.getInstance().getTemplate(100000);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		for (int i = 0; i < 5; i++) {
			L1LittleBugInstance bug = new L1LittleBugInstance(npcTemp, arr[i],
					startX[i], startY[i]);
			RaceRecordTable.getInstance().getRaceRecord(arr[i], bug);
			float winpoint = 0;
			float record = bug.getWin() + bug.getLose();
			if (record == 0) {
				winpoint = 0;
			} else {
				winpoint = bug.getWin() / record * 100;
			}
			bug.setWinPoint(nf.format(winpoint));
			_littleBug.add(bug);
		}
	}

	private void setSpeed() {
		for (L1LittleBugInstance bug : _littleBug) {
			int pulsSpeed = 0;
			int condition = bug.getCondition();
			if (condition == L1LittleBugInstance.GOOD) {
				pulsSpeed = 30;
			} else if (condition == L1LittleBugInstance.NORMAL) {
				pulsSpeed = 60;
			} else if (condition == L1LittleBugInstance.BAD) {
				pulsSpeed = 90;
			}
			int time = (int) (bug.getPassispeed() - (bug.getPassispeed() * 0.40));// 奄糎
																					// 獄井
			// int time = (int) (bug.getPassispeed() - (bug.getPassispeed() *
			// 0.20));//奄糎 昔井
			// bug.setPassispeed(bug.getPassispeed() +
			// _random.nextInt(pulsSpeed));
			bug.setPassispeed(time + _random.nextInt(pulsSpeed));
			// System.out.println(bug.getName() + " > "+bug.getPassispeed());
		}
	}

	public void startRace() {
		setBugRaceStatus(STATUS_PLAYING);
		buyTickets = false;
		calcDividend();
		setSpeed();
		openDoor();
		int i = 0;
		for (L1LittleBugInstance bug : _littleBug) {
			L1BugBearRacing bbr = new L1BugBearRacing(bug, i++);
			GeneralThreadPool.getInstance().execute(bbr);
		}
	}

	private void clearBug() {
		try {
			ArrayList<L1LittleBugInstance> clonn = new ArrayList<L1LittleBugInstance>();
			clonn.addAll(_littleBug);
			GeneralThreadPool.getInstance().schedule(new deleteBug(clonn),
					10000);
			/*
			 * for (L1LittleBugInstance bug : _littleBug) { bug.deleteMe(); }
			 */
			_littleBug.clear();
			for (int i = 0; i < 5; i++) {
				bugStat[i] = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class deleteBug implements Runnable {
		ArrayList<L1LittleBugInstance> clonn;

		public deleteBug(ArrayList<L1LittleBugInstance> ll) {
			clonn = ll;
		}

		@Override
		public void run() {
			// TODO 切疑 持失吉 五社球 什途
			for (L1LittleBugInstance bug : clonn) {
				bug.deleteMe();
			}
		}

	}

	class Startbug implements Runnable {
		public Startbug() {
		}

		@Override
		public void run() {
			// TODO 切疑 持失吉 五社球 什途
			try {
				L1ReadyThread rt = new L1ReadyThread();
				GeneralThreadPool.getInstance().execute(rt);

				storeBug();
				closeDoor();
				setBugRaceStatus(STATUS_READY);
				// broadCastTime("$376 " + racetime + " $377");
				broadCastTime("井奄 獣拙 3 歳穿");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void openDoor() {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (door.getGfxId().getGfxId() == 1487) {
				door.open();
			}
		}
	}

	private void closeDoor() {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (door.getGfxId().getGfxId() == 1487) {
				door.close();
			}
		}
	}

	private void broadCastTime(String chat) {
		for (L1NpcInstance npc : _merchant) {
			Broadcaster.wideBroadcastPacket(npc, new S_NpcChatPacket(npc, chat,
					2), true);
		}
	}

	private void broadCastWinner(String winner) {
		String chat = "薦 " + getRound() + "$366" + " '" + winner + "' "
				+ "$367";
		for (L1NpcInstance npc : _merchant) {
			if (npc.getNpcTemplate().get_npcId() == 70035) { // 室叔
				Broadcaster.wideBroadcastPacket(npc, new S_NpcChatPacket(npc,
						chat, 2), true);
			}
		}
	}

	private void calcDividend() {
		float[] dividend = new float[5];
		L1LittleBugInstance[] bugs = getBugsArray();
		float allBetting = 0;
		for (int b : _betting) {
			allBetting += b;
		}

		long AllbettingPrice = (long) (allBetting * 500);
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc == null || pc.getNetConnection() == null || !pc.isGm())
				continue;
			pc.sendPackets(new S_SystemMessage("薦 " + getRound()
					+ " 獄井 銅掴 姥脊 恥 榎衝: " + AllbettingPrice), true);
		}

		for (int i = 0; i < 5; i++) {
			if (_betting[i] == 0) {
				dividend[i] = 0;
			} else {
				// dividend[i] = (float)((allBetting / _betting[i]) * 0.8f);
				// dividend[i] = (float)((allBetting / _betting[i]) * 0.89f);
				dividend[i] = (float) ((allBetting / _betting[i]) * (float) ((float) GMCommands.発呪晴 * 0.01f));
				if (dividend[i] < 1) {
					dividend[i] = 1f;
				}

				if (dividend[i] > 10000) {
					dividend[i] = 10000;
				}
			}
			bugs[i].setDividend(dividend[i]);
		}
	}

	public int bugRound() {
		return getRound();
	}

	public String getTicketName(int i) {
		L1LittleBugInstance bug = _littleBug.get(i);
		return new StringBuilder().append(getRound()).append("-")
				.append(bug.getNumber() + 1).append(" ").append(bug.getName())
				.toString();
	}

	public int[] getTicketInfo(int i) {
		return new int[] { getRoundId(), getRound(), _littleBug.get(i).getNumber() };
	}

	public String getTicketBugName(int i) {
		for (int ai = 0; ai < 5; ai++) {
			L1LittleBugInstance bug = _littleBug.get(ai);
			if (bug.getNumber() == i)
				return bug.getName();
		}
		return null;
	}

	public double getTicketPrice(L1ItemInstance item) {
		for (L1RaceTicket ticket : _ticketPrice) {
			if (ticket.getRoundId() == item.getSecondId()
					&& ticket.getWinner() == item.getTicketId()) {
				return ticket.getDividend();
			}
		}
		/*
		 * L1RaceTicket ticket = _ticketPrice.get(item.getSecondId()); if
		 * (ticket == null && item.getSecondId() != getRoundId()) { return 1; }
		 * if (ticket.getWinner() == item.getTicketId()) { return
		 * ticket.getDividend(); }
		 */
		return 0;
	}

	public String[] makeStatusString() {
		ArrayList<String> status = new ArrayList<String>();
		for (L1LittleBugInstance bug : _littleBug) {
			status.add(bug.getName());
			if (bug.getCondition() == L1LittleBugInstance.GOOD) {
				status.add("疏製");// $368
			} else if (bug.getCondition() == L1LittleBugInstance.NORMAL) {
				status.add("左搭");// $369
			} else if (bug.getCondition() == L1LittleBugInstance.BAD) {
				status.add("蟹旨");// $370
			}
			status.add(bug.getWinPoint() + "%");
		}
		return status.toArray(new String[status.size()]);
	}

	private synchronized void finish(L1LittleBugInstance bug) {
		try {
			if (!_goal && getBugRaceStatus() == STATUS_PLAYING) {
				_goal = true;
				racing_im = false;
				byte i = 0;
				for (L1LittleBugInstance b : _littleBug) {
					if (b == bug)
						break;
					i++;
				}
				int allBetting = 0;
				for (int b : _betting) {
					allBetting += b;
				}
				allBetting = allBetting * 500;
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					if (pc == null || pc.getNetConnection() == null
							|| !pc.isGm())
						continue;
					pc.sendPackets(new S_SystemMessage("薦 " + getRound()
							+ " 獄井 酔渋"), true);
					pc.sendPackets(
							new S_SystemMessage(
									" 恥銅掴: "
											+ (long) allBetting
											+ " 酔渋銅掴: "
											+ (long) (_betting[i]
													* bug.getDividend() * 500)
											+ " 託衝: "
											+ ((long) allBetting - (long) (_betting[i]
													* bug.getDividend() * 500))),
							true);
				}

				race_difference((int) allBetting,
						(int) (_betting[i] * bug.getDividend() * 500));
				L1RaceTicket ticket = new L1RaceTicket(getRoundId(),
						bug.getNumber(), bug.getDividend());
				_ticketPrice.add(ticket);
				race_divAdd(getRoundId(), bug.getNumber(), bug.getDividend());
				broadCastWinner(bug.getNameId());
				setBugRaceStatus(STATUS_NONE);
				RaceRecordTable.getInstance().updateRaceRecord(bug.getNumber(),
						bug.getWin() + 1, bug.getLose());
				// BugRaceFinishRecord.getInstance().addRecord(getRoundId(),
				// bug.getNumber(), bug.getDividend());
				L1WaitingTimer wt = new L1WaitingTimer();
				wt.begin();
			} else {
				RaceRecordTable.getInstance().updateRaceRecord(bug.getNumber(),
						bug.getWin(), bug.getLose() + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private L1LittleBugInstance[] getBugsArray() {
		return _littleBug.toArray(new L1LittleBugInstance[_littleBug.size()]);
	}

	public synchronized void addBetting(int num, int count) {
		if (getBugRaceStatus() == STATUS_READY) {
			_betting[num] += count;
		}
	}

	// public synchronized void addBetting(int num, int count) {
	// if (getBugRaceStatus() == STATUS_READY) {
	// _betting[num] += count;
	// }
	// }

	private void clearBetting() {
		_betting = new int[5];
	}

	public void setRound(int i) {
		_round = i;
	}

	public int getRound() {
		return _round;
	}

	public void setRoundId(int i) {
		_roundId = i;
	}

	public int getRoundId() {
		return _roundId;
	}

	private void setBugRaceStatus(int i) {
		_bugRaceStatus = i;
	}

	public int getBugRaceStatus() {
		return _bugRaceStatus;
	}

	class L1WaitingTimer implements Runnable {// TimerTask {
		@Override
		public void run() {
			loadingGame();
		}

		public void begin() {
			// _timer.schedule(this, 30 * 1000);
			GeneralThreadPool.getInstance().schedule(this, 5 * 1000);
		}
	}

	class L1ReadyThread implements Runnable {
		@Override
		public void run() {
			try {
				buyTickets = true;
				broadCastTime("傾戚什 妊 毒古亜 獣拙鞠醸柔艦陥.");
				for (int time = racetime; time > 0; time--) {
					if (time <= 2) {
						broadCastTime("$376 " + time + " $377");
					}
					try {
						/*
						 * if(time == 3) Thread.sleep(60000); else
						 */
						Thread.sleep(60000);
					} catch (Exception e) {
					}
				}
				buyTickets = false;
				broadCastTime("傾戚什妊 毒古亜 曽戟鞠醸柔艦陥.");
				L1BroadCastDividend bcd = new L1BroadCastDividend();
				GeneralThreadPool.getInstance().execute(bcd);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	class L1BroadCastDividend implements Runnable {
		private L1NpcInstance _npc;

		public L1BroadCastDividend() {
			for (L1NpcInstance npc : _merchant) {
				if (npc.getNpcTemplate().get_npcId() == 70041) { // 遁轍
					_npc = npc;
				}
			}
		}

		@Override
		public void run() {
			try {

				Broadcaster.wideBroadcastPacket(_npc, new S_NpcChatPacket(_npc,
						"$363", 2), true);
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}
				Broadcaster.wideBroadcastPacket(_npc, new S_NpcChatPacket(_npc,
						"$364", 2), true);
				startRace();
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(1);
				nf.setMinimumFractionDigits(1);
				for (L1LittleBugInstance bug : _littleBug) {
					String chat = bug.getName() + " $402 "
							+ nf.format(bug.getDividend());
					Broadcaster.wideBroadcastPacket(_npc, new S_NpcChatPacket(
							_npc, chat, 2), true);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class L1BugBearRacing implements Runnable {
		private L1LittleBugInstance _bug;
		private int _num;
		private int count = 0;
		private int i = 0;

		public L1BugBearRacing(L1LittleBugInstance bug, int num) {
			_bug = bug;
			_num = num;
			count = movingCount[_num][0];
		}

		@Override
		public void run() {
			try {

				if (_bug._destroyed)
					return;
				if (_bug.getX() == 33527) {
					finish(_bug);
					return;
				}
				if (count == 0) {
					count = movingCount[_num][++i];
				}

				// 獄井 稽鎖 鞠宜焼亜澗 獣繊
				if (!_goal && !racing_im && i == 4
						&& _bugRaceStatus == STATUS_PLAYING)
					racing_im = true;

				if (_random.nextInt(150) == 0) {
					Broadcaster.broadcastPacket(_bug,
							new S_DoActionGFX(_bug.getId(), 30), true); // 獄井
					// Broadcaster.broadcastPacket(_bug, new
					// S_DoActionGFX(_bug.getId(), 66 + _random.nextInt(2)),
					// true); 昔井
					GeneralThreadPool.getInstance().schedule(this, 3360);
				} else {
					count--;
					_bug.setDirectionMove(heading[i]);
					GeneralThreadPool.getInstance().schedule(this,
							_bug.getPassispeed());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * class L1BugBearRacing implements Runnable { private L1LittleBugInstance
	 * _bug; private int _num;
	 * 
	 * public L1BugBearRacing(L1LittleBugInstance bug, int num) { _bug = bug;
	 * _num = num; }
	 * 
	 * @Override public void run() { int i = 0; int count =
	 * movingCount[_num][i]; while (true) { if (count == 0) { count =
	 * movingCount[_num][++i]; } if (_random.nextInt(150) == 0) {
	 * Broadcaster.broadcastPacket(_bug, new S_DoActionGFX(_bug.getId(), 30));
	 * try { Thread.sleep(3360); } catch(Exception e) { } } else { count--;
	 * _bug.setDirectionMove(heading[i]); try {
	 * 
	 * Thread.sleep(_bug.getPassispeed()); } catch(Exception e) { } } if
	 * (_bug.getX() == 33527) { finish(_bug); break; } } } }
	 */

	private void race_difference(int b, int s) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO race_difference SET date=?, Round=?, buy=?, winner_sell=?, difference=?");
			pstm.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstm.setInt(2, getRound());
			pstm.setInt(3, b);
			pstm.setInt(4, s);
			pstm.setInt(5, b - s);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void race_divAdd(int id, int b, double d) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO race_div_record SET id=?, bug_number=?, dividend=?");
			pstm.setInt(1, id);
			pstm.setInt(2, b);
			pstm.setInt(3, (int) (d * 1000));
			pstm.executeUpdate();
		} catch (SQLException e) {
			// e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void race_loading() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM race_div_record");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1RaceTicket ticket = new L1RaceTicket(rs.getInt("id"),
						rs.getInt("bug_number"),
						(double) (rs.getInt("dividend")) / 1000);
				_ticketPrice.add(ticket);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private static final String bugment[] = { "獄 井 人 虞 袴 馬 稽 毘 級 惟 紫撹馬劃!!!",
			"醤 獄井 被媒陥 遭宿せせせ 獄 井 杯 拾!!!!",
			"獄井 餌!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "獄井舌 神室推 像 壕雁 位蟹 疏革推",
			"穿毒 2腰 傾戚 何隅陥 ばば 戚腰毒拭 伊", "獄井舌 社厳杷 照桔艦猿? 慎切沢?",
			"焼 酔 哨 降 掩 岨 搾 佃 虞", "焼猿 壕雁戚 遭宿疏紹澗汽せせ?", "壕雁岨左社",
			"陥級 獄 井 杯 拾!!!!!!!!", "戚訓言拭 獄井馬澗暗 焼脊艦猿????",
			"焼 呪酵 戚腰毒 魚醤馬澗汽 松1降虞股壱 粛蟹", "獣降 袴 亜醤馬劃?!!!!!!!", "焼 持唖 崖艦陥ばば",
			"遭舛廃 壕雁戚陥", "鎧亜 郊稽 遭舛廃 獄井重 戚推", "ぞ た 焦 ぞ た 焦", "獄 井 背 辞 増 紫 室",
			"汗界戚 紳陥 焼脊艦猿?せせせ", "神潅精 獄井馬澗劾戚陥", "安紗亜じ た", "菰稽 股嬢醤 設股醸陥壱 社庚蟹劃?",
			"笑艦陥 戚暗 巷譲昔走せせせ", "しび雌馬陥せせ汗界戚せせ", "遭促 戚腰毒 遭宿 加依旭葛1", "級填葛??",
			"             ", "                           ", "         ",
			"佐1重旭精 依級 膳蟹 幻革 せせせ", "戚腰毒 遭宿膳壕雁 吟依旭葛!", "し.し.し.し.し.し.し.し.",
			"膳 匙 虞 鯵 歯 絢", "馬焦馬焦!", "逢獣陥跡~", "醤 益幻背虞 せせせ !", "紫撹 哀空陥 蟹澗",
			"澗 煩戚滴せせせせせせ", "馬走原虞 益幻背虞せせせ", "硲焼硲焼", "3腰幻 臣昔娃陥", "反滴 鯵 歯 1殴",
			"膳 旭 葛!!!!", "馬戚郊 閏形虞せせ", "蟹亀 魚壱粛陥..", "赤堰蒸堰", "               ",
			"                              ", "          ",
			"                                ",
			"                                    ", "   ", "     ",
			"                  ", "        " };
}