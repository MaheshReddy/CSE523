package com.simulator.ccn;

import java.util.Iterator;
import java.util.List;

import com.simulator.packets.Packets;

public class PITEntry {
	
	private int outgoingInterface;
	private int refPacketId;
	private double createdAtTime;
	
	PITEntry () {
		
		outgoingInterface = 0;
		createdAtTime = 0;	
		refPacketId = 0;
	}
	
	PITEntry (int tempInt, double tempTime) {
		
		outgoingInterface = tempInt;
		createdAtTime = tempTime;		
	}
	
	PITEntry (int tempInt, int tempPacketId, double tempTime) {
		
		outgoingInterface = tempInt;
		refPacketId = tempPacketId;
		createdAtTime = tempTime;		
	}
	
	public boolean equals(Object o) {
		
	    if (o == this)
	        return true;
	    if (!(o instanceof PITEntry))
	        return false;
	    
	    PITEntry pn = (PITEntry)o;
	    return pn.outgoingInterface == outgoingInterface 
	    		&& pn.refPacketId == refPacketId;
	}
	
	public int hashCode() {
		
		int hash = 7;
		hash = 31 * hash + outgoingInterface;
		hash = 31 * hash + refPacketId;
		
		long bits = Double.doubleToLongBits(createdAtTime);
		int var_code = (int)(bits ^ (bits >>> 32));		
		hash = 31 * hash + var_code;	
		
		return hash;	   
	}
	
	
	public void setRefPacketId (int temp) {
		refPacketId = temp;
	}
	
	public void setOutgoingInterface (int temp) {
		outgoingInterface = temp;
	}
	
	
	public void setCreatedAtTime (double temp) {
		createdAtTime = temp;
	}
	
	public int getoutgoingInterface () {
		return outgoingInterface;
	}
	
	public int getRefPacketId () {
		return refPacketId;
	}
	
	public double getCreatedAtTime () {
		return createdAtTime;
	}			
}
