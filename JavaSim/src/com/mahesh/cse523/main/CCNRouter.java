package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCNRouter extends SimulationProcess
{
	private  Machine M = null;
	private  CCNQueue PacketsQ = null;
	private int serverId = 0;
	Map<Integer,List<Integer>> PIT=null;


	
	public CCNRouter (int i)
	{
		PacketsQ = new CCNQueue();
		PIT = new HashMap<Integer,List<Integer>>();
		M = new Machine(PacketsQ,PIT,i,8);
		serverId=i;
		
	}
	public CCNRouter()
	{
	//	PacketsQ = new Queue();
		//M = new Machine(PacketsQ,8);
	}

	public Machine getM() {
		return M;
	}
	public void setM(Machine m) {
		M = m;
	}
	public CCNQueue getPacketsQ() {
		return PacketsQ;
	}
	public void setPacketsQ(CCNQueue packetsQ) {
		PacketsQ = packetsQ;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public boolean isPresentInPit(Integer packetId)
	{
		if(PIT.containsKey(packetId))
			return true;
		else
			return false;
	}
	
	public boolean isPresentInQueue(Packets packet)
	{
	    if(PacketsQ.contains(packet))
	    	return true;
	    else
	    	return false;
	}
	public Map<Integer, List<Integer>> getPIT() {
		return PIT;
	}
	public void setPIT(Map<Integer, List<Integer>> pIT) {
		PIT = pIT;
	}

};
