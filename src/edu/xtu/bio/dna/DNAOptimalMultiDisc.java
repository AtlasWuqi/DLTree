package edu.xtu.bio.dna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.xtu.bio.model.GlobalKV;
import edu.xtu.bio.model.LocalKV;
import edu.xtu.bio.model.ResultSection;
import edu.xtu.bio.parallel.InnerDBBuilder;
import edu.xtu.bio.utils.FileUtil;
import edu.xtu.bio.utils.SectionNew;
import edu.xtu.bio.worker.WorkerSection;

public class DNAOptimalMultiDisc extends DNAAbstract {

	private Map<String, BufferedWriter> writerk7map = null;
	private Map<String, BufferedWriter> writerk6map = null;

	private List<SectionNew> k6 = null;
	
	private GlobalKV cv6 = null;
	private GlobalKV cv7 = null;


	public DNAOptimalMultiDisc() {
		super();
	}

	public DNAOptimalMultiDisc(String genome_name, String input, String output, String method, boolean[] ktuple) {
		super(genome_name, input, output, method, ktuple);
	}

	public GlobalKV getCv6() {
		return cv6;
	}

	public GlobalKV getCv7() {
		return cv7;
	}

	public void setCv6(GlobalKV cv6) {
		this.cv6 = cv6;
	}

	public void setCv7(GlobalKV cv7) {
		this.cv7 = cv7;
	}

	private boolean check() {
		File in = new File(this.input);
		if (!in.exists()) {
			logger.error("not found file:" + this.input);
			return false;
		}

		File out = new File(this.output);
		File k6 = new File(output + File.separatorChar + "k6");
		File k7 = new File(output + File.separatorChar + "k7");

		if (!out.exists()) {
			if (!out.mkdirs()) {
				logger.error("create directory:" + out.getAbsolutePath());
				return false;
			}
		}
		if (this.ktuple_in[MAXLEN-2]) {
			if (!k6.exists()) {
				if (!k6.mkdirs()) {
					logger.error("create directory:" + k6.getAbsolutePath());
					return false;
				}
			}
		}
		if (this.ktuple_in[MAXLEN-1]) {
			if (!k7.exists()) {
				if (!k7.mkdirs()) {
					logger.error("create directory:" + k7.getAbsolutePath());
					return false;
				}
			}
		}
		return true;
	}

	private boolean init() {

		for(int i=1;i<kx.length-2;i++){
			if (this.ktuple_in[i-1]){
				kx[i] = new int[BASES[i]];
			}
		}

		sections = new ArrayList<Integer>();
		nks = new ArrayList<Long>();

		if (this.ktuple_in[MAXLEN-2]) {
			if (!initWriter6())
				return false;
		}
		if (this.ktuple_in[MAXLEN-1]) {
			if (!initWriter7())
				return false;
		}
		return true;
	}

	private boolean initWriter6() {
		this.writerk6map = new HashMap<String, BufferedWriter>();
		String index = null;
		try {
			for (int i = 0; i < X.length(); i++) {
				index = X.substring(i, i + 1);
				this.writerk6map.put(index, new BufferedWriter(
						new FileWriter(output + File.separator + "k6" + File.separator + index + ".txt")));
			}
		} catch (IOException e) {
			logger.error("init writer6 fail");
			return false;
		}
		return true;
	}

	private boolean initWriter7() {
		String index = null;
		this.writerk7map = new HashMap<String, BufferedWriter>();
		try {
			for (int i = 0; i < X.length(); i++) {
				for (int j = 0; j < X.length(); j++) {
					index = X.substring(i, i + 1) + X.substring(j, j + 1);
					this.writerk7map.put(index, new BufferedWriter(
							new FileWriter(output + File.separator + "k7" + File.separator + index + ".txt")));
				}
			}
		} catch (IOException e) {
			logger.error("init writer7 fail");
			return false;
		}
		return true;
	}

	private boolean hashStat() {
		this.setNKS();
		if (this.ktuple_in[MAXLEN-2]) {
			if (!hashStat6())
				return false;
		}
		if (this.ktuple_in[MAXLEN-1]) {
			if (!hashStat7())
				return false;
		}
		this.setCV();
		return true;
	}

