package com.simulator.packets;


import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.Scheduler;

import com.simulator.enums.DataPacketsApplTypes;
import com.simulator.enums.SimulationTypes;


/* *
 * This is class for DataPacket for CCN networks it extends Packets Class. It has constructors for GlobeTraffic Generator. 
**/
public class DataPacket extends Packets implements Cloneable {
	
	private DataPacketsApplTypes applType;
	private int popularity;
	/**
	 * 
	 * This is a constructor of a packet. It takes following parameters and sets them.
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
	public DataPacket(Integer nodeId, int size) {
		
		setPacketId(getCurrenPacketId());
		setSourcePacketId(getPacketId());
		setPacketType(SimulationTypes.SIMULATION_PACKETS_DATA);
		setPrevHop(-1);
		setRefPacketId(-1);
		setOriginNode(nodeId);
		setSizeOfPacket(size);
		setAlive(true);
		setCauseOfSupr(SimulationTypes.SIMULATION_NOT_APPLICABLE);
		log.info("node id = "+nodeId+" packet id ="+ getPacketId());
	}
	/**
	 * Constructor for DataPacket which parses a line from Doc.all from GlobeTraffic and sets the properties accordinly.
	 * @param line line from docs.all file
	 * @param nodeId node to which this packet is assigned.
	 */
	public DataPacket(String line,int nodeId)
	{
		String [] words = line.split("\\s+");
		System.out.println(nodeId);
		System.out.println(words[0]+":"+Integer.parseInt(words[2]));
		setPacketId(Integer.parseInt(words[0]));
		setPopularity(Integer.parseInt(words[1]));
		setApplType(DataPacketsApplTypes.values()[Integer.parseInt(words[3])]);
		setSizeOfPacket(Integer.parseInt(words[2]));
		setSourcePacketId(getPacketId());
		setPacketType(SimulationTypes.SIMULATION_PACKETS_DATA);
		setPrevHop(-1);
		setRefPacketId(-1);
		setOriginNode(nodeId);
		setAlive(true);
		setCauseOfSupr(SimulationTypes.SIMULATION_NOT_APPLICABLE);
		log.info("node id = "+nodeId+" packet id ="+ getPacketId());

		//super(1,SimulationTypes.SIMULATION_PACKETS_DATA,2);
	}
	static final Logger log = Logger.getLogger(DataPacket.class);
	public DataPacketsApplTypes getApplType() {
		return applType;
	}

	public void setApplType(DataPacketsApplTypes applType) {
		this.applType = applType;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
		
};
