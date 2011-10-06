import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import arjuna.JavaSim.Simulation.SimulationException;

public class Breaks extends SimulationProcess
{
    
public Breaks ()
    {
	RepairTime = new UniformStream(10, 100);
	OperativeTime = new UniformStream(200, 500);
	interrupted_service = false;
    }

public void run ()
    {
	for(;;)
	{
	    try
	    {
		double failedTime = RepairTime.getNumber();
		
		Hold(OperativeTime.getNumber());

		MachineShop.M.Broken();
		MachineShop.M.Cancel();

		if(!MachineShop.JobQ.IsEmpty())
		    interrupted_service = true;

		Hold(failedTime);

		MachineShop.MachineFailedTime += failedTime;
		MachineShop.M.Fixed();
	
		if (interrupted_service)
		    MachineShop.M.ActivateAt(MachineShop.M.ServiceTime() + CurrentTime());
		else
		    MachineShop.M.Activate();
		
		interrupted_service = false;
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

private UniformStream RepairTime;
private UniformStream OperativeTime;
private boolean interrupted_service;
    
};
