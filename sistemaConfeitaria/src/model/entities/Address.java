package model.entities;

public class Address {
	private Integer id;
	private String cep;
	private String street;
	private Integer number;
	private String complement;
	private String reference;
	private Area area;
	
	public Address() {}
	
	public Address(Integer id, String cep, String street, Integer number, String complement, String reference, Area area) {
		setCep(cep);
		setStreet(street);
		setNumber(number);
		setComplement(complement);
		setArea(area);
	}
	
	public Integer getInteger() {
		return this.id;
	}
	
	public String getCep() {
		return this.cep;
	}
	
	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public String getStreet() {
		return this.street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public Integer getNumber() {
		return this.number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public String getComplement() {
		return this.complement;
	}
	
	public void setComplement(String complement) {
		this.complement = complement;
	}
	
	public String getReference() {
		return this.reference;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public Area getArea() {
		return this.area;
	}
	
	public void setArea(Area area) {
		this.area = area;
	}

	@Override
	public String toString() {
		return "Address [getInteger()=" + getInteger() + ", getCep()=" + getCep() + ", getStreet()=" + getStreet()
				+ ", getNumber()=" + getNumber() + ", getComplement()=" + getComplement() + ", getReference()="
				+ getReference() + "]";
	}
	
	
}

