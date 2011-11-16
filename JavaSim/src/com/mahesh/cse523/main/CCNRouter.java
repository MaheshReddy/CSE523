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
	Map<Integer, List<Integer>> pit = null;
	/**
	 * forwarding table is a map of <DataPacketId,NodeId>
	 */
	Map<Integer,Integer> forwardingTable = null;
	private int routerId = 0;
	private CCNCache localCache = null;
	private CCNCache globalCache = null;
	
	public CCNRouter (int id)
	{
		STime = new ExponentialStream(8);
		operational = true;
		working = false;
		currentPacket = null;
		packetsQ = new CCNQueue(id);
		pit = new HashMap<Integer,List<Integer>>();
		forwardingTable = new HashMap<Integer,Integer>();
		setRouterId(id);
		localCache = new CCNCache(id);
		globalCache = new CCNCache(id);
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
				log.info(toString());
				currentPacket = (Packets) packetsQ.remove();
				log.info("Processing  packet "+currentPacket.toString());
				if(currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_INTEREST)
					interestPacketsHandler(currentPacket);
				else if (currentPacket.getPacketType() == SimulationTypes.SIMULATION_PACKETS_DATA)
					dataPacketsHandler(currentPacket);
				log.info(toString());
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
	/**
	 * This is the handler  data packets in a router. It first checks if there is an entry in Pit table
	 * if it doesn't find any it just ignores the data packet. else it runs through the the linked list
	 * returned by Pit table and sends the data packet to those routers. If the router id is my own I just ignore it.
	 * After that it deletes the pit entry.
	 * @param curPackets the data packet
	 */
	public void dataPacketsHandler(Packets curPacket)
	{
		log.info("In Data packet handler");
		List<Integer> pitEntry = pit.get(curPacket.getPacketId()); 
		if(pitEntry == null) // I havent seen this packet so discard it
		{
			log.info("No entry in pit table ignoring");
			return;
		}
		Iterator<Integer> itr = pitEntry.iterator();
		while(itr.hasNext())
		{
			Integer rid = itr.next();
			if(rid != getRouterId()) // if its not my id than send
			{
				CCNRouter rtr = Grid.getRouter(rid);
				rtr.getPacketsQ().add(curPacket);
			}
		}
		// Now remove the entry 
		pit.remove(curPacket.getPacketId());
		//add the data packet into my global cache
		log.info("Adding to global cache");
		getGlobalCache().addToCache(curPacket);
		//adding entry to forwarding table
		log.info("Adding a entry on forwarding table");
		getForwardingTable().put(curPacket.getPacketId(), curPacket.getSourceNode());
	}
	/**
	 * Interest Packet handler for the machine. It first searches in Machine caches. If it fails then adds it to PIT. 
	 * Then looks in Forwarding Table
	 * if successful forwards the Interest packet to that node. If it fails then floods all the neighbouring nodes. 
	 * @param curPacket
	 */
	public void interestPacketsHandler(Packets curPacket)
	{
		log.info("Machine Interest packet handler"+curPacket.toString());
		Boolean newInPit = false;
		Packets data_packet = getDataPacketfromCache(curPacket.getDataPacketId());
		if(data_packet != null)
		{
			if(getRouterId() != curPacket.getSourceNode()) // send the data packet only if the interest packer is not from me.
			{
				log.info("Sending data packet to nodeId:"+Integer.toString(curPacket.getSourceNode()));
			CCNRouter rtr = Grid.getRouter(curPacket.getSourceNode());
			rtr.getPacketsQ().add(data_packet);
			}
			else
				log.info("Interest packet from my node for this data,so suppressing");
			return;
		}
		log.info("Inserting into pit table");
		List<Integer> pitEntry = pit.get(curPacket.getDataPacketId());
		if(pitEntry == null) // I havent seen this packet so I need to flood it
		{
			log.info("New entry in pit table");
			pitEntry = new ArrayList<Integer>();
			newInPit=true;
		}
		if(!pitEntry.contains(curPacket.getSourceNode()))
		{
			pitEntry.add(curPacket.getSourceNode());
			pit.put(curPacket.getDataPacketId(), pitEntry);
		}
		log.info("Current Pit table-> "+pit);
		Integer rid = getForwardingTableEntry(curPacket.getDataPacketId());
		if(rid!=null)
		{
			log.info("Forwarding table hit sending to"+ rid);
			CCNRouter router = Grid.getRouter(rid);
			router.getPacketsQ().add(curPacket);
		}
		else // oh god !! the flooding devil
		{
			if(newInPit) // its new in pit so flood
				floodInterestPacket(curPacket);
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
	public void floodInterestPacket(Packets curPacket)
	{
		LinkedHashSet<HashMap<Integer, Integer>> adjList= Grid.getAdjacencyList(getRouterId());
		Iterator<HashMap<Integer,Integer>> itr = adjList.iterator();
		log.info("Flooding the packet to {");
		Integer srcNode = curPacket.getSourceNode(); // getting the source Node 
		curPacket.setSourceNode(getRouterId()); // setting the source id as my Id before flooding it to my good neighbors
		while(itr.hasNext())
		{
			HashMap<Integer,Integer> adjNode = (HashMap<Integer, Integer>) itr.next();
			Integer nodeId = adjNode.keySet().iterator().next();
			if(nodeId != srcNode) // we shouldn't flood the interest packet to same node where it came from
			{
			CCNRouter adjRouter = Grid.getRouter(nodeId);
			adjRouter.getPacketsQ().addLast(curPacket);
			log.info(" nodeId:"+nodeId+":sourceId:"+curPacket.getSourceNode());
			}
		}
		log.info("}");
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
		str = "CCNRouter{ Id:"+getRouterId()+ " \nQueue:"+ getPacketsQ().toString()+"\n PIT:"+getPIT().toString()+"\n ForwardingTable"+
		getForwardingTable().toString()+"\nGlobalCache:"+getGlobalCache().toString()+"\nLocalCache:"+getLocalCache().toString()+"}\n";
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


};
