
package com.simulator.ccn;
import arjuna.JavaSim.Simulation.*;
import arjuna.JavaSim.Distributions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.simulator.controller.SimulationTypes;
import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

import arjuna.JavaSim.Simulation.SimulationException;

/* This class is a SimulationProcess, hence, it will get scheduled in JavaSim Scheduler. The main function is that when it is 
 * scheduled to be picked, then the CCNRouter object selected will process a packet from its queue. 
 * */
public class CCNRouter extends SimulationProcess {
	
	static final Logger log = Logger.getLogger(CCNRouter.class);
	private ExponentialStream STime;
	private boolean working;
	private Packets currentPacket;
	private CCNQueue packetsQ;
	private static double procDelay;
	/**
	 * Pending Interest Table is a map of Interest Id and as list of Integers representing routerId's of interested nodes.
	 */
	Map<Integer, List<Integer>> pit = null;
	/**
	 * forwarding table is a map of <DataPacketId,NodeId>
	 */
	Map<Integer,Integer> forwardingTable = null;
	/**
	 * Routers Unique Id
	 */
	private int routerId = 0;
	/**
	 * Routers localCache. Its own Cache
	 */
	private CCNCache localCache = null;
	/**
	 * Cache to store the data objects it recieved from other routers.
	 */
	private CCNCache globalCache = null;
	
	/**
	 * Counter to distinguish various log entries of this router.
	 */
	private int logCounter = 0;
	
	/**
	 * List of interest packets served. Before serving an Interest packet we check with this list first to make sure 
	 * we haven't served it already.
	 * TODO Implementing this list in a very naive way for now, need to think of more efficient way of handling this
	 */
	
	private List<Integer> interestsServed = null;
	
	public CCNRouter (int id) {
		
		STime = new ExponentialStream(8);
		working = false;
		currentPacket = null;
		packetsQ = new CCNQueue(id);
		pit = new HashMap<Integer,List<Integer>>();
		forwardingTable = new HashMap<Integer,Integer>();
		interestsServed = new ArrayList<Integer>();
		setRouterId(id);
		localCache = new CCNCache(id);
		globalCache = new CCNCache(id);
		globalCache.setMaxSize(100);
	}
	
