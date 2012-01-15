package com.simulator.ccn;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

public class CCNQueue 
{
    
	private static final Logger log = Logger.getLogger(CCNQueue.class);
	private LinkedList<Packets> queue = null;
	private Integer maxSize = 0;
	private List<Packets> head;
	private long length;
	private int nodeId;
	public long TotalPackets = 0;
	public long ProcessedPackets = 0;
	public  long PacketssInCCNQueue = 0;
	public  long averageCCNQueueTime = 0;

public CCNQueue (int id)
	{
		setNodeId(id);
		queue = new LinkedList<Packets>();
		maxSize = 10000;
    }

/**
 * Add a packet to end of the queue. 
 * @param packet
 */
 public void addLast(Packets packet)
 {
	 //log.info("Addindg in last" + packet.toString()+" to queue");
	 //Need to use clone as we don't want future changes made to same packet.
	 //pacToadd.setPacketId(Packets.getCurrenPacketId());
	 packet.setCurNode(getNodeId()); // Updating the curnode of the packet
	 //pacToadd.setSourcePacketId(packet.getPacketId());
	 queue.addLast(packet);
	 //log.info("Current Queue " + queue);
	// Calling router activate every time  a packet is added to its queue. to make sure its on the scheduling queue 
	 CCNRouter rtr = Grid.getRouter(getNodeId());
	 rtr.Activate();
 }
 
 public boolean isEmpty()
 {
	return queue.isEmpty();
	 
 }

 public Packets remove()
 {
	 Packets packet = queue.remove();
	 log.info("removing" + packet.toString()+" from queue");
	 log.info("Current Queue " + queue);
	 return packet;
 }
 

 public boolean contains(Packets packet)
 {
	return queue.contains(packet);
	 
 }
 public void setNodeId(int id)
 {
	 nodeId=id;
 }
 public int getNodeId()
 {
	 return nodeId;
 }
 @Override
 public String toString()
 {
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
