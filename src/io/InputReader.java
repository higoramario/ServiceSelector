package io;

import java.io.*;
import java.util.*;

import model.*;

import com.esotericsoftware.yamlbeans.*;

public class InputReader {

	
	public Map loadFile(String file) throws FileNotFoundException, YamlException{
		YamlReader yamlReader = new YamlReader(new FileReader(file));
		Map yamlData = (Map) yamlReader.read();
		return yamlData;
	}

	public void writeFile(Solution solution, FTT ftt) throws IOException {
		ArrayList<Service> serviceLevel;
		ArrayList<HashMap<Integer,ArrayList<Service>>> solutions;
		
		YamlWriter yamlWriter = new YamlWriter(new FileWriter("/home/higor/mac5910-experiments/output_"+ftt.toString()+"_"+System.currentTimeMillis()+"_"+".yaml"));
		yamlWriter.getConfig().setClassTag("solution", Solution.class);
		//yamlWriter.getConfig().setClassTag("service", Service.class);
		solutions = solution.getSolutionList();
		yamlWriter.write(solutions);
		/*for(HashMap<Integer,ArrayList<Service>> map: solutions){
			Set<Integer> keys = map.keySet();
			for(int key: keys){
				serviceLevel = map.get(key);
				for(Service serv : serviceLevel){
					yamlWriter.write(serv);
					//System.out.println(key+"_"+serv.getTime()+","+serv.getRel());
				}
			}
		}*/
		yamlWriter.close();
	}
	
	public void writeResult(FTT ftt, Composition comp, long timeSpent, int solutions, int totalCombinations, int maxDepth) throws IOException{
		Map<Integer,ArrayList<Service>> serviceMatrix;
		File result = new File("/home/higor/mac5910-experiments/"+comp.getServiceMatrix().size()+"x"+maxDepth+"_"+ftt.name()+"_"+System.currentTimeMillis());
		FileWriter fw = new FileWriter(result);
		String content = "";
		content += ftt.name()+"\n";
		content += "Response time: "+comp.getResponseTime()+"\n";
		content += "Reliability: "+comp.getReliability()+"\n";
		content += "Size: "+ comp.getServiceMatrix().size()+"x"+maxDepth+"\n";
		content += "Solutions: "+solutions+"\n";
		content += "Configurations: "+totalCombinations+"\n";
		content += "Time (ns): "+timeSpent+"\n\n";
		content += "Composition\n";
		serviceMatrix = comp.getServiceMatrix();
		
		for(int i = 1; i <= serviceMatrix.size(); i++){
			ArrayList<Service> services = serviceMatrix.get(i);
			content += "\nLevel "+i+"\n";
			for(int j = 0; j < services.size(); j++){
				if(ftt == FTT.RETRY){
					content += "Service "+(j+1)+" - time: "+services.get(j).getTime()+", rel: "+services.get(j).getRel()+", tries: "+services.get(j).getTries()+"\n";
				}
				else{
					content += "Service "+(j+1)+" - time: "+services.get(j).getTime()+", rel: "+services.get(j).getRel()+"\n";
				}
			}
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	}
	
}
