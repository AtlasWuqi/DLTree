package edu.xtu.bio.model;

public class ResultNew {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月23日,上午9:43:30
	 * @version 1.0
	 */
	
	private int k ;
	private int raw ;
	private int column ;
	private long src ;
	private long dst ;
	private boolean status ;
	
	
	public ResultNew() {
		super();
	}


	public ResultNew(int k, int raw, int column, long src, long dst, boolean status) {
		super();
		this.k = k;
		this.raw = raw;
		this.column = column;
		this.src = src;
		this.dst = dst;
		this.status = status;
	}


	public int getK() {
		return k;
	}


	public void setK(int k) {
		this.k = k;
	}


	public int getRaw() {
		return raw;
	}


	public void setRaw(int raw) {
		this.raw = raw;
	}


	public int getColumn() {
		return column;
	}


	public void setColumn(int column) {
		this.column = column;
	}


	public long getSrc() {
		return src;
	}


	public void setSrc(long src) {
		this.src = src;
	}


	public long getDst() {
		return dst;
	}


	public void setDst(long dst) {
		this.dst = dst;
	}


	public boolean isStatus() {
		return status;
	}


	public void setStatus(boolean status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return "ResultNew [k=" + k + ", raw=" + raw + ", column=" + column + ", src=" + src + ", dst=" + dst
				+ ", status=" + status + "]";
	}
	
}
