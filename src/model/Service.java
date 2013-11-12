package model;

import java.io.Serializable;

public class Service implements Serializable{

	public Service(){}
	
	public Service(int time, float rel){
		this.time = time;
		this.rel = rel;
		this.tries = 1;
	}
	
	public Service(int time, float rel, int tries){
		this.time = time;
		this.rel = rel;
		this.tries = tries;
	}
	
	//private String name;
	private int time;
	private float rel;
	private int tries;
	
	
	public int getTime() {
		return time;
	}
	public float getRel() {
		return rel;
	}
	public int getTries() {
		return tries;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public void setRel(float rel) {
		this.rel = rel;
	}
	public void setTries(int tries) {
		this.tries = tries;
	}
}
