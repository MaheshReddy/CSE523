package com.simulator.topology;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import com.simulator.ccn.CCNRouter;
import com.simulator.enums.GridTypes;

public class Grid 
{
	static CCNRouter[] routerList ;	

	/* Represents an edge */
	private static Map<Integer,LinkedHashSet<HashMap<Integer,Integer>>> graph = new HashMap<Integer,LinkedHashSet<HashMap<Integer,Integer>>>(); 
    private static Integer gridSize = 0;

	private static void addEdge(Integer node1,Integer node2,Integer weight)
	{
		LinkedHashSet<HashMap<Integer,Integer>>  adjacencyList = graph.get(node1);
		HashMap<Integer,Integer> innerHash = new HashMap<Integer,Integer>();
		if(adjacencyList == null)
		{
			adjacencyList = new LinkedHashSet<HashMap<Integer,Integer>>();
			graph.put(node1, adjacencyList);
		}
		innerHash.put(node2, weight);
		adjacencyList.add(innerHash);
	}

	public static void createTopology (GridTypes gridType) throws Exception
	{
		if (GridTypes.SIMULATION_GRID_MESH == gridType)
			createMeshGrid();
		else if (GridTypes.SIMULATION_GRID_BRITE == gridType) {
			createBriteTopology();
		}
		else
		{
			System.out.println("Illegal Grid Type:"+gridType);
			throw new Exception();
		}
	}
	
	public static boolean isConnected(Integer node1, Integer node2) {
		LinkedHashSet<HashMap<Integer, Integer>> adjacent = graph.get(node1);
		HashMap<Integer,Integer> searchMap = new HashMap<Integer,Integer>();
		searchMap.put(node2, 1);
		if(adjacent==null) {
			return false;
		}
		return adjacent.contains(searchMap);
	}

	public static LinkedHashSet<HashMap<Integer,Integer>> getAdjacencyList(Integer node1)
	{
		LinkedHashSet<HashMap<Integer,Integer>> adjacency = graph.get(node1);
		if(adjacency == null)
			return new  LinkedHashSet<HashMap<Integer,Integer>>();
		else 
			return adjacency;
	}
	
	/* Create the topology from Brite configuration files */
	static public void createBriteTopology() {
		
		ReadBriteFile readToplogyBrite = new ReadBriteFile ("First.brite");
		
		/* The number of nodes from the configuration file is used to to set the Grid size. I am not using the 
		 * "resources" file as I do not want to do text manipulation. I hope the resource file will not 
		 * overwrite this method by making another call */
		setGridSize((readToplogyBrite.getAllNodes()).length); 
		System.out.println ("Grid Size: " + getGridSize());
		
		routerList = new CCNRouter[getGridSize()];	
		
		/* This will read in the nodes into an array of Node objects, which contain everything pertaining to a 
		 * node from the topology configuration file 
		 * */
		
		Node[] nodes = readToplogyBrite.getAllNodes(); 
		
		for(int i = 0; i < getGridSize(); i++)
		{
			routerList[i] = new CCNRouter(i);
			System.out.println("Routing Table for Node " + i);
			for (int j = 0; j < (nodes[i].getRoutingTable()).size(); j++) {
				/* First parameter: Node (Whose routing table is being formed
				 * Second parameter: Neighboring node 
				 * Third parameter: Weight
				 * Eventually, we will replace this parameter with an Edge object
				 * */
				addEdge(i,((nodes[i].getRoutingTable()).get(j)).getEdgeTo(),1);
				System.out.println ("" + (j + 1) + ". Edge To: Neighboring Node "  + ((nodes[i].getRoutingTable()).get(j)).getEdgeTo());
			}			
		}		
		contentsOfGrid();
	}
	
	static public void createMeshGrid()
	{
		routerList = new CCNRouter[getGridSize()];
		for(int i=0;i<getGridSize();i++)
		{
			routerList[i]=new CCNRouter(i);
			for(int j=0;j<getGridSize();j++)
				if(i!=j)
					addEdge(i,j,1);
		}
	}

	public static Integer getGridSize() {
		return gridSize;
	}

	public static void setGridSize(Integer gridSize) {
		Grid.gridSize = gridSize;
	}

	public static CCNRouter getRouter(int id) {
		return routerList[id];
	}
	
	/* We need this function to print the all the adjacency lists of all the nodes to verify if the topolgy
	 * matches that of the topology configuration file
	 * 	 * */
	public static void contentsOfGrid() {
		
		System.out.println("Grid Configuration");
		for(int i=0;i<getGridSize();i++)
		{
			System.out.println("Printing Routing Informatin of Router"+i+":");
			LinkedHashSet<HashMap<Integer,Integer>> adj = getAdjacencyList(i);
			Iterator<HashMap<Integer,Integer>> itr = adj.iterator();
			while(itr.hasNext())
				System.out.println("Edge To: Neighboring Node"+itr.next());
		}		
	}
}
