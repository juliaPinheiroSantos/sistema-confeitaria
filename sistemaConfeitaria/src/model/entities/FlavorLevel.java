package model.entities;

public class FlavorLevel {
	private Integer id;
	private String level;
	private double price;
	
	public FlavorLevel() {}
	
	public FlavorLevel(Integer id, String level, double price) {
		setLevel(level);
		setPrice(price);
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getLevel() {
		return this.level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "FlavorLevel [getId()=" + getId() + ", getLevel()=" + getLevel() + ", getPrice()=" + getPrice() + "]";
	}
	
	
}
