package model.entities;

public class Category {
	private Integer id;
	private double price;
	private int yield;
	private String size;
	
	public Category() {}
	
	public Category(Integer id, double price, int yield, String size) {
		setPrice(price);
		setYield(yield);
		setSize(size);
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public double getPrice() {
		return this.price;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	
	public int getYield() {
		return this.yield;
	}
	
	public void setYield(int yield) {
		this.yield = yield;
	}
	
	public String getSize() {
		return this.size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Category [getId()=" + getId() + ", getPrice()=" + getPrice() + ", getYield()=" + getYield()
				+ ", getSize()=" + getSize() + "]";
	}
	
	
}
