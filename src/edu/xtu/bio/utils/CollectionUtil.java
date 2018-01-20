package edu.xtu.bio.utils;

import java.util.Collections;
import java.util.List;

import edu.xtu.bio.comparator.CellASC;
import edu.xtu.bio.comparator.ElementASC;
import edu.xtu.bio.model.Cell;
import edu.xtu.bio.model.Element;
import edu.xtu.bio.model.GlobalCV;
import edu.xtu.bio.model.GlobalKV;
import edu.xtu.bio.model.LocalCV;
import edu.xtu.bio.model.LocalKV;
import edu.xtu.bio.model.SingleCV;

/**
 * @author WuQi@XTU
 * @time_created 2015��9��20��,����10:24:47
 * @version 1.0
 */

public class CollectionUtil {
	public static final int retrieveElement(List<Integer> list,Integer key,int fromIndex){
		return Collections.binarySearch(list.subList(fromIndex, list.size()),key) ;
	}
	
	public static final Element retrieveElementExisted(List<Element> list,Element key){
		return list.get(Collections.binarySearch(list, key,ElementASC.getInstance())) ;
	}
	
	public static final int retrieveElement(List<Element> list,Element key,int fromIndex){
		return Collections.binarySearch(list.subList(fromIndex, list.size()), key,ElementASC.getInstance()) ;
	}
	
	public static final Cell retrieveCellExisted(List<Cell> list,Cell key){
		return list.get(Collections.binarySearch(list, key,CellASC.getInstance())) ;
	}
	
	public static final int retrieveCell(List<Cell> list,Cell key,int fromIndex){
		return Collections.binarySearch(list.subList(fromIndex, list.size()), key,CellASC.getInstance()) ;
	}
	
	public static final int retrieveCell(List<Cell> list,Cell key){
		return Collections.binarySearch(list, key,CellASC.getInstance()) ;
	}
	
	public static final int search(int[] list,int key,int fromIndex){
		return BinarySearch.search(list,key,fromIndex) ;
	}
	
	public static final int search(int[] list,int key){
		return BinarySearch.search(list,key,0) ;
	}
	
	
	
	private static final double product(List<List<Cell>> section1,List<List<Cell>> section2){
		double[] sum = new double[3] ;
		List<Cell> s1 = null ;
		List<Cell> s2 = null ;
		Cell target = null ;
		int fromIndex = 0 ;
		int returnedIndex = 0 ;
		for(int i=0; i<section1.size(); i++){
			s1 = section1.get(i) ;
			s2 = section2.get(i) ;
			for(Cell e:s1){
				sum[1] += e.getValue()*e.getValue();
			}
			for(Cell e:s2){
				sum[2] += e.getValue()*e.getValue();
			}
			
			if(!s1.isEmpty()&&!s2.isEmpty()&&
					s1.get(0).getKey()<=s2.get(s2.size()-1).getKey() && 
					s2.get(0).getKey()<=s1.get(s1.size()-1).getKey()){
				//s1 and s2 is not empty and is intersectant  
					for(Cell e:s1){
						returnedIndex = retrieveCell(s2,e,fromIndex) ;
						if(returnedIndex>=0){
							fromIndex += returnedIndex ; //get index
							target = s2.get(fromIndex) ;
							sum[0] += e.getValue() * target.getValue() ;
							fromIndex++ ; // add 1 to index
						}else{
							// (-(insertion point) - 1)
							fromIndex += -(returnedIndex+1) ; //set index
						}
						if(fromIndex>=s2.size())break;
					}
			}
			fromIndex = 0 ;
		}
		return sum[0] / Math.sqrt( sum[1]*sum[2] ) ;
	}
	
	private static final double productStrict(List<List<Cell>> section1,List<List<Cell>> section2){
		double[] sum = new double[3] ;
		
		List<Cell> s1 = null ;
		List<Cell> s2 = null ;
		Cell target = null ;

		int returnedIndex = 0 ;
		for(int i=0; i<section1.size(); i++){
			s1 = section1.get(i) ;
			s2 = section2.get(i) ;	
			for(Cell e:s1){
				sum[1] += e.getValue()*e.getValue();
			}
			for(Cell e:s2){
				sum[2] += e.getValue()*e.getValue();
			}
			
			if(!s1.isEmpty()&&!s2.isEmpty()&&
					s1.get(0).getKey()<=s2.get(s2.size()-1).getKey() && 
					s2.get(0).getKey()<=s1.get(s1.size()-1).getKey()){
				//s1 and s2 is not empty and is intersectant  
				for(Cell e:s1){
					returnedIndex = retrieveCell(s2,e) ;
					if(returnedIndex>=0){
						target = s2.get(returnedIndex) ; 
						sum[0] += e.getValue() * target.getValue() ;
					}
				}
			}
		}
		return sum[0] / Math.sqrt( sum[1]*sum[2] ) ;
	}
	
