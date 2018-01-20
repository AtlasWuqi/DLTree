package edu.xtu.bio.model;

/**
 * @author WuQi@XTU
 * @time_created 2015年9月28日,下午5:42:38
 * @version 1.0
 */
public class Species extends Size{
	
		private int id ;
		private String filename ;
		private long cvsize;

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}

		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
	
		public long getCvsize() {
			return cvsize;
		}
		public void setCvsize(long cvsize) {
			this.cvsize = cvsize;
		}
		public Species() {
			super();
		}
		public Species(int id, String filename,long cvsize) {
			super();
			this.id = id;
			this.filename = filename;
			this.cvsize = cvsize ;
		}
}
