package com.simulator.ccn;
/* This class creates the TimeOutFields object which are stored in the TimeOutQueue,
 * and play a vital role in the retransmission of interest packets */

public class TimeOutFields {
	
	/* Global interest packet value, which remains
	 * same throughout multiple retransmissions of
	 * the interest packet */
	private int primaryInterestID;
	
	private int interestID;
	private int segmentID;
	private int objectID;
	private int nodeID;
	
	/* The number of times the interest
	 * packet has timed out*/
	private int expirationCount;
	
	/* The simulation time at which the interest
	 * packet will expire. It is an absolute 
	 * value */
	private double timeOutValue;
	
	/* The variable that is checked to see whether
	 * the object corresponding to the interest
	 * packet has been received */
	private boolean receivedDataObject;
	
	public TimeOutFields(){
		
		primaryInterestID = 0;
		interestID = 0;
		segmentID = 0;
		objectID = -1;
		nodeID = -1;
		expirationCount = -1;
		timeOutValue = -1.0;
		receivedDataObject = false;
	}
	
	public TimeOutFields(int tempPrimaryIntID, int tempIntID, int tempSegID, int tempObjectID, int tempNodeID, int tempExpirationCount, double tempTimeOutValue, boolean tempReceivedDataObject){
		
		primaryInterestID = tempPrimaryIntID;
		interestID = tempIntID;
		segmentID = tempSegID;
		objectID = tempObjectID;
		nodeID = tempNodeID;
		expirationCount = tempExpirationCount;
		timeOutValue = tempTimeOutValue;
		receivedDataObject = tempReceivedDataObject;
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof TimeOutFields))
	        return false;
	    
	    TimeOutFields pn = (TimeOutFields)o;
	    return pn.interestID == interestID &&
	           pn.segmentID == segmentID;
	}
	
	public int hashCode() {
		
		int hash = 7;
		
		hash = 31 * hash + interestID;
		hash = 31 * hash + segmentID;
		
		return hash;
	}	
	
	public int getPrimaryInterestID() {
		return primaryInterestID;
	}

	public void setPrimaryInterestID(int primaryInterestID) {
		this.primaryInterestID = primaryInterestID;
	}

	public void setInterestID (int temp) {
		interestID  = temp;
	}
	
	public int getInterestID () {
		return interestID;
	}
	
	public void setSegmentID (int temp) {
		segmentID  = temp;
	}	
	
	public int getSegmentID () {
		return segmentID;
	}
	
	public void setObjectID (int temp) {
		objectID  = temp;
	}	
	
	public int getObjectID () {
		return objectID;
	}
	
	public void setNodeID (int temp) {
		nodeID  = temp;
	}
	
	public int getNodeID () {
		return nodeID;
	}
	
	public void setTimeOutValue (double temp) {
		timeOutValue  = temp;
	}
	
	public double getTimeOutValue () {
		return timeOutValue;
	}
	
	public void setReceivedDataObject (boolean temp) {
		receivedDataObject  = temp;
	}
	
	public boolean getReceivedDataObject () {
		return receivedDataObject;
	}

	public int getExpirationCount() {
		return expirationCount;
	}

	public void setExpirationCount(int expirationCount) {
		this.expirationCount = expirationCount;
	}
}
