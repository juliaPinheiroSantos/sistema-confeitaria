package controller;

import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.FlavorLevelPreset;
import model.entities.FlavorPreset;
import model.entities.Product;
import model.entities.Size;
import model.entities.SizePreset;
import model.repositories.RepositoryFlavor;
import model.repositories.RepositoryFlavorLevel;
import model.repositories.RepositoryOrderItems;
import model.repositories.RepositoryProduct;
import model.repositories.RepositorySize;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Controller de produtos: CRUD e totais.
 */
public class ControllerProduct {

    private final RepositoryProduct repoProduct;
    private final RepositoryFlavor repoFlavor;
    private final RepositoryFlavorLevel repoFlavorLevel;
    private final RepositorySize repoSize;
    private final RepositoryOrderItems repoOrderItems;

    public ControllerProduct(RepositoryProduct repoProduct,
                             RepositoryFlavor repoFlavor,
                             RepositoryFlavorLevel repoFlavorLevel,
                             RepositorySize repoSize,
                             RepositoryOrderItems repoOrderItems) {
        this.repoProduct = repoProduct;
        this.repoFlavor = repoFlavor;
        this.repoFlavorLevel = repoFlavorLevel;
        this.repoSize = repoSize;
        this.repoOrderItems = repoOrderItems;
    }

    public List<Product> listProducts() {
        try {
            return repoProduct.findAllProduct();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Flavor> listFlavors() {
        try {
            return repoFlavor.findAllFlavor();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Size> listSizes() {
        try {
            return repoSize.findAllSize();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean createProduct(Product product) {
        try {
            return repoProduct.createProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        try {
            return repoProduct.updateProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(Product product) {
        try {
            return repoProduct.deleteProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getTotalProductsPurchased() {
        try {
            return repoOrderItems.getTotalProductsPurchased();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Seed manual de níveis, sabores e tamanhos padrão.
     */
    public boolean seedDefaults() {
        try {
            for (FlavorLevelPreset preset : FlavorLevelPreset.values()) {
                FlavorLevel existing = repoFlavorLevel.findByNameFlavorLevel(preset.getName());
                if (existing == null) {
                    repoFlavorLevel.createFlavorLevel(
                            new FlavorLevel(null, preset.getName(), preset.getPrice()));
                }
            }
            for (SizePreset preset : SizePreset.values()) {
                Size existing = repoSize.findByNameSize(preset.getName());
                if (existing == null) {
                    repoSize.createFlavorLevel(new Size(null, preset.getName(),
                            preset.getYield(), preset.getWeight(), preset.getPrice()));
                }
            }
            for (FlavorPreset preset : FlavorPreset.values()) {
                Flavor existing = repoFlavor.findByNameFlavor(preset.getName());
                if (existing == null) {
                    FlavorLevel level = repoFlavorLevel.findByNameFlavorLevel(preset.getLevelName());
                    if (level != null) {
                        repoFlavor.createFlavor(new Flavor(preset.getName(), level, preset.getDescription()));
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
