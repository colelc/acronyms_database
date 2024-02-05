package edu.duke.fuqua.vo;

public class ExcelAcronym extends Base {

	private String acronym;
	private String refersTo;
	private String definition;
	private String areaKey;
	private String tagString;

	public ExcelAcronym() {
		super();
		this.acronym = "";
		this.refersTo = "";
		this.definition = "";
		this.areaKey = "";
		this.tagString = "";
	}

	public ExcelAcronym(String acronym, String refersTo, String definition, String areaKey) {
		this();
		this.acronym = acronym;
		this.refersTo = refersTo;
		this.definition = definition;
		this.areaKey = areaKey;
	}

	public ExcelAcronym(String acronym, String refersTo, String definition, String areaKey, String tagString) {
		this(acronym, refersTo, definition, areaKey);
		this.tagString = tagString;
	}

	public ExcelAcronym(Integer id, String acronym, String refersTo, String definition, String areaKey) {
		this(acronym, refersTo, definition, areaKey);
		this.setId(id);
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getRefersTo() {
		return refersTo;
	}

	public void setRefersTo(String refersTo) {
		this.refersTo = refersTo;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getAreaKey() {
		return areaKey;
	}

	public void setAreaKey(String areaKey) {
		this.areaKey = areaKey;
	}

	public String getTagString() {
		return tagString;
	}

	public void setTagString(String tagString) {
		this.tagString = tagString;
	}

}
