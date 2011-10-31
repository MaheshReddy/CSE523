package com.mahesh.cse523.main;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class CCNQueue extends LinkedList<Object>
{
    
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public CCNQueue ()
    {
//	head = null;
	length = 0;
    }

    

private List<Packets> head;
private long length;
public long TotalPackets = 0;
public long ProcessedPackets = 0;
public  long PacketssInCCNQueue = 0;
public  long averageCCNQueueTime = 0;

// Statics Variables
private DescriptiveStatistics queueLengthStatistics = new DescriptiveStatistics();
};
