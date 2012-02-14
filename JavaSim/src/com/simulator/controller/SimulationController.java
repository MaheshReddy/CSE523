package com.simulator.controller;

import org.apache.log4j.Logger;


import com.simulator.ccn.CCNRouter;
import com.simulator.ccn.TransmitPackets;
import com.simulator.distributions.Arrivals;
import com.simulator.distributions.PacketDistributions;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;
import com.simulator.topology.SimulationTypes;
import com.simulator.trace.*;

import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * It is a SimulationProcess (thread). This class initializes various parameters and later transfers the control to JavaSim 
 * (the simulator). After the constructor the code transfer to the "run()" method where the object of Arrivals class is created
 * which is responsible to generate interest packets. Before the simulation starts, the first interest packet is created, and then
 * the simulation is started. The Arrival class object is called intermittently to generate new interest packets. The termination
 * condition is called after a wait of "hold(holdTerminationVerification)"; after which the SimulationController thread is 
 * reinvoked. If the termination condition is satisfied the simulation stops.   
 * */

public class SimulationController extends SimulationProcess {
	
	static final Logger log = Logger.getLogger(SimulationController.class);;
	private static long packetsGenerated = 0;
	public static long maxSimulatedPackets = 0;
	public static int holdTerminationVerification = 0;
	private SimulationTypes gridType = SimulationTypes.SIMULATION_GRID_BRITE;
    /**
     * Reads "ccn.properties" file and sets the corresponding simulation parameters.
     * @throws Exception
     */
	public SimulationController () throws Exception	{
		
		Properties prop = new Properties();
		
		/* Initializing the simulation */		
		try {
			
			prop.load(ClassLoader.getSystemResourceAsStream("ccn.properties"));
			setNoDataPackets(Integer.parseInt(prop.getProperty("ccn.no.datapackets")));
			PacketDistributions.setDataPacketSize(Integer.parseInt(prop.getProperty("ccn.sizeOf.datapackets")));
			Arrivals.setInterestPacketSize(Integer.parseInt(prop.getProperty("ccn.sizeOf.interestpackets")));
			setNoNodes(Integer.parseInt(prop.getProperty("ccn.no.nodes")));
			setMaxSimulatedPackets(Integer.parseInt(prop.getProperty("ccn.no.simulationpackets")));
			setHoldTerminationVerification(Integer.parseInt(prop.getProperty("ccn.termination.verification")));
			Packets.setDataDumpFile(prop.getProperty("dumpfile.packets"));
			setGridType(SimulationTypes.valueOf(prop.getProperty("ccn.topology")));
			CCNRouter.setProcDelay(Double.parseDouble(prop.getProperty("ccn.delay.processing")));
			TransmitPackets.setTransDelay(Double.parseDouble(prop.getProperty("ccn.delay.transmitting")));
			Arrivals.setArvDelay(Double.parseDouble(prop.getProperty("ccn.delay.arrivals")));	
			CCNRouter.setPitTimeOut(Double.parseDouble(prop.getProperty("ccn.pit.timeout")));
		} 
		catch (IOException e) {
			
			// TODO Auto-generated catch block
			log.info("Couldn't load properties file using default values");
			setNoDataPackets(10);
			setNoNodes(5);
			setMaxSimulatedPackets(500);
			e.printStackTrace();
		}
		
		/* The following method will create the appropriate topology based on the choice provided in the ccn.properties file */
		Grid.createTopology(gridType);
		
		/* The following function will distributed the objects amongst various nodes based on some distribution. Presently,
		 * this is normal distribution among all the nodes 
		 * */
		PacketDistributions.distributeContent();
	}

	public SimulationController(Integer noDataPackets,Integer noNodes) throws Exception	{
		
		setNoDataPackets(noDataPackets);
		setNoNodes(noNodes);
		setHoldTerminationVerification (10);
		Grid.createTopology(gridType);
		PacketDistributions.distributeContent();
	}
	
	/* The following method is called after the constructor. It will create an object of the Arrivals class which is responsible
	 * to generate interest packets based on the frequency provided in ccn.properties 
	 * */
	public void run () {
		
		try {
			
			log.info("Starting Simulation -- Config\n Grid Size:"+Grid.getGridSize());
			
			/*
			 * Arrivals object is created along with setting of the frequency with which it will be reinvoked again, and again ..
			 * Moreover, the first interest packet is created. 
			 * */
			Arrivals A = new Arrivals(Arrivals.getArvDelay());
			
			/* The following call will place the Arrival object onto the JavaSim scheduler's queue. */
			A.Activate();

			/* The simulation is started, and JavaSim scheduler comes to life */
			Scheduler.startSimulation();

			/* The termination condition. It will be checked according to the value provided in ccn.properties. The larger the 
			 * value, the longer will the simulation last. We are not planning on an exact point of termination for this 
			 * simulation 
			 * */
			while (Packets.getCurrenPacketId() < getMaxSimulatedPackets()) {
				
				System.out.println(getPacketsGenerated()+" "+getMaxSimulatedPackets());
				Hold(holdTerminationVerification);
			}
			
			/* Stops the simulation */
			Scheduler.stopSimulation();
			
			log.info("Done with simulation");
			log.info("Final Router configrations--------->");
			
			for(int i=0;i<Grid.getGridSize();i++)
				log.info(Grid.getRouter(i));
			
			A.terminate();
			SimulationProcess.mainResume();
		}
		catch (SimulationException e) {}
		catch (RestartException e) {}
	}

	/* 
	 * The following method is a standard requirement of JavaSim implementation. It is called from the main function of this 
	 * class
	 *  */
	public void Await () {
		
		this.Resume();
		SimulationProcess.mainSuspend();
	}

	public Integer getNoDataPackets() {
		return PacketDistributions.getNoDataPackets();
	}

	public void setNoDataPackets(Integer noDataPackets) {
		PacketDistributions.setnoDataPackets(noDataPackets);
	}
	public Integer getNoNodes() {
		return Grid.getGridSize();
	}

	public void setNoNodes(Integer noNodes) {
		Grid.setGridSize(noNodes);
	}

	public SimulationTypes getGridType() {
		return gridType;
	}

	public void setGridType(SimulationTypes gridType) {
		this.gridType = gridType;
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
	
	public static void setHoldTerminationVerification (int holdTerminationVerification) {
		SimulationController.holdTerminationVerification = holdTerminationVerification;
	}
	
	public static int getHoldTerminationVerification () {
		return holdTerminationVerification;
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
		
		/* Standard call made as it is a requirement of JavaSim. It is essentiallly to suspend the main thread, and pass on
		 * execution to the other threads executing the simulation (Read JavaSim manual for further clarification)
		 *  */
		ctrl.Await();
		
		/* The following is a user-defined class which helps to categorize the trace file. It assists in verifying the trace
		 * file.
		 * */
		ManipulateTrace m = new ManipulateTrace ("dump/trace.txt");
		
		System.out.println("Done simulation exiting");
		System.exit(0);
	}
};
