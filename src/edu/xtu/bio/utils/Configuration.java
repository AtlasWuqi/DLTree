package edu.xtu.bio.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author WuQi@XTU
 * @time_created 2015年10月31日,下午4:37:02
 * @version 1.0
 */
public class Configuration {
	private static Logger logger = Logger.getLogger(Configuration.class);
	private static Properties pro ;

	public static synchronized boolean init(String path){
		if(pro==null)pro = getPro(path) ;
		return pro!=null ;
	}
	
	public static Properties getPro(){
		return pro ;
	}

	public static String getProperty(String key){
		return pro.getProperty(key) ;
	}
	public static String getProperty(String key,String defaultValue){
		return pro.getProperty(key,defaultValue) ;
	}
	
	public static Properties getPro(String path) {
		InputStream is = null;
		try {
			try {
				is = new FileInputStream(path);
				Properties p = new Properties();
				p.load(is);
				return p;
			}  finally {
				if(is != null) {
					is.close();
				}
			}
		} catch (Exception e) {
			logger.fatal(e.getMessage()) ;
			return null ;
		}
	}
	
	public static int getProperty(String para,int def){
		String bs = getProperty(para)  ;
		try {
			return Integer.parseInt(bs) ;
		} catch (NumberFormatException e){
			logger.error("not found para or error format:" +para);
			return  def;
		}
	}
	
	public static long getProperty(String para,long def){
		String bs = getProperty(para)  ;
		try {
			return Long.parseLong(bs) ;
		} catch (NumberFormatException e){
			logger.error("not found para or error format:" +para);
			return  def;
		}
	}
	
}
