package com.simulator.distributions;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;
import java.util.Random;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.simulator.ccn.CCNRouter;
import com.simulator.controller.SimulationController;
import com.simulator.enums.SimulationTypes;
import com.simulator.packets.InterestPacket;
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
	
	private Integer gridSize = 0;
	private int countInterestPackets;
	private ExponentialStream InterArrivalTime;
	
	private static double loadImpact;
	
	private static int interestPacketSize = 0;
	private static int segmentSize;
	
	boolean simStatus = false;
	
	private Random nodeSelecter;
	private Random packetIdGenerator;
	
	private static String workload = null;
	
	/**
	 * Buffered Reader for reading workload.all file for GlobeTraff 
	 */
	private static BufferedReader rdr = null;

	public Arrivals () {
		
		InterArrivalTime = new ExponentialStream(loadImpact);
		gridSize = Grid.getGridSize();
		
		countInterestPackets = PacketDistributions.getNoDataPackets();
		
		rdr = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(getWorkload())));
		
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
			
			int objectID = 0;
			int objectSize = 0;
						
			if (SimulationController.getDistributionType() == SimulationTypes.SIMULATION_DISTRIBUTION_GLOBETRAFF) {
			
				/*
				 * Now we have to parse next line from workload.all and assign the refpacket id for this interest packet.
				 * If we have reached EOF just end this thread thus controller thread which is waiting on this 
				 * notified.
				 * */
				String line = null;
				
				try {				
					
					line = rdr.readLine();
				
					if(line != null) {
						
						String [] words = line.split("\\s+");
						objectID = Integer.parseInt(words[1]);
						objectSize = Integer.parseInt(words[2]);
					}
					else{
						
						rdr.close();
						setSimStatus(true);
						System.out.println("Done with Arrivals");
					}
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (SimulationController.getDistributionType() == SimulationTypes.SIMULATION_DISTRIBUTION_DEFAULT) {
				
				objectID = packetIdGenerator.nextInt(PacketDistributions.getNoDataPackets());
				objectSize = PacketDistributions.getDataPacketSize();
				
				if (countInterestPackets >= SimulationController.getMaxSimulatedPackets()) { 
					
					setSimStatus(true);
					System.out.println("Done with Arrivals");
				}
			}
			
			int srcNode = nodeSelecter.nextInt(gridSize);
		    
			/* 
			 * This is not inside the for loop because we want to generate the same packet number for all the packets. 
			 * The packets created in the for loop are cloned, hence use the same packetID created with the first packet 
			 * */
			Packets firstPacket = new InterestPacket(srcNode, interestPacketSize, 1);
			firstPacket.setRefPacketId(objectID);	
			Packets.dumpStatistics(firstPacket, "CREATED");
			firstPacket.activate();		
			
			countInterestPackets++;
			
			double numberOfIntPacks = Math.ceil((double)objectSize/(double)Arrivals.getSegmentSize());			
			
			if (numberOfIntPacks >= 2) {
			
				for (int i = 2; i <= (int)numberOfIntPacks; i++) {
					/* The following statement will randomly choose a source node for the interest packet */
					
			    	Packets otherPackets = (Packets) firstPacket.clone();
			    	otherPackets.setSegmentId(i);  	
			    	otherPackets.setCurNode(-1);
					
					/* 
					 * The following code records the creation of the interest packet 
					 * */			
					Packets.dumpStatistics(otherPackets, "CREATED");			
					
					/* The following statement moves the program control the Packet class, where this interest packet is added into
					 * the source nodes queue 
					 */			
					otherPackets.activate();
			    }
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
	
	public static void setSegmentSize(int tempSegSize) {
		Arrivals.segmentSize = tempSegSize;
	} 
	
	public static int getSegmentSize() {
		return segmentSize;
	}

	public static String getWorkload() {
		return workload;
	}

	public static void setWorkload(String workload) {
		Arrivals.workload = workload;
	}

	public synchronized boolean isSimStatus() {
		return simStatus;
	}

	public void setSimStatus(boolean simStatus) {
		this.simStatus = simStatus;
	}
};
