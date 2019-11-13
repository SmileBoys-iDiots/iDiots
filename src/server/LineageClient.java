package server;

import static l1j.server.server.Opcodes.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.PacketHandler;
import l1j.server.server.PacketOutput;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.UByte8;
import l1j.server.server.types.UChar8;
import l1j.server.server.utils.SQLUtil;

import org.apache.mina.core.session.IoSession;

import server.manager.eva;
import server.mina.coder.LineageEncryption;
import xnetwork.ConnectionHandler;
import xnetwork.SelectorThread;

public class LineageClient implements ConnectionHandler, PacketOutput {

	private static Logger _log = Logger.getLogger(LineageClient.class.getName());

	private static Random _rnd = new Random(System.nanoTime());
	// 세션 키값
	public static final String CLIENT_KEY = "CLIENT";
	// 클라 관리용 세션
	// 암호화용
	// 접속중인 케릭터
	private L1PcInstance activeCharInstance;
	// 클라 닫혓는지 체크

	public boolean AutoCheckTel = false;
	public boolean close = false;
	
	public boolean AutoPhoneCheck = false;
	public String AutoPhoneQuiz = "";
	public byte AutoPhoneCheckCount = 0;

	public boolean AutoCheck = false;
	public String AutoAnswer = "";
	public String AutoQuiz = "";
	public byte AutoCheckCount = 0;

	private int loginStatus = 0;

	private boolean charRestart = true;
	private int _loginfaieldcount = 0;
	private Account account;

	public boolean 제작템패킷전송 = false;
	public boolean 제작템패킷전송중 = false;

	public boolean 인형합성패킷전송;
	public boolean 인형합성패킷전송중;
	
	/** 인터 서버 체크 용  */
	public boolean getletteron() {
		return _letteron;
	}

	public void setletteron(boolean letteron) {
		_letteron = letteron;
	}
	
	private boolean _letteron = false;
	
	/** 인터 서버 체크 용  */

	/**
	 * LineageClient 생성자
	 * 
	 * @param session
	 * @param key
	 */

