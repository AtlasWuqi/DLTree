package edu.xtu.bio.service.intf;

import java.util.List;

import edu.xtu.bio.model.Cell;
import edu.xtu.bio.model.GlobalCV;
import edu.xtu.bio.model.GlobalKV;
import edu.xtu.bio.model.SingleCV;

public interface InnerFileService {
	
	
	double[][] loadDistance(String path) ;
	boolean storeDistance(String path,double[][] matrix) ;
	
	List<List<Cell>>  loadCV(String path);
	boolean storeCV(String path,List<List<Cell>> cv) ;
	
	double[]  loadCVSingle(String path);
	boolean storeCVSingle(String path,double[] cv) ;
	
	GlobalCV  loadGlobalCV(String path);
	boolean storeGlobalCV(String path,GlobalCV gcv) ;
	
	GlobalKV  loadGlobalKV(String path);
	boolean storeGlobalKV(String path,GlobalKV gcv) ;
	
	SingleCV  loadSingleCV(String path);
	boolean storeSingleCV(String path,SingleCV gcv) ;
	
	/*
	List<List<Cell>>  loadCVByBinary(String path);
	boolean storeCVByBinary(String path,List<List<Cell>> cv) ;
	List<List<Cell>>  loadCVByChar(String path);
	boolean storeCVByChar(String path,List<List<Cell>> cv) ;
	
	
	
	double[]  loadCVByCharSingle(String path);
	boolean storeCVByCharSingle(String path,double[] cv) ;
	double[]  loadCVByBinarySingle(String path);
	boolean storeCVByBinarySingle(String path,double[] cv) ;
	
	
	
	GlobalCV  loadGlobalCVByChar(String path);
	boolean storeGlobalCVByChar(String path,GlobalCV gcv) ;
	GlobalCV  loadGlobalCVByBinary(String path);
	boolean storeGlobalCVByBinary(String path,GlobalCV gcv) ;
	*/
}
