package com.simulator.djikstra;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.simulator.topology.Node;
import com.simulator.topology.ReadBriteFile;

public class Djikstra {

	private static Map<Integer, LinkedHashSet<HashMap<Integer, Integer>>> graph = new HashMap<Integer, LinkedHashSet<HashMap<Integer, Integer>>>();
	private static Integer gridSize = 0;
	private static int shortestPathTable [][] = null;

	public static Integer getGridSize() {
		return gridSize;
	}

	public static void setGridSize(Integer gridSize) {
		Djikstra.gridSize = gridSize;
	}

	public static int[][] getShortestPathTable() {
		return shortestPathTable;
	}

	public static void setShortestPathTable(int[][] shortestPathTable) {
		Djikstra.shortestPathTable = shortestPathTable;
	}

	private static void addEdge(Integer node1, Integer node2, Integer weight) {
		LinkedHashSet<HashMap<Integer, Integer>> adjacencyList = graph
				.get(node1);
		HashMap<Integer, Integer> innerHash = new HashMap<Integer, Integer>();
		if (adjacencyList == null) {
			adjacencyList = new LinkedHashSet<HashMap<Integer, Integer>>();
			graph.put(node1, adjacencyList);
		}
		innerHash.put(node2, weight);
		adjacencyList.add(innerHash);
	}

	public static LinkedHashSet<HashMap<Integer, Integer>> getAdjacencyList(
			Integer node1) {
		LinkedHashSet<HashMap<Integer, Integer>> adjacency = graph.get(node1);
		if (adjacency == null)
			return new LinkedHashSet<HashMap<Integer, Integer>>();
		else
			return adjacency;
	}

	public static void computePaths(Vertex source) {
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();

			// Visit each edge exiting u
			for (Edge e : u.adjacencies) {
				Vertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					vertexQueue.remove(v);

					v.minDistance = distanceThroughU;
					v.previous = u;
					vertexQueue.add(v);
				}
			}
		}
	}
	
	public static List<Vertex> getShortestPathTo(Vertex target)
	{
	    List<Vertex> path = new ArrayList<Vertex>();
	    for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
	        path.add(vertex);

	    Collections.reverse(path);
	    return path;
	}

	public static void computeShortestPaths() {

		ReadBriteFile readToplogyBrite = new ReadBriteFile("First.brite");

		setGridSize((readToplogyBrite.getAllNodes()).length);
		System.out.println("Grid Size: " + getGridSize());

		Node[] nodes = readToplogyBrite.getAllNodes();
		List<Vertex> vertexList = new ArrayList<Vertex>();
		int[][] pathTable = new int[getGridSize()][getGridSize()];
		Vertex v = null;
		Edge e = null;

		for (int i = 0; i < getGridSize(); i++) {
			vertexList.add(new Vertex(i));
			for (int j = 0; j < (nodes[i].getRoutingTable()).size(); j++) {
				addEdge(i, ((nodes[i].getRoutingTable()).get(j)).getEdgeTo(), 1);
			}
		}

		for (int i = 0; i < getGridSize(); i++) {
			LinkedHashSet<HashMap<Integer, Integer>> adjacencyList = graph.get(i);
			v = vertexList.get(i);
			v.adjacencies = new ArrayList<Edge>();
			for (HashMap<Integer, Integer> nodeAdjacency : adjacencyList) {
				Set<Integer> vertexSet = nodeAdjacency.keySet();
				for (Integer vertexKey : vertexSet) {
					e = new Edge(vertexList.get(vertexKey),
							nodeAdjacency.get(vertexKey));
					v.adjacencies.add(e);
				}
			}
		}
		
		try {
			
			Writer fs1 = new BufferedWriter(new FileWriter("dump/Djikstra.txt",true));
		
			List<Vertex> path = null;
			for (int i=0; i<vertexList.size(); i++) {
				computePaths(vertexList.get(i));
				for (int j=0; j<vertexList.size(); j++) {
					v = vertexList.get(j);
					pathTable[i][j] = (int)v.minDistance;
					fs1.write("shortestPathTable["+i+"]["+j+"] = "+pathTable[i][j]+ ";" + "\n");
					System.out.println("shortestPathTable["+i+"]["+j+"] = "+pathTable[i][j]+";");
					path = getShortestPathTo(v);
					fs1.write("Path: " + path + "\n");
					System.out.println("Path: " + path);
				}
				resetDistances(vertexList);
				System.out.println();
			}
			fs1.close();
		}
		catch (IOException d){}	
		
		setShortestPathTable(pathTable);		
	}
	
	public static void resetDistances(List<Vertex> vertexList){
		for(Vertex v : vertexList){
			v.minDistance = Double.POSITIVE_INFINITY;
			v.previous = null;
		}
	}

	//public static void main(String[] args) {
		//computeShortestPaths();
	//}
}
