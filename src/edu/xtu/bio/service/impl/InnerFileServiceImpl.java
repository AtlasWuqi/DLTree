package edu.xtu.bio.service.impl;

import org.apache.log4j.Logger;

import edu.xtu.bio.service.intf.InnerFileService;
import edu.xtu.bio.utils.Configuration;

/**
 * @author WuQi@XTU
 * @time_created 2015年11月1日,上午11:33:47
 * @version 1.0
 */
public class InnerFileServiceImpl {

	private static final Logger Log = Logger.getLogger(InnerFileServiceImpl.class) ;
	private static int mode ;
	private static InnerFileService binary = InnerBinaryFileServiceImpl.getInstance();
	private static InnerFileService chara = InnerCharFileServiceImpl.getInstance();
	static{
		if(!Configuration.init(AGPConstant.PATH_CONF)){
			Log.fatal("init configuration fail,quit");
			System.exit(1);
		}
		mode = Integer.parseInt(Configuration.getProperty("switch.storebinary","1"));
	}

	public static InnerFileService getInstance(){
		return mode==1?binary:chara ;
	}

	/**
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
		return mode==0?this.loadCVByBinary(path):this.loadCVByChar(path);
	}

	@Override
	public boolean storeCV(String path, List<List<Cell>> cv) {
		return mode==0?this.storeCVByBinary(path, cv):this.storeCVByChar(path, cv);
	}
	
	@Override
	public double[] loadCVSingle(String path) {
		return mode==0?this.loadCVByBinarySingle(path):this.loadCVByCharSingle(path);
	}

	@Override
	public boolean storeCVSingle(String path, double[] cv) {
		return mode==0?this.storeCVByBinarySingle(path, cv):this.storeCVByCharSingle(path, cv);
	}

	@Override
	public GlobalCV loadGlobalCV(String path) {
		return mode==0?this.loadGlobalCVByBinary(path):this.loadGlobalCVByChar(path);
	}

	@Override
	public boolean storeGlobalCV(String path, GlobalCV gcv) {
		return mode==0?this.storeGlobalCVByBinary(path, gcv):this.storeGlobalCVByChar(path, gcv);
	}
	
	private List<List<Cell>> loadCVByChar(String path) {
		
		if(!new File(path).exists())return null ;
		BufferedReader in = null ;
		int cv_size = 0 ;
		int channel_size = 0 ;
		List<List<Cell>> cv = null ;
		List<Cell> channel = null ;
		try {
			try {
				in = new BufferedReader(new FileReader(path)) ;
				String line = in.readLine() ;
				if(StringUtils.isEmpty(line))return null ;
				cv_size = Integer.parseInt(line) ;
				cv = new ArrayList<List<Cell>>( cv_size ) ;
				String[] ss = null ;
				
				for(int i=0;i<cv_size;i++){
					line = in.readLine() ;
					if(StringUtils.isEmpty(line))return null ;
					channel_size = Integer.parseInt(line) ;
					channel = new ArrayList<Cell>( channel_size ) ;
					for(int j=0;j<channel_size;j++){
						line = in.readLine() ;
						if(StringUtils.isEmpty(line))return null ;
						ss = line.split(AGPConstant.CV_SEP) ;
						if(ss.length!=2)return null ;
						channel.add(new Cell(Integer.parseInt(ss[0]),Double.parseDouble(ss[1]))) ;
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

	private boolean storeCVByChar(String path, List<List<Cell>> cv) {
		FileUtil.check(path) ;
		if(cv==null)return false;
		PrintWriter out = null ;
		//int i = 0 ;
		//int index = 0 ;
		try {
			try {
				out = new PrintWriter( new BufferedWriter(new FileWriter(path))) ;
				out.println(cv.size()) ;
				
				for(List<Cell> cells:cv){
					//index = i*Protein.BASE5;
					out.println(cells.size()) ;
					for(Cell e:cells){
						out.print(e.getKey());
						out.print(AGPConstant.CV_SEP);
						out.println(e.getValue());
						//out.printf("%.15f\n", e.getValue());
					}
					//i++;
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

	private double[] loadCVByCharSingle(String path) {
		if(!new File(path).exists())return null ;
		BufferedReader in = null ;
		int cv_size = 0 ;
		double[] cv = null ;
		try {
			try {
				in = new BufferedReader(new FileReader(path)) ;
				String line = in.readLine() ;
				if(StringUtils.isEmpty(line))return null ;
				cv_size = Integer.parseInt(line) ;
				cv = new double [cv_size ] ;
				for(int i=0;i<cv_size;i++){
					line = in.readLine() ;
					if(StringUtils.isEmpty(line))return null ;
					cv[i] = Double.parseDouble(line) ;
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

	private boolean storeCVByCharSingle(String path, double[] cv) {
		FileUtil.check(path) ;
		if(cv==null)return false;
		PrintWriter out = null ;
		try {
			try {
				out = new PrintWriter( new BufferedWriter(new FileWriter(path))) ;
				out.println(cv.length) ;
				for(double e:cv){
					out.println(e);
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

	private GlobalCV loadGlobalCVByChar(String path) {
		if(!new File(path).exists())return null ;
		BufferedReader in = null ;
		int cv_size = 0 ;
		int nonzero_size = 0 ;
		int zero_size = 0 ;
		GlobalCV gcv = null ;
		
		int[] keys = null ;
		double[] values = null ;
		int[] zeros = null ;	
		
		try {
			try {
				in = new BufferedReader(new FileReader(path)) ;
				String line = in.readLine() ;
				if(StringUtils.isEmpty(line))return null ;
				String[] head = line.split(AGPConstant.CV_SEP);
				if(head.length!=3)return null ;
				cv_size = Integer.parseInt(head[0]) ;
				gcv = new GlobalCV(Integer.parseInt(head[1]),Integer.parseInt(head[2]),new ArrayList<LocalCV>( cv_size )) ;
				String[] ss = null ;
				
				for(int i=0;i<cv_size;i++){
					line = in.readLine() ;
					if(StringUtils.isEmpty(line))return null ;
					head = line.split(AGPConstant.CV_SEP);
					if(head.length!=2)return null ;	
					nonzero_size = Integer.parseInt(head[0]) ;
					zero_size = Integer.parseInt(head[1]) ;
					keys = new int[nonzero_size];
					values = new double[nonzero_size];
					zeros = new int[zero_size];
					for(int j=0;j<nonzero_size;j++){
						line = in.readLine() ;
						if(StringUtils.isEmpty(line))return null ;
						ss = line.split(AGPConstant.CV_SEP) ;
						if(ss.length!=2)return null ;
						keys[j]=Integer.parseInt(ss[0]) ;
						values[j]=Double.parseDouble(ss[1]);
					}
					for(int j=0;j<zero_size;j++){
						line = in.readLine() ;
						if(StringUtils.isEmpty(line))return null ;
						zeros[j]=Integer.parseInt(line) ;
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

	private boolean storeGlobalCVByChar(String path, GlobalCV gcv) {
		FileUtil.check(path) ;
		if(gcv==null)return false;
		PrintWriter out = null ;
		int[] keys = null ;
		double[] values = null ;
		int[] zeros = null ;
		try {
			try {
				out = new PrintWriter( new BufferedWriter(new FileWriter(path))) ;
				out.println(gcv.getLocals().size()+AGPConstant.CV_SEP+ gcv.getNonzero()+ AGPConstant.CV_SEP+gcv.getZero()) ;
				
				for(LocalCV lcv : gcv.getLocals()){
					//index = i*Protein.BASE5;
					keys = lcv.getKeys();
					values = lcv.getValues() ;
					zeros = lcv.getZeros() ;
					
					out.println(keys.length+AGPConstant.CV_SEP+zeros.length) ;
					
					for(int i=0;i<keys.length;i++){
						out.print(keys[i]);
						out.print(AGPConstant.CV_SEP);
						out.println(values[i]);
						//out.printf("%.15f\n", e.getValue());
					}
					for(int i=0;i<zeros.length;i++){
						out.println(zeros[i]);
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
				gcv = new GlobalCV(in.readInt(),in.readInt(),new ArrayList<LocalCV>( cv_size )) ;
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
				out.writeInt(gcv.getNonzero());
				out.writeInt(gcv.getZero());
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
	*/
}
