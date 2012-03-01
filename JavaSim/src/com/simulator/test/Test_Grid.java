/**
 * 
 */
package com.simulator.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedHashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.simulator.controller.SimulationTypes;
import com.simulator.topology.Grid;

/**
 * @author mahesh
 *
 */
public class Test_Grid {

	/**
	 * @throws java.lang.Exception
	 */
	Grid Test_grid = null;
	@Before
	public void setUp() throws Exception {
		Grid.createMeshGrid();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.simulator.topology.Grid#isConnected(java.lang.Integer, java.lang.Integer)}.
	 */
	@Test
	public final void testIsConnected() {
		for(int i=0;i<4;i++)
			for(int j=0;j<4;j++)
				if (i!=j)
				if(!(Grid.isConnected(i,j)))
					fail("Failed the mesh test");
		
	}

	@Test
	public final void testgetAdjacencyList(){
		for(int i=0;i<4;i++)
		{
			LinkedHashSet<HashMap<Integer,Integer>> adjacency = Grid.getAdjacencyList(i);
			for(int j=0;j<4;j++)
			{
			 if(i!=j)
			 {
				 HashMap<Integer,Integer> test = new HashMap<Integer,Integer>();
				 test.put(j, 1);
				 if (!(adjacency.contains(test)))
					 fail("Failed the adjacencyList test");
			 }
			}
		}
		
	}

}


