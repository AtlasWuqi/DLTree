package edu.xtu.bio.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class MD5Util {

	 //用于加密的字符   
	public static final char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' }; 
	public static final Logger logger = Logger.getLogger(MD5Util.class) ;
    public final static String MD5(String line) {  
       
        try {  
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中  
            byte[] btInput = line.getBytes();  
               
            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。  
            MessageDigest mdInst = MessageDigest.getInstance("MD5");  
            
            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要  
            mdInst.update(btInput);  
            
            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文  
            byte[] md = mdInst.digest();  
               
            // 把密文转换成十六进制的字符串形式  
            int j = md.length;  
            char str[] = new char[j * 2];  
            int k = 0;  
            for (int i = 0; i < j; i++) {   //  i = 0  
                byte byte0 = md[i];  //95  
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5   
                str[k++] = md5String[byte0 & 0xf];   //   F  
            }  
               
            //返回经过加密后的字符串  
            return new String(str);  
               
        } catch (Exception e) {  
            return null;  
        }  
    }
    
    
    public static String getMD5(MessageDigest mdInst){
    	// 摘要更新之后，通过调用digest（）执行哈希计算，获得密文  
        byte[] md = mdInst.digest();  
        
    	// 把密文转换成十六进制的字符串形式  
        int j = md.length;  
        char str[] = new char[j * 2];  
        int k = 0;  
        for (int i = 0; i < j; i++) {   //  i = 0  
            byte byte0 = md[i];  //95  
            str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5   
            str[k++] = md5String[byte0 & 0xf];   //   F  
        }  
           
        //返回经过加密后的字符串  
        return new String(str); 
    }
    
    	public static String filemd5(String path){
		BufferedInputStream bi = null ; 
		try {
			bi = new BufferedInputStream(new FileInputStream(path)) ;
			MessageDigest mdInst = MessageDigest.getInstance("MD5") ;
			byte[] buffer = new byte[102400];
			int count = 0;
			int n = 0;
			while (-1 != (n = bi.read(buffer))) {
			    mdInst.update(buffer, 0, n);
			    count += n;
			}
			return getMD5(mdInst);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null ;
		}finally{
			IOUtils.closeQuietly(bi);
		}
    	}

    	public static String filemd5OnlyAlpha(String path){
		BufferedReader reader = null;
		String line = null;
		try {
				reader = new BufferedReader(new FileReader(path));
				MessageDigest mdInst = MessageDigest.getInstance("MD5") ;
				while ((line = reader.readLine()) != null) {
					if(line.startsWith(">")||line.length()==0){continue;}
					mdInst.update(line.getBytes());
				}
				return getMD5(mdInst);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null ;
		}finally{
			IOUtils.closeQuietly(reader);
		}
    	}

    public static void main(String[] args) {
    		//System.out.println("linux md5sum:"+filemd5(args[0]));
		System.out.println(filemd5OnlyAlpha(args[0]));
/**
    	 byte[] btInput = "WUQI".getBytes();
         //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。  
         MessageDigest mdInst = null ;
		try {
			mdInst = MessageDigest.getInstance("MD5");
			//MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要  
	        mdInst.update(btInput); 
	        System.out.println(getMD5(mdInst));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
**/
	}


}
