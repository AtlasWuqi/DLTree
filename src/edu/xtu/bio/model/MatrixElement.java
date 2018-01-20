package edu.xtu.bio.model;

public class MatrixElement {
	/**
	 * @author WuQi@XTU
	 * @time_created 2015年11月3日,上午10:52:03
	 * @version 1.0
	 */
	
	private int row ;
	private int column ;
	private double value;
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public MatrixElement(int row, int column, double value) {
		super();
		this.row = row;
		this.column = column;
		this.value = value;
	}
	public MatrixElement() {
		super();
	}
	
}
