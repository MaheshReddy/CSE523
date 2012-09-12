package com.simulator.ccn;
import java.util.LinkedList;
//import org.apache.log4j.Logger;

import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

/*
 * This class manages the CCN routers queue
 */

public class CCNQueue {
    
	//private static final Logger log = Logger.getLogger(CCNQueue.class);
	private LinkedList<Packets> queue = null;
	private Integer maxSize = 0;
	private int nodeId;
	public long TotalPackets = 0;
	public long ProcessedPackets = 0;
	public  long PacketssInCCNQueue = 0;
	public  long averageCCNQueueTime = 0;

	public CCNQueue (int id) {
		setNodeId(id);
		queue = new LinkedList<Packets>();
		maxSize = 10000;//useless value
    }

	/**
	 * Add a packet to end of the queue. 
	 * @param packet
	 */
	public void addLast(Packets packet) {		 
		 
		/* Updating the current node of the packet */
		packet.setCurNode(getNodeId()); 
		
		/* Add packet at the end of the neighboring nodes queue */
		queue.addLast(packet);
		
		/* To maintain current queue size */
		PacketssInCCNQueue++;
		 		
		/* We need to record packet is sent into the queue 
		 * Packets.dumpStatistics(packet);
		 * */
		Packets.dumpStatistics(packet,"ENQUEUE");
		
		/* Calling router activate every time a packet is added to its queue to make sure its on the scheduling queue */	 
		CCNRouter rtr = Grid.getRouter(getNodeId());
		rtr.Activate();	 
	}
	 
	public boolean isEmpty() {
		return queue.isEmpty();	 
	}
		
	/* Returns current queue size */
	public long packetsInCCNQueue () {
		return PacketssInCCNQueue;
	}
	
	public Packets remove() {
		 
		Packets packet = queue.remove();
		//log.info("removing" + packet.toString()+" from queue");
		//log.info("Current Queue " + queue);
		PacketssInCCNQueue--;
		return packet;
	}
	 
	public boolean contains(Packets packet) {
		return queue.contains(packet);		 
	}
	 
	public void setNodeId(int id) {
		nodeId=id;
	}
	 
	public int getNodeId() {
		return nodeId;
	}
	 
	@Override
	public String toString() {
		 
		String str;
		str = "Queue{"+queue.toString()+"}";
		return str;
	}
	 
	public Integer getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}
};
