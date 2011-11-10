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
	public long TotalPackets = 0;
	public long ProcessedPackets = 0;
	public  long PacketssInCCNQueue = 0;
	public  long averageCCNQueueTime = 0;

public CCNQueue ()
	{
		queue = new LinkedList<Packets>();
		maxSize = 10000;
    }

 public void addLast(Packets packet)
 {
	 log.info("Adding in last" + packet.toString()+" to queue");
	 queue.addLast(packet);
 }
 
 public boolean isEmpty()
 {
	return queue.isEmpty();
	 
 }

 public Packets remove()
 {
	 Packets packet = queue.remove();
	 log.info("removing" + packet.toString()+" from queue");
	 return packet;
 }
 
 public void add(Packets packet)
 {
	 log.info("Adding" + packet.toString()+" to queue");
	 queue.add(packet);
 }
 
 public boolean contains(Packets packet)
 {
	return queue.contains(packet);
	 
 }
// Statics Variables
private DescriptiveStatistics queueLengthStatistics = new DescriptiveStatistics();
};
