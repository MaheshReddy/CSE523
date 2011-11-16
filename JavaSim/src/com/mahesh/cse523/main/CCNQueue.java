package com.mahesh.cse523.main;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

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

 public void addLast(Packets packet)
 {
	 //log.info("Addindg in last" + packet.toString()+" to queue");
	 //Need to use clone as we don't want future changes made to same packet.
	 queue.addLast((Packets)packet.clone());
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
 
 public void add(Packets packet)
 {
	 //log.info("Adding" + packet.toString()+" to queue");
	 addLast(packet);
	 
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
 
// Statics Variables
private DescriptiveStatistics queueLengthStatistics = new DescriptiveStatistics();

public Integer getMaxSize() {
	return maxSize;
}

public void setMaxSize(Integer maxSize) {
	this.maxSize = maxSize;
}
};
