package com.simulator.ccn;

public class PITEntry {
	
	private int outgoingInterface;
	private double createdAtTime;
	
	PITEntry () {
		
		outgoingInterface = 0;
		createdAtTime = 0;		
	}
	
	PITEntry (int tempInt, double tempTime) {
		
		outgoingInterface = tempInt;
		createdAtTime = tempTime;		
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof PITEntry))
	        return false;
	    
	    PITEntry pn = (PITEntry)o;
	    return pn.outgoingInterface == outgoingInterface;
	}
	
	public int hashCode() {
	    return outgoingInterface;
	}
	
	
	public void setOutgoingInterface (int temp) {
		outgoingInterface = temp;
	}
	
	public void setCreatedAtTime (double temp) {
		createdAtTime = temp;
	}
	
	public int getoutgoingInterface () {
		return outgoingInterface;
	}
	
	public double getCreatedAtTime () {
		return createdAtTime;
	}
}
