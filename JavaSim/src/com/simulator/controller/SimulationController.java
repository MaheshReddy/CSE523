package com.simulator.controller;

//import org.apache.log4j.Logger;


import com.simulator.ccn.TimeOutProcess;
import com.simulator.ccn.TimeOutFields;

import com.simulator.ccn.CCNRouter;
import com.simulator.ccn.TransmitPackets;
import com.simulator.distributions.Arrivals;
import com.simulator.distributions.PacketDistributions;
import com.simulator.enums.GridTypes;
import com.simulator.enums.SimulationTypes;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;
import com.simulator.trace.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;

import arjuna.JavaSim.Simulation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/*
 * It is a SimulationProcess (thread). This class initializes various parameters and later transfers the control to JavaSim 
 * (the simulator). After the constructor the code transfer to the "run()" method where the object of Arrivals class is created
 * which is responsible to generate interest packets. Before the simulation starts, the first interest packet is created, and then
 * the simulation is started. The Arrival class object is called intermittently to generate new interest packets. The termination
 * condition is called after a wait of "hold()"; after which the SimulationController thread is 
 * reinvoked. If the termination condition is satisfied the simulation stops. Once new interest packets have stopped, the "timeOutQueue" is
 * checked till its empty. Once all interest packets and their retransmission have completed, the simulation is terminated
 * */

	public class SimulationController extends SimulationProcess {
	
	//static final Logger log = Logger.getLogger(SimulationController.class);;
	private static long packetsGenerated = 0;
	public static long maxSimulatedPackets = 0;
	private static int cacheSize;
	private static int edgeCacheSize;
	private static int coreCacheSize;
	private static int localCacheSize;
	private static int fibSize;
	private static int pitSize;

	private static double pitTimeOut;
	private static double retransNuance;
	
	public static TimeOutProcess top = null;
	public static PriorityBlockingQueue <TimeOutFields> timeOutQueue = null; 
	
	private static int hold = 0;
	
	private GridTypes gridType = GridTypes.SIMULATION_GRID_BRITE;

	private static SimulationTypes distributionType = SimulationTypes.SIMULATION_DISTRIBUTION_DEFAULT;
	private static SimulationTypes cacheType = SimulationTypes.SIMULATION_UNLIMITED_CACHESIZE;
	private static SimulationTypes cacheAvailability = SimulationTypes.SIMULATION_NO_CACHE;
	private static SimulationTypes fibAvailability = SimulationTypes.SIMULATION_NO_FIB;
	private static SimulationTypes debugging = SimulationTypes.SIMULATION_DEBUGGING_ON;
	private static SimulationTypes objectSegmentation = SimulationTypes.SIMULATION_SEG_OFF;
	private static SimulationTypes cacheEntries = SimulationTypes.SIMULATION_CACHE_ENTRIES_OFF;
	private static SimulationTypes cacheFibEntries = SimulationTypes.SIMULATION_CACHE_FIB_TRACE_OFF;
	

	public static Writer fs = null;
	
    /**
     * Reads "ccn.properties" file and sets the corresponding simulation parameters.
     * @throws Exception
     */
	public SimulationController () throws Exception	{
		
		Properties prop = new Properties();
		
		/* Initializing the simulation */		
		try {
			
			prop.load(ClassLoader.getSystemResourceAsStream("ccn.properties"));			
			
			setHold(Integer.parseInt(prop.getProperty("ccn.hold.time")));			
			PacketDistributions.setNoOfObjects(Integer.parseInt(prop.getProperty("ccn.no.objects")));			
			PacketDistributions.setObjectSize(Integer.parseInt(prop.getProperty("ccn.sizeOf.objects")));			
			Arrivals.setInterestPacketSize(Integer.parseInt(prop.getProperty("ccn.sizeOf.interestpackets")));	
			Arrivals.setSegmentSize(Integer.parseInt(prop.getProperty("ccn.sizeOf.segment")));
			setMaxSimulatedPackets(Integer.parseInt(prop.getProperty("ccn.no.simulationpackets")));			
			Packets.setDataDumpFile(prop.getProperty("dumpfile.packets"));			
			setGridType(GridTypes.valueOf(prop.getProperty("ccn.topology")));			
			setDistributionType (SimulationTypes.valueOf(prop.getProperty("ccn.object.distribution")));			
			setObjectSegmentation (SimulationTypes.valueOf(prop.getProperty("ccn.object.segmentation")));			
			setCacheType (SimulationTypes.valueOf(prop.getProperty("ccn.cachesize.type")));			
			setCacheAvailability (SimulationTypes.valueOf(prop.getProperty("ccn.cache")));			
			setFibAvailability (SimulationTypes.valueOf(prop.getProperty("ccn.fib")));			
			setCacheSize(Integer.parseInt(prop.getProperty("ccn.cache.size")));			
			setEdgeCacheSize(Integer.parseInt(prop.getProperty("ccn.edgecache.size")));			
			setCoreCacheSize(Integer.parseInt(prop.getProperty("ccn.corecache.size")));			
			setCacheEntries(SimulationTypes.valueOf(prop.getProperty("ccn.cache.entries")));			
			setCacheFibTrace(SimulationTypes.valueOf(prop.getProperty("ccn.cachefib.trace")));			
			setDebugging (SimulationTypes.valueOf(prop.getProperty("ccn.debgging")));			
			setLocalCacheSize(Integer.parseInt(prop.getProperty("ccn.localcache.size")));			
			CCNRouter.setProcDelay(Double.parseDouble(prop.getProperty("ccn.delay.processing")));			
			TransmitPackets.setTransDelay(Double.parseDouble(prop.getProperty("ccn.delay.transmitting")));
			Arrivals.setLoadImpact(Double.parseDouble(prop.getProperty("ccn.load.impact")));	
			setPitTimeOut(Double.parseDouble(prop.getProperty("ccn.pit.timeout")));			
			setRetransNuance(Double.parseDouble(prop.getProperty("ccn.retransmission.nuance")));			
			setPITSize(Integer.parseInt(prop.getProperty("ccn.pit.size")));			
			setFIBSize(Integer.parseInt(prop.getProperty("ccn.fib.size")));			
			PacketDistributions.setAllDocs(prop.getProperty("ccn.globeTraff.alldocs"));			
			Arrivals.setWorkload(prop.getProperty("ccn.globeTraff.workload"));
		} 
		catch (IOException e) {
			
			// TODO Auto-generated catch block
			//log.info("Couldn't load properties file using default values");
			PacketDistributions.setNoOfObjects(10);
			setMaxSimulatedPackets(500);
			e.printStackTrace();
		}
		
		timeOutQueue = new PriorityBlockingQueue <TimeOutFields> (200000, new Comparator<TimeOutFields>() {
			public int compare(TimeOutFields to1, TimeOutFields to2) {
				if (to1.getTimeOutValue() < to2.getTimeOutValue()) {
            		return -1;
            	}
            	else if (to1.getTimeOutValue() > to2.getTimeOutValue()) {
            		return 1;
            	}
            	else {
            		return 0;
            	}
            }
        });
		
		/* The following method will create the appropriate topology based on the choice provided in the ccn.properties file */
		Grid.createTopology(gridType);
		
		/* The following function will distributed the objects amongst various nodes based on docs.all file of
		 * Globe Traffic.  
		 * */	
			
		PacketDistributions.distributeContent(distributionType);
		
		fs = new BufferedWriter(new FileWriter(Packets.getDataDumpFile(),true));
	}
	/* The following method is called after the constructor. It will create an object of the Arrivals class which is responsible
	 * to generate interest packets based on the frequency provided in ccn.properties 
	 * */
	public void run () {
		
		try {
			
			//log.info("Starting Simulation -- Config\n Grid Size:"+Grid.getGridSize());
			
			/*
			 * Arrivals object is created along with setting of the frequency with which it will be reinvoked again, and again ..
			 * Moreover, the first interest packet is created. 
			 * */
			Arrivals A = new Arrivals();
			
			/* If Cache and FIB trace is required, then we create the CacheFIBTrace process which is invoked intermittently to print
			 * the sizes of all caches and fibs.*/
			if (SimulationController.getCacheFibTrace() == SimulationTypes.SIMULATION_CACHE_FIB_TRACE_ON) {
				CacheFIBTrace B = new CacheFIBTrace ();
				B.ActivateAt(1.0);
			}
			
			/* The following call will place the Arrival object onto the JavaSim scheduler's queue. */
			A.Activate();
			
			top = new TimeOutProcess();

			/* The simulation is started, and JavaSim scheduler comes to life */
			Scheduler.startSimulation();
			
			/**
			 //TODO
			 * Joining this thread to Arrivals Thread. By doing this we are blocking this thread till finished execution.
			 * Initial Hold is necessary to as to make Arrivals thread run.
			 */
			/**
			 * For now using a workaround of using a flag to indicate end of simulation.
			 */
			
			while(!Arrivals.isArrivalStatus())	{
		
				System.out.println("Number of Interest Packets Generated: " + Packets.getCurrentPacketId());
				System.out.println("Number of Data Packets Received: " + CCNRouter.countDataPacketsReceived);
				Hold(SimulationController.getHold());				
			}
			
			/* The following code insures that the TimeOutQueue is empty before the simulation is terminated */
			while(true) {
				
				boolean terminate = true;
				
				if (SimulationController.timeOutQueue.size() != 0) {
					terminate = false;
				}
				
				if (!terminate) 
					Hold(10);
				else 
					break;			
			}
			
					
			/* The following 'while' loop will execute till all router queues are empty */		
			while (true) {
				
				/* Loop over all routers */
				boolean terminate = true;	
				for(int i=0;i<Grid.getGridSize();i++) {
					if (!Grid.getRouter(i).getPacketsQ().isEmpty()) {						
						System.out.println("The queue of Router + " + Grid.getRouter(i).getRouterId() + " has " + Grid.getRouter(i).getPacketsQ().packetsInCCNQueue() + " packets");
						terminate = false;
					}
				}

				System.out.println("Supposedly, this loop should not execute more than once only when the timeOutQueue will" +
						"be empty, will the code reach this point\n");
				
				System.out.println("TimeOutQueue " + SimulationController.timeOutQueue.size());
				
				/* If they are not empty, wait for 10 'ticks', and execute this thread again */
				if (!terminate) 
					Hold(10);
				else 
					break;				
			}
			
			/* Stops the simulation */
			Scheduler.stopSimulation();
			
			SimulationController.fs.close();

			
			//log.info("Done with simulation");
			//log.info("Final Router configurations--------->");			
			
			
			for(int i=0;i<Grid.getGridSize();i++)
				Grid.getRouter(i).terminate();
			
			A.terminate();
			
			SimulationProcess.mainResume();
		}
		catch (SimulationException e) {}
		catch (RestartException e) {}
		catch (IOException e) {}
	}

	/* 
	 * The following method is a standard requirement of JavaSim implementation. It is called from the main function of this 
	 * class
	 *  */
	
	public void Await () {
		
		this.Resume();
		SimulationProcess.mainSuspend();
	}

	public GridTypes getGridType() {
		return gridType;
	}

	public void setGridType(GridTypes gridType) {
		this.gridType = gridType;
	}
	
	public static SimulationTypes getDistributionType() {
		return distributionType;
	}

	public static void setDistributionType(SimulationTypes distType) {
		SimulationController.distributionType = distType;
	}
	
	public static SimulationTypes getDebugging() {
		return debugging;
	}

	public static void setDebugging(SimulationTypes debugging) {
		SimulationController.debugging = debugging;
	}
	
	public static void setCacheEntries(SimulationTypes cEntries) {
		SimulationController.cacheEntries = cEntries;
	}
	
	public static SimulationTypes getCacheEntries() {
		return cacheEntries;
	}
	
	public static void setCacheFibTrace(SimulationTypes cFTrace) {
		SimulationController.cacheFibEntries = cFTrace;
	}
	
	public static SimulationTypes getCacheFibTrace() {
		return cacheFibEntries;
	}
	
	public static void setCacheType(SimulationTypes cacheType) {
		SimulationController.cacheType = cacheType;
	}
	
	public static SimulationTypes getCacheType() {
		return cacheType;	
	}
	
	
	public static void setCacheAvailability(SimulationTypes cacheAvail) {
		SimulationController.cacheAvailability = cacheAvail;
	}
	
	public static SimulationTypes getCacheAvailability() {
		return cacheAvailability;
	}
	
	public static void setFibAvailability(SimulationTypes fibAvail) {
		SimulationController.fibAvailability = fibAvail;
	}
	
	public static SimulationTypes getFibAvailability() {
		return fibAvailability;
	}
	
	public static void setObjectSegmentation(SimulationTypes seg) {
		SimulationController.objectSegmentation = seg;
	}
	
	public static SimulationTypes getObjectSegmentation() {
		return objectSegmentation;
	}
	
	public static void setCacheSize (int cacheSize) {
		SimulationController.cacheSize = cacheSize;
	}
	
	public static void setEdgeCacheSize (int cacheSize) {
		SimulationController.edgeCacheSize = cacheSize;
	}
	
	public static void setCoreCacheSize (int cacheSize) {
		SimulationController.coreCacheSize = cacheSize;
	}
	
	public static int getCacheSize () {
		return cacheSize;
	}
	
	public static int getEdgeCacheSize () {
		return edgeCacheSize;
	}
	
	public static int getCoreCacheSize () {
		return coreCacheSize;
	}
	
	public static void setLocalCacheSize (int cacheSize) {
		SimulationController.localCacheSize = cacheSize;
	}
	
	public static int getLocalCacheSize () {
		return localCacheSize;
	}
	
	public static void setFIBSize (int size) {
		SimulationController.fibSize = size;
	}
	
	public static int getFIBSize () {
		return fibSize;
	}
	
	public static void setPITSize (int size) {
		SimulationController.pitSize = size;
	}
	
	public static int getPITSize () {
		return pitSize;
	}
	
	public static void setHold (int hold) {
		SimulationController.hold = hold;
	}
	
	public static int getHold () {
		return hold;
	}

	public synchronized static void incrementPacketsProcessed() {
		packetsGenerated=packetsGenerated+1;
	}

	public static long getPacketsGenerated() {
		return packetsGenerated;
	}

	public static long getMaxSimulatedPackets() {
		return maxSimulatedPackets;
	}

	public static void setMaxSimulatedPackets(long maxSimulatedPackets) {
		SimulationController.maxSimulatedPackets = maxSimulatedPackets;
	}
	
	public static boolean cacheType() {
		
		if (getCacheType() == SimulationTypes.SIMULATION_UNLIMITED_CACHESIZE) {
			return true;
		}		
		return false;		
	}
	
	public static double getPitTimeOut (){
		return pitTimeOut;
	}

	public static void setPitTimeOut (double tempTimeOut) {
		pitTimeOut = tempTimeOut;
	}
	
	public static double getRetransNuance (){
		return retransNuance;
	}

	public static void setRetransNuance (double tempNuance) {
		retransNuance = tempNuance;
	}
	

	public static void main (String[] args) {

		SimulationController ctrl;
		
		/* Created the SimulationController object */
		try {
			ctrl = new SimulationController();
		}
		catch (Exception e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		/* Standard call made as it is a requirement of JavaSim. It is essentially to suspend the main thread, and pass on
		 * execution to the other threads executing the simulation (Read JavaSim manual for further clarification)
		 *  */
		ctrl.Await();
		
		/* The following is a user-defined class which helps to categorize the trace file. It assists in verifying the trace
		 * file.
		 * */
		if (SimulationController.getDebugging() == SimulationTypes.SIMULATION_DEBUGGING_ON) {
			new ManipulateTrace (Packets.getDataDumpFile());
		}	
		
		System.out.println("Done simulation exiting");			
		System.exit(0);
	}
};
