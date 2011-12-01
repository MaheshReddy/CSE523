
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

import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.SimulationException;

public class CCNRouter extends SimulationProcess
{
	static final Logger log = Logger.getLogger(CCNRouter.class);
	private ExponentialStream STime;
	private boolean operational;
	private boolean working;
	private Packets currentPacket;
	private CCNQueue packetsQ;
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
	 * List of interest packets served. Before serving an Interest packet we check with this list first to make sure 
	 * we haven't served it already.
	 * TODO Implementing this list in a very naive way for now, need to think of more efficient way of handling this
	 */
	
	private List<Integer> interestsServed = null;
	
	public CCNRouter (int id)
	{
		STime = new ExponentialStream(8);
		operational = true;
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

	public void run ()
	{
		for (;;)
		{
			working = true;

			while (!packetsQ.isEmpty())
			{
				CurrentTime();

				//	CCNRouter.PacketssInQueue += CCNRouter.PacketsQ.QueueSize();
				log.info("Start");
				log.info(toString());
				currentPacket = (Packets) packetsQ.remove();
				log.info("Processing  packet "+currentPacket.toString());
				if(currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_INTEREST)
					interestPacketsHandler(currentPacket);
				else if (currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_DATA)
					dataPacketsHandler(currentPacket);
				log.info(toString());
				log.info("End");
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

				//currentPacket.finished();
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
	/**
	 * This is the handler  data packets in a router. It first checks if there is an entry in Pit table
	 * if it doesn't find any it just ignores the data packet. else it runs through the the linked list
	 * returned by Pit table and sends the data packet to those routers. If the router id is my own I just ignore it.
	 * After that it deletes the pit entry.
	 * @param curPackets the data packet
	 */
	public void dataPacketsHandler(Packets curPacket)
	{
		// I got this data packet so setting its locality to false
		curPacket.setLocality(false);
		log.info("In Data packet handler");
		List<Integer> pitEntry = pit.get(curPacket.getPacketId()); 
		if(pitEntry == null) // I havent seen this packet so discard it
		{
			log.info("No entry in pit table ignoring");
			curPacket.finished(SimulationTypes.SUPRESSION_NO_PIT);
			return;
		}
		// If the current node is not present in the PIT table list dump the packet as live packet and set the hop counts to zero.
		if(!(pitEntry.contains(-1)))
		{
			curPacket.dumpStatistics();
			curPacket.setNoOfHops(0);
		}
       Iterator<Integer> itr = pitEntry.iterator();
		while(itr.hasNext())
		{
			Integer rid = itr.next();
			if(rid != getRouterId()) // if its not my id than send
			{
				//CCNRouter rtr = Grid.getRouter(rid);
				//rtr.getPacketsQ().add(curPacket);
				sendPacket(curPacket, rid);
			}
		}
		// Now remove the entry 
		pit.remove(curPacket.getPacketId());
		//add the data packet into my global cache
		log.info("Adding to global cache");
		getGlobalCache().addToCache(curPacket);
		//adding entry to forwarding table
		log.info("Adding a entry on forwarding table");
		
		getForwardingTable().put(curPacket.getPacketId(), curPacket.getPrevHop());
	}
	/**
	 * Interest Packet handler for the machine. It first searches in Machine caches. If it fails then adds it to PIT. 
	 * Then looks in Forwarding Table
	 * if successful forwards the Interest packet to that node. If it fails then floods all the neighbouring nodes. 
	 * @param curPacket
	 */
	public void interestPacketsHandler(Packets curPacket)
	{
		if (isInterestServed(curPacket.getPacketId()))
		{
			curPacket.finished(SimulationTypes.SUPRESSION_ALREADY_SERVED);
			log.info("Already served interest packet:"+ curPacket.getPacketId());
			return;
		}
		log.info("Machine Interest packet handler"+curPacket.toString());
		addToInterestServed(curPacket.getPacketId());
		Boolean newInPit = false;
		Packets data_packet = getDataPacketfromCache(curPacket.getRefPacketId());
		if(data_packet != null)
		{
				log.info("Sending data packet to nodeId:"+Integer.toString(curPacket.getPrevHop()));
				//Chaning the reference of the data packet to the interest packet, after sendPacket should change back to -1
				data_packet.setRefPacketId(curPacket.getPacketId());
				sendPacket(data_packet, curPacket.getPrevHop());
				data_packet.setRefPacketId(-1);
				curPacket.finished(SimulationTypes.SUPRESSION_SENT_DATA_PACKET);
			//CCNRouter rtr = Grid.getRouter(curPacket.getPrevHop());
			//rtr.getPacketsQ().add(data_packet);
			return;
		}
		log.info("Inserting into pit table");
		List<Integer> pitEntry = pit.get(curPacket.getRefPacketId());
		if(pitEntry == null) // I havent seen this packet so I need to flood it
		{
			log.info("New entry in pit table");
			pitEntry = new ArrayList<Integer>();
			newInPit=true;
		}
		
		if(!pitEntry.contains(curPacket.getPrevHop()))
		{
			pitEntry.add(curPacket.getPrevHop());
			pit.put(curPacket.getRefPacketId(), pitEntry);
		}
		
		log.info("Current Pit table-> "+pit);
		Integer rid = getForwardingTableEntry(curPacket.getRefPacketId());
		if(rid!=null)
		{
			log.info("Forwarding table hit sending to"+ rid);
			sendPacket(curPacket, rid);
			curPacket.finished(SimulationTypes.SUPRESSION_FIB_ENTRY);
			//CCNRouter router = Grid.getRouter(rid);
			//router.getPacketsQ().add(curPacket);
		}
		else // oh god !! the flooding devil
		{
			if(newInPit) // its new in pit so flood
				floodInterestPacket(curPacket);
			else
				curPacket.finished(SimulationTypes.SUPRESSION_PIT_ENTRY);
		}
		
	}
	/**
	 * gets the forwardingTableEntry for this packetid.
	 * @param packetId
	 * @return
	 */
	private Integer getForwardingTableEntry(Integer packetId)
	{
		return forwardingTable.get(packetId);
	}
	/**
	 * Searches localcache and then global cache to find the the packet and if successful reutrns it else returns null;
	 * @param packetId Id if the datapacket
	 * @return data packet
	 */
	private Packets getDataPacketfromCache(Integer packetId)
	{
		Packets packet = localCache.getaPacketFromCache(packetId);
		if(packet != null)
		{
			log.info("Hit in local cache "+packet.toString());
			return packet;
		}
		
		packet = globalCache.getaPacketFromCache(packetId);

		if(packet != null)
		{
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
	public void floodInterestPacket(Packets curPacket)
	{
		LinkedHashSet<HashMap<Integer, Integer>> adjList= Grid.getAdjacencyList(getRouterId());
		Iterator<HashMap<Integer,Integer>> itr = adjList.iterator();
		log.info("Flooding the packet to {");

		int srcNode = curPacket.getPrevHop(); // getting the previous hop of the packet. so as not to flood to same node. 
		
		//While flooding the interest packet. Just dump the curent packet as a live packet and set all the hop count to zero.
		curPacket.dumpStatistics();
		curPacket.setNoOfHops(0);
		while(itr.hasNext())
		{
			HashMap<Integer,Integer> adjNode = (HashMap<Integer, Integer>) itr.next();
			Integer nodeId = adjNode.keySet().iterator().next();
			if(nodeId != srcNode) // we shouldn't flood the interest packet to same node where it came from
				sendPacket(curPacket, nodeId);
		}
		log.info("}");
	}
	
	
	/**
	 * This functin puts the packet in the destination Router's queue. But makes necessary changes to the packet before putting it.
	 */
	public void sendPacket(Packets curPacket,final Integer nodeId)
	{
		if(nodeId != -1)
		{
		curPacket.setPrevHop(getRouterId()); // setting the source id as my Id before flooding it to my good neighbors
		curPacket.incrHops();
		CCNRouter adjRouter = Grid.getRouter(nodeId);
		adjRouter.getPacketsQ().addLast(curPacket);
		log.info("Sending packet to nodeId:"+nodeId);
		}
		else
		{
			curPacket.finished(SimulationTypes.SUPRESSION_DEST_NODE);
			log.info("Packet Destined to my node,so suppressing");
		}
	}
	
	@Override
	public void Activate()
	{
		if (!getPacketsQ().isEmpty() && !Processing())
		{
			//log.info("Activating Router");
			try
			{
				super.Activate();
			}
			catch (SimulationException e)
			{
				log.info("Exception "+ e.toString());
			}
			catch (RestartException e)
			{
				log.info("Exception "+ e.toString());
			}
		}
		else
			log.info("Not activating "+getPacketsQ().isEmpty()+Processing());
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

	
	@Override
	public String toString()
	{
		String str;
		str = "CCNRouter\n{ Id:"+getRouterId()+ " \nQueue:"+ getPacketsQ().toString()+"\n PIT:"+getPIT().toString()+"\n ForwardingTable"+
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
	public boolean isPresentInPit(Integer packetId)
	{
		if(pit.containsKey(packetId))
			return true;
		else
			return false;
	}
	
	public boolean isPresentInQueue(Packets packet)
	{
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


};
