package com.mahesh.cse523.main;
import arjuna.JavaSim.Simulation.*;

public class Packets
{
	private double ResponseTime;
	private double ArrivalTime;
	private Integer packetId = 0;

	public Packets (Integer nodeId, Integer packetid, SimulationTypes packetType)
	{
		boolean empty = false;
		packetId= packetid;
		System.out.println("node id = "+nodeId+" packet id ="+ packetId);

		ResponseTime = 0.0;
		ArrivalTime = Scheduler.CurrentTime();
		CCNRouter router = Grid.getRouter(nodeId);
		Queue packetsQ = router.getPacketsQ();
		Machine m = router.getM();
		empty = packetsQ.IsEmpty();
		packetsQ.Enqueue(this);
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



};
