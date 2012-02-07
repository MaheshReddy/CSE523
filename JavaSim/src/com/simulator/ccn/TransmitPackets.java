package com.simulator.ccn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;

import com.simulator.packets.Packets;
import com.simulator.topology.Grid;

import arjuna.JavaSim.Simulation.*;;

public class TransmitPackets extends SimulationProcess {
	
	Packets curPacket;
	CCNRouter dstNode;
	static final Logger log = Logger.getLogger(TransmitPackets.class);
	private static double transDelay;
	
	TransmitPackets (Packets tempPacket,int destNode) {
		curPacket = tempPacket;
		dstNode = Grid.getRouter(destNode);
	}
	
	public void run() {
		
		//try {
			//This statement simulates a "transmission delay"
			//Hold(0.01);
			log.info("Sending packet id:"+curPacket.getPacketId()+"Srcnode:"+curPacket.getCurNode()+" DstNode:"+dstNode.getRouterId());
			dstNode.getPacketsQ().addLast(curPacket);
			 //pacToadd.setPacketId(Packets.getCurrenPacketId());
			//node.sendPacket(pacToadd, sendNode);
			this.terminate();
		//}
		//catch (SimulationException e) {
			//log.info("Exception "+ e.toString());
		//}
		//catch (RestartException e) {
			//log.info("Exception "+ e.toString());
    }

	public static double getTransDelay() {
		return transDelay;
	}

	public static void setTransDelay(double transDelay) {
		TransmitPackets.transDelay = transDelay;
	}
}