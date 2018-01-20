package edu.xtu.bio.model;

/**
 * @author WuQi@XTU
 * @time_created 2015��9��18��,����9:20:54
 * @version 1.0
 */
public class Element{
	private int key ;
	private int value;
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Element() {
		super();
	}

	public Element(int key, int value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return key + " " + value ;
	}
	
}
