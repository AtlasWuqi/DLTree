package edu.xtu.bio.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.xtu.bio.model.Cell;
import edu.xtu.bio.model.GlobalCV;
import edu.xtu.bio.model.GlobalKV;
import edu.xtu.bio.model.LocalCV;
import edu.xtu.bio.model.LocalKV;
import edu.xtu.bio.model.SingleCV;
import edu.xtu.bio.service.intf.InnerFileService;
import edu.xtu.bio.utils.FileUtil;

public class InnerBinaryFileServiceImpl implements InnerFileService{
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月20日,下午10:17:12
	 * @version 1.0
	 */
	
	private static final Logger Log = Logger.getLogger(InnerBinaryFileServiceImpl.class) ;
	private static InnerFileService instance =new InnerBinaryFileServiceImpl() ;
	
	private InnerBinaryFileServiceImpl() {
		super();
	}

	public static InnerFileService getInstance(){
		return instance ;
	}
	
	@Override
	public double[][] loadDistance(String path) {
		if(!new File(path).exists())return null ;
		double[][] matrix = null ;
		DataInputStream in = null ;
		int size = 0 ;
		try {
			try {
				in = new DataInputStream(new BufferedInputStream( new FileInputStream(path)) );
				size = in.readInt() ;
				matrix = new double[size][size] ;
				
				for(int i=0;i<size-1;i++){
					for(int j=i+1;j<size;j++){
						matrix[i][j] = in.readDouble() ;
						matrix[j][i] = matrix[i][j] ;
					}
				}
				return matrix ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("load inner distance "+path);
			return null ;
		}
	}
	
	@Override
	public boolean storeDistance(String path,double[][] matrix) {
		FileUtil.check(path) ;
		if(matrix==null)return false;
		DataOutputStream in = null ;
		try {
			try {
				in = new DataOutputStream( new BufferedOutputStream(new FileOutputStream(path))) ;
				int size = matrix.length ;
				in.writeInt(size) ;
				for(int i=0;i<size-1;i++){
					for(int j=i+1;j<size;j++){
						in.writeDouble(matrix[i][j]) ;
					}
				}
				return true ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("store inner distance "+path);
			return false ;
		}
	}
	
	@Override
	public List<List<Cell>> loadCV(String path) {
		return this.loadCVByBinary(path);
	}

	@Override
	public boolean storeCV(String path, List<List<Cell>> cv) {
		return this.storeCVByBinary(path, cv);
	}
	
	@Override
	public double[] loadCVSingle(String path) {
		return this.loadCVByBinarySingle(path);
	}

	@Override
	public boolean storeCVSingle(String path, double[] cv) {
		return this.storeCVByBinarySingle(path, cv);
	}

	@Override
	public GlobalCV loadGlobalCV(String path) {
		return this.loadGlobalCVByBinary(path);
	}

	@Override
	public boolean storeGlobalCV(String path, GlobalCV gcv) {
		return this.storeGlobalCVByBinary(path, gcv);
	}

	private GlobalCV loadGlobalCVByBinary(String path) {
		if(!new File(path).exists())return null ;
		DataInputStream in = null ;
		int cv_size = 0 ;
		int nonzero_size = 0 ;
		int zero_size = 0 ;
		GlobalCV gcv = null ;
		
		int[] keys = null ;
		double[] values = null ;
		int[] zeros = null ;
		try {
			try {
				in = new DataInputStream( new BufferedInputStream( new FileInputStream(path) ) ) ;
				cv_size = in.readInt();
				gcv = new GlobalCV(in.readLong(),in.readLong(),new ArrayList<LocalCV>( cv_size )) ;
				for(int i=0;i<cv_size;i++){
					nonzero_size = in.readInt();
					zero_size = in.readInt();
					keys = new int[nonzero_size];
					values = new double[nonzero_size];
					zeros = new int[zero_size];
					for(int j=0;j<nonzero_size;j++){
						keys[j]=in.readInt() ;
						values[j]=in.readDouble() ;
					}
					for(int j=0;j<zero_size;j++){
						zeros[j]=in.readInt() ;
					}
					gcv.getLocals().add(new LocalCV(keys,values,zeros));
				}
				return gcv ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("load cv "+path);
			return null ;
		}
	}

	private boolean storeGlobalCVByBinary(String path, GlobalCV gcv) {
		FileUtil.check(path) ;
		if(gcv==null)return false;
		DataOutputStream out = null ;
		int[] keys = null ;
		double[] values = null ;
		int[] zeros = null ;
		try {
			try {
				out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(path) ) ) ;
				out.writeInt(gcv.getLocals().size());
				out.writeLong(gcv.getNonzero());
				out.writeLong(gcv.getZero());
				for(LocalCV lcv : gcv.getLocals()){
					keys = lcv.getKeys();
					values = lcv.getValues() ;
					zeros = lcv.getZeros() ;
					out.writeInt(keys.length);
					out.writeInt(zeros.length);
					for(int i=0;i<keys.length;i++){
						out.writeInt(keys[i]);
						out.writeDouble(values[i]);
					}
					for(int i=0;i<zeros.length;i++){
						out.writeInt(zeros[i]);
					}
				}
				out.flush();
				return true ;
			}finally{
				if(out!=null)out.close();
			}
		} catch (IOException e) {
			Log.error("store cv "+path);
			return false ;
		}
	}


	@Override
	public GlobalKV loadGlobalKV(String path) {
		return this.loadGlobalKVByBinary(path);
	}

	@Override
	public boolean storeGlobalKV(String path, GlobalKV gcv) {
		return this.storeGlobalKVByBinary(path, gcv);
	}

	private GlobalKV loadGlobalKVByBinary(String path) {
		if(!new File(path).exists())return null ;
		DataInputStream in = null ;
		int cv_size = 0 ;
		int nonzero_size = 0 ;
		GlobalKV gcv = null ;
		int[] keys = null ;
		double[] values = null ;
		try {
			try {
				in = new DataInputStream( new BufferedInputStream( new FileInputStream(path) ) ) ;
				cv_size = in.readInt();
				gcv = new GlobalKV(in.readLong(),new ArrayList<LocalKV>( cv_size )) ;
				for(int i=0;i<cv_size;i++){
					nonzero_size = in.readInt();
					keys = new int[nonzero_size];
					values = new double[nonzero_size];
					for(int j=0;j<nonzero_size;j++){
						keys[j]=in.readInt() ;
						values[j]=in.readDouble() ;
					}
					gcv.getLocals().add(new LocalKV(keys,values));
				}
				return gcv ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("load cv "+path);
			return null ;
		}
	}

	private boolean storeGlobalKVByBinary(String path, GlobalKV gcv) {
		FileUtil.check(path) ;
		if(gcv==null)return false;
		DataOutputStream out = null ;
		int[] keys = null ;
		double[] values = null ;
		try {
			try {
				out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(path) ) ) ;
				out.writeInt(gcv.getLocals().size());
				out.writeLong(gcv.getNonzero());
				for(LocalKV lcv : gcv.getLocals()){
					keys = lcv.getKeys();
					values = lcv.getValues() ;
					out.writeInt(keys.length);
					for(int i=0;i<keys.length;i++){
						out.writeInt(keys[i]);
						out.writeDouble(values[i]);
					}
				}
				out.flush();
				return true ;
			}finally{
				if(out!=null)out.close();
			}
		} catch (IOException e) {
			Log.error("store cv "+path);
			return false ;
		}
	}
	
