package com.simulator.distributions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

import org.apache.log4j.Logger;

import com.simulator.ccn.CCNCache;
import com.simulator.ccn.CCNRouter;
import com.simulator.enums.SimulationTypes;
import com.simulator.packets.DataPacket;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

/**
 * This class handles all the distribution functions of the this CCN simulation.
 * It generates data based on the required distribution and sets the scenario accordingly.
 * @author contra
 *
 */
public class PacketDistributions {
	static final Logger log = Logger.getLogger(PacketDistributions.class);
	
	static Integer noDataPackets;
	static int dataPacketSize;
	private static String allDocs = null;
	
	private static Random nodeSelecter = new Random(5);
	
	/**
	 *  distributeContentGlobeTraffic distributes the content from doc.all produced by globe traffic tool into various nodes of the topology.
	 *  //TODO currently randomly choosing a node to distribute content need to review this.
	 * @throws IOException 
	 */
	public static void distributeContentGlobeTraffic() throws IOException
	{
		BufferedReader rd = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(getAllDocs())));
		String line;
		while((line = rd.readLine())!=null)
		{
			Integer rtrId = nodeSelecter.nextInt(Grid.getGridSize());
			DataPacket pac = new DataPacket(line,rtrId,1);
			CCNRouter router = Grid.getRouter(rtrId);
			CCNCache routerLocalCache = router.getLocalCache();
			pac.setLocality(true);
			routerLocalCache.addToCache(pac);
			log.info("Creating Data Object:"+line);
		}
		rd.close();
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
	public static String getAllDocs() {
		return allDocs;
	}
	public static void setAllDocs(String allDocs) {
		PacketDistributions.allDocs = allDocs;
	}	
}
