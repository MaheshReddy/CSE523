package com.mahesh.cse523.main;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Grid 
{
	static CCNRouter[] routerList ;
	

	//static int[][] adjacencyList;
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

	/*public  Grid (SimulationTypes gridType,int size)
	{
		gridSize = size;
		routerList = new CCNRouter[size];
		if (SimulationTypes.SIMULATION_GRID_MESH == gridType)
			createMeshGrid(size);
	}*/


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
	
	static public void createMeshGrid(int size)
	{
		gridSize = size;
		routerList = new CCNRouter[size];
		for(int i=0;i<size;i++)
		{
			routerList[i]=new CCNRouter(i);
			for(int j=0;j<size;j++)
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
}
