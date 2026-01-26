package model.entities;

import java.util.Arrays;

import services.EncryptionService;

public class User {

	private String login;
	private String passwordHash;
	
	public User(String login, char[] password) throws Exception{
		this.setLogin(login);
		this.passwordHash = EncryptionService.hashPassword(password);
		Arrays.fill(password,'0');
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
}
