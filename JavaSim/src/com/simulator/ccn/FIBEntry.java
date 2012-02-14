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
