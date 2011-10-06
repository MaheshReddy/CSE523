import arjuna.JavaSim.*;

public class Job
{

public Job (boolean isSignal)
    {
	if (isSignal)
	    MachineShop.SignalQ.Enqueue(this);
	else
	    MachineShop.JobQ.Enqueue(this);
    }

};
