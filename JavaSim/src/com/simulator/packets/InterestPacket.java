package com.simulator.packets;


import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.Scheduler;

import com.simulator.enums.SimulationTypes;

/* The following class is the Packet class which records information of each individual packet. Notice that it is not a 
 * SimulationProcess, but a simple class. It serves to three important tasks: (1) adds newly created interest packets into the queue of
 * requesting nodes; (b) it activates these requesting nodes; (c) it collects the statistics for each packet
 *  */
public class InterestPacket extends Packets implements Cloneable{
	
	/**
	 * 
	 * This is a constructor of a packet. It takes following param and sets them.
	 * @param nodeId If its Interest Packet than this determines the source of
	 * 		   of the Interest Packet. When we are flooding we change the source
	 * 			of the Interest Packet to the node that is flooding.
	 * 		   else if a packet is of type Data Packet then nodeId represents the node which owns this data packet.
	 * @param packettype Type of the packet.
	 * Notes:
	 * When a packet is created 
	 * We get a unique Id from a static packet Id generator and assign it to PacketId. We also assign the same Id of sourceId since we
	 * are creating the packet here.
	 */
	public InterestPacket (Integer nodeId, int size, int segId) {
		
		setPacketId(getCurrenPacketId());
		setSourcePacketId(getPacketId());
		setSegmentId (segId);
		setPacketType(SimulationTypes.SIMULATION_PACKETS_INTEREST);
		setPrevHop(-1);
		setRefPacketId(-1);
		setOriginNode(nodeId);
		setSizeOfPacket(size);
		setAlive(true);
		setCauseOfSupr(SimulationTypes.SIMULATION_NOT_APPLICABLE);
		log.info("node id = "+nodeId+" packet id ="+ getPacketId());
	}
	
	static final Logger log = Logger.getLogger(InterestPacket.class);
	/**
	 * Source Packet Id only makes sence when the packet is a clone. And this represents the packetid of the source packet.
	 */
	private int sourcePacketId = 0;
	/**
	 * Type of the packet.
	 */
	private SimulationTypes packetType;
	
	/**
	 * Comma seperated list of nodes traversed by this packet. Only used of debugging purpose.
	 */
	private String pathTravelled;
	/**
	 * 
	 * This is a constructor of a packet. It takes following param and sets them.
	 * @param nodeId If its Interest Packet than this determines the source of
	 * 		   of the Interest Packet. When we are flooding we change the source
	 * 			of the Interest Packet to the node that is flooding.
	 * 		   else if a packet is of type Data Packet then nodeId represents the node which owns this data packet.
	 * @param packettype Type of the packet.
	 * Notes:
	 * When a packet is created 
	 * We get a unique Id from a static packet Id generator and assign it to PacketId. We also assign the same Id of sourceId since we
	 * are creating the packet here.
	 */
};