	private void setCV() {
		if (this.method.equals("0")) {
			for(int i=2;i<MAXLEN-2;i++){
				if (this.ktuple_out[i]){
					cvx[i+1] = this.setCVDLM(kx[i+1], kx[i], kx[1], i);
				}
			}
		} else {
			for(int i=2;i<MAXLEN-2;i++){
				if (this.ktuple_out[i]){
					cvx[i+1] = this.setCVTREE(kx[i+1], kx[i], kx[i-1], i);
				}
			}
		}
	}

	private boolean hashStat6() {
		this.k6 = new ArrayList<SectionNew>(BASES[1]);
		List<LocalKV> locals = new ArrayList<LocalKV>(BASES[1]) ;
		for(int i=0;i<BASES[1];i++){
			locals.add(null);
			k6.add(null);
		}
		this.cv6 = new GlobalKV(locals);
		String section = null;
		String path = null;
		WorkerSection work = null ;
		List<Future<ResultSection>> wait = new ArrayList<Future<ResultSection>>();
		int index = 0 ;
		for (int i = 0; i < X.length(); i++) {
				section = X.substring(i, i + 1) ;
				path = output + File.separator + "k6" + File.separator + section + ".txt";		
				work = new WorkerSection(this,section,path,index);
				wait.add( InnerDBBuilder.se.submit(work) ) ;
				index++;
		}
		if( this.submit(wait)){
			long nonzero = 0L ;
			for(LocalKV e:this.cv6.getLocals()){
				if(e !=null)nonzero+=e.getKeys().length;
			}
			this.cv6.setNonzero(nonzero);
			return true;
		}else{
			return false;
		}
	}

	private boolean hashStat7() {
		List<LocalKV> locals = new ArrayList<LocalKV>(BASES[2]) ;
		for(int i=0;i<BASES[2];i++){
			locals.add(null);
		}
		this.cv7 = new GlobalKV(locals);
		String section = null;
		String path = null;
		WorkerSection work = null ;
		List<Future<ResultSection>> wait = new ArrayList<Future<ResultSection>>();
		int index = 0 ;
		for (int i = 0; i < X.length(); i++) {
			for (int j = 0; j < X.length(); j++) {
				section = X.substring(i, i + 1) + X.substring(j, j + 1);
				path = output + File.separator + "k7" + File.separator + section + ".txt";		
				work = new WorkerSection(this,section,path,index);
				wait.add( InnerDBBuilder.se.submit(work) ) ;
				index++;
			}
		}
		if( this.submit(wait)){
			long nonzero = 0L ;
			for(LocalKV e:this.cv7.getLocals()){
				if(e !=null)nonzero+=e.getKeys().length;
			}
			this.cv7.setNonzero(nonzero);
			return true;
		}else{
			return false;
		}
	}

	private boolean submit(List<Future<ResultSection>> wait){
		if(wait.isEmpty())return true;
		WorkerSection work = null ;
		List<Future<ResultSection>> fail = new ArrayList<Future<ResultSection>>(wait.size());
		boolean flag = true ;
		int success = 0 ;
		for(int i=0;i<wait.size();i++){
			ResultSection result = null ;
				try {
					 result = wait.get(i).get() ;
						if(!result.isStatus()){
							logger.error(result);
							flag = false ;
						}else{
							success++ ;
							flag = true ;
						}
				}catch (Exception e) {
					logger.error(e.getMessage());
					flag = false ;
				}
			if(!flag){
				work = new WorkerSection(this,result.getSection(),result.getPath(),result.getIndex());
				fail.add( InnerDBBuilder.se.submit(work) ) ;
			}
		}
		//logger.info("end 1 round succ="+success+" total="+wait.size());
		if(success==wait.size())return true ;
		for(Future<ResultSection> f:fail){
			try {
				ResultSection result = f.get() ;
					if(!result.isStatus()){
						logger.error(result);
					}else{
						success++ ;
					}
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			} catch (ExecutionException e) {
				logger.error(e.getMessage());
			}
		}
		//logger.info("end 2 round succ="+success+" total="+wait.size());
		return success==wait.size() ;
	}
	
	public boolean computeSection(String section,String path,int index){
		SectionNew s = loadSection(section, path); 
		if (s==null){
			return false;
		}

		if(section.length()==1){
			k6.set(index, s);
			if (this.ktuple_out[MAXLEN-2]) {
				if (this.method.equals("0")) {
					this.setCV6DLM(s,index);
				}else{
					this.setCV6CVTREE(s,index);
				}
			}
		}else if(section.length()==2){
			if (this.ktuple_out[MAXLEN-1]) {
				if (this.method.equals("0")) {
					this.setCV7DLM(s,index);
				}else{
					this.setCV7CVTREE(s,index);
				}
			}
		}
		return true;
	}
	
	private SectionNew loadSection(String section, String path) {
		BufferedReader reader = null;
		String line = null;
		try {
			try {
				reader = new BufferedReader(new FileReader(path));
				int[] kt5 = new int[BASES[MAXLEN-2]];
				while ((line = reader.readLine()) != null) {
					kt5[toNumber(line)]++;
				}
				return getSectionNew(section, kt5);
				
			} finally {
				if(reader!=null)reader.close();
			}
		} catch (IOException e) {
			logger.error("load section=" + section + "," + path);
			return null;
		}
	}

	private SectionNew getSectionNew(String section, int[] kt5) {
		if (this.method.equals("0")) {
			if (section.length() == 1) {
				if (this.ktuple_out[MAXLEN-2]) {
					return getNonzeroAndDLMk6(section, kt5);
				} else {
					return getNonzeroElements(section, kt5);
				}
			} else {
				return getNonzeroAndDLMk7(section, kt5);
			}
		} else {
			if (section.length() == 1) {
				if (this.ktuple_out[MAXLEN-2]) {
					return getNonzeroAndCVTREEk6(section, kt5);
				} else {
					return getNonzeroElements(section, kt5);
				}
			} else {
				return getNonzeroAndCVTREEk7(section, kt5);
			}
		}

	}

	private SectionNew getNonzeroElements(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;
		for (int i = 0; i < BASES[MAXLEN-2]; i++) {
			if (kt5[i] != 0) {
				nonzero++;
			}
		}
		s.setNonzero(nonzero);
		s.setZero(zero);
		s.setK5(kt5);
		return s;
	}

