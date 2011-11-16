package com.mahesh.cse523.main;

public class Main
{

public static void main (String[] args)
    {
    
	//CCNRouter m = new CCNRouter(8);
    SimulationController ctrl = new SimulationController();
	ctrl.Await();
	System.out.println("Done simulation exiting");
	System.exit(0);
    }
}