	/*
	 * public synchronized void sendPacket(ServerBasePacket bp){
	 * if(bp.getLength() <= 2){ return; } if(패킷로그){ 행동로그(bp); }
	 * session.write(bp); } public synchronized void sendPacket(ServerBasePacket
	 * bp, boolean ck){ if(bp.getLength() <= 2){ return; } if(패킷로그){ 행동로그(bp); }
	 * session.write(bp); if(ck){ bp.clear(); bp = null; } }
	 */
	@Override
	public void onDisconnect(xnetwork.Connection connection) {
		// System.out.println("종료");
		try {
			if (activeCharInstance != null) {

				if (activeCharInstance.isPinkName()
						|| activeCharInstance.isParalyzed()
						|| activeCharInstance.getSkillEffectTimerSet()
								.hasSkillEffect(L1SkillId.SHOCK_STUN)
								|| activeCharInstance.getSkillEffectTimerSet()
								.hasSkillEffect(L1SkillId.FORCE_STUN)
								|| activeCharInstance.getSkillEffectTimerSet()
								.hasSkillEffect(L1SkillId.엠파이어)
								|| activeCharInstance.getSkillEffectTimerSet()
								.hasSkillEffect(L1SkillId.PANTERA)) {
					GeneralThreadPool.getInstance().schedule(
							new StunExitDelay(activeCharInstance, this), 3000);
				} else {
					Config._quit_Q.requestWork(activeCharInstance);
					// quitGame(activeCharInstance);
					if (!(activeCharInstance.getInventory()
							.checkItem(999998, 1) || activeCharInstance
							.getInventory().checkItem(999999, 1))) { // 무인pc(쿠우)
						activeCharInstance.logout();
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			LoginController.getInstance().logout(this);
			setActiveChar(null);
		}
	}

	@Override
	public void onRecv(xnetwork.Connection connection, ByteBuffer buffer) {
		try {
			while (true) {
				buffer.mark();

				if (buffer.remaining() < 2) // size 2byte
				{
					// System.out.println(buffer.remaining());
					buffer.reset();
					break;
				}

				int hiByte = UChar8.fromUByte8(buffer.get());
				int loByte = UChar8.fromUByte8(buffer.get());

				int dataLength = (loByte * 256 + hiByte) - 2;

				if (dataLength > 4 * 1024) {
					// System.out.println("22222222222222");
					buffer.reset();
					connection.close();
					return;
				}

				if (buffer.remaining() < dataLength) {
					// System.out.println("3233333333333333333");
					buffer.reset();
					break;
				}

				byte data[] = new byte[dataLength];

				buffer.get(data, 0, dataLength);

				data = le.decrypt(data);

				onPacket(connection, data);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			connection.close();
		}
	}
	
	public void setLoginAvailable() {
		loginStatus = 1;
	}

	// 감자거 맞고요
	public void onPacket(xnetwork.Connection connection, byte[] data) {
		try {

			int opcode = data[0] & 0xFF;
			if (gaming && opcode == Opcodes.C_LOGIN) {
				return;
			}
			if (패킷로그) {
				행동로그(opcode, data.length, data);
			}

			if (opcode == Opcodes.C_READ_NEWS
					|| opcode == Opcodes.C_RESTART) {
				loginStatus = 1;
				if (opcode == Opcodes.C_RESTART)
					gaming = false;
			} else if (opcode == Opcodes.C_ONOFF) {
				loginStatus = 0;
				gaming = true;
			} else if (opcode == Opcodes.C_LOGIN) {
				DDos.Check = true;
			} else if (opcode == Opcodes.C_ENTER_WORLD) {
				/*if (data.length < 20) {
					System.out.println("오토 입니다.");
					return;
				}*/
				if (loginStatus != 1)
					return;
			}
			_executor.requestWork(data);
		} catch (Exception e) {
			e.printStackTrace();
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onSend(xnetwork.Connection connection) {
		// do nothing

	}

	public void close() {
		if (Config.새로운패킷구조) {
			_connection.close();
		} else {
			if (!close) {
				close = true;
				try {
					if (activeCharInstance != null) {
						if (activeCharInstance.isParalyzed()
								|| activeCharInstance.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.SHOCK_STUN)
										|| activeCharInstance.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.엠파이어)
										|| activeCharInstance.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.FORCE_STUN)
										|| activeCharInstance.getSkillEffectTimerSet()
										.hasSkillEffect(L1SkillId.PANTERA)) {
							GeneralThreadPool.getInstance()
									.schedule(
											new StunExitDelay(
													activeCharInstance, this),
											3000);
						} else {
							quitGame(activeCharInstance);
							synchronized (activeCharInstance) {
								 if (!(activeCharInstance.getInventory().checkItem(999998, 1) || activeCharInstance.getInventory().checkItem(999999, 1))) { // 무인pc(쿠우)
									activeCharInstance.logout();
								}
								setActiveChar(null);
							}
						}
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
				try {
					LoginController.getInstance().logout(this);
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
				try {
					_session.close();
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
		}
	}

	public synchronized void sendPacket(ServerBasePacket packet, boolean ok) {
		if (Config.새로운패킷구조) {
			sendPacket(packet);
			if (ok) {
				packet.clear();
			}
		} else {
			if (패킷로그) {
				행동로그(packet);
			}
			_session.write(packet);
			if (ok) {
				packet.clear();
				packet = null;
			}
		}
	}

	private String gogo1 = "";

	public byte[] encryptE(byte[] data) {
		try {

			if (Config.서버패킷출력) {
				if (getActiveChar() != null) {
					System.out.println("PC NAME : " + "["
							+ getActiveChar().getName() + "]");
				} else {
					System.out.println("PC NAME : 미접속 중입니다.");
				}
				int opcode = data[0] & 0xFF;
				System.out.println("[NEW] S -> C [" + opcode + "]\n"
						+ DataToPacket(data, data.length)); // 사용 처리
			}
			// System.out.println(data[0]);
			// if(data[0] == (byte)0xc8)
			//

			char[] ac = new char[data.length];
			ac = UChar8.fromArray(data);
			ac = le.encrypt(ac);
			data = UByte8.fromArray(ac);
			// data = le.encryptsabu(data, data.length);
			// System.out.println("encPacket \n"+printData(data, data.length));
			// // 사용 처리
			return data;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public synchronized void sendPacket(ServerBasePacket packet) {
		if (Config.새로운패킷구조) {
			try {
				byte[] abyte0 = packet.getContent();
				if (abyte0 == null) {
					return;
				}
				if (abyte0.length < 1) {
					return;
				}
				int opcode = abyte0[0] & 0xFF;
				if (Config.서버패킷출력) {
					if (getActiveChar() != null) {
						System.out.println("PC NAME : " + "["
								+ getActiveChar().getName() + "]");
					} else {
						System.out.println("PC NAME : 미접속 중입니다.");
					}
					System.out.println("[NEW] S -> C [" + opcode + "]\n"
							+ DataToPacket(abyte0, abyte0.length)); // 사용 처리
				}
				char[] ac = new char[abyte0.length];
				ac = UChar8.fromArray(abyte0);
				if (abyte0.length < 2) {
					return;
				}
				ac = le.encrypt(ac);
				abyte0 = UByte8.fromArray(ac);
				int j = abyte0.length + 2;

				byte[] buffer = new byte[j];
				buffer[0] = (byte) (j & 0xff);
				buffer[1] = (byte) (j >> 8 & 0xff);

				System.arraycopy(abyte0, 0, buffer, 2, abyte0.length);

				_connection.send(buffer);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		} else {
			if (패킷로그) {
				행동로그(packet);
			}
			_session.write(packet);
		}
	}
	
	private DDos DDos = new DDos();
	
	public void setDDosCheck(boolean DDosCheck) {
		DDos.Check = DDosCheck;
	}

	public class DDos extends TimerTask {
		public boolean Check = false;
		private xnetwork.Connection _session;
		private IoSession _minasession;

		public DDos() {
		}

		public void start(xnetwork.Connection Session) {
			_session = Session;
			Timer timer = new Timer();
			timer.schedule(this, 1 * 90 * 1000);// 30초안에 로그인없으면 절단
		}

		public void start(IoSession Session) {
			_minasession = Session;
			Timer timer = new Timer();
			timer.schedule(this, 1 * 120 * 1000);// 30초안에 로그인없으면 절단
		}

		@Override
		public void run() {

			if (Check) {
				cancel();
				return;
			}

			if (_session != null && !_session.isOpen()) {
				cancel();
				return;
			}

			if (_minasession != null && _minasession.isClosing()) {
				cancel();
				return;
			}
			close();
		}
	}

	private PacketExecutor _executor;
	private String _ip;
	private String _hostname;

	public LineageClient(SocketChannel socketChannel, SelectorThread selector) throws IOException {
		Socket socket = socketChannel.socket();
		if (socket.getPort() != 0) {
			_ip = socket.getInetAddress().getHostAddress();
			_hostname = _ip;
			_executor = new PacketExecutor();
			start(socketChannel, selector);
		}
	}

	private PacketHandler packetHandler;
	private IoSession _session;
	public receivePacketer pck;
	private LineageEncryption le;

	public LineageClient(IoSession session, long key) {
		StringTokenizer st = new StringTokenizer(session.getRemoteAddress()
				.toString().substring(1), ":");
		_ip = st.nextToken();
		_hostname = _ip;

		DDos.start(session);

		_session = session;
		le = new server.mina.coder.LineageEncryption();
		le.initKeys(key);

		packetHandler = new PacketHandler(this);

		pck = new receivePacketer();
	}

	private static boolean sss = false;
	// public String selectCharName;
	public class receivePacketer implements Runnable {
		private final Queue<byte[]> _queue;
		private boolean on = false;
		private ByteArrayOutputStream bao = null;
		private byte packetCount = 0;
		private long packetLastTime = 0;
		
		public receivePacketer() {
			_queue = new ConcurrentLinkedQueue<byte[]>();
			
			if(!sss)
			{
				sss = true;
			}
		}

		public void requestWork(byte data[]) {
			packetCount++;
			if (packetCount > 100) {
				System.out.println(" 과도한 패킷 전송 차단 - 공격 의심 ---  IP : " + getIp());
				close();
				return;
			}
			// System.out.println(System.currentTimeMillis() - packetLastTime);
			// packetLastTime = System.currentTimeMillis();
			if (System.currentTimeMillis() - packetLastTime > 500) {
				packetCount = 0;
				packetLastTime = System.currentTimeMillis();
			}
			_queue.offer(data);
			if (!on) {
				on = true;
				GeneralThreadPool.getInstance().execute(this);
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (this) {
						if (_session.isClosing())
							break;
						byte[] data = _queue.poll();
						if (data == null) {
							if (bao != null)
								continue;
							else
								break;
						}
						int dataLength = 0;

						if (bao != null) {
							// System.out.println("꺼낸 데이타 "+data.length);
							bao.write(data);
							data = bao.toByteArray();
							bao.close();
							bao = null;
							// System.out.println("합친후 데이타 "+data.length);
						}
						while (dataLength < data.length) {
							// System.out.println(dataLength+" > "+data.length);
							if (dataLength >= data.length)
								break;

							int hiByte = (char) (data[0] & 0xFF);
							int loByte = (char) (data[1] & 0xFF);
							int length = (loByte * 256 + hiByte) - 2;

							// System.out.println(length+2);
							if (length <= 0 || length > 1024 * 4)
								break;

							if (dataLength + length + 2 > data.length) {
								int remainSize = data.length - dataLength;
								// System.out.println("합쳐야될 데이타 "+remainSize);
								bao = new ByteArrayOutputStream();
								for (int i = 0; i < remainSize; i++) {
									bao.write(data[i] & 0xFF);
								}
								break;
							}
							byte[] temp = new byte[length];
							System.arraycopy(data, 2, temp, 0, length);
	
							dataLength += (length + 2);
							System.arraycopy(data, length + 2, data, 0, data.length - (length + 2));
							
							byte buf1[];

							/*
							 * if(!gaming && (temp[0] & 0xFF) ==
							 * Opcodes.C_OPCODE_LOGINPACKET && (temp[1] & 0xFF)
							 * == Opcodes.C_OPCODE_LOGINPACKET){ buf1 = temp;
							 * }else{
							 */
							if (temp.length < 4)
								return;

							 buf1 = le.decrypt(temp);   // 이건 기존 코드
							// System.out.println("dec C -> S \n"+DataToPacket(buf1,
							 //buf1.length)); // 사용 처리
							
							if (gaming
									&& (buf1[0] & 0xFF) == Opcodes.C_LOGIN) {
								continue;
							}
							// }

							int opcode = buf1[0] & 0xFF;
							if (패킷로그) {
								행동로그(opcode, buf1.length, buf1);
							}

							/*
							 * if(logok == 0){ 로그인창로그(opcode, buf1.length,
							 * buf1); }
							 */
							if (opcode == Opcodes.C_READ_NEWS
									|| opcode == Opcodes.C_RESTART) {
								loginStatus = 1;
								if (opcode == Opcodes.C_RESTART)
									gaming = false;
							} else if (opcode == Opcodes.C_ONOFF ) {
								loginStatus = 0;
								gaming = true;
							} else if (opcode == Opcodes.C_LOGIN) {
								DDos.Check = true;
								// 로그인창로그저장();
							} else if (opcode == Opcodes.C_ENTER_WORLD) {
								/*if (buf1.length < 20) {
									// System.out.println("C -> S ["+opcode+"]\n"+DataToPacket(buf1,
									// buf1.length)); // 사용 처리
									System.out.println("오토 입니다.");
									return;
								}*/
								if (loginStatus != 1)
									continue;
							}
							packetHandler
									.handlePacket(buf1, activeCharInstance);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("종료");
			on = false;
		}
	}

	private xnetwork.Connection _connection;

	protected void start(SocketChannel socketChannel, SelectorThread selector) throws IOException {
		_connection = new xnetwork.Connection(socketChannel, selector, this);

		long seed = Config.SeedVal;

		_connection.send(Config.FIRST_PACKET);
		le = new server.mina.coder.LineageEncryption();
		le.initKeys(seed);

		_connection.resumeRecv();

		DDos.start(_connection);
	}

	class PacketExecutor implements Runnable {
		private final Queue<byte[]> _queue;
		private AtomicInteger _jobCount;
		private AtomicInteger _attackPendingCount;
		private AtomicInteger _skillPendingCount;
		private PacketHandler _handler;

		private int _requestCount;
		private long _checkTime;
		private boolean _attack;

		PacketExecutor() {
			_queue = new ConcurrentLinkedQueue<byte[]>();
			_handler = new PacketHandler(LineageClient.this);
			_jobCount = new AtomicInteger(0);
			_attackPendingCount = new AtomicInteger(0);
			_skillPendingCount = new AtomicInteger(0);
			_requestCount = 0;
			_checkTime = 0;
			_attack = false;
		}

		public void requestWork(byte data[]) {
			if (_attack) {
				return;
			}

			L1PcInstance pc = getActiveChar();

			++_requestCount;

			if (System.currentTimeMillis() - _checkTime > 1000) {
				if (_requestCount > 100) {
					_attack = true;
					_connection.close();

					for (L1PcInstance listner : L1World.getInstance()
							.getAllPlayers()) {
						if (listner.isGm()) {
							if (pc != null) {
								listner.sendPackets(new S_SystemMessage("["
										+ pc.getName() + "] 공격 의심. 확인해주세요."));
							} else {
								listner.sendPackets(new S_SystemMessage("["
										+ getIp() + "] 공격 의심. 확인해주세요."));
							}
						}
					}

					return;
				}

				_requestCount = 0;
				_checkTime = System.currentTimeMillis();
			}

			int opcode = data[0] & 0xFF;
			if (opcode == Opcodes.C_ATTACK
					|| opcode == Opcodes.C_FAR_ATTACK) {
				if (_attackPendingCount.get() > 3) {
					return;
				} else {
					_attackPendingCount.incrementAndGet();
				}
			} else if (opcode == Opcodes.C_USE_SPELL) {
				if (_skillPendingCount.get() > 2) {
					return;
				} else {
					_skillPendingCount.incrementAndGet();
				}
			}

			_queue.offer(data);

			if (_jobCount.getAndIncrement() == 0) {
				GeneralThreadPool.getInstance().execute(this);
			}
		}

		@Override
		public void run() {

			while (true) {
				boolean needToBreak = false;
				L1PcInstance pc = getActiveChar();
				if (_attack) {
					return;
				}

				try {
					synchronized (this) // 타이밍 문제로 2번 진입 가능하다. 매우 낮은 확률이긴 하다만.
										// 이론상.-_-
					{
						byte[] data = _queue.peek();
						if (data == null) {
							return;
						}
						int opcode = data[0] & 0xFF;

						if (opcode == Opcodes.C_ATTACK
								|| opcode == Opcodes.C_FAR_ATTACK) {
							_attackPendingCount.decrementAndGet();
						} else if (opcode == Opcodes.C_USE_SPELL) {
							_skillPendingCount.decrementAndGet();
						}
						needToBreak = false;

						_queue.remove();

						if (_jobCount.decrementAndGet() == 0) {
							needToBreak = true;
						}

						_handler.handlePacket(data, pc);

						if (needToBreak) {
							break;
						}
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					if (needToBreak) {
						break;
					}
				}
			}
		}
	}

	/*
	 * class NullPacketCarrier extends RepeatTask { public NullPacketCarrier() {
	 * super(2000); // 2초마다 한번씩 } public void start() {
	 * GeneralThreadPool.getInstance().schedule(NullPacketCarrier.this, 3000);
	 * // 3초부터 시작. }
	 * 
	 * @Override public void execute() { try { if (!_connection.isOpen()) {
	 * cancel(); return; } //로그인창에서 이패킷으로 인해 문제가됨... if(getActiveChar() == null)
	 * return; long serverTime =
	 * GameTimeClock.getInstance().getGameTime().getSeconds(); sendPacket(new
	 * S_GameTime(serverTime)); } catch (Exception e) { _log.log(Level.SEVERE,
	 * e.getLocalizedMessage(), e); cancel(); } } }
	 */

	public void packetwaitgo(byte[] bb) {
		if (bb == null) {
			return;
		}
		try {
			if (Config.새로운패킷구조) {
				onPacket(_connection, bb);
			} else {
				packetHandler.handlePacket(bb, activeCharInstance);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * class AutoCheck implements Runnable{
	 * 
	 * @Override public void run() { // TODO 자동 생성된 메소드 스텁 if(close) return;
	 * 
	 * if(!Config.AUTO_CHECK){ GeneralThreadPool.getInstance().schedule(this,
	 * 60000*30); return; } if(getActiveChar() == null){
	 * GeneralThreadPool.getInstance().schedule(this, 60000); return; }else
	 * if(getActiveChar().isGm()) return; else
	 * if(CharPosUtil.getZoneType(getActiveChar()) == 1){
	 * GeneralThreadPool.getInstance().schedule(this,
	 * 60000*60+_rnd.nextInt(60000*60)); return; } //전송! int x =
	 * _rnd.nextInt(30); int y = _rnd.nextInt(30); AutoCheck = true;
	 * AutoCheckCount = 0; AutoAnswer = ""+(x+y); sendPacket(new
	 * S_PacketBox(S_PacketBox.GREEN_MESSAGE,
	 * "오토 방지 코드 [ "+x+" + "+y+" = ? ] 답을 입력해주세요."), true); sendPacket(new
	 * S_SystemMessage("오토 방지 코드 [ "+x+" + "+y+" = ? ] 답을 입력해주세요."), true);
	 * GeneralThreadPool.getInstance().schedule(this,
	 * 60000*60+_rnd.nextInt(60000*60));
	 * GeneralThreadPool.getInstance().schedule(new Runnable(){
	 * 
	 * @Override public void run() { // TODO 자동 생성된 메소드 스텁 if(!AutoCheck ||
	 * close) return; kick(); } }, 60000*3); } }
	 */

	/** 현재 상태를 끊는다 */
	public void kick() {
		try {
			sendPacket(new S_Disconnect());
			// _kick = 1;
			if (Config.새로운패킷구조) {
				_connection.close();
			} else {
				_session.close();
			}
			//
		} catch (Exception e) {
		}
	}
	
	/** 인터 서버에 대한 처리 부분 */
	public void ServerInterKick() {
		try {
			if (activeCharInstance != null)
				quitGame(activeCharInstance);
				synchronized (activeCharInstance) {
					setActiveChar(null);
				}
			if (Config.새로운패킷구조) {
				_connection.close();
			} else {
				_session.close(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/** 케릭터의 리스타트 여부 */
	public void CharReStart(boolean flag) {
		this.charRestart = flag;
	}

	/** 케릭터의 리스타트 여부 */
	public boolean CharReStart() {
		return charRestart;
	}

	/** 로그인 상태값을 변경한다 */
	public void setloginStatus(int i) {
		loginStatus = i;
	}

	/**
	 * 해당 패킷을 전송 한다.
	 * 
	 * @param bp
	 */

	/**
	 * 종료시 호출
	 */

	class StunExitDelay implements Runnable {
		private L1PcInstance pc;
		private LineageClient cl;

		public StunExitDelay(L1PcInstance _pc, LineageClient _cl) {
			pc = _pc;
			cl = _cl;
		}

		@Override
		public void run() {
			// TODO 자동 생성된 메소드 스텁
			try {
				quitGame(pc);
				synchronized (pc) {
					if (!pc.isPrivateShop()) {
						if (!(pc.getInventory().checkItem(999998, 1) || pc.getInventory().checkItem(999999, 1))) { // 무인pc(쿠우)
							pc.logout();
						}
						cl.setActiveChar(null);
					}
				}
			} catch (Exception e) {
			}
		}

	}

	public int getsbsize() {
		return sb2.length();
	}

	private StringBuffer sb2 = new StringBuffer();
	private StringBuffer _로그인창로그 = new StringBuffer();

	private ArrayList<String> packet = new ArrayList<String>();

	public void 로그인창로그저장() {  // text 파일로 저장되는건데  지금은  사용안하네요 
		로그인창로그저장(false);
	}

	public void 로그인창로그저장(boolean ck) {
		if (_로그인창로그.length() < 1)
			return;
		int cnt = 0;
		String ymd = Config.YearMonthDate2();
		ymd = ymd + "_로그인창로그_" + System.currentTimeMillis();
		File f = null;
		f = new File("userlog");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		f = new File("userlog/packet");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		f = new File("userlog/packet/" + ymd);
		if (!f.isDirectory()) {
			f.mkdir();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH_mm_ss");
		String time = dateFormat.format(new Timestamp(System
				.currentTimeMillis()));
		dateFormat = null;
		File f2 = null;
		String nm = null;
		while (f2 == null || f2.isFile()) {
			if (getActiveChar() != null) {
				f2 = new File("userlog/packet/" + ymd + "/" + time + "-"
						+ getActiveChar().getName() + "-" + getAccountName()
						+ "(" + _ip + ")" + cnt + ".txt");
				nm = "userlog/packet/" + ymd + "/" + time + "-"
						+ getActiveChar().getName() + "-" + getAccountName()
						+ "(" + _ip + ")" + cnt + ".txt";
			} else if (getAccount() != null) {
				f2 = new File("userlog/packet/" + ymd + "/" + time + "-"
						+ getAccountName() + "(" + _ip + ")" + cnt + ".txt");
				nm = "userlog/packet/" + ymd + "/" + time + "-"
						+ getAccountName() + "(" + _ip + ")" + cnt + ".txt";
			} else {
				f2 = new File("userlog/packet/" + ymd + "/" + time + "-" + "("
						+ _ip + ")" + cnt + ".txt");
				nm = "userlog/packet/" + ymd + "/" + time + "-" + "(" + _ip
						+ ")" + cnt + ".txt";
			}
			cnt++;
		}

		try {
			if (!f2.exists()) {
				f2.createNewFile();
			}
			packet.add(_로그인창로그.toString());
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(nm));
			byte[] data = null;
			data = new byte[bis.available()];
			bis.read(data, 0, data.length);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(nm));
			bos.write(data);
			for (String s : packet) {
				bos.write(s.getBytes());
			}
			bos.flush();
			bos.close();
			bis.close();
			packet.clear();
			_로그인창로그.delete(0, _로그인창로그.capacity());
		} catch (Exception e) {
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(nm));
				bos.close();
			} catch (Exception e2) {
			}
		}

	}

	public void 행동로그저장() {
		행동로그저장(false);
	}

	public void 행동로그저장(boolean ck) {
		if (sb2.length() < 1)
			return;
		int cnt = 0;
		String ymd = Config.YearMonthDate2();
		if (ck) {
			ymd = ymd + "_화면로그_" + System.currentTimeMillis();
		}
		File f = null;
		f = new File("userlog");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		f = new File("userlog/packet");
		if (!f.isDirectory()) {
			f.mkdir();
		}
		f = new File("userlog/packet/" + ymd);
		if (!f.isDirectory()) {
			f.mkdir();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH_mm_ss");
		String time = dateFormat.format(new Timestamp(System
				.currentTimeMillis()));
		dateFormat = null;
		File f2 = null;
		String nm = null;
		while (f2 == null || f2.isFile()) {
			if (getActiveChar() != null) {
				f2 = new File("userlog/packet/" + ymd + "/" + time + "-"
						+ getActiveChar().getName() + "-" + getAccountName()
						+ "(" + _ip + ")" + cnt + ".txt");
				nm = "userlog/packet/" + ymd + "/" + time + "-"
						+ getActiveChar().getName() + "-" + getAccountName()
						+ "(" + _ip + ")" + cnt + ".txt";
			} else if (getAccount() != null) {
				f2 = new File("userlog/packet/" + ymd + "/" + time + "-"
						+ getAccountName() + "(" + _ip + ")" + cnt + ".txt");
				nm = "userlog/packet/" + ymd + "/" + time + "-"
						+ getAccountName() + "(" + _ip + ")" + cnt + ".txt";
			} else {
				f2 = new File("userlog/packet/" + ymd + "/" + time + "-" + "("
						+ _ip + ")" + cnt + ".txt");
				nm = "userlog/packet/" + ymd + "/" + time + "-" + "(" + _ip
						+ ")" + cnt + ".txt";
			}
			cnt++;
		}

		try {
			if (!f2.exists()) {
				f2.createNewFile();
			}
			packet.add(sb2.toString());
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(nm));
			byte[] data = null;
			data = new byte[bis.available()];
			bis.read(data, 0, data.length);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(nm));
			bos.write(data);
			for (String s : packet) {
				bos.write(s.getBytes());
			}
			bos.flush();
			bos.close();
			bis.close();
			packet.clear();
			sb2.delete(0, sb2.capacity());
		} catch (Exception e) {
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(nm));
				bos.close();
			} catch (Exception e2) {
			}
		}

	}

	// 로그인창 진입시 해당 옵코드   처리
	// 패킷 , 사이즈 , 데이터 
	// 해당 c_ 처리할때  나오는 로그 
	
	public void 로그인창로그(int packet, int size, byte[] data) {
		synchronized (_로그인창로그) {
			StringBuffer sb3 = new StringBuffer();
			TimeZone kst = TimeZone.getTimeZone("KST");
			Date day = new Date(System.currentTimeMillis());
			_로그인창로그.append((day.getYear() + 1900) + "-" + (day.getMonth() + 1)
					+ "-" + day.getDate() + " " + day.getHours() + ":"
					+ day.getMinutes() + ":" + day.getSeconds()
					+ "=======================\n");
			// sb2.append(+cal.get (Calendar.YEAR )+"-" +(cal.get(Calendar.MONTH
			// )+1)+"-"+cal.get(Calendar.DATE )+" "+cal.get(Calendar.HOUR_OF_DAY
			// )+":"+cal.get(Calendar.MINUTE )+":"+cal.get (Calendar.SECOND
			// )+"=======================\n" );
			sb3.append("    [C] ");  // 출력 
			switch (packet) {
			// 설명 드리자면  어떤 패킷이냐  그 옵코드  명 이름을 나타 내기위한 로그 입니다. 
			// 수정전은  이전 옵코드 이름으로 로그가 나왓고  지금은  수정했으니. 본섭옵코드 명으로 출력되겟네요
			// 저 옵코드가 본섭에서 사용하는 옵코드 명이라서  계속 저이름으로 사용하는게 옵코드 처리하는데 편하겟죠
			//ㅋㅋ  아하고맙습니다 설명까지 ㅎ 
			case C_EXTENDED:  // 현재 옵코드 
				sb3.append("C_EXTENDED"); // 현재 옵코드명 
				break;
			case C_READ_NOTICE:  // 이건 현재 옵코드 자바에 있는 패킷값 
				sb3.append("C_READ_NOTICE");  // 로그에  이옵코드 이름으로 출력된다 
				break;
			case C_PLEDGE_WATCH:
				sb3.append("C_PLEDGE_WATCH"); 
				break;
			case C_SHUTDOWN:
				sb3.append("C_SHUTDOWN");
				break;
			case C_ATTACK_CONTINUE:
				sb3.append("C_ATTACK_CONTINUE");
				break;
			case C_RETURN_SUMMON:
				sb3.append("C_RETURN_SUMMON");
				break;
			case C_GOTO_PORTAL:
				sb3.append("C_GOTO_PORTAL");
				break;
			case C_EXCLUDE:
				sb3.append("C_EXCLUDE");
				break;
			case C_SAVEIO:
				sb3.append("C_SAVEIO");
				break;
			case C_OPEN:
				sb3.append("C_OPEN");
				break;
			case C_TITLE:
				sb3.append("C_TITLE");
				break;
			case C_BOARD_DELETE:
				sb3.append("C_BOARD_DELETE");
				break;
			case C_WHO_PLEDGE:
				sb3.append("C_WHO_PLEDGE");
				break;
			case C_CHANGE_DIRECTION:
				sb3.append("C_CHANGE_DIRECTION");
				break;
			case C_HACTION:
				sb3.append("C_HACTION");
				break;
			case C_USE_SPELL:
				sb3.append("C_USE_SPELL");
				break;
			case C_UPLOAD_EMBLEM:
				sb3.append("C_UPLOAD_EMBLEM");
				break;
			case C_CANCEL_XCHG:
				sb3.append("C_CANCEL_XCHG");
				break;
			case C_BOOKMARK:
				sb3.append("C_BOOKMARK");
				break;
			case C_CREATE_PLEDGE:
				sb3.append("C_CREATE_PLEDGE");
				break;
			case C_VERSION:
				sb3.append("C_VERSION");
				break;
			case C_MARRIAGE:
				sb3.append("C_MARRIAGE");
				break;
			case C_BUYABLE_SPELL:
				sb3.append("C_BUYABLE_SPELL");
				break;
			case C_BOARD_LIST:
				sb3.append("C_BOARD_LIST");
				break;
			case C_PERSONAL_SHOP:
				sb3.append("C_PERSONAL_SHOP");
				break;
			case C_BOARD_READ:
				sb3.append("C_BOARD_READ");
				break;
			case C_ASK_XCHG:
				sb3.append("C_ASK_XCHG");
				break;
			case C_DELETE_CHARACTER:
				sb3.append("C_DELETE_CHARACTER");
				break;
			case C_ALIVE:
				sb3.append("C_ALIVE");
				break;
			case C_ANSWER:
				sb3.append("C_ANSWER");
				break;
			case C_LOGIN:
				sb3.append("C_LOGIN");
				break;
			case C_BUY_SELL:
				sb3.append("C_BUY_SELL");
				break;
			case C_DEPOSIT:
				sb3.append("C_DEPOSIT");
				break;
			case C_WITHDRAW:
				sb3.append("C_WITHDRAW");
				break;
			case C_ONOFF:
				sb3.append("C_ONOFF");
				break;
			case C_BUY_SPELL:
				sb3.append("C_BUY_SPELL");
				break;
			case C_ADD_XCHG:
				sb3.append("C_ADD_XCHG");
				break;
			case C_ADD_BUDDY:
				sb3.append("C_ADD_BUDDY");
				break;
			case C_LOGOUT:
				sb3.append("C_LOGOUT");
				break;
			case C_SAY:
				sb3.append("C_SAY");
				break;
			case C_ACCEPT_XCHG:
				sb3.append("C_ACCEPT_XCHG");
				break;
			case C_CHECK_PK:
				sb3.append("C_CHECK_PK");
				break;
			case C_TAX:
				sb3.append("C_TAX");
				break;
			case C_RESTART:
				sb3.append("C_RESTART");
			//	sb3.append("C_OPCODE_RESTART_AFTER_DIE");
				break;
			case C_QUERY_BUDDY:
				sb3.append("C_QUERY_BUDDY");
				break;
			case C_DROP:
				sb3.append("C_DROP");
				break;
			case C_LEAVE_PARTY:
				sb3.append("C_LEAVE_PARTY");
				break;
			case C_ATTACK:
				sb3.append("C_ATTACK");
				break;
			case C_FAR_ATTACK:
				sb3.append("C_FAR_ATTACK");
				break;
			case C_QUIT:
				sb3.append("C_QUIT");
				break;
			case C_BAN_MEMBER:
				sb3.append("C_BAN_MEMBER");
				break;
			case C_PLATE:
				sb3.append("C_PLATE");
				break;
			case C_DESTROY_ITEM:
				sb3.append("C_DESTROY_ITEM");
				break;
			case C_TELL:
				sb3.append("C_TELL");
				break;
			case C_WHO_PARTY:
				sb3.append("C_WHO_PARTY");
				break;
			case C_GET:
				sb3.append("C_GET");
				break;
			case C_WHO:
				sb3.append("C_WHO");
				break;
			case C_GIVE:
				sb3.append("C_GIVE");
				break;
			case C_MOVE:
				sb3.append("C_MOVE");
				break;
			case C_DELETE_BOOKMARK:
				sb3.append("C_DELETE_BOOKMARK");
				break;
			//case C_OPCODE_RESTART_AFTER_DIE:
			//	sb3.append("C_OPCODE_RESTART_AFTER_DIE");
			//	break;
			case C_LEAVE_PLEDGE:
				sb3.append("C_LEAVE_PLEDGE");
				break;
			case C_DIALOG:
				sb3.append("C_DIALOG");
				break;
			case C_BANISH_PARTY:
				sb3.append("C_BANISH_PARTY");
				break;
			case C_REMOVE_BUDDY:
				sb3.append("C_REMOVE_BUDDY");
				break;
			case C_WAR:
				sb3.append("C_WAR");
				break;
			case C_ENTER_WORLD:
				sb3.append("C_ENTER_WORLD");
				break;
			case C_QUERY_PERSONAL_SHOP:
				sb3.append("C_QUERY_PERSONAL_SHOP");
				break;
			//case C_OPCODE_CHATGLOBAL:
			//	sb3.append("C_OPCODE_CHATGLOBAL");
			//	break;
			case C_JOIN_PLEDGE:
				sb3.append("C_JOIN_PLEDGE");
				break;
			case C_READ_NEWS:
				sb3.append("C_READ_NEWS");
				break;
			case C_CREATE_CUSTOM_CHARACTER:
				sb3.append("C_CREATE_CUSTOM_CHARACTER");
				break;
			case C_ACTION:
				sb3.append("C_ACTION");
				break;
			case C_BOARD_WRITE:
				sb3.append("C_BOARD_WRITE");
				break;
			case C_USE_ITEM:
				sb3.append("C_USE_ITEM");
				break;
			case C_INVITE_PARTY_TARGET:
				sb3.append("C_INVITE_PARTY_TARGET");
				break;
			case C_ENTER_PORTAL:
				sb3.append("C_ENTER_PORTAL");
				break;
			case C_HYPERTEXT_INPUT_RESULT:
				sb3.append("C_HYPERTEXT_INPUT_RESULT");
				break;
			case C_FIXABLE_ITEM :
				sb3.append("C_FIXABLE_ITEM");
				break;
			case C_FIX:
				sb3.append("C_FIX");
				break;
			case C_SUMMON:
				sb3.append("C_SUMMON");
				break;
			case C_THROW:
				sb3.append("C_THROW");
				break;
			case C_SLAVE_CONTROL:
				sb3.append("C_SLAVE_CONTROL");
				break;
			case C_CHECK_INVENTORY:
				sb3.append("C_CHECK_INVENTORY");
				break;
			case C_NPC_ITEM_CONTROL:
				sb3.append("C_NPC_ITEM_CONTROL");
				break;
			case C_RANK_CONTROL:
				sb3.append("C_RANK_CONTROL");
				break;
			case C_CHAT_PARTY_CONTROL:
				sb3.append("C_CHAT_PARTY_CONTROL");
				break;
			case C_DUEL:
				sb3.append("C_DUEL");
				break;
			case C_GOTO_MAP:
				sb3.append("C_GOTO_MAP");
				break;
			case C_MAIL:
				sb3.append("C_MAIL");
				break;
			case C_VOICE_CHAT:
				sb3.append("C_VOICE_CHAT");
				break;
			case C_WAREHOUSE_CONTROL:
				sb3.append("C_WAREHOUSE_CONTROL");
				break;
			case C_EXCHANGEABLE_SPELL:
				sb3.append("C_EXCHANGEABLE_SPELL");
				break;
			case C_EXCHANGE_SPELL:
				sb3.append("C_EXCHANGE_SPELL");
				break;
			case C_MERCENARYEMPLOY:
				sb3.append("C_MERCENARYEMPLOY");
				break;
		//	case C_______OPCODE_SOLDIERGIVE:
		//		sb3.append("C_OPCODE_SOLDIERGIVE");
		//		break;
		//	case C_OPCODE_SOLDIERGIVEOK:
		//		sb3.append("C_OPCODE_SOLDIERGIVEOK");
		//		break;
			case C_EMBLEM:
				sb3.append("C_EMBLEM");
				break;
			case C_QUERY_CASTLE_SECURITY:
				sb3.append("C_QUERY_CASTLE_SECURITY");
				break;
			case C_CHANGE_CASTLE_SECURITY:
				sb3.append("C_CHANGE_CASTLE_SECURITY");
				break;
			case C_CHANNEL:
				sb3.append("C_CHANNEL");
				break;
			case C_EXTENDED_PROTOBUF:
				sb3.append("C_EXTENDED_PROTOBUF");
				break;
			default:
				sb3.append("null " + packet);
				break;
			}

			_로그인창로그.append(String.format("%s  Size : %d\n", sb3.toString(),
					size));
			_로그인창로그.append(printData(data, size, true));
			_로그인창로그.append("\n");
			
			// %s  Size : %d\n    - 사이즈 : 옵코드명 
			// printData(data, size, true)  - 패킷  
			// 이렇게 출력됩니다. 
			sb3 = null;
		}
	}

	
	// 감자가  로그가 좀많음 ㅋㅋ ㅡ_ㅡㅋㅋㅋㅋㅋ
	public void 행동로그(int packet, int size, byte[] data) {
		if (!패킷로그) {  
			return;
		}
		synchronized (sb2) {
			StringBuffer sb3 = new StringBuffer();
			TimeZone kst = TimeZone.getTimeZone("KST");
			Calendar cal = Calendar.getInstance(kst);

			Date day = new Date(System.currentTimeMillis());
			sb2.append((day.getYear() + 1900) + "-" + (day.getMonth() + 1)
					+ "-" + day.getDate() + " " + day.getHours() + ":"
					+ day.getMinutes() + ":" + day.getSeconds()
					+ "=======================\n");
			// sb2.append(+cal.get (Calendar.YEAR )+"-" +(cal.get(Calendar.MONTH
			// )+1)+"-"+cal.get(Calendar.DATE )+" "+cal.get(Calendar.HOUR_OF_DAY
			// )+":"+cal.get(Calendar.MINUTE )+":"+cal.get (Calendar.SECOND
			// )+"=======================\n" );
			sb3.append("    [C] ");
			switch (packet) {
			// case C_OPCODE_NCOINSHOPLIST:
			// sb3.append("C_OPCODE_ADEN_SHOP");break;
			// case C_OPCODE_b501: sb3.append("C_OPCODE_LOGIN_CHEAK");break;
			case C_EXTENDED:  // 현재 옵코드 
				sb3.append("C_EXTENDED"); // 현재 옵코드명 
				break;
			case C_READ_NOTICE:  // 이건 현재 옵코드 자바에 있는 패킷값 
				sb3.append("C_READ_NOTICE");  // 로그에  이옵코드 이름으로 출력된다 
				break;
			case C_PLEDGE_WATCH:
				sb3.append("C_PLEDGE_WATCH"); 
				break;
			case C_SHUTDOWN:
				sb3.append("C_SHUTDOWN");
				break;
			case C_ATTACK_CONTINUE:
				sb3.append("C_ATTACK_CONTINUE");
				break;
			case C_RETURN_SUMMON:
				sb3.append("C_RETURN_SUMMON");
				break;
			case C_GOTO_PORTAL:
				sb3.append("C_GOTO_PORTAL");
				break;
			case C_EXCLUDE:
				sb3.append("C_EXCLUDE");
				break;
			case C_SAVEIO:
				sb3.append("C_SAVEIO");
				break;
			case C_OPEN:
				sb3.append("C_OPEN");
				break;
			case C_TITLE:
				sb3.append("C_TITLE");
				break;
			case C_BOARD_DELETE:
				sb3.append("C_BOARD_DELETE");
				break;
			case C_WHO_PLEDGE:
				sb3.append("C_WHO_PLEDGE");
				break;
			case C_CHANGE_DIRECTION:
				sb3.append("C_CHANGE_DIRECTION");
				break;
			case C_HACTION:
				sb3.append("C_HACTION");
				break;
			case C_USE_SPELL:
				sb3.append("C_USE_SPELL");
				break;
			case C_UPLOAD_EMBLEM:
				sb3.append("C_UPLOAD_EMBLEM");
				break;
			case C_CANCEL_XCHG:
				sb3.append("C_CANCEL_XCHG");
				break;
			case C_BOOKMARK:
				sb3.append("C_BOOKMARK");
				break;
			case C_CREATE_PLEDGE:
				sb3.append("C_CREATE_PLEDGE");
				break;
			case C_VERSION:
				sb3.append("C_VERSION");
				break;
			case C_MARRIAGE:
				sb3.append("C_MARRIAGE");
				break;
			case C_BUYABLE_SPELL:
				sb3.append("C_BUYABLE_SPELL");
				break;
			case C_BOARD_LIST:
				sb3.append("C_BOARD_LIST");
				break;
			case C_PERSONAL_SHOP:
				sb3.append("C_PERSONAL_SHOP");
				break;
			case C_BOARD_READ:
				sb3.append("C_BOARD_READ");
				break;
			case C_ASK_XCHG:
				sb3.append("C_ASK_XCHG");
				break;
			case C_DELETE_CHARACTER:
				sb3.append("C_DELETE_CHARACTER");
				break;
			case C_ALIVE:
				sb3.append("C_ALIVE");
				break;
			case C_ANSWER:
				sb3.append("C_ANSWER");
				break;
			case C_LOGIN:
				sb3.append("C_LOGIN");
				break;
			case C_BUY_SELL:
				sb3.append("C_BUY_SELL");
				break;
			case C_DEPOSIT:
				sb3.append("C_DEPOSIT");
				break;
			case C_WITHDRAW:
				sb3.append("C_WITHDRAW");
				break;
			case C_ONOFF:
				sb3.append("C_ONOFF");
				break;
			case C_BUY_SPELL:
				sb3.append("C_BUY_SPELL");
				break;
			case C_ADD_XCHG:
				sb3.append("C_ADD_XCHG");
				break;
			case C_ADD_BUDDY:
				sb3.append("C_ADD_BUDDY");
				break;
			case C_LOGOUT:
				sb3.append("C_LOGOUT");
				break;
			case C_SAY:
				sb3.append("C_SAY");
				break;
			case C_ACCEPT_XCHG:
				sb3.append("C_ACCEPT_XCHG");
				break;
			case C_CHECK_PK:
				sb3.append("C_CHECK_PK");
				break;
			case C_TAX:
				sb3.append("C_TAX");
				break;
			case C_RESTART:
				sb3.append("C_RESTART");
			//	sb3.append("C_OPCODE_RESTART_AFTER_DIE");
				break;
			case C_QUERY_BUDDY:
				sb3.append("C_QUERY_BUDDY");
				break;
			case C_DROP:
				sb3.append("C_DROP");
				break;
			case C_LEAVE_PARTY:
				sb3.append("C_LEAVE_PARTY");
				break;
			case C_ATTACK:
				sb3.append("C_ATTACK");
				break;
			case C_FAR_ATTACK:
				sb3.append("C_FAR_ATTACK");
				break;
			case C_QUIT:
				sb3.append("C_QUIT");
				break;
			case C_BAN_MEMBER:
				sb3.append("C_BAN_MEMBER");
				break;
			case C_PLATE:
				sb3.append("C_PLATE");
				break;
			case C_DESTROY_ITEM:
				sb3.append("C_DESTROY_ITEM");
				break;
			case C_TELL:
				sb3.append("C_TELL");
				break;
			case C_WHO_PARTY:
				sb3.append("C_WHO_PARTY");
				break;
			case C_GET:
				sb3.append("C_GET");
				break;
			case C_WHO:
				sb3.append("C_WHO");
				break;
			case C_GIVE:
				sb3.append("C_GIVE");
				break;
			case C_MOVE:
				sb3.append("C_MOVE");
				break;
			case C_DELETE_BOOKMARK:
				sb3.append("C_DELETE_BOOKMARK");
				break;
			//case C_OPCODE_RESTART_AFTER_DIE:
			//	sb3.append("C_OPCODE_RESTART_AFTER_DIE");
			//	break;
			case C_LEAVE_PLEDGE:
				sb3.append("C_LEAVE_PLEDGE");
				break;
			case C_DIALOG:
				sb3.append("C_DIALOG");
				break;
			case C_BANISH_PARTY:
				sb3.append("C_BANISH_PARTY");
				break;
			case C_REMOVE_BUDDY:
				sb3.append("C_REMOVE_BUDDY");
				break;
			case C_WAR:
				sb3.append("C_WAR");
				break;
			case C_ENTER_WORLD:
				sb3.append("C_ENTER_WORLD");
				break;
			case C_QUERY_PERSONAL_SHOP:
				sb3.append("C_QUERY_PERSONAL_SHOP");
				break;
			//case C_OPCODE_CHATGLOBAL:
			//	sb3.append("C_OPCODE_CHATGLOBAL");
			//	break;
			case C_JOIN_PLEDGE:
				sb3.append("C_JOIN_PLEDGE");
				break;
			case C_READ_NEWS:
				sb3.append("C_READ_NEWS");
				break;
			case C_CREATE_CUSTOM_CHARACTER:
				sb3.append("C_CREATE_CUSTOM_CHARACTER");
				break;
			case C_ACTION:
				sb3.append("C_ACTION");
				break;
			case C_BOARD_WRITE:
				sb3.append("C_BOARD_WRITE");
				break;
			case C_USE_ITEM:
				sb3.append("C_USE_ITEM");
				break;
			case C_INVITE_PARTY_TARGET:
				sb3.append("C_INVITE_PARTY_TARGET");
				break;
			case C_ENTER_PORTAL:
				sb3.append("C_ENTER_PORTAL");
				break;
			case C_HYPERTEXT_INPUT_RESULT:
				sb3.append("C_HYPERTEXT_INPUT_RESULT");
				break;
			case C_FIXABLE_ITEM :
				sb3.append("C_FIXABLE_ITEM");
				break;
			case C_FIX:
				sb3.append("C_FIX");
				break;
			case C_SUMMON:
				sb3.append("C_SUMMON");
				break;
			case C_THROW:
				sb3.append("C_THROW");
				break;
			case C_SLAVE_CONTROL:
				sb3.append("C_SLAVE_CONTROL");
				break;
			case C_CHECK_INVENTORY:
				sb3.append("C_CHECK_INVENTORY");
				break;
			case C_NPC_ITEM_CONTROL:
				sb3.append("C_NPC_ITEM_CONTROL");
				break;
			case C_RANK_CONTROL:
				sb3.append("C_RANK_CONTROL");
				break;
			case C_CHAT_PARTY_CONTROL:
				sb3.append("C_CHAT_PARTY_CONTROL");
				break;
			case C_DUEL:
				sb3.append("C_DUEL");
				break;
			case C_GOTO_MAP:
				sb3.append("C_GOTO_MAP");
				break;
			case C_MAIL:
				sb3.append("C_MAIL");
				break;
			case C_VOICE_CHAT:
				sb3.append("C_VOICE_CHAT");
				break;
			case C_WAREHOUSE_CONTROL:
				sb3.append("C_WAREHOUSE_CONTROL");
				break;
			case C_EXCHANGEABLE_SPELL:
				sb3.append("C_EXCHANGEABLE_SPELL");
				break;
			case C_EXCHANGE_SPELL:
				sb3.append("C_EXCHANGE_SPELL");
				break;
			case C_MERCENARYEMPLOY:
				sb3.append("C_MERCENARYEMPLOY");
				break;
		//	case C_______OPCODE_SOLDIERGIVE:
		//		sb3.append("C_OPCODE_SOLDIERGIVE");
		//		break;
		//	case C_OPCODE_SOLDIERGIVEOK:
		//		sb3.append("C_OPCODE_SOLDIERGIVEOK");
		//		break;
			case C_EMBLEM:
				sb3.append("C_EMBLEM");
				break;
			case C_QUERY_CASTLE_SECURITY:
				sb3.append("C_QUERY_CASTLE_SECURITY");
				break;
			case C_CHANGE_CASTLE_SECURITY:
				sb3.append("C_CHANGE_CASTLE_SECURITY");
				break;
			case C_CHANNEL:
				sb3.append("C_CHANNEL");
				break;
			case C_EXTENDED_PROTOBUF:
				sb3.append("C_EXTENDED_PROTOBUF");
				break;
			default:
				sb3.append("null " + packet);
				break;
				
				// 위아래 똑같이 사용함 
			}

			sb2.append(String.format("%s  Size : %d\n", sb3.toString(), size));
			sb2.append(printData(data, size, true));
			sb2.append("\n");
			sb3 = null;
		}
	}

	public void 행동로그(ServerBasePacket sb) {
		if (!패킷로그) {
			return;
		}
		synchronized (sb2) {
			try {
				int size = sb.getLength() - 2;
				byte[] data = sb.getContent();
				StringBuffer sb3 = new StringBuffer();
				TimeZone kst = TimeZone.getTimeZone("KST");
				Calendar cal = Calendar.getInstance(kst);
				Date day = new Date(System.currentTimeMillis());
				sb2.append((day.getYear() + 1900) + "-" + (day.getMonth() + 1)
						+ "-" + day.getDate() + " " + day.getHours() + ":"
						+ day.getMinutes() + ":" + day.getSeconds()
						+ "=======================\n");
				/*
				 * sb2.append(+cal.get(Calendar.YEAR) + "-" +
				 * (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE)
				 * + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" +
				 * cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) +
				 * "=======================\n");
				 */
				sb3.append(sb.getType());
				sb2.append(String.format("%s  Size : %d\n", sb3.toString(),
						size));
				sb2.append(printData(data, size));
				sb2.append("\n");
				sb3 = null;
				data = null;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 현재 클라이언트에 사용할 PC 객체를 설정한다.
	 * 
	 * @param pc
	 */
	
	
	
	public void setActiveChar(L1PcInstance pc) {
		activeCharInstance = pc;
	}

	/**
	 * 현재 클라이언트 사용하고 있는 PC 객체를 반환한다.
	 * 
	 * @return activeCharInstance;
	 */
	public L1PcInstance getActiveChar() {
		return activeCharInstance;
	}

	/**
	 * 현재 사용하는 계정을 설정한다.
	 * 
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * 현재 사용중인 계정은 반환한다.
	 * 
	 * @return account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * 현재 사용중인 계정명을 반환한다.
	 * 
	 * @return account.getName();
	 */
	public String getAccountName() {
		if (account == null) {
			return null;
		}
		String name = account.getName();

		return name;
	}
	
	private String InterServerName;
	
	/** 인터 서버 체크 용  */
	public String getInterServerName() {
		return InterServerName;
	}

	public void setInterServerName(String InterName) {
		InterServerName = InterName;
	}

	private boolean InterServer = false;
	
	/** 인터 서버 체크 용  */
	public boolean getInterServer() {
		return InterServer;
	}

	public void setInterServer(boolean Inter) {
		InterServer = Inter;
	}
	
	private int InterServerType;
	
	/** 인터 서버 체크 용  */
	public int getInterServerType() {
		return InterServerType;
	}

	public void setInterServerType(int Inter) {
		InterServerType = Inter;
	}
	
	private L1Party InterServerParty;
	
	/** 인터 서버 체크 용  */
	public L1Party getInterServerParty() {
		return InterServerParty;
	}

	public void setInterServerParty(L1Party Inter) {
		InterServerParty = Inter;
	}
	
	private int InterServerNotice;
	
	/** 인터 서버 체크 용  */
	public int getInterServerNotice() {
		return InterServerNotice;
	}

	public void setInterServerNotice(int Notice) {
		InterServerNotice = Notice;
	}

	public void storeItem(L1PcInstance pc, L1ItemInstance item)
			throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_items SET id = ?, item_id = ?, char_id = ?, item_name = ?, count = ?, is_equipped = 0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, bless = ?, attr_enchantlvl = ?, step_enchantlvl = ?, end_time = ?, second_id=?, round_id=?, ticket_id=?, regist_level=?, KeyVal=?, CreaterName=?, demon_bongin=?, char_name=?");
			pstm.setInt(1, item.getId());
			pstm.setInt(2, item.getItem().getItemId());
			pstm.setInt(3, pc.getId());
			pstm.setString(4, item.getItem().getName());
			pstm.setInt(5, item.getCount());
			pstm.setInt(6, item.getEnchantLevel());
			pstm.setInt(7, item.isIdentified() ? 1 : 0);
			pstm.setInt(8, item.get_durability());
			pstm.setInt(9, item.getChargeCount());
			pstm.setInt(10, item.getRemainingTime());
			pstm.setTimestamp(11, item.getLastUsed());
			pstm.setInt(12, item.getBless());
			pstm.setInt(13, item.getAttrEnchantLevel());
			pstm.setInt(14, item.getStepEnchantLevel());
			pstm.setTimestamp(15, item.getEndTime());
			pstm.setInt(16, item.getSecondId());
			pstm.setInt(17, item.getRoundId());
			pstm.setInt(18, item.getTicketId());
			pstm.setInt(19, item.getRegistLevel());
			pstm.setInt(20, item.getKey());
			pstm.setString(21, item.getCreaterName());
			pstm.setInt(22, item.isDemonBongin() ? 1 : 0);
			pstm.setString(23, pc.getName());
			pstm.executeUpdate();
		} catch (SQLException e) {
			System.out.println(pc.getName()
					+ "의 인벤에 아이템 저장 오류 아이템명 : "
					+ item.getName()
					+ " 소유자 : "
					+ (item.getItemOwner() == null ? "없음" : item.getItemOwner()
							.getName()));
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		item.getLastStatus().updateAll();
	}

	public void fairlystore(int objectId, byte[] data) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_Fairly_Config SET object_id=?, data=?");
			pstm.setInt(1, objectId);
			pstm.setBytes(2, data);
			pstm.executeUpdate();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void fairlupdate(int objectId, byte[] data) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE character_Fairly_Config SET data=? WHERE object_id=?");
			pstm.setBytes(1, data);
			pstm.setInt(2, objectId);
			pstm.executeUpdate();
		} catch (SQLException e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 해당 LineageClient가 종료할때 호출
	 * 
	 * @param pc
	 */
	public void quitGame(L1PcInstance pc) {
		eva.LogServerAppend("종료", pc,pc.getNetConnection().getIp(), -1);
		Config._quit_Q.requestWork(pc);
	}

	/**
	 * 현재 연결된 호스트명을 반환한다.
	 * 
	 * @return
	 */
	public String getHostname() {
		return _hostname;
	}

	/**
	 * 현재 로그인 실패한 카운트 수를 반환한다.
	 * 
	 * @return
	 */
	public int getLoginFailedCount() {
		return _loginfaieldcount;
	}

	/**
	 * 현재 로그인 실패한 카운트 수를 설정한다.
	 * 
	 * @param i
	 */
	public void setLoginFailedCount(int i) {
		_loginfaieldcount = i;
	}

	public byte[] DecSabuData(byte[] data, int type) {
		try {
			data = le.DecSabuData(data, data.length, type);
			return data;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * LineageClient의 접속 여부를 반환한다.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (Config.새로운패킷구조) {
			return _connection.isOpen();
		} else {
			return _session.isConnected();
		}
	}

	/**
	 * 현재 접속중인 LineageClient에 IP를 반환한다.
	 * 
	 * @return
	 */
	public String getIp() {
		return _ip;
	}

	public boolean 패킷로그 = false;// 로그

	/**
	 * 현재 새션 종료상태를 반환한다.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		if (Config.새로운패킷구조) {
			if (!_connection.isOpen())
				return true;
			else {
				return false;
			}
		} else {
			if (_session.isClosing()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String printData(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
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

	private String fillHex(int data, int digits) {
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}
		return number;
	}

	private boolean gaming = false;

	private static String HexToDex(int data, int digits) {
		String number = Integer.toHexString(data);
		for (int i = number.length(); i < digits; i++)
			number = "0" + number;
		return number;
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

	public void 얼던팅기록(L1PcInstance pc) {
		Connection c = null;
		PreparedStatement p = null;
		try {
			String classname = "";
			if (pc.isCrown())
				classname = "군주";
			else if (pc.isWizard())
				classname = "마법사";
			else if (pc.isKnight())
				classname = "기사";
			else if (pc.isIllusionist())
				classname = "환술사";
			else if (pc.isElf())
				classname = "요정";
			else if (pc.isDarkelf())
				classname = "다크엘프";
			else if (pc.isDragonknight())
				classname = "용기사";
			else if (pc.isWarrior())
				classname = "전사";
			else if (pc.isFencer())
				classname = "검사";
			c = L1DatabaseFactory.getInstance().getConnection();
			p = c.prepareStatement("INSERT INTO icequeen_playing_quit SET time=SYSDATE(), name=?, x=?, y=?, mapid=?, class_name=?, polyid=?");
			p.setString(1, pc.getName());
			p.setInt(2, pc.getX());
			p.setInt(3, pc.getY());
			p.setInt(4, pc.getMapId());
			p.setString(5, classname);
			p.setInt(6, pc.getGfxId().getTempCharGfx());
			p.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(p);
			SQLUtil.close(c);
		}
	}

	public String printData(byte[] data, int len, boolean ck) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				if (ck)
					result.append("     " + fillHex(i, 4) + ": ");
				else
					result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
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

}
