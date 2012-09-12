package com.simulator.ccn;

//import org.apache.log4j.Logger;

import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

import arjuna.JavaSim.Simulation.*;;

/* The following class implements the logic of transmitting a packet. It is always called before sending a packet with a
 * delay, which represents the transmission delay. It is immediately terminated after implementing transmission which is
 * to simply add the packet into the queue of the destination node. The delay is implemented by calling this class with explicit
 * delay.
 * */
public class TransmitPackets extends SimulationProcess {
	
	Packets curPacket;
	CCNRouter dstNode;
	//static final Logger log = Logger.getLogger(TransmitPackets.class);
	private static double transDelay;
	
	TransmitPackets (Packets tempPacket,int destNode) {
		curPacket = tempPacket;
		dstNode = Grid.getRouter(destNode);
	}
	
	public void run() {
		
		//log.info("Sending packet id:"+curPacket.getPacketId()+"Srcnode:"+curPacket.getCurNode()+" DstNode:"+dstNode.getRouterId());
		dstNode.getPacketsQ().addLast(curPacket);

		this.terminate();		
    }

	public static double getTransDelay() {
		return transDelay;
	}

	public static void setTransDelay(double transDelay) {
		TransmitPackets.transDelay = transDelay;
	}
}