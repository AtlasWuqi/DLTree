package edu.xtu.bio.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import edu.xtu.bio.comparator.DescBySize;
import edu.xtu.bio.dna.DNAAbstract;
import edu.xtu.bio.dna.DNAOptimalMultiDisc;
import edu.xtu.bio.model.Report;
import edu.xtu.bio.model.ResultLoader;
import edu.xtu.bio.model.ResultNew;
import edu.xtu.bio.model.Species;
import edu.xtu.bio.parallel.InnerDBBuilder;
import edu.xtu.bio.service.impl.InnerFileServiceImpl;
import edu.xtu.bio.service.intf.InnerFileService;
import edu.xtu.bio.utils.FileUtil;
import edu.xtu.bio.worker.WorkerLoader;
import edu.xtu.bio.worker.WorkerParallelDNADisc;

public class SCMulDNA  implements ScalableCompution{
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月15日,上午10:01:39
	 * @version 1.0
	 */
	private static final Logger Log = Logger.getLogger(SCMulDNA.class) ;
	private static InnerFileService inner = InnerFileServiceImpl.getInstance() ;

	private List<Species> list ;
	private Report r ;
	private boolean[] ktuple ;
	private double[][] matrix ;
	private ExecutorService se ;
	
	//当前头指标
	private int next = 0 ;

	public SCMulDNA(double[][] matrix, List<Species> list,Report r,boolean[] ktuple,ExecutorService se) {
		super();
		this.matrix = matrix;
		this.list = list;
		this.r = r;
		this.ktuple = ktuple ;
		this.se = se ;
	}

	private String getCVpath(Species s){
		return InnerDBBuilder.cvDir + File.separator+r.getType() +File.separator+r.getMethod()+ File.separator+s.getFilename() +".cv" ;
	}
	
	private DNAOptimalMultiDisc computeCV(Species e,boolean[] ktuple){
	
		String in = InnerDBBuilder.genDir + File.separator +e.getFilename();
		String out = InnerDBBuilder.ktupleDir +File.separator + e.getFilename() ;
		DNAOptimalMultiDisc protein = new DNAOptimalMultiDisc(e.getFilename(),in,out,r.getMethod(),ktuple) ;
		FileUtil.mkdirs(out) ;
		
		if(!protein.start()){
			protein.freeAll();
			return null;
		}else{
			protein.free();
		}
		
		storeCV(protein,getCVpath(e),ktuple);
		return protein ;
	}
	
	private void storeCV(DNAOptimalMultiDisc protein,String path,boolean[] ktuple){
		String pathk = null ;
		if(ktuple[DNAAbstract.MAXLEN-1]){
			pathk = path+"."+(DNAAbstract.MAXLEN-1);
			String tmp = pathk+".tmp" ;
			inner.storeGlobalKV(tmp , protein.getCv7()) ;
			new File(tmp).renameTo(new File(pathk)) ;
		}
	}
	
	private DNAOptimalMultiDisc loadInnerProtein(Species s){
		DNAOptimalMultiDisc p = new DNAOptimalMultiDisc();
		p.setKtuple_out(ktuple);
		String path = getCVpath(s);
		boolean[] ktuple_redo = new boolean[DNAAbstract.MAXLEN] ;
		int fail = 0 ;
		//开始加载
		for(int i=2;i<DNAAbstract.MAXLEN-1;i++){
			if(this.ktuple[i]){
				ktuple_redo[i]=true;
				fail++;
			}
		}
		//开始加载
		if(this.ktuple[DNAAbstract.MAXLEN-1]){
			p.setCv7(inner.loadGlobalKV(path+"."+(DNAAbstract.MAXLEN-1)));
			if(p.getCv7()==null){
				ktuple_redo[DNAAbstract.MAXLEN-1]=true;
				fail++ ;
			}
		}
	
		if(fail!=0){
			//恢复加载失败部分
			DNAOptimalMultiDisc rp = this.computeCV(s, ktuple_redo);
			if(rp==null){
				Log.error("loadInnerProtein,recover the part of load failure");
				return null;
			}
			if(ktuple_redo[DNAAbstract.MAXLEN-1])p.setCv7(rp.getCv7());
			for(int i=2;i<DNAAbstract.MAXLEN-1;i++){
				if(ktuple_redo[i])p.cvx[i+1] = rp.cvx[i+1];
			}
		}
		
		return p;
	}
	
