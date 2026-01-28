package model.entities;

import java.util.Arrays;

import services.EncryptionService;

public class User extends Person {

	private String login;
	private String passwordHash;
	
	public User() {
		super();
	}
	
	public User(String firstName, String lastName, String email, String login, char[] password) throws Exception{
		super(firstName, lastName, email);
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
	
	
	@Override
	public String toString() {
		return "User ["+ super.toString() + "getPasswordHash()=" + getPasswordHash() + ", getLogin()=" + getLogin() + "]";
	}
}
