package com.simulator.ccn;

public class HistoryEntry {
	
	IDEntry interestID;
	int numberOfHops;
	
	public HistoryEntry(IDEntry interestID, int numberOfHops) {
		
		this.interestID = interestID;
		this.numberOfHops = numberOfHops;
	}
	
	public HistoryEntry() {
		
		this.interestID = null;
		this.numberOfHops = 0;
	}

	public IDEntry getInterestID() {
		return interestID;
	}

	public void setInterestID(IDEntry interestID) {
		this.interestID = interestID;
	}

	public int getNumberOfHops() {
		return numberOfHops;
	}

	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
	}
}