	private DNAOptimalMultiDisc loadInnerProtein7(Species s){
		
		DNAOptimalMultiDisc p = new DNAOptimalMultiDisc();
		p.setKtuple_out(ktuple);
		String path = getCVpath(s); 

		boolean[] ktuple_redo = new boolean[DNAAbstract.MAXLEN] ;
		int fail = 0 ;
		//开始加载
		if(this.ktuple[DNAAbstract.MAXLEN-1]){
			p.setCv7(inner.loadGlobalKV(path+"."+(DNAAbstract.MAXLEN-1)));
			if(p.getCv7()==null){
				ktuple_redo[DNAAbstract.MAXLEN-1]=true;
				fail++ ;
			}
		}
		if(fail!=0){
			//恢复加载失败部分
			DNAOptimalMultiDisc rp = this.computeCV(s, ktuple_redo);
			if(rp==null){
				Log.error("loadInnerProtein,recover the part of load failure");
				return null;
			}
			return rp;
		}
		return p;
	}
	
	private long getId(Species e){
		return e.getId() ;
	}
	
	private void preprocessMatrix(){
		for(int i=0;i<this.list.size()-1;i++){
			for(int j=i+1;j<this.list.size();j++){
				this.matrix[i][j] = -2;
			}
		}
	}
	
	public DNAOptimalMultiDisc getProtein(int index){
		return this.loadInnerProtein(list.get(index));
	}
	
	private int nextLoaded(int next,List<Integer> currentList){
		if(next>=list.size()-1)return list.size()-1;
		//从行列来看都不应加载
		while(!shouldBeLoadedRaw(next)&&!shouldBeLoadedColumn(next,currentList)){
			next++;
			if(next==list.size()-1)break;
		}
		return next ;
	}
	
	private void clearBlock(List<Integer> currentList,List<DNAOptimalMultiDisc> proteins){
		for(int i:currentList){
			proteins.set(i, null);
		}
		currentList.clear();
	}

