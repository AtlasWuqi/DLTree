package edu.xtu.bio.utils;

import java.util.List;

import edu.xtu.bio.model.Element;

/**
 * @author WuQi@XTU
 * @time_created 2015��9��18��,����9:37:27
 * @version 1.0
 */
public class Section {
	private String name ;
	private List<Element> elements ;
	
	
	public Section(String name) {
		super();
		this.name = name;
	}

	public Section(String name, List<Element> elements) {
		super();
		this.name = name;
		this.elements = elements;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Element> getElements() {
		return elements;
	}
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
	
}
