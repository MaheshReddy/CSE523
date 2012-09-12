package com.simulator.ccn;


public class PITEntry {
	
	private int outgoingInterface;
	private int refPacketId;
	private double createdAtTime;
	private int primaryInterestID;
	private int numOfTimesExpired;
	
	public PITEntry () {
		
		outgoingInterface = -1;
		createdAtTime = -1;	
		refPacketId = -1;
		primaryInterestID = -1;		
		numOfTimesExpired = -1;
	}
	
	
	public PITEntry (int tempInt) {
		
		outgoingInterface = tempInt;
		createdAtTime = -1;	
		refPacketId = -1;
		primaryInterestID = -1;		
		numOfTimesExpired = -1;
	}
	
	public PITEntry (int tempInt, double tempTime) {
		
		outgoingInterface = tempInt;
		createdAtTime = tempTime;		
	}
	
	public PITEntry (int tempInt, int tempPacketId, int tempPrimaryInterestID, double tempTime, int tempNumOfTimesExpired) {
		
		outgoingInterface = tempInt;
		refPacketId = tempPacketId;
		primaryInterestID = tempPrimaryInterestID;
		createdAtTime = tempTime;		
		numOfTimesExpired = tempNumOfTimesExpired;
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof PITEntry))
	        return false;
	    
	    PITEntry pn = (PITEntry)o;
	    return pn.outgoingInterface == outgoingInterface; 
	    		//&& pn.refPacketId == refPacketId;
	}
	
	public int hashCode() {
		
		int hash = 7;
		hash = 31 * hash + outgoingInterface;
		hash = 31 * hash + refPacketId;
		
		long bits = Double.doubleToLongBits(createdAtTime);
		int var_code = (int)(bits ^ (bits >>> 32));		
		hash = 31 * hash + var_code;	
		
		return hash;	   
	}
	
	
	public void setRefPacketId (int temp) {
		refPacketId = temp;
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
	
	public int getRefPacketId () {
		return refPacketId;
	}
	
	public double getCreatedAtTime () {
		return createdAtTime;
	}

	public int getPrimaryInterestID() {
		return primaryInterestID;
	}

	public void setPrimaryInterestID(int primaryInterestID) {
		this.primaryInterestID = primaryInterestID;
	}

	public int getNumOfTimesExpired() {
		return numOfTimesExpired;
	}

	public void setNumOfTimesExpired(int numOfTimesExpired) {
		this.numOfTimesExpired = numOfTimesExpired;
	}			
}
