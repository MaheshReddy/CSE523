package arjuna.JavaSim.Statistics;

import java.lang.Exception;

/**
  General exception thrown by the statistics gathering classes.
  */

public class StatisticsException extends Exception
{

public StatisticsException ()
    {
	super();
    }

public StatisticsException (String s)
    {
	super(s);
    }

};
