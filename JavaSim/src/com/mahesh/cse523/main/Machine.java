package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import arjuna.JavaSim.Simulation.SimulationException;

public class Machine extends SimulationProcess
{
	private ExponentialStream STime;
	private boolean operational;
	private boolean working;
	private Packets currentPacket;
	private Queue packetsQ;
public Machine (Queue queue, double mean)
    {
	STime = new ExponentialStream(mean);
	operational = true;
	working = false;
	currentPacket = null;
	packetsQ=queue;
    }

public void run ()
    {
	double ActiveStart, ActiveEnd;

	for (;;)
	{
	    working = true;

	    while (!packetsQ.IsEmpty())
	    {
		ActiveStart = CurrentTime();
//		CCNRouter.CheckFreq++;

	//	CCNRouter.PacketssInQueue += CCNRouter.PacketsQ.QueueSize();
		currentPacket = packetsQ.Dequeue();
		System.out.println("got following packet->"+currentPacket.getPacketId());
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
		//CCNRouter.MachineActiveTime += ActiveEnd - ActiveStart;
		//CCNRouter.ProcessedPackets++;

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


};
