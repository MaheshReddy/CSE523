package com.simulator.topology;
import java.util.*;

/**/
public class Node {
	
	private int nodeID;
	private float xPos;
	private float yPos;
	private int indegree;
	private int outdegree;
	private int asID;
	private ArrayList <Edge> routingTable;
	
	Node (String nodeConfig) {
		
		Scanner scanNodeConfig = new Scanner (nodeConfig);
		
		nodeID = scanNodeConfig.nextInt();
		xPos = scanNodeConfig.nextFloat();
		yPos = scanNodeConfig.nextFloat();
		indegree = scanNodeConfig.nextInt();
		outdegree = scanNodeConfig.nextInt();
		asID = scanNodeConfig.nextInt();	
		routingTable = null;
	}	
	
	public void setRoutingTable (ArrayList <Edge> table){
		routingTable = table;
	}
	
	public ArrayList <Edge> getRoutingTable (){
		return routingTable;
	}
	
	public void contentsOfNode ()	{
		System.out.println("Node ID: " + nodeID);
		System.out.println("X-axis Position: " + xPos);
		System.out.println("Y-axis Position: " + yPos);
		System.out.println("Indegree: " + indegree);
		System.out.println("Outdegree: " + outdegree);
		System.out.println("ASID: " + asID);
		
		int loop;
		
		System.out.println("Routing Table for Node " + nodeID);
		
		for (loop = 0; loop < routingTable.size(); loop++) {			 
			System.out.println("" + (loop + 1) + ". Edge To: Node "  + (routingTable.get(loop)).getEdgeTo());
		}		
	}
	
	public int getNodeID (){
		return nodeID;
	}
	
	public float getXPos (){
		return xPos;
	}
	
	public float getYPos (){
		return yPos;
	}
	
	public int getIndegree (){
		return indegree;
	}
	
	public int getOutdegree (){
		return outdegree;
	}
	
	public int getAsID () {
		return asID;
	}	
}
