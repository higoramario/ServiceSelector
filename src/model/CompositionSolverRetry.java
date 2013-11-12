package model;

import io.InputReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositionSolverRetry extends CompositionSolver{

	Composition composition;
	private Map<Integer,ArrayList<Service>> serviceMatrix;
	private Solution solution;
	
	private int compositionLevel;
	private int combination[];
	private int servicesByLevel[];
	private int maxDepth;
	
	private int compositionTimeResponse;
	private float compositionReliability;
	
	private int totalCombinations = 0;
	
	public CompositionSolverRetry(Composition comp){
		//super(comp);
		composition = comp;
		serviceMatrix = composition.getServiceMatrix();
		compositionTimeResponse = composition.getResponseTime();
		compositionReliability = composition.getReliability();
		compositionLevel = composition.getServiceMatrix().size();
		combination = new int [compositionLevel];
		servicesByLevel = new int [compositionLevel];
		initCombination();
		initServicesByLevel();
		getMaximumDepth();
		solution = new Solution();
	}

	protected void initCombination() {
		for(int i = 0;i<compositionLevel; i++){
			combination[i]=1;
		}
	}
	
	protected void initServicesByLevel(){
		ArrayList<Service> services = new ArrayList<Service>();
		for(int i = 1; i <= serviceMatrix.size(); i++){
			services = serviceMatrix.get(i);
			servicesByLevel[i-1] = services.size();
		}
	}
	
	/*
	 * Get the maximum number of services in a level
	 * */
	private void getMaximumDepth() {
		maxDepth = 0;
		for(int i = 0; i < servicesByLevel.length; i++){
			if(servicesByLevel[i] > maxDepth){
				maxDepth = servicesByLevel[i];
			}
		}
	}
	
	/*
	 * This method generates all possible combinations between services to use with FTT Retry
	 * */
	public void changeCombination() throws IOException
	{
		int countLevel = compositionLevel;
		while(countLevel > 0){
			countLevel = compositionLevel;
			combination[compositionLevel-1] = 1;
			for(int i = 0; i < servicesByLevel[compositionLevel-1];i++){
				getCombinationServices(combination);
				combination[compositionLevel-1] += 1;
			}
			countLevel--;
			while(countLevel > 0){
				if(combination[countLevel-1] < servicesByLevel[countLevel-1]){
					combination[countLevel-1] += 1;
					break;
				}
				else{
					combination[countLevel-1] = 1;
					countLevel--;
				}
			}
		}
		printSolutions();
		exportSolutions(FTT.RETRY);
		System.out.println("Number of solutions:" + solution.size());
		System.out.println("Total of configurations:" + totalCombinations);		
	}
	
	public void getCombinationServices(int[] actualCombination) {
		HashMap<Integer,ArrayList<Service>> combinationMatrix = new HashMap<Integer,ArrayList<Service>>();
		ArrayList<Service> serviceSequence;
		Service service = new Service();
		//one service by level for Retry
		for(int i = 0; i < actualCombination.length; i++){
			serviceSequence = new ArrayList<Service>();
			service = serviceMatrix.get(i+1).get(actualCombination[i]-1);
			serviceSequence.add(service);
			combinationMatrix.put(i+1, serviceSequence);
		}
		calculateCombination(combinationMatrix);
	}
	
	public void calculateCombination(HashMap<Integer,ArrayList<Service>> combinationMatrix) {
		int timeResponse=0;
		float reliability=0f;
		
		timeResponse = calculateTime(combinationMatrix, FTT.RETRY);
		reliability = calculateReliability(combinationMatrix, FTT.RETRY);
		
		if(timeResponse <= composition.getResponseTime() && reliability >= composition.getReliability()){
			solution.addSolution(combinationMatrix);
		}
		totalCombinations++;
	}

	public int calculateTime(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt) {
		int result = 0;
		ArrayList<Service> serviceLevel;
		if(ftt == FTT.RETRY){
			Set<Integer> keys = combinationMatrix.keySet();
			for(int key: keys){
				serviceLevel = combinationMatrix.get(key);
				for(Service serv : serviceLevel){
					result += (serv.getTries()*serv.getTime()*(1/serv.getRel()));
				}
			}
		}
		System.out.println("time: "+result);
		return result;
	}

	public float calculateReliability(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt) {
		float result = 1f;
		int tries = 2;
		ArrayList<Service> serviceLevel;
		if(ftt == FTT.RETRY){
			Set<Integer> keys = combinationMatrix.keySet();
			for(int key: keys){
				serviceLevel = combinationMatrix.get(key);
				for(Service serv : serviceLevel){
					result *= (1 - Math.pow((1 - serv.getRel()),serv.getTries()));
				}
			}
		}
		System.out.println("rel: "+result);
		return result;
	}
	
	public void exportSolutions(FTT ftt) throws IOException{
		InputReader input = new InputReader();
		input.writeFile(solution,ftt);
	}
	
	public void exportResults(long timeSpent) throws IOException{
		InputReader input = new InputReader();
		input.writeResult(FTT.RETRY, composition, timeSpent, solution.size(), totalCombinations, maxDepth);
	}
	
	protected void printCombination(int[] actualCombination) {
		for(int i = 0; i < actualCombination.length; i++){
			System.out.print(actualCombination[i]);
		}
		System.out.println();
	}

	
	protected void printSolutions() {
		ArrayList<Service> serviceLevel;
		ArrayList<HashMap<Integer,ArrayList<Service>>> solutions;
		solutions = solution.getSolutionList();
		for(HashMap<Integer,ArrayList<Service>> map: solutions){
			Set<Integer> keys = map.keySet();
			for(int key: keys){
				serviceLevel = map.get(key);
				for(Service serv : serviceLevel){
					System.out.println(key+"_"+serv.getTime()+","+serv.getRel());
				}
			}
		}
	}

	@Override
	public void getCombinationServices() {
		// TODO Auto-generated method stub
		
	}
}
