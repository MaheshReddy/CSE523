package com.simulator.distributions;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;
import java.util.Random;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.simulator.ccn.CCNRouter;
import com.simulator.controller.SimulationTypes;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

import arjuna.JavaSim.Simulation.SimulationException;

/*
 * This class is SimulationProcess thread which is used 
 * to generate interest packets continuously based on
 * ExponentialStream (mean)
 * */

public class Arrivals extends SimulationProcess {
	
	static final Logger log = Logger.getLogger(Arrivals.class);
	static int interestPacketSize = 0;
	private ExponentialStream InterArrivalTime;
	private Integer gridSize = 0;
	private Random nodeSelecter;
	private Random packetIdGenerator;
	private static double loadImpact;
	
	public Arrivals () {
		
		InterArrivalTime = new ExponentialStream(loadImpact);
		gridSize = Grid.getGridSize();
		
		/* At present, we are using seeds for testing purposes. However, eventually, we will remove them in the production 
		 * mode. 
		 * */
		packetIdGenerator = new Random(5);
		nodeSelecter = new Random(5);
    }

	/* This method is revoked intermittently to create interest packets */
	public void run () {
		
		for (;;) {
	    
			try {				
				
				/* We used this command for experimental purposes: 
				 * "Hold(5);" 
				 * */ 
				Hold(InterArrivalTime.getNumber());
			}	    
			catch (SimulationException e) {}			
			catch (RestartException e) {}			
		    catch (IOException e) {}
			
			/* 
			 * TODO: Calculate how many interest packets will we generate for this object. We divide the object size by segment size
			 * Presently, we assume this value to give us two interest packets per object 
			 * */
			
			int objectID = packetIdGenerator.nextInt(PacketDistributions.getNoDataPackets());
			int srcNode = nodeSelecter.nextInt(gridSize);
			
			/* 
			 * This is not inside the for loop because we want to generate the same packet number for all the packets. Notice they are using
			 * different constructors. The packets created in the for loop are using the same packetID created with the first packet 
			 * */
			Packets firstPacket = new Packets(srcNode, SimulationTypes.SIMULATION_PACKETS_INTEREST, interestPacketSize, 1);
			firstPacket.setRefPacketId(objectID);
			Packets.dumpStatistics(firstPacket, "CREATED");
			firstPacket.activate();
			
			int packetID = firstPacket.getPacketId();
			
		    for (int i = 2; i <= 2; i++) {
				/* The following statement will randomly choose a source node for the interest packet */
				Packets otherPackets = new Packets(srcNode, SimulationTypes.SIMULATION_PACKETS_INTEREST, interestPacketSize, packetID, i);
				
				/* The following statement will randomly choose the data/object which is being request with the interest packet */
				otherPackets.setRefPacketId(objectID);
				
				/* 
				 * The following code records the creation of the interest packet 
				 * */			
				Packets.dumpStatistics(otherPackets, "CREATED");			
				
				/* The following statement moves the program control the Packet class, where this interest packet is added into
				 * the source nodes queue 
				 */			
				otherPackets.activate();
		    }
			
			log.info("Packet generated ");				
		}
	}

	public int getHostId() {	
		return 0;	
	}
	
	public static double getLoadImpact () {
		return loadImpact;
	}
	
	public static void setLoadImpact(double tempLoadImpact) {
		Arrivals.loadImpact = tempLoadImpact;
	}    
	
	public static void setInterestPacketSize(int tempIntPacketSize) {
		Arrivals.interestPacketSize = tempIntPacketSize;
	} 
	
	public static int getInterestPacketSize() {
		return interestPacketSize;
	}
};
