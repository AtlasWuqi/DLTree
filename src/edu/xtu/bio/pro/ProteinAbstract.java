package edu.xtu.bio.pro;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.xtu.bio.service.impl.AGPConstant;
import edu.xtu.bio.utils.Configuration;
import edu.xtu.bio.service.intf.Genome;
public class ProteinAbstract implements Genome{

	//public static final int BASE1 = 20 ;
	//public static final int BASE2 = 400 ;
	//public static final int BASE3 = 8000 ;
	//public static final int BASE4 = 160000 ;
	//public static final int BASE5 = 3200000 ;
	//public static final int BASE6 = 64000000 ;
	//public static final int BASE7 = 1280000000 ;
	
	//public static final int[] BASES = {BASE1,BASE2,BASE3,BASE4,BASE5,BASE6,BASE7};
	public static int MAXLEN = 7 ;
	public static int[] BASES = new int[MAXLEN+1];
	public static final String X =  "ACDEFGHIKLMNPQRSTVWY" ;
	public static String mode_switch = "1" ; 
	public static String ktuple_switch = "0" ;
	
	public static Map<String,Integer> charmap = null ;
	public static Logger logger = Logger.getLogger(ProteinAbstract.class);
	static{
		BASES[0]=1;
		for(int i=1;i<BASES.length;i++){
			BASES[i]=BASES[i-1]*20;
		}
		charmap = new HashMap<String, Integer>() ;
		for(Integer i=0;i<BASES[1];i++){
			charmap.put(X.substring(i, i+1), i);
		}
		Configuration.init(AGPConstant.PATH_CONF);
		mode_switch = Configuration.getProperty("switch.mode","1");
		ktuple_switch = Configuration.getProperty("switch.ktuple","0");
	}
	
	// warning:numerical overflow when the length of word is bigger than 5
	public static int getIndex1(String word) {
		return charmap.get(word).intValue();
	}

	public static int getIndex2(String word) {
		return BASES[1] * charmap.get(word.substring(0, 1)).intValue() 
				     + charmap.get(word.substring(1, 2)).intValue();
	}

	public static int getIndex3(String word) {
		return BASES[2] * charmap.get(word.substring(0, 1)).intValue() + 
			   BASES[1] * charmap.get(word.substring(1, 2)).intValue() + 
			           charmap.get(word.substring(2, 3)).intValue();
	}

	public static int getIndex4(String word) {
		return BASES[3] * charmap.get(word.substring(0, 1)).intValue()+
			   BASES[2] * charmap.get(word.substring(1, 2)).intValue()+
			   BASES[1] * charmap.get(word.substring(2, 3)).intValue()+
				       charmap.get(word.substring(3, 4)).intValue();
	}

	public static int getIndex5(String word) {
		return BASES[4] * charmap.get(word.substring(0, 1)).intValue()+
			   BASES[3] * charmap.get(word.substring(1, 2)).intValue()+
			   BASES[2] * charmap.get(word.substring(2, 3)).intValue()+
			   BASES[1] * charmap.get(word.substring(3, 4)).intValue()+
				       charmap.get(word.substring(4, 5)).intValue();
	}

	public static int getIndex6(String word) {
		return BASES[5] * charmap.get(word.substring(0, 1)).intValue()+
			   BASES[4] * charmap.get(word.substring(1, 2)).intValue()+
			   BASES[3] * charmap.get(word.substring(2, 3)).intValue()+
			   BASES[2] * charmap.get(word.substring(3, 4)).intValue()+
			   BASES[1] * charmap.get(word.substring(4, 5)).intValue()+
				       charmap.get(word.substring(5, 6)).intValue();
	}

	public static int getIndex7(String word) {
		return BASES[6] * charmap.get(word.substring(0, 1)).intValue()+
			   BASES[5] * charmap.get(word.substring(1, 2)).intValue()+
			   BASES[4] * charmap.get(word.substring(2, 3)).intValue()+
			   BASES[3] * charmap.get(word.substring(3, 4)).intValue()+
			   BASES[2] * charmap.get(word.substring(4, 5)).intValue()+
			   BASES[1] * charmap.get(word.substring(5, 6)).intValue()+
				       charmap.get(word.substring(6, 7)).intValue();
	}
	
	public static int toNumber(int[] bits,int from,int len){
		int sum = 0 ;
		int i = 0;
		for(;i<len;i++){
			sum += bits[from+i]*BASES[len-i];
		}
		sum += bits[from+i];
		return sum ;
	}
	
	public boolean computeSection(String section,String path,int index){
		return true;
	}
}
