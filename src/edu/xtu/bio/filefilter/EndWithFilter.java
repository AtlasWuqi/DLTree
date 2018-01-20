package edu.xtu.bio.filefilter;

import java.io.File;
import java.io.FilenameFilter;

	/**
	 * @author WuQi@XTU
	 * @time_created 2016年4月9日,下午10:25:31
	 * @version 1.0
	 */
public class EndWithFilter implements FilenameFilter{
	
	private String filename ;
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public EndWithFilter() {
		super();
	}
	public EndWithFilter(String filename) {
		super();
		this.filename = filename;
	}
	
	@Override
	public boolean accept(File dir, String name){
		return name.endsWith(this.filename) ;
	}
}