	//TODO
	private SectionNew getNonzeroAndDLMk6(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;
		int prefixk1_index = toNumber(section);
		int prefix = 0;
		int prefixk1 = this.kx[1][prefixk1_index];
		int suffixk1 = 0;
		prefixk1_index *= BASES[MAXLEN-3];
		if (prefixk1 != 0) {
			for (int i = 0; i < BASES[MAXLEN-2]; i++) {
				if (kt5[i] != 0) {
					nonzero++;
				} else {
					if (this.kx[MAXLEN-2][i] != 0) {
						zero++;
						kt5[i] = -1;
					} else {
						prefix = this.kx[MAXLEN-2][prefixk1_index + (i / BASES[1])];
						suffixk1 = this.kx[1][i % BASES[1]];
						if (prefix != 0 && suffixk1 != 0) {
							zero++;
							kt5[i] = -1;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < BASES[MAXLEN-2]; i++) {
				if (kt5[i] != 0) {
					nonzero++;
				} else {
					prefix = this.kx[MAXLEN-2][prefixk1_index + (i / BASES[1])];
					suffixk1 = this.kx[1][i % BASES[1]];
					if (prefix != 0 && suffixk1 != 0) {
						zero++;
						kt5[i] = -1;
					}
				}
			}
		}
		s.setNonzero(nonzero);
		s.setZero(zero);
		s.setK5(kt5);
		return s;
	}
	
	private SectionNew getNonzeroAndDLMk7(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;
		int firstpos = toNumber(section.substring(0, 1));
		int secondpos = toNumber(section.substring(1, 2));
		int[] prelist = this.k6.get(firstpos).getK5();
		int[] suflist = this.k6.get(secondpos).getK5();
		secondpos*=BASES[MAXLEN-3];
		if (this.kx[1][firstpos] != 0) {
			for (int i = 0; i < BASES[MAXLEN-2]; i++) {
				if (kt5[i] != 0) {
					nonzero++;
				} else {
					if (suflist[i] > 0) {
						zero++;
						kt5[i] = -1;
					} else {
						if (prelist[secondpos + (i / BASES[1])] > 0 && this.kx[1][i % BASES[1]] != 0) {
							zero++;
							kt5[i] = -1;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < BASES[MAXLEN-2]; i++) {
				if (kt5[i] != 0) {
					nonzero++;
				} else {
					if (prelist[secondpos + (i / BASES[1])] > 0 && this.kx[1][i % BASES[1]] != 0) {
							zero++;
							kt5[i] = -1;
						}
				}
			}
		}
		s.setNonzero(nonzero);
		s.setZero(zero);
		s.setK5(kt5);
		return s;
	}

	private SectionNew getNonzeroAndCVTREEk6(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;

		int prefixk1_index = toNumber(section) * BASES[MAXLEN-3];

		for (int i = 0; i < BASES[MAXLEN-2]; i++) {
			if (kt5[i] != 0) {
				nonzero++;
			} else {
				if (this.kx[MAXLEN-2][i] != 0 && this.kx[MAXLEN-2][prefixk1_index + (i / BASES[1])] != 0) {
					zero++;
					kt5[i] = -1;
				}
			}
		}
		s.setNonzero(nonzero);
		s.setZero(zero);
		s.setK5(kt5);
		return s;
	}
	
	private SectionNew getNonzeroAndCVTREEk7(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;
		int firstpos = toNumber(section.substring(0, 1));
		int secondpos = toNumber(section.substring(1, 2));
		int[] prelist = this.k6.get(firstpos).getK5();
		int[] suflist = this.k6.get(secondpos).getK5();
		secondpos*=BASES[MAXLEN-3];
		
		for (int i = 0; i < BASES[MAXLEN-2]; i++) {
			if (kt5[i] != 0) {
				nonzero++;
			} else {
				if (suflist[i] > 0 && prelist[secondpos + (i/BASES[1])] > 0) {
					zero++;
					kt5[i] = -1;
				}
			}
		}
		s.setNonzero(nonzero);
		s.setZero(zero);
		s.setK5(kt5);
		return s;
	}

	private boolean close(BufferedWriter writer) {

		try {
			try {
				writer.flush();
				return true;
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			return false;
		}
	}

	private boolean shutdown() {
		if (!this.ktuple_in[MAXLEN-2] && !this.ktuple_in[MAXLEN-1])
			return true;
		boolean flag = true;
		if (this.ktuple_in[MAXLEN-2]) {
			for (Map.Entry<String, BufferedWriter> e : this.writerk6map.entrySet()) {
				if (!this.close(e.getValue())) {
					logger.error("close writer6" + e.getKey());
					flag = false;
				}
			}
		}
		if (this.ktuple_in[MAXLEN-1]) {
			for (Map.Entry<String, BufferedWriter> e : this.writerk7map.entrySet()) {
				if (!this.close(e.getValue())) {
					logger.error("close writer7" + e.getKey());
					flag = false;
				}
			}
		}
		return flag;
	}

	public boolean start() {
		if (!this.check())
			return false;
		if (!this.init())
			return false;
		if ("1".equals(mode_switch)) {
			if (!this.processStrict())
				return false;
		} else {
			if (!this.processSimple())
				return false;
		}
		if (!this.shutdown())
			return false;
		// compute CV
		if (!this.hashStat())
			return false;

		FileUtil.delete(new File(this.output));
		return true;
	}

	public void free() {
		this.kx = null;
		this.k6 = null;

		this.nks = null;
		this.output = null;
		this.input = null;
		this.sections = null;
		this.writerk6map = null;
		this.writerk7map = null;
	}

	public void freeAll() {
		free();
		this.cvx = null;
		this.cv6 = null;
		this.cv7 = null;
	}

	

	protected boolean stat(String sb) {
		return statOptimal(sb);
	}
	
	private boolean statOptimal(String sb) {
		int[] bits = new int[sb.length()];
		for (int i = 0; i < sb.length(); i++) {
			bits[i] = charmap.get(sb.substring(i, i + 1)).intValue();
		}
		
		int LEN = 0 ;
		
		for(int j=0;j<MAXLEN-2;j++){
			if (this.ktuple_in[j]) {
				LEN = bits.length-j ;
				for (int i = 0; i < LEN ; i++) {
					kx[j+1][toNumber(bits,i,j)]++;
				}
			}
		}
		
		if (this.ktuple_in[MAXLEN-2]) {
			LEN = bits.length-(MAXLEN-2) ;
			for (int i = 0; i < LEN; i++) {
				if (!outputHashK(this.writerk6map, sb.substring(i, i + 1), sb.substring(i + 1, i + (MAXLEN-1))))
					return false;
			}
		}
		if (this.ktuple_in[MAXLEN-1]) {	
			LEN = bits.length-(MAXLEN-1) ;
			for (int i = 0; i < LEN; i++) {
				if (!outputHashK(this.writerk7map, sb.substring(i, i + 2), sb.substring(i + 2, i + MAXLEN)))
					return false;
			}
		}
		return true;
	}
	
	private boolean outputHashK(Map<String, BufferedWriter> map, String index, String ktuple) {
		BufferedWriter writer = map.get(index);
		try {
			writer.write(ktuple);
			writer.newLine();
			return true;
		} catch (IOException e) {
			logger.error("output hashk " + index + " " + ktuple);
			return false;
		}
	}

	private void setCV6CVTREE(SectionNew s,int index) {
		double value = 0;
		double con = ((double) (this.nks.get(MAXLEN-3) * this.nks.get(MAXLEN-3))) / ((double) this.nks.get(MAXLEN-2) * this.nks.get(MAXLEN-4));
		int middle_index = 0;
		int prefixk1_index = 0;
		/// ----------------------------------------------------
		
		LocalKV local = null;
		int[] keys = null;
		double[] values = null;
		int nonzero = s.getNonzero() + s.getZero();
		keys = new int[nonzero];
		values = new double[nonzero];
		local = new LocalKV(keys, values);
		this.cv6.getLocals().set(index, local);
			///// ------------------------------------------------
			prefixk1_index = toNumber(s.getName()) * BASES[MAXLEN-3];

			int[] kt5 = s.getK5();
			int current = 0;
			for (int i = 0; i < kt5.length; i++) {
				if (kt5[i] == -1) {
					value = 0;
				} else if (kt5[i] > 0) {
					middle_index = i / BASES[1];
					value = (con*kt5[i]*this.kx[MAXLEN-3][middle_index])/((double)(this.kx[MAXLEN-2][prefixk1_index+middle_index]*this.kx[MAXLEN-2][i]));
				} else {
					continue;
				}
				keys[current] = i;
				values[current] = value - 1;
				current++;
			}
			// use only for cv7
			if (!this.ktuple_out[MAXLEN-1]) {
				if (!"1".equals(ktuple_switch))
					s.setK5(null);
			}
	}

	private void setCV7CVTREE(SectionNew s,int index) {
		String name = null;
		int firstpos = 0;
		int secondpos = 0;
		double value = 0;
		double con = ((double) (this.nks.get(MAXLEN-2) * this.nks.get(MAXLEN-2))) / ((double) this.nks.get(MAXLEN-1) * this.nks.get(MAXLEN-3));

		int middle_index = 0;
		int[] prelist = null;
		int[] suflist = null;

		LocalKV local = null;
		int[] keys = null;
		double[] values = null;
		int nonzero = s.getNonzero() + s.getZero();
		keys = new int[nonzero];
		values = new double[nonzero];
		local = new LocalKV(keys, values);
		this.cv7.getLocals().set(index, local);
			///// ------------------------------------------------
			name = s.getName();
			firstpos = toNumber(name.substring(0, 1));
			secondpos = toNumber(name.substring(1, 2));
			prelist = this.k6.get(firstpos).getK5();
			suflist = this.k6.get(secondpos).getK5();
			secondpos*=BASES[MAXLEN-3];
			int[] kt5 = s.getK5();
			int current = 0;
			for (int i = 0; i < kt5.length; i++) {
				if (kt5[i] == -1) {
					value = 0;
				} else if (kt5[i] > 0) {
					middle_index = secondpos + (i / BASES[1]);
					value = (con*kt5[i]*this.kx[MAXLEN-2][middle_index])/((double)(prelist[middle_index]*suflist[i]));
				} else {
					continue;
				}
				keys[current] = i;
				values[current] = value - 1;
				current++;
			}
			if (!"1".equals(ktuple_switch))
				s.setK5(null);
	}
	
	// TODO
	private void setCV6DLM(SectionNew s,int index) {
		double value = 0;
		int prefixk1_index = 0;
		int prefixk1 = 0;
		double con = ((double) (2 * this.nks.get(0) * this.nks.get(MAXLEN-3))) / (double) this.nks.get(MAXLEN-2);
		LocalKV local = null;
		int[] keys = null;
		double[] values = null;
		int nonzero = s.getNonzero() + s.getZero();
		keys = new int[nonzero];
		values = new double[nonzero];
		local = new LocalKV(keys, values);
		this.cv6.getLocals().set(index, local);
			///// ------------------------------------------------
			prefixk1_index = toNumber(s.getName());
			prefixk1 = this.kx[1][prefixk1_index];
			prefixk1_index *= BASES[MAXLEN-3];

			int[] kt5 = s.getK5();
			int current = 0;
			for (int i = 0; i < kt5.length; i++) {
				if (kt5[i] == -1) {
					value = 0;
				} else if (kt5[i] > 0) {
					value = (con*kt5[i])/(double)((this.kx[MAXLEN-2][prefixk1_index+(i/BASES[1])]*this.kx[1][i%BASES[1]])+(this.kx[MAXLEN-2][i]*prefixk1));
				} else {
					continue;
				}
				keys[current] = i;
				values[current] = value - 1;
				current++;
			}
			// use only for cv7
			if (!this.ktuple_out[MAXLEN-1]) {
				if (!"1".equals(ktuple_switch))
					s.setK5(null);
			}
	}
	
	private void setCV7DLM(SectionNew s,int index) {
		String name = null;
		int prefixk1 = 0;
		int firstpos = 0;
		int secondpos = 0;
		double value = 0;
		double con = ((double) (2 * this.nks.get(0) * this.nks.get(MAXLEN-2))) / (double) this.nks.get(MAXLEN-1);
		int[] prelist = null;
		int[] suflist = null;

		LocalKV local = null;
		int[] keys = null;
		double[] values = null;
		int nonzero = s.getNonzero() + s.getZero();
		keys = new int[nonzero];
		values = new double[nonzero];
		local = new LocalKV(keys, values);
		this.cv7.getLocals().set(index, local);
			///// ------------------------------------------------
			name = s.getName();
			firstpos = toNumber(name.substring(0, 1));
			prefixk1 = this.kx[1][firstpos];
			secondpos = toNumber(name.substring(1, 2));
			prelist = this.k6.get(firstpos).getK5();
			suflist = this.k6.get(secondpos).getK5();
			secondpos*=BASES[MAXLEN-3];
			int[] kt5 = s.getK5();
			int current = 0;
			for (int i = 0; i < kt5.length; i++) {
				if (kt5[i] == -1) {
					value = 0;
				} else if (kt5[i] > 0) {
					value = (con*kt5[i])/(double)((prelist[secondpos+(i/BASES[1])]*this.kx[1][i%BASES[1]])+(suflist[i] * prefixk1));
				} else {
					continue;
				}
				keys[current] = i;
				values[current] = value - 1;
				current++;
			}
			if (!"1".equals(ktuple_switch))
				s.setK5(null);
	}

	public long totalMemory() {
		long total = 0;
		for (int i = 2; i < MAXLEN-2; i++) {
			if (this.ktuple_out[i])
				total += (long)BASES[i+1] * 8L;
		}
		if (this.ktuple_out[MAXLEN-2])
			total += this.cv6.getMemory();
		if (this.ktuple_out[MAXLEN-1])
			total += this.cv7.getMemory();

		return total;
	}

}
