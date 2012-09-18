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

/* I am in the master at Wali's PC*/

//import org.apache.log4j.Logger;

import com.simulator.ccn.CCNCache;
import com.simulator.ccn.CCNRouter;
import com.simulator.controller.SimulationController;
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
	
	private static Integer noOfObjects;

	private static int objectSize;	

	private static String allDocs = null;
	
	private static Random nodeSelecter = new Random(5);
	
	/* This list is created to store the leaf nodes of a topology. This nodes in this list
	 * will be used as the source and destination of all interest and data packets. 
	 */
	public static ArrayList<Integer> leafNodes = null;
	
	/* We create this array to store sizes of all objects. This array is used in Arrivals class
	 * for segmentation of Interest packets
	 * */
	public static long size[];
	

	
	/**
	 *  distributeContentGlobeTraffic distributes the content from doc.web produced by globe traffic tool into various nodes of the topology.
	 *  //TODO currently randomly choosing a node to distribute content need to review this.
	 * @throws IOException 
	 */

	/* This functions selects different options to distribute content among various nodes*/
	public static void distributeContent (SimulationTypes distType) throws Exception 
	{
		
		size = new long [noOfObjects+1];
		
		if (SimulationTypes.SIMULATION_DISTRIBUTION_DEFAULT == distType) {
			distributeContentDefault();
		}
		else if (SimulationTypes.SIMULATION_DISTRIBUTION_GLOBETRAFF == distType) {
			distributeContentGlobeTraffic();
		}
		else if (SimulationTypes.SIMULATION_DISTRIBUTION_GLOBETRAFF_LEAFNODE == distType) {
			distributeContentGlobeTrafficLeafNodes();
		}
		else if (SimulationTypes.SIMULATION_DISTRIBUTION_LEAFNODE == distType) {
			distributeContentLeafNodes();
		}
		else {
			System.out.println("Invalid distribution type:"+distType);
			throw new Exception();
		}
		
		/* We have the same ID to generate Data packets and Interest packets. It is a flaw in the design. 
		 * However, we want the Interest packets to generte IDs from 0, so we reinitialize the CurrentPacketID
		 * in Packets class.
		 * */
		Packets.setCurrenPacketId(0);
	}	
	
	
	/* We distribute (serially: 0, 1, 2 .. n-1, n, 0, 1 ... ) content between all nodes in any given topology */
	private static void distributeContentDefault() {
		
		Integer noNodes = Grid.getGridSize();
		for(int i=0,j=0;i<=getNoOfObjects();i++,j++) {
			
			DataPacket pack = new DataPacket(j % noNodes, objectSize);
			pack.setSegmentId(0);
			
			size [i] = (long) pack.getSizeOfPacket();
			
			CCNRouter router = Grid.getRouter(j % noNodes);
			CCNCache routerLocalCache = router.getLocalStorage();
			pack.setLocality(true);
			routerLocalCache.addToCache(pack);
		}
	}
	
	/* We distribute content between the leaf nodes of 100 customized topology. The leaf nodes are selected in circular
	 * list starting from the node having smallest ID */
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
		
		for(int i=0,j=0;i<=getNoOfObjects();i++,j++) {
			
			DataPacket pack = new DataPacket(PacketDistributions.leafNodes.get(j % noNodes), objectSize);
			System.out.println ("Data object " + i + " will reside in Node " + PacketDistributions.leafNodes.get(j % noNodes));
			pack.setSegmentId(0);
			
			size [i] = (long) pack.getSizeOfPacket();

			CCNRouter router = Grid.getRouter(PacketDistributions.leafNodes.get(j % noNodes));
			CCNCache routerLocalCache = router.getLocalStorage();
			pack.setLocality(true);
			routerLocalCache.addToCache(pack);
		}
	}
	
	/**
	 *  distributeContentGlobeTraffic distributes the content from doc.all produced by globe traffic tool into various nodes of the topology.
	 *  //TODO currently randomly choosing a node to distribute content need to review this.
	 * @throws IOException 
	 */
	/* We randomly distribute content between nodes of a given topology. We select the objects from a given docs.web file. */
	private static void distributeContentGlobeTraffic() throws IOException {
		
		/* Open the docs.web file to read the object id's and their sizes */
		BufferedReader rd = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(getAllDocs())));
		
		int count = 0;
		int count1 = 0;
		String line;
		
		while((line = rd.readLine())!=null)	{
			
			Integer rtrId = nodeSelecter.nextInt(Grid.getGridSize());
			DataPacket pac = new DataPacket(line,rtrId,0);
			
			if (SimulationController.getObjectSegmentation() == SimulationTypes.SIMULATION_SEG_ON) {
				size [count1] = (long)pac.getSizeOfPacket();
			}
			else if (SimulationController.getObjectSegmentation() == SimulationTypes.SIMULATION_SEG_OFF) {
				size [count1] = Arrivals.getSegmentSize();
			}	
			
			count1++;
			
			CCNRouter router = Grid.getRouter(rtrId);
			CCNCache routerLocalCache = router.getLocalStorage();
			pac.setLocality(true);
			routerLocalCache.addToCache(pac);
			//log.info("Creating Data Object:"+line);
		}
		rd.close();
	}
	
	/**
	 *  distributeContentGlobeTraffic distributes the content from doc.all produced by globe traffic tool into various nodes of the topology.
	 *  //TODO currently randomly choosing a node to distribute content need to review this.
	 * @throws IOException 
	 * 
	 */
	
	/* We distribute content between the leaf nodes of 100 and 39 custom-built topologies. The leaf nodes are selected in circular
	 * list starting from the node having smallest ID. We select the objects from a given docs.web file. */
	private static void distributeContentGlobeTrafficLeafNodes() throws IOException {
		
		
		/* Manually enter leaf nodes based on the topology created in Brite for 100 nodes using GlobeTraff*/
		
		if (Grid.getGridSize() == 100) {
		
			leafNodes = new ArrayList <Integer> (32);
			
			//For 100 node topology these are the leaf nodes
			leafNodes.add(11); leafNodes.add(30); leafNodes.add(32); leafNodes.add(44); leafNodes.add(45); leafNodes.add(49);
			leafNodes.add(72); leafNodes.add(70); leafNodes.add(61); leafNodes.add(59); leafNodes.add(54); leafNodes.add(51);
			leafNodes.add(75); leafNodes.add(78); leafNodes.add(79); leafNodes.add(80); leafNodes.add(82); leafNodes.add(83);
			leafNodes.add(84); leafNodes.add(87); leafNodes.add(88); leafNodes.add(89); leafNodes.add(90); leafNodes.add(91);
			leafNodes.add(92); leafNodes.add(93); leafNodes.add(94); leafNodes.add(95); leafNodes.add(96); leafNodes.add(97);
			leafNodes.add(98); leafNodes.add(99);
		}
		
		else if (Grid.getGridSize() == 39) {
			
			/* For 39 node topology these are the leaf nodes.
			 * YOu there? */
			
			leafNodes = new ArrayList <Integer> (31);
			for (int i = 8; i <= 38; i++)	
				leafNodes.add(i); 
			
		}
		
		else {
			System.out.println("Error: Incorrect topology");
			System.exit(0);
		}		
		
		Integer noNodes = PacketDistributions.leafNodes.size();

		/* Open the docs.web file to read the object id's and their sizes */
		BufferedReader rd = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(getAllDocs())));
		
		String line;
		int count = -1;
		int count1 = 0;
		
		while((line = rd.readLine())!=null)	{
			
			count++;
			
			DataPacket pack = new DataPacket(line,PacketDistributions.leafNodes.get(count % noNodes),0);
			
			System.out.println ("Data object " + count + " will reside in Node " + PacketDistributions.leafNodes.get(count % noNodes));
			
			pack.setSegmentId(0);
			
			if (SimulationController.getObjectSegmentation() == SimulationTypes.SIMULATION_SEG_ON) {
				size [count1] = (long)pack.getSizeOfPacket();
			}
			else if (SimulationController.getObjectSegmentation() == SimulationTypes.SIMULATION_SEG_OFF) {
				size [count1] = Arrivals.getSegmentSize();
			}			
			
			count1++;
			
			CCNRouter router = Grid.getRouter(PacketDistributions.leafNodes.get(count % noNodes));
			CCNCache routerLocalCache = router.getLocalStorage();
			pack.setLocality(true);
			routerLocalCache.addToCache(pack);

			//log.info("Creating Data Object:"+line);
		}	
		
		rd.close();
	}
	/**
	 * Generates the packetId of the data for the Interest Packet Id.
	 * TODO as of now just returns a random value between maxPacketId and minPacketId
	 * @return
	 */
	
	
	public static Integer getNoOfObjects() {
		return noOfObjects;
	}

	public static void setNoOfObjects(Integer tempNoOfObjects) {
		PacketDistributions.noOfObjects = tempNoOfObjects;
	}	
	
	public static int getObjectSize() {
		return objectSize;
	}

	public static void setObjectSize(int tempDataPacketSize) {
		PacketDistributions.objectSize = tempDataPacketSize;
	}
	
	public static String getAllDocs() {
		return allDocs;
	}
	public static void setAllDocs(String allDocs) {
		PacketDistributions.allDocs = allDocs;
	}	
}
