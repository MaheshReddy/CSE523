import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import arjuna.JavaSim.Simulation.SimulationException;

public class Processor extends SimulationEntity
{

public Processor (double mean)
    {
	sTime = new ExponentialStream(mean);
    }

public void run ()
    {
	Job j = null;

	for (;;)
	{
	    try
	    {
		try
		{
		    Wait(sTime.getNumber());

		    if (!MachineShop.JobQ.IsEmpty())
		    {
			j = MachineShop.JobQ.Dequeue();
			MachineShop.ProcessedJobs++;
		    }
		}
		catch (InterruptedException e)
		{
		    if (MachineShop.SignalQ.IsEmpty())
			System.out.println("Error - signal caught, but no message given!");
		    else
		    {
			j = MachineShop.SignalQ.Dequeue();
			MachineShop.SignalledJobs++;
		    }
		}

		if (MachineShop.SignalledJobs == 2)
		    terminate();
	    }
	    catch (SimulationException e)
	    {
	    }
	    catch (RestartException e)
	    {
	    }
	    catch (IOException e)
	    {
	    }
	}
    }

private ExponentialStream sTime;
    
};
