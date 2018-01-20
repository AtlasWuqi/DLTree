package edu.xtu.bio.model;

public class SingleCV {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年4月3日,上午9:13:51
	 * @version 1.0
	 */
	
	private int[] keys = null ;
	private double[] values = null ;
	

	public SingleCV() {
		super();
	}

	public SingleCV(int[] keys, double[] values) {
		super();
		this.keys = keys;
		this.values = values;
	}

	public int[] getKeys() {
		return keys;
	}

	public void setKeys(int[] keys) {
		this.keys = keys;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
	
	public long getMemory(){
		return (long)this.keys.length*12L;
	}
}
