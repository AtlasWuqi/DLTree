package edu.xtu.bio.dna;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.xtu.bio.service.impl.AGPConstant;
import edu.xtu.bio.service.intf.Genome;
import edu.xtu.bio.utils.Configuration;

public abstract class DNAAbstract implements Genome{
	
	public static int MAXLEN = 15 ;
	public static int[] BASES = new int[MAXLEN+1];
	public static final String X =  "ACGT" ;
	public static String mode_switch = "1" ; 
	public static String ktuple_switch = "0" ;
	
	public static Map<String,Integer> charmap = null ;
	public static Logger logger = Logger.getLogger(DNAAbstract.class);
	
	protected int[][] kx = new int[MAXLEN+1][] ; 
	public double[][] cvx = new double[MAXLEN+1][] ; 
	
	protected List<Integer> sections = null;
	protected List<Long> nks = null;

	protected String method = null;
	protected String genome_name = null;
	protected String input = null;
	protected String output = null;
	protected boolean[] ktuple_in = new boolean[MAXLEN];
	protected boolean[] ktuple_out = null;
	
	static{
		BASES[0]=1;
		for(int i=1;i<BASES.length;i++){
			BASES[i]=BASES[i-1]<<2;
		}
		charmap = new HashMap<String, Integer>() ;
		for(Integer i=0;i<BASES[1];i++){
			charmap.put(X.substring(i, i+1), i);
		}
		Configuration.init(AGPConstant.PATH_CONF);
		mode_switch = Configuration.getProperty("switch.mode","1");
		ktuple_switch = Configuration.getProperty("switch.ktuple","0");
	}
	
	public DNAAbstract() {
		super();
	}

	public DNAAbstract(String genome_name, String input, String output, String method, boolean[] ktuple) {
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
	

	public double[][] getCvx() {
		return cvx;
	}

	public void setCvx(double[][] cvx) {
		this.cvx = cvx;
	}

	public String getGenome_name() {
		return genome_name;
	}

	public boolean[] getKtuple_out() {
		return ktuple_out;
	}

	public void setKtuple_out(boolean[] ktuple_out) {
		this.ktuple_out = ktuple_out;
	
	}
	
	protected boolean processSimple() {
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

	protected boolean processStrict() {
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
		/**International Union of Pure and Applied Chemistry CODE
Nucleic acid code	Meaning	Mnemonic
A	A	Adenine
C	C	Cytosine
G	G	Guanine
T	T	Thymine
U	U	Uracil
R	A or G	Purine
Y	C, T or U	Pyrimidines
K	G, T or U	Bases which are ketones
M	A or C	Bases with amino groups
S	C or G	Strong interaction
W	A, T or U	Weak interaction
B	Not A (i.e. C, G, T or U)	B comes after A
D	Not C (i.e. A, G, T or U)	D comes after C
H	Not G (i.e., A, C, T or U)	H comes after G
V	Neither T nor U (i.e. A, C or G)	V comes after U
N	A C G T U	Nucleic acid
X	Masked	
-	Gap of indeterminate length
		 */
		
		/**NCBI CODE
		Nucleotide Codes
		A	adenosine	   
		C	cytidine	   
		G	guanine	       
		T	thymidine	   
		U	uridine (matches T)
		R	G or A
		Y	T or C
		K	G or T
		M	A or C
		S	G or C
		W	A or T
		B	G, T, or C
		D	G, A, or T
		H	A, C, or T
		V	G, C, or A
		N	A, G, C, or T
		 */
		char c = 'A';
		for (int i = 0; i < sb.length(); i++) {
			c = sb.charAt(i);
			switch (c) {
			case 'N':
				sb.setCharAt(i, 'A');
				break;
			case 'U':
				sb.setCharAt(i, 'T');
				break;
			case 'K':
				sb.setCharAt(i, 'G');
				break;
			case 'S':
				sb.setCharAt(i, 'G');
				break;
			case 'Y':
				sb.setCharAt(i, 'T');
				break;
			case 'M':
				sb.setCharAt(i, 'A');
				break;
			case 'W':
				sb.setCharAt(i, 'A');
				break;
			case 'R':
				sb.setCharAt(i, 'G');
				break;
			case 'B':
				sb.setCharAt(i, 'G');
				break;
			case 'D':
				sb.setCharAt(i, 'G');
				break;
			case 'H':
				sb.setCharAt(i, 'A');
				break;
			case 'V':
				sb.setCharAt(i, 'G');
				break;
			}
		}
		sections.add(sb.length());
		return this.stat(sb.toString());
	}

	private boolean processSection(List<String> list) {
	
		/**NCBI CODE
		Nucleotide Codes
		R	G or A
		Y	T or C
		K	G or T
		M	A or C
		S	G or C
		W	A or T
		B	G, T, or C
		D	G, A, or T
		H	A, C, or T
		V	G, C, or A
		N	A, G, C, or T
		 */
		StringBuilder sb = new StringBuilder();
		char c = 'A';
		for (String s : list) {
			for (int i = 0; i < s.length(); i++) {
				c = s.charAt(i);
				switch (c) {
				case 'A':
					sb.append(c);
					break;
				case 'C':
					sb.append(c);
					break;
				case 'G':
					sb.append(c);
					break;
				case 'T':
					sb.append(c);
					break;
				case 'U':
					sb.append('T');
				case 'N':
					sb.append('A');
				case 'K':
					sb.append('G');
				case 'S':
					sb.append('G');
				case 'Y':
					sb.append('T');
				case 'M':
					sb.append('A');
				case 'W':
					sb.append('A');
				case 'R':
					sb.append('G');
				case 'B':
					sb.append('G');
				case 'D':
					sb.append('G');
				case 'H':
					sb.append('A');
				case 'V':
					sb.append('G');
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
	
	protected void setNKS() {
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
	
	protected double[] setCVTREE(int[] o3, int[] o2, int[] o1, int pos) {
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

	protected double[] setCVDLM(int[] o3, int[] o2, int[] o1, int pos) {
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
	
	protected abstract boolean stat(String sb);
	
	public static int toNumber(int[] bits,int from,int len){
		int sum = 0 ;
		int i = 0;
		for(;i<len;i++){
			//sum += bits[from+i]*BASES[len-i];
			sum += bits[from+i]<<((len-i)<<1);
		}
		sum += bits[from+i];
		return sum ;
	}
	
	public static int toNumber(String sb){
		int[] bits = new int[sb.length()];
		for (int i = 0; i < sb.length(); i++) {
			bits[i] = charmap.get(sb.substring(i, i + 1)).intValue();
		}
		return toNumber(bits, 0, sb.length()-1);
	}
	
	public boolean computeSection(String section,String path,int index){
		return true;
	}
}
