package edu.xtu.bio.model;

import java.util.Date;

/**
 * @author WuQi@XTU
 * @time_created 2015年10月18日,‏‎上午10:11:25
 * @version 1.0
 */
public class Report {
	
	private long id ;
	private long pid ;
	private int ctime ;
	private int outgrp ;
	private String type ;
	private String ktuple ; 
	private String name ;
	private String email ;
	private String selected ;
	private String status = "5" ;
	private String method ;
	private String ip ;
	private String file ;
	private String domain ;
	private Date stime ;
	private int total ;
	private String glist ;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKtuple() {
		return ktuple;
	}
	public void setKtuple(String ktuple) {
		if(ktuple!=null){
			this.ktuple = ktuple.replace(" ", "") ;
		}
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Date getStime() {
		return stime;
	}
	public void setStime(Date stime) {
		this.stime = stime;
	}
	public int getCtime() {
		return ctime;
	}
	public void setCtime(int ctime) {
		this.ctime = ctime;
	}
	
	public int getOutgrp() {
		return outgrp;
	}
	
	public void setOutgrp(int outgrp) {
		this.outgrp = outgrp;
	}
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file ;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
			this.domain = domain ;
	}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getGlist() {
		return glist;
	}
	public void setGlist(String glist) {
		this.glist = glist;
	}
	

	public Report() {
		super();
	}
	
	public Report(long id, long pid, int ctime, int outgrp, String type, String ktuple, String name, String email,
			String selected, String status, String method, String ip, String file, String domain, Date stime,int total) {
		super();
		this.id = id;
		this.pid = pid;
		this.ctime = ctime;
		this.outgrp = outgrp;
		this.type = type;
		this.ktuple = ktuple;
		this.name = name;
		this.email = email;
		this.selected = selected;
		this.status = status;
		this.method = method;
		this.ip = ip;
		this.file = file;
		this.domain = domain;
		this.stime = stime;
		this.total = total ;
	}
	@Override
	public String toString() {
		return "Report [id=" + id + ", pid=" + pid + ", ctime=" + ctime + ", outgrp=" + outgrp + ", type=" + type
				+ ", ktuple=" + ktuple + ", name=" + name + ", email=" + email + ", selected=" + selected
				+ ", status=" + status + ", method=" + method + ", ip=" + ip + ", file=" + file + ", domain=" + domain
				+ ", stime=" + stime + ", total=" + total+"]";
	}
	
	
}