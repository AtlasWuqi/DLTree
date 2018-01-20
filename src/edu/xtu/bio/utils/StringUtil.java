package edu.xtu.bio.utils;

public class StringUtil {
	public static String truncate(String s,int length){
		if(s==null)return null;
		return s.length()>length?s.substring(0,length):s ;
	}
}
