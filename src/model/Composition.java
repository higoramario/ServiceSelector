package model;

import java.util.*;

public class Composition {
	
	private FTT ftt;
	private int responseTime;
	private float reliability;
	protected Map<Integer,ArrayList<Service>> serviceMatrix;
	
	public Composition(){
		serviceMatrix = new HashMap<Integer, ArrayList<Service>>();
	}
	
	public int getResponseTime() {
		return responseTime;
	}

	public float getReliability() {
		return reliability;
	}

	public Map<Integer, ArrayList<Service>> getServiceMatrix() {
		return serviceMatrix;
	}

	public void setResponseTime(int responseTime) {
		this.responseTime = responseTime;
	}

	public void setReliability(float reliability) {
		this.reliability = reliability;
	}

	public void addServiceMatrix(int key, Service serv) {
		ArrayList<Service> listTempService = new ArrayList<Service>();
		if(serviceMatrix.containsKey(key)){
			listTempService = serviceMatrix.get(key);
			
		}
		listTempService.add(serv);
		serviceMatrix.put(key, listTempService);
	}
}
