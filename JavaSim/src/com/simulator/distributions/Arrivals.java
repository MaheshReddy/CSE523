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
	private static String workload=null;
	boolean simStatus = false;
	/**
	 * Buffered Reader for reading workload.all file for GlobeTraff 
	 */
	private static BufferedReader rdr = null;

	public Arrivals (double mean) {
		
		InterArrivalTime = new ExponentialStream(mean);
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
			Packets packets = new InterestPacket(nodeSelecter.nextInt(gridSize), interestPacketSize);
					
			/*
			 * Now we have to parse next line from workload.all and assign the refpacket id for this interest packet.
			 * If we have reached EOF just end this thread thus controller thread which is waiting on this 
			 * notified.
			 */
			String line = null;
			try {
				line = rdr.readLine();
			
			if(line != null)
			{
				String [] words = line.split("\\s+");
				packets.setRefPacketId(Integer.parseInt(words[1]));
			}
			else{
				rdr.close();
				setSimStatus(true);
				System.out.println("Done with Arrivals");
				return;
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
