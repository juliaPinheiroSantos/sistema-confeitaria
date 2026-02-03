package controller;

import app.FlowHandler;
import view.ViewCadastro;
import view.ViewDashboard;
import view.ViewHome;
import view.ViewLogin;

import javax.swing.*;

/**
 * Coordinates navigation: main screen (ViewHome), register, login and dashboard.
 * ViewHome, ControllerCadastro and ControllerLogin injected via constructor.
 */
public class ControllerHome {

    private final ViewHome viewHome;
    private final ControllerCadastro controllerCadastro;
    private final ControllerLogin controllerLogin;

    public ControllerHome(ViewHome viewHome, ControllerCadastro controllerCadastro, ControllerLogin controllerLogin) {
        this.viewHome = viewHome;
        this.controllerCadastro = controllerCadastro;
        this.controllerLogin = controllerLogin;
    }

    /**
     * Hides main screen and opens register screen. When register closes, main screen shows again.
     */
    public void openRegister() {
        FlowHandler.log("HOME_HIDDEN");
        viewHome.setVisible(false);
        FlowHandler.log("REGISTER_OPENED");
        ViewCadastro viewCadastro = new ViewCadastro(controllerCadastro, this::backToHome);
        viewCadastro.setLocationRelativeTo(viewHome);
        viewCadastro.setVisible(true);
    }

    /**
     * Hides main screen and opens login screen. On Voltar, main shows again. On success, opens dashboard.
     */
    public void openLogin() {
        FlowHandler.log("HOME_HIDDEN");
        viewHome.setVisible(false);
        FlowHandler.log("LOGIN_OPENED");
        ViewLogin viewLogin = new ViewLogin(controllerLogin, this::backToHome, this::onLoginSuccess);
        viewLogin.setLocationRelativeTo(viewHome);
        viewLogin.setVisible(true);
    }

    /**
     * Called when register or login screen is closed (Back/Cancel): shows main screen again.
     */
    public void backToHome() {
        FlowHandler.log("BACK_TO_HOME");
        SwingUtilities.invokeLater(() -> viewHome.setVisible(true));
    }

    /**
     * Called when login succeeds: disposes login screen and opens dashboard.
     */
    public void onLoginSuccess(ViewLogin viewLogin) {
        FlowHandler.log("DASHBOARD_OPENED");
        viewLogin.dispose();
        ViewDashboard dashboard = new ViewDashboard();
        dashboard.setLocationRelativeTo(viewLogin);
        dashboard.setVisible(true);
    }
}
