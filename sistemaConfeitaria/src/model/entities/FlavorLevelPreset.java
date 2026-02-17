package model.entities;

/**
 * Niveis de sabor padrao para seed inicial.
 */
public enum FlavorLevelPreset {
    TRADICIONAL("Tradicional", 0.0),
    ESPECIAL("Especial", 5.0),
    PREMIUM("Premium", 10.0);

    private final String name;
    private final double price;

    FlavorLevelPreset(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
