package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import arjuna.JavaSim.Simulation.SimulationException;

public class Machine extends SimulationProcess
{
    
public Machine (double mean)
    {
	STime = new ExponentialStream(mean);
	operational = true;
	working = false;
	currentPacket = null;
    }

public void run ()
    {
	double ActiveStart, ActiveEnd;

	for (;;)
	{
	    working = true;

	    while (!SimpleServer.PacketsQ.IsEmpty())
	    {
		ActiveStart = CurrentTime();
		SimpleServer.CheckFreq++;

		SimpleServer.PacketssInQueue += SimpleServer.PacketsQ.QueueSize();
		currentPacket = SimpleServer.PacketsQ.Dequeue();
		
		try
		{
		    Hold(ServiceTime());
		}
		catch (SimulationException e)
		{
		}
		catch (RestartException e)
		{
		}

		ActiveEnd = CurrentTime();
		SimpleServer.MachineActiveTime += ActiveEnd - ActiveStart;
		SimpleServer.ProcessedPackets++;

		/*
		 * Introduce this new method because we usually rely upon
		 * the destructor of the object to do the work in C++.
		 */
		
		currentPacket.finished();
	    }

	    working = false;

	    try
	    {
		Cancel();
	    }
	    catch (RestartException e)
	    {
	    }
	}
    }

public void Broken ()
    {
	operational = false;
    }
    
public void Fixed ()
    {
	operational = true;
    }
    
public boolean IsOperational ()
    {
	return operational;
    }
    
public boolean Processing ()
    {
	return working;
    }
    
public double ServiceTime ()
    {
	try
	{
	    return STime.getNumber();
	}
	catch (IOException e)
	{
	    return 0.0;
	}
    }

private ExponentialStream STime;
private boolean operational;
private boolean working;
private Packets currentPacket;

};
