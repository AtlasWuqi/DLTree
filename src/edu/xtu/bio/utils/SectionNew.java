package edu.xtu.bio.utils;

public class SectionNew {
	
	private String name ;
	private int nonzero;
	private int zero;
	private int[] k5 ;

	public SectionNew() {
		super();
	}

	public SectionNew(String name) {
		super();
		this.name = name;
	}

	public SectionNew(String name, int[] k5) {
		super();
		this.name = name;
		this.k5 = k5;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getK5() {
		return k5;
	}

	public void setK5(int[] k5) {
		this.k5 = k5;
	}

	public int getNonzero() {
		return nonzero;
	}

	public void setNonzero(int nonzero) {
		this.nonzero = nonzero;
	}

	public int getZero() {
		return zero;
	}

	public void setZero(int zero) {
		this.zero = zero;
	}

}
