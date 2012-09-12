package com.simulator.packets;


//import org.apache.log4j.Logger;

import arjuna.JavaSim.Simulation.Scheduler;


import com.simulator.ccn.CCNQueue;
import com.simulator.ccn.CCNRouter;
import com.simulator.enums.PacketTypes;
import com.simulator.enums.SimulationTypes;
import com.simulator.enums.SupressionTypes;
import com.simulator.topology.Grid;

/* The following class is the Packet class which records information of each individual packet. Notice that it is not a 
 * SimulationProcess, but a simple class. It serves to three important tasks: 
 * (1) adds newly created interest packets into the queue of requesting nodes; 
 * (b) it activates these requesting nodes;
 *  (c) it collects the statistics for each packet
 *   NOTE:
 *  Important Note while adding elements to this class make sure you add only basic data types (i.e like int,char etc) , if you have to add Complex data types
 *  please make sure to edit clone() method of packets so that it works properly.
 *  */
public class InterestPacket extends Packets implements Cloneable{
	
	//static final Logger log = Logger.getLogger(InterestPacket.class);
	/**
	 * Source Packet Id only makes sense when the packet is a clone. And this represents the packetid of the source packet.
	 */
	private int sourcePacketId = 0;
	
	
	/**
	 * Type of the packet.
	 */
	private SimulationTypes packetType;

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

	public InterestPacket (Integer nodeId, int size,int segId) {
		
		setPacketId(getCurrenPacketId());
		setSegmentId(segId);
		setSourcePacketId(getPacketId());
		setPacketType(PacketTypes.PACKET_TYPE_INTEREST);
		setPrevHop(-1);
		setRefPacketId(-1);
		setOriginNode(nodeId);
		setSizeOfPacket(size);
		setAlive(true);
		setCauseOfSupr(SupressionTypes.SUPRESSION_NOT_APPLICABLE);
		
		setPrimaryInterestId(-1);
		setParentInterestId(-1);		
		setExpirationCount(0);	
		
		//log.info("node id = "+nodeId+" packet id ="+ getPacketId());
	}

	/* This method name is confusing. It does not need to be activate() as this method is pre-defined thread method */

	public void activate() {
		
		//log.info("Handling Interest Packet"+this.toString());
		CCNRouter router = Grid.getRouter(getOriginNode());
		CCNQueue packetsQ = router.getPacketsQ();
		
		/* CCNRouter is activated (put in the JavaSim's scheduler queue) when we add the packet to the queue in the last statement
		 * of the following method in CCNQueue
		 *  */
		packetsQ.addLast(this);
	}

	@Override
	public Object clone() {
		
		InterestPacket clonedPacket = (InterestPacket) super.clone();
		//clonedPacket.pathTravelled = new String(this.getPathTravelled());
		return clonedPacket;
	}	
};
