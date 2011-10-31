/**
 * 
 */
package com.mahesh.cse523.test;


import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import com.mahesh.cse523.main.CCNQueue;
import com.mahesh.cse523.main.CCNRouter;
import com.mahesh.cse523.main.Grid;
import com.mahesh.cse523.main.Machine;
import com.mahesh.cse523.main.Packets;
import com.mahesh.cse523.main.SimulationTypes;

/**
 * @author mahesh
 *
 */
public class Test_Machine {

	private Logger log;
	private CCNQueue queue = null;
	HashMap<Integer,List<Integer>> pit = null;
	Machine test_obj =null;
	Grid Test_grid = null;
	CCNRouter test_rtr = null;
	Packets test_pac = null;
	HashSet<HashMap<Integer,Integer>> test_adjList = null;
	/**
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Grid.createMeshGrid(4);
		pit = new HashMap<Integer,List<Integer>>();
		queue = new CCNQueue();
		for(int i=0;i<4;i++)
			queue.add(new Packets(i,0,SimulationTypes.SIMULATION_PACKETS_INTEREST));
		//test_obj = new Machine(queue, pit, 0,0);
		
			
	}

	/**
	 * @throws java.lang.Exception
	 * This guy tests if the generated Interest packets is flooded to all of its
	 * Neighbors
	 */
	@Test
	public void testFloodingHappens() 
	{
		test_pac = (Packets) queue.remove();
		Integer routerId = test_pac.getPacketId();
		System.out.println("Current Packetis "+test_pac.toString());
		test_rtr = Grid.getRouter(routerId);
		test_rtr.getM().interestPacketsHandler(test_pac);
		// now this has to be present in PIT table of all the adjacent nodes
	    test_adjList = Grid.getAdjacencyList(routerId);
		Iterator<HashMap<Integer,Integer>>  itr = test_adjList.iterator();
		while(itr.hasNext())
		{
		  HashMap<Integer,Integer> node = itr.next();
		  Integer rtrId = node.keySet().iterator().next();
		  CCNRouter rtr = Grid.getRouter(rtrId);
		  System.out.println(rtr.getPIT().toString());
		  if(!rtr.isPresentInQueue(test_pac))
			  fail("Packet not present in the queue of "+rtrId.toString());
		}
		
	}
	@Test
	public void testFloodingHalts()
	{
		testFloodingHappens();
		Iterator<HashMap<Integer,Integer>> itr = test_adjList.iterator();
		while(itr.hasNext()) // call interest packets handler of each of its adj node
		{
			HashMap<Integer,Integer> node = itr.next();
			  Integer rtrId = node.keySet().iterator().next();
			  CCNRouter rtr = Grid.getRouter(rtrId);
			  Packets pac = (Packets) rtr.getPacketsQ().remove();
			  rtr.getM().interestPacketsHandler(pac);
			// Now just check if all the PIT have entries for this packet and their queues are empty
			 if(!rtr.isPresentInPit(pac.getPacketId()))
				 fail("Not present in PIT of "+rtrId.toString());
			 if(!rtr.getPacketsQ().isEmpty())
				 fail("Packet still present in one of the queue");
			 
		}
		if(!test_rtr.getPacketsQ().isEmpty())
			 fail("Packet still present in the originator Queue");
		
	}
	@After
	public void tearDown() throws Exception {
	}

}
