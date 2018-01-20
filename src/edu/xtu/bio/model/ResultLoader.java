package edu.xtu.bio.model;

public class ResultLoader {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月28日,上午8:27:28
	 * @version 1.0
	 */
	private Object o;
	private int index ;
	private boolean status ;
	public ResultLoader() {
		super();
	}
	
	public ResultLoader(int index) {
		super();
		this.index = index;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}
	
	public Object getO() {
		return o;
	}

	public void setO(Object o) {
		this.o = o;
	}

	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
}
