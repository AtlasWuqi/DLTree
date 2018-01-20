package edu.xtu.bio.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

/**
 * @author WuQi@XTU
 * @time_created 2015年9月18日,‏‎10:59:30
 * @version 1.0
 */
public class FileUtil {

	private static final Logger Log = Logger.getLogger(FileUtil.class) ;
	
	public static boolean mkdirs(String path){
		File f = new File(path) ;
		if(!f.exists()){
			return f.mkdirs() ;
		}
		return true ;
	}
	
	public static boolean check(String path){
		File pf = new File(path).getParentFile() ;
		if(!pf.exists()){
			return pf.mkdirs() ;
		}
		return true ;
	}
	
	public static boolean exists(String path){
		return new File(path).exists() ;
	}

	
	public static boolean copy(File f,String dir){
		BufferedInputStream bi = null ;
		BufferedOutputStream bo = null ;
		try {
			bi = new BufferedInputStream(new FileInputStream(f)) ;
			bo = new BufferedOutputStream(new FileOutputStream( dir + File.separator + f.getName())) ;
			IOUtils.copy(bi, bo, Constant.bufferSize);
			bo.flush() ; 
			return true ;
		} catch (Exception e) {
			Log.error(e.getMessage());
			return false ;
		}finally{
			IOUtils.closeQuietly(bi);
	        IOUtils.closeQuietly(bo);
		}
    }
	
	public static boolean delete(File f){
		if(f.isDirectory()){
			File[] fs = f.listFiles(); 
			if(fs.length!=0){
				for(File e:fs){
					if(!delete(e))return false;
				}
			}
		}else{
			if(!f.delete())return false;
		}
		return true;
	}
	
	public static boolean delete(String path){
		File f = new File(path);
		return delete(f);
	}

}
