package com.simulator.ccn;

public class InterestEntry {
	
	private int interestID;
	private int segmentID;
	
	InterestEntry(){
		
		interestID = 0;
		segmentID = 0;
	}
	
	InterestEntry(int tempIntID, int tempSegID){
		
		interestID = tempIntID;
		segmentID = tempSegID;
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof InterestEntry))
	        return false;
	    
	    InterestEntry pn = (InterestEntry)o;
	    return pn.interestID == interestID &&
	           pn.segmentID == segmentID;
	}
	
	public int hashCode() {
	    return interestID;
	}
	
	public void setSegmentID (int temp) {
		segmentID  = temp;
	}	
	
	public void setInterestID (int temp) {
		interestID  = temp;
	}
	
	public int getInterestID () {
		return interestID;
	}
	
	public int getSegmentID () {
		return segmentID;
	}
}
