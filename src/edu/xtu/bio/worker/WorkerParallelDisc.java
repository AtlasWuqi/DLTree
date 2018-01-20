package edu.xtu.bio.worker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import edu.xtu.bio.model.Report;
import edu.xtu.bio.model.ResultNew;
import edu.xtu.bio.pro.ProteinOptimalMultiDisc;
import edu.xtu.bio.utils.CollectionUtil;

/**
	 * @author WuQi@XTU
	 * @time_created 2016年3月23日,上午9:36:08
	 * @version 1.0
	 */

public class WorkerParallelDisc implements Callable<ResultNew>{

	private static final Logger Log = Logger.getLogger(WorkerParallelDisc.class) ;

	private ProteinOptimalMultiDisc proteinRaw;
	private ProteinOptimalMultiDisc proteinColumn ;
	private double[][] matrix ;
	private Report r;
	private int k ;
	private int raw ;
	private int column ;
	private long src ;
	private long dst ;
	
	public WorkerParallelDisc(ProteinOptimalMultiDisc proteinRaw,ProteinOptimalMultiDisc proteinColumn, double[][] matrix, Report r, int k, int raw, int column,long src,long dst) {
		
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
			switch (k) {
			case 2: {
				this.matrix[raw][column] = CollectionUtil.distance(this.proteinRaw.getCv3(),
						this.proteinColumn.getCv3(), r.getMethod());
				break;
			}
			case 3: {
				this.matrix[raw][column] = CollectionUtil.distance(this.proteinRaw.getCv4(),
						this.proteinColumn.getCv4(), r.getMethod());
				break;
			}
			case 4: {
				this.matrix[raw][column]= CollectionUtil.distance(this.proteinRaw.getCv5(),
						this.proteinColumn.getCv5(), r.getMethod());
				break;
			}
			case 5: {
				this.matrix[raw][column] = CollectionUtil.distance(this.proteinRaw.getCv6(),
						this.proteinColumn.getCv6(), r.getMethod());
				break;
			}
			case 6: {
				this.matrix[raw][column] = CollectionUtil.distance(this.proteinRaw.getCv7(),
						this.proteinColumn.getCv7(), r.getMethod());
				break;
			}
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

