/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.testng.annotations.Test;

import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.ExecutableTestSkeleton;
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
        return TestFixture.werePerformed(statementNames,'|');
    }

    protected final void clrPerformed()
    {
        TestFixture.clrPerformed();
    }

    protected final TouchAction touch(String id)
    {
        return new TouchAction(id);
    }

    protected final ThrowAction error(String id)
    {
        return new ThrowAction(id);
    }

    protected final ThrowAction error(String id, Exception what)
    {
        return new ThrowAction(id,what);
    }

    protected final Action never()
    {
        return new EpicFail();
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
