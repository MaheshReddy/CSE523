package com.simulator.topology;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;

/**/
public class ReadFile {

	private String path;
	private ArrayList <Integer> nodesAndEdgesCount;
	private String [] nodesArray;
	private String [] edgesArray;
	
	public ReadFile (String filePath) throws IOException {
		
		path = filePath;
		
		nodesAndEdgesCount = new ArrayList <Integer> (2);
		
		readNumOfNodesAndEdges();
		
		nodesArray = new String[nodesAndEdgesCount.get(0)];
		edgesArray = new String[nodesAndEdgesCount.get(1)];
		
	}
	
	public String [] getNodesArray () {
		return nodesArray;
	}
	
	public String [] getEdgesArray () {
		return edgesArray;
	}
	
	public ArrayList <Integer> getNodesAndEdgesCount () {
		return nodesAndEdgesCount;
	}
	
	/*
	 * Extract Node and Edge Configurations from Text file
	 * 
	 * */
	
	public void openFile () throws IOException {
		
		//FileReader fr = new FileReader (path);
		FileReader fr = new FileReader(ClassLoader.getSystemResource(path).getPath());
		BufferedReader lineReader = new BufferedReader (fr);		
		
		int nodeCount = 0;
		int edgeCount = 0;
		
		String aLine = null;	
		
		while ((aLine = lineReader.readLine()) != null) {	
				
			Scanner scanNodesEdges = new Scanner (aLine);
			
			/* Extracting Node Configuration from "Nodes" string onwards in the Text file */
			if (scanNodesEdges.findInLine("Nodes:") != null) {
				for (nodeCount = 0; nodeCount < nodesAndEdgesCount.get(0); nodeCount++)
					nodesArray[nodeCount] = lineReader.readLine();				
			} 
			
			/* Extracting Edge Configuration from "Edges" string onwards in the Text file */
			else if (scanNodesEdges.findInLine("Edges:") != null) {
				for (edgeCount = 0; edgeCount < nodesAndEdgesCount.get(1); edgeCount++)
					edgesArray[edgeCount] = lineReader.readLine();	
			} 
			
			/* Ignore all other lines */
		}
		lineReader.close( );		
	}
	
	
	/*
	 * Extract the number of Nodes and Edges essentially from the first line of the Text file
	 * 
	 * */
	
	void readNumOfNodesAndEdges () throws IOException {
		
		FileReader fileCountNodesAndEdges = new FileReader(ClassLoader.getSystemResource(path).getPath());
		BufferedReader bufferCountNodesAndEdges = new BufferedReader (fileCountNodesAndEdges);
		
		String aLine;
		
		while ((aLine = bufferCountNodesAndEdges.readLine()) != null) {			
			Scanner scanNodesEdges = new Scanner (aLine);
			
			/* Finding the first line containing the "Topology:" string, and extracting the
			 * the number of nodes and edges information on that line */
			if (scanNodesEdges.findInLine("Topology:") != null) {
				while (scanNodesEdges.hasNext()) {
	                if (scanNodesEdges.hasNextInt ()) {
	                	nodesAndEdgesCount.add(scanNodesEdges.nextInt());
	                } else {
	                	scanNodesEdges.next();
	                }   
	            }
				break;
	        } 
		}
		bufferCountNodesAndEdges.close( );
	}
}
