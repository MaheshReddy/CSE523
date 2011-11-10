package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;
import java.util.Random;

import java.io.IOException;

import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.SimulationException;

public class Arrivals extends SimulationProcess
{
	static final Logger log = Logger.getLogger(Arrivals.class);
	private ExponentialStream InterArrivalTime;
	private Integer gridSize = 0;
	private Random nodeSelecter;
	private Random packetIdGenerator;
	
public Arrivals (double mean)
    {
	InterArrivalTime = new ExponentialStream(mean);
	gridSize = Grid.getGridSize();
	
	packetIdGenerator = new Random();
	nodeSelecter = new Random();
    }


public void run ()
    {
	for (;;)
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
	    
			Packets packets = new Packets(nodeSelecter.nextInt(gridSize),
					SimulationTypes.SIMULATION_PACKETS_INTEREST,1);
			packets.setDataPacketId(PacketDistributions.getNextDataPacketID());
			packets.activate();
			log.info("Packet generated ");
			
	}
    }

public int getHostId()
{
	
	return 0;
	
}

    

    
};
