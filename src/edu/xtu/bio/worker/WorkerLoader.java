package edu.xtu.bio.worker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import edu.xtu.bio.comp.ScalableCompution;
import edu.xtu.bio.model.ResultLoader;

public class WorkerLoader implements Callable<ResultLoader>{
	/**
	 * @author WuQi@XTU
	 * @time_created 2016年4月5日,上午9:07:48
	 * @version 1.0
	 */
	private static final Logger Log = Logger.getLogger(WorkerLoader.class) ;
	private ScalableCompution sc ;
	private int next ;
	public WorkerLoader(ScalableCompution sc, int next) {
		super();
		this.sc = sc;
		this.next = next;
	}

	@Override
	public ResultLoader call() throws Exception {
		Object o = null ;
		ResultLoader	result = new ResultLoader(next);
		try {
			o = sc.getProtein(next);
			if(o==null){
				result.setStatus(false);
			}else{
				result.setO(o);
				result.setStatus(true);
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); 
            e.printStackTrace(new PrintWriter(sw, true));
			Log.error(sw);
			result.setStatus(false);
		}
		return result;
	}
}
