package model.entities;

public class Flavor {
	private Integer id;
	private String description;
	private String name;
	
	public Flavor() {}
	
	public Flavor(Integer id, String description, String name) {
		setDescription(description);
		setName(name);
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Flavor [getId()=" + getId() + ", getDescription()=" + getDescription() + ", getName()=" + getName()
				+ "]";
	}
	
	
}
