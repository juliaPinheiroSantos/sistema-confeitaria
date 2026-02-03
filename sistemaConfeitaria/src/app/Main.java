package app;

import controller.ControllerCadastro;
import controller.ControllerLogin;
import controller.ControllerHome;
import model.entities.Area;
import model.repositories.CreateTables;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryUser;
import view.ViewHome;

import javax.swing.*;
import java.awt.*;

/**
 * Bootstrap: creates tables, injects dependencies into controllers, and shows the main screen.
 * In headless mode (e.g. Docker without display), only flow events are logged then exit.
 */
public class Main {

    public static void main(String[] args) {
        FlowHandler.log("APP_START");
        try {
            CreateTables.createAllTables();
            FlowHandler.log("TABLES_CREATED");
            seedDefaultAreas();
            FlowHandler.log("DEFAULT_AREAS_SEEDED");
            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            FlowHandler.log("APP_START_ERROR", e.getMessage());
            return;
        }

        if (GraphicsEnvironment.isHeadless()) {
            FlowHandler.log("HEADLESS_MODE", "No display - flow log only, exiting.");
            FlowHandler.log("HOME_SHOWN", "simulated");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            RepositoryPerson repoPerson = new RepositoryPerson();
            RepositoryUser repoUser = new RepositoryUser();
            RepositoryAddress repoAddress = new RepositoryAddress();
            RepositoryArea repoArea = new RepositoryArea();

            ControllerCadastro controllerCadastro = new ControllerCadastro(
                    repoPerson, repoUser, repoAddress, repoArea);
            ControllerLogin controllerLogin = new ControllerLogin(repoUser);

            ViewHome viewHome = new ViewHome();
            ControllerHome controllerHome = new ControllerHome(
                    viewHome, controllerCadastro, controllerLogin);
            viewHome.setController(controllerHome);

            viewHome.setVisible(true);
            viewHome.toFront();
            viewHome.requestFocus();
            FlowHandler.log("HOME_SHOWN");
        });
    }

    /** Cria as áreas de exemplo no banco via RepositoryArea (usado pelo cadastro). */
    private static void seedDefaultAreas() throws java.sql.SQLException {
        RepositoryArea repoArea = new RepositoryArea();
        for (Area area : Area.getDefaultAreas()) {
            repoArea.createArea(area);
        }
    }
}
