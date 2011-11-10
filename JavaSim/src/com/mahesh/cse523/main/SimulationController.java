package com.mahesh.cse523.main;

import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

public class SimulationController extends SimulationProcess
{
	static final Logger log = Logger.getLogger(SimulationController.class);;
	public static long packetsGenerated = 0;
	private SimulationTypes gridType = SimulationTypes.SIMULATION_GRID_MESH;

public SimulationController ()
    {
	// Create the grid first
	setNoDataPackets(100);
    setNoNodes(4);
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

	    while (packetsGenerated < 5)
	    { 
		Hold(1);
	    }
	    Scheduler.stopSimulation();
	    log.info("Done with simulation");

	    A.terminate();
    
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
    
};
