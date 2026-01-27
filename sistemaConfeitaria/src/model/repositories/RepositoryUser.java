package model.repositories;


import model.entities.User;
import java.sql.Connection;
import java.sql.SQLException; 
import java.sql.PreparedStatement; 

import model.entities.User;

public class RepositoryUser {

	private static final String insertSQL = "INSERT INTO users(login,password_hash) VALUES (?,?) "
			+ "ON CONFLICT (login) DO NOTHING";

	
	public boolean createUser(User user) {
		try (Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(insertSQL)) { 
			
			stmt.setString(1, user.getLogin());
			stmt.setString(2, user.getPasswordHash());
			
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
