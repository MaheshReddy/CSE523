package com.simulator.distributions;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;
import java.util.Random;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import org.apache.log4j.Logger;

import com.simulator.ccn.CCNRouter;
import com.simulator.controller.SimulationController;
import com.simulator.enums.SimulationTypes;
import com.simulator.packets.InterestPacket;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;
import com.simulator.ccn.TimeOutFields;

import arjuna.JavaSim.Simulation.SimulationException;

/*
 * This class is SimulationProcess thread which is used 
 * to generate interest packets continuously based on
 * ExponentialStream (mean)
 * */

public class Arrivals extends SimulationProcess {
	
	//static final Logger log = Logger.getLogger(Arrivals.class);
	
	private Integer gridSize = 0;
	public int countInterestPackets;
	private ExponentialStream InterArrivalTime;
	
	private static double loadImpact;
	
	private static int interestPacketSize = 0;
	private static int segmentSize;
	
	/* This variable to set to true when all the required number of interest packets have
	 * been generated */
	private static boolean arrivalStatus = false;
	
	private Random nodeSelecter;
	private Random packetIdGenerator;
	
	private static String workload = null;
	
	/**
	 * Buffered Reader for reading workload.all file for GlobeTraff 
	 */
	private static BufferedReader rdr = null;

	public Arrivals () {
		
		//InterArrivalTime = new ExponentialStream(loadImpact, 0, 3063366117L, 3003062878L);
		InterArrivalTime = new ExponentialStream(loadImpact);
		gridSize = Grid.getGridSize();
		
		countInterestPackets = 0;
		
		rdr = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(getWorkload())));
		
		/* At present, we are using seeds for testing purposes. However, eventually, we will remove them in the production 
		 * mode. 
		 * */
		//packetIdGenerator = new Random(3008370503L);
		//nodeSelecter = new Random(3009377119L);
		packetIdGenerator = new Random(5);
		nodeSelecter = new Random(3);		
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
			
			int srcNode = nodeSelecter.nextInt(gridSize);
			
			/* This 'if' conditions stops the arrival class from generating anymore interest packets when the total number of 
			 * interest packets has reached
			 * */
			if (!Arrivals.isArrivalStatus()) {	
				
				if (SimulationController.getDistributionType() == SimulationTypes.SIMULATION_DISTRIBUTION_GLOBETRAFF) {
					
					/*
					 * Now we have to parse next line from workload.all and assign the object id for this interest packet.
					 * If we have reached EOF just end this thread thus controller thread which is waiting on this 
					 * notified.
					 * */
					String line = null;
					
					try {				
						
						line = rdr.readLine();
					
						if(line != null) {
							
							String [] words = line.split("\\s+");
							objectID = Integer.parseInt(words[1]);
							objectSize = (int) PacketDistributions.size[objectID];
						}
						else{
							
							rdr.close();
							setArrivalStatus(true);
							System.out.println("Done with Arrivals");
						}
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (SimulationController.getDistributionType() == SimulationTypes.SIMULATION_DISTRIBUTION_DEFAULT) {
					
					objectID = packetIdGenerator.nextInt(PacketDistributions.getNoOfObjects());
					objectSize = (int) PacketDistributions.size[objectID];
					
					if (countInterestPackets >= SimulationController.getMaxSimulatedPackets()-1) { 
						
						setArrivalStatus(true);
						System.out.println("Done with Arrivals");
					}
				}
				
				else if (SimulationController.getDistributionType() == SimulationTypes.SIMULATION_DISTRIBUTION_LEAFNODE) {
					
					objectID = packetIdGenerator.nextInt(PacketDistributions.getNoOfObjects());
					objectSize = (int) PacketDistributions.size[objectID];
					
					srcNode = PacketDistributions.leafNodes.get(nodeSelecter.nextInt(PacketDistributions.leafNodes.size())) ;
					
					if (countInterestPackets >= SimulationController.getMaxSimulatedPackets()-1) { 
						
						setArrivalStatus(true);
						System.out.println("Done with Arrivals");
					}
				}
				
				else if (SimulationController.getDistributionType() == SimulationTypes.SIMULATION_DISTRIBUTION_GLOBETRAFF_LEAFNODE) {
					
					srcNode = PacketDistributions.leafNodes.get(nodeSelecter.nextInt(PacketDistributions.leafNodes.size()));
					
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
							objectSize = (int) PacketDistributions.size[objectID];							
						}
						else{
							
							rdr.close();
							setArrivalStatus(true);
							System.out.println("Done with Arrivals");
						}
					} 
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				/* 
				 * This is not inside the for loop because we want to generate the same packet number for all the packets. 
				 * The packets created in the for loop are cloned, hence use the same packetID (but different segment number) created with the first packet 
				 * */
				
				/* This decrements the defaultInterface value (-ve value) maintained at a node. When a interest packet is created at a node ,
				 * this interface value is assigned. This value is used to identify this interest packet in the PIT table when
				 * the respective data packet is received. 
				*/				
				Grid.getRouter(srcNode).decDefaultInterface();
				
				/* Create an Interest packet */
				InterestPacket firstPacket = new InterestPacket(srcNode, interestPacketSize, 1);
				firstPacket.setRefPacketId(objectID);
				/* PrimaryInterestID is the global marker for this Interest packet. It will remain same throughout all retransmissions
				 * 
				*/ 
				firstPacket.setPrimaryInterestId(firstPacket.getPacketId());
				firstPacket.setPrevHop(Grid.getRouter(srcNode).getDefaultInterface());
				
				Packets.dumpStatistics(firstPacket, "CREATED");
				
				firstPacket.activate();		
				
				/* The following code add this interest packet into the TimeOutQueue. */
				double tempTimeOutValue = SimulationController.CurrentTime() + SimulationController.getPitTimeOut() + 
						SimulationController.getRetransNuance(); 
				
				SimulationController.timeOutQueue.add(new TimeOutFields(firstPacket.getPrimaryInterestId(), firstPacket.getPacketId(), firstPacket.getSegmentId(), 
						firstPacket.getRefPacketId(), firstPacket.getOriginNode(), firstPacket.getExpirationCount(), 
						tempTimeOutValue, false));
				
				/* In case the TimeOutProcess is idle or suspended state, we need to reactivate it. This process will be in this state only
				 * when TimeOutQueue has one element (the one we just entered) 
				 * */
				try {
					if (SimulationController.timeOutQueue.size() == 1 && SimulationController.top.idle()) {
						SimulationController.top.ActivateAt(SimulationController.timeOutQueue.peek().getTimeOutValue(), false);
					}
				} 
				catch (SimulationException e) {
					 //TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (RestartException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//System.out.println("Generated Interest Packet (Actual): " + Packets.getCurrentPacketId());				
				countInterestPackets++;
				
				/* The following code will perform segmentation, if required. */
				double numberOfIntPacks = Math.ceil((double)objectSize/(double)Arrivals.getSegmentSize());			
				
				if (numberOfIntPacks >= 2) {
				
					for (int i = 2; i <= (int)numberOfIntPacks; i++) {
						/* The following statement will randomly choose a source node for the interest packet */
						Grid.getRouter(srcNode).decDefaultInterface();
				    	InterestPacket otherPackets = (InterestPacket) firstPacket.clone();
				    	otherPackets.setSegmentId(i);  	
				    	otherPackets.setCurNode(-1);
				    	otherPackets.setPrevHop(Grid.getRouter(srcNode).getDefaultInterface());
						
						/* 
						 * The following code records the creation of the interest packet 
						 * */			
						Packets.dumpStatistics(otherPackets, "CREATED");			
						
						/* The following statement moves the program control the Packet class, where this interest packet is added into
						 * the source nodes queue 
						 */			
						otherPackets.activate();
						
						/* The TimeOutValue of subsequent segmented interest packets should have a larger time out by the processing delay as they
						 * will wait in queue behind the previous segmented packet (untested idea as of 9/10/2012)
						 * */
						tempTimeOutValue = SimulationController.CurrentTime() + SimulationController.getPitTimeOut() + 
								SimulationController.getRetransNuance() + CCNRouter.getProcDelay() * (i -1); 
						
						SimulationController.timeOutQueue.add(new TimeOutFields(otherPackets.getPrimaryInterestId(), otherPackets.getPacketId(), 
								otherPackets.getSegmentId(), otherPackets.getRefPacketId(), otherPackets.getOriginNode(), 
								otherPackets.getExpirationCount(), tempTimeOutValue, false));
						
						try {
							if (SimulationController.timeOutQueue.size() == 1 && SimulationController.top.idle()) {
								SimulationController.top.ActivateAt(SimulationController.timeOutQueue.peek().getTimeOutValue(), false);
							}
						} 
						catch (SimulationException e) {
							 //TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (RestartException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				}			
				//log.info("Packet generated ");
			}
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

	public static synchronized boolean isArrivalStatus() {
		return arrivalStatus;
	}

	public static void setArrivalStatus(boolean simStatus) {
		arrivalStatus = simStatus;
	}
};
