import java.lang.*;
import arjuna.JavaSim.Simulation.*;

public class Job
{
    
public Job ()
    {
	boolean empty = false;

	ResponseTime = 0.0;
	ArrivalTime = Scheduler.CurrentTime();

	empty = MachineShop.JobQ.IsEmpty();
	MachineShop.JobQ.Enqueue(this);
	MachineShop.TotalJobs++;

	if (empty && !MachineShop.M.Processing() && MachineShop.M.IsOperational())
	{
	    try
	    {
		MachineShop.M.Activate();
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
	MachineShop.TotalResponseTime += ResponseTime;	
    }

private double ResponseTime;
private double ArrivalTime;
    
};
