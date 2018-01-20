package edu.xtu.bio.comparator;

import java.io.File;
import java.util.Comparator;

/**
 * @author WuQi@XTU
 * @time_created 2015年9月18日,下午‏‎10:35:26
 * @version 1.0
 */
public class DescByFileSize implements Comparator<File>{

	@Override
	public int compare(File o1, File o2) {
		if(o1.length()>o2.length()){
			//DESC by size
			return -1 ;
		}else if(o1.length()<o2.length()){
			return 1 ;
		}else{
			return 0 ;
		}

	}

	
}
