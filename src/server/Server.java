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
/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/Server.java,v 1.5 2004/11/19 08:54:43 l2chef Exp $
 *
 * $Author: l2chef $
 * $Date: 2004/11/19 08:54:43 $
 * $Revision: 1.5 $
 * $Log: Server.java,v $
 * Revision 1.5  2004/11/19 08:54:43  l2chef
 * database is now used
 *
 * Revision 1.4  2004/07/08 22:42:28  l2chef
 * logfolder is created automatically
 *
 * Revision 1.3  2004/06/30 21:51:33  l2chef
 * using jdk logger instead of println
 *
 * Revision 1.2  2004/06/27 08:12:59  jeichhorn
 * Added copyright notice
 */
package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.LogManager;

import l1j.server.server.utils.PerformanceTimer;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import server.WebProtocol.WebCodecFactory;
import server.WebProtocol.WebProtocolHandler;
import server.mina.LineageCodecFactory;
//import server.monitor.MonitorManager;
import l1j.server.server.utils.SQLUtil;
import l1j.server.L1DatabaseFactory;
//import l1j.server.telnet.TelnetServer;
import l1j.server.server.utils.SystemUtil;
import l1j.server.Database.DB;
import l1j.server.Config;

public class Server {
	private volatile static Server uniqueInstance;
	private static final String LOG_PROP = "./config/log.properties";// 로그 설정 파일

	private Server() {
	}

