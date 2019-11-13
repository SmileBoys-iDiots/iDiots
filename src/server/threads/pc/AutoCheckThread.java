package server.threads.pc;

import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.GMCommands;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.LineageClient;

public class AutoCheckThread implements Runnable {

	private static AutoCheckThread _instance;
	public static final String[] maps = { "화둥", "하이네", "오크숲", "글루디오", "윈다우드",
			"용계", "오렌", "켄트", "기란", "요숲", "아덴", "은기사", "섬던", "본던", "요던", "윈던",
			"사던", "용던", "개던", "기던", "수던", "상아탑", "오만", "켄트성던", "기란성던", "하이네성던",
			"아덴성던", "침공", "해적섬", "라던", "그신", "욕망", "선박", "고무", "마족", "지저",
			"정무", "버땅", "테베" };

	/*
	 * public static final String[] quize = {"일 더하기 일은? 숫자로입력해주세요",
	 * "이 더하기 이는? 숫자로입력해주세요", "삼 더하기 삼은? 숫자로입력해주세요", "사 더하기 사는? 숫자로입력해주세요",
	 * "오 더하기 오는? 숫자로입력해주세요", "육 더하기 육은? 숫자로입력해주세요", "칠 더하기 칠은? 숫자로입력해주세요",
	 * "팔 더하기 팔은? 숫자로입력해주세요", "구 더하기 구는? 숫자로입력해주세요", "십 더하기 십은? 숫자로입력해주세요",
	 * "십일 더하기 십일은? 숫자로입력해주세요", "십이 더하기 십이는? 숫자로입력해주세요",
	 * "십삼 더하기 십삼은? 숫자로입력해주세요", "십사 더하기 십사는? 숫자로입력해주세요",
	 * "십오 더하기 십오는? 숫자로입력해주세요", "십육 더하기 십육은? 숫자로입력해주세요",
	 * "십칠 더하기 십칠은? 숫자로입력해주세요", "십팔 더하기 십팔은? 숫자로입력해주세요",
	 * "십구 더하기 십구는? 숫자로입력해주세요", "이십 더하기 이십은? 숫자로입력해주세요",
	 * "이십일 더하기 이십일은? 숫자로입력해주세요", "이십이 더하기 이십이는? 숫자로입력해주세요",
	 * "이십삼 더하기 이십삼은? 숫자로입력해주세요", "이십사 더하기 이십사는? 숫자로입력해주세요",
	 * "이십오 더하기 이십오는? 숫자로입력해주세요", "이십육 더하기 이십육은? 숫자로입력해주세요",
	 * "이십칠 더하기 이십칠은? 숫자로입력해주세요", "이십팔 더하기 이십팔은? 숫자로입력해주세요",
	 * "이십구 더하기 이십구는? 숫자로입력해주세요", "삼십 더하기 삼십은? 숫자로입력해주세요",
	 * "삼십일 더하기 삼십일은? 숫자로입력해주세요", "삼십이 더하기 삼십이은? 숫자로입력해주세요",
	 * "삼십삼 더하기 삼십삼은? 숫자로입력해주세요", "삼십사 더하기 삼십사은? 숫자로입력해주세요",
	 * "삼십오 더하기 삼십오은? 숫자로입력해주세요", "삼십육 더하기 삼십육은? 숫자로입력해주세요",
	 * "삼십칠 더하기 삼십칠은? 숫자로입력해주세요", "삼십팔 더하기 삼십팔은? 숫자로입력해주세요",
	 * "삼십구 더하기 삼십구은? 숫자로입력해주세요", "사십 더하기 사십은? 숫자로입력해주세요",
	 * "사십일 더하기 사십일은? 숫자로입력해주세요", "사십이 더하기 사십이은? 숫자로입력해주세요",
	 * "사십삼 더하기 사십삼은? 숫자로입력해주세요", "사십사 더하기 사십사은? 숫자로입력해주세요",
	 * "사십오 더하기 사십오은? 숫자로입력해주세요", "사십육 더하기 사십육은? 숫자로입력해주세요",
	 * "사십칠 더하기 사십칠은? 숫자로입력해주세요", "사십팔 더하기 사십팔은? 숫자로입력해주세요",
	 * "사십구 더하기 사십구은? 숫자로입력해주세요", "오십 더하기 오십은? 숫자로입력해주세요",};
	 */
	public static final String[] quize = { "주먹밥", "화장실", "새우깡", "감자깡", "화장지",
			"네이버", "네이트", "영단어", "어머니", "아버지", "시누이", "사우나", "며느리", "변두리",
			"카카오", "커피잔", "콜라병", "소주잔", "막걸리", "위스키", "주전자", "테이프", "클로버",
			"도서관", "다이아", "음식물", "고구마", "책가방", "월요일", "화요일", "수요일", "목요일",
			"금요일", "토요일", "일요일", "넥타이", "도가니", "호랑이", "감자탕", "삼천리", "암반수",
			"생수통", "약수터", "백두산", "설악산", "핸드폰", "스턴트", "이미지", "고등어", "도서관" };

