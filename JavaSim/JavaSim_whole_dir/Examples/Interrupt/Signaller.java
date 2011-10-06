import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import arjuna.JavaSim.Simulation.SimulationException;

public class Signaller extends SimulationEntity
{
    
public Signaller (double mean)
    {
	sTime = new ExponentialStream(mean);
    }
    
public void run ()
    {
	for (;;)
	{
	    try
	    {
		Hold(sTime.getNumber());
		Job j = new Job(true);
		Interrupt(MachineShop.cpu, false);
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
