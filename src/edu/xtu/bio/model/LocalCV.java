package edu.xtu.bio.model;

public class LocalCV {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月16日,下午10:29:52
	 * @version 1.0
	 */
	private int[] keys = null ;
	private double[] values = null ;
	private int[] zeros = null ;	
	
	public LocalCV() {
		super();
	}

	public LocalCV(int[] keys, double[] values, int[] zeros) {
		super();
		this.keys = keys;
		this.values = values;
		this.zeros = zeros;
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

	public int[] getZeros() {
		return zeros;
	}

	public void setZeros(int[] zeros) {
		this.zeros = zeros;
	}
	
}
