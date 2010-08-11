/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.Test;

import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.ExecutableTestSkeleton;
import  org.jwaresoftware.mwf4j.starters.SleepAction;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Starting implementation for an {@linkplain Action action} test suite. Most functions
 * have been pulled up to ExecutableTestSkeleton and TestFixture but we define some
 * shorthand convience methods here.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
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

    protected final void iniStatementCount()
    {
        TestFixture.iniStatementCount();
    }
    
    protected final int getStatementCount()
    {
        return TestFixture.getStatementCount();
    }

    protected final void iniPerformedList()
    {
        TestFixture.iniPerformedList();
    }

    protected final boolean wasPerformed(String statementName) 
    {
        return TestFixture.wasPerformed(statementName);
    }

    protected final boolean wasPerformed(String statementName, int count) 
    {
        return TestFixture.wasPerformed(statementName,count);
    }
    
    protected final boolean werePerformedInOrder(String statementNames)
    {
        return TestFixture.werePerformedInOrder(statementNames,'|',true);
    }

    protected final boolean werePerformedInRelativeOrder(String statementNames)
    {
        return TestFixture.werePerformedInRelativeOrder(statementNames,'|',true);
    }

    protected final boolean werePerformed(String statementNames)
    {
        return TestFixture.werePerformed(statementNames,'|',true);
    }

    protected final boolean werePerformedAndExited(String statementNames)
    {
        return TestFixture.werePerformed(statementNames,'|',false);
    }

    protected final void clrPerformed()
    {
        TestFixture.clrPerformed();
    }

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
