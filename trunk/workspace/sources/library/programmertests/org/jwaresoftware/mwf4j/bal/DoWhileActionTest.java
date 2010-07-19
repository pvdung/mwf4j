/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Map;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.helpers.False;
import  org.jwaresoftware.mwf4j.starters.EchoAction;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.Once;
import  org.jwaresoftware.mwf4j.starters.OnceAfterStarted;

/**
 * Test suite for {@linkplain DoWhileAction} and associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class DoWhileActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected Map<String,Object> iniDATAMAP()
    {
        Map<String,Object> vars = super.iniDATAMAP();
        vars.put("counter",new LoopCounter());
        return vars;
    }

    private DoWhileAction newOUT(String id)
    {
        DoWhileAction out = id==null 
            ? new DoWhileAction() 
            : new DoWhileAction(id);
        return out;
    }
    
    private DoWhileAction newOUT()
    {
        return newOUT(null);
    }

    private DoWhileAction newNEVER(String id)
    {
        DoWhileAction out = newOUT(id);
        out.setMaxIterations(0);
        out.setHaltIfMax(true);
        out.setTest(False.INSTANCE);
        out.setBody(new EpicFail());
        return out;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testOnceByDefault_1_0_0()
    {
        DoWhileAction out = newOUT();
        out.setBody(touch("a"));
        runTASK(out);
        assertTrue(wasPerformed("a",1),"test statements run once");
    }

    public void testDefaultCursorSetup_1_0_0()
    {
        DoWhileAction out = newOUT("L");
        out.setTest(new Once());
        out.setBody(new EchoAction("L",true));
        runTASK(out);
        assertTrue(werePerformedInOrder("L[0]|L[1]"),"cursor fallback");
    }

    @Test(expectedExceptions= {TooManyIterationsException.class})
    public void testAlwaysFailsScenario_1_0_0()
    {
        DoWhileAction out = newNEVER("nooo");
        runTASK(out);
        fail("Should NOT iterate loop AT ALL");
    }

    public void testConditionIsStartedIfNeeded_1_0_0()
    {
        DoWhileAction out = newOUT();
        out.setTest(new OnceAfterStarted());
        out.setBody(touch("w"));
        runTASK(out);
        assertTrue(wasPerformed("w",2),"ran twice");
    }

}


/* end-of-DoWhileActionTest.java */
