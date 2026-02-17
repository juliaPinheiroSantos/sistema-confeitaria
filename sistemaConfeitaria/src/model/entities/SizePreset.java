package model.entities;

/**
 * Tamanhos padrao para seed inicial.
 */
public enum SizePreset {
    P("P", "8 fatias", "1kg", 0.0),
    M("M", "12 fatias", "1.5kg", 5.0),
    G("G", "20 fatias", "2.5kg", 10.0);

    private final String name;
    private final String yield;
    private final String weight;
    private final double price;

    SizePreset(String name, String yield, String weight, double price) {
        this.name = name;
        this.yield = yield;
        this.weight = weight;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getYield() {
        return yield;
    }

    public String getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }
}
