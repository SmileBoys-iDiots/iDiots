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
	public static final String[] maps = { "ȭ��", "���̳�", "��ũ��", "�۷���", "���ٿ��",
			"���", "����", "��Ʈ", "���", "�佣", "�Ƶ�", "�����", "����", "����", "���", "����",
			"���", "���", "����", "���", "����", "���ž", "����", "��Ʈ����", "�������", "���̳׼���",
			"�Ƶ�����", "ħ��", "������", "���", "�׽�", "���", "����", "��", "����", "����",
			"����", "����", "�׺�" };

	/*
	 * public static final String[] quize = {"�� ���ϱ� ����? ���ڷ��Է����ּ���",
	 * "�� ���ϱ� �̴�? ���ڷ��Է����ּ���", "�� ���ϱ� ����? ���ڷ��Է����ּ���", "�� ���ϱ� ���? ���ڷ��Է����ּ���",
	 * "�� ���ϱ� ����? ���ڷ��Է����ּ���", "�� ���ϱ� ����? ���ڷ��Է����ּ���", "ĥ ���ϱ� ĥ��? ���ڷ��Է����ּ���",
	 * "�� ���ϱ� ����? ���ڷ��Է����ּ���", "�� ���ϱ� ����? ���ڷ��Է����ּ���", "�� ���ϱ� ����? ���ڷ��Է����ּ���",
	 * "���� ���ϱ� ������? ���ڷ��Է����ּ���", "���� ���ϱ� ���̴�? ���ڷ��Է����ּ���",
	 * "�ʻ� ���ϱ� �ʻ���? ���ڷ��Է����ּ���", "�ʻ� ���ϱ� �ʻ��? ���ڷ��Է����ּ���",
	 * "�ʿ� ���ϱ� �ʿ���? ���ڷ��Է����ּ���", "���� ���ϱ� ������? ���ڷ��Է����ּ���",
	 * "��ĥ ���ϱ� ��ĥ��? ���ڷ��Է����ּ���", "���� ���ϱ� ������? ���ڷ��Է����ּ���",
	 * "�ʱ� ���ϱ� �ʱ���? ���ڷ��Է����ּ���", "�̽� ���ϱ� �̽���? ���ڷ��Է����ּ���",
	 * "�̽��� ���ϱ� �̽�����? ���ڷ��Է����ּ���", "�̽��� ���ϱ� �̽��̴�? ���ڷ��Է����ּ���",
	 * "�̽ʻ� ���ϱ� �̽ʻ���? ���ڷ��Է����ּ���", "�̽ʻ� ���ϱ� �̽ʻ��? ���ڷ��Է����ּ���",
	 * "�̽ʿ� ���ϱ� �̽ʿ���? ���ڷ��Է����ּ���", "�̽��� ���ϱ� �̽�����? ���ڷ��Է����ּ���",
	 * "�̽�ĥ ���ϱ� �̽�ĥ��? ���ڷ��Է����ּ���", "�̽��� ���ϱ� �̽�����? ���ڷ��Է����ּ���",
	 * "�̽ʱ� ���ϱ� �̽ʱ���? ���ڷ��Է����ּ���", "��� ���ϱ� �����? ���ڷ��Է����ּ���",
	 * "����� ���ϱ� �������? ���ڷ��Է����ּ���", "����� ���ϱ� �������? ���ڷ��Է����ּ���",
	 * "��ʻ� ���ϱ� ��ʻ���? ���ڷ��Է����ּ���", "��ʻ� ���ϱ� ��ʻ���? ���ڷ��Է����ּ���",
	 * "��ʿ� ���ϱ� ��ʿ���? ���ڷ��Է����ּ���", "����� ���ϱ� �������? ���ڷ��Է����ּ���",
	 * "���ĥ ���ϱ� ���ĥ��? ���ڷ��Է����ּ���", "����� ���ϱ� �������? ���ڷ��Է����ּ���",
	 * "��ʱ� ���ϱ� ��ʱ���? ���ڷ��Է����ּ���", "��� ���ϱ� �����? ���ڷ��Է����ּ���",
	 * "����� ���ϱ� �������? ���ڷ��Է����ּ���", "����� ���ϱ� �������? ���ڷ��Է����ּ���",
	 * "��ʻ� ���ϱ� ��ʻ���? ���ڷ��Է����ּ���", "��ʻ� ���ϱ� ��ʻ���? ���ڷ��Է����ּ���",
	 * "��ʿ� ���ϱ� ��ʿ���? ���ڷ��Է����ּ���", "����� ���ϱ� �������? ���ڷ��Է����ּ���",
	 * "���ĥ ���ϱ� ���ĥ��? ���ڷ��Է����ּ���", "����� ���ϱ� �������? ���ڷ��Է����ּ���",
	 * "��ʱ� ���ϱ� ��ʱ���? ���ڷ��Է����ּ���", "���� ���ϱ� ������? ���ڷ��Է����ּ���",};
	 */
	public static final String[] quize = { "�ָԹ�", "ȭ���", "�����", "���ڱ�", "ȭ����",
			"���̹�", "����Ʈ", "���ܾ�", "��Ӵ�", "�ƹ���", "�ô���", "��쳪", "�����", "���θ�",
			"īī��", "Ŀ����", "�ݶ�", "������", "���ɸ�", "����Ű", "������", "������", "Ŭ�ι�",
			"������", "���̾�", "���Ĺ�", "����", "å����", "������", "ȭ����", "������", "�����",
			"�ݿ���", "�����", "�Ͽ���", "��Ÿ��", "������", "ȣ����", "������", "��õ��", "�Ϲݼ�",
			"������", "�����", "��λ�", "���ǻ�", "�ڵ���", "����Ʈ", "�̹���", "����", "������" };

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

			if (map.equalsIgnoreCase("ȭ��")) {
				minx = 33472;
				maxx = 33856;
				miny = 32191;
				maxy = 32511;
			} else if (map.equalsIgnoreCase("���̳�")) {
				minx = 33280;
				maxx = 33792;
				miny = 33023;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("��ũ��")) {
				minx = 32511;
				maxx = 32960;
				miny = 32191;
				maxy = 32537;
			} else if (map.equalsIgnoreCase("�۷���")) {
				minx = 32512;
				maxx = 32960;
				miny = 32537;
				maxy = 33023;
			} else if (map.equalsIgnoreCase("���ٿ��")) {
				minx = 32512;
				maxx = 32960;
				miny = 33023;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("���")) {
				minx = 33216;
				maxx = 33472;
				miny = 32191;
				maxy = 32511;
			} else if (map.equalsIgnoreCase("����")) {
				minx = 33856;
				maxx = 34304;
			} else if (map.equalsIgnoreCase("��Ʈ")) {
				minx = 32960;
				maxx = 33280;
				miny = 32511;
				maxy = 33087;
			} else if (map.equalsIgnoreCase("���")) {
				minx = 33280;
				maxx = 33920;
				miny = 32511;
				maxy = 33023;
			} else if (map.equalsIgnoreCase("�佣")) {
				minx = 32960;
				maxx = 33216;
				miny = 32191;
				maxy = 32511;
			} else if (map.equalsIgnoreCase("�Ƶ�")) {
				minx = 33792;
				maxx = 34304;
				miny = 32739;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("�����")) {
				minx = 32896;
				maxx = 33311;
				miny = 33023;
				maxy = 33535;
			} else if (map.equalsIgnoreCase("����") || map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("���") || map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("���") || map.equalsIgnoreCase("���")
					|| map.equalsIgnoreCase("����") || map.equalsIgnoreCase("���")
					|| map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("���ž")
					|| map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("��Ʈ����")
					|| map.equalsIgnoreCase("�������")
					|| map.equalsIgnoreCase("���̳׼���")
					|| map.equalsIgnoreCase("�Ƶ�����")
					|| map.equalsIgnoreCase("ħ��")
					|| map.equalsIgnoreCase("������")
					|| map.equalsIgnoreCase("���") || map.equalsIgnoreCase("�׽�")
					|| map.equalsIgnoreCase("���") || map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("��") || map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("����") || map.equalsIgnoreCase("����")
					|| map.equalsIgnoreCase("����") || map.equalsIgnoreCase("�׺�")) {
				dun = true;
			}

			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {

				// int q_rnd = rnd.nextInt(quize.length);
				// String q = quize[q_rnd];
				// String d = ""+((1+q_rnd)*2);
				String q = Config.�ڵ�����_����;// "���� �� �Է��� "+quize[q_rnd]+" ��� ���ּ���";
				String d = Config.�ڵ�����_��;// quize[q_rnd];

				/*
				 * int rnd111 = rnd.nextInt(8); switch (rnd111) { case 0: q =
				 * "�Ŀ��׸��̶�� �Է����ּ���"; d = "�Ŀ��׸�"; break; case 1: q =
				 * "�ڵ����������Դϴ�.ä��â���丶ȣũ����Է����ּ���"; d = "�丶ȣũ"; break; case 2: q =
				 * "����ϴµ��˼��մϴ��Է��ؾ��Ҵ������Դϴ�"; d = "��"; break; case 3: q =
				 * "�ڵ��������Դϴ������̷����̶���Է����ּ���"; d = "�̷���"; break; case 4: q =
				 * "������Ʈ������Ʈ������Ʈ�ڵ����������ؼ�����Ʈ���Է����ּ���"; d = "������Ʈ"; break; case 5:
				 * q = "���ݾ�𼭻���ϰ��Ű���?�ڵ��������̴��������Է����ּ���"; d = "����"; break; case
				 * 6: q = "���ڼ������̿����ּż�����帳�ϴ��Է�â�����ڼ�������Է����ּ���"; d = "���ڼ���";
				 * break; case 7: q = "�̹������������մϴ�������ڵ�üũ���̴Ϻ����Ͻô����ڵ��ƴ����Է����ּ���";
				 * d = "�ڵ��ƴ�"; break; default: break; }
				 */
				// if(pc.isGm())
				// pc.sendPackets(new S_SystemMessage(map+" > "+q+" > "+d),
				// true);

				if (dun) {
					if (map.equalsIgnoreCase("����")) {
						if (1 == pc.getMapId() || 2 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (807 >= pc.getMapId() && 813 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���")) {
						if (19 >= pc.getMapId() && 21 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (23 >= pc.getMapId() && 24 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���")) {
						if (25 >= pc.getMapId() && 28 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���")) {
						if (30 >= pc.getMapId() && 37 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if ((43 >= pc.getMapId() && 51 <= pc.getMapId())
								|| (541 >= pc.getMapId() && 543 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���")) {
						if (53 >= pc.getMapId() && 57 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (59 >= pc.getMapId() && 63 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���ž")) {
						if ((75 >= pc.getMapId() && 82 <= pc.getMapId())
								|| (280 >= pc.getMapId() && 289 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (101 >= pc.getMapId() && 200 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("��Ʈ����")) {
						if (240 >= pc.getMapId() && 243 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("�������")) {
						if (248 >= pc.getMapId() && 251 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���̳׼���")) {
						if (252 >= pc.getMapId() && 254 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("�Ƶ�����")) {
						if (257 >= pc.getMapId() && 259 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("ħ��")) {
						if (307 >= pc.getMapId() && 309 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("������")) {
						if ((440 >= pc.getMapId() && 445 <= pc.getMapId())
								|| (480 >= pc.getMapId() && 484 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("��")) {
						if (400 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (410 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (420 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (430 == pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���")) {
						if ((451 >= pc.getMapId() && 479 <= pc.getMapId())
								|| (490 >= pc.getMapId() && 496 <= pc
										.getMapId())
								|| (530 >= pc.getMapId() && 537 <= pc
										.getMapId())) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("�׽�")) {
						if (521 >= pc.getMapId() && 524 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("����")) {
						if (550 >= pc.getMapId() && 558 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("���")) {
						if (600 >= pc.getMapId() && 608 <= pc.getMapId()) {
						} else
							continue;
					} else if (map.equalsIgnoreCase("�׺�")) {
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
						if (map.equalsIgnoreCase("����")) {

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
	 * ������ �����
	 */
	public class AutoCheckPrison implements Runnable {

		private static final int sleep2 = 60000;

		public AutoCheckPrison() {
		}

		@Override
		public void run() {
			// TODO �ڵ� ������ �޼ҵ� ����
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
						 * String q = "���� �� �Է��� "+quize[q_rnd]+" ��� ���ּ���";
						 * String d = quize[q_rnd];
						 */
						String q = Config.�ڵ�����_����;
						String d = Config.�ڵ�����_��;

						/*
						 * int rnd111 = rnd.nextInt(8); switch (rnd111) { case
						 * 0: q = "�Ŀ��׸��̶�� �Է����ּ���"; d = "�Ŀ��׸�"; break; case 1: q
						 * = "�ڵ����������Դϴ�.ä��â���丶ȣũ����Է����ּ���"; d = "�丶ȣũ"; break;
						 * case 2: q = "����ϴµ��˼��մϴ��Է��ؾ��Ҵ������Դϴ�"; d = "��"; break;
						 * case 3: q = "�ڵ��������Դϴ������̷����̶���Է����ּ���"; d = "�̷���";
						 * break; case 4: q = "������Ʈ������Ʈ������Ʈ�ڵ����������ؼ�����Ʈ���Է����ּ���";
						 * d = "������Ʈ"; break; case 5: q =
						 * "���ݾ�𼭻���ϰ��Ű���?�ڵ��������̴��������Է����ּ���"; d = "����"; break;
						 * case 6: q = "���ڼ������̿����ּż�����帳�ϴ��Է�â�����ڼ�������Է����ּ���"; d =
						 * "���ڼ���"; break; case 7: q =
						 * "�̹������������մϴ�������ڵ�üũ���̴Ϻ����Ͻô����ڵ��ƴ����Է����ּ���"; d = "�ڵ��ƴ�";
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
	 * �Ϲ� ���� ����ó��
	 */
	public class AutoCheck implements Runnable {
		private LineageClient lc;

		public AutoCheck(LineageClient cl) {
			lc = cl;
		}

		@Override
		public void run() {
			// TODO �ڵ� ������ �޼ҵ� ����
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

				// ����!
				String ip = lc.getIp();
				String account = lc.getAccountName();
				lc.AutoCheck = true;
				lc.AutoCheckCount = 0;

				for (int ii = 0; ii < 2; ii++) {
					// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
					// "�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."), true);
					// lc.sendPacket(new
					// S_SystemMessage("�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."),
					// true);
					lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
							"�ڵ� ���� : " + lc.AutoQuiz), true);
					lc.sendPacket(
							new S_SystemMessage("�ڵ� ���� : " + lc.AutoQuiz), true);
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
						|| lc.getActiveChar().�ڴ��()) {
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
				// "�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���. [����!! �̹��� ���Է½� ������ ���ϴ�.]"),
				// true);
				// lc.sendPacket(new
				// S_SystemMessage("�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."),
				// true);
				// lc.sendPacket(new
				// S_SystemMessage("�ڵ� ���� : [����!! �̹��� ���Է½� ������ ���ϴ�.]"), true);
				lc.sendPacket(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�ڵ� ���� : "
								+ lc.AutoQuiz + " [����!! �̹��� ���Է½� ������ ���ϴ�.]"),
						true);
				lc.sendPacket(new S_SystemMessage("�ڵ� ���� : " + lc.AutoQuiz),
						true);
				lc.sendPacket(new S_SystemMessage(
						"�ڵ� ���� : [����!! �̹��� ���Է½� ������ ���ϴ�.]"), true);
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
				lc.sendPacket(new S_SystemMessage("�ڵ� ���� ���� �Է����� �����̽��ϴ�."),
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
							|| lc.getActiveChar().�ڴ��()) {
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
							32928, 32864, 6202), 5, true);// �� ��Ŷ �̻��
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
	 * ������ ����ó��
	 */
	public class AutoCheckPrison2 implements Runnable {
		private LineageClient lc;

		public AutoCheckPrison2(LineageClient cl) {
			lc = cl;
		}

		@Override
		public void run() {
			// TODO �ڵ� ������ �޼ҵ� ����
			try {
				if (lc.close)
					return;
				if (lc.getActiveChar() == null)
					return;
				else if (lc.getActiveChar().isGm())
					return;

				// ����!
				lc.AutoCheck = true;
				lc.AutoCheckCount = 0;
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."), true);
				// lc.sendPacket(new
				// S_SystemMessage("�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."),
				// true);
				lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						"�ڵ� ���� : " + lc.AutoQuiz), true);
				lc.sendPacket(new S_SystemMessage("�ڵ� ���� : " + lc.AutoQuiz),
						true);
				Thread.sleep(10000);
				if (!lc.AutoCheck || lc.close)
					return;
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."), true);
				// lc.sendPacket(new
				// S_SystemMessage("�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."),
				// true);
				lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						"�ڵ� ���� : " + lc.AutoQuiz), true);
				lc.sendPacket(new S_SystemMessage("�ڵ� ���� : " + lc.AutoQuiz),
						true);
				Thread.sleep(10000);
				if (!lc.AutoCheck || lc.close)
					return;
				// lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				// "�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."), true);
				// lc.sendPacket(new
				// S_SystemMessage("�ڵ� ���� : [ "+lc.AutoQuiz+" ] ���� ä��â�� �Է����ּ���."),
				// true);
				lc.sendPacket(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
						"�ڵ� ���� : " + lc.AutoQuiz), true);
				lc.sendPacket(new S_SystemMessage("�ڵ� ���� : " + lc.AutoQuiz),
						true);
				Thread.sleep(10000);
				if (!lc.AutoCheck || lc.close)
					return;
				lc.sendPacket(new S_SystemMessage("�ڵ� ���� ���� �Է����� �����̽��ϴ�."),
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
			 * String q = "���� �� �Է��� "+quize[q_rnd]+" ��� ���ּ���"; String d =
			 * quize[q_rnd];
			 */
			String q = Config.�ڵ�����_����;
			String d = Config.�ڵ�����_��;
			/*
			 * int rnd111 = rnd.nextInt(8); switch (rnd111) { case 0: q =
			 * "�Ŀ��׸��̶�� �Է����ּ���"; d = "�Ŀ��׸�"; break; case 1: q =
			 * "�ڵ����������Դϴ�.ä��â���丶ȣũ����Է����ּ���"; d = "�丶ȣũ"; break; case 2: q =
			 * "����ϴµ��˼��մϴ��Է��ؾ��Ҵ������Դϴ�"; d = "��"; break; case 3: q =
			 * "�ڵ��������Դϴ������̷����̶���Է����ּ���"; d = "�̷���"; break; case 4: q =
			 * "������Ʈ������Ʈ������Ʈ�ڵ����������ؼ�����Ʈ���Է����ּ���"; d = "������Ʈ"; break; case 5: q =
			 * "���ݾ�𼭻���ϰ��Ű���?�ڵ��������̴��������Է����ּ���"; d = "����"; break; case 6: q =
			 * "���ڼ������̿����ּż�����帳�ϴ��Է�â�����ڼ�������Է����ּ���"; d = "���ڼ���"; break; case 7: q
			 * = "�̹������������մϴ�������ڵ�üũ���̴Ϻ����Ͻô����ڵ��ƴ����Է����ּ���"; d = "�ڵ��ƴ�"; break;
			 * default: break; }
			 */

			pc.getNetConnection().AutoQuiz = q;
			pc.getNetConnection().AutoAnswer = d;
			GeneralThreadPool.getInstance().schedule(
					new AutoCheck(pc.getNetConnection()), 1);
		}

	}

}
