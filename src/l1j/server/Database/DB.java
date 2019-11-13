package l1j.server.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import l1j.server.Config;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public class DB {

	private static DataSource dataSource;
	private static GenericObjectPool connectionPool;
	private static String databaseName;
	private static int databaseMajorVersion;
	private static int databaseMinorVersion;

	public synchronized static void init() {

		connectionPool = new GenericObjectPool();

		connectionPool.setMinIdle(10);
		connectionPool.setMaxIdle(300);
		connectionPool.setMaxActive(300);

		connectionPool.setTestWhileIdle(true);
		connectionPool.setTimeBetweenEvictionRunsMillis(600000); // 1/1000초,
																	// 600000은
																	// 10분이 된다

		try {
			dataSource = setupDataSource();
			Connection c = getConnection();
			DatabaseMetaData dmd = c.getMetaData();
			databaseName = dmd.getDatabaseProductName();
			databaseMajorVersion = dmd.getDatabaseMajorVersion();
			databaseMinorVersion = dmd.getDatabaseMinorVersion();
			c.close();
		} catch (Exception e) {
			System.out.println("연결 에러: " + Config.DB_URL);
			e.printStackTrace();
			throw new Error("DatabaseFactory를 초기화 할 수 없습니다!");
		}

		// System.out.println("데이터베이스에 성공적으로 연결됐습니다.");
	}

	private static DataSource setupDataSource() throws Exception {
		// Create Connection Factory
		ConnectionFactory conFactory = new DriverManagerConnectionFactory(
				Config.DB_URL, Config.DB_LOGIN, Config.DB_PASSWORD);

		KeyedObjectPoolFactory kopf = new GenericKeyedObjectPoolFactory(null,
				300);
		// KeyedObjectPoolFactory kopf = new GenericKeyedObjectPoolFactory(null,
		// 100); 13_07_07 제거

		// Makes Connection Factory Pool-able (Wrapper for two objects)
		// We are using our own implementation of PoolableConnectionFactory that
		// use 1.6 Connection.isValid(timeout) for
		// validation check instead dbcp manual query.
		// new PoolableConnectionFactoryAK(conFactory, connectionPool, null, 1,
		// false, true);
		new PoolableConnectionFactoryAK(conFactory, connectionPool, kopf, 1,
				false, true);// 13_07_07 제거

		// Create data source to utilize Factory and Pool
		return new PoolingDataSource(connectionPool);
	}

	public static int active_count_check = 1;

	public static Connection getConnection() throws SQLException {
		/*
		 * int count = connectionPool.getNumActive(); if(active_count_check <
		 * count+1){ active_count_check = count+1;
		 * System.out.println(":: DBCP Active Connection Count >> "
		 * +active_count_check); }
		 */
		return dataSource.getConnection();
	}

	public static int getActiveConnections() {
		return connectionPool.getNumActive();
	}

	public static int getIdleConnections() {
		return connectionPool.getNumIdle();
	}

	public static synchronized void shutdown() {
		try {
			connectionPool.close();
		} catch (Exception e) {
			System.out.println("DatabaseFactory 종료 실패");
			e.printStackTrace();
		}
		// set datasource to null so we can call init() once more...
		dataSource = null;
	}

	public static void close(Connection con) {
		if (con == null)
			return;
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("DatabaseFactory: 데이터베이스 연결 닫기 실패!");
			e.printStackTrace();
		}
	}

	public static String getDatabaseName() {
		return databaseName;
	}

	public static int getDatabaseMajorVersion() {
		return databaseMajorVersion;
	}

	public static int getDatabaseMinorVersion() {
		return databaseMinorVersion;
	}
}
