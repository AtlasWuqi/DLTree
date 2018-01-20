package edu.xtu.bio.comparator;

import java.util.Comparator;

import edu.xtu.bio.model.Size;

public class DescBySize implements Comparator<Size>{

private static final DescBySize instance = new DescBySize() ;
	
	private DescBySize() {
		super();
	}
	
	@Override
	public int compare(Size o1, Size o2) {
			if (o1.getCvsize() > o2.getCvsize()) {
				// DESC by size
				return -1;
			} else if (o1.getCvsize() < o2.getCvsize()) {
				return 1;
			} else {
				return 0;
			}
	}
	
	public static final DescBySize getInstance(){
		return instance ;
	}
}
