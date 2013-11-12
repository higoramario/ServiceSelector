package model;

import io.InputReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CompositionSolverRecBlock extends CompositionSolver{

	Composition composition;
	private Map<Integer,ArrayList<Service>> serviceMatrix;
	private Solution solution;
	private int maxDepth;
	
	private int compositionLevel;
	private int servicesByLevel[];
	private int combination[][];
	
	private int servicesByLevelCounter[];
	int number;
	
	private int compositionTimeResponse;
	private float compositionReliability;
	
	private int totalCombinations = 0;
	
	public CompositionSolverRecBlock(Composition comp){
		//super(comp);
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
		exportSolutions(FTT.RBLOCK);
		System.out.println("Number of solutions:" + solution.size());
		System.out.println("Total of configurations:" + totalCombinations);		
	}
	
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
	
	void printCombination()
	{
		for(int i = 0;i<compositionLevel; i++){
			for(int j = 0;j<maxDepth; j++){
				System.out.print(combination[i][j]+"|");
			}
			System.out.println();
		}
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
	
	
	public void calculateCombination(HashMap<Integer, ArrayList<Service>> combinationMatrix) {
		int timeResponse=0;
		float reliability=0f;
		
		timeResponse = calculateTime(combinationMatrix, FTT.RBLOCK);
		reliability = calculateReliability(combinationMatrix, FTT.RBLOCK);
		
		if(timeResponse <= composition.getResponseTime() && reliability >= composition.getReliability()){
			solution.addSolution(combinationMatrix);
		}
		totalCombinations++;
	}
	
	public int calculateTime(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt) {
		int result = 0;
		int timeLevel = 0;
		ArrayList<Service> serviceLevel;
		if(ftt == FTT.RBLOCK){
			Set<Integer> keys = combinationMatrix.keySet();
			for(int key: keys){
				serviceLevel = combinationMatrix.get(key);
				for(Service serv : serviceLevel){
					if(timeLevel < serv.getTime()){
						timeLevel += serv.getTime();
					}
				}
				result += timeLevel;
				timeLevel = 0;
			}
		}
		System.out.println("tr: "+result);
		return result;
	}

	public float calculateReliability(HashMap<Integer, ArrayList<Service>> combinationMatrix, FTT ftt) {
		float result = 1f;
		float resultTemp = 1f;
		ArrayList<Service> serviceLevel;
		if(ftt == FTT.RBLOCK){
			Set<Integer> keys = combinationMatrix.keySet();
			for(int key: keys){
				serviceLevel = combinationMatrix.get(key);
				resultTemp = 1f;
				for(Service serv : serviceLevel){
					resultTemp *= (1 - serv.getRel());
				}
				result *= (1 - resultTemp);
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
		input.writeResult(FTT.RBLOCK, composition, timeSpent, solution.size(), totalCombinations, maxDepth);
	}
	
	protected void printCombination(int[][] actualCombination) {
		for(int i = 0; i < compositionLevel; i++){
			for(int j = 0; j < maxDepth; j++){
				System.out.print(actualCombination[i][j]);
			}
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

	@Override
	protected void printCombination(int[] actualCombination) {
		// TODO Auto-generated method stub
		
	}
	
	
	///OLD SOLUTION
	
	/*
	 * Initialize arrays with the value 1 to start combinations and levels of counters 
	 * */
	private int[] initWithValueOne(int size){
		int cLevel[] = new int[size];
		for(int i = 0; i < cLevel.length; i++){
			cLevel[i] = 1;
		}
		return cLevel;
	}
	
	public void oldChangeCombination() throws IOException{
		int compLevel = compositionLevel;
		int countLevel[] = initWithValueOne(compositionLevel-1);
		int combinationRecBlock[] = initWithValueOne(compositionLevel);
		boolean pause = false; //avoid repeating combinations 
		int counter = 0;
		
		while(countLevel[0] < Math.pow(2,servicesByLevel[0]) && compLevel > 0){
			//if(!pause){
			if(counter == 0){
				for(int i = 1; i <= (Math.pow(2,servicesByLevel[compLevel-1])-1);i++){
					combinationRecBlock[compLevel-1] = i;
					System.out.println(String.valueOf(combinationRecBlock[0])+String.valueOf(combinationRecBlock[1])+String.valueOf(combinationRecBlock[2])+String.valueOf(combinationRecBlock[3])+String.valueOf(combinationRecBlock[4]));
					getCombinationServices(convertToBinary(combinationRecBlock));
				}	
			}
			else{
				counter--;
			//	pause = false;
			}
			compLevel--;
			
			while(compLevel > 0){
				if(countLevel[compLevel-1] < (Math.pow(2,servicesByLevel[compLevel-1])-1)){
					countLevel[compLevel-1] += 1;
					combinationRecBlock[compLevel-1] = countLevel[compLevel-1];
					compLevel = compositionLevel;
					break;
				}
				else{
					if(compLevel==4)counter = 1;
					if(compLevel==3)counter = 4;
					if(compLevel==2)counter = 4;
					combinationRecBlock[compLevel-1] = 1;
					countLevel[compLevel-1] = 0;
					compLevel--;
					//pause = true;
				}
			}
			
		}
		//printSolutions();
		exportSolutions(FTT.RBLOCK);
		System.out.println("Number of solutions:" + solution.size());
	}
	
}