	private static final double product(SingleCV cv1, SingleCV cv2) {
		double[] sum = new double[3];
		int[] keys1 = cv1.getKeys();
		double[] values1 = cv1.getValues();
		int[] keys2 = cv2.getKeys();
		double[] values2 = cv2.getValues();
		int f1 = 0;
		int r1 = 0;
		for (double e : values1) {
			sum[1] += e*e;
		}
		for (double e : values2) {
			sum[2] += e*e;
		}

		if (keys1.length != 0 && keys2.length != 0 && keys1[0] <= keys2[keys2.length - 1]
				&& keys2[0] <= keys1[keys1.length - 1]) {
			for (int j = 0; j < keys1.length; j++) {
				// return the global index of the array
				r1 = search(keys2, keys1[j], f1);
				if (r1 >= 0) {
					sum[0] += values1[j] * values2[r1];
					f1 = r1 + 1;
					continue;
				} else {
					f1 = -(r1 + 1);
				}
				if (f1 >= keys2.length)
					break;
			}
		}
		return sum[0] / Math.sqrt(sum[1] * sum[2]);
	}
	
	
	private static final double product(GlobalCV cv1,GlobalCV cv2){
		double[] sum = new double[3] ;
		List<LocalCV> list1 = cv1.getLocals() ;
		List<LocalCV> list2 = cv2.getLocals() ;
		LocalCV local1 = null ;
		LocalCV local2 = null ;
		int[] keys1 = null;
		double[] values1 = null ;
		int[] zeros1 = null ;
		int[] keys2 = null;
		double[] values2 = null ;
		int[] zeros2 = null ;
		
		int f1 = 0 ;
		int r1 = 0 ;
		
		for(int i=0; i<list1.size(); i++){
			local1 = list1.get(i) ;
			local2 = list2.get(i) ;
			
			keys1 = local1.getKeys();
			values1 = local1.getValues();
			zeros1 = local1.getZeros() ;
			
			keys2 = local2.getKeys();
			values2 = local2.getValues();
			zeros2 = local2.getZeros() ;
			
			for(double e:values1){
				sum[1] += e*e;
			}
			sum[1]+=zeros1.length;
			for(double e:values2){
				sum[2] += e*e;
			}
			sum[2]+=zeros2.length;
		
			if( keys1.length!=0){
				f1=0;
				if(keys2.length!=0 && keys1[0]<=keys2[keys2.length-1] && keys2[0]<=keys1[keys1.length-1]){
					for(int j=0;j<keys1.length;j++){
							//return the global index of the array
							r1 = search(keys2,keys1[j],f1) ;
							if(r1>=0){
								sum[0]+= values1[j]*values2[r1] ;
								f1 = r1+1;
								continue;
							}else{
								f1 = -(r1+1);
							}
							if(f1>=keys2.length)break;
					}
				}
				f1=0;
				if(zeros2.length!=0 && keys1[0]<=zeros2[zeros2.length-1] && zeros2[0]<=keys1[keys1.length-1]){
					for(int j=0;j<keys1.length;j++){
							r1 = search(zeros2,keys1[j],f1) ;
							if(r1>=0){
								sum[0]+= -values1[j] ;
								f1 = r1+1;
								continue;
							}else{
								f1 = -(r1+1);
							}
							if(f1>=zeros2.length)break;
					}
				}
			}
			if( zeros1.length!=0){
				f1=0;
				if(keys2.length!=0 && zeros1[0]<=keys2[keys2.length-1] && keys2[0]<=zeros1[zeros1.length-1]){
					for(int j=0;j<zeros1.length;j++){
							r1 = search(keys2,zeros1[j],f1) ;
							if(r1>=0){
								sum[0]+= -values2[r1] ;
								f1 = r1+1;
								continue;
							}else{
								f1 = -(r1+1);
							}
							if(f1>=keys2.length)break;
					}
				}
				f1=0;
				if(zeros2.length!=0 && zeros1[0]<=zeros2[zeros2.length-1] && zeros2[0]<=zeros1[zeros1.length-1]){
					for(int j=0;j<zeros1.length;j++){
							r1 = search(zeros2,zeros1[j],f1) ;
							if(r1>=0){
								sum[0]+= 1 ;
								f1=r1+1;
								continue;
							}else{
								f1 = -(r1+1);
							}
							if(f1>=zeros2.length)break;
					}
				}
			}
			
		}
		return sum[0] / Math.sqrt( sum[1]*sum[2] ) ; 
	}
	
