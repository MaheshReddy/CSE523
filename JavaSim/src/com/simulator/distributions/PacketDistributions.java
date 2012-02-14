package com.simulator.distributions;

import java.util.Random;

import com.simulator.ccn.CCNCache;
import com.simulator.ccn.CCNRouter;
import com.simulator.controller.SimulationTypes;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

/**
 * This class handles all the distribution functions of the this CCN simulation.
 * It generates data based on the required distribution and sets the scenario accordinly.
 * @author contra
 *
 */
public class PacketDistributions {
	static Integer noDataPackets;
	static int dataPacketSize;
	
	/**
	 * Distributes the contents across all the nodes. 
	 * TODO this is just a very simple distribution function need to write a more suitable distribution function. 
	 */
	public static void distributeContent() {
		
		Integer noNodes = Grid.getGridSize();
		for(int i=0,j=0;i<=getNoDataPackets();i++,j++)
		{
			Packets pack = new Packets(j % noNodes, SimulationTypes.SIMULATION_PACKETS_DATA, dataPacketSize);
				CCNRouter router = Grid.getRouter(j % noNodes);
				CCNCache routerLocalCache = router.getLocalCache();
				pack.setLocality(true);
				routerLocalCache.addToCache(pack);
		}
	}
	/**
	 * Generates the packetId of the data for the Interest Packet Id.
	 * TODO as of now just returns a random value between maxPacketId and minPacketId
	 * @return
	 */
	
	/* 
	 * We are not using the following function anymore. It might not be needed anymore. This functionality is achieved in
	 * Arrivals.java using the PacketIDGenerator.
	 *  */
	public static Integer getNextDataPacketID() {
		
		Random rand = new Random(6);
		return rand.nextInt(getNoDataPackets());
	}
	
	public static Integer getNoDataPackets() {
		return noDataPackets;
	}

	public static void setnoDataPackets(Integer noDataPackets) {
		PacketDistributions.noDataPackets = noDataPackets;
	}	
	
	public static int getDataPacketSize() {
		return dataPacketSize;
	}

	public static void setDataPacketSize(int tempDataPacketSize) {
		PacketDistributions.dataPacketSize = tempDataPacketSize;
	}	
}
