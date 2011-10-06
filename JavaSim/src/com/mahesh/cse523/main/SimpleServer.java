package com.mahesh.cse523.main;
import java.lang.*;
import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

public class SimpleServer extends SimulationProcess
{
    
public SimpleServer ()
    {
    }
    
public void run ()
    {
	try
	{
	    Arrivals A = new Arrivals(8);
	    SimpleServer.M = new Machine(8);
	    Packets J = new Packets();
	    
	    A.Activate();
	    

	    Scheduler.startSimulation();

	    while (SimpleServer.ProcessedPackets < 1000)
		Hold(1000);

	    System.out.println("Total number of Packetss present "+TotalPackets);
	    System.out.println("Total number of Packetss processed "+ProcessedPackets);
	    System.out.println("Total response time of "+TotalResponseTime);
	    System.out.println("Average response time = "+(TotalResponseTime / ProcessedPackets));
	    System.out.println("Probability that machine is active = "+MachineActiveTime);
	    System.out.println("Average number of Packetss present = "+(PacketssInQueue / CheckFreq));
    
	    Scheduler.stopSimulation();

	    A.terminate();
	    SimpleServer.M.terminate();
    
	    
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

public static Machine M = null;
public static Queue PacketsQ = new Queue();
public static double TotalResponseTime = 0.0;
public static long TotalPackets = 0;
public static long ProcessedPackets = 0;
public static long PacketssInQueue = 0;
public static long CheckFreq = 0;
public static double MachineActiveTime = 0.0;
public static double MachineFailedTime = 0.0;
    
private boolean useBreaks;
    
};
