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
	J = null;
    }

public void run ()
    {
	double ActiveStart, ActiveEnd;

	for (;;)
	{
	    working = true;

	    while (!MachineShop.JobQ.IsEmpty())
	    {
		ActiveStart = CurrentTime();
		MachineShop.CheckFreq++;

		MachineShop.JobsInQueue += MachineShop.JobQ.QueueSize();
		J = MachineShop.JobQ.Dequeue();
		
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
		MachineShop.MachineActiveTime += ActiveEnd - ActiveStart;
		MachineShop.ProcessedJobs++;

		/*
		 * Introduce this new method because we usually rely upon
		 * the destructor of the object to do the work in C++.
		 */
		
		J.finished();
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
private Job J;

};
