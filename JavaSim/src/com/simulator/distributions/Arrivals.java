package com.simulator.distributions;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;
import java.util.Random;

import java.io.IOException;

import org.apache.log4j.Logger;

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
	private static double arvDelay;
	
	public Arrivals (double mean) {
		
		InterArrivalTime = new ExponentialStream(mean);
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
		    
			/* The following statement will randomly choose a source node for the interest packet */
			Packets packets = new Packets(nodeSelecter.nextInt(gridSize), SimulationTypes.SIMULATION_PACKETS_INTEREST, interestPacketSize);
			
			/* The following statement will randomly choose the data/object which is being request with the interest packet */
			packets.setRefPacketId(packetIdGenerator.nextInt(PacketDistributions.getNoDataPackets()));
			
			/* 
			 * The following code records the creation of the interest packet 
			 * */			
			Packets.dumpStatistics(packets, "CREATED");			
			
			/* The following statement moves the program control the Packet class, where this interest packet is added into
			 * the source nodes queue 
			 */			
			packets.activate();
			
			log.info("Packet generated ");				
		}
	}

	public int getHostId() {	
		return 0;	
	}
	
	public static double getArvDelay() {
		return arvDelay;
	}
	
	public static void setArvDelay(double arvDelay) {
		Arrivals.arvDelay = arvDelay;
	}    
	
	public static void setInterestPacketSize(int tempIntPacketSize) {
		Arrivals.interestPacketSize = tempIntPacketSize;
	} 
	
	public static int getInterestPacketSize() {
		return interestPacketSize;
	}
};
