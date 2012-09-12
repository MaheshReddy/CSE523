package com.simulator.ccn;

//import org.apache.log4j.Logger;


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
						SimulationController.top.ReActivateAt(SimulationController.timeOutQueue.peek().getTimeOutValue(), false);
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
				
				int expirationCount = SimulationController.timeOutQueue.peek().getExpirationCount() + 1;
				double tempTimeOutValue = SimulationController.CurrentTime() + SimulationController.getPitTimeOut() + SimulationController.getRetransNuance(); 
				
				
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
					Packets.dumpStatistics(timedOutPacket, "CRTEDTO");
					timedOutPacket.activate();				
				//}
				//else {
					
					//Packets.dumpStatistics(timedOutPacket, "TRMNTED");
					//timedOutPacket.finished(SupressionTypes.EXHAUSTED_EXPIRATION_COUNT);					
				//}
				
				/* Remove TimeOutfields object for expired interest packet from the front of the queue */				
				SimulationController.timeOutQueue.poll();				
				
				try {
					
					SimulationController.top.ReActivateAt(SimulationController.timeOutQueue.peek().getTimeOutValue(), false);
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