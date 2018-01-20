package edu.xtu.bio.pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class ProteinOptimalMultiDisc extends ProteinAbstract {

	private int[] k1 = null;
	private int[] k2 = null;
	private int[] k3 = null;
	private int[] k4 = null;
	private int[] k5 = null;
	private int[] k6 = null;
	private Map<String, BufferedWriter> writerk7map = null;
	private double[] cv3 = null;
	private double[] cv4 = null;
	private double[] cv5 = null;
	private double[] cv6 = null;
	private GlobalKV cv7 = null;
	private List<Integer> sections = null;
	private List<Long> nks = null;
	private String method = null;
	private String genome_name = null;
	private String input = null;
	private String output = null;
	private boolean[] ktuple_in = new boolean[MAXLEN];
	private boolean[] ktuple_out = null;
	public ProteinOptimalMultiDisc() {
		super();
	}
	public ProteinOptimalMultiDisc(String genome_name, String input, String output, String method, boolean[] ktuple) {
		super();
		this.genome_name = genome_name;
		this.input = input;
		this.output = output;
		this.method = method;
		this.ktuple_out = ktuple;

		if (this.method.equals("0")) {
			this.ktuple_in[0] = true;
			for (int i = 2; i < MAXLEN; i++) {
				if (ktuple[i]) {
					this.ktuple_in[i] = true;
					this.ktuple_in[i - 1] = true;
				}
			}
		} else {
			for (int i = 2; i < MAXLEN; i++) {
				if (ktuple[i]) {
					this.ktuple_in[i] = true;
					this.ktuple_in[i - 1] = true;
					this.ktuple_in[i - 2] = true;
				}
			}
		}
	}

	public String getGenome_name() {
		return genome_name;
	}

	public double[] getCv3() {
		return cv3;
	}

	public double[] getCv4() {
		return cv4;
	}

	public double[] getCv5() {
		return cv5;
	}

	public double[] getCv6() {
		return cv6;
	}

	public GlobalKV getCv7() {
		return cv7;
	}

	public void setCv3(double[] cv3) {
		this.cv3 = cv3;
	}

	public void setCv4(double[] cv4) {
		this.cv4 = cv4;
	}

	public void setCv5(double[] cv5) {
		this.cv5 = cv5;
	}

	public void setCv6(double[] cv6) {
		this.cv6 = cv6;
	}

	public void setCv7(GlobalKV cv7) {
		this.cv7 = cv7;
	}

	public boolean[] getKtuple_out() {
		return ktuple_out;
	}

	public void setKtuple_out(boolean[] ktuple_out) {
		this.ktuple_out = ktuple_out;
	}
	
	private boolean check() {
		File in = new File(this.input);
		if (!in.exists()) {
			logger.error("not found file:" + this.input);
			return false;
		}

		File out = new File(this.output);
		File k7 = new File(output + File.separatorChar + "k7");
		if (!out.exists()) {
			if (!out.mkdirs()) {
				logger.error("create directory:" + out.getAbsolutePath());
				return false;
			}
		}

		if (this.ktuple_in[6]) {
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

		if (this.ktuple_in[0])
			k1 = new int[BASES[1]];
		if (this.ktuple_in[1])
			k2 = new int[BASES[2]];
		if (this.ktuple_in[2])
			k3 = new int[BASES[3]];
		if (this.ktuple_in[3])
			k4 = new int[BASES[4]];
		if (this.ktuple_in[4])
			k5 = new int[BASES[5]];
		if (this.ktuple_in[5])
			k6 = new int[BASES[6]];
		sections = new ArrayList<Integer>();
		nks = new ArrayList<Long>();

		if (this.ktuple_in[6]) {
			if (!initWriter7())
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
		if (this.ktuple_in[6]) {
			if (!hashStat7())return false;
		}
		this.setCV();
		return true;
	}

	private void setCV() {
		if (this.method.equals("0")) {
			if (this.ktuple_out[2])
				this.cv3 = this.setCVDLM(k3, k2, k1, 2);
			if (this.ktuple_out[3])
				this.cv4 = this.setCVDLM(k4, k3, k1, 3);
			if (this.ktuple_out[4])
				this.cv5 = this.setCVDLM(k5, k4, k1, 4);
			if (this.ktuple_out[5])
				this.cv6 = this.setCVDLM(k6, k5, k1, 5);
		} else {
			if (this.ktuple_out[2])
				this.cv3 = this.setCVTREE(k3, k2, k1, 2);
			if (this.ktuple_out[3])
				this.cv4 = this.setCVTREE(k4, k3, k2, 3);
			if (this.ktuple_out[4])
				this.cv5 = this.setCVTREE(k5, k4, k3, 4);
			if (this.ktuple_out[5])
				this.cv6 = this.setCVTREE(k6, k5, k4, 5);
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
				nonzero+=e.getKeys().length;
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
		if (this.method.equals("0")) {
			this.setCV7DLM(s,index);
		}else{
			this.setCV7CVTREE(s,index);
		}
		return true;
	}
	
	private SectionNew loadSection(String section, String path) {
		BufferedReader reader = null;
		String line = null;
		try {
			try {
				reader = new BufferedReader(new FileReader(path));
				int[] kt5 = new int[BASES[5]];
				while ((line = reader.readLine()) != null) {
					kt5[getIndex5(line)]++;
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
		return this.method.equals("0")?getNonzeroAndDLMk7(section, kt5):getNonzeroAndCVTREEk7(section, kt5);
	}

	private SectionNew getNonzeroAndDLMk7(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;
		int prefixk1 = k1[getIndex1(section.substring(0, 1))];
		int two = getIndex2(section.substring(0, 2))*BASES[4];
		int one = getIndex1(section.substring(1, 2))*BASES[5];
		
		if (prefixk1!=0) {
			for (int i = 0; i < BASES[5]; i++) {
				if (kt5[i] != 0) {
					nonzero++;
				} else {
					if (k6[i+one] != 0) {
						zero++;
						kt5[i] = -1;
					} else {
						if (k6[two + (i / BASES[1])] != 0 && k1[i % BASES[1]] != 0) {
							zero++;
							kt5[i] = -1;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < BASES[5]; i++) {
				if (kt5[i] != 0) {
					nonzero++;
				} else {
					if (k6[two + (i / BASES[1])] != 0 && k1[i % BASES[1]] != 0) {
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

	private SectionNew getNonzeroAndCVTREEk7(String section, int[] kt5) {
		SectionNew s = new SectionNew(section);
		int nonzero = 0;
		int zero = 0;
		int two = getIndex2(section.substring(0, 2))*BASES[4];
		int one = getIndex1(section.substring(1, 2))*BASES[5];
		for (int i = 0; i < BASES[5]; i++) {
			if (kt5[i] != 0) {
				nonzero++;
			} else {
				if (k6[one+i] != 0 && k6[two + (i/BASES[1])] != 0) {
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
		if (!this.ktuple_in[6])
			return true;
		boolean flag = true;
		if (this.ktuple_in[6]) {
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

		if ("1".equals(ktuple_switch)) {
			this.outputCount(this.output + File.separator + "section.txt");
			this.outputNKS(this.output + File.separator + "kt.txt");
			if (this.ktuple_in[0])
				this.outputKtuple(k1, this.output + File.separator + "k1.txt");
			if (this.ktuple_in[1])
				this.outputKtuple(k2, this.output + File.separator + "k2.txt");
			if (this.ktuple_in[2])
				this.outputKtuple(k3, this.output + File.separator + "k3.txt");
			if (this.ktuple_in[3])
				this.outputKtuple(k4, this.output + File.separator + "k4.txt");
			if (this.ktuple_in[4])
				this.outputKtuple(k5, this.output + File.separator + "k5.txt");
			if (this.ktuple_in[5])
				this.outputKtuple(k6, this.output + File.separator + "k6.txt");
			this.shutdown();
		} else {
			FileUtil.delete(new File(this.output));
		}

		return true;
	}

	public void free() {
		this.k1 = null;
		this.k2 = null;
		this.k3 = null;
		this.k4 = null;
		this.k5 = null;
		this.k6 = null;

		this.nks = null;
		this.output = null;
		this.input = null;
		this.sections = null;
		this.writerk7map = null;
	}

	public void freeAll() {
		free();
		this.cv3 = null;
		this.cv4 = null;
		this.cv5 = null;
		this.cv6 = null;
		this.cv7 = null;
	}

	private boolean processSimple() {
		String line = null;
		StringBuilder sb = null;
		BufferedReader reader = null;

		try {
			try {
				reader = new BufferedReader(new FileReader(input));
				sb = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(">")) {
						if (sb.length() != 0) {
							// end this protein ,do it
							if (!processSeq(sb))
								return false;
							sb.delete(0, sb.length());
						}
					} else if (line.length() != 0) {
						sb.append(line);
					}
				}
				// end the last protein ,do it
				if (!processSeq(sb))
					return false;
				return true;
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			logger.error("process fasta file" + input);
			return false;
		}
	}

	private boolean processStrict() {
		String line = null;
		BufferedReader reader = null;
		List<String> list = null;
		try {
			try {
				reader = new BufferedReader(new FileReader(input));
				list = new ArrayList<String>();
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(">")) {
						if (!list.isEmpty()) {
							// end this protein ,do it
							if (!processSection(list))
								return false;
							list.clear();
						}
					} else if (line.length() != 0) {
						list.add(line);
					}
				}
				// end the last protein ,do it
				if (!processSection(list))
					return false;
				return true;
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			logger.error("process fasta file" + input);
			return false;
		}
	}

	private boolean processSeq(StringBuilder sb) {
		/**
		 * B D,N; D J L,I; L X ANY; G Z E,Q ; E O,U C BJXZ
		 */
		for (int i = 0; i < sb.length(); i++) {
			switch (sb.charAt(i)) {
			case 'X':
				sb.setCharAt(i, 'G');
				break;
			case 'U':
				sb.setCharAt(i, 'C');
				break;
			case 'B':
				sb.setCharAt(i, 'D');
				break;
			case 'Z':
				sb.setCharAt(i, 'E');
				break;
			case 'J':
				sb.setCharAt(i, 'L');
				break;
			case 'O':
				sb.setCharAt(i, 'C');
				break;
			case '-':
				sb.delete(i, i + 1);
				break;
			case '*':
				sb.delete(i, i + 1);
				break;
			case ' ':
				sb.delete(i, i + 1);
				break;
			}
		}
		sections.add(sb.length());
		return this.stat(sb.toString());
	}

	private boolean processSection(List<String> list) {
		/**
		 * B D,N; D J L,I; L X ANY; G Z E,Q ; E O,U C BJXZ
		 */
		StringBuilder sb = new StringBuilder();
		char c = 'A';
		// System.out.print("\n0");
		for (String s : list) {
			// System.out.print(s);
			for (int i = 0; i < s.length(); i++) {
				c = s.charAt(i);
				switch (c) {
				case 'A':
					sb.append(c);
					break;
				case 'C':
					sb.append(c);
					break;
				case 'D':
					sb.append(c);
					break;
				case 'E':
					sb.append(c);
					break;
				case 'F':
					sb.append(c);
					break;
				case 'G':
					sb.append(c);
					break;
				case 'H':
					sb.append(c);
					break;
				case 'I':
					sb.append(c);
					break;
				case 'K':
					sb.append(c);
					break;
				case 'L':
					sb.append(c);
					break;
				case 'M':
					sb.append(c);
					break;
				case 'N':
					sb.append(c);
					break;
				case 'P':
					sb.append(c);
					break;
				case 'Q':
					sb.append(c);
					break;
				case 'R':
					sb.append(c);
					break;
				case 'S':
					sb.append(c);
					break;
				case 'T':
					sb.append(c);
					break;
				case 'V':
					sb.append(c);
					break;
				case 'W':
					sb.append(c);
					break;
				case 'Y':
					sb.append(c);
					break;
				case 'X':
					sb.append('G');
					break;
				case 'U':
					sb.append('C');
					break;
				case 'B':
					sb.append('D');
					break;
				case 'Z':
					sb.append('E');
					break;
				case 'J':
					sb.append('L');
					break;
				case 'O':
					sb.append('C');
					break;
				default: {
					logger.warn("invalid char " + c);
				}
					;
				}
			}
		}
		sections.add(sb.length());
		return this.stat(sb.toString());
	}

	private boolean stat(String sb) {
		return statOptimal(sb);
	}
	
	private boolean statOptimal(String sb) {
		int[] bits = new int[sb.length()];
		for (int i = 0; i < sb.length(); i++) {
			bits[i] = charmap.get(sb.substring(i, i + 1)).intValue();
		}
		
		if (this.ktuple_in[0]) {
			for (int i = 0; i < bits.length; i++) {
				k1[bits[i]]++;
			}
		}
		if (this.ktuple_in[1]) {
			for (int i = 0; i < bits.length - 1; i++) {
				k2[toNumber(bits,i,1)]++;
			}
		}
		if (this.ktuple_in[2]) {
			for (int i = 0; i < bits.length - 2; i++) {
				k3[toNumber(bits,i,2)]++;
			}
		}
		if (this.ktuple_in[3]) {
			for (int i = 0; i < bits.length - 3; i++) {
				k4[toNumber(bits,i,3)]++;
			}
		}
		if (this.ktuple_in[4]) {
			for (int i = 0; i < bits.length - 4; i++) {
				k5[toNumber(bits,i,4)]++;
			}
		}
		if (this.ktuple_in[5]) {
			for (int i = 0; i < bits.length - 5; i++) {
				k6[toNumber(bits,i,5)]++;
			}
		}
		if (this.ktuple_in[6]) {
			for (int i = 0; i < bits.length - 6; i++) {
				if (!outputHashK(this.writerk7map, sb.substring(i, i + 2), sb.substring(i + 2, i + 7)))
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

	private boolean outputKtuple(int[] ktuples, String path) {
		PrintWriter writer = null;
		try {
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path)));
				long total = 0;
				for (int i = 0; i < ktuples.length; i++) {
					writer.println(i + " " + ktuples[i]);
					total += ktuples[i];
				}
				writer.println("total " + total);
				writer.flush();
				return true;
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			logger.error("output ktuple" + path);
			return false;
		}
	}

	private long getRealSize(List<Integer> a) {
		long count = 0;
		for (int i : a) {
			count += i;
		}
		return count;
	}

	private boolean outputCount(String path) {
		PrintWriter writer = null;
		try {
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path)));
				writer.println("total=" + this.getRealSize(sections));
				for (int i = 0; i < this.sections.size(); i++) {
					writer.println(sections.get(i));
				}
				writer.flush();
				return true;
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			logger.error("output section " + path);
			return false;
		}
	}

	private boolean outputNKS(String path) {
		PrintWriter writer = null;
		try {
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path)));
				for (int i = 0; i < this.nks.size(); i++) {
					writer.println(nks.get(i));
				}
				writer.flush();
				return true;
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			logger.error("output section " + path);
			return false;
		}
	}

	private void setNKS() {
		long total = 0;
		for (int cnt : sections) {
			total += cnt;
		}
		long sub = 0;
		for (long i = 1; i <= MAXLEN; i++) {
			sub = (i - 1) * this.sections.size();
			this.nks.add(total - sub);
		}
	}

	private double[] setCVTREE(int[] o3, int[] o2, int[] o1, int pos) {
		double[] result = new double[BASES[pos+1]];
		double con = ((double) (this.nks.get(pos - 1) * this.nks.get(pos - 1)))
				/ ((double) this.nks.get(pos) * this.nks.get(pos - 2));
		int suffix_index = 0;
		for (int i = 0; i < BASES[pos+1]; i++) {
			if (o3[i] == 0) {
				if (o2[i / BASES[1]] == 0 || o2[i % BASES[pos]] == 0) {
					continue;
				} else {
					result[i] = -1;
				}
			} else {
				suffix_index = i % BASES[pos];
				result[i] = (con * o3[i] * o1[suffix_index / BASES[1]]) / ((double) (o2[i / BASES[1]] * o2[suffix_index]))
						- 1;
			}
		}
		return result;
	}

	private double[] setCVDLM(int[] o3, int[] o2, int[] o1, int pos) {
		double[] result = new double[BASES[pos+1]];
		double con = ((double) (2 * this.nks.get(0) * this.nks.get(pos - 1))) / this.nks.get(pos);
		for (int i = 0; i < BASES[pos+1]; i++) {
			if (o3[i] == 0) {
				if ((o2[i / BASES[1]] != 0 && o1[i % BASES[1]] != 0)
						|| (o2[i % BASES[pos]] != 0 && o1[i / BASES[pos]] != 0)) {
					result[i] = -1;
				}
			} else {
				result[i] = (con * o3[i])
						/ ((double) (o2[i / BASES[1]] * o1[i % BASES[1]] + o2[i % BASES[pos]] * o1[i / BASES[pos]]))
						- 1;
			}
		}
		return result;
	}

	private void setCV7CVTREE(SectionNew s,int index) {
		String section = null;
		int one = 0;
		int two = 0;
		int middle = 0 ;
		double value = 0;
		double con = ((double) (this.nks.get(5) * this.nks.get(5))) / ((double) this.nks.get(6) * this.nks.get(4));
		int middle_index = 0;
		LocalKV local = null;
		int[] keys = null;
		double[] values = null;
		int nonzero = s.getNonzero() + s.getZero();
		keys = new int[nonzero];
		values = new double[nonzero];
			local = new LocalKV(keys, values);
			this.cv7.getLocals().set(index, local);
			///// ------------------------------------------------
			section = s.getName();
			two = getIndex2(section.substring(0, 2))*BASES[4];
			one = getIndex1(section.substring(1, 2))*BASES[5];
			middle = getIndex1(section.substring(1, 2))*BASES[4];
			int[] kt5 = s.getK5();
			int current = 0;
			for (int i = 0; i < kt5.length; i++) {
				if (kt5[i] == -1) {
					value = 0;
				} else if (kt5[i] > 0) {
					middle_index =  i / BASES[1];
					value = (con*kt5[i]*k5[middle+middle_index])/((double)(k6[one+i]*k6[two+middle_index]));
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

	private void setCV7DLM(SectionNew s,int index) {
		String section = null;
		int prefixk1 = 0;
		int one = 0;
		int two = 0;
		double value = 0;
		double con = ((double) (2 * this.nks.get(0) * this.nks.get(5))) / (double) this.nks.get(6);
		
		LocalKV local = null;
		int[] keys = null;
		double[] values = null;
		int nonzero = s.getNonzero() + s.getZero();
			keys = new int[nonzero];
			values = new double[nonzero];
			local = new LocalKV(keys, values);
			this.cv7.getLocals().set(index, local);
			///// ------------------------------------------------
			section = s.getName();
			prefixk1 = k1[getIndex1(section.substring(0, 1))];
			two = getIndex2(section.substring(0, 2))*BASES[4];
			one = getIndex1(section.substring(1, 2))*BASES[5];
			int[] kt5 = s.getK5();
			int current = 0;
			for (int i = 0; i < kt5.length; i++) {
				if (kt5[i] == -1) {
					value = 0;
				} else if (kt5[i] > 0) {
					value = (con*kt5[i])/(double)((k6[two+(i/BASES[1])]*k1[i%BASES[1]])+(k6[one+i]*prefixk1));
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
		for (int i = 2; i < 6; i++) {
			if (this.ktuple_out[i])
				total += (long)BASES[i+1] * 8L;
		}
		if (this.ktuple_out[6])
			total += this.cv7.getMemory();
		return total;
	}

}
