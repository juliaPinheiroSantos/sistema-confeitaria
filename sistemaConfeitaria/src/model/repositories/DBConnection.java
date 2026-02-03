package model.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static String get(String envKey, String propKey, String defaultValue) {
		String v = System.getenv(envKey);
		if (v != null && !v.isEmpty()) return v;
		v = System.getProperty(propKey);
		if (v != null && !v.isEmpty()) return v;
		return defaultValue;
	}

	/**
	 * Conexão com PostgreSQL. Usa variáveis de ambiente ou system properties
	 * (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD) para permitir Docker ou Postgres local.
	 */
	public static Connection getConnection() throws SQLException {
		String host = get("DB_HOST", "DB_HOST", "127.0.0.1");
		String port = get("DB_PORT", "DB_PORT", "5435");
		String database = get("DB_NAME", "DB_NAME", "confeitaria");
		String user = get("DB_USER", "DB_USER", "postgres");
		String password = get("DB_PASSWORD", "DB_PASSWORD", "123");
		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
		
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw e;
		}
	}
}