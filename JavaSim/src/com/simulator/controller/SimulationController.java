package com.simulator.controller;

import org.apache.log4j.Logger;

import com.simulator.ccn.CCNRouter;
import com.simulator.ccn.TransmitPackets;
import com.simulator.distributions.Arrivals;
import com.simulator.distributions.PacketDistributions;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;
import com.simulator.topology.SimulationTypes;

import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class SimulationController extends SimulationProcess
{
	static final Logger log = Logger.getLogger(SimulationController.class);;
	private static long packetsGenerated = 0;
	public static long maxSimulatedPackets = 0;
	private SimulationTypes gridType = SimulationTypes.SIMULATION_GRID_BRITE;
    /**
     * Reads "ccn.properties" file and sets the corresponding simulation parameters.
     * @throws Exception
     */
	public SimulationController () throws Exception
	{
		// Create the grid first
		Properties prop = new Properties();
		try {
			prop.load(ClassLoader.getSystemResourceAsStream("ccn.properties"));
			setNoDataPackets(Integer.parseInt(prop.getProperty("ccn.no.datapackets")));
			setNoNodes(Integer.parseInt(prop.getProperty("ccn.no.nodes")));
			setMaxSimulatedPackets(Integer.parseInt(prop.getProperty("ccn.no.simulationPackets")));
			Packets.setDataDumpFile(prop.getProperty("dumpfile.packets"));
			setGridType(SimulationTypes.valueOf(prop.getProperty("ccn.topology")));
			CCNRouter.setProcDelay(Double.parseDouble(prop.getProperty("ccn.delay.processing")));
			TransmitPackets.setTransDelay(Double.parseDouble(prop.getProperty("ccn.delay.transmitting")));
			Arrivals.setArvDelay(Double.parseDouble(prop.getProperty("ccn.delay.arrivals")));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("Couldn't load properties file using default values");
			setNoDataPackets(10);
			setNoNodes(5);
			setMaxSimulatedPackets(500);
			e.printStackTrace();
		}

		Grid.createTopology(gridType);
		PacketDistributions.distributeContent();
	}

	public SimulationController(Integer noDataPackets,Integer noNodes) throws Exception
	{
		setNoDataPackets(noDataPackets);
		setNoNodes(noNodes);
		Grid.createTopology(gridType);
		PacketDistributions.distributeContent();
	}


	public void run ()
	{
		try
		{
			log.info("Starting Simulation -- Config\n Grid Size:"+Grid.getGridSize());
			Arrivals A = new Arrivals(Arrivals.getArvDelay());
			A.Activate();

			Scheduler.startSimulation();

			while (Packets.getCurrenPacketId() < getMaxSimulatedPackets())
			{ 
				System.out.println(getPacketsGenerated()+" "+getMaxSimulatedPackets());
				Hold(10);
			}
			Scheduler.stopSimulation();
			log.info("Done with simulation");
			log.info("Final Router configrations--------->");
			for(int i=0;i<Grid.getGridSize();i++)
				log.info(Grid.getRouter(i));
			A.terminate();
			SimulationProcess.mainResume();
		}
		catch (SimulationException e)
		{
		}
		catch (RestartException e)
		{
		}
	}

	public void Await ()
	{
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

	public synchronized static void incrementPacketsProcessed()
	{
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
	public static void main (String[] args)
	{

		//CCNRouter m = new CCNRouter(8);
		SimulationController ctrl;
		try {
			ctrl = new SimulationController();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		ctrl.Await();
		System.out.println("Done simulation exiting");
		System.exit(0);
	}
};
