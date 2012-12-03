package com.simulator.ccn;


public class PITEntry {
	
	private int outgoingInterface;
	private int refPacketId;
	private double createdAtTime;
	private double expirationTime;
	private int primaryInterestID;
	private int numOfTimesExpired;
	private double interestCreatedAt;
	private double interestTimeoutAt;
	private double interestProcessingDelayAtNode;
	private double interestProcessingDelaySoFar;	
	private double interestTransmissionDelaySoFar;
	
	public PITEntry () {
		
		outgoingInterface = -1;
		createdAtTime = -1;	
		expirationTime = -1;
		refPacketId = -1;
		primaryInterestID = -1;		
		numOfTimesExpired = -1;
		interestCreatedAt = -1;
		interestTimeoutAt = -1;
		interestProcessingDelayAtNode = -1;
		interestProcessingDelaySoFar = -1;
		interestTransmissionDelaySoFar = -1;
		
	}
	
	
	public PITEntry (int tempInt) {
		
		outgoingInterface = tempInt;
		createdAtTime = -1;	
		expirationTime = -1;
		refPacketId = -1;
		primaryInterestID = -1;		
		numOfTimesExpired = -1;
		interestCreatedAt = -1;
		interestTimeoutAt = -1;
		interestProcessingDelayAtNode = -1;
		interestProcessingDelaySoFar = -1;
		interestTransmissionDelaySoFar = -1;
	}
	
	public PITEntry (int tempInt, double tempTime) {
		
		outgoingInterface = tempInt;
		createdAtTime = tempTime;		
	}
	
	public PITEntry (int tempInt, int tempPacketId, int tempPrimaryInterestID, double tempTime, double tempExpirationTime, 
			int tempNumOfTimesExpired, double tempInterestCreatedAt, double tempInterestTimeoutAt, double tempInterestProcessingDelayAtNode,
			double tempInterestProcessingDelaySoFar, double tempInterestTransmissionDelaySoFar) {
		
		outgoingInterface = tempInt;
		refPacketId = tempPacketId;
		primaryInterestID = tempPrimaryInterestID;
		createdAtTime = tempTime;
		expirationTime = tempExpirationTime;
		numOfTimesExpired = tempNumOfTimesExpired;
		interestCreatedAt = tempInterestCreatedAt;
		interestTimeoutAt = tempInterestTimeoutAt;
		interestProcessingDelayAtNode = tempInterestProcessingDelayAtNode;
		interestProcessingDelaySoFar = tempInterestProcessingDelaySoFar;
		interestTransmissionDelaySoFar = tempInterestTransmissionDelaySoFar;
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


	public double getExpirationTime() {
		return expirationTime;
	}


	public void setExpirationTime(double expirationTime) {
		this.expirationTime = expirationTime;
	}


	public double getInterestCreatedAt() {
		return interestCreatedAt;
	}


	public void setInterestCreatedAt(double interestCreatedAt) {
		this.interestCreatedAt = interestCreatedAt;
	}


	public double getInterestTimeoutAt() {
		return interestTimeoutAt;
	}


	public void setInterestTimeoutAt(double interestTimeoutAt) {
		this.interestTimeoutAt = interestTimeoutAt;
	}


	public double getInterestProcessingDelayAtNode() {
		return interestProcessingDelayAtNode;
	}


	public void setInterestProcessingDelayAtNode(
			double interestProcessingDelayAtNode) {
		this.interestProcessingDelayAtNode = interestProcessingDelayAtNode;
	}


	public double getInterestProcessingDelaySoFar() {
		return interestProcessingDelaySoFar;
	}


	public void setInterestProcessingDelaySoFar(double interestProcessingDelaySoFar) {
		this.interestProcessingDelaySoFar = interestProcessingDelaySoFar;
	}


	public double getInterestTransmissionDelaySoFar() {
		return interestTransmissionDelaySoFar;
	}


	public void setInterestTransmissionDelaySoFar(
			double interestTransmissionDelaySoFar) {
		this.interestTransmissionDelaySoFar = interestTransmissionDelaySoFar;
	}			
}
