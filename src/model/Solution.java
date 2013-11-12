package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Solution extends Composition  implements Serializable{

	private int executionTime;
	private float executionReliability;
	private ArrayList<HashMap<Integer,ArrayList<Service>>> solutionList = new ArrayList<HashMap<Integer,ArrayList<Service>>>();
	
	public int getExecutionTime() {
		return executionTime;
	}
	public float getExecutionReliability() {
		return executionReliability;
	}
	public ArrayList<HashMap<Integer, ArrayList<Service>>> getSolutionList() {
		return solutionList;
	}
	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}
	public void setExecutionReliability(float executionReliability) {
		this.executionReliability = executionReliability;
	}
	public void setSolutionList(
			ArrayList<HashMap<Integer, ArrayList<Service>>> solutionList) {
		this.solutionList = solutionList;
	}
	public void addSolution(HashMap<Integer,ArrayList<Service>> matrix){
		solutionList.add(matrix);
	}
	public int size(){
		return solutionList.size();
	}
}