	private double[] loadCVByBinarySingle(String path) {
		if(!new File(path).exists())return null ;
		DataInputStream in = null ;
		int cv_size = 0 ;
		double[] cv = null ;
		try {
			try {
				in = new DataInputStream( new BufferedInputStream( new FileInputStream(path) ) ) ;
				
				cv_size = in.readInt();
				cv = new double [cv_size ] ;
				for(int i=0;i<cv_size;i++){
					cv[i] = in.readDouble();
				}
				return cv ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("load cv "+path);
			return null ;
		}
	}

	
	private boolean storeCVByBinarySingle(String path, double[] cv) {
		FileUtil.check(path) ;
		if(cv==null)return false;
		DataOutputStream out = null ;
		try {
			try {
				out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(path) ) ) ;
				out.writeInt(cv.length) ;
				for(double e:cv){
					out.writeDouble(e);
				}
				out.flush();
				return true ;
			}finally{
				if(out!=null)out.close();
			}
		} catch (IOException e) {
			Log.error("store cv "+path);
			return false ;
		}
	}

	
	private List<List<Cell>> loadCVByBinary(String path) {
		if(!new File(path).exists())return null ;
		DataInputStream in = null ;
		int cv_size = 0 ;
		int channel_size = 0 ;
		List<List<Cell>> cv = null ;
		List<Cell> channel = null ;
		try {
			try {
				in = new DataInputStream(new BufferedInputStream( new FileInputStream(path)) );
				cv_size = in.readInt() ;
				cv = new ArrayList<List<Cell>>( cv_size ) ;
				for(int i=0;i<cv_size;i++){
					channel_size = in.readInt() ;
					channel = new ArrayList<Cell>( channel_size ) ;
					for(int j=0;j<channel_size;j++){
						channel.add(new Cell(in.readInt(),in.readDouble())) ;
					}
					cv.add(channel) ;
				}
				
				return cv ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("load cv "+path);
			return null ;
		}
	}

	private boolean storeCVByBinary(String path, List<List<Cell>> cv) {
		FileUtil.check(path) ;
		if(cv==null)return false;
		DataOutputStream in = null ;
		try {
			try {
				in = new DataOutputStream( new BufferedOutputStream(new FileOutputStream(path))) ;
				in.writeInt(cv.size()) ;
				for(List<Cell> cells:cv){
					in.writeInt(cells.size());
					for(Cell e:cells){
						in.writeInt(e.getKey());
						in.writeDouble(e.getValue()) ;
					}
				}
				return true ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("store cv "+path);
			return false ;
		}
	}

	@Override
	public SingleCV loadSingleCV(String path) {
		if(!new File(path).exists())return null ;
		DataInputStream in = null ;
		try {
			try {
				in = new DataInputStream( new BufferedInputStream( new FileInputStream(path) ) ) ;
				int nonzero_size = in.readInt();
				int[] keys = new int[nonzero_size];
				double[] values = new double[nonzero_size];
				for(int j=0;j<nonzero_size;j++){
					keys[j]=in.readInt() ;
					values[j]=in.readDouble() ;
				}
				return new SingleCV(keys,values) ;
			}finally{
				if(in!=null)in.close();
			}
		} catch (IOException e) {
			Log.error("load cv "+path);
			return null ;
		}
	}

	@Override
	public boolean storeSingleCV(String path, SingleCV gcv) {
		FileUtil.check(path) ;
		if(gcv==null)return false;
		DataOutputStream out = null ;
		int[] keys = gcv.getKeys() ;
		double[] values = gcv.getValues() ;
		try {
			try {
				out = new DataOutputStream( new BufferedOutputStream( new FileOutputStream(path) ) ) ;
				out.writeInt(keys.length);
				for(int i=0;i<keys.length;i++){
					out.writeInt(keys[i]);
					out.writeDouble(values[i]);
				}
				out.flush();
				return true ;
			}finally{
				if(out!=null)out.close();
			}
		} catch (IOException e) {
			Log.error("store cv "+path);
			return false ;
		}
	}
}
