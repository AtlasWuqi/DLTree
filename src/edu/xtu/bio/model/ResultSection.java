package edu.xtu.bio.model;

public class ResultSection {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年11月1日,下午7:52:33
	 * @version 1.0
	 */
	
	private String section;
	private String path;
	private int index ;
	private boolean status ;
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public ResultSection() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ResultSection(String section, String path, int index, boolean status) {
		super();
		this.section = section;
		this.path = path;
		this.index = index;
		this.status = status;
	}
	
}
