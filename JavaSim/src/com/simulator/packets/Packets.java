package com.simulator.packets;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Formatter;
import java.util.Map;

//import org.apache.log4j.Logger;


import com.simulator.ccn.IDEntry;
import com.simulator.controller.SimulationController;
import com.simulator.enums.PacketTypes;
import com.simulator.enums.SupressionTypes;


import arjuna.JavaSim.Simulation.*;

/* The following class is the Packet class which records information of each individual packet. Notice that it is not a 
 * SimulationProcess, but a simple class. It serves to three important tasks: 
 * (1) adds newly created interest packets into the queue of requesting nodes; 
 * (b) it activates these requesting nodes; 
 * (c) it collects the statistics for each packet
 *  NOTE:
 *  Important Note while adding elements to this class make sure you add only basic data types (i.e like int,char etc) , if you have to add Complext data types
 *  please make sure to edit clone() method of packets so that it works properly.
 *  */
public class Packets implements Cloneable {
	
	//static final Logger log = Logger.getLogger(Packets.class);
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
	 * SegmentId of this packet instance.
	 */
	private int segmentId = 0;
	
	/**
	 * Source Packet Id only makes sense when the packet is a clone. And this represents the packetid of the source packet.
	 */
	private int sourcePacketId = 0;
	
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
	private PacketTypes packetType;
	
	/**
	 * Number of hops 
	 */

	private int noOfHops=0;
	
	/**
	 * Useful number of hops 
	 */
	private int usefulNoOfHops=-1;
	
	/**
	 * Statistics dump file
	 */
	private static String dataDumpFile="dump/packetsDump.txt";
	/**
	 * Only meaningful for data packets. 0 means belongs to global cache and 1 means belongs to local cache.
	 * TODO as of now adding very naive way of setting local. Please revisit when have time.
	 */
	private boolean local;
	/**
	 * Indicates if the packet is still alive or isdead. This parameter is used to denote the state of the packet before dumping its statistics.
	 *
	 */
	private boolean alive;
	
	/**
	 * Denotes the cause of suppression. Used while dumping the the packet. Its is set by passing the cause of suppression to 
	 * finished method.
	 */
	private SupressionTypes causeOfSupr;
	
	/* The first interest packet id which requested the object. 
	 * It comes into use when a interest packet is timed out. 
	 * */
	private int primaryInterestId;
	
	/* When an interest packet is timed out, the interest packet 
	 * that has timed out will be stored parentInterestId field.
	 * */
	private int parentInterestId;

	
	private static int currentDataPacketId = 0;
	
	private int dataPacketId = 0;
	
	/* It will hold the history of which interest packet has contributed in fetching in this object to this point */
	private Map<IDEntry, Integer> historyOfDataPackets = null;
	
	public int getDataPacketId() {
		return dataPacketId;
	}
	
	public void setDataPacketId(int tempDataPacketId) {
		dataPacketId = tempDataPacketId;
	}
	
	public static int getCurrentDataPacketId() {
		return currentDataPacketId;
	}
	
	public static void incCurrentDataPacketId() {
		currentDataPacketId++;;
	}

	private int expirationCount;

	
	public Packets(Packets pac)	{}

	
	public Packets(){}
	/**
	 *  Activates packet. It performs necessary action on the packet. Depending on the packet type.
	 *  @author contra
	 *  TODO Also accept a parameter on what to do.
	 */
	
	
	/**
	 * This function is called when the Packet is about to Die.
	 * Various scenarios when a packet can die are
	 * 1. When there is No entry in pit table for this data packet.
	 * 2. Already served interest packet
	 * 3. Statisfying the interest packet by sending the 
	 *    corresponding data packet.
	 * 4. When there is a hit in forwarding table.
	 * 5. When there is already an entry in PIT table.  
	 */

	/* Called when a packet is being terminated */
	public void finished(SupressionTypes cause)	{
		
		ResponseTime = Scheduler.CurrentTime() - ArrivalTime;
		
		/* The following statement is setting the cause of why the packet is being terminated */
		setCauseOfSupr(cause);
		setAlive(false);
				
		/* Recording this instance in the trace file */
		dumpStatistics(this, "DESTROY");
		
		//log.info("Finished Packetid:"+getPacketId());
		SimulationController.incrementPacketsProcessed();
	}
	
