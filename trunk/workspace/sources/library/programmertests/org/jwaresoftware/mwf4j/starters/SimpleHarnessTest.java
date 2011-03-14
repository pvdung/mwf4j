/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.ServiceProviderException;
import  org.jwaresoftware.gestalt.system.LocalSystem;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Unwindable;

/**
 * Test suite to verify balk if bad services installed for expected
 * MWf4J providers. Frankly we depend on most of the overall BAL test suite
 * to cover all the nooks-n-crannies of the MWf4J harness implementations.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline"})
public final class SimpleHarnessTest extends ExecutableTestSkeleton
{
    public static final class InstallThenBarfStatement extends BarfStatement 
    {
        class Unwinder implements Unwindable {
            public void unwind(Harness h) {
                MDC.put("unwoundFor."+getOwner().getId(),Boolean.TRUE);
            }
        }
        public InstallThenBarfStatement(Action owner, ControlFlowStatement next) {
            super(owner,next);
            setMessage("TEEHEEHEEHEE!");
        }
        protected ControlFlowStatement runInner(Harness harness) {
            harness.addUnwind(new Unwinder());
            return super.runInner(harness);
        }
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testBaseline()
    {
        Harness h = newHARNESS(new UnknownAction("foo",TestStatement.class));
        System.out.println("Name: "+h.getName());
        assertNotNull(h.getName());
        h.run();
        assertTrue(TestFixture.wasPerformed("foo"));
        assertFalse(h.isAborted(),"aborted");
        assertFalse(h.isRunning(),"running");
    }

    public void testSimplePostedContinuation_1_0_0()
    {
        newHARNESS(new UnknownAction("bar",FinishLaterStatement.class)).run();
        assertTrue(TestFixture.wasPerformed("bar"));
    }

    @Test (expectedExceptions= {ServiceProviderException.class})
    public void testFailsIfActionFails_1_0_0()
    {
        Action work = new UnknownAction("die",BarfStatement.class);
        try {
            newHARNESS(work).run();
            fail("Should not exit normally from barfing action!");
        } catch(ServiceProviderException Xpected) {
            System.err.println("Caught barfage: "+Xpected);
            assertEquals(Xpected.getMessage(),BarfStatement.DEFAULT_MESSAGE,"thrown");
            throw Xpected;
        }
    }
    
    public void testUncaughtErrorHandlerRun_1_0_0()
    {
        PlainTask task = new PlainTask("Fred");
        task.setDefinition(new UnknownAction("spew",BarfStatement.class));
        assertFalse(MDC.has("errorHandler.Fred"));
        try {
            new PlainHarness(task,SYSTEM).run();
            fail("Should not exit normally from barfing action!");
        } catch(ServiceProviderException Xpected) {
            System.err.println("Caught barfage: "+Xpected);
        }
        assertTrue(MDC.has("errorHandler.Fred"),"stored feedback from handler");
    }

    public void testIgnoreBadVariablesInstance_1_0_0()
    {
        SYSTEM.setServiceInstance(MWf4J.ServiceIds.VARIABLES,LocalSystem.newMap(),null);
        runTASK(new EchoAction("peekaboo"));
        assertTrue(TestFixture.wasPerformed("peekaboo"),"action performed anyway");
    }

    @Test (expectedExceptions= {ServiceProviderException.class})
    public void testUnwindUnwindables_1_0_0()
    {
        Action work = new UnknownAction("Doom",InstallThenBarfStatement.class);
        try {
            newHARNESS(work).run();
            fail("Should not exit normally from barfing action!");
        } finally {
            assertTrue(MDC.has("unwoundFor.Doom"),"triggered unwinding");
        }
    }

    @Test (dependsOnMethods={"testFailsIfActionFails_1_0_0"})
    public void testAbortFlagSetIfError_1_0_0()
    {
        Harness h = newHARNESS("barfola",new UnknownAction("spew",BarfStatement.class));
        assertFalse(h.isAborted(),"aborted(enter)");
        try {
            h.run();
            fail("Should not exit normally from barfing action!");
        } catch(ServiceProviderException Xpected) {
            assertTrue(h.isAborted(),"aborted(leave)");
        }
    }

//  ---------------------------------------------------------------------------------------
//  Adjustment test case setup (lots of classes to fake an independent adjustment)
//  ---------------------------------------------------------------------------------------

    private static void await(CountDownLatch latch) {
        try { latch.await(); } catch(InterruptedException iruptedX) { }
    }

    private static abstract class ParticipantAdjustment extends ExtensionPoint implements Adjustment {
        ParticipantAdjustment(String id) {
            super(id);
        }
        public void doEnter(Harness harness) {
            super.doEnter(harness);
            TestFixture.addPerformed(getId());
        }
    }

    private static class Cancellation extends ParticipantAdjustment {
        Cancellation() {
            super("cancel");
        }
        public boolean isTerminal() {
            return true;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            return new ActivityEndStatement();
        }
    }

    private static class Passthrough extends ParticipantAdjustment {
        Passthrough(String id) {
            super(id);
        }
        public boolean isTerminal() {
            return false;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            return next();
        }
    }

    private static class ParticipantStatement extends TestStatement {
        ParticipantStatement(String id) {
            super(Action.anonINSTANCE,null);
            myId = id;
        }
        ParticipantStatement(String id, ControlFlowStatement next) {
            super(Action.anonINSTANCE,next);
            myId = id;
        }
    }

    private static class TriggerCancel extends ParticipantStatement {
        TriggerCancel(CountDownLatch _cancelFlag, ControlFlowStatement next) {
            super("trigger",next);
            this.cancelFlag = _cancelFlag;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            cancelFlag.countDown();
            return next();
        }
        private final CountDownLatch cancelFlag;
    }

    private static class WaitForCancel extends ParticipantStatement {
        WaitForCancel(CountDownLatch _cancelledFlag, ControlFlowStatement next) {
            super("wait",next);
            this.cancelledFlag = _cancelledFlag;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            await(cancelledFlag);
            return next();
        }
        private final CountDownLatch cancelledFlag;
    }

    private static class Adjust extends ParticipantStatement {
        Adjust(CountDownLatch _cancelFlag, CountDownLatch _cancelledFlag) {
            super("start");
            initNextStatement(new TriggerCancel(_cancelFlag,
                new WaitForCancel(_cancelledFlag,
                    new ParticipantStatement("retch",
                        new FailStatement("Should NEVER be get past cancellation")))));
        }
        protected ControlFlowStatement runInner(Harness harness) {
            harness.applyAdjustment(new Passthrough("transient"));
            return next();
        }
    }
    
    static class AdjustAction extends ActionSkeleton {
        AdjustAction() {
            super("adjust");
            cancelFlag = newLatch();
            cancelledFlag = newLatch();
        }
        private CountDownLatch newLatch() {
            return new CountDownLatch(1);
        }
        public ControlFlowStatement buildStatement(ControlFlowStatement next, Fixture environ) {
            return new Adjust(cancelFlag,cancelledFlag);
        }
        public void configureStatement(ControlFlowStatement statement, Fixture environ) {
            Validate.isTrue(statement instanceof Adjust,"kindof adjust statement");
        }
        final CountDownLatch cancelFlag, cancelledFlag;
    }

    static class Canceller implements Runnable {
        Canceller(AdjustAction seq, Harness h) {
            this.startFlag = seq.cancelFlag;
            this.leaveFlag = seq.cancelledFlag;
            theHarness = h;
        }
        public void run() {
            await(startFlag);
            theHarness.applyAdjustment(new Cancellation());
            leaveFlag.countDown();
        }
        private final CountDownLatch startFlag;
        private final CountDownLatch leaveFlag;
        private final Harness theHarness;
    }

    public void testAdjustment_1_0_0()
    {
        AdjustAction seq = new AdjustAction();
        Harness out = newHARNESS(seq);
        new Thread(new Canceller(seq,out),"cancel-thread").start();
        try { Thread.sleep(TimeUnit.SECONDS.toMillis(2L)); } 
        catch(InterruptedException iruptedX) { }
        runTASK(out);
    }
}


/* end-of-SimpleHarnessTest.java */
