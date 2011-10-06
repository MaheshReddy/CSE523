import arjuna.JavaSim.Simulation.*;

import arjuna.JavaSim.Simulation.SimulationException;

public class MachineShop extends SimulationEntity
{

public MachineShop ()
    {
    }

public void run ()
    {
	try
	{
	    Signaller s = new Signaller(1000);
	    Arrivals A = new Arrivals(2);
	    MachineShop.cpu = new Processor(10);
	    Job J = new Job(false);

	    MachineShop.cpu.Activate();
	    A.Activate();
	    s.Activate();

	    Scheduler.startSimulation();

	    WaitFor(cpu);

	    System.out.println("Total jobs processed "+ProcessedJobs);
	    System.out.println("Total signals processed "+SignalledJobs);

	    Scheduler.stopSimulation();

	    MachineShop.cpu.terminate();
	    A.terminate();
	    s.terminate();

	    SimulationProcess.mainResume();
	}
	catch (SimulationException e)
	{
	}
	catch (InterruptedException e)
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

public static Processor cpu = null;
public static Queue JobQ = new Queue();
public static Queue SignalQ = new Queue();
public static long ProcessedJobs = 0;
public static long SignalledJobs = 0;
public static double TotalResponseTime = 0.0;
public static double MachineActiveTime = 0.0;
    
};
