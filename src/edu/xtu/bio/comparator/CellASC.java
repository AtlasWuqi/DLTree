package edu.xtu.bio.comparator;

import java.util.Comparator;

import edu.xtu.bio.model.Cell;

/**
 * @author WuQi@XTU
 * @time_created 2015��9��18��,����10:34:14
 * @version 1.0
 */
public class CellASC implements Comparator<Cell>{
	
	private static final CellASC instance = new CellASC() ;
	
	private CellASC() {
		super();
	}

	@Override
	public int compare(Cell o1, Cell o2) {
		if(o1.getKey()>o2.getKey()){
			//ASC by key
			return 1 ;
		}else if(o1.getKey()<o2.getKey() ){
			return -1 ;
		}else{
			return 0 ;
		}
	}		
	
	public static final CellASC getInstance(){
		return instance ;
	}
}