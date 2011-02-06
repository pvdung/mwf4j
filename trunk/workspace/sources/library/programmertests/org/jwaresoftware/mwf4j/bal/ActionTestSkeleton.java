/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.Test;

import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.starters.CheckPerformed;
import  org.jwaresoftware.mwf4j.starters.CheckPerformedInOrder;
import  org.jwaresoftware.mwf4j.starters.EchoAction;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.ExecutableTestSkeleton;
import  org.jwaresoftware.mwf4j.starters.SleepAction;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Starting implementation for an {@linkplain Action action} test suite. 
 * Mostly adds a set of convenient helper action factory methods.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public abstract class ActionTestSkeleton extends ExecutableTestSkeleton
{
    protected static final long _1SEC= TimeUnit.SECONDS.toMillis(1L);
    
//  ---------------------------------------------------------------------------------------
//  Harness preparation and verification methods
//  ---------------------------------------------------------------------------------------

    protected final static TouchAction touch(String id)
    {
        return new TouchAction(id);
    }

    protected final static ThrowAction error(String id)
    {
        return new ThrowAction(id);
    }

    protected final static ThrowAction error(String id, Exception what)
    {
        return new ThrowAction(id,what);
    }

    protected final static Action never()
    {
        return new EpicFail();
    }

    protected final static SequenceAction block(String id)
    {
        SequenceAction action = new SequenceAction(id);
        action.setMode(SequenceAction.Mode.MULTIPLE);
        return action;
    }

    protected final static Action sleep(long millis)
    {
        return new SleepAction(millis);
    }

    protected final static Action sleep(String id, long millis)
    {
        return new SleepAction(id,millis);
    }

    protected final static Action sleep1(String id)
    {
        if (id==null) id= SleepAction.idFrom(_1SEC);
        return new SleepAction(id,_1SEC);
    }

    protected final static Action sleepN(String id, int numsecs)
    {
        if (id==null) id= SleepAction.idFrom(_1SEC*numsecs);
        return new SleepAction(id,_1SEC*numsecs);
    }

    protected final static Action checkdone(String name)
    {
        return new CheckPerformed("chk."+name,name);
    }

    protected final static Action checkdone(String name, int times)
    {
        return new CheckPerformed("chk."+name,name,times);
    }

    protected final static Action checkdoneorder(String subid, String names)
    {
        return new CheckPerformedInOrder("chk."+subid,names);
    }

    protected final static Action echocursor(String id, String cursor)
    {
        return new EchoAction(id,cursor);
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    @Test
    public void testBaseline0()
    {
        assertNotNull(newTASK(),"newTASK");
    }
}


/* end-of-ActionTestSkeleton.java */
