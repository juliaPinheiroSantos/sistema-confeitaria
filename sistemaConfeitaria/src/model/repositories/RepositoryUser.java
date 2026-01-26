package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;

import model.entities.User;

public class RepositoryUser {
	
	private static final String insertSQL = "INSERT INTO users(login,passowrd_hash) VALUES (?,?)";
	
	public User createUser(User user) {
		
		try(Connection conn = DBConnection.getConnection()) {
		
			
			pstmt.setString(1,user.getLogin());
			pstmt.setString(2, user.getPasswordHash());
			
			pstmt.executeUpdate();

		}
	}

}
