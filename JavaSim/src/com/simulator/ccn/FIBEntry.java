package com.simulator.ccn;

public class FIBEntry {
	
	private int destinationNode;
	private int hops;

	FIBEntry () {
		
		destinationNode = 0;
		hops = 0;		
	}
	
	FIBEntry (int tempDestNode, int tempHops) {
		
		destinationNode = tempDestNode;
		hops = tempHops;		
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof FIBEntry))
	        return false;
	    
	    FIBEntry pn = (FIBEntry)o;	    
	    return pn.destinationNode == destinationNode;
	}
	
	public int hashCode() {
	    return destinationNode;
	}
	
	public void setDestinationNode (int temp) {
		destinationNode = temp;
	}
	
	public void setHops (int temp) {
		hops = temp;
	}
	
	public int getDestinationNode () {
		return destinationNode;
	}
	
	public int getHops () {
		return hops;
	}
}