	/* The CCNRouter is a SimulationProcess, and is scheduled by JavaSim Scheduler. The following method is called when 
	 * the JavaSim scheduler selects a CCNRouter object from its queue. The CCNRouter removes a packet from its queue, 
	 * and processes it accordingly. 
	 * */
	public void run () {
		
		for (;;) {
			
			working = true;

			while (!packetsQ.isEmpty())	{
				
				CurrentTime();

				int ctr= getLogCounter();
				
				log.info("Start Processing of Router:"+getRouterId()+" Iteration:"+ctr+"\n");
				log.info(toString());
				
				currentPacket = (Packets) packetsQ.remove();
				log.info("Processing  packet "+currentPacket.toString());
				
				/* Records the time at which the packet was removed from the queue */
				Packets.dumpStatistics(currentPacket, "DEQUEUE");
				
				/* 
				 * "Hold(getProcDelay())" represents the processing delay for a packet before the result of that
				 *  processing is reflected in the methods interestpackethandler() and datapackethandler()
				 */
				
				try	{
					Hold(getProcDelay());
				} 
				catch (SimulationException e) {}
				catch (RestartException e) {}
				
				/* Records the time at which the packet has been processed */
				Packets.dumpStatistics(currentPacket, "PROCSED");	
				
				/* The packet will be processed according to its packet type */
				if(currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_INTEREST)
					interestPacketsHandler(currentPacket);
				else if (currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_DATA)
					dataPacketsHandler(currentPacket);
				
				log.info(toString());
				log.info("Ending Processing of Router:"+getRouterId()+" Iteration:"+ctr+"#########################################################\n");
				
				CurrentTime();
			}

			working = false;

			/* In case the CCNRouter objects queue is empty, then we suspend the CCNRouter using "Cancel()". When it will have 
			 * packets in its queue, it will be activated and placed in the scheduler again. 
			 * */
			try	{
				Cancel();
			}
			catch (RestartException e) {}
		}
	}
	/**
	 * This is the handler  data packets in a router. It first checks if there is an entry in Pit table
	 * if it doesn't find any it just ignores the data packet. else it runs through the the linked list
	 * returned by Pit table and sends the data packet to those routers. If the router id is my own I just ignore it.
	 * After that it deletes the pit entry.
	 * @param curPackets the data packet
	 */
	public void dataPacketsHandler(Packets curPacket) {
		
		// I got this data packet so setting its locality to false
		curPacket.setLocality(false);
		log.info("In Data packet handler");
		List<Integer> pitEntry = pit.get(curPacket.getPacketId()); 
		
		/* I havent seen this packet so discard it */
		if(pitEntry == null) {
			
			log.info("No entry in pit table ignoring");
			curPacket.finished(SimulationTypes.SUPRESSION_NO_PIT);
			return;
		}
		
		/* If the current node is not present in the PIT table list dump the packet as live packet and set the hop counts 
		 * to zero.
		 * */
		if(!(pitEntry.contains(-1))) {
			//Packets.dumpStatistics(curPacket);
			//curPacket.setNoOfHops(0);
		}
		
		
		/* The following code is used to flood data packets over all the interfaces in PIT entry for this object */
		Iterator<Integer> itr = pitEntry.iterator();		
		while(itr.hasNext()) {
			
			Integer rid = itr.next();
			
			/* We shouldn't flood the interest packet to same node where it came from */
			if(rid != getRouterId()) {
				
				Packets clonePac = (Packets) curPacket.clone();
				sendPacket(clonePac, rid);
			}
		}
		
		/* Now remove the entry */ 
		pit.remove(curPacket.getPacketId());
		
		/* add the data packet into my global cache */
		log.info("Adding to global cache");
		
		getGlobalCache().addToCache(curPacket);
		
		/* adding entry to forwarding table */
		log.info("Adding a entry on forwarding table");
		
		getForwardingTable().put(curPacket.getPacketId(), curPacket.getPrevHop());
	}
	/**
	 * Interest Packet handler for the machine. It first searches in Machine caches. If it fails then adds it to PIT. 
	 * Then looks in Forwarding Table
	 * if successful forwards the Interest packet to that node. If it fails then floods all the neighbouring nodes. 
	 * @param curPacket
	 */
	public void interestPacketsHandler(Packets curPacket) {
		
		/* The following code is to suppress the interest packets that have already been served */
		if (isInterestServed(curPacket.getPacketId())) {
			
			curPacket.finished(SimulationTypes.SUPRESSION_ALREADY_SERVED);
			log.info("Already served interest packet:"+ curPacket.getPacketId());
			return;
		}
		
		log.info("Machine Interest packet handler"+curPacket.toString());
		
		/* Add the interest packet into the list of served interest packets. It will assist in achieving the step above, which
		 * is to suppress interest packets already served 
		 * */
		addToInterestServed(curPacket.getPacketId());
		
		Boolean newInPit = false;
		
		/* The following code is called when the interest is satisfied from: (a) the Local Cache, which means, the object originally 
		 * resides on this CCNRouter; or (b) the Global Cache or the content store of CCN */
		Packets data_packet = getDataPacketfromCache(curPacket.getRefPacketId());
		if(data_packet != null) {
			
			/* Create a data packet in reply of the interest packet, and place it in the trace file */
			Packets.dumpStatistics(data_packet, "CRTDPRD");
			log.info("Sending data packet to nodeId:"+Integer.toString(curPacket.getPrevHop()));
			
			/* Changing the reference of the data packet to the interest packet, after sendPacket should change back to -1 */
			data_packet.setRefPacketId(curPacket.getSourcePacketId());
			
			sendPacket((Packets) data_packet.clone(), curPacket.getPrevHop());
			
			data_packet.setRefPacketId(-1);
			curPacket.finished(SimulationTypes.SUPRESSION_SENT_DATA_PACKET);
			return;
		}
		
		log.info("Inserting into pit table");
		List<Integer> pitEntry = pit.get(curPacket.getRefPacketId());
		
		/* I havent seen this packet so I need to create a new PIT entry for this objectID */
		if(pitEntry == null) {
			log.info("New entry in pit table");
			pitEntry = new ArrayList<Integer>();
			newInPit=true;
		}
		
		/* The following code differentiates between when a source node (first node) is requesting for an object as compared
		 * to when the interest request is at intermediate nodes, in which case, they will have a previous node. The node 
		 * requesting will not have a previous hop as the request will stop there. Moreover, it will not have to forward the
		 * data packet any further.
		 *  */
		if(!pitEntry.contains(curPacket.getPrevHop())) {
			
			pitEntry.add(curPacket.getPrevHop());
			pit.put(curPacket.getRefPacketId(), pitEntry);
		}
		
		log.info("Current Pit table-> "+pit);
		
		/* If we have a FIB match, then we will not flood the packet. We will simply help it along the FIB entry. If not then
		 * we will flood the packet 
		 * */
		Integer rid = getForwardingTableEntry(curPacket.getRefPacketId());
		if(rid!=null) {
			
			log.info("Forwarding table hit sending to"+ rid);
			sendPacket(curPacket, rid);
			curPacket.finished(SimulationTypes.SUPRESSION_FIB_ENTRY);			
		}
		/* oh god !! the flooding devil */
		else {
			floodInterestPacket(curPacket);			
		}	
	}
	/**
	 * gets the forwardingTableEntry for this packetid.
	 * @param packetId
	 * @return
	 */
	private Integer getForwardingTableEntry(Integer packetId) {
		
		return forwardingTable.get(packetId);
	}
	/**
	 * Searches localcache and then global cache to find the the packet and if successful reutrns it else returns null;
	 * @param packetId Id if the datapacket
	 * @return data packet
	 */
	private Packets getDataPacketfromCache(Integer packetId) {
		
		Packets packet = localCache.getaPacketFromCache(packetId);
		if(packet != null) {
			
			log.info("Hit in local cache "+packet.toString());
			return packet;
		}
		
		packet = globalCache.getaPacketFromCache(packetId);

		if(packet != null) {
			
			log.info("Hit in Global cache "+packet.toString());
			return packet;
		}
		
		log.info("cache miss ");
		return null;		
	}
	
