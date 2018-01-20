package edu.xtu.bio.model;

import java.util.List;

public class GlobalKV {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月16日,下午10:29:52
	 * @version 1.0
	 */
	private long nonzero = 0L ;
	private List<LocalKV> locals = null ;
	public GlobalKV() {
		super();
	}
	public GlobalKV( List<LocalKV> locals) {
		super();
		this.locals = locals;
	}
	public GlobalKV(long nonzero, List<LocalKV> locals) {
		super();
		this.nonzero = nonzero;
		this.locals = locals;
	}
	public long getNonzero() {
		return nonzero;
	}
	
	public void setNonzero(long nonzero) {
		this.nonzero += nonzero;
	}

	public List<LocalKV> getLocals() {
		return locals;
	}
	public void setLocals(List<LocalKV> locals) {
		this.locals = locals;
	}
	
	public long getMemory(){
		return nonzero*12L;
	}
}
