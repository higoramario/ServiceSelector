package model;

import io.InputReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class CompositionSolver{

	private Composition composition;
	private Map<Integer,ArrayList<Service>> serviceMatrix;
	private Solution solution;
	
	private int compositionLevel;
	private int combination[];
	private int servicesByLevel[];
	
	private int compositionTimeResponse;
	private float compositionReliability;
	
	/*public CompositionSolver(Composition comp){
		composition = comp;
		serviceMatrix = composition.getServiceMatrix();
		compositionTimeResponse = composition.getResponseTime();
		compositionReliability = composition.getReliability();
		compositionLevel = composition.getServiceMatrix().size();
		combination = new int [compositionLevel];
		servicesByLevel = new int [compositionLevel];
		initCombination();
		initServicesByLevel();
		solution = new Solution();
	}*/

	protected abstract void initCombination();
	
	protected abstract void initServicesByLevel();
	
	/*
	 * This method must be used to get strategy of a FTT.
	 * Call the method getCombinationServices
	 * */
	public abstract void changeCombination() throws IOException;
	
	/*
	 * This method reads the serviceMatrix and extract each service composition combination
	 * Call the method calculateCombination
	 * */
	public abstract void getCombinationServices();
	
	/*
	 * This method calculates and verifies which composition combination meet the composition QoS requirements
	 * Call the method calculateTime and calculateReliability
	 * Compare the results and add to the solution
	 * */
	public abstract void calculateCombination(HashMap<Integer,ArrayList<Service>> combinationMatrix);

	/*
	 * This method calculates timeResponse for a FTT
	 * */
	public abstract int calculateTime(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt);

	/*
	 * This method calculates reliability for a FTT
	 * */
	public abstract float calculateReliability(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt);
	
	public abstract void exportSolutions(FTT ftt) throws IOException;
	
	protected abstract void printCombination(int[] actualCombination);

	
	protected abstract void printSolutions();
	
}
