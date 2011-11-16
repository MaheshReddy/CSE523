package com.mahesh.cse523.main;
import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.*;

public class Packets implements Cloneable
{
	static final Logger log = Logger.getLogger(Packets.class);
	private static Integer currenPacketId=0;
	private double ResponseTime;
	private double ArrivalTime;
	private int packetId = 0;
	private int sourceNode = -1;
	/**
	 * Id of the data packet this interest packet is interested in.
	 */
	private int dataPacketId=0;
	/*
	 * gives the size of the packet. Set using setters and getters.
	 */
	private int sizeOfPacket = 1;
	private SimulationTypes packetType;



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
		setSourceNode(nodeId);
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
		CCNRouter router = Grid.getRouter(getSourceNode());
		CCNQueue packetsQ = router.getPacketsQ();
		boolean empty = packetsQ.isEmpty();
		packetsQ.add(this); //Note: Router activation is done when we add the packet to the queue by the queue
		//CCNRouter.TotalPackets++;
		//router.Activate();
	}

	public void finished ()
	{
		ResponseTime = Scheduler.CurrentTime() - ArrivalTime;
		SimulationController.incrementPacketsProcessed();
		//CCNRouter.TotalResponseTime += ResponseTime;	
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
	public Integer getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(Integer sourceNode) {
		this.sourceNode = sourceNode;
	}
	/**
	 * Overriding toString method
	 */
	@Override
	public String toString()
	{
		String str = new String();
		str = "Packet{PacketId: "+Integer.toString(packetId)+" NodeId: "+getSourceNode().toString()+" dataPacket:"+getDataPacketId()
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

	public Integer getDataPacketId() {
		return dataPacketId;
	}
	public void setDataPacketId(Integer dataPacketId) {
		this.dataPacketId = dataPacketId;
	}
	public static synchronized Integer getCurrenPacketId() {
		return currenPacketId++;
	}
	public static synchronized void setCurrenPacketId(Integer currenPacketId) {
		Packets.currenPacketId = currenPacketId;
	}
	
};
