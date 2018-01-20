package edu.xtu.bio.model;

public class LocalKV {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月16日,下午10:29:52
	 * @version 1.0
	 */
	private int[] keys = null ;
	private double[] values = null ;

	public LocalKV() {
		super();
	}

	public LocalKV(int[] keys, double[] values) {
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
	
}
