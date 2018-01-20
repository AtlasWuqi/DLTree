package edu.xtu.bio.parallel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import edu.xtu.bio.comp.SCMul;
import edu.xtu.bio.comp.SCMulDNA;
import edu.xtu.bio.comp.ScalableCompution;
import edu.xtu.bio.dna.DNAAbstract;
import edu.xtu.bio.filefilter.EndWithFilter;
import edu.xtu.bio.model.Report;
import edu.xtu.bio.model.Species;
import edu.xtu.bio.pro.ProteinAbstract;
import edu.xtu.bio.service.impl.AGPConstant;
import edu.xtu.bio.utils.Configuration;
import edu.xtu.bio.utils.FileUtil;
import edu.xtu.bio.utils.VisualityUtil;
import edu.xtu.bio.utils.MD5Util;

/**
 * @author WuQi@XTU
 * @time_created 2016年3月18日,上午10:38:49
 * @version 1.0
 */
	
	public class InnerDBBuilder {
	
	private static final Logger logger = Logger.getLogger(InnerDBBuilder.class) ;

	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
	public static ExecutorService se ;
	public static int pool_max_size ;
	private static long totalMemory ;
	public static int compressLevel ;
	public static final int FROM = 2 ;
	public static boolean isMultiCompare ;
	public static boolean isMultiLoad ;
	public static boolean isMultiInit ;
	public static boolean isMax ;
	public static boolean isFilter ;
	public static String genDir ;
	public static String dataDir ;
	public static String cvDir ;
	public static String distanceDir  ;
	public static String treeDir  ;
	public static String ktupleDir ;
	public static String ktuples;
	
	public static String type  ;
	public static String method  ;
	public static int size ;

	public static long getTotalMemory(){
		//two distance matrixes and one list of species, results and workers
		return totalMemory - size*size*8*2 - size*8*(289+6+9);
	}
	
	private static boolean initThreadPool(){
		try {
			pool_max_size = Integer.parseInt(Configuration.getProperty("threadpool.max.size","2")) ;
			se = Executors.newFixedThreadPool(pool_max_size);
			
			type = Configuration.getProperty("type","0") ;
			method = Configuration.getProperty("method","0") ;
			
			StringBuffer sb = new StringBuffer() ;
			if("0".equals(type)){
				ktuples = Configuration.getProperty("ktuple","0000111") ;
				if(ktuples==null||ktuples.length()<ProteinAbstract.MAXLEN){
					logger.error("ktuple is null or <"+ProteinAbstract.MAXLEN);
					return false;
				}else{
					for(int i=0;i<ProteinAbstract.MAXLEN;i++){
						if(i<2){
							sb.append('0');
						}else{
							sb.append(ktuples.charAt(i));
						}
					}
				}
			}else{
				ktuples = Configuration.getProperty("ktuple.dna","000000000010101") ;
				if(ktuples==null||ktuples.length()<DNAAbstract.MAXLEN){
					logger.error("ktuple is null or <"+DNAAbstract.MAXLEN);
					return false;
				}else{
					for(int i=0;i<DNAAbstract.MAXLEN;i++){
						if(i<6||i==DNAAbstract.MAXLEN-2){
							sb.append('0');
						}else{
							sb.append(ktuples.charAt(i));
						}
					}
				}
			}
			ktuples = sb.toString();
			return true ;	
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			return false ;
		}
	}
	
	public static void order(List<Species> species,double[][] matrixSRC,double[][] matrixDST){
		int raw = 0 ;
		int column = 0 ;
		for(int i=0;i<species.size()-1;i++){
			raw = species.get(i).getId()-1;
			for(int j=i+1;j<species.size();j++){
				column = species.get(j).getId()-1;
				if(raw<column){
					matrixDST[raw][column] = matrixSRC[i][j];
				}else{
					matrixDST[column][raw] = matrixSRC[i][j];
				}
			}
		}
	}
	
	public static void exit(int status){
		se.shutdownNow();
		System.exit(status);
	}

	
	public static List<File> filter(File[] cvs){
		ArrayList<String> md5array = new ArrayList<String>(cvs.length);
		ArrayList<Integer> indexarray = new ArrayList<Integer>(cvs.length);
		ArrayList<File> filter = new ArrayList<File>(cvs.length);
		for(int i=0;i<cvs.length;i++){
			
		}
		for(int i=0;i<cvs.length;i++){
			String md5 = MD5Util.filemd5OnlyAlpha( cvs[i].getAbsolutePath() ) ;
			md5array.add( md5 );
			boolean flag = true;
			for(int j=0;j<indexarray.size();j++){
				if( md5.equals(  md5array.get(indexarray.get(j))) ){
					flag=false;
					break;
				}
			}
			if(flag){
				indexarray.add(i);
				filter.add(cvs[i]);
			}
		}
		return filter;
	}

	public static void main(String[] args) {
		logger.info("[start]"+new Date());
		long stime = System.currentTimeMillis();
		if(!Configuration.init(AGPConstant.PATH_CONF)){
			logger.error("init conf fail");
			return ;
		}
		
		if(!initThreadPool()){
			logger.error("init thread pool fail");
			return ;
		}
		int len = "0".equals(type)?ProteinAbstract.MAXLEN:DNAAbstract.MAXLEN;
		
		int ktotal=0;
		for(int i=0;i<len;i++){
			if(ktuples.charAt(i)=='1')ktotal++;
		}
		boolean[][] ktuple = new boolean[ktotal][len] ;
		
		int kcount = 0 ;
		for(int i=0;i<len;i++){
			if(ktuples.charAt(i)=='1'){
				ktuple[kcount][i]= true;
				kcount++ ;
			}
		}
		
		genDir = Configuration.getProperty("genDir") ;	
		dataDir = Configuration.getProperty("dataDir","data") ;
		FileUtil.mkdirs(dataDir) ;
		dataDir = new File(dataDir).getAbsolutePath();
		
		distanceDir = dataDir+File.separator+"distance" ;
		ktupleDir = dataDir+File.separator+"ktuple" ;
		treeDir= dataDir + File.separator+"tree" ;
		cvDir= dataDir + File.separator+"cv" ;
		
		FileUtil.mkdirs(treeDir) ;
		FileUtil.mkdirs(cvDir) ;
		FileUtil.mkdirs(distanceDir) ;
		FileUtil.mkdirs(ktupleDir) ;
		FileUtil.mkdirs(cvDir + File.separator+type +File.separator+method+ File.separator) ;
		
		logger.info("maximum memory "+Runtime.getRuntime().maxMemory());
		logger.info("total memory   "+Runtime.getRuntime().totalMemory());
		logger.info("unused memory  "+Runtime.getRuntime().freeMemory());
		
		double ratio = Double.parseDouble(Configuration.getProperty("total.ratio","0.95")) ;
		totalMemory =  (long)(Runtime.getRuntime().totalMemory() * ratio) ;
		
		logger.info("avail memory   "+totalMemory);
		logger.info("total.ratio="+ratio);
		logger.info("ktuple="+ktuples);
	
		isMultiCompare = "1".equals(Configuration.getProperty("switch.multicompare","1"));
		isMultiLoad = "1".equals(Configuration.getProperty("switch.multiload","1"));
		isMultiInit = "1".equals(Configuration.getProperty("switch.multiinit","1"));
		isMax = "1".equals(Configuration.getProperty("switch.max","1"));
		isFilter = "1".equals(Configuration.getProperty("filter","1"));
		List<Species> species = null ;
		File fgen = new File(genDir);
		if(!fgen.exists()){
			logger.fatal("not found dataDir ["+genDir+"],quit");
			exit(1);
		}
		String ftype = "0".equals(type)?"faa":"ffn";
		File[] cvs = fgen.listFiles(new EndWithFilter(ftype)) ;
		List<File> filter = isFilter?filter(cvs):Arrays.asList(cvs);
		if(filter.size()<3){
			logger.warn("the number of species is less than 3,quit!");
			exit(1);
		}
		species = new ArrayList<Species>(filter.size());
		for(int i=0;i<filter.size();i++){
			species.add(new Species(i+1,filter.get(i).getName(),0));
		}
		
		List<String> label = new ArrayList<String>(species.size()) ;
		size = species.size() ;
		double[][] matrix = new double[size][size] ;
		double[][] matrixOrder = new double[size][size];
		Report r = new Report();
		r.setMethod(method);
		r.setType(type);
		ScalableCompution s = null ;
		
		// TODO
		for (int k = 0; k < ktotal; k++) {
			label.clear();

			if ("0".equals(type)) {
				s = new SCMul(matrix, species, r, ktuple[k], se);
			} else {
				s = new SCMulDNA(matrix, species, r, ktuple[k], se);
			}

			if (s.doloop()) {
				logger.info("scalable compution succ");
			} else {
				logger.error("scalable compution fail");
				exit(1);
			}
			order(species, matrix, matrixOrder);

			for (Species e : species) {
				label.add(e.getFilename());
			}

			for (int i = 0; i < len; i++) {
				if (ktuple[k][i]) {
					String name = fgen.getName() + "-" + (i + 1);
					if (!post(name, label, matrix, matrixOrder)) {
						logger.error("output k" + (i + 1) + " fail,quit!!!");
						exit(1);
					} else {
						logger.info("k" + (i + 1) + " end");
					}
				}
			}

		}
		///
		se.shutdownNow();
		long etime = System.currentTimeMillis();
		logger.info("[end]"+new Date());
		logger.info("cost time:"+(etime-stime)+
				":type:"+type+
				":method:"+method+
				":ktuple:"+ktuples+
				":totalMemory:"+totalMemory);
	}
	
	public static boolean post(String path,List<String> label,double[][] matrix,double[][] matrixDST){
		//VisualityUtil.printMatrix(matrix);
		//VisualityUtil.printMatrix(matrixDST);
		//VisualityUtil.saveMatrixbyRow(label,matrix,path+".r.dist");
		//VisualityUtil.saveMatrixbyColumn(label,matrix,path+".c.dist");
		VisualityUtil.saveMatrixbyMEGE(label,matrix,distanceDir+File.separator+ path+AGPConstant.ITOL_MEGA);
		//VisualityUtil.saveMatrixbyPHYLIP(label,matrix,distanceDir+File.separator+path+AGPConstant.ITOL_PHYLIP);
		return cluster(path);
	}
	
	private static boolean cluster(String path){
		//megacc.exe -a M6CC.mao -d test\Hum_Dist_simp.meg -o test\hum_simp.nwk -n
		try {
			boolean os = System.getProperty("os.name").toLowerCase().startsWith("win") ;
			String exe = os?"megacc.exe":"megacc";
			String nwk =treeDir+File.separator+ path + AGPConstant.ITOL_NWK ;
			File fnwk = new File(nwk);
			if(fnwk.exists()){
				fnwk.delete();
			}
			String mega = System.getProperty("user.dir")+File.separator+"mega" + File.separator;
			Process process = Runtime.getRuntime().exec(mega+ exe +
					" -a " + mega+"M6CC.mao" +
					" -d " + distanceDir+File.separator+path + AGPConstant.ITOL_MEGA +
					" -n -o " + nwk) ;
			 if( process.waitFor()==0){
				 return true;
			 }else{
				 logger.error("generate the tree fail");
				 return false;
			 }
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("generate the tree fail");
			return false ;
		}
	}
}

