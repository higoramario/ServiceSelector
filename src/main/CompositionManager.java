package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import io.*;
import model.*;

public class CompositionManager {
	
	private  int serviceLevel;
	private Composition composition;
	
	public CompositionManager(){
		composition = new Composition();
	}
	
	public Composition load(String fileServices, String fileComposition) throws FileNotFoundException, YamlException{
		
		InputReader inputReader = new InputReader();
		Map yamlDataServices = inputReader.loadFile(fileServices);
		Map yamlDataComposition = inputReader.loadFile(fileComposition);
		
		serviceLevel = yamlDataServices.size();
		composition.setResponseTime(Integer.parseInt((String)yamlDataComposition.get("responseTime")));
		composition.setReliability(Float.parseFloat((String)yamlDataComposition.get("reliability")));
		
		for(int i = 1; i <= serviceLevel; i++){
			if(yamlDataServices.containsKey(String.valueOf(i))){
				ArrayList servicesFromLevel = (ArrayList) yamlDataServices.get(String.valueOf(i));
				for(Object objService: servicesFromLevel){
					HashMap<String,String> hshService = (HashMap<String,String>) objService;
					if(hshService.containsKey("tries")){
						Service serv = new Service(Integer.parseInt(hshService.get("time")),Float.parseFloat(hshService.get("rel")),Integer.parseInt(hshService.get("tries")));
						composition.addServiceMatrix(i,serv);
					}
					else{
						Service serv = new Service(Integer.parseInt(hshService.get("time")),Float.parseFloat(hshService.get("rel")));
						composition.addServiceMatrix(i,serv);
					}
				}
			}
		}
		
		System.out.println(composition.getResponseTime());
		System.out.println(composition.getServiceMatrix().size());
		System.out.println(composition.getServiceMatrix().get(2).get(0).getRel());
		
		return composition;
		
	}
	
	public static void main(String args[]) throws FileNotFoundException, YamlException, IOException
	{
		
		long startTime = System.nanoTime();//.currentTimeMillis();
		
		CompositionManager manager = new CompositionManager();
		Composition composition;
		composition = manager.load("/home/higor/Ubuntu One/doctorate/MAC5910/paper/files/01-stock_exchange_.yaml","/home/higor/Ubuntu One/doctorate/MAC5910/paper/files/01-stock_exchange_comp.yaml");
		
		//CompositionSolverRetry solver = new CompositionSolverRetry(composition);
		//CompositionSolverRecBlock solver = new CompositionSolverRecBlock(composition);
		//CompositionSolverNVersion solver = new CompositionSolverNVersion(composition);
		CompositionSolverActive solver = new CompositionSolverActive(composition);
		
		solver.changeCombination();
		
		long totalTime = System.nanoTime() - startTime;//.currentTimeMillis()
		
		solver.exportResults(totalTime);
		
		System.out.println("Time spent (ns): "+totalTime);
		
		
	}
	
	
}
