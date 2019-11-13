package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.Robot.L1RobotInstance;
import l1j.server.server.Account;
import l1j.server.server.AccountAlreadyLoginException;
import l1j.server.server.GameServerFullException;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.clientpackets.C_AuthLogin;
import l1j.server.server.clientpackets.C_NoticeClick;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharPass;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_LoginResult;
import l1j.server.server.serverpackets.S_Notice;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class Authorization {
	private static Authorization uniqueInstance = null;
	private static Logger _log = Logger.getLogger(C_AuthLogin.class.getName());

	public static Authorization getInstance() {
		if (uniqueInstance == null) {
			synchronized (Authorization.class) {
				if (uniqueInstance == null)
					uniqueInstance = new Authorization();
			}
		}

		return uniqueInstance;
	}

	private Authorization() {
	}

	private static final S_Notice sn = new S_Notice(
			"서버에 접속한 동일한 IP의 중복 로그인을 거부했습니다.");
	LineageClient _cl;

	class 종료스레드 implements Runnable {
		public 종료스레드(LineageClient CL) {
			_cl = CL;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(100);
				disconnect(_cl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void auth(LineageClient client, String accountName, String password,
			String ip, String host) throws IOException {

		/** 계정이 영문, 숫자로 되어있는지 체크 **/
		int length = accountName.length();
		char chr;
		for (int i = 0; i < length; i++) {
			chr = accountName.charAt(i);
			if (Character.UnicodeBlock.of(chr) != Character.UnicodeBlock.BASIC_LATIN) {
				if (client.getActiveChar() == null) {
					client.sendPacket(new S_LoginResult(client.getAccount(), 26), true);
				}
				return;
			}/*
			 * else{ System.out.print(chr); }
			 */
		}

		int accountLength = accountName.getBytes().length;
		int passwordLength = password.getBytes().length;
		if (accountLength > 12 || accountLength < 4) {        
		    _log.info("계정명 오류 [오류 계정 " + accountName + "] ");
		
		    client.sendPacket(new S_LoginResult(client.getAccount(), S_LoginResult.REASON_WRONG_ACCOUNT));

		    return;
		}
		// 패스워드 길이 (eva 는 매니저 클래스임)
		else if(passwordLength > 12 || passwordLength < 4)
		{
		    _log.info("패스워드 길지 오류 [패스워드 길이 " + passwordLength + "] ");

		    client.sendPacket(new S_LoginResult(client.getAccount(), S_LoginResult.REASON_WRONG_PASSWORD));
		    return;
		}

		Account account = Account.load(accountName);
		if (account == null) {
			if (Config.AUTO_CREATE_ACCOUNTS) {
				/*
				 * if(Account.checkLoginIP(ip)) { S_Notice sn2 = new
				 * S_Notice("IP당 2개의 계정만 허용합니다."); client.sendPacket(sn2);
				 * sn2.clear(); sn2 = null; try { Thread.sleep(1500);
				 * client.kick(); client.close(); } catch (Exception e1) {}
				 * }else{
				 */
				account = Account.create(accountName, password, ip, host);
				account = Account.load(accountName);
				// }
			} else {
				_log.warning("account missing for user " + accountName);
			}
		}

		if (account == null || !account.validatePassword(accountName, password)) {
			int lfc = client.getLoginFailedCount();
			client.setLoginFailedCount(lfc + 1);
			if (lfc > 2)
				disconnect(client);
			else {
				client.sendPacket(new S_LoginResult(client.getAccount(), 26),
						true);
				// client.sendPacket(new
				// S_Notice("서버에서 벤당하신 계정입니다. 운영자에게 문의 하시기 바랍니다"), true);
			}
			return;
		}
		// =========== IP Check[#C_AuthLogin] ===========
		// if(Config.AUTH_CONNECT) {
		// LoginAuth authIP = new LoginAuth();
		// if (authIP.ConnectCheck(ip)) {
		// client.sendPacket(new S_LoginResult(0x01)); // 0x01
		// return;
		// }
		// }
		// =========== IP Check[#C_AuthLogin] ===========
		if (account.isBanned()) {
			_log.info("BAN 계정의 로그인을 거부했습니다. account=" + accountName + " host="
					+ host);
			client.sendPacket(
					new S_Notice("서버에서 벤당하신 계정입니다. 운영자에게 문의 하시기 바랍니다"), true);
			// disconnect(client);
			GeneralThreadPool.getInstance().execute(new 종료스레드(client));
			return;
		}

		try {
			LoginController.getInstance().login(client, account);
			Account.updateLastActive(account, ip); // 최종 로그인일을 갱신한다
			client.setAccount(account);

			client.sendPacket(new S_LoginResult());
			// accountTimeCheck(client);
			client.sendPacket(new S_CharPass(), true);
			// 123
			// 로긴 리절트
			// client.sendPacket(new
			// S_LoginResult(S_LoginResult.REASON_LOGIN_OK), true);
			// client.sendPacket(new S_LoginResult(0), true);
			// client.sendPacket(new S_LoginResult(), true);
			//
			/*
			 * client.sendPacket(new S_LoginResult()); client.sendPacket(new
			 * S_CharPass(), true);
			 */
			// sendNotice(client);
		} catch (GameServerFullException e) {
			client.sendPacket(new S_Notice(
					"서버 접속인원이 많아 접속이 지연되고있습니다.\n \n 잠시후에 다시 접속을 시도해주시기바랍니다."),
					true);
			disconnect(client);
			_log.info("접속 인원수를 초과하였습니다. (" + client.getHostname()
					+ ")의 접속 시도를 강제 종료했습니다.");
			return;
		} catch (AccountAlreadyLoginException e) { // by 낭만고양이
			client.sendPacket(new S_LoginResult(client.getAccount(),
					S_LoginResult.REASON_ACCOUNT_IN_USE), true);
			disconnect(client);
			String name = Alreadychr(accountName);
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			if (target instanceof L1RobotInstance) {
				return;
			}
			if (target == null)
				return;

			target.sendPackets(new S_SystemMessage("다른 사람이 같은 계정으로 로그인했습니다."),
					true);
			target.sendPackets(new S_Disconnect(), true);
			if (target.getNetConnection() != null) {
				target.getNetConnection().kick();
				target.getNetConnection().close();
			}
			return;
		}
	}

	private void accountTimeCheck(LineageClient client) {
		int time = client.getAccount().getAccountTime() + 1;
		int count = 0;

		if (time == 1)
			time = 0;
		if (client.getAccount().getAccountTimeReady() > 0)
			count = 1;
		time = 0;

		client.sendPacket(new S_PacketBox(S_PacketBox.ACCOUNT_TIME, count,
				time * 60), true);
	}

	private static String _chr;

	public static String Alreadychr(String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT * FROM characters WHERE account_name='"
					+ name + "' and onlineStatus = 1";
			pstm = con.prepareStatement(sqlstr);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				return null;
			}
			_chr = rs.getString("char_name");

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return _chr;

	}

	public void sendNotice(LineageClient client) {
		String accountName = client.getAccountName();

		// 읽어야할 공지가 있는지 체크
		if (S_Notice.NoticeCount(accountName) > 0) {
			client.sendPacket(new S_Notice(accountName, client), true);
		} else {
			client.setloginStatus(1);
			C_NoticeClick nc = new C_NoticeClick(client);
			nc = null;
			// client.sendPacket(new S_Notice("123"));
		}
	}

	private void disconnect(LineageClient client) throws IOException {
		client.kick();
		client.close();
	}

	@SuppressWarnings("unused")
	private Account loadAccountInfoFromDB(String accountName) {
		/*
		 * 자동 계정 생성을 IP 기준으로 막고 있음, 자동 생성은 막음 if (account == null) { if
		 * (Config.AUTO_CREATE_ACCOUNTS) {
		 * 
		 * if(Account.checkLoginIP(ip)) { client.sendPacket(new
		 * S_CommonNews("동일 IP로 생성한 계정이 3개 있습니다.")); try { Thread.sleep(1500);
		 * client.kick(); client.close(); } catch (Exception e1) {} return;
		 * }else{ account = Account.create(accountName, password, ip, host); //
		 * account = Account.load(accountName); } } else {
		 * _log.warning("account missing for user " + accountName); } }
		 */

		return Account.load(accountName);
	}

	private boolean checkDuplicatedIPConnection(String ip) {
		if (!Config.ALLOW_2PC) {
			return LoginController.getInstance().checkDuplicatedIP(ip);
		}
		return false;
	}

}
