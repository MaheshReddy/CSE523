package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;

public class Packets
{
	private double ResponseTime;
	private double ArrivalTime;
	private Integer packetId = 0;
	private Integer sourceNode = -1;
	
	private SimulationTypes packetType;

	

	public Packets (Integer nodeId, Integer packetid, SimulationTypes packettype)
	{
		boolean empty = false;
		setPacketId(packetid);
		setPacketType(packettype);
		setSourceNode(nodeId);
		
		System.out.println("node id = "+nodeId+" packet id ="+ packetId);

		ResponseTime = 0.0;
		ArrivalTime = Scheduler.CurrentTime();
		CCNRouter router = Grid.getRouter(nodeId);
		CCNQueue packetsQ = router.getPacketsQ();
		Machine m = router.getM();
		empty = packetsQ.isEmpty();
		packetsQ.add(this);
		//CCNRouter.TotalPackets++;

		if (empty && !m.Processing() && m.IsOperational())
		{
			try
			{
				m.Activate();
			}
			catch (SimulationException e)
			{
			}
			catch (RestartException e)
			{
			}
		}
	}

	public void finished ()
	{
		ResponseTime = Scheduler.CurrentTime() - ArrivalTime;
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
	@Override
	public String toString()
	{
		String str = new String();
		str = "PacketId->"+packetId.toString()+"\t";
		str = str+"NodeId->"+getSourceNode().toString()+"\t";
		str=str+"PacketType->"+getPacketType().toString()+"\n";
		return str;
		
	}

};
