package com.simulator.ccn;

public class IDEntry {
	
	private int id;
	private int segmentID;
	
	IDEntry(){
		
		id = 0;
		segmentID = 0;
	}
	
	public IDEntry(int tempIntID, int tempSegID){
		
		id = tempIntID;
		segmentID = tempSegID;
	}
	
	IDEntry(IDEntry tempIDEntry){
		
		id = tempIDEntry.getID();
		segmentID = tempIDEntry.getSegmentID();
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof IDEntry))
	        return false;
	    
	    IDEntry pn = (IDEntry)o;
	    return pn.id == id &&
	           pn.segmentID == segmentID;
	}
	
	public int hashCode() {
		
		int hash = 7;
		
		hash = 31 * hash + id;
		hash = 31 * hash + segmentID;
		
		return hash;
	}
	
	public void setSegmentID (int temp) {
		segmentID  = temp;
	}	
	
	public void setID (int temp) {
		id  = temp;
	}
	
	public int getID () {
		return id;
	}
	
	public int getSegmentID () {
		return segmentID;
	}
}
