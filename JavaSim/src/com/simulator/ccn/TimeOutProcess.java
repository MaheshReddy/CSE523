package com.simulator.ccn;

//import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import com.simulator.controller.SimulationController;
import com.simulator.distributions.Arrivals;
import com.simulator.packets.InterestPacket;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

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
			
			
			/* Generate the Interest packet again */				
			InterestPacket timedOutPacket = new InterestPacket(SimulationController.timeOutQueue.peek().getNodeID(),
					Arrivals.getInterestPacketSize(), SimulationController.timeOutQueue.peek().getSegmentID());
			
			ArrayList<Double> timeoutValue = CCNRouter.calculateTimeOutValue(SimulationController.timeOutQueue.peek().getNodeID());
			
			double tempTimeOutValue = SimulationController.CurrentTime() + timeoutValue.get(1);
			int expirationCount = SimulationController.timeOutQueue.peek().getExpirationCount() + 1;
			
			System.out.println("\nPrimaryInterestID " + SimulationController.timeOutQueue.peek().getPrimaryInterestID());
			System.out.println("SegmentID " + SimulationController.timeOutQueue.peek().getSegmentID());
			System.out.println("SegmentID TO " + timedOutPacket.getSegmentId());
			//Grid.getRouter(SimulationController.timeOutQueue.peek().getNodeID()).decDefaultInterface();
				
			timedOutPacket.setRefPacketId(SimulationController.timeOutQueue.peek().getObjectID());
			timedOutPacket.setPrimaryInterestId(SimulationController.timeOutQueue.peek().getPrimaryInterestID());
			timedOutPacket.setParentInterestId(SimulationController.timeOutQueue.peek().getInterestID());
			timedOutPacket.setPrevHop(SimulationController.timeOutQueue.peek().getInterfaceID());
			timedOutPacket.setExpirationCount(expirationCount);
								
			/* To implement limitation on number of times packet should be retransmitted, remove comment from following
			 * if-statement 
			 * */
			//if (expirationCount <= 3) { 
				
			/* Add new interest packet into the queue with new interest packet id and updated timeout value */
			SimulationController.timeOutQueue.add(new TimeOutFields(timedOutPacket.getPrimaryInterestId(), timedOutPacket.getPacketId(),
				SimulationController.timeOutQueue.peek().getSegmentID(), SimulationController.timeOutQueue.peek().getObjectID(), 
				SimulationController.timeOutQueue.peek().getNodeID(), expirationCount, tempTimeOutValue, false, 
				timedOutPacket.getPrevHop()));
			/*		
			try {
				
				Writer fs1 = new BufferedWriter(new FileWriter(Packets.getDataDumpFile() + "_IntPktTOHist.txt",true));
				//fs1.write("\nStatus : " + status+ "\n");
				fs1.write(timedOutPacket.getPrimaryInterestId() + "\t");
				fs1.write(timedOutPacket.getPacketId() + "\t");
				fs1.write(SimulationController.timeOutQueue.peek().getNodeID() + "\t");
				fs1.write("RC\t");
				fs1.write(SimulationProcess.CurrentTime() + "\t");
				fs1.write(tempTimeOutValue + "\t");
				
				
				for(int i = 0; i < Grid.getGridSize(); i++) {			
					if (!timedOutPacket.sourceBasedRouting.contains(Grid.getRouter(i))) {
						fs1.write(Grid.getRouter(i).getPacketsQ().packetsInCCNQueue() + "(" + i + ") ");
					}							
				}
				fs1.write("\n");
				
				fs1.close();
			}
			catch (IOException e){}*/	
			
			timedOutPacket.setTimeoutAt(tempTimeOutValue);			
			timedOutPacket.setPitTimeoutAt(SimulationController.CurrentTime() + timeoutValue.get(0));
				
			/*try {
						
				Writer fs1 = new BufferedWriter(new FileWriter(Packets.getDataDumpFile() + "_TimeOutQueue.txt",true));
				fs1.write("\n(False)\n");
				
				for ( Iterator<TimeOutFields> tempIteratedTimeOutQueue = SimulationController.timeOutQueue.iterator(); 
						tempIteratedTimeOutQueue.hasNext(); ) {
					
					TimeOutFields tempTimeOutQueue = tempIteratedTimeOutQueue.next();
					
					fs1.write("" + SimulationProcess.CurrentTime() + "\t");								
					
					fs1.write("[TOV, " + tempTimeOutQueue.getTimeOutValue() + ";");
					fs1.write(" PriIntID, " +tempTimeOutQueue.getPrimaryInterestID() + ";");
					fs1.write(" IntID, " + tempTimeOutQueue.getInterestID() + ";");
					fs1.write(" SegID, " + tempTimeOutQueue.getSegmentID() + ";");
					fs1.write(" ObjID, " + tempTimeOutQueue.getObjectID() + ";");
					fs1.write(" NodeID, " + tempTimeOutQueue.getNodeID() + ";");
					fs1.write(" RecedObj, " + tempTimeOutQueue.getReceivedDataObject() + ";");
					fs1.write(" InfID, " + tempTimeOutQueue.getInterfaceID() + ";");
					fs1.write(" NumTimesExp, " + tempTimeOutQueue.getExpirationCount() + "]\t");	
					fs1.write("\n");	
				}
				
				//fs1.write("\n");
				fs1.close();
			}
			catch (IOException e){} */				           
					
			/* Using a different label so that we can differentiate between interest packet
			 * that are retransmitted 
			 * */
						
			retranmissionPacketCount++;
						
			Packets.dumpStatistics(timedOutPacket, "CRTEDTO");
			timedOutPacket.activate();	
			
			timedOutPacket.setProcessingDelayAtNode(Grid.getRouter(timedOutPacket.getCurNode()).getPacketsQ().packetsInCCNQueue() * CCNRouter.getProcDelay());
			//}
			//else {
					
				//Packets.dumpStatistics(timedOutPacket, "TRMNTED");
				//timedOutPacket.finished(SupressionTypes.EXHAUSTED_EXPIRATION_COUNT);					
			//}
					
			//System.out.println("\nRetransmitted packets: " + retranmissionPacketCount);
			//try {
				//Writer queueSizeWriter = new BufferedWriter(new FileWriter(Packets.getDataDumpFile()+ "_timeoutQueue",true));
				
				//queueSizeWriter.write("\n\nCurrent time: " + SimulationController.CurrentTime());
				//queueSizeWriter.write("\nNumber of Data Packets Received: " + CCNRouter.countDataPacketsReceived);
				//queueSizeWriter.write("\nTimeOutQueue Size" + SimulationController.timeOutQueue.size());
				
				//queueSizeWriter.write("\n(PrimaryInterestID)" + SimulationController.timeOutQueue.peek().getPrimaryInterestID());
				//queueSizeWriter.write("\n(NodeID)" + SimulationController.timeOutQueue.peek().getNodeID());
				//queueSizeWriter.write("\n(TimeOutValue)" + tempTimeOutValue);
				//queueSizeWriter.write("\n(Expiration Count): " + expirationCount);
				//queueSizeWriter.write("\n(InterestID)" + SimulationController.timeOutQueue.peek().getInterestID());
				//queueSizeWriter.write("\n(ObjectID)" + SimulationController.timeOutQueue.peek().getObjectID());
				//queueSizeWriter.write("\n(ReceivedObject)" + SimulationController.timeOutQueue.peek().getReceivedDataObject() + "\n");
				//queueSizeWriter.write("Current Time: " + SimulationController.CurrentTime() + "\n");
				
				//queueSizeWriter.close();
				
			//}
			//catch (IOException e) {}
				
			//System.out.println("\nPrimary InterestID for new Packet in TimeOutQueue: " + SimulationController.timeOutQueue.peek().getPrimaryInterestID());
			//System.out.println("Node ID for new Packet in TimeOutQueue: " + SimulationController.timeOutQueue.peek().getNodeID());
			//System.out.println("InterestID for new Packet in TimeOutQueue: " + timedOutPacket.getPacketId());
			//System.out.println("ObjectID for new Packet in TimeOutQueue: " + SimulationController.timeOutQueue.peek().getObjectID());
			//System.out.println("Timeout value to be added: " + timeoutValue.get(1));
			//System.out.println("TimeOut Value for new Packet in TimeOutQueue: " + tempTimeOutValue);
			//System.out.println("Expiration Count for new Packet in TimeOutQueue: " + expirationCount);
			//System.out.println("Current Time: " + SimulationController.CurrentTime() + "\n");
			
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
		//}
		}		
    }
}