package com.mahesh.cse523.main;
import java.lang.*;
import arjuna.JavaSim.*;

public class Main
{

public static void main (String[] args)
    {
    
	for (int i = 0; i < args.length; i++)
	{
	    if (args[i].equalsIgnoreCase("-help"))
	    {
		System.out.println("Usage: Main [-help]");
		System.exit(0);
	    }
	}

	SimpleServer m = new SimpleServer();

	m.Await();

	System.exit(0);
    }
    
}
