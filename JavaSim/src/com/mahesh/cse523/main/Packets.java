package com.mahesh.cse523.main;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.*;

public class Packets implements Cloneable
{
	static final Logger log = Logger.getLogger(Packets.class);
	/**
	 * This is global counter for packetId's. When ever you want to generate a packet ig call the getter of this var.
	 */
	private static int currenPacketId=0;
	/**
	 * Response Time. Gets updated once the packet is processed.
	 *TODO Hasn't implemented this feature yet. future work.
	 */
	private double ResponseTime;
	/**
	 * The time when packet arrived
	 */
	
	private double ArrivalTime;
	/**
	 * Id of this packet instance.
	 */
	private int packetId = 0;
	
	/**
	 * Node id of the previous hop from where the packet came.
	 */
	private int prevHop = -1;
	
	/**
	 * The id of the node where Interest Packet or Data Packet were orginated.
	 */
	
	private int originNode = -1;
	/**
	 * Id of the data packet this interest packet is interested in.
	 */
	
	/**
	 * Curnode denotes the Id of the node to which this packet currently belongs to.
	 */
	private int curNode = -1;
	/**
	 * This denotes the dataPacket Interest Packet is looking for and Interest Packet this data packets is looking to satisfy.
	 */
	private int refPacketId=0;
	/*
	 * gives the size of the packet. Set using setters and getters.
	 */
	private int sizeOfPacket = 1;
	
	/**
	 * Type of the packet.
	 */
	private SimulationTypes packetType;
	
	/**
	 * Number of hops 
	 */

	private int noOfHops=0;
	/**
	 * Statstics dump file
	 */
	private static String dataDumpFile="packetsDump.txt";
	/**
	 * Only meaningful for data packets. 0 means belongs to global cache and 1 means belongs to local cache.
	 * TODO as of now adding very naive way of setting local. Please revisit when have time.
	 */
	private boolean local;
	/**
	 * 
	 * This is a constructor of a packet. It takes following param and sets them.
	 * @param nodeId If its Interest Packet than this determines the source of
	 * 		   of the Interest Packet. When we are flooding we change the source
	 * 			of the Interest Packet to the node that is flooding.
	 * 		   else if a packet is of type Data Packet then nodeId represents the node which owns this data packet.
	 * @param packettype Type of the packet.
	 */
	public Packets (Integer nodeId, SimulationTypes packettype,Integer size)
	{
		setPacketId(getCurrenPacketId());
		setPacketType(packettype);
		setPrevHop(-1);
		setRefPacketId(-1);
		setOriginNode(nodeId);
		setSizeOfPacket(size);
		log.info("node id = "+nodeId+" packet id ="+ packetId);

		ResponseTime = 0.0;
		ArrivalTime = Scheduler.CurrentTime();
	}
	public Packets(Packets pac)
	{
		
	}
	/**
	 *  Activates packet. It performs necessary action on the packet. Depending on the packet type.
	 *  @author contra
	 *  TODO Also accept a parameter on what to do.
	 */
	public void activate()
	{
		if(SimulationTypes.SIMULATION_PACKETS_INTEREST == getPacketType())
			interestPacketHandler();
		else
			log.info("Not activation method specified or found");

	}
	
	
	private void interestPacketHandler()
	{
		log.info("Handling Interest Packet"+this.toString());
		CCNRouter router = Grid.getRouter(getOriginNode());
		CCNQueue packetsQ = router.getPacketsQ();
		packetsQ.add(this); //Note: Router activation is done when we add the packet to the queue by the queue
		//CCNRouter.TotalPackets++;
		//router.Activate();
	}
	
	/**
	 * 
	 */

	public void finished(String cause)
	{
		ResponseTime = Scheduler.CurrentTime() - ArrivalTime;
		SimulationController.incrementPacketsProcessed();
		try {
			@SuppressWarnings("unused")
			Writer fs = new BufferedWriter(new FileWriter("dump/packetsDump.txt",true));
			StringBuilder str = new StringBuilder();
			if(SimulationTypes.SIMULATION_PACKETS_DATA == getPacketType())
				str.append('d');
			else 
				str.append('i');
			str.append(" "+Double.toString(SimulationProcess.CurrentTime()));
			str.append(" "+getPacketId());
			str.append(" "+getOriginNode());
			str.append(" "+getCurNode());
			str.append(" "+getRefPacketId());
			str.append(" "+getNoOfHops());
			if(isLocal())
				str.append(" 1");
			else
				str.append(" 0");
			str.append(" "+cause);
			str.append('\n');
			fs.write(str.toString());
			fs.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public Integer getPacketId() {
		return packetId;
	}

	public void setPacketId(Integer packetId) {
		this.packetId = packetId;
	}

	public SimulationTypes getPacketType() {
		return packetType;
	}

	public void setPacketType(SimulationTypes packetType) {
		this.packetType = packetType;
	}
	public Integer getPrevHop() {
		return prevHop;
	}

	public void setPrevHop(Integer sourceNode) {
		this.prevHop = sourceNode;
	}
	/**
	 * Overriding toString method
	 */
	@Override
	public String toString()
	{
		String str = new String();
		str = "Packet{PacketId: "+Integer.toString(packetId)+" PrevHop: "+getPrevHop().toString()+" No. Of hops:"+getNoOfHops()
		       +" CurrentNode"+getCurNode()+" OriginNode:"+getOriginNode()+" dataPacket:"+getRefPacketId()
		+" size:"+getSizeOfPacket()+" PacketType:"+getPacketType().toString()+"}\n";
		return str;
		
	}
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
			
		}
		catch(CloneNotSupportedException e)
		{
			throw new Error("Got clone not support Exception in Packets class");
		}
	}
/*
 * Returns the size of packet.
 */
	public Integer getSizeOfPacket() {
		return sizeOfPacket;
	}
	/*
	 * Sets the size of the packet. 
	 */
	public void setSizeOfPacket(Integer sizeOfPacket) {
		this.sizeOfPacket = sizeOfPacket;
	}

	public Integer getRefPacketId() {
		return refPacketId;
	}
	public void setRefPacketId(Integer dataPacketId) {
		this.refPacketId = dataPacketId;
	}
	public static synchronized Integer getCurrenPacketId() {
		return currenPacketId++;
	}
	public static synchronized void setCurrenPacketId(Integer currenPacketId) {
		Packets.currenPacketId = currenPacketId;
	}
	public int getOriginNode() {
		return originNode;
	}
	public void setOriginNode(int originNode) {
		this.originNode = originNode;
	}
	public int getNoOfHops() {
		return noOfHops;
	}
	public void setNoOfHops(int noOfHops) {
		this.noOfHops = noOfHops;
	}
	/**
	 * Increments number of hops by one
	 */
	public void incrHops()
	{
		setNoOfHops(getNoOfHops()+1);
	}
	public static String getDataDumpFile() {
		return dataDumpFile;
	}
	public static void setDataDumpFile(String dataDumpFile) {
		Packets.dataDumpFile = dataDumpFile;
	}
	public int getCurNode() {
		return curNode;
	}
	public void setCurNode(int curNode) {
		this.curNode = curNode;
	}
	public boolean isLocal() {
		return local;
	}
	public void setLocality(boolean locality) {
		this.local = locality;
	}
	
};
