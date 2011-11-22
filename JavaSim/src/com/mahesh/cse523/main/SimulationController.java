package com.mahesh.cse523.main;

import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class SimulationController extends SimulationProcess
{
	static final Logger log = Logger.getLogger(SimulationController.class);;
	public static long packetsGenerated = 0;
	public static long maxSimulatedPackets = 0;
	private SimulationTypes gridType = SimulationTypes.SIMULATION_GRID_MESH;

public SimulationController ()
    {
	// Create the grid first
	Properties prop = new Properties();
	try {
		prop.load(ClassLoader.getSystemResourceAsStream("ccn.properties"));
		setNoDataPackets(Integer.parseInt(prop.getProperty("ccn.no.datapackets")));
		setNoNodes(Integer.parseInt(prop.getProperty("ccn.no.nodes")));
		setMaxSimulatedPackets(Integer.parseInt(prop.getProperty("ccn.no.simulationPackets")));
		Packets.setDataDumpFile(prop.getProperty("dumpfile.packets"));
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

public SimulationController(Integer noDataPackets,Integer noNodes)
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
	    Arrivals A = new Arrivals(8);
	    A.Activate();

	    Scheduler.startSimulation();

	    while (getPacketsGenerated() < getMaxSimulatedPackets())
	    { 
	    //System.out.println(packetsGenerated);
		Hold(1);
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
	packetsGenerated++;
}

public static long getPacketsGenerated() {
	return packetsGenerated;
}

public static void setPacketsGenerated(long packetsGenerated) {
	SimulationController.packetsGenerated = packetsGenerated;
}

public static long getMaxSimulatedPackets() {
	return maxSimulatedPackets;
}

public static void setMaxSimulatedPackets(long maxSimulatedPackets) {
	SimulationController.maxSimulatedPackets = maxSimulatedPackets;
}
    
};
