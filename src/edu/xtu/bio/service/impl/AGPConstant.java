package edu.xtu.bio.service.impl;

import java.io.File;

public class AGPConstant {
	/**
	 * @author WuQi@XTU
	 * @time_created 2015年11月3日,上午10:17:24
	 * @version 1.0
	 */

	
	public static final String SUFFIX_P_DLM = "00" ;
	public static final String SUFFIX_P_CVTREE = "01" ; 
	public static final String SUFFIX_C_DLM = "10" ; 
	public static final String SUFFIX_C_CVTREE = "11" ;
	
	public static final String TYPE_P = "0" ;
	public static final String TYPE_C = "1" ;
	
	public static final String METHOD_P = "0" ;
	public static final String METHOD_C = "1" ;
	
	public static final char URL_SLASH = '/' ;
	public static final String URL_PARAMETER = "1" ;
	public static final int BATCH_SIZE = 100 ;
	public static final int FILE_MAX_LENGTH = 50 ;
	public static final int BUFFER_SIZE = 10240 ;
	public static final String PATH_CONF = "conf"+File.separator +"conf.properties" ;

	public static final String   ITOL_MEGA = ".meg";
	public static final String   ITOL_PHYLIP = ".phylip";
	public static final String   ITOL_TREE = ".tree";
	public static final String   ITOL_NWK = ".nwk";
	public static final String   ITOL_ZIP = ".zip";
	public static final String   ITOL_LABELS = ".labels.txt";
	public static final String   ITOL_ANNOTAION = ".txt";
	public static final String   ITOL_LIST = ".list.txt";

	public static final String   CV_CHAR = ".cv";
	public static final String   CV_SEP = "\t";
	//,"genus","species","strains"
	
}
