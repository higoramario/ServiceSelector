package model;

import io.InputReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositionSolverNVersion extends CompositionSolver{

	private Composition composition;
	private Map<Integer,ArrayList<Service>> serviceMatrix;
	private Solution solution;
	private int maxDepth;
	
	private int compositionLevel;
	private int combination[][];
	private int servicesByLevel[];
	private int servicesByLevelCounter[];
	
	private int compositionTimeResponse;
	private float compositionReliability;
	
	private int totalCombinations = 0;
	
	public CompositionSolverNVersion(Composition comp){
		composition = comp;
		serviceMatrix = composition.getServiceMatrix();
		compositionTimeResponse = composition.getResponseTime();
		compositionReliability = composition.getReliability();
		compositionLevel = composition.getServiceMatrix().size();
		servicesByLevel = new int [compositionLevel];
		initCombination();
		initServicesByLevel();
		getMaximumDepth();
		combination = new int [compositionLevel][maxDepth];
		solution = new Solution();
	}

	protected void initCombination() {
		for(int i = 0;i<compositionLevel; i++){
			for(int j = 0;j<maxDepth; i++){
			combination[i][j]=1;
			}
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
	 * This method must be used to get strategy of a FTT.
	 * Call the method getCombinationServices
	 * */
	public void changeCombination() throws IOException{
		servicesByLevelCounter = getBinaryNumber();
		int numberCombination[] = new int[servicesByLevelCounter.length];
		numberCombination = getBinaryNumber();
		int counter;
		int arraySize = servicesByLevelCounter.length;
		
		counter = arraySize-1;
		
		while(numberCombination[0] > 0)
		{
			while(numberCombination[counter]>0)
			{
				System.out.println(String.valueOf(numberCombination[0])+String.valueOf(numberCombination[1]));//+String.valueOf(numberCombination[2])+String.valueOf(numberCombination[3])+String.valueOf(numberCombination[4]));
				getCombinationServices(convertToBinary(numberCombination));
				numberCombination[counter] -= 1; 
			}
			
			while(numberCombination[counter]==0){
				if(counter > 0){
					numberCombination[counter] = servicesByLevelCounter[counter];
					counter--;
					numberCombination[counter] -= 1;
				}
				else{
					numberCombination[counter] -= 1;
				}
			}
			counter = arraySize-1;
		}
		//printSolutions();
		exportSolutions(FTT.NVERSION);
		System.out.println("Number of solutions:" + solution.size());
		System.out.println("Total of configurations:" + totalCombinations);		
	}
	
	/*
	 * This method reads the serviceMatrix and extract each service composition combination
	 * Call the method calculateCombination
	 * */
	private void getCombinationServices(int[][] actualCombination) {
		HashMap<Integer,ArrayList<Service>> combinationMatrix = new HashMap<Integer,ArrayList<Service>>();
		ArrayList<Service> serviceSequence = new ArrayList<Service>();
		Service service = new Service();
		
		for(int i = 0; i < compositionLevel; i++){
			for(int j = 0; j < maxDepth; j++){
				if(actualCombination[i][j] == 1){
					service = serviceMatrix.get(i+1).get(j);
					serviceSequence.add(service);
				}
			}
			combinationMatrix.put(i+1, serviceSequence);
			serviceSequence = new ArrayList<Service>();
		}
		calculateCombination(combinationMatrix);
		
	}
	
	private int[] getBinaryNumber()
	{
		int binaryLevel[] = new int[servicesByLevel.length];
		
		for(int i = 0; i < servicesByLevel.length;i++){
			int num = servicesByLevel[i];
			binaryLevel[i] = (int)(Math.pow(2, num) - 1);
		}
		return binaryLevel;
	}
	
	
	private int[][] convertToBinary(int combinationDecimal[]){
		int combinationBinary[][] = new int[compositionLevel][maxDepth];
		String decimal; 
		for(int i = 0; i < combinationDecimal.length; i++){
			decimal = Integer.toBinaryString(combinationDecimal[i]);
			//System.out.println(decimal);
			int decimalSize = decimal.length();
			for(int k = servicesByLevel[i]; k > decimalSize; k--)
			{
				decimal = "0" + decimal;
			}
			for(int j = 0; j < decimal.length(); j++){
				combinationBinary[i][j] = Integer.valueOf(String.valueOf(decimal.charAt(j)));
			}
		}
		//printCombination(combinationBinary);
		return combinationBinary;
	}
	
	
	
	/*
	 * This method calculates and verifies which composition combination meet the composition QoS requirements
	 * Call the method calculateTime
	 * Compare the results and add to the solution
	 * */
	public void calculateCombination(HashMap<Integer,ArrayList<Service>> combinationMatrix){
		int timeResponse=0;
		timeResponse = calculateTime(combinationMatrix, FTT.NVERSION);
		
		if(timeResponse <= composition.getResponseTime()){
			solution.addSolution(combinationMatrix);
		}
		totalCombinations++;
	}

	/*
	 * This method calculates timeResponse for a FTT
	 * */
	public int calculateTime(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt){
		int result = 0;
		int timeLevel = 0;
		ArrayList<Service> serviceLevel;
		if(ftt == FTT.NVERSION){
			Set<Integer> keys = combinationMatrix.keySet();
			for(int key: keys){
				serviceLevel = combinationMatrix.get(key);
				for(Service serv : serviceLevel){
					if(timeLevel < serv.getTime()){
						timeLevel = serv.getTime();
					}
				}
				result += timeLevel;
				timeLevel = 0;
			}
		}
		System.out.println("tr: "+result);
		return result;
	}

	/*
	 * This method calculates reliability for a FTT
	 * */
	public float calculateReliability(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt){return 0f;}
	
	public void exportSolutions(FTT ftt) throws IOException{
		InputReader input = new InputReader();
		input.writeFile(solution,ftt);
	}
	
	public void exportResults(long timeSpent) throws IOException{
		InputReader input = new InputReader();
		input.writeResult(FTT.NVERSION, composition, timeSpent, solution.size(), totalCombinations, maxDepth);
	}
	
	protected void printCombination(int[] actualCombination){
		for(int i = 0; i < actualCombination.length; i++){
			System.out.print(actualCombination[i]);
		}
		System.out.println();
	}

	
	protected void printSolutions(){
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
