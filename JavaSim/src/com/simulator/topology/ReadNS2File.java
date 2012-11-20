package com.simulator.topology;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;

/**/
public class ReadNS2File {

	private String path;
		
	public ArrayList <String> nodesArray;
	public ArrayList <String> briteOutput;
	
	public static int nodeCount = 0;
	public static int edgeCount = 0;
	
	
	
	public ReadNS2File (String filePath) throws IOException {
		
		path = filePath;		
		nodesArray = new ArrayList <String> (100);
		briteOutput = new ArrayList <String> (100);
	}
	
	/*
	 * Extract Node and Edge Configurations from Text file
	 * 
	 * */
	
	public void openFile () throws IOException {
		
		FileReader fr = new FileReader (path);
		//FileReader fr = new FileReader(ClassLoader.getSystemResource(path).getPath());
		BufferedReader lineReader = new BufferedReader (fr);		
		
		String aLine = null;	
		
		while ((aLine = lineReader.readLine()) != null) {	
				
			Scanner scanNodesEdges = new Scanner (aLine.trim());
			System.out.print(scanNodesEdges);
			
			/* Extracting Node Configuration from "Nodes" string onwards in the Text file */
			if (scanNodesEdges.findInLine("[$ns nodes]") != null) {
				scanNodesEdges.next();
				nodesArray.add("$" + scanNodesEdges.next());
				System.out.print(nodesArray.get(nodeCount));
				nodeCount++;
			} 
			
			/* Extracting Edge Configuration from "Edges" string onwards in the Text file */
			else if (scanNodesEdges.findInLine("Edges:") != null) {				
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
	                	//nodesAndEdgesCount.add(scanNodesEdges.nextInt());
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
