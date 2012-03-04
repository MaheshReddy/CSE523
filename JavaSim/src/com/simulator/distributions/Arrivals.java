package com.simulator.distributions;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;
import java.util.Random;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.simulator.controller.SimulationController;
import com.simulator.enums.SimulationTypes;
import com.simulator.packets.InterestPacket;
import com.simulator.ccn.CCNRouter;
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
	private static String workload=null;
	boolean simStatus = false;
	/**
	 * Buffered Reader for reading workload.all file for GlobeTraff 
	 */
	private static BufferedReader rdr = null;

	private static double loadImpact;
	
	public Arrivals () {
		
		InterArrivalTime = new ExponentialStream(loadImpact);
		gridSize = Grid.getGridSize();
		
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
		    
			/* The following statement will randomly choose a source node for the interest packet */
		    InterestPacket firstPacket = new InterestPacket(nodeSelecter.nextInt(gridSize), interestPacketSize,1);
					
			/*
			 * Now we have to parse next line from workload.all and assign the refpacket id for this interest packet.
			 * If we have reached EOF just end this thread thus controller thread which is waiting on this 
			 * notified.
			 */
			String line = null;
			try {
				line = rdr.readLine();
			
			/* 
			 * TODO: Calculate how many interest packets will we generate for this object. We divide the object size by segment size
			 * Presently, we assume this value to give us two interest packets per object 
			 * */
			
			
			if(line != null)
			{
				String [] words = line.split("\\s+");
				firstPacket.setRefPacketId(Integer.parseInt(words[1]));
			}
			else{
				rdr.close();
				setSimStatus(true);
				System.out.println("Done with Arrivals");
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* 
			 * This is not inside the for loop because we want to generate the same packet number for all the packets. Notice they are using
			 * different constructors. The packets created in the for loop are using the same packetID created with the first packet 
			 * */
			Packets.dumpStatistics(firstPacket, "CREATED");
			firstPacket.activate();
			
			
		    for (int i = 2; i <= 2; i++) {
				/* The following statement will randomly choose a source node for the interest packet */
				InterestPacket otherPackets = (InterestPacket) firstPacket.clone();
				
				/* The following statement will randomly choose the data/object which is being request with the interest packet */
				otherPackets.setSegmentId(i);
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
