package edu.xtu.bio.model;

import java.util.List;

public class GlobalCV {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月16日,下午10:29:52
	 * @version 1.0
	 */
	private long nonzero ;
	private long zero ;
	private List<LocalCV> locals = null ;
	public GlobalCV() {
		super();
	}
	public GlobalCV( List<LocalCV> locals) {
		super();
		this.locals = locals;
	}
	public GlobalCV(Long nonzero, Long zero, List<LocalCV> locals) {
		super();
		this.nonzero = nonzero;
		this.zero = zero;
		this.locals = locals;
	}
	public long getNonzero() {
		return nonzero;
	}
	public void setNonzero(long nonzero) {
		this.nonzero = nonzero;
	}
	public long getZero() {
		return zero;
	}
	public void setZero(long zero) {
		this.zero = zero;
	}
	public List<LocalCV> getLocals() {
		return locals;
	}
	public void setLocals(List<LocalCV> locals) {
		this.locals = locals;
	}
	
	public long getMemory(){
		return nonzero*12L+zero*4L;
	}
}
