package edu.xtu.bio.worker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import edu.xtu.bio.dna.DNAAbstract;
import edu.xtu.bio.dna.DNAOptimalMultiDisc;
import edu.xtu.bio.model.Report;
import edu.xtu.bio.model.ResultNew;
import edu.xtu.bio.utils.CollectionUtil;

/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月23日,上午9:36:08
	 * @version 1.0
	 */

public class WorkerParallelDNADisc implements Callable<ResultNew>{

	private static final Logger Log = Logger.getLogger(WorkerParallelDNADisc.class) ;

	private DNAOptimalMultiDisc proteinRaw;
	private DNAOptimalMultiDisc proteinColumn ;
	private double[][] matrix ;
	private Report r;
	private int k ;
	private int raw ;
	private int column ;
	private long src ;
	private long dst ;
	
	public WorkerParallelDNADisc(DNAOptimalMultiDisc proteinRaw,DNAOptimalMultiDisc proteinColumn, double[][] matrix, Report r, int k, int raw, int column,long src,long dst) {
		
		this.proteinRaw =  proteinRaw;
		this.proteinColumn = proteinColumn ;
		this.matrix = matrix;
		this.r = r;
		this.k = k;
		this.raw = raw;
		this.column = column;
		this.src = src ;
		this.dst = dst ;
	}

	@Override
	public ResultNew call() throws Exception {
		ResultNew	result = new ResultNew(k,raw,column,src,dst,false);
		try {
			
			if(k<DNAAbstract.MAXLEN-1){
				this.matrix[raw][column] = CollectionUtil.distance(this.proteinRaw.cvx[k+1],
					this.proteinColumn.cvx[k+1], r.getMethod());
			}else{
				this.matrix[raw][column] = CollectionUtil.distance(this.proteinRaw.getCv7(),
						this.proteinColumn.getCv7(), r.getMethod());
			}
		
			result.setStatus(true);
		} catch (Exception e) {
			StringWriter sw = new StringWriter(); 
            e.printStackTrace(new PrintWriter(sw, true));
			Log.error(sw);
			result.setStatus(false);
		}
		return result;
	}
}

