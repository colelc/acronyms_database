package edu.duke.fuqua.vo;

public class BoardMember extends Base {
	private Integer entityId;
	private String boardFname;
	private String boardLname;
	private String boardPreferredName;
	private String boardEmail;
	private String program;
	private String boardClass;
	private String hsmCert;
	private String otherDukeDegree;
	private String employer;
	private String jobTitle;
	private String linkedIn;
	private String curServeOn;
	private String boardPhoto;

	public BoardMember() {
		super();
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getBoardFname() {
		return boardFname;
	}

	public void setBoardFname(String boardFname) {
		this.boardFname = boardFname;
	}

	public String getBoardLname() {
		return boardLname;
	}

	public void setBoardLname(String boardLname) {
		this.boardLname = boardLname;
	}

	public String getBoardPreferredName() {
		return boardPreferredName;
	}

	public void setBoardPreferredName(String boardPreferredName) {
		this.boardPreferredName = boardPreferredName;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getBoardClass() {
		return boardClass;
	}

	public void setBoardClass(String boardClass) {
		this.boardClass = boardClass;
	}

	public String getHsmCert() {
		return hsmCert;
	}

	public void setHsmCert(String hsmCert) {
		this.hsmCert = hsmCert;
	}

	public String getOtherDukeDegree() {
		return otherDukeDegree;
	}

	public void setOtherDukeDegree(String otherDukeDegree) {
		this.otherDukeDegree = otherDukeDegree;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getCurServeOn() {
		return curServeOn;
	}

	public void setCurServeOn(String curServeOn) {
		this.curServeOn = curServeOn;
	}

	public String getBoardPhoto() {
		return boardPhoto;
	}

	public void setBoardPhoto(String boardPhoto) {
		this.boardPhoto = boardPhoto;
	}

	public String getBoardEmail() {
		return boardEmail;
	}

	public void setBoardEmail(String boardEmail) {
		this.boardEmail = boardEmail;
	}
}
