package arjuna.JavaSim.Simulation;

import java.util.NoSuchElementException;

public class SimulationProcessList
{
    
public SimulationProcessList ()
    {
	Head = null;
    }

public synchronized void Insert (SimulationProcess p)
    {
	Insert(p, false);
    }
    
public synchronized void Insert (SimulationProcess p, boolean prior)
    {
	// If list is empty, insert at head
	
	if (Head == null)
	{
	    Head = new SimulationProcessCons(p, null);
	    return;
	}

	// Try to insert before (if there is anything scheduled later)
	
	SimulationProcessIterator iter = new SimulationProcessIterator(this);
	SimulationProcess prev = null;
    
	for (SimulationProcess q = iter.get(); q != null; prev = q, q = iter.get())
	{
	    if (prior)
	    {
		if (q.evtime() >= p.evtime())
		{
		    InsertBefore(p, q);
		    return;
		}
	    }
	    else
	    {
		if (q.evtime() > p.evtime())
		{
		    InsertBefore(p, q);
		    return;
		}
	    }
	}

	// Got to insert at the end (currently pointed at by 'prev')

	InsertAfter(p, prev);
    }
    
public synchronized boolean InsertBefore (SimulationProcess ToInsert, SimulationProcess Before)
    {
	for (SimulationProcessCons prev = null, p = Head; p != null; prev = p, p = p.cdr())
	{
	    if (p.car() == Before)
	    {
		SimulationProcessCons newcons = new SimulationProcessCons(ToInsert, p);
		if (prev != null)
		    prev.SetfCdr(newcons);
		else
		    Head = newcons;

		return true;
	    }
	}
	
	return false;	
    }

public synchronized boolean InsertAfter (SimulationProcess ToInsert, SimulationProcess After)
    {
	for (SimulationProcessCons p = Head; p != null; p = p.cdr())
	    if (p.car() == After)
	    {
		SimulationProcessCons newcons = new SimulationProcessCons(ToInsert, p.cdr());
		p.SetfCdr(newcons);
		return true;
	    }
	
	return false;
    }
    
public synchronized SimulationProcess Remove (SimulationProcess element) throws NoSuchElementException
    {
	// Take care of boundary condition - empty list

	if (Head == null)
	    throw(new NoSuchElementException());

	SimulationProcess p = null;
	
	for (SimulationProcessCons prev = null, ptr = Head; ptr != null; prev = ptr, ptr = ptr.cdr())
	{
	    if (ptr.car() == element)
	    {
		SimulationProcessCons oldcons = ptr;
		
		// unlink the cons cell for the element we're removing
		
		if (prev != null)
		    prev.SetfCdr(ptr.cdr());
		else
		    Head = ptr.cdr();
		
		// return the pointer to the process
		p = ptr.car();
		
		return p;
	    }
	}

	throw(new NoSuchElementException());
    }

public synchronized SimulationProcess Remove () throws NoSuchElementException
    {
	// Change unspecified element to "remove head of list" request
	
	if (Head != null)
	    return(Remove(Head.car()));
	else
	    throw(new NoSuchElementException());
    }
    
public synchronized SimulationProcess getNext (SimulationProcess current) throws NoSuchElementException
    {
	// take care of boundary condition - empty list.

	if ((Head == null) || (current == null))
	    throw(new NoSuchElementException());

	for (SimulationProcessCons ptr = Head; ptr != null; ptr = ptr.cdr())
	{
	    if (ptr.car() == current)
	    {
		if (ptr.cdr() == null)
		    return null;
		else
		    return ptr.cdr().car();
	    }
	    else  // terminate search - past the point current could be
		if (ptr.car().evtime() > current.evtime())
		    break;
	}

	/*
	 * If we get here then we have not found current on the list
	 * which can only mean that it is currently active.
	 */

	return Head.car();
    }

public void print ()
    {
	SimulationProcessIterator iter = new SimulationProcessIterator(this);
	SimulationProcess prev = null;
    
	for (SimulationProcess q = iter.get(); q != null; prev = q, q = iter.get())	
	{
	    System.out.println(q.evtime());
	}
    }

    // package?
    
protected SimulationProcessCons Head;

};


class SimulationProcessIterator
{
    
public SimulationProcessIterator (SimulationProcessList L)
    {
	ptr = L.Head;
    }
    
public final synchronized SimulationProcess get ()
    {
	if (ptr != null)
	{
	    SimulationProcessCons p = ptr;
	    ptr = ptr.cdr();
	    return p.car();
	}

	return null;
    }
    
private SimulationProcessCons ptr;

};


class SimulationProcessCons
{
    
public SimulationProcessCons (SimulationProcess p, SimulationProcessCons n)
    {
	Proc = p;
	Next = n;
    }
    
public final SimulationProcess car ()
    {
	return Proc;
    }
    
public final SimulationProcessCons cdr ()
    {
	return Next;
    }
    
public final void SetfCdr (SimulationProcessCons n)
    {
	Next = n;
    }
    
private SimulationProcess Proc;
private SimulationProcessCons Next;
    
};
