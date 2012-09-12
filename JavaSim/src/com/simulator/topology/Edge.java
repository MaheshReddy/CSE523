package com.simulator.topology;

import java.util.*;

/**/

public class Edge {
	
	private int edgeID;
	private int edgeFrom;
	private int edgeTo;
	private float length;
	private float delay;
	private float bandwidth;
	private int asIDFrom;
	private int asIDTo;
	
	public Edge (String edgeConfig) {
		
		Scanner scanNodeConfig = new Scanner (edgeConfig);
		
		edgeID = scanNodeConfig.nextInt();
		edgeFrom = scanNodeConfig.nextInt();
		edgeTo = scanNodeConfig.nextInt();
		length = scanNodeConfig.nextFloat();
		delay = scanNodeConfig.nextFloat();
		bandwidth = scanNodeConfig.nextFloat();
		asIDFrom = scanNodeConfig.nextInt();
		asIDTo = scanNodeConfig.nextInt();
	}
	
	public Edge (Edge duplicateEdge) {
		
		edgeID = duplicateEdge.getEdgeID();
		edgeFrom = duplicateEdge.getEdgeTo();
		edgeTo = duplicateEdge.getEdgeFrom();
		length = duplicateEdge.getLength();
		delay = duplicateEdge.getDelay();
		bandwidth = duplicateEdge.getBandwidth();
		asIDFrom = duplicateEdge.getAsIDFrom();
		asIDTo = duplicateEdge.getAsIDTo();
	}
	
	public void contentsOfEdge ()	{
		System.out.println("Edge ID: " + edgeID);
		System.out.println("Edge From: " + edgeFrom);
		System.out.println("Edge To: " + edgeTo);
		System.out.println("Length: " + length);
		System.out.println("Delay: " + delay);
		System.out.println("Bandwidth: " + bandwidth);
		System.out.println("ASID From: " + asIDFrom);
		System.out.println("ASID To: " + asIDTo);
	}
	
	public int getEdgeID (){
		return edgeID;
	}
	
	public int getEdgeFrom (){
		return edgeFrom;
	}
	
	public int getEdgeTo (){
		return edgeTo;
	}
	
	public float getLength() {
		return length;
	}
	
	public float getDelay() {
		return delay;
	}
	
	public float getBandwidth() {
		return bandwidth;
	}
	
	public int getAsIDFrom (){
		return asIDFrom;
	}
	
	public int getAsIDTo (){
		return asIDTo;
	}
	
	

}
