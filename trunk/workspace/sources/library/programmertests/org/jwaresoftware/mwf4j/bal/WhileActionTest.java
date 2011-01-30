/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Map;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.helpers.NIterations;
import  org.jwaresoftware.mwf4j.helpers.Once;
import  org.jwaresoftware.mwf4j.helpers.OnceAfterStarted;
import  org.jwaresoftware.mwf4j.helpers.True;
import  org.jwaresoftware.mwf4j.starters.EchoAction;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.StampAction;

/**
 * Test suite for {@linkplain WhileAction} and associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class WhileActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected Variables iniDATAMAP()
    {
        Variables vars = super.iniDATAMAP();
        vars.put("counter",new LoopCounter());
        return vars;
    }

    private WhileAction newOUT(String id)
    {
        WhileAction out = id==null 
            ? new WhileAction() 
            : new WhileAction(id);
        return out;
    }
    
    private WhileAction newOUT()
    {
        return newOUT(null);
    }

    private WhileAction newNEVER(String id)
    {
        WhileAction out = newOUT(id);
        out.setMaxIterations(0);
        out.setHaltIfMax(true);
        out.setTest(True.INSTANCE);
        out.setBody(new EpicFail());
        return out;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testTestIsFalseByDefault_1_0_0()
    {
        WhileAction out = newOUT();
        out.setBody(new EpicFail());
        runTASK(out);
    }

    public void testDefaultCursorSetup_1_0_0()
    {
        WhileAction out = newOUT("L");
        out.setTest(new Once());
        out.setBody(new EchoAction("L",true));
        runTASK(out);
        assertTrue(wasPerformed("L[0]"),"cursor fallback");
    }

    public void testCursorClearedAfterFinished_1_0_0()
    {
        Map<String,Object> vars= iniDATAMAP();
        WhileAction out = newOUT("e");
        out.setTest(new Once());
        out.setBody(touch("e"));
        runTASK(out);
        assertTrue(wasPerformed("e",1),"ran once");
        assertFalse(vars.containsKey(BAL.getCursorKey(out.getId())),"old cursor");
    }

    public void testCustomObjectCursorOK_1_0_0()//ALSO checks is empty body by default!
    {
        Map<String,Object> vars= iniDATAMAP();
        LoopCounter counter = (LoopCounter)vars.get("counter");
        WhileAction out = newOUT();
        out.setTest(new Once());
        out.setCursor("counter.i");
        out.setCursorStoreType(StoreType.OBJECT);
        runTASK(out);
        assertEquals(counter.size(),1,"loopcount");
        assertFalse(vars.containsKey(BAL.getCursorKey(out.getId())),"old cursor");
    }

    public void testConditionIsStartedIfNeeded_1_0_0()
    {
        WhileAction out = newOUT();
        out.setTest(new OnceAfterStarted());
        out.setBody(touch("b"));
        runTASK(out);
        assertTrue(wasPerformed("b",1),"ran once");
    }

    public void testReuseBodyStatement_1_0_0()
    {
        StampAction body = new StampAction("b");
        WhileAction out = newOUT();
        out.setTest(new NIterations(3));
        out.setBody(body);
        out.setCopy(false);
        runTASK(out);
        String marker = body.getLastStamp();
        assertTrue(wasPerformed(marker,3),"body='"+marker+"' performed 3 times");
    }

    public void testHardLimit_1_0_0()
    {
        WhileAction out = newOUT("4x");
        out.setTest(new NIterations(5));
        out.setMaxIterations(4);
        out.setBody(touch("i"));
        runTASK(out);
        assertTrue(wasPerformed("i",4),"loopcount");
    }

    @Test(expectedExceptions= {TooManyIterationsException.class})
    public void testFailIfHaltMaxAndOverflow_1_0_0()
    {
        final int MAX=5;
        iniStatementCount();
        Map<String,Object> vars= iniDATAMAP();
        WhileAction out = newOUT("over"+MAX);
        out.setTest(new NIterations(MAX));
        out.setCursor("counter.i");
        out.setCursorStoreType(StoreType.OBJECT);
        out.setMaxIterations(MAX);
        out.setHaltIfMax(true);
        out.setBody(new EchoAction("e","counter.i",true,StoreType.OBJECT));
        try {
            runTASK(out);
            fail("Should not be able to iterate past "+MAX+" loops");
        } finally {
            assertEquals(getStatementCount(),MAX,"calls into loop");
            LoopCounter counter = (LoopCounter)vars.get("counter");
            assertEquals(counter.size(),MAX,"loopcount");
        }
    }

    @Test(expectedExceptions= {TooManyIterationsException.class})
    public void testAlwaysFailsScenario_1_0_0()
    {
        WhileAction out = newNEVER("never");
        runTASK(out);
        fail("Should NOT iterate loop AT ALL");
    }

    @Test(expectedExceptions= {TooManyIterationsException.class}, dependsOnMethods={"testAlwaysFailsScenario_1_0_0"})
    public void testHaltContinuationDoesUnwind_1_0_0()
    {
        Map<String,Object> vars= iniDATAMAP();
        WhileAction out = newNEVER("doom-edd");
        out.setUseHaltContinuation(true);
        try {
            runTASK(out);
            fail("Should not be able to iterate loop even ONCE");
        } finally {
            assertFalse(vars.containsKey(BAL.getCursorKey("doom-edd")),"old cursor");
        }
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailIfNullTest_1_0_0()
    {
        newOUT().setTest(null);
        fail("Should not be able to set test to NULL");
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailIfNullBody_1_0_0()
    {
        newOUT().setBody(null);
        fail("Should not be able to set body to NULL");
    }
}


/* end-of-WhileActionTest.java */