	/**
	 * Floods packet on the all the interfaces of this router, except on the node from which this packet came from.
	 * @param curPacket Packet to be flooded.
	 */
	public void floodInterestPacket(Packets curPacket) {
		
		LinkedHashSet<HashMap<Integer, Integer>> adjList= Grid.getAdjacencyList(getRouterId());
		Iterator<HashMap<Integer,Integer>> itr = adjList.iterator();
		log.info("Flooding the packet to {");

		/* Getting the previous node of the packet so as not to flood to same node */ 
		int srcNode = curPacket.getPrevHop(); 
		
		/* The following code is used to send packets to all the neighbors of a node */
		while(itr.hasNext()) {
			
			HashMap<Integer,Integer> adjNode = (HashMap<Integer, Integer>) itr.next();
			Integer nodeId = adjNode.keySet().iterator().next();
			
			 /* We shouldn't flood the interest packet to same node where it came from */
			if(nodeId != srcNode) {
				
				Packets pacToadd = (Packets)curPacket.clone();
				sendPacket(pacToadd, nodeId);
			}
		}
		log.info("}");
	}	
	
	/**
	 * This function puts the packet in the destination Router's queue. But makes necessary changes to the packet before putting it.
	 * It creates a new SimulationProcess TransmitPackets and adds transmission delay to it.
	 */
	public void sendPacket(Packets curPacket,final Integer nodeId) {
		
		if(nodeId != -1) {
			
			/* setting the source id as my Id before flooding it to my good neighbors */
			curPacket.setPrevHop(getRouterId()); 
			curPacket.incrHops();
			
			TransmitPackets trans = new TransmitPackets(curPacket,nodeId);
			
			try {
				trans.ActivateDelay(TransmitPackets.getTransDelay());
			} 
			catch (SimulationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (RestartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			log.info("Sending packet to nodeId:"+nodeId);
		}
		else {
			
			curPacket.finished(SimulationTypes.SUPRESSION_DEST_NODE);
			log.info("Packet Destined to my node,so suppressing");
		}
	}
	
	@Override
	/* I am not sure if this function is even needed. Moreover, the two functions following it seem unnecessary */
	public void Activate() {
		
		if (!getPacketsQ().isEmpty() && !Processing()) {
			
			try {				
				super.Activate();
			}
			catch (SimulationException e) {
				log.info("Exception "+ e.toString());
			}
			catch (RestartException e) {
				log.info("Exception "+ e.toString());
			}
		}
		else
			log.info("Not activating "+getPacketsQ().isEmpty()+Processing());
	}	
	
	public boolean Processing () {
		return working;
	}

	public double ServiceTime () {
		try
		{
			return STime.getNumber();
		}
		catch (IOException e)
		{
			return 0.0;
		}
	}

	@Override
	public String toString() {
		
		String str;
		str = "CCNRouter\n{ Id:"+getRouterId()+ " \n"+ getPacketsQ().toString()+"\n PIT:"+getPIT().toString()+"\n ForwardingTable"+
		getForwardingTable().toString()+"\n interestServedTable"+interestsServed.toString()+"\nGlobalCache:"+getGlobalCache().toString()+"\nLocalCache:"+getLocalCache().toString()+"}\n";
		return str;
	}
	
	public CCNQueue getPacketsQ() {
		return packetsQ;
	}
	
	public void setPacketsQ(CCNQueue packetsQ) {
		packetsQ = packetsQ;
	}
	
	public int getRouterId() {
		return routerId;
	}
	
	public void setRouterId(int routerId) {
		this.routerId = routerId;
	}
	
	public boolean isPresentInPit(Integer packetId) {
		
		if(pit.containsKey(packetId))
			return true;
		else
			return false;
	}
	
	public boolean isPresentInQueue(Packets packet) {
		
	    if(packetsQ.contains(packet))
	    	return true;
	    else
	    	return false;
	}
	
	public Map<Integer, List<Integer>> getPIT() {
		return pit;
	}
	
	public void setPIT(Map<Integer, List<Integer>> pIT) {
		pit = pIT;
	}
	
	public CCNCache getLocalCache() {
		return localCache;
	}
	
	public void setLocalCache(CCNCache localCache) {
		this.localCache = localCache;
	}
	
	public CCNCache getGlobalCache() {
		return globalCache;
	}
	
	public void setGlobalCache(CCNCache globalCache) {
		this.globalCache = globalCache;
	}

	public Map<Integer, Integer> getForwardingTable() {
		return forwardingTable;
	}

	public void setForwardingTable(Map<Integer, Integer> forwardingTable) {
		this.forwardingTable = forwardingTable;
	}

	/**
	 * Searches the interestServed list.
	 * @param id
	 * @return
	 */
	public boolean isInterestServed(int id) {
		return interestsServed.contains(id);
	}
	/**
	 * Adds to the interestServed Table.
	 * @param id
	 */

	public void addToInterestServed(int id) {
		this.interestsServed.add(id);
	}

	public int getLogCounter() {
		
		logCounter=logCounter+1;
		return logCounter;
	}

	public void setLogCounter(int logCounter) {
		this.logCounter = logCounter;
	}

	public static double getProcDelay() {
		return procDelay;
	}

	public static void setProcDelay(double procDelay) {
		CCNRouter.procDelay = procDelay;
	}
};
