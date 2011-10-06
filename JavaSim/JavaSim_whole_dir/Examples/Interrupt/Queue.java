import java.lang.*;
import arjuna.JavaSim.*;
import java.util.NoSuchElementException;

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
    
public Job Dequeue () throws NoSuchElementException
    {
	if (IsEmpty())
	    throw(new NoSuchElementException());

	List ptr = head;
	head = head.next;

	length--;

	return ptr.work;
    }
    
public void Enqueue (Job toadd)
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
    }

private List head;
private long length;
    
};

/* This is the queue on which Jobs are placed before they are used. */

class List
{
    
public List ()
    {
	work = null;
	next = null;
    }

public Job work;
public List next;
    
};
