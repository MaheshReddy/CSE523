package com.simulator.trace;

public class TraceRecord {
	
	String packetType = null;
	String packetStatus;
	float timeStamp;
	int packetID;
	int sourceNode;
	int currentNode;
	int previousNode;
	int requestedObjectID;
	String causeOfSuprression = null;
	int numOfHops;
	String localOrGlobalCache = null;
	String deadOrAlive = null;


	TraceRecord () {
		
		String packetType = null;
		String packetStatus = null;
		float timeStamp = 0;
		int packetID = 0;
		int sourceNode = 0;
		int currentNode = 0;
		int previousNode = 0;
		int requestedObjectID = 0;
		String causeOfSuprression = null;
		int numOfHops = 0;
		String localOrGlobalCache = null;
		String deadOrAlive = null;
	}
	
	void printTraceRecord (){
		System.out.println ("PKTType:" + this.getPacketType() + " PCKSTATUS:" + this.getPacketStatus() + " TIMESTMP:" + this.getTimeStamp() + " PKTID:" + this.getPacketID() + " OBJID/PKTID:" + this.getRequestedObjectID() + " SRCND:" + this.getSourceNode() + " PREVNDE:" + this.getPreviousNode() + " CURRNDE:" + this.getCurrentNode() + " HOPS:" + this.getNumOfHops() + " " + this.getCauseOfSuprression() + " " + this.getLocalOrGlobalCache() + " " + this.getDeadOrAlive());
	}
	
	public String toString(){
		return "PKTType:" + this.getPacketType() + " PCKSTATUS:" + this.getPacketStatus() + " TIMESTMP:" + this.getTimeStamp() + " PKTID:" + this.getPacketID() + " OBJID/PKTID:" + this.getRequestedObjectID() + " SRCND:" + this.getSourceNode() + " PREVNDE:" + this.getPreviousNode() + " CURRNDE:" + this.getCurrentNode() + " HOPS:" + this.getNumOfHops() + " " + this.getCauseOfSuprression() + " " + this.getLocalOrGlobalCache() + " " + this.getDeadOrAlive() + "\n";
	}
	
	public String toVerifyShortestPath(){
		return " TIMESTMP:" + " PCKSTATUS:" + this.getPacketStatus() + this.getTimeStamp() + " PKTID:" + this.getPacketID() + " OBJID/PKTID:" + this.getRequestedObjectID() + " SRCND:" + this.getSourceNode() + " CURRNDE:" + this.getCurrentNode() + " HOPS:(" + this.getNumOfHops();
	}

	void setPacketType (String temp) {		
		packetType = temp;
	}
	
	void setPacketStatus (String temp) {		
		packetStatus = temp;
	}
	
	void setTimeStamp (float temp) {		
		timeStamp = temp;
	}
	
	void setPacketID (int temp) {		
		packetID = temp;
	}
	
	void setSourceNode (int temp) {		
		sourceNode = temp;
	}
	
	void setCurrentNode (int temp) {		
		currentNode = temp;
	}
	
	void setPreviousNode (int temp) {		
		previousNode = temp;
	}
	
	void setRequestedObjectID (int temp) {		
		requestedObjectID = temp;
	}
	
	void setCauseOfSuprression (String temp) {		
		causeOfSuprression = temp;
	}
		
	void setNumOfHops (int temp) {		
		numOfHops = temp;
	}
	
	void setLocalOrGlobalCache (String temp) {		
		localOrGlobalCache = temp;
	}
	
	void setDeadOrAlive (String temp) {		
		deadOrAlive = temp;
	}

	String getPacketType () {		
		return packetType;
	}
	
	String getPacketStatus () {		
		return packetStatus;
	}	
	
	float getTimeStamp () {		
		return timeStamp;
	}
	
	int getPacketID () {		
		return packetID;
	}
	
	int getSourceNode () {		
		return sourceNode;
	}
	
	int getCurrentNode () {		
		return currentNode;
	}
	
	int getPreviousNode () {		
		return previousNode;
	}
	
	int getRequestedObjectID () {		
		return requestedObjectID;
	}
	
	String getCauseOfSuprression () {		
		return causeOfSuprression;
	}
		
	int getNumOfHops () { 		
		return numOfHops;
	}
	
	String getLocalOrGlobalCache () {		
		return localOrGlobalCache;
	}
	
	String getDeadOrAlive () {		
		return deadOrAlive;
	}

}