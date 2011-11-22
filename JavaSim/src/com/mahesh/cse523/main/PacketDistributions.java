package com.mahesh.cse523.main;

import java.util.Random;

/**
 * This class handles all the distribution functions of the this CCN simulation.
 * It generates data based on the required distribution and sets the scenario accordinly.
 * @author contra
 *
 */
public class PacketDistributions {
	static Integer noDataPackets;
	/**
	 * Distributes the contents across all the nodes. 
	 * TODO this is just a very simple distribution function need to write a more suitable disribution function. 
	 */
	public static void distributeContent()
	{
		Integer noNodes = Grid.getGridSize();
		for(int i=0,j=0;i<=getNoDataPackets();i++,j++)
		{
			Packets pack = new Packets(j % noNodes, SimulationTypes.SIMULATION_PACKETS_DATA, 50);
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
	public static Integer getNextDataPacketID()
	{
		Random rand = new Random();
		return rand.nextInt(getNoDataPackets());
	}
	public static Integer getNoDataPackets() {
		return noDataPackets;
	}

	public static void setnoDataPackets(Integer noDataPackets) {
		PacketDistributions.noDataPackets = noDataPackets;
	}	
}
