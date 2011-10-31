package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import arjuna.JavaSim.Simulation.SimulationException;

public class Machine extends SimulationProcess
{
	private ExponentialStream STime;
	private boolean operational;
	private boolean working;
	private Packets currentPacket;
	private CCNQueue packetsQ;
	Map<Integer, List<Integer>> pit = null;
	private Integer id=0;
	public Machine (CCNQueue queue, Map<Integer, List<Integer>> pIT,int id, double mean)
	{
		STime = new ExponentialStream(mean);
		operational = true;
		working = false;
		currentPacket = null;
		packetsQ=queue;
		pit = pIT;
		this.id=id;
	}

	public void run ()
	{
		for (;;)
		{
			working = true;

			while (!packetsQ.isEmpty())
			{
				CurrentTime();

				//	CCNRouter.PacketssInQueue += CCNRouter.PacketsQ.QueueSize();
				currentPacket = (Packets) packetsQ.remove();
				
				System.out.println("got following packet->"+currentPacket.getPacketId());
				if(currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_INTEREST)
					interestPacketsHandler(currentPacket);
				try
				{
					Hold(ServiceTime());
				}
				catch (SimulationException e)
				{
				}
				catch (RestartException e)
				{
				}

				CurrentTime();

				/*
				 * Introduce this new method because we usually rely upon
				 * the destructor of the object to do the work in C++.
				 */

				currentPacket.finished();
			}

			working = false;

			try
			{
				Cancel();
			}
			catch (RestartException e)
			{
			}
		}
	}

	public void interestPacketsHandler(Packets curPacket)
	{
		List<Integer> pitEntry = pit.get(curPacket.getPacketId());
		if(pitEntry == null) // I havent seen this packet so I need to flood it
		{
			pitEntry = new ArrayList<Integer>();
			floodInterestPacket(curPacket);
		}
		pitEntry.add(curPacket.getSourceNode());
		pit.put(curPacket.getPacketId(), pitEntry);
	}
	
	public void floodInterestPacket(Packets curPacket)
	{
		LinkedHashSet<HashMap<Integer, Integer>> adjList= Grid.getAdjacencyList(id);
		Iterator<HashMap<Integer,Integer>> itr = adjList.iterator();
		curPacket.setSourceNode(id); // setting the source id as my Id before flooding it to my good neighbors
		while(itr.hasNext())
		{
			HashMap<Integer,Integer> adjNode = (HashMap<Integer, Integer>) itr.next();
			Integer nodeId = adjNode.keySet().iterator().next();
			CCNRouter adjRouter = Grid.getRouter(nodeId);
			adjRouter.getPacketsQ().addLast(curPacket);
			// After flooding its queue activate the machine if its deactivated
			if ( adjRouter.getPacketsQ().isEmpty() && ! adjRouter.getM().Processing() && adjRouter.getM().IsOperational())
			{
				try
				{
					adjRouter.getM().Activate();
				}
				catch (SimulationException e)
				{
				}
				catch (RestartException e)
				{
				}
			}
		}
	}
	public void Broken ()
	{
		operational = false;
	}

	public void Fixed ()
	{
		operational = true;
	}

	public boolean IsOperational ()
	{
		return operational;
	}

	public boolean Processing ()
	{
		return working;
	}

	public double ServiceTime ()
	{
		try
		{
			return STime.getNumber();
		}
		catch (IOException e)
		{
			return 0.0;
		}
	}


};
