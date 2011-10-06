package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;

public class Packets
{
    
public Packets ()
    {
	boolean empty = false;

	ResponseTime = 0.0;
	ArrivalTime = Scheduler.CurrentTime();

	empty = SimpleServer.PacketsQ.IsEmpty();
	SimpleServer.PacketsQ.Enqueue(this);
	SimpleServer.TotalPackets++;

	if (empty && !SimpleServer.M.Processing() && SimpleServer.M.IsOperational())
	{
	    try
	    {
		SimpleServer.M.Activate();
	    }
	    catch (SimulationException e)
	    {
	    }
	    catch (RestartException e)
	    {
	    }
	}
    }

public void finished ()
    {
	ResponseTime = Scheduler.CurrentTime() - ArrivalTime;
	SimpleServer.TotalResponseTime += ResponseTime;	
    }

private double ResponseTime;
private double ArrivalTime;
    
};