	private static final int sleep = 60000 * 3;// * 60;
	private static boolean on = true;

	public static boolean getOn() {
		return on;
	}

	public static void setOn(boolean _on) {
		try {
			if (!on && _on) {
				_instance = new AutoCheckThread();
			} else if (on && !_on) {
				act.cancel(true);
				acp.cancel(true);
			}
		} catch (Exception e) {
		}
		on = _on;
	}

	public static AutoCheckThread getInstance() {
		if (_instance == null) {
			_instance = new AutoCheckThread();
		}
		return _instance;
	}

	public static ScheduledFuture<?> act = null;
	public static ScheduledFuture<?> acp = null;
	private static Queue<String> _queue = null;

	public AutoCheckThread() {
		act = GeneralThreadPool.getInstance().schedule(this, sleep);
		acp = GeneralThreadPool.getInstance().schedule(new AutoCheckPrison(),
				sleep);
		_queue = new ConcurrentLinkedQueue<String>();
		ArrayList<String> templist = new ArrayList<String>();
		for (String mapp : maps) {
			templist.add(mapp);
		}
		Collections.shuffle(templist);
		_queue.addAll(templist);
	}

	@Override
	public void run() {
		try {
			if (!on)
				return;

			Random rnd = new Random(System.nanoTime());
			int mapid = 4;
			int minx = 0;
			int miny = 0;
			int maxx = 0;
			int maxy = 0;
			boolean dun = false;

			String map = _queue.poll();
			if (map == null) {
				ArrayList<String> templist = new ArrayList<String>();
				for (String mapp : maps) {
					templist.add(mapp);
				}
				Collections.shuffle(templist);
				_queue.addAll(templist);
				map = _queue.poll();
			}

			if (map.equalsIgnoreCase("화둥")) {
				minx = 33472;
				maxx = 33856;
				miny = 32191;
				maxy = 32511;
			} else if (map.equalsIgnoreCase("하이네")) {
				minx = 33280;
				maxx = 33792;
				miny = 33023;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("오크숲")) {
				minx = 32511;
				maxx = 32960;
				miny = 32191;
				maxy = 32537;
			} else if (map.equalsIgnoreCase("글루디오")) {
				minx = 32512;
				maxx = 32960;
				miny = 32537;
				maxy = 33023;
			} else if (map.equalsIgnoreCase("윈다우드")) {
				minx = 32512;
				maxx = 32960;
				miny = 33023;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("용계")) {
				minx = 33216;
				maxx = 33472;
				miny = 32191;
				maxy = 32511;
			} else if (map.equalsIgnoreCase("오렌")) {
				minx = 33856;
				maxx = 34304;
			} else if (map.equalsIgnoreCase("켄트")) {
				minx = 32960;
				maxx = 33280;
				miny = 32511;
				maxy = 33087;
			} else if (map.equalsIgnoreCase("기란")) {
				minx = 33280;
				maxx = 33920;
				miny = 32511;
				maxy = 33023;
			} else if (map.equalsIgnoreCase("요숲")) {
				minx = 32960;
				maxx = 33216;
				miny = 32191;
				maxy = 32511;
			} else if (map.equalsIgnoreCase("아덴")) {
				minx = 33792;
				maxx = 34304;
				miny = 32739;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("은기사")) {
				minx = 32896;
				maxx = 33311;
				miny = 33023;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("섬던") || map.equalsIgnoreCase("본던")
					|| map.equalsIgnoreCase("요던") || map.equalsIgnoreCase("윈던")
					|| map.equalsIgnoreCase("사던") || map.equalsIgnoreCase("용던")
					|| map.equalsIgnoreCase("개던") || map.equalsIgnoreCase("기던")
					|| map.equalsIgnoreCase("수던")
					|| map.equalsIgnoreCase("상아탑")
					|| map.equalsIgnoreCase("오만")
					|| map.equalsIgnoreCase("켄트성던")
					|| map.equalsIgnoreCase("기란성던")
					|| map.equalsIgnoreCase("하이네성던")
					|| map.equalsIgnoreCase("아덴성던")
					|| map.equalsIgnoreCase("침공")
					|| map.equalsIgnoreCase("해적섬")
					|| map.equalsIgnoreCase("라던") || map.equalsIgnoreCase("그신")
					|| map.equalsIgnoreCase("욕망") || map.equalsIgnoreCase("선박")
					|| map.equalsIgnoreCase("고무") || map.equalsIgnoreCase("마족")
					|| map.equalsIgnoreCase("지저") || map.equalsIgnoreCase("정무")
					|| map.equalsIgnoreCase("버땅") || map.equalsIgnoreCase("테베")) {
				dun = true;
			}

			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {

				// int q_rnd = rnd.nextInt(quize.length);
				// String q = quize[q_rnd];
				// String d = ""+((1+q_rnd)*2);
				String q = Config.자동방지_퀴즈;// "퀴즈 답 입력을 "+quize[q_rnd]+" 라고 해주세요";
				String d = Config.자동방지_답;// quize[q_rnd];

				/*
				 * int rnd111 = rnd.nextInt(8); switch (rnd111) { case 0: q =
				 * "파워그립이라고 입력해주세요"; d = "파워그립"; break; case 1: q =
				 * "자동방지문제입니다.채팅창에토마호크라고입력해주세요"; d = "토마호크"; break; case 2: q =
				 * "사냥하는데죄송합니다입력해야할답은힐입니다"; d = "힐"; break; case 3: q =
				 * "자동방지중입니다지금이럽션이라고입력해주세요"; d = "이럽션"; break; case 4: q =
				 * "선버스트선버스트선버스트자동방지를위해선버스트를입력해주세요"; d = "선버스트"; break; case 5:
				 * q = "지금어디서사냥하고계신가요?자동방지중이니좀비라고입력해주세요"; d = "좀비"; break; case
				 * 6: q = "감자서버를이용해주셔서감사드립니다입력창에감자서버라고입력해주세요"; d = "감자서버";
				 * break; case 7: q = "이번차에도감사합니다현재는자동체크중이니불편하시더라도자동아님을입력해주세요";
				 * d = "자동아님"; break; default: break; }
				 */
				// if(pc.isGm())
				// pc.sendPackets(new S_SystemMessage(map+" > "+q+" > "+d),
				// true);

				if (dun) {
					if (map.equalsIgnoreCase("섬던")) {
						if (1 == pc.getMapId() || 2 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("본던")) {
						if (807 >= pc.getMapId() && 813 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("요던")) {
						if (19 >= pc.getMapId() && 21 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("윈던")) {
						if (23 >= pc.getMapId() && 24 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("사던")) {
						if (25 >= pc.getMapId() && 28 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("용던")) {
						if (30 >= pc.getMapId() && 37 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("개던")) {
						if ((43 >= pc.getMapId() && 51 <= pc.getMapId())
								|| (541 >= pc.getMapId() && 543 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("기던")) {
						if (53 >= pc.getMapId() && 57 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("수던")) {
						if (59 >= pc.getMapId() && 63 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("상아탑")) {
						if ((75 >= pc.getMapId() && 82 <= pc.getMapId())
								|| (280 >= pc.getMapId() && 289 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("오만")) {
						if (101 >= pc.getMapId() && 200 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("켄트성던")) {
						if (240 >= pc.getMapId() && 243 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("기란성던")) {
						if (248 >= pc.getMapId() && 251 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("하이네성던")) {
						if (252 >= pc.getMapId() && 254 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("아덴성던")) {
						if (257 >= pc.getMapId() && 259 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("침공")) {
						if (307 >= pc.getMapId() && 309 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("해적섬")) {
						if ((440 >= pc.getMapId() && 445 <= pc.getMapId())
								|| (480 >= pc.getMapId() && 484 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("고무")) {
						if (400 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("마족")) {
						if (410 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("지저")) {
						if (420 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("정무")) {
						if (430 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("라던")) {
						if ((451 >= pc.getMapId() && 479 <= pc.getMapId())
								|| (490 >= pc.getMapId() && 496 <= pc
										.getMapId())
								|| (530 >= pc.getMapId() && 537 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("그신")) {
						if (521 >= pc.getMapId() && 524 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("선박")) {
						if (550 >= pc.getMapId() && 558 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("욕망")) {
						if (600 >= pc.getMapId() && 608 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("테베")) {
						if (780 >= pc.getMapId() && 784 <= pc.getMapId()) {
						} else
							continue;
					}
					if (pc.getNetConnection() != null) {
						pc.getNetConnection().AutoQuiz = q;
						pc.getNetConnection().AutoAnswer = d;
						GeneralThreadPool.getInstance().schedule(
								new AutoCheck(pc.getNetConnection()), 1);
					}
				} else if (mapid == pc.getMapId()) {
					if (minx != 0 && maxx != 0) {
						if (map.equalsIgnoreCase("오렌")) {

							minx = 33856;
							maxx = 34304;
							if (pc.getX() >= minx
									&& pc.getX() <= maxx
									&& (pc.getY() >= 32191
											&& pc.getY() <= 32575 || pc.getY() >= 32127
											&& pc.getY() <= 32739)) {
							} else
								continue;
						} else if (!(pc.getX() >= minx && pc.getX() <= maxx
								&& pc.getY() >= miny && pc.getY() <= maxy))
							continue;

					}
					if (pc.getNetConnection() != null) {
						pc.getNetConnection().AutoQuiz = q;
						pc.getNetConnection().AutoAnswer = d;
						GeneralThreadPool.getInstance().schedule(
								new AutoCheck(pc.getNetConnection()), 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		GeneralThreadPool.getInstance().schedule(this, sleep);
	}

	/**
	 * 감옥에 퀴즈내기
	 */
	public class AutoCheckPrison implements Runnable {

		private static final int sleep2 = 60000;

		public AutoCheckPrison() {
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				if (!on)
					return;
				// Random rnd = new Random(System.nanoTime());
				for (L1Object obj : L1World.getInstance()
						.getVisibleObjects(6202).values()) {
					if (obj instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) obj;
						if (pc.isGm())
							continue;

						/*
						 * if
						 * (!pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND
						 * )) {
						 * pc.getSkillEffectTimerSet().setSkillEffect(EARTH_BIND
						 * , 9999*1000); pc.sendPackets(new S_Poison(pc.getId(),
						 * 2), true); Broadcaster.broadcastPacket(pc, new
						 * S_Poison(pc.getId(), 2), true); pc.sendPackets(new
						 * S_Paralysis(S_Paralysis.TYPE_FREEZE, true), true); }
						 */
						// int q_rnd = rnd.nextInt(quize.length);
						// String q = quize[q_rnd];
						// String d = ""+((1+q_rnd)*2);
						/*
						 * String q = "퀴즈 답 입력을 "+quize[q_rnd]+" 라고 해주세요";
						 * String d = quize[q_rnd];
						 */
						String q = Config.자동방지_퀴즈;
						String d = Config.자동방지_답;

						/*
						 * int rnd111 = rnd.nextInt(8); switch (rnd111) { case
						 * 0: q = "파워그립이라고 입력해주세요"; d = "파워그립"; break; case 1: q
						 * = "자동방지문제입니다.채팅창에토마호크라고입력해주세요"; d = "토마호크"; break;
						 * case 2: q = "사냥하는데죄송합니다입력해야할답은힐입니다"; d = "힐"; break;
						 * case 3: q = "자동방지중입니다지금이럽션이라고입력해주세요"; d = "이럽션";
						 * break; case 4: q = "선버스트선버스트선버스트자동방지를위해선버스트를입력해주세요";
						 * d = "선버스트"; break; case 5: q =
						 * "지금어디서사냥하고계신가요?자동방지중이니좀비라고입력해주세요"; d = "좀비"; break;
						 * case 6: q = "감자서버를이용해주셔서감사드립니다입력창에감자서버라고입력해주세요"; d =
						 * "감자서버"; break; case 7: q =
						 * "이번차에도감사합니다현재는자동체크중이니불편하시더라도자동아님을입력해주세요"; d = "자동아님";
						 * break; default: break; }
						 */

						if (pc.getNetConnection() != null) {
							pc.getNetConnection().AutoQuiz = q;
							pc.getNetConnection().AutoAnswer = d;
							GeneralThreadPool.getInstance()
									.schedule(
											new AutoCheckPrison2(
													pc.getNetConnection()), 1);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			GeneralThreadPool.getInstance().schedule(this, sleep2);
		}
	}

	/**
	 * 일반 유저 퀴즈처리
	 */
	public class AutoCheck implements Runnable {
		private LineageClient lc;

		public AutoCheck(LineageClient cl) {
			lc = cl;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				if (lc.close)
					return;
				if (lc.getActiveChar() == null)
					return;
				else if (lc.getActiveChar().isGm())
					return;
				else if (lc.getActiveChar().isPrivateShop())
					return;
				else if (lc.getActiveChar().getMapId() == 800)
					return;
				else if ((CharPosUtil.getZoneType(lc.getActiveChar()) == 1 && lc
						.getActiveChar().getMapId() != 2005)
						|| (CharPosUtil.getZoneType(lc.getActiveChar()) == -1 && lc
								.getActiveChar().getMapId() == 4))
					return;
				int castleid = L1CastleLocation.getCastleIdByArea(lc
						.getActiveChar());
				if (castleid != 0) {
					if (WarTimeController.getInstance().isNowWar(castleid)) {
						return;
					}
				}

				// 전송!
				String ip = lc.getIp();
				String account = lc.getAccountName();
				lc.AutoCheck = true;
				lc.AutoCheckCount = 0;

				for (int ii = 0; ii < 2; ii++) {
					// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
					// "자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."), true);
					// lc.sendPacket(new
					// S_SystemMessage("자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."),
					// true);
					lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
							"자동 방지 : " + lc.AutoQuiz), true);
					lc.sendPacket(
							new S_SystemMessage("자동 방지 : " + lc.AutoQuiz), true);
					Thread.sleep(10000);
					if (!lc.AutoCheck)
						return;
					if (lc.close || lc.getActiveChar() == null) {
						if (!GMCommands.autocheck_iplist.contains(ip))
							GMCommands.autocheck_iplist.add(ip);
						if (account != null
								&& !account.equalsIgnoreCase("")
								&& !GMCommands.autocheck_accountlist
										.contains(account))
							GMCommands.autocheck_accountlist.add(account);
						return;
					}
				}

				/*
				 * if
				 * (lc.getActiveChar().getSkillEffectTimerSet().hasSkillEffect
				 * (EARTH_BIND)) {
				 * lc.getActiveChar().getSkillEffectTimerSet().removeSkillEffect
				 * (EARTH_BIND); }
				 */
				while (lc.getActiveChar().isTeleport()
						|| lc.getActiveChar().텔대기()) {
					Thread.sleep(100);
				}
				/*
				 * lc.getActiveChar().getSkillEffectTimerSet().setSkillEffect(
				 * EARTH_BIND, 9999*1000); lc.getActiveChar().sendPackets(new
				 * S_Poison(lc.getActiveChar().getId(), 2), true);
				 * Broadcaster.broadcastPacket(lc.getActiveChar(), new
				 * S_Poison(lc.getActiveChar().getId(), 2), true);
				 * lc.getActiveChar().sendPackets(new
				 * S_Paralysis(S_Paralysis.TYPE_FREEZE, true), true);
				 */
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요. [주의!! 이번에 미입력시 감옥에 갑니다.]"),
				// true);
				// lc.sendPacket(new
				// S_SystemMessage("자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."),
				// true);
				// lc.sendPacket(new
				// S_SystemMessage("자동 방지 : [주의!! 이번에 미입력시 감옥에 갑니다.]"), true);
				lc.sendPacket(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "자동 방지 : "
								+ lc.AutoQuiz + " [주의!! 이번에 미입력시 감옥에 갑니다.]"),
						true);
				lc.sendPacket(new S_SystemMessage("자동 방지 : " + lc.AutoQuiz),
						true);
				lc.sendPacket(new S_SystemMessage(
						"자동 방지 : [주의!! 이번에 미입력시 감옥에 갑니다.]"), true);
				Thread.sleep(10000);
				if (!lc.AutoCheck)
					return;
				if (lc.close || lc.getActiveChar() == null) {
					if (!GMCommands.autocheck_iplist.contains(ip))
						GMCommands.autocheck_iplist.add(ip);
					if (account != null
							&& !account.equalsIgnoreCase("")
							&& !GMCommands.autocheck_accountlist
									.contains(account))
						GMCommands.autocheck_accountlist.add(account);
					return;
				}
				lc.sendPacket(new S_SystemMessage("자동 방지 답을 입력하지 않으셨습니다."),
						true);
				if (lc.getActiveChar() == null) {
					if (!GMCommands.autocheck_iplist.contains(ip))
						GMCommands.autocheck_iplist.add(ip);
					if (account != null
							&& !account.equalsIgnoreCase("")
							&& !GMCommands.autocheck_accountlist
									.contains(account))
						GMCommands.autocheck_accountlist.add(account);
					return;
				} else {
					while (lc.getActiveChar().isTeleport()
							|| lc.getActiveChar().텔대기()) {
						Thread.sleep(100);
					}
					/*
					 * if
					 * (lc.getActiveChar().getSkillEffectTimerSet().hasSkillEffect
					 * (EARTH_BIND)) {
					 * lc.getActiveChar().getSkillEffectTimerSet(
					 * ).removeSkillEffect(EARTH_BIND); }
					 */
					//
					if (!GMCommands.autocheck_Tellist.contains(lc
							.getAccountName())) {
						GMCommands.autocheck_Tellist.add(lc.getAccountName());
					}
					L1Teleport.teleport(lc.getActiveChar(), new L1Location(
							32928, 32864, 6202), 5, true);// 텔 패킷 미사용
					// L1Teleport.teleport(lc.getActiveChar(), 32928, 32864,
					// (short) 6202, 5, true);
				}
				lc.AutoCheck = false;
				lc.AutoCheckCount = 0;
				lc.AutoQuiz = "";
				lc.AutoAnswer = "";
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	/**
	 * 감옥에 퀴즈처리
	 */
	public class AutoCheckPrison2 implements Runnable {
		private LineageClient lc;

		public AutoCheckPrison2(LineageClient cl) {
			lc = cl;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				if (lc.close)
					return;
				if (lc.getActiveChar() == null)
					return;
				else if (lc.getActiveChar().isGm())
					return;

				// 전송!
				lc.AutoCheck = true;
				lc.AutoCheckCount = 0;
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."), true);
				// lc.sendPacket(new
				// S_SystemMessage("자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."),
				// true);
				lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						"자동 방지 : " + lc.AutoQuiz), true);
				lc.sendPacket(new S_SystemMessage("자동 방지 : " + lc.AutoQuiz),
						true);
				Thread.sleep(10000);
				if (!lc.AutoCheck || lc.close)
					return;
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."), true);
				// lc.sendPacket(new
				// S_SystemMessage("자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."),
				// true);
				lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						"자동 방지 : " + lc.AutoQuiz), true);
				lc.sendPacket(new S_SystemMessage("자동 방지 : " + lc.AutoQuiz),
						true);
				Thread.sleep(10000);
				if (!lc.AutoCheck || lc.close)
					return;
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."), true);
				// lc.sendPacket(new
				// S_SystemMessage("자동 방지 : [ "+lc.AutoQuiz+" ] 답을 채팅창에 입력해주세요."),
				// true);
				lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						"자동 방지 : " + lc.AutoQuiz), true);
				lc.sendPacket(new S_SystemMessage("자동 방지 : " + lc.AutoQuiz),
						true);
				Thread.sleep(10000);
				if (!lc.AutoCheck || lc.close)
					return;
				lc.sendPacket(new S_SystemMessage("자동 방지 답을 입력하지 않으셨습니다."),
						true);
				if (lc.getActiveChar() == null)
					lc.kick();
				else if (lc.getAccount()._account_auto_check_count++ > 4) {
					Account.ban(lc.getAccountName());
					lc.kick();
					return;
				}
				lc.AutoCheck = false;
				lc.AutoCheckCount = 0;
				lc.AutoQuiz = "";
				lc.AutoAnswer = "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void createCharAutoCheck(L1PcInstance pc) {

		if (pc.getNetConnection() != null) {
			pc.getSkillEffectTimerSet().setSkillEffect(EARTH_BIND, 9999 * 1000);
			pc.sendPackets(new S_Poison(pc.getId(), 2), true);
			Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 2), true);
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true), true);

			// Random rnd = new Random(System.nanoTime());

			// int q_rnd = rnd.nextInt(quize.length);
			// String q = quize[q_rnd];
			// String d = ""+((1+q_rnd)*2);
			/*
			 * String q = "퀴즈 답 입력을 "+quize[q_rnd]+" 라고 해주세요"; String d =
			 * quize[q_rnd];
			 */
			String q = Config.자동방지_퀴즈;
			String d = Config.자동방지_답;
			/*
			 * int rnd111 = rnd.nextInt(8); switch (rnd111) { case 0: q =
			 * "파워그립이라고 입력해주세요"; d = "파워그립"; break; case 1: q =
			 * "자동방지문제입니다.채팅창에토마호크라고입력해주세요"; d = "토마호크"; break; case 2: q =
			 * "사냥하는데죄송합니다입력해야할답은힐입니다"; d = "힐"; break; case 3: q =
			 * "자동방지중입니다지금이럽션이라고입력해주세요"; d = "이럽션"; break; case 4: q =
			 * "선버스트선버스트선버스트자동방지를위해선버스트를입력해주세요"; d = "선버스트"; break; case 5: q =
			 * "지금어디서사냥하고계신가요?자동방지중이니좀비라고입력해주세요"; d = "좀비"; break; case 6: q =
			 * "감자서버를이용해주셔서감사드립니다입력창에감자서버라고입력해주세요"; d = "감자서버"; break; case 7: q
			 * = "이번차에도감사합니다현재는자동체크중이니불편하시더라도자동아님을입력해주세요"; d = "자동아님"; break;
			 * default: break; }
			 */

			pc.getNetConnection().AutoQuiz = q;
			pc.getNetConnection().AutoAnswer = d;
			GeneralThreadPool.getInstance().schedule(
					new AutoCheck(pc.getNetConnection()), 1);
		}

	}

}
