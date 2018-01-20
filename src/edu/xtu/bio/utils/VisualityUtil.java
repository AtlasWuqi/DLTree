package edu.xtu.bio.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.xtu.bio.service.impl.AGPConstant;

/**
 * @author WuQi@XTU
 * @time_created 2015��9��22��,����11:20:16
 * @version 1.0
 */
public class VisualityUtil {
	private static final Logger logger = Logger.getLogger(VisualityUtil.class) ;
	
	public static  void printMatrix(double[][] matrix){
		//0.4963874614434474
		System.out.println("-----------------------");
		for(double[] line: matrix){
			for(double e:line){
				System.out.printf("%+20.19f  ", e) ;
			}
			System.out.println();
		}
		System.out.println("-----------------------");
		/*
		System.out.println(Double.MAX_VALUE);
		System.out.println(double.MAX_VALUE);
		System.out.println(Long.MAX_VALUE);
		System.out.println(Long.MIN_VALUE);
		System.out.println(Integer.MAX_VALUE);
		*/
	}
	
	public static  void saveMatrixbyRow(List<String>genome_name, double[][] matrix,String path){
		//0.4963874614434474
		//System.out.println("-----------------------");
		BufferedWriter writer = null ;
		try {
			try {
				writer = new BufferedWriter(new FileWriter(path)) ;
				
				for(String s:genome_name){
					writer.write(s);
					writer.write(AGPConstant.CV_SEP);
				}
				writer.newLine();
				for(double[] line: matrix){
					for(double e:line){
						writer.write(e+AGPConstant.CV_SEP);
					}
					writer.newLine();
				}
				writer.flush();
			}finally{
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static  void saveMatrixbyColumn(List<String> genome_name, double[][] matrix,String path){
		
		BufferedWriter writer = null ;
		try {
			try {
				writer = new BufferedWriter(new FileWriter(path)) ;
				
				int i = 0 ;
				for(double[] line: matrix){
					writer.write(genome_name.get(i)+AGPConstant.CV_SEP);
					for(double e:line){
						writer.write(e+AGPConstant.CV_SEP);
					}
					writer.newLine();
					i++ ;
				}
				
				writer.flush();
			}finally{
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static  void saveMatrixbyMEGE(List<String>genome_name, double[][] matrix,String path){
		/**
		 * For a set of m sequences (or taxa), there are m(m-1)/2 pairwise distances. 
		 * These distances can be arranged either in the lower-left 
		 * or in the upper-right triangular matrix. 
		 * After writing the #mega,!Title,!Description, 
		 * and !Format commands (some of which are optional), 
		 * you then need to write all the taxa names (see below).
		 *  Taxa names are followed by the distance matrix. An example of a matrix is:
#one
#two
#three
#four
#five
1.0 2.0 3.0 4.0
    3.0 2.5 4.6
        1.3 3.6
            4.2
Lower-left matrix
Upper-right matrix

		 */
		PrintWriter writer = null ;
		try {
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path))) ;
				writer.println("#mega");
				
				writer.println("!TITLE;");
				for(String s:genome_name){
					writer.println("#"+s);
				}
				for(int i=1;i<matrix.length;i++){
					writer.print(matrix[0][i]);
					for(int j=1;j<i;j++){
						writer.print(AGPConstant.CV_SEP);
						writer.print(matrix[j][i]);
					}
					writer.println();
				}
				writer.flush();
			}finally{
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static  void saveMatrixbyPHYLIP(List<String>genome_name, double[][] matrix,String path){
		/**
the number of sequences is recorded on the first line, after that there is a
Lower-left or Square matrix with the sequence name on the left side of each line
		 */
		PrintWriter writer = null ;
		try {
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path))) ;
				writer.println(genome_name.size());
				writer.println(genome_name.get(0));
				
				for(int i=1;i<matrix.length;i++){
					writer.print(genome_name.get(i));
					for(int j=0;j<i;j++){
						writer.print(AGPConstant.CV_SEP);
						writer.print(matrix[j][i]);
					}
					writer.println();
				}
				writer.flush();
			}finally{
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(!Configuration.init(AGPConstant.PATH_CONF))return ;
		double[][] matrix =
		
		{
		{0.0000000000000000000f , 0.4963874614434474000f , 0.4956136161067371500f},  
		{0.4963874614434474000f , 0.0000000000000000000f , 0.4972893882924198000f},
		{0.4956136161067371500f , 0.4972893882924198000f , 0.0000000000000000000f}
		} ;
		List<String> genome_name = new ArrayList<String>(3) ;
		genome_name.add("species_a") ;
		genome_name.add("species_b") ;
		genome_name.add("species_c") ;
		String distance = Configuration.getProperty("distance") ;
		String path = distance+File.separator+"test_distance.txt" ;
		saveMatrixbyRow(genome_name,matrix,path);
		System.out.println("end");
	}
}
