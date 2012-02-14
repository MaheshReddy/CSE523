package com.simulator.trace;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import com.simulator.topology.Edge;
import com.simulator.topology.Grid;
import com.simulator.topology.Node;
import com.simulator.topology.ReadFile;

public class ManipulateTrace {
	
	public ManipulateTrace (String fileName) {
	
		ArrayList <TraceRecord> trace = null;
		
		ShortestPathRoutingTable shortestpath = new ShortestPathRoutingTable();
		
		try {
			
			/* Creates a user-defined class that reads the Trace file */
			ReadTraceFile file = new ReadTraceFile(fileName);
			Writer fs1 = new BufferedWriter(new FileWriter("dump/unsortedAllTraceRecords.txt",true));
			Writer fs2 = new BufferedWriter(new FileWriter("dump/sortedAllTraceRecords.txt",true));
			Writer fs3 = new BufferedWriter(new FileWriter("dump/allInterstPackets.txt",true));
			Writer fs4 = new BufferedWriter(new FileWriter("dump/allDataPackets.txt",true));
			Writer fs5 = new BufferedWriter(new FileWriter("dump/verifyShortestPath.txt",true));
			
			/*
			 * Write the Unsorted ArrayList into the file
			 * */
			file.readAllTraceRecords();
			trace = file.getTraceRecords();		

			for (int count = 0; count < trace.size(); count++) {
				//(trace.get(count)).printTraceRecord();	
				fs1.write((trace.get(count)).toString());
			}
			fs1.close();
			
			/*
			 * Write the sorted ArrayList into the file
			 * */
			trace.clear();
			
			/* Sort by packet ID, timestamp, and number of hops*/
			file.sortPacketIDTimeStampNumOfHops();
			trace = file.getTraceRecords();

			for (int count = 0; count < trace.size(); count++) {
				//(trace.get(count)).printTraceRecord();	
				fs2.write((trace.get(count)).toString());
			}
			fs2.close();
			
			/*
			 * Write all trace records containing the Interest packets
			 * */
			trace.clear();
			file.readIntOrDataTraceRecords("i");
			
			/* Sort by PacketID, timestamp, and number of hops*/
			file.sortPacketIDTimeStampNumOfHops();
			trace = file.getTraceRecords();		

			for (int count = 0; count < trace.size(); count++) {
				//(trace.get(count)).printTraceRecord();	
				fs3.write((trace.get(count)).toString());
			}
			fs3.close();
				
			/*
			 * Write all trace records containing the data packets
			 * */
			trace.clear();
			file.readIntOrDataTraceRecords("d");
			
			/* Sort by packet ID, timestamp, and number of hops*/
			file.sortObjectIDTimeStampNumOfHops();
			trace = file.getTraceRecords();		

			for (int count = 0; count < trace.size(); count++) {
				//(trace.get(count)).printTraceRecord();	
				fs4.write((trace.get(count)).toString());
			}		
			fs4.close();
			/*
			 * Verify whether all the interest packets are using the shortest path to find data objects
			 * Excellent: The request is satisfied earlier with Global Cache
			 * OK: Expected number of hops which is shortest path between source and destination
			 * Not Optimal: Using more hops than the shortest path between source and destination
			 * */			
			trace.clear();
			file.readCSupprTraceRecords("SUPRESSION_SENT_DATA_PACKET");
			
			/* Sort by timestamp, packet ID, and number of hops*/
			file.sortTimeStampPacketIDNumOfHops();
			trace = file.getTraceRecords();		

			for (int count = 0; count < trace.size(); count++) {
				//(trace.get(count)).printTraceRecord();
				if (shortestpath.shortestPathTable [(trace.get(count)).sourceNode][(trace.get(count)).getRequestedObjectID() % Grid.getGridSize()] > (trace.get(count)).numOfHops) {
					fs5.write((trace.get(count)).toVerifyShortestPath() + " " + shortestpath.shortestPathTable [(trace.get(count)).sourceNode][(trace.get(count)).getRequestedObjectID() % Grid.getGridSize()]  + ") Result:SAVED " + (shortestpath.shortestPathTable [(trace.get(count)).sourceNode][(trace.get(count)).getRequestedObjectID() % Grid.getGridSize()] - (trace.get(count)).numOfHops) + " HOPS\n");
				}
				else if (shortestpath.shortestPathTable [(trace.get(count)).sourceNode][(trace.get(count)).getRequestedObjectID() % Grid.getGridSize()] == (trace.get(count)).numOfHops) {
					fs5.write((trace.get(count)).toVerifyShortestPath() + " " + shortestpath.shortestPathTable [(trace.get(count)).sourceNode][(trace.get(count)).getRequestedObjectID() % Grid.getGridSize()]  + ") Result:EXPECTED\n");
				}
				else {
					fs5.write((trace.get(count)).toVerifyShortestPath() + " " + shortestpath.shortestPathTable [(trace.get(count)).sourceNode][(trace.get(count)).getRequestedObjectID() % Grid.getGridSize()]  + ") Result:NOT OPTIMAL\n");					
				}
			}	
			fs5.close();
		}		
		catch (IOException e){
			System.out.println( e.getMessage() );
		}	
	}
}
