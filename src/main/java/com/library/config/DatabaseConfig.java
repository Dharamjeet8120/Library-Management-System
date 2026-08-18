//package com.library.config;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//
//public class DatabaseConfig {
//	private static final HikariDataSource dataSource;
//
//    static {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true");
//        config.setUsername("root");
//        config.setPassword("password");
//        
//        // Connection Pool Settings
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(2);
//        config.setIdleTimeout(30000);
//        config.setConnectionTimeout(10000);
//
//        dataSource = new HikariDataSource(config);
//    }
//    public static Connection getConnection() throws SQLException {
//    		try {
//				Class.forName("com.mysql.cj.jdbc.Driver");
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//}

package com.library.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

	private static final HikariDataSource dataSource;

	static {
		try {
			HikariConfig config = new HikariConfig();

			config.setJdbcUrl(
					"jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
			config.setUsername("root");
			config.setPassword("812046");
			config.setDriverClassName("com.mysql.cj.jdbc.Driver");

			config.setMaximumPoolSize(10);
			config.setMinimumIdle(2);
			config.setIdleTimeout(30000);
			config.setConnectionTimeout(10000);
			config.setMaxLifetime(1800000);

			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.addDataSourceProperty("useServerPrepStmts", "true");

			config.setPoolName("LibrarySystemHikariPool");

			dataSource = new HikariDataSource(config);

		} catch (Exception e) {
			System.err.println("Error initializing HikariCP DataSource: " + e.getMessage());
			throw new ExceptionInInitializerError(e);
		}
	}

	private DatabaseConfig() {
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static void closePool() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
			System.out.println("HikariCP pool closed successfully.");
		}
	}
}