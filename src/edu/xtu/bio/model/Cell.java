package edu.xtu.bio.model;

public class Cell {
	/**
	 * @author WuQi@XTU
	 * @time_created 2015年11月1日,上午11:38:28
	 * @version 1.0
	 */
	
	private int key ;
	private double value;
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Cell() {
		super();
	}
	public Cell(int key, double value) {
		super();
		this.key = key;
		this.value = value;
	}
	@Override
	public String toString() {
		return  key + "," + value ;
	}
}
