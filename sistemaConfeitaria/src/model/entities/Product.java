package model.entities;

public class Product {
	private Integer id;
	private String name;
	private String description;
	private boolean status;
	
	public Product() {}
	
	public Product(Integer id, String name, String description, boolean status) {
		setName(name);
		setDescription(description);
		setStatus(status);
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean getStatus() {
		return this.status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Product [getId()=" + getId() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
				+ ", getStatus()=" + getStatus() + "]";
	}
	
	
	
	
}
