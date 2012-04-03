package com.simulator.distributions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;
import java.util.ArrayList;


//import org.apache.log4j.Logger;

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
	//static final Logger log = Logger.getLogger(PacketDistributions.class);
	
	static Integer noDataPackets;

	static int dataPacketSize;	

	private static String allDocs = null;
	
	private static Random nodeSelecter = new Random(5);
	
	public static ArrayList<Integer> leafNodes = null;
	
	/**
	 *  distributeContentGlobeTraffic distributes the content from doc.all produced by globe traffic tool into various nodes of the topology.
	 *  //TODO currently randomly choosing a node to distribute content need to review this.
	 * @throws IOException 
	 */

	
	public static void distributeContent (SimulationTypes distType) throws Exception 
	{
		if (SimulationTypes.SIMULATION_DISTRIBUTION_DEFAULT == distType) {
			distributeContentDefault();
		}
		else if (SimulationTypes.SIMULATION_DISTRIBUTION_GLOBETRAFF == distType) {
			distributeContentGlobeTraffic();
		}
		else if (SimulationTypes.SIMULATION_DISTRIBUTION_LEAFNODE == distType) {
			distributeContentLeafNodes();
		}
		else {
			System.out.println("Invalid distribution type:"+distType);
			throw new Exception();
		}
	}	
	
	private static void distributeContentDefault() {
		
		Integer noNodes = Grid.getGridSize();
		for(int i=0,j=0;i<=getNoDataPackets();i++,j++) {
			
			DataPacket pack = new DataPacket(j % noNodes, dataPacketSize);
			pack.setSegmentId(0);
			CCNRouter router = Grid.getRouter(j % noNodes);
			CCNCache routerLocalCache = router.getLocalCache();
			pack.setLocality(true);
			routerLocalCache.addToCache(pack);
		}
	}
	
	private static void distributeContentLeafNodes() {
		
		/* Manually enter leaf nodes based on the topology created in Brite for 100 nodes */
		leafNodes = new ArrayList <Integer> (32);
		
		leafNodes.add(11); leafNodes.add(30); leafNodes.add(32); leafNodes.add(44); leafNodes.add(45); leafNodes.add(49);
		leafNodes.add(72); leafNodes.add(70); leafNodes.add(61); leafNodes.add(59); leafNodes.add(54); leafNodes.add(51);
		leafNodes.add(75); leafNodes.add(78); leafNodes.add(79); leafNodes.add(80); leafNodes.add(82); leafNodes.add(83);
		leafNodes.add(84); leafNodes.add(87); leafNodes.add(88); leafNodes.add(89); leafNodes.add(90); leafNodes.add(91);
		leafNodes.add(92); leafNodes.add(93); leafNodes.add(94); leafNodes.add(95); leafNodes.add(96); leafNodes.add(97);
		leafNodes.add(98); leafNodes.add(99);
		
		Integer noNodes = PacketDistributions.leafNodes.size();
		for(int i=0,j=0;i<=getNoDataPackets();i++,j++) {
			
			DataPacket pack = new DataPacket(PacketDistributions.leafNodes.get(j % noNodes), dataPacketSize);
			System.out.println ("Data object " + i + " will reside in Node " + PacketDistributions.leafNodes.get(j % noNodes));
			pack.setSegmentId(0);
			CCNRouter router = Grid.getRouter(PacketDistributions.leafNodes.get(j % noNodes));
			CCNCache routerLocalCache = router.getLocalCache();
			pack.setLocality(true);
			routerLocalCache.addToCache(pack);
		}
	}
	
	/**
	 *  distributeContentGlobeTraffic distributes the content from doc.all produced by globe traffic tool into various nodes of the topology.
	 *  //TODO currently randomly choosing a node to distribute content need to review this.
	 * @throws IOException 
	 */
	private static void distributeContentGlobeTraffic() throws IOException {
		
		//FileReader inReader = new FileReader(new File(ClassLoader.getSystemResource(getAllDocs()).toURI()));
		//InputStreamReader inReader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(getAllDocs()));
		BufferedReader rd = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(getAllDocs())));
		String line;
		
		while((line = rd.readLine())!=null)	{
			
			Integer rtrId = nodeSelecter.nextInt(Grid.getGridSize());
			DataPacket pac = new DataPacket(line,rtrId,0);
			CCNRouter router = Grid.getRouter(rtrId);
			CCNCache routerLocalCache = router.getLocalCache();
			pac.setLocality(true);
			routerLocalCache.addToCache(pac);
			//log.info("Creating Data Object:"+line);
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
