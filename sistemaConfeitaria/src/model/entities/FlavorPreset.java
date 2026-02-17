package model.entities;

/**
 * Sabores padrao para seed inicial.
 */
public enum FlavorPreset {
    CHOCOLATE("Chocolate", "Tradicional", "Chocolate classico"),
    BAUNILHA("Baunilha", "Tradicional", "Baunilha suave"),
    MORANGO("Morango", "Tradicional", "Morango natural"),
    NINHO("Ninho", "Especial", "Leite ninho"),
    COCO("Coco", "Especial", "Coco fresco");

    private final String name;
    private final String levelName;
    private final String description;

    FlavorPreset(String name, String levelName, String description) {
        this.name = name;
        this.levelName = levelName;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getDescription() {
        return description;
    }
}
