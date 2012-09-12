package com.simulator.topology;

import java.io.IOException;
import java.util.*;

/**/
public class ReadBriteFile {

	/**
	 * @param args
	 */
	
	private Node [] nodes;
	private Edge [] edges;
	
	public ReadBriteFile (String fileName) {
		
		try {
			
			/* Creates a user-defined class that reads the Topology configuration file in Brite 
			 * format*/
			ReadFile file = new ReadFile(fileName);
			
			
			/** Calls the openFile method of user-defined ReadFile class **/
			file.openFile( );
			
			/* Creates two arrays of User-defined classes, namely, Nodes and Edges which will
			 * store the configurations for the nodes and edges, respectively */
			nodes = new Node [file.getNodesArray().length];
			edges = new Edge [file.getEdgesArray().length * 2];
			
			/* These methods retrieve an array of Strings containing configurations of nodes
			 * and edges retrieved from the Topology configuration file*/
			String [] nodesConfiguration = file.getNodesArray();
			String [] edgesConfiguration = file.getEdgesArray();
			
			int i;
			int j;
			
			/* Parsing the String array containing configuration information of Edges, and 
			 * putting it in the array of user-defined class Edge, so that we can use them */
			for ( i = 0, j = 0; i < edgesConfiguration.length; i++, j++ ) {
				System.out.println( edgesConfiguration[i]) ;
				
				/* We need to add two-way edges, hence, we are duplicating the edges while
				 * interchanging only the To and From fields */
				edges [j++] = new Edge (edgesConfiguration[i]);
				edges [j] = new Edge (edges [j-1]);
				
			}
			
			/* Parsing the String array containing configuration information of Nodes, and 
			 * putting it in the array of user-defined class Node, so that we can use them */ 
			for ( i=0; i < nodesConfiguration.length; i++ ) {
				//System.out.println( nodesConfiguration[i]);
				ArrayList <Edge> tempTable = new ArrayList <Edge> (10);
				
				nodes [i] = new Node (nodesConfiguration[i]);
				
				/* This loop helps in constructing the Routing Table. We attach the Edges to their
				 * respective Nodes */
				for (j = 0; j < edges.length; j++) {
					if (nodes[i].getNodeID() == edges[j].getEdgeFrom()){
						tempTable.add(edges[j]);
					}						
				}
				
				/** This method constructs the Routing table of a particular node **/
				nodes[i].setRoutingTable(tempTable);				
			}
			
			/** The following two loops are to print the outputs **/
			for ( i=0; i < nodes.length; i++ ) {
				System.out.println(nodesConfiguration[i]);
				//nodes[i].contentsOfNode();
			}

			for ( i=0; i < edges.length; i++ ) {				
				//edges[i].contentsOfEdge();
			}
			
		}
		catch (IOException e){
			System.out.println( e.getMessage() );
		}		

	}		
	
	public Node [] getAllNodes () {
		return nodes;
	}
	
	public Edge [] getAllEdges () {
		return edges;
	}
}