	private static final double product(GlobalKV cv1,GlobalKV cv2){
		double[] sum = new double[3] ;
		List<LocalKV> list1 = cv1.getLocals() ;
		List<LocalKV> list2 = cv2.getLocals() ;
		LocalKV local1 = null ;
		LocalKV local2 = null ;
		int[] keys1 = null;
		double[] values1 = null ;
		int[] keys2 = null;
		double[] values2 = null ;
		int f1 = 0 ;
		int r1 = 0 ;
		for(int i=0; i<list1.size(); i++){
			local1 = list1.get(i) ;
			local2 = list2.get(i) ;
			keys1 = local1.getKeys();
			values1 = local1.getValues();
			keys2 = local2.getKeys();
			values2 = local2.getValues();
			for(double e:values1){
				sum[1] += e*e;
			}
			for(double e:values2){
				sum[2] += e*e;
			}
			f1=0;
			if(keys1.length!=0&& keys2.length!=0 && keys1[0]<=keys2[keys2.length-1] && keys2[0]<=keys1[keys1.length-1]){
				for(int j=0;j<keys1.length;j++){
							//return the global index of the array
							r1 = search(keys2,keys1[j],f1) ;
							if(r1>=0){
								sum[0]+= values1[j]*values2[r1] ;
								f1 = r1+1;
								continue;
							}else{
								f1 = -(r1+1);
							}
							if(f1>=keys2.length)break;
				}
			}
			
		}
		return sum[0] / Math.sqrt( sum[1]*sum[2] ) ; 
	}
	
	private static final double product(double[] section1,double[] section2){
		double[] sum = new double[3] ;
		for(int i=0; i<section1.length; i++){
			sum[1] += section1[i]*section1[i];
			sum[2] += section2[i]*section2[i];
			sum[0] +=  section1[i]*section2[i] ;
		}
		return sum[0] / Math.sqrt( sum[1]*sum[2] ) ;
	}
	
	private static final double normalDistance(List<List<Cell>> section1,List<List<Cell>> section2){
		double cos = product( section1, section2) ;
		return (1 - cos ) / 2;
	}
	
	private static final double normalDistance(GlobalKV section1,GlobalKV section2){
		double cos = product( section1, section2) ;
		return (1 - cos ) / 2;
	}
	
	private static final double normalDistance(GlobalCV section1,GlobalCV section2){
		double cos = product( section1, section2) ;
		return (1 - cos ) / 2;
	}
	
	private static final double normalDistance(SingleCV section1,SingleCV section2){
		double cos = product( section1, section2) ;
		return (1 - cos ) / 2;
	}
	
	private static final double chordDistance(List<List<Cell>> section1,List<List<Cell>> section2){
		double cos = product( section1, section2) ;
		return Math.sqrt( ( 1 - cos )*2  ) ;
	}
	
	private static final double chordDistance(GlobalKV section1,GlobalKV section2){
		double cos = product( section1, section2) ;
		return Math.sqrt( ( 1 - cos )*2  ) ;
	}
	
	private static final double chordDistance(GlobalCV section1,GlobalCV section2){
		double cos = product( section1, section2) ;
		return Math.sqrt( ( 1 - cos )*2  ) ;
	}
	
	private static final double chordDistance(SingleCV section1,SingleCV section2){
		double cos = product( section1, section2) ;
		return Math.sqrt( ( 1 - cos )*2  ) ;
	}
	
	private static final double normalDistance(double[] section1,double[] section2){
		double cos = product( section1, section2) ;
		return (1 - cos ) / 2;
	}
	
	private static final double chordDistance(double[] section1,double[] section2){
		double cos = product( section1, section2) ;
		return Math.sqrt( (   1 -  cos  )*2 ) ;
	}
	
	public static final double distance(List<List<Cell>> section1,List<List<Cell>> section2,String method){
		return "0".equals(method)?chordDistance(section1,section2):normalDistance(section1,section2) ;
	}
	
	public static final double distance(double[] section1,double[] section2,String method){
		return "0".equals(method)?chordDistance(section1,section2):normalDistance(section1,section2) ;
	}
	
	public static final double distance(GlobalCV section1,GlobalCV section2,String method){
		return "0".equals(method)?chordDistance(section1,section2):normalDistance(section1,section2) ;
	}
	public static final double distance(GlobalKV section1,GlobalKV section2,String method){
		return "0".equals(method)?chordDistance(section1,section2):normalDistance(section1,section2) ;
	}
	public static final double distance(SingleCV section1,SingleCV section2,String method){
		return "0".equals(method)?chordDistance(section1,section2):normalDistance(section1,section2) ;
	}
}
