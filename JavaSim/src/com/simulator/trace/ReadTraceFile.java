package com.simulator.trace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class ReadTraceFile {
	
	private String path;
	private ArrayList <TraceRecord> trace;
	
	public ReadTraceFile (String filePath) throws IOException {
		
		path = filePath;
		trace = new ArrayList <TraceRecord> (50);		
		
		//readTraceRecords();		
	}

	public void readAllTraceRecords () throws IOException {
		
		/* Empty the ArrayList before reusing it */		
		trace.clear();
		
		FileReader fr = new FileReader (path);
		//FileReader fr = new FileReader(ClassLoader.getSystemResource(path).getPath());
		BufferedReader lineReader = new BufferedReader (fr);		
				
		String aLine = null;	
				
		while ((aLine = lineReader.readLine()) != null) {	
						
			Scanner scanTraceRecord = new Scanner (aLine);
			TraceRecord temp = new TraceRecord();
				
			temp.setPacketType(scanTraceRecord.next());
			temp.setPacketStatus(scanTraceRecord.next());
			temp.setTimeStamp(scanTraceRecord.nextFloat());
			temp.setPacketID(scanTraceRecord.nextInt());
			temp.setSourceNode(scanTraceRecord.nextInt());
			temp.setCurrentNode(scanTraceRecord.nextInt());
			temp.setPreviousNode(scanTraceRecord.nextInt());
			temp.setRequestedObjectID(scanTraceRecord.nextInt());
			temp.setCauseOfSuprression(scanTraceRecord.next());
			temp.setNumOfHops(scanTraceRecord.nextInt());
			temp.setLocalOrGlobalCache(scanTraceRecord.next());
			temp.setDeadOrAlive(scanTraceRecord.next());
			
			trace.add(temp);
						
		}
		lineReader.close( );		
	}		
	
	/*
	 * Collect the trace records at the destination node where the Interest packet is satisfied
	 * */
	public void readCSupprTraceRecords (String tempCSuppr) throws IOException {
		
		/* Empty the ArrayList before reusing it */		
		trace.clear();		
		
		FileReader fr = new FileReader (path);
		//FileReader fr = new FileReader(ClassLoader.getSystemResource(path).getPath());
		BufferedReader lineReader = new BufferedReader (fr);		
				
		String aLine = null;	
				
		while ((aLine = lineReader.readLine()) != null) {	
						
			Scanner scanTraceRecord = new Scanner (aLine);
			TraceRecord temp = new TraceRecord();
			
			/* Extracting Node Configuration from "Nodes" string onwards in the Text file */
			
			temp.setPacketType(scanTraceRecord.next());
			temp.setPacketStatus(scanTraceRecord.next());
			temp.setTimeStamp(scanTraceRecord.nextFloat());
			temp.setPacketID(scanTraceRecord.nextInt());
			temp.setSourceNode(scanTraceRecord.nextInt());
			temp.setCurrentNode(scanTraceRecord.nextInt());
			temp.setPreviousNode(scanTraceRecord.nextInt());
			temp.setRequestedObjectID(scanTraceRecord.nextInt());
			temp.setCauseOfSuprression(scanTraceRecord.next());
			temp.setNumOfHops(scanTraceRecord.nextInt());
			temp.setLocalOrGlobalCache(scanTraceRecord.next());
			temp.setDeadOrAlive(scanTraceRecord.next());
			
			if (tempCSuppr.compareTo(temp.getCauseOfSuprression()) == 0){
				trace.add(temp);
			}						
		}
		lineReader.close( );		
	}		
	
	/*
	 * Collect the trace records at the destination node where the Interest packet is satisfied
	 * */
	public void readIntOrDataTraceRecords (String tempPacketType) throws IOException {

		/* Empty the ArrayList before reusing it */		
		trace.clear();
		
		FileReader fr = new FileReader (path);
		//FileReader fr = new FileReader(ClassLoader.getSystemResource(path).getPath());
		BufferedReader lineReader = new BufferedReader (fr);		
				
		String aLine = null;	
				
		while ((aLine = lineReader.readLine()) != null) {	
						
			Scanner scanTraceRecord = new Scanner (aLine);
			TraceRecord temp = new TraceRecord();
			
			/* Extracting Node Configuration from "Nodes" string onwards in the Text file */
			
			temp.setPacketType(scanTraceRecord.next());
			temp.setPacketStatus(scanTraceRecord.next());
			temp.setTimeStamp(scanTraceRecord.nextFloat());
			temp.setPacketID(scanTraceRecord.nextInt());
			temp.setSourceNode(scanTraceRecord.nextInt());
			temp.setCurrentNode(scanTraceRecord.nextInt());
			temp.setPreviousNode(scanTraceRecord.nextInt());
			temp.setRequestedObjectID(scanTraceRecord.nextInt());
			temp.setCauseOfSuprression(scanTraceRecord.next());
			temp.setNumOfHops(scanTraceRecord.nextInt());
			temp.setLocalOrGlobalCache(scanTraceRecord.next());
			temp.setDeadOrAlive(scanTraceRecord.next());
			
			if (tempPacketType.compareTo(temp.getPacketType()) == 0){
				trace.add(temp);
			}						
		}
		lineReader.close( );		
	}
	
	public void sortTimeStampPacketIDNumOfHops (){
		/* 
		 * The following will sort the ArrayList with respect to PKTID, TimeStamp, and NumOfHops
		 *  */			
		Collections.sort(trace, new Comparator(){
			 
            public int compare(Object o1, Object o2) {
                
            	TraceRecord p1 = (TraceRecord) o1;
                TraceRecord p2 = (TraceRecord) o2;
                
                if (p1.getTimeStamp() > p2.getTimeStamp())
                	return 1;
                else if (p1.getTimeStamp() < p2.getTimeStamp())
                	return -1;
                else {
                	if(p1.getPacketID() > p2.getPacketID())
                		return 1;
                	else if (p1.getPacketID() < p2.getPacketID())
                		return -1;
                	else {
	                	if(p1.getNumOfHops() > p2.getNumOfHops())
	                		return 1;
	                	else if (p1.getNumOfHops() < p2.getNumOfHops())
	                		return -1;
	                	else
	                		return 0;
	                }
                }		                	                
            }
 
        });
	}

	public void sortPacketIDTimeStampNumOfHops (){
		/* 
		 * The following will sort the ArrayList with respect to PKTID, TimeStamp, and NumOfHops
		 *  */			
		Collections.sort(trace, new Comparator(){
			 
            public int compare(Object o1, Object o2) {
                
            	TraceRecord p1 = (TraceRecord) o1;
                TraceRecord p2 = (TraceRecord) o2;
                
                if (p1.getPacketID() > p2.getPacketID())
                	return 1;
                else if (p1.getPacketID() < p2.getPacketID())
                	return -1;
                else {
                	if(p1.getTimeStamp() > p2.getTimeStamp())
                		return 1;
                	else if (p1.getTimeStamp() < p2.getTimeStamp())
                		return -1;
                	else {
	                	if(p1.getNumOfHops() > p2.getNumOfHops())
	                		return 1;
	                	else if (p1.getNumOfHops() < p2.getNumOfHops())
	                		return -1;
	                	else
	                		return 0;
	                }
                }		                	                
            }
 
        });
	}
	
	public void sortObjectIDTimeStampNumOfHops (){
		/* 
		 * The following will sort the ArrayList with respect to PKTID, TimeStamp, and NumOfHops
		 *  */			
		Collections.sort(trace, new Comparator(){
			 
            public int compare(Object o1, Object o2) {
                
            	TraceRecord p1 = (TraceRecord) o1;
                TraceRecord p2 = (TraceRecord) o2;
                
                if (p1.getRequestedObjectID() > p2.getRequestedObjectID())
                	return 1;
                else if (p1.getRequestedObjectID() < p2.getRequestedObjectID())
                	return -1;
                else {
                	if(p1.getTimeStamp() > p2.getTimeStamp())
                		return 1;
                	else if (p1.getTimeStamp() < p2.getTimeStamp())
                		return -1;
                	else {
	                	if(p1.getNumOfHops() > p2.getNumOfHops())
	                		return 1;
	                	else if (p1.getNumOfHops() < p2.getNumOfHops())
	                		return -1;
	                	else
	                		return 0;
	                }
                }		                	                
            }
 
        });
	}
	
	ArrayList <TraceRecord> getTraceRecords () {
		return trace;
	}
		
}
	

