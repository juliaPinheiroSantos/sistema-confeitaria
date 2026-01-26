package model.repositories;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	private static final String ADDRESS_iP = "localhost";
	private static final String ADDRESS_PORT = "5432";
	private static final String NAME_DATABASE = "confeitaria";
	private static final String USER = "postgres";
	private static final String PASSWORD = "134340";
	
	public static Connection getConnection() throws SQLException {
		
		return DriverManager.getConnection("jdbc:postgresql://"+ ADDRESS_iP + ":" 
		+ ADDRESS_PORT + "/" + 
		NAME_DATABASE,
		USER,
		PASSWORD);
	}

}