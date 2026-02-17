package app;

import controller.ControllerCadastro;
import controller.ControllerHome;
import controller.ControllerLogin;
import controller.ControllerProduct;
import model.entities.Area;
import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.FlavorLevelPreset;
import model.entities.FlavorPreset;
import model.entities.Size;
import model.entities.SizePreset;
import model.repositories.CreateTables;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryFlavor;
import model.repositories.RepositoryFlavorLevel;
import model.repositories.RepositoryOrderItems;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryProduct;
import model.repositories.RepositorySize;
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
            seedDefaultFlavorLevels();
            FlowHandler.log("DEFAULT_FLAVOR_LEVELS_SEEDED");
            seedDefaultFlavors();
            FlowHandler.log("DEFAULT_FLAVORS_SEEDED");
            seedDefaultSizes();
            FlowHandler.log("DEFAULT_SIZES_SEEDED");
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
            RepositoryProduct repoProduct = new RepositoryProduct();
            RepositoryFlavor repoFlavor = new RepositoryFlavor();
            RepositoryFlavorLevel repoFlavorLevel = new RepositoryFlavorLevel();
            RepositorySize repoSize = new RepositorySize();
            RepositoryOrderItems repoOrderItems = new RepositoryOrderItems();

            ControllerCadastro controllerCadastro = new ControllerCadastro(
                    repoPerson, repoUser, repoAddress, repoArea);
            ControllerLogin controllerLogin = new ControllerLogin(repoUser);
            ControllerProduct controllerProduct = new ControllerProduct(
                    repoProduct, repoFlavor, repoFlavorLevel, repoSize, repoOrderItems);

            ViewHome viewHome = new ViewHome();
            ControllerHome controllerHome = new ControllerHome(
                    viewHome, controllerCadastro, controllerLogin, controllerProduct);
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

    /** Cria níveis de sabor padrão no banco. */
    private static void seedDefaultFlavorLevels() throws java.sql.SQLException {
        RepositoryFlavorLevel repoLevel = new RepositoryFlavorLevel();
        for (FlavorLevelPreset preset : FlavorLevelPreset.values()) {
            FlavorLevel existing = repoLevel.findByNameFlavorLevel(preset.getName());
            if (existing != null) continue;
            repoLevel.createFlavorLevel(new FlavorLevel(null, preset.getName(), preset.getPrice()));
        }
    }

    /** Cria sabores padrão no banco usando os enums. */
    private static void seedDefaultFlavors() throws java.sql.SQLException {
        RepositoryFlavor repoFlavor = new RepositoryFlavor();
        RepositoryFlavorLevel repoLevel = new RepositoryFlavorLevel();
        for (FlavorPreset preset : FlavorPreset.values()) {
            Flavor existing = repoFlavor.findByNameFlavor(preset.getName());
            if (existing != null) continue;
            FlavorLevel level = repoLevel.findByNameFlavorLevel(preset.getLevelName());
            if (level == null) continue;
            repoFlavor.createFlavor(new Flavor(preset.getName(), level, preset.getDescription()));
        }
    }

    /** Cria tamanhos padrão no banco usando os enums. */
    private static void seedDefaultSizes() throws java.sql.SQLException {
        RepositorySize repoSize = new RepositorySize();
        for (SizePreset preset : SizePreset.values()) {
            Size existing = repoSize.findByNameSize(preset.getName());
            if (existing != null) continue;
            repoSize.createFlavorLevel(new Size(null, preset.getName(), preset.getYield(),
                    preset.getWeight(), preset.getPrice()));
        }
    }
}
