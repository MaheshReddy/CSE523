package com.simulator.ccn;

//import org.apache.log4j.Logger;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.io.IOException;

import com.simulator.controller.SimulationController;
import com.simulator.distributions.Arrivals;
import com.simulator.packets.InterestPacket;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;
//import com.simulator.enums.SupressionTypes;

//import java.util.Iterator;

import arjuna.JavaSim.Simulation.*;;

/* 
 * This class is a SimulationProcess which is invoked intermittently 
 * to check whether an interest packet has timed out, needs retransmission
 * */

public class TimeOutProcess extends SimulationProcess {
	
	public static int retranmissionPacketCount = 0;
	
	public TimeOutProcess () {		
	}
	
	public void run() {
		
		while (true) {			
			
			/* If the corresponding data packet is received, then the TimeOutField object
			 * pertaining to the Interest packet is removed from the TimeOutQueue. */
			if(SimulationController.timeOutQueue.peek().getReceivedDataObject()) {
				
				/* Remove item from the first element of the Priority queue. */
				SimulationController.timeOutQueue.poll();
				
				try {
					if (SimulationController.timeOutQueue.size() >= 1) {
						if (SimulationController.CurrentTime() - SimulationController.timeOutQueue.peek().getTimeOutValue() <= 0) {
							SimulationController.top.ReActivateAt(SimulationController.timeOutQueue.peek().getTimeOutValue(), false);
						}
					}
					/* If the TimeOutQueue is empty, then the process is put back to sleep. */
					else {
						/* This will help terminate the process */
						try	{

							Cancel();
						}
						catch (RestartException e) {}
						
					}
				} 
				catch (SimulationException e) {
					e.printStackTrace();
				} 
				catch (RestartException e) {
					e.printStackTrace();
				}	
			}
			/* Scenario that the interest has been timed out */
			else {								
				
				//while () {
					
					ArrayList<Double> timeoutValue = CCNRouter.calculateTimeOutValue(SimulationController.timeOutQueue.peek().getNodeID());
					
					double tempTimeOutValue = SimulationController.CurrentTime() + timeoutValue.get(1);
					int expirationCount = SimulationController.timeOutQueue.peek().getExpirationCount() + 1;
					
					/* Generate the Interest packet again */				
					InterestPacket timedOutPacket = new InterestPacket(SimulationController.timeOutQueue.peek().getNodeID(),
							SimulationController.timeOutQueue.peek().getSegmentID(), Arrivals.getInterestPacketSize());
					
					Grid.getRouter(SimulationController.timeOutQueue.peek().getNodeID()).decDefaultInterface();
					
					timedOutPacket.setRefPacketId(SimulationController.timeOutQueue.peek().getObjectID());
					timedOutPacket.setPrimaryInterestId(SimulationController.timeOutQueue.peek().getPrimaryInterestID());
					timedOutPacket.setParentInterestId(SimulationController.timeOutQueue.peek().getInterestID());
					timedOutPacket.setExpirationCount(expirationCount);
					timedOutPacket.setPrevHop(Grid.getRouter(SimulationController.timeOutQueue.peek().getNodeID()).getDefaultInterface());				
					
					/* To implement limitation on number of times packet should be retransmitted, remove comment from following
					 * if-statement 
					 * */
					//if (expirationCount <= 3) { 
					
						/* Add new interest packet into the queue with new interest packet id and updated timeout value */
						SimulationController.timeOutQueue.add(new TimeOutFields(timedOutPacket.getPrimaryInterestId(), timedOutPacket.getPacketId(),
								SimulationController.timeOutQueue.peek().getSegmentID(), SimulationController.timeOutQueue.peek().getObjectID(), 
								SimulationController.timeOutQueue.peek().getNodeID(), expirationCount, tempTimeOutValue, false));
						
						/* Using a different label so that we can differentiate between interest packet
						 * that are retransmitted 
						 * */
						
						retranmissionPacketCount++;
						
						Packets.dumpStatistics(timedOutPacket, "CRTEDTO");
						timedOutPacket.activate();				
					//}
					//else {
						
						//Packets.dumpStatistics(timedOutPacket, "TRMNTED");
						//timedOutPacket.finished(SupressionTypes.EXHAUSTED_EXPIRATION_COUNT);					
					//}
						
					//System.out.println("\nRetransmitted packets: " + retranmissionPacketCount);
					try {
						Writer queueSizeWriter = new BufferedWriter(new FileWriter(Packets.getDataDumpFile()+ "_timeoutQueue",true));
						
						queueSizeWriter.write("\n\nCurrent time: " + SimulationController.CurrentTime());
						queueSizeWriter.write("\nNumber of Data Packets Received: " + CCNRouter.countDataPacketsReceived);
						queueSizeWriter.write("\nTimeOutQueue Size" + SimulationController.timeOutQueue.size());
						
						queueSizeWriter.write("\n(PrimaryInterestID)" + SimulationController.timeOutQueue.peek().getPrimaryInterestID());
						queueSizeWriter.write("\n(NodeID)" + SimulationController.timeOutQueue.peek().getNodeID());
						queueSizeWriter.write("\n(TimeOutValue)" + tempTimeOutValue);
						queueSizeWriter.write("\n(Expiration Count): " + expirationCount);
						queueSizeWriter.write("\n(InterestID)" + SimulationController.timeOutQueue.peek().getInterestID());
						queueSizeWriter.write("\n(ObjectID)" + SimulationController.timeOutQueue.peek().getObjectID());
						queueSizeWriter.write("\n(ReceivedObject)" + SimulationController.timeOutQueue.peek().getReceivedDataObject() + "\n");
						queueSizeWriter.write("Current Time: " + SimulationController.CurrentTime() + "\n");
						
						queueSizeWriter.close();
						
					}
					catch (IOException e) {}
						
					System.out.println("\nPrimary InterestID for new Packet in TimeOutQueue: " + SimulationController.timeOutQueue.peek().getPrimaryInterestID());
					System.out.println("Node ID for new Packet in TimeOutQueue: " + SimulationController.timeOutQueue.peek().getNodeID());
					//System.out.println("InterestID for new Packet in TimeOutQueue: " + timedOutPacket.getPacketId());
					System.out.println("ObjectID for new Packet in TimeOutQueue: " + SimulationController.timeOutQueue.peek().getObjectID());
					//System.out.println("Timeout value to be added: " + timeoutValue.get(1));
					System.out.println("TimeOut Value for new Packet in TimeOutQueue: " + tempTimeOutValue);
					System.out.println("Expiration Count for new Packet in TimeOutQueue: " + expirationCount);
					System.out.println("Current Time: " + SimulationController.CurrentTime() + "\n");
					
					/* Remove TimeOutfields object for expired interest packet from the front of the queue */				
					SimulationController.timeOutQueue.poll();	
				
				//}				
				
				try {
					
					if ((SimulationController.CurrentTime() - SimulationController.timeOutQueue.peek().getTimeOutValue()) <= 0) {
						SimulationController.top.ReActivateAt(SimulationController.timeOutQueue.peek().getTimeOutValue(), false);
					}
				} 
				catch (SimulationException e) {
					e.printStackTrace();
				} 
				catch (RestartException e) {
					e.printStackTrace();
				}			
			}
		}		
    }
}