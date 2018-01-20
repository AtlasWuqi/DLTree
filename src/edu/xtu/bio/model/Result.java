package edu.xtu.bio.model;

public class Result {
	/**
	 * @author WuQi@XTU
	 * @time_created 2015年11月5日,下午7:39:41
	 * @version 1.0
	 */
	int index ;
	boolean status ;
	
	public int getIndex() {
		return index;
	}

	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public Result() {
		super();
	}
	public Result( int index, boolean status) {
		super();
		this.index = index;
		this.status = status;
	}
	
}
