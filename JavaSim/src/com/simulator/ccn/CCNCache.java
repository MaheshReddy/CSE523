package com.simulator.ccn;

import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.simulator.packets.Packets;



public class CCNCache  {
	
	Logger log = Logger.getLogger(CCNCache.class);
	
	/**
	 * Cache element of CCNCache. Its a object which implements Map Interface.
	 * We have provided functions to insert, query and remove from this cache. 
	 * As of now we implement a LRU cache. 
	 */
	private Map<InterestEntry, Packets> cache;
	
	/**
	 * This denotes the maximum entries and size in the cache. 
	 * The cache will not have more than these number of entries.Before 
	 * doing any inserts we check if the resultant cache size is bigger
	 * than this limit so we employ a replacement strategy to free up space. 
	 */
	 private Integer cacheSize;
	
	/**
	 * Id of the node owning this Cache.
	 */
	private int nodeId;
	
	private float hashTableLoadFactor = 0.75f;
	/**
	 * Constructor of CCNCache which initializes cache and sets max-size to be
	 * 1000 and current size to zero.
	 * @param id 
	 */
	public CCNCache(int id, int size, boolean unlimitedSizeCache)	{
		
		setNodeId(id);		
		setCacheSize(size);
		Integer hashTableSize = (int) Math.ceil(getCacheSize() / hashTableLoadFactor) +1 ;
		
		if (unlimitedSizeCache) {
			cache = new LinkedHashMap<InterestEntry, Packets>(hashTableSize,hashTableLoadFactor,true);
		}
		else {
			cache = new LinkedHashMap<InterestEntry, Packets>(hashTableSize,hashTableLoadFactor,true) {
		      @Override protected boolean removeEldestEntry (Map.Entry<InterestEntry,Packets> eldest) {
		         return size() > CCNCache.this.cacheSize; }};
		}
	}
	/**
	 * Adds a packet to the cache. First checks if the packet size can fit into
	 * the cache by comparing the cache size to max size. Else calls one of
	 * the replacement strategies to make space for this packet in the cache.
	 * @param packet the packet to be inserted in to the cache
	 */
	public void addToCache(Packets packet) {
		
		InterestEntry dataObject = new InterestEntry (packet.getPacketId(), packet.getSegmentId());
		
		/* While adding to cache change the origin node of the packet to this node
		 * How will we know where was the object originally placed? */
		packet.setOriginNode(getNodeId());
	
		/* It will put if there is space, otherwise, it will create space, and place the entry */
		cache.put(dataObject, packet);	
	}
	
	/**
	* Retrieves an entry from the cache.<br>
	* The retrieved entry becomes the MRU (most recently used) entry.
	* @param key the key whose associated value is to be returned.
	* @return    the value associated to this key, or null if no value with this key exists in the cache.
	*/
	public synchronized Packets get (InterestEntry key) {
	   return cache.get(key); }
	
	/**
	* Adds an entry to this cache.
	* The new entry becomes the MRU (most recently used) entry.
	* If an entry with the specified key already exists in the cache, it is replaced by the new entry.
	* If the cache is full, the LRU (least recently used) entry is removed from the cache.
	* @param key    the key with which the specified value is to be associated.
	* @param value  a value to be associated with the specified key.
	*/
	public synchronized void put (InterestEntry key, Packets value) {
	   cache.put (key, value); }
	
	/**
	* Clears the cache.
	*/
	public synchronized void clear() {
	   cache.clear(); }
	
	/**
	* Returns the number of used entries in the cache.
	* @return the number of entries currently in the cache.
	*/
	public synchronized int usedEntries() {
	   return cache.size(); }
	
	/**
	* Returns a <code>Collection</code> that contains a copy of all cache entries.
	* @return a <code>Collection</code> with a copy of the cache content.
	*/
	public synchronized Collection<Map.Entry<InterestEntry,Packets>> getAll() {
	   return new ArrayList<Map.Entry<InterestEntry,Packets>>(cache.entrySet()); }
	
	public boolean isPresentInCache(Packets entry) {
		return cache.containsKey(entry.getPacketId());
	}
	
	@Override
	public String toString() {
		
		String str;
		str = "{ nodeId:"+getNodeId()+ " size:"+usedEntries()+ "\nCache:"+cache.toString()+"}\n";
		return str;
	}
	
	public Integer getCacheSize() {
		return cacheSize;
	}
	
	public void setCacheSize(Integer tempSize) {
		this.cacheSize = tempSize;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
}
