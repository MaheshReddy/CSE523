package com.mahesh.cse523.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CCNCache  {
	/**
	 * Cache element of CCNCache. Its a object which implements Map Interface.
	 * We have provided functions to insert, query and remove from this cache. 
	 * As of now we implement a LRU cache. 
	 */
	private Map<Integer, Packets> cache;
	
	/**
	 * This is the max size upto which this cache can grow. 
	 * Before doing any inserts we check if the resultant cache size is bigger
	 * than this limit we do a employ a replacement strategy to free up sapce. 
	 */
	private Integer maxSize;
	/**
	 * This denotes the maximum entries in a cache.
	 */
	private Integer maxEntries;
	/** 
	 * This gives the current size of cache
	 */
	private Integer sizeOfCache;
	
	private int nodeId;
	
	private float hashTableLoadFactor = 0.75f;
	/**
	 * Constructor of CCNCache which intilizes cache and sets maxsize to be
	 * 1000 and current size to zero.
	 * @param id 
	 */
	public CCNCache(int id)
	{
		setNodeId(id);
		setMaxSize(10000);
		setSizeOfCache(0);
		setMaxEntries(10000);
		Integer hashTableSize = (int) Math.ceil(getMaxEntries() / hashTableLoadFactor) +1 ;
		cache = new LinkedHashMap<Integer,Packets>(hashTableSize,hashTableLoadFactor,true);
	}
	/**
	 * Adds a packet to the cache. First checks if the packet size can fit into
	 * the cache by comparing the cahce size to max size. Else calls one of
	 * the replacement strategies to make space for this packet in the cache.
	 * @param packet the packet to be inserted in to the cache
	 */
	public void addToCache(Packets packet)
	{
		if(getSizeOfCache() + packet.getSizeOfPacket() < getMaxSize())
		{
			cache.put(packet.getPacketId(), packet); // just put the packet upand return
			setSizeOfCache(getSizeOfCache()+packet.getSizeOfPacket());
			return;
		}
		// this is the else part where we need to free up memory
		Iterator<Integer> itr = cache.keySet().iterator(); // This iterator gives me a LRU sorted list.
		while(getSizeOfCache() + packet.getSizeOfPacket() > getMaxSize())
		{
			Integer key = (Integer) itr.next();
			setSizeOfCache(getSizeOfCache() - cache.get(key).getSizeOfPacket());
			cache.remove(key);
		}

		cache.put(packet.getPacketId(), packet); // just put the packet and return
		setSizeOfCache(getSizeOfCache()+packet.getSizeOfPacket());		
		return;
	}
	/**
	 * Removes a packet from the cache. and update the size of the cache. 
	 * @param packet
	 */
	public void removeFromCache(Packets packet)
	{
		if(cache.containsKey(packet.getPacketId()))
		{
		cache.remove(packet.getPacketId());
		setSizeOfCache(getSizeOfCache() - packet.getSizeOfPacket());
		}
		return;
	}
	
	public boolean isPresentInCache(Packets packet)
	{
		return cache.containsKey(packet.getPacketId());
	}
	
	@Override
	public String toString()
	{
		String str;
		str = "Cache{ nodeId:"+getNodeId()+ " size:"+getSizeOfCache()+ "\nCache:"+cache.toString()+"}\n";
		return str;
	}
	public Packets getaPacketFromCache(Integer pacektId)
	{
		return cache.get(pacektId);
	}
	public Integer getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	public Integer getSizeOfCache() {
		return sizeOfCache;
	}
	public void setSizeOfCache(Integer sizeOfCache) {
		this.sizeOfCache = sizeOfCache;
	}
	public Integer getMaxEntries() {
		return maxEntries;
	}
	public void setMaxEntries(Integer maxEntries) {
		this.maxEntries = maxEntries;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
}
