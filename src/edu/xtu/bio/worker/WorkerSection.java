package edu.xtu.bio.worker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import edu.xtu.bio.model.ResultSection;
import edu.xtu.bio.service.intf.Genome;

/**
	 * @author WuQi@XTU
	 * @time_created 2016年11月1日,下午7:30:32
	 * @version 1.0
	 */
public class WorkerSection implements Callable<ResultSection>{

	private static final Logger Log = Logger.getLogger(WorkerSection.class) ;

	private Genome genome;
	private String section;
	private String path;
	private int index;
	
	public WorkerSection(Genome genome, String section, String path, int index) {
		super();
		this.genome = genome;
		this.section = section;
		this.path = path;
		this.index = index;
	}

	@Override
	public ResultSection call() throws Exception {
		ResultSection	result = new ResultSection(section,path,index,false);
		try {
			result.setStatus(genome.computeSection(section,path,index));
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); 
            e.printStackTrace(new PrintWriter(sw, true));
			Log.error(sw);
			result.setStatus(false);
		}
		return result;
	}
}


