import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import arjuna.JavaSim.Simulation.SimulationException;

public class Arrivals extends SimulationProcess
{
    
public Arrivals (double mean)
    {
	InterArrivalTime = new ExponentialStream(mean);
    }

public void run ()
    {
	for(;;)
	{
	    try
	    {
		Hold(InterArrivalTime.getNumber());
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
	    
	    Job work = new Job(false);
	}
    }
    
private ExponentialStream InterArrivalTime;
    
};
