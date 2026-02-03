package controller;

import app.FlowHandler;
import model.entities.User;
import model.repositories.RepositoryUser;
import services.EncryptionService;

import java.sql.SQLException;

/**
 * Login logic: find user by email and validate password.
 * RepositoryUser injected via constructor.
 */
public class ControllerLogin {

    private final RepositoryUser repoUser;

    public ControllerLogin(RepositoryUser repoUser) {
        this.repoUser = repoUser;
    }

    /**
     * Attempts login with email and password.
     *
     * @return the logged-in User on success; null on failure (invalid credentials or error)
     */
    public User login(String email, char[] password) {
        FlowHandler.log("LOGIN_ATTEMPT");
        if (isEmptyCredentials(email, password)) return null;
        try {
            User user = repoUser.findByEmailUser(email.trim());
            boolean ok = user != null && EncryptionService.checkPassword(password, user.getPasswordHash());
            if (ok) FlowHandler.log("LOGIN_OK");
            else FlowHandler.log("LOGIN_FAILED");
            return ok ? user : null;
        } catch (SQLException e) {
            e.printStackTrace();
            FlowHandler.log("LOGIN_FAILED", "db");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            FlowHandler.log("LOGIN_FAILED", e.getMessage());
            return null;
        }
    }

    private static boolean isEmptyCredentials(String email, char[] password) {
        return email == null || email.isBlank()
                || password == null || password.length == 0;
    }
}
