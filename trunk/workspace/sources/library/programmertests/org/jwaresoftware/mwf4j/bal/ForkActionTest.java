/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.Map;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.TimeoutException;

import  org.testng.annotations.BeforeMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;
import  org.jwaresoftware.mwf4j.starters.TestExtensionPoint;
import  org.jwaresoftware.mwf4j.starters.TestStatement;

/**
 * Test suite for {@linkplain ForkAction} and its related classes.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","bal","advanced"})
public final class ForkActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    private ForkAction newOUT(String id)
    {
        ForkAction out = id==null 
            ? new ForkAction() 
            : new ForkAction(id);
            
        MDC.Propagator fixtureClipboard = new MDC.CopyPropagator
            (TestFixture.STMT_NAMELIST, TestFixture.STMT_EXITED_NAMELIST);
        out.setMDCPropagtor(fixtureClipboard);

        return out;
    }

    private ForkAction newOUT()
    {
        return newOUT(null);
    }

    @BeforeMethod
    protected void setUp() throws Exception
    {
        super.setUp();
        iniPerformedList();
    }

    private Collection<Action> branches(Action... branches)
    {
        Collection<Action> set = LocalSystem.newList();
        for (Action branch:branches) {
            set.add(branch);
        }
        return set;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testForkAndWaitForAllIsDefault_1_0_0()
    {
        ForkAction out = newOUT();
        out.setBranches(branches(touch("T1"),sleep("T2",500L),touch("T3")));
        runTASK(out);
        assertTrue(werePerformedAndExited("T1|T2|T3"),"all branches done before fork returned");
    }

    public void testForkAndWaitForOne_1_0_0()
    {
        ForkAction out = newOUT();
        out.setJoinType(JoinType.ANY);
        out.setBranches(branches(touch("T1"),sleep1("S1"),sleep1("S2")));
        runTASK(out);
        assertFalse(werePerformedAndExited("S2|S1"),"sleep branches alive after fork returned");
        assertTrue(werePerformedAndExited("T1"),"no-sleep branch done before fork returned");
        zzzzz(_1SEC*2);
        assertTrue(werePerformedAndExited("S1|S2"),"branches done after some time");
    }

    final class Checkpoint extends ActionSkeleton
    {
        Checkpoint() { 
            super("checkpoint"); 
        }
        public ControlFlowStatement buildStatement(ControlFlowStatement next, Fixture environ) {
            assertTrue(werePerformedAndExited("W1|W2|W3"),"all branches done before join-body called");
            TestStatement statement= new TestStatement(this,next);
            return statement;
        }
        public void configureStatement(ControlFlowStatement statement, Fixture environ) {
            assertTrue(statement instanceof TestStatement);
        }
    }

    public void testJoinBodyRunAfterJoinPoint_1_0_0()
    {
        ForkAction out = newOUT("merge");
        out.setJoinAction(new Checkpoint());
        out.setBranches(branches(touch("W1")));
        out.addBranch(sleep("W2",500L));
        out.addBranch(touch("W3"));
        runTASK(out);
        assertTrue(wasPerformed("checkpoint"),"checkpoint join body done");
    }

    public void testUseAsFireAndForget_1_0_0()
    {
        ForkAction out = newOUT("fire-n-forgit");
        out.setJoinType(JoinType.NONE);
        out.setJoinAction(touch("JB"));
        out.setBranches(branches(sleep1("t1"),sleep1("t2")));
        runTASK(out);
        assertFalse(werePerformedAndExited("t1|t2"),"branches done on exit to fork");
        assertTrue(wasPerformed("JB"),"body done on exit to fork");
        zzzzz(_1SEC*2);
        assertTrue(werePerformedAndExited("t1|t2"),"branches done after some time");
    }

    public void testWillRetryWaitPass_1_0_0()
    {
        ForkAction out = newOUT();
        out.setJoinBreakSupport(new RetryDef(2,_1SEC),touch("break!"));
        out.setBranches(branches(sleep("W1",1500L),touch("T1"),sleepN("W2",2)));
        out.setJoinAction(touch("JB"));
        runTASK(out);
        assertTrue(werePerformed("W1|T1|W2|break!|JB"),"all done including break");
    }

    @Test(expectedExceptions={RunFailedException.class})
    public void testWillRetryWaitFail_1_0_0()
    {
        ForkAction out = newOUT(TestFixture.currentTestName());
        out.setJoinBreakSupport(new RetryDef(2,_1SEC),touch("break!"));
        out.setBranches(branches(sleep("W1",1500L),touch("T1"),sleepN("W2",3)));
        out.setJoinAction(touch("JB"));
        out.setUseHaltContinuation(false);
        try {
            runTASK(out);
            fail("Should not be able to get past run after 2 timeouts");
        } catch(RunFailedException Xpected) {
            System.err.println(Xpected);
            assertEquals(Xpected.getCause().getClass(),TimeoutException.class,"error=timeout");
            assertTrue(werePerformed("W1|T1|break!"),"all done including break");
            assertFalse(wasPerformed("JB"),"join action done");
            throw Xpected;
        }
    }

    final static class ErrorChecker extends TestExtensionPoint {
        ErrorChecker() {
            super("errorcheck");
        }
        protected ControlFlowStatement runInner(Harness harness) {
            Map<String,Object> vars = harness.getVariables();
            assertTrue(vars.get("join.broken.error") instanceof Exception,"errorKey updated before join body called");
            return next();
        }
    }

    public void testWillContinueIfBreakAndNotHaltIfError_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        ForkAction out = newOUT(TestFixture.currentTestName());
        out.setJoinBreakSupport(new RetryDef(1,_1SEC),touch("break!"));
        out.setErrorKey("join.broken.error");
        out.setHaltIfError(false);
        out.setJoinAction(new ErrorChecker());
        out.setBranches(branches(touch("T1"),sleepN("W1",3)));
        runTASK(out);
        assertTrue(wasPerformed("break!"),"broke join-point at least once");
        assertTrue(vars.get("join.broken.error") instanceof Exception,"errorKey updated with break error");
        assertTrue(wasPerformed("errorcheck"),"join-action done even if broken");
        assertTrue(wasPerformed("T1"),"non-blocking action completed");
        assertTrue(werePerformedInRelativeOrder("break!|errorcheck"),"Break occured before join-body");
    }

    final static class JoinInterrupter extends TestExtensionPoint {
        final Thread joinThread;
        JoinInterrupter(Thread thread) {
            super("interrupt");
            joinThread= thread;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            joinThread.interrupt();
            return next();
        }
    }

    /**
     * Verifies that an interruption on a forked join-point is handled and if 
     * flagged-for-error will also notify the parent harness (error adjustment).
     **/
    @Test(expectedExceptions={RunFailedException.class})
    public void testInterruptedJoinThreadNotifiesParentHarness_1_0_0()
    {
        Sequence mainflow = new SequenceAction("main");
        Harness mainHarness = newHARNESS("main",mainflow);

        JoinStatement join = new JoinStatement(new EndStatement());
        join.setBarrier(new CountDownLatch(1));//NB: *never* tripped => wait forever w/o interrupt!
        join.setUseHaltContinuation(false);
        join.verifyReady();

        Harness joinHarness = newHARNESS(mainHarness,join);
        Thread joinThread = new Thread(joinHarness,"joinThread");//retain handle to interrupt
        joinThread.start();

        //Make sure we given some time for interrupt to percolate across threads...
        mainflow.add(new JoinInterrupter(joinThread)).add(sleep1("zzz1")).add(sleep1("zzz2")).add(sleep1("zzz3")).add(never());
        try {
            runTASK(mainHarness);
            fail("WHAT DA HECK IS GOING ON HERE?! Should not be able to get past main run");
        } catch(RunFailedException Xpected) {
            System.err.println(Xpected);
            assertEquals(Xpected.getCause().getClass(),InterruptedException.class,"error=irupted");
            throw Xpected;
        }
    }
}


/* end-of-ForkActionTest.java */
