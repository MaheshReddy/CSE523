package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

public class SimulationController extends SimulationProcess
{
	public static long packetsGenerated = 0;

public SimulationController ()
    {
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
	    System.out.println("Done with simulation");

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

    
};
