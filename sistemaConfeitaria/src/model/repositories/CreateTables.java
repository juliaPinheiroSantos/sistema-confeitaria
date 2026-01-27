package model.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {
	
	public static void createTableUser() {
		

		
		String createTableUser = "CREATE TABLE IF NOT EXISTS"
				+ " users (id SERIAL PRIMARY KEY,"
				+ "login VARCHAR(50) NOT NULL UNIQUE,"
				+ "password_hash TEXT NOT NULL"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableUser);
				
				if(stmt != null) stmt.close();
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create user succesfull");
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
