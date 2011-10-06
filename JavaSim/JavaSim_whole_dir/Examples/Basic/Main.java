import java.lang.*;
import arjuna.JavaSim.*;

public class Main
{

public static void main (String[] args)
    {
	boolean isBreaks = false;
    
	for (int i = 0; i < args.length; i++)
	{
	    if (args[i].equalsIgnoreCase("-help"))
	    {
		System.out.println("Usage: Main [-breaks] [-help]");
		System.exit(0);
	    }
	    if (args[i].equalsIgnoreCase("-breaks"))
		isBreaks = true;
	}

	MachineShop m = new MachineShop(isBreaks);

	m.Await();

	System.exit(0);
    }
    
}
