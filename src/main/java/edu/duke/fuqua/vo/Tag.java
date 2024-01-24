package edu.duke.fuqua.vo;

public class Tag extends Base {
	private String name;

	public Tag() {
		super();
	}

	public Tag(Integer id, String name, boolean active) {
		super();
		this.setId(id);
		this.name = name;
		this.setActive(active);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