	private String getCurrentBlock(List<Integer> currentList){
		StringBuilder sb = new StringBuilder();
		sb.append(currentList.size());
		sb.append("[");
		for(Integer e:currentList){
			sb.append(e);
			sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}
	
	private boolean submit(List<Future<ResultNew>> wait,List<DNAOptimalMultiDisc> proteins,DNAOptimalMultiDisc current_protein){
		if(wait.isEmpty())return true;
		WorkerParallelDNADisc work = null ;
		List<Future<ResultNew>> fail = new ArrayList<Future<ResultNew>>(wait.size());
		boolean flag = true ;
		int success = 0 ;
		for(int i=0;i<wait.size();i++){
			ResultNew result = null ;
				try {
					 result = wait.get(i).get() ;
						if(!result.isStatus()){
							Log.error(result);
							flag = false ;
						}else{
							success++ ;
							flag = true ;
						}
				}catch (Exception e) {
					Log.error(e.getMessage());
					flag = false ;
				}
			if(!flag){
				if(current_protein==null){
					work = new WorkerParallelDNADisc(proteins.get(result.getRaw()),proteins.get(result.getColumn()), matrix, r, result.getK(), result.getRaw(), result.getColumn(), result.getSrc(), result.getDst());
				}else{
					work = new WorkerParallelDNADisc(proteins.get(result.getRaw()),current_protein, matrix, r, result.getK(), result.getRaw(), result.getColumn(), result.getSrc(), result.getDst());
				}
				fail.add( se.submit(work) ) ;
			}
		}
		Log.info("end 1 round succ="+success+" total="+wait.size());
		if(success==wait.size())return true ;
		for(Future<ResultNew> f:fail){
			try {
				ResultNew result = f.get() ;
					if(!result.isStatus()){
						Log.error(result);
					}else{
						success++ ;
					}
			} catch (InterruptedException e) {
				Log.error(e.getMessage());
			} catch (ExecutionException e) {
				Log.error(e.getMessage());
			}
		}
		Log.info("end 2 round succ="+success+" total="+wait.size());
		return success==wait.size() ;
	}
	
	
	private boolean computeInnerBlock(List<Integer> currentList,List<DNAOptimalMultiDisc> proteins){
		Log.info("current block:"+getCurrentBlock(currentList));
		if(currentList.size()<2)return true;
		WorkerParallelDNADisc work = null ;
		List<Future<ResultNew>> wait = new ArrayList<Future<ResultNew>>();
		int raw = -1;
		int column = -1 ;
		for (int i = 0 ; i < currentList.size()-1; i++) {
			work = null ;
			wait.clear();
			Log.info("current raw:"+i);

			raw = currentList.get(i);
			long src = getId(this.list.get(raw));
			for (int j = i+1 ; j < currentList.size(); j++) {
				for (int k = 2; k < this.ktuple.length; k++) {
					if (this.ktuple[k]) {
						column = currentList.get(j);
						if (this.matrix[raw][column] < -1) {
							long dst = getId(list.get(column));
							work = new WorkerParallelDNADisc(proteins.get(raw),proteins.get(column) , this.matrix, r, k, raw, column, src, dst);
							wait.add( se.submit(work) ) ;
						}
						break;
					}
				}
			}
			if(!this.submit(wait, proteins,null))return false;
		}
		return true ;
	}
	
	private boolean computeOuterBlock(int next,List<Integer> currentList,List<DNAOptimalMultiDisc> proteins){
		if(next==list.size())return true ;
		WorkerParallelDNADisc work = null ;
		List<Future<ResultNew>> wait = new ArrayList<Future<ResultNew>>();
		DNAOptimalMultiDisc current_protein = null ;
		for (int column = next ; column < list.size(); column++) {
			if (!shouldBeLoadedColumn(column, currentList))continue;
			work = null ;
			wait.clear();
			Log.info("current Column:"+column);
			current_protein = getProtein(column);//当前加载值
			if (current_protein == null)return false;
			long src = getId(this.list.get(column));
			
			for (int raw : currentList) {
				for (int k = 2; k < this.ktuple.length; k++) {
					if (this.ktuple[k]) {
						if (this.matrix[raw][column] < -1) {
							long dst = getId(list.get(raw));
							work = new WorkerParallelDNADisc( proteins.get(raw),current_protein,matrix, r, k, raw, column, src, dst);
							wait.add( se.submit(work) ) ;
						}
						break;
					}
				}
			}
			if(!this.submit(wait, proteins,current_protein))return false;
		}
		return true ;
	}

	private long getFullVectorMemory(){
		boolean[] ktuple_in = new boolean[ktuple.length];
		if (this.r.getMethod().equals("0")) {
			ktuple_in[0] = true;
			for (int i = 2; i < ktuple.length; i++) {
				if (ktuple[i]) {
					ktuple_in[i] = true;
					ktuple_in[i - 1] = true;
				}
			}
		} else {
			for (int i = 2; i < ktuple.length; i++) {
				if (ktuple[i]) {
					ktuple_in[i] = true;
					ktuple_in[i - 1] = true;
					ktuple_in[i - 2] = true;
				}
			}
		}
		long total = 0;
		
		/**========== modified by WuQi , 2017-6-2
		 * 
		 **/
		for (int i = 0; i < ktuple.length-2; i++) {
			if (ktuple_in[i])
				total += (long)DNAAbstract.BASES[i+1] * 4L;
		}
		
		if (ktuple_in[ktuple.length-2]){
			int size = InnerDBBuilder.pool_max_size>4?4:InnerDBBuilder.pool_max_size;
			total += (long)DNAAbstract.BASES[ktuple.length-2] * 4L * (long)size;
		}
		if (ktuple_in[ktuple.length-1]){
			int size = InnerDBBuilder.pool_max_size>16?16:InnerDBBuilder.pool_max_size;
			total += (long)DNAAbstract.BASES[ktuple.length-2] * 4L * (long)size;
		}
		/**=====
		for (int i = 0; i < ktuple.length; i++) {
			if (ktuple_in[i])
				total += (long)DNAAbstract.BASES[i+1] * 4L;
		}
		===== */
		return total;
	}
	
	private boolean initList(){
	
		return this.initListSingle();
	}
	
	private boolean initListSingle(){
		Log.info("do single init");
		DNAOptimalMultiDisc p = null ;
		for(Species s:list){
			if(s.getCvsize()!=0)continue;
			p = this.loadInnerProtein7(s);
			if(p!=null){
				 if(p.getCv7()!=null){
					s.setCvsize(p.getCv7().getMemory());
				}else{
					Log.error("cv <6");
					return false;
				}
				Log.info(s.getId()+"  "+s.getCvsize());
			}else{
				Log.info(s.getId()+"  "+s.getCvsize());
				return false ;
			}
		}

		list.sort(DescBySize.getInstance());
		return true ;
	}
	
	private boolean submitLoader(List<Future<ResultLoader>> wait,List<DNAOptimalMultiDisc> proteins,List<Integer> currentList){
		try {
			ResultLoader result = null;
			for (Future<ResultLoader> r : wait) {
				result = r.get();
				if (result.isStatus()) {
					proteins.set(result.getIndex(), (DNAOptimalMultiDisc)result.getO());
					currentList.add(result.getIndex());
				} else {
					Log.info("loader fail "+result.getIndex());
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			Log.error(e.getMessage());
			return false;
		}
	}
	
	public boolean doloop() {

		if(this.ktuple[DNAAbstract.MAXLEN-1]){
			if(!this.initList())return false ;
		}
		preprocessMatrix();
		// 当前块 指标集
		List<Integer> currentList = new ArrayList<Integer>();
		// null表示 未加载或已卸载
		List<DNAOptimalMultiDisc> proteins = new ArrayList<DNAOptimalMultiDisc>(list.size());
		// 初始化
		for (int i = 0; i < list.size(); i++) {
			proteins.add(null);
		}
		return this.ktuple[DNAAbstract.MAXLEN-1]?this.doloopSingle(currentList, proteins):this.doloopMulti(currentList, proteins);
	}
	
	public boolean doloopSingle(List<Integer> currentList,List<DNAOptimalMultiDisc> proteins){
		//由next指标 的数据使用量 来确定 当前最大加载数
		long max = 0 ;
		int ret = -1 ;
		//进入迭代
		while(next<list.size()-1){
			this.clearBlock(currentList, proteins);//清空上一个块
			//next = nextLoaded(next,currentList);//确定下一个头指标
			if(next==list.size()-1){
				return true ;//到尾部，返回 成功
			}
			DNAOptimalMultiDisc protein =  getProtein(next) ;//获取头指标的加载值
			if(protein==null){
				return false;//获取失败，返回
			}else{
				proteins.set(next, protein);//设置对应 加载值
				currentList.add(next);//添加到 指标集
			}
			// 计算最大容量
			max = ((InnerDBBuilder.getTotalMemory()-this.getFullVectorMemory() )/proteins.get(next).totalMemory())-2 ;
			if(max<0){
				Log.error("the head protein is so big");
				return false;
			}
			
			ret = InnerDBBuilder.isMax?this.loaderSingleThread(proteins,currentList)
					:this.loaderSingleThread(max,proteins,currentList);
			//ret = this.loaderSingleThread(max, proteins,currentList);
			if(ret==-1){
				return false;
			}else if(ret==0){
				break;
			}
			next++;//下一次迭代 开始指标
			// 计算块中的距离（集合内），之后继续
			if(!this.computeInnerBlock(currentList, proteins))return false;
			// 计算块与指标的距离 （从 集合外 到 尾部）
			if(!this.computeOuterBlock(next, currentList, proteins))return false;
		}
	
		return true ;
	}
	
	public boolean doloopMulti(List<Integer> currentList,List<DNAOptimalMultiDisc> proteins){
		//由next指标 的数据使用量 来确定 当前最大加载数
		long max = 0 ;
		long max_parallel = 0 ;
		long max_next = 0 ;
		long ret = -1 ;
		List<Future<ResultLoader>> wait = new ArrayList<Future<ResultLoader>>();
		//进入迭代
		while(next<list.size()-1){
			this.clearBlock(currentList, proteins);//清空上一个块
			wait.clear();
			//next = nextLoaded(next,currentList);//确定下一个头指标
			if(next==list.size()-1){
				return true ;//到尾部，返回 成功
			}
			DNAOptimalMultiDisc protein = getProtein(next) ;//获取头指标的加载值
			if(protein==null){
				return false;//获取失败，返回
			}else{
				proteins.set(next, protein);//设置对应 加载值
				currentList.add(next);//添加到 指标集
			}
			max_next = proteins.get(next).totalMemory() ;
			// 计算最大容量
			max = ((InnerDBBuilder.getTotalMemory()-this.getFullVectorMemory() )/max_next ) - 2 ;
			//计算最大并发容量
			max_parallel = (InnerDBBuilder.getTotalMemory()- 2*max_next )/(max_next+this.getFullVectorMemory() ) ;
			if(max<0){
				Log.error("the head protein is so big");
				return false;
			}
			
			if(max_parallel>1){
				ret = loaderMultiThread(max, max_parallel, proteins, wait, currentList);
			}else{
				ret = InnerDBBuilder.isMax?this.loaderSingleThread(proteins,currentList)
						:this.loaderSingleThread(max,proteins,currentList);
			}
			
			//ret = max_parallel>1?loaderMultiThread(max, max_parallel, proteins, wait, currentList):loaderSingleThread(max, proteins,currentList);
			if(ret==-1){
				return false;
			}else if(ret==0){
				break;
			}
			next++;//下一次迭代 开始指标
			// 计算块中的距离（集合内），之后继续
			if(!this.computeInnerBlock(currentList, proteins))return false;
			// 计算块与指标的距离 （从 集合外 到 尾部）
			if(!this.computeOuterBlock(next, currentList, proteins))return false;
		}
	
		return true ;
	}
	
	public int loaderSingleThread(List<DNAOptimalMultiDisc> proteins,List<Integer> currentList){
		DNAOptimalMultiDisc protein = null ;
		long max_next = proteins.get(next).totalMemory() ;
		long total = InnerDBBuilder.getTotalMemory()-this.getFullVectorMemory();
		long free = total - max_next ;
		//剩余值 大于 最小值的2倍
		while(free > 2*max_next){
			next++;
			next = nextLoaded(next,currentList);
			if(next==list.size()-1){
				// 计算块中的距离（集合内）
				if(!this.computeInnerBlock(currentList, proteins))return -1;
				// 计算块与 指标的距离 （从 集合外 到 尾部） 之后退出
				if(!this.computeOuterBlock(next, currentList, proteins))return -1;
				return 0 ;
			}else{
				protein = getProtein(next) ;//获取头指标的加载值
				if(protein==null){
					return -1;
				}else{
					proteins.set(next, protein);
					currentList.add(next);
					max_next = proteins.get(next).totalMemory() ;
					free -= max_next ;
				}
			}
		}
		return 1;
	}
	
	public int loaderSingleThread(long max,List<DNAOptimalMultiDisc> proteins,List<Integer> currentList){
		DNAOptimalMultiDisc protein = null ;
		for(int i=0;i<max;i++){
			next++;
			next = nextLoaded(next,currentList);
			if(next==list.size()-1){
				// 计算块中的距离（集合内）
				if(!this.computeInnerBlock(currentList, proteins))return -1;
				// 计算块与 指标的距离 （从 集合外 到 尾部） 之后退出
				if(!this.computeOuterBlock(next, currentList, proteins))return -1;
				return 0 ;
			}else{
				protein = getProtein(next) ;//获取头指标的加载值
				if(protein==null){
					return -1;
				}else{
					proteins.set(next, protein);
					currentList.add(next);
				}
			}
		}
		return 1;
	}
	
	public int loaderMultiThread(long max,long max_parallel,List<DNAOptimalMultiDisc> proteins,
			List<Future<ResultLoader>> wait,List<Integer> currentList){
		for(int i=0;i<max;i++){
			if(wait.size()==max_parallel){
				if(!this.submitLoader(wait, proteins, currentList)){
					Log.error("parallel loader failure");
					return -1;
				}else{
					wait.clear();
				}
			}
			next++;
			next = nextLoaded(next,currentList);
			if(next==list.size()-1){
				if(!this.submitLoader(wait, proteins, currentList)){
					Log.error("parallel loader failure");
					return -1;
				}else{
					wait.clear();
				}
				// 计算块中的距离（集合内）
				if(!this.computeInnerBlock(currentList, proteins))return -1;
				// 计算块与 指标的距离 （从 集合外 到 尾部） 之后退出
				if(!this.computeOuterBlock(next, currentList, proteins))return -1;
				return 0 ;
			}else{
				wait.add( se.submit(new WorkerLoader(this,next)) ) ;
			}
		}
		if(!this.submitLoader(wait, proteins, currentList)){
			Log.error("parallel loader failure");
			return -1;
		}else{
			wait.clear();
		}
		return 1;
	}
	
	private boolean shouldBeLoadedRaw(int i){
		for(int j=i+1;j<this.list.size();j++){
			if(this.matrix[i][j]<-1)return true ;
		}
		return false ;
	}
	
	private boolean shouldBeLoadedColumn(int pos,List<Integer> currentList){
		for(int i:currentList){
			if(this.matrix[i][pos]<-1)return true ;
		}
		return false ;
	}
}