	/* This method is for creating the trace file */
	public synchronized static void dumpStatistics(Packets curPacket, String status) {
		
		try {
			@SuppressWarnings("unused")
			
			StringBuilder str1 = new StringBuilder();
			Formatter str = new Formatter(str1);
			
			str.format("%(,2.4f",SimulationProcess.CurrentTime());
			
			if(PacketTypes.PACKET_TYPE_DATA == curPacket.getPacketType())
				str.format(" d");
			else 
				str.format(" i");
			
			str.format(" %d",curPacket.getPacketId());
			str.format(" %d",curPacket.getSegmentId());
			str.format(" %s", status);
			str.format(" %d",curPacket.getRefPacketId());
			str.format(" %d",curPacket.getCurNode());
			str.format(" %d",curPacket.getPrevHop());
			str.format(" %d",curPacket.getOriginNode());
			str.format(" %d",curPacket.getNoOfHops());
			
			//if(Integer.toBinaryString((curPacket.isAlive())?1:0).compareTo("1") == 0)
				//str.format(" alive");
			//else
				//str.format(" dead");
			
			str.format(" %d", (curPacket.isAlive())?1:0);			
			str.format(" %d", (curPacket.getCauseOfSupr().ordinal()));
			
			if(curPacket.isLocal())
				str.format(" 0");
			else
				str.format(" 1");			

			str.format(" %d",curPacket.getPrimaryInterestId());
			str.format(" %d",curPacket.getParentInterestId());
			str.format(" %d",curPacket.getExpirationCount());
			str.format(" %d",curPacket.getDataPacketId());
			
			if (PacketTypes.PACKET_TYPE_DATA == curPacket.getPacketType())
				str.format(" %d",curPacket.getUsefulNoOfHops());

			str.format("\n");
			SimulationController.fs.write(str.toString());
			
			//if (SimulationController.getDebugging() == SimulationTypes.SIMULATION_DEBUGGING_ON)
				//collectTrace (curPacket, status);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/* Used only when a data packet (CRTDPRDA) is created because of a PIT entry */
	public synchronized static void dumpStatistics(Packets curPacket, String status, Packets causalPacket) {
		
		try {
			@SuppressWarnings("unused")
			
			StringBuilder str1 = new StringBuilder();
			Formatter str = new Formatter(str1);
			
			str.format("%(,2.4f",SimulationProcess.CurrentTime());
			
			if(PacketTypes.PACKET_TYPE_DATA == curPacket.getPacketType())
				str.format(" d");
			else 
				str.format(" i");
			
			str.format(" %d",curPacket.getPacketId());
			str.format(" %d",curPacket.getSegmentId());
			str.format(" %s", status);
			str.format(" %d",curPacket.getRefPacketId());
			str.format(" %d",curPacket.getCurNode());
			str.format(" %d",curPacket.getPrevHop());
			str.format(" %d",curPacket.getOriginNode());
			str.format(" %d",curPacket.getNoOfHops());
			
			//if(Integer.toBinaryString((curPacket.isAlive())?1:0).compareTo("1") == 0)
				//str.format(" alive");
			//else
				//str.format(" dead");
			
			str.format(" %d", (curPacket.isAlive())?1:0);
			
			str.format(" %d", (curPacket.getCauseOfSupr().ordinal()));
			
			if(curPacket.isLocal())
				str.format(" 0");
			else
				str.format(" 1");
			

			str.format(" %d",curPacket.getPrimaryInterestId());
			str.format(" %d",curPacket.getParentInterestId());
			str.format(" %d",curPacket.getExpirationCount());
			str.format(" %d",curPacket.getDataPacketId());
			
			str.format(" %d",causalPacket.getDataPacketId());
			str.format(" %d",causalPacket.getPrimaryInterestId());
			str.format(" %d",causalPacket.getRefPacketId());
			str.format(" %d",causalPacket.getPrevHop());
			

			str.format("\n");
			SimulationController.fs.write(str.toString());
			
			//if (SimulationController.getDebugging() == SimulationTypes.SIMULATION_DEBUGGING_ON)
				//collectTrace (curPacket, status);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/* This method is for used to create a trace file without any "headings". It is used by the "ManipulateTrace" class */
	public synchronized static void collectTrace (Packets curPacket, String status)	{
		
		try {
			@SuppressWarnings("unused")
			
			Writer fs = new BufferedWriter(new FileWriter("dump/readableTrace.txt",true));
			StringBuilder str1 = new StringBuilder();
			Formatter str = new Formatter(str1);
			
			str.format("%(,2.4f\t",SimulationProcess.CurrentTime());
			
			if(PacketTypes.PACKET_TYPE_DATA == curPacket.getPacketType())
				str.format("d\t");
			else 
				str.format("i\t");	
			
			str.format("id:%2d\t",curPacket.getPacketId());
			str.format("seg:%2d\t",curPacket.getSegmentId());
			str.format("status=%s\t", status);
			str.format("object/interest=%2d\t",curPacket.getRefPacketId());
			str.format("curr=%2d\t",curPacket.getCurNode());
			str.format("prev:%2d\t",curPacket.getPrevHop());
			str.format("src:%2d\t",curPacket.getOriginNode());
			//str.format(" SRCPACK_ID:%2d",curPacket.getSourcePacketId());
			str.format("hops=%d\t",curPacket.getNoOfHops());
			str.format("dead/alive="+ Integer.toBinaryString((curPacket.isAlive())?1:0) + "\t");
			str.format("%s\t", (curPacket.getCauseOfSupr().toString()));
												
			if(curPacket.isLocal())
				str.format("lcache");
			else
				str.format("gcache");
						
			str.format("\n");
			fs.write(str.toString());
			fs.close();			
		}
		catch (IOException e) {
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
	
	public Integer getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Integer segId) {
		this.segmentId = segId;
	}

	public PacketTypes getPacketType() {
		return packetType;
	}

	public void setPacketType(PacketTypes packetType) {
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
	public String toString() {
		
		String str = new String();
		str = "Packet{PacketId: "+Integer.toString(packetId)+" PrevHop: "+getPrevHop().toString()+" No. Of hops:"+getNoOfHops()
		       +" CurrentNode"+getCurNode()+" OriginNode:"+getOriginNode()+" dataPacket:"+getRefPacketId()
		+" size:"+getSizeOfPacket()+" PacketType:"+getPacketType().toString()+"}\n";
		return str;		
	}
	@Override
	public Object clone() {
		
		try	{
			
			Packets clonedPacket = (Packets) super.clone();
			//clonedPacket.pathTravelled = new String(this.getPathTravelled());
			return clonedPacket;
		}
		catch(CloneNotSupportedException e)	{
			throw new Error("Got clone not support Exception in Packets class");
		}
	}
	
	/*
	 * Returns the size of packet.
	 * */
	public int getSizeOfPacket() {
		return sizeOfPacket;
	}

	
	/*
	 * Sets the size of the packet. 
	 * */
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
	
	public static synchronized Integer getCurrentPacketId() {
		return currenPacketId;
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
	public void incrHops() {
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
	
	public boolean isAlive() {
		return alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public SupressionTypes getCauseOfSupr() {
		return causeOfSupr;
	}
	
	public void setCauseOfSupr(SupressionTypes causeOfSupr) {
		this.causeOfSupr = causeOfSupr;
	}
	
	public int getSourcePacketId() {
		return sourcePacketId;
	}
	
	public void setSourcePacketId(int sourcePacketId) {
		this.sourcePacketId = sourcePacketId;
	}
	
	public int getPrimaryInterestId() {
		return primaryInterestId;
	}

	public void setPrimaryInterestId(int primaryInterestId) {
		this.primaryInterestId = primaryInterestId;
	}

	public int getParentInterestId() {
		return parentInterestId;
	}

	public void setParentInterestId(int parentInterestId) {
		this.parentInterestId = parentInterestId;
	}

	public int getExpirationCount() {
		return expirationCount;
	}

	public void setExpirationCount(int expirationCount) {
		this.expirationCount = expirationCount;
	}
	
	public Map<IDEntry, Integer> getHistoryOfDataPackets() {
		return historyOfDataPackets;
	}
	
	public void setHistoryOfDataPackets(
			Map<IDEntry, Integer> historyOfDataPackets) {
		this.historyOfDataPackets = historyOfDataPackets;
	}

	public int getUsefulNoOfHops() {
		return usefulNoOfHops;
	}

	public void setUsefulNoOfHops(int usefulNoOfHops) {
		this.usefulNoOfHops = usefulNoOfHops;
	}
};
