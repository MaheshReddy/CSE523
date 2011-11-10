package com.mahesh.cse523.main;

public class Main
{

public static void main (String[] args)
    {
    
	//CCNRouter m = new CCNRouter(8);
    SimulationController ctrl = new SimulationController();
	ctrl.Await();
	System.exit(0);
    }
}
