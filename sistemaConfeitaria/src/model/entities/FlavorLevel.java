package model.entities;

public class FlavorLevel {
	private Integer id;
    private String name;
    private Double price;

	public FlavorLevel(){}
	
	public FlavorLevel(Integer id, String name, Double price) {
		this.id = id;
		this.name = name;
        this.price = price;
		
	}


	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}

	public void setId(Integer id){
		this.id = id;
	}

	
    public Integer getId() {
        return this.id;
    }

	public void setPrice(Double price){
		this.price = price;
	}

    public Double getPrice(){
        return this.price;
    }


	@Override
	public String toString() {
		return "FlavorLevel [getName()=" + getName() + ", getId()=" + getId() + ", getPrice()=" + getPrice() + "]";
	}



}



