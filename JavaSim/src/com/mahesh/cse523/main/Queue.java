package com.mahesh.cse523.main;
import java.util.NoSuchElementException;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class Queue
{
    
public Queue ()
    {
	head = null;
	length = 0;
    }

public boolean IsEmpty ()
    {
	if (length == 0)
	    return true;
	else
	    return false;
    }
    
public long QueueSize ()
    {
	return length;
    }
    
public Packets Dequeue () throws NoSuchElementException
    {
	if (IsEmpty())
	    throw(new NoSuchElementException());

	List ptr = head;
	head = head.next;

	length--;
	queueLengthStatistics.addValue(length);

	return ptr.work;
    }
    
public void Enqueue (Packets toadd)
    {
	if (toadd == null)
	    return;

	List ptr = head;
    
	if (IsEmpty())
	{
	    head = new List();
	    ptr = head;
	}
	else
	{
	    while (ptr.next != null)
		ptr = ptr.next;
	    
	    ptr.next = new List();
	    ptr = ptr.next;
	}

	ptr.next = null;
	ptr.work = toadd;
	length++;
	queueLengthStatistics.addValue(length);
	TotalPackets++;
	PacketssInQueue++;
    }

private List head;
private long length;
public long TotalPackets = 0;
public long ProcessedPackets = 0;
public  long PacketssInQueue = 0;
public  long averageQueueTime = 0;

// Statics Variables
private DescriptiveStatistics queueLengthStatistics = new DescriptiveStatistics();

    
};

/* This is the queue on which Jobs are placed before they are used. */

class List
{
    
public List ()
    {
	work = null;
	next = null;
    }

public Packets work;
public List next;
    
};