	public static Server createServer() {
		if (uniqueInstance == null) {
			synchronized (Server.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new Server();
				}
			}
		}
		return uniqueInstance;
	}

	public void start() {
		initLogManager();
		initDBFactory();
		try {
			// World.init();

			PerformanceTimer timer = new PerformanceTimer();
			System.out
					.println("========모든 DB파일 압축 실행 가동========");
		//	System.out.print("[Delete DB] Delete DB Data - 1...");
			clearDB();
		//	System.out.println("OK! " + timer.get() + " ms");
			timer.reset();
		//	System.out
		//			.println("=====================================================");
			timer = null;
		} catch (SQLException e) {
		}
		startGameServer();
		if (!Config.새로운패킷구조) {
			startLoginServer();
		}
		startWebServer();
		// startLoginServer2();

	}

	private void addLogger(DefaultIoFilterChainBuilder chain) throws Exception {
		chain.addLast("logger", new LoggingFilter());

	}

	// private void startLoginServer2(){
	// NettyServer.getInstance();
	// }

	public void shutdown() {
		// loginServer.shutdown();
		GameServer.getInstance().shutdown();
		// System.exit(0);
	}

	private void initLogManager() {
		File logFolder = new File("log");
		logFolder.mkdir();

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			// _log.log(Level.SEVERE, "Failed to Load " + LOG_PROP + " File.",
			// e);
			System.exit(0);
		}
		try {
			Config.load();
		} catch (Exception e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.exit(0);
		}
	}

	private void initDBFactory() {// L1DatabaseFactory 초기설정
		/*
		 * L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER,
		 * Config.DB_URL, Config.DB_LOGIN, Config.DB_PASSWORD); try {
		 * L1DatabaseFactory.getInstance(); } catch(Exception e) {
		 * e.printStackTrace(); }
		 */
		// FIXME StrackTrace하면 error

		DB.init();
	}

	private void startGameServer() {
		try {
			GameServer.getInstance().initialize();
		} catch (Exception e) { 
//			e.printStackTrace();
		}
		;
		// FIXME StrackTrace하면 error
	}

	private static NioSocketAcceptor acceptor;

	private void startLoginServer() {
		try {
			// loginServer.initialize();
			// TCP/IP 서버 클레스 활성화
			LoginController.getInstance().setMaxAllowedOnlinePlayers(
					Config.MAX_ONLINE_USERS);
			acceptor = new NioSocketAcceptor();

			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

			// 암호화 클레스 등록
			chain.addLast("codec", new ProtocolCodecFilter(
					new LineageCodecFactory()));

			// 로거로 기본 세팅
			if (Config.LOGGER) {
				addLogger(chain);
			}

			acceptor.setReuseAddress(true);
			// acceptor.getSessionConfig().setTcpNoDelay(true);
			acceptor.getSessionConfig().setReceiveBufferSize(2048);// 1024*32
			// Bind
			acceptor.setHandler(new LineageProtocolHandler());
			acceptor.setCloseOnDeactivation(false);
			acceptor.bind(new InetSocketAddress(Config.GAME_SERVER_PORT));

			System.out.println(":: Server가 " + Config.GAME_SERVER_PORT
					+ "번 포트를 이용해서 가동 되었습니다.  : Memory : "
					+ SystemUtil.getUsedMemoryMB() + " MB");
			System.out.println("서버 셋팅을 최적화 상태로 진행합니다");
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace하면 error
	}

	private static NioSocketAcceptor web_acceptor;

	private void startWebServer() {
		try {
			// loginServer.initialize();
			// TCP/IP 서버 클레스 활성화
			LoginController.getInstance().setMaxAllowedOnlinePlayers(
					Config.MAX_ONLINE_USERS);
			web_acceptor = new NioSocketAcceptor();

			DefaultIoFilterChainBuilder chain = web_acceptor.getFilterChain();

			// 암호화 클레스 등록
			chain.addLast("codec", new ProtocolCodecFilter(
					new WebCodecFactory()));

			// 로거로 기본 세팅
			if (Config.LOGGER) {
				addLogger(chain);
			}

			web_acceptor.setReuseAddress(true);
			// acceptor.getSessionConfig().setTcpNoDelay(true);
			web_acceptor.getSessionConfig().setReceiveBufferSize(2048);// 1024*32
			// Bind
			web_acceptor.setHandler(new WebProtocolHandler());
			web_acceptor.setCloseOnDeactivation(false);
			web_acceptor.bind(new InetSocketAddress(4000));

	
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace하면 error
	}

	/*
	 * private void startTelnetServer() { if (Config.TELNET_SERVER) {
	 * TelnetServer.getInstance().start(); } }
	 */

	public boolean ChangeWebPort(int port) {
		try {
			web_acceptor.unbind();
			web_acceptor.bind(new InetSocketAddress(port));
			System.out.println(":: Web 서버가 " + port
					+ "번 포트를 이용해서 재가동 되었습니다.  : Memory : "
					+ SystemUtil.getUsedMemoryMB() + " MB");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean ChangePort(int port) {
		try {
			acceptor.unbind();
			acceptor.bind(new InetSocketAddress(port));
			System.out.println(":: Game 서버가 " + port
					+ "번 포트를 이용해서 재가동 되었습니다.  : Memory : "
					+ SystemUtil.getUsedMemoryMB() + " MB");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void clearDB() throws SQLException {
		Connection c = null;
		PreparedStatement p = null;
		try {
			c = L1DatabaseFactory.getInstance().getConnection();
			p = c.prepareStatement("call deleteData(?)");
			p.setInt(1, Config.DELETE_DB_DAYS);
			p.executeUpdate();
		} catch (Exception e) {
		} finally {
			SQLUtil.close(p);
			SQLUtil.close(c);
		}
	}

	ArrayList<Integer> del = new ArrayList<Integer>();
	ArrayList<Integer> temp = new ArrayList<Integer>();
	ArrayList<Integer> org = new ArrayList<Integer>();

	public void clearDB2() throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select objid FROM characters");
			rs = pstm.executeQuery();
			while (rs.next()) {
				del.add(rs.getInt(1));
			}
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void clearDB3() throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("select char_id FROM character_items");
			rs = pstm.executeQuery();
			while (rs.next()) {
				temp.add(rs.getInt(1));
			}
			// pstm =
			// con.prepareStatement("delete FROM character_items WHERE char_id!=? ");
			// pstm.setInt(1, del.get(i));
			pstm.executeUpdate();
			// for(int i =0; i<del.size(); i++){
			// }
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void clearDB4() throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			for (int i = 0; i < org.size(); i++) {
				pstm = con
						.prepareStatement("delete FROM character_items WHERE char_id=? ");
				pstm.setInt(1, org.get(i));
				rs = pstm.executeQuery();
				pstm.executeUpdate();
			}
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void gogo() {
		for (int j = 0; j < del.size(); j++) {
			if (!temp.contains(del.get(j))) {
				org.add(del.get(j));
			}
		}

	}

}
