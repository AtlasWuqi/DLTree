package edu.xtu.bio.comparator;

import java.util.Comparator;

import edu.xtu.bio.model.Element;

/**
 * @author WuQi@XTU
 * @time_created 2015��9��18��,����10:34:14
 * @version 1.0
 */
public class ElementASC implements Comparator<Element>{
	
	private static final ElementASC instance = new ElementASC() ;
	
	private ElementASC() {
		super();
	}

	@Override
	public int compare(Element o1, Element o2) {
		if(o1.getKey()>o2.getKey()){
			//ASC by key
			return 1 ;
		}else if(o1.getKey()<o2.getKey() ){
			return -1 ;
		}else{
			return 0 ;
		}
	}		
	
	public static final ElementASC getInstance(){
		return instance ;
	}
}