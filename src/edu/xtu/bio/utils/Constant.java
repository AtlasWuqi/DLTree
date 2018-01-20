package edu.xtu.bio.utils;

import org.apache.log4j.Logger;

public class Constant {
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年9月21日,下午9:37:44
	 * @version 1.0
	 */
	private static final Logger LOG = Logger.getLogger(Constant.class); 
	
	public static int bufferSize = Configuration.getProperty("buffer.size", 1024) ;
	public static String resultDir ;

	static{
		resultDir = Configuration.getProperty("resultDir");
		if(resultDir!=null){
			FileUtil.mkdirs(resultDir);
		}else{
			LOG.fatal("not found para:resultDir,quit" );
			System.exit(1); ;
		}
	}
	
}
