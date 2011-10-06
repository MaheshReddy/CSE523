import java.lang.*;
import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

public class MachineShop extends SimulationProcess
{
    
public MachineShop (boolean isBreaks)
    {
	useBreaks = isBreaks;
    }
    
public void run ()
    {
	try
	{
	    Breaks B = null;
	    Arrivals A = new Arrivals(8);
	    MachineShop.M = new Machine(8);
	    Job J = new Job();
	    
	    A.Activate();
	    
	    if (useBreaks)
	    {
		B = new Breaks();
		B.Activate();
	    }

	    Scheduler.startSimulation();

	    while (MachineShop.ProcessedJobs < 1000)
		Hold(1000);

	    System.out.println("Total number of jobs present "+TotalJobs);
	    System.out.println("Total number of jobs processed "+ProcessedJobs);
	    System.out.println("Total response time of "+TotalResponseTime);
	    System.out.println("Average response time = "+(TotalResponseTime / ProcessedJobs));
	    System.out.println("Probability that machine is working = "+((MachineActiveTime - MachineFailedTime) / CurrentTime()));
	    System.out.println("Probability that machine has failed = "+(MachineFailedTime / MachineActiveTime));
	    System.out.println("Average number of jobs present = "+(JobsInQueue / CheckFreq));
    
	    Scheduler.stopSimulation();

	    A.terminate();
	    MachineShop.M.terminate();
    
	    if (useBreaks)
		B.terminate();

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
public static Queue JobQ = new Queue();
public static double TotalResponseTime = 0.0;
public static long TotalJobs = 0;
public static long ProcessedJobs = 0;
public static long JobsInQueue = 0;
public static long CheckFreq = 0;
public static double MachineActiveTime = 0.0;
public static double MachineFailedTime = 0.0;
    
private boolean useBreaks;
    
};
