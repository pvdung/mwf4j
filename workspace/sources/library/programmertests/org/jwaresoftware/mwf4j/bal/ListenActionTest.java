/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.Future;
import  java.util.concurrent.TimeoutException;

import  org.testng.annotations.BeforeMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.helpers.NoReturn;
import  org.jwaresoftware.mwf4j.starters.MWf4JWrapException;
import  org.jwaresoftware.mwf4j.starters.TestExtensionPoint;

/**
 * Test suite for {@linkplain ListenAction} and related classes. Will also test
 * many of the standard MWf4J harness implementations as side-effect of running
 * listen from own thread-of-execution in various scenarios. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","bal","advanced"})
public final class ListenActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    private LaunchAction newLauncher(String id)
    {
        LaunchAction out = id==null 
            ? new LaunchAction() 
            : new LaunchAction(id);
            
        MDC.Propagator fixtureClipboard = new MDC.CopyPropagator
            (TestFixture.STMT_NAMELIST, TestFixture.STMT_EXITED_NAMELIST);
        out.setMDCPropagator(fixtureClipboard);

        return out;
    }

    @BeforeMethod
    protected void setUp() throws Exception
    {
        super.setUp();
        iniPerformedList();
    }

    private static Future<?> getLinkOrFail(Variables theVars,String linkVar)
    {
        return LaunchActionTest.getLinkOrFail(theVars, linkVar);
    }

//  ---------------------------------------------------------------------------------------
//  Little helper classes for constructing various scenarios
//  ---------------------------------------------------------------------------------------

    private static final String ERROR_VAR="listenInbound.ERROR";

    static class NoActionBuilder implements ActionLookupMethod<NoReturn>
    {
        public Action create(NoReturn nul, Harness harness) {
            return Action.anonINSTANCE;
        }
    }

    static class NoReturnTilInterrupted implements Callable<NoReturn>
    {
        private final boolean wrapIruptedX;
        NoReturnTilInterrupted(boolean wrapX) {
            wrapIruptedX = wrapX;
        }
        public NoReturn call() throws Exception {
            CountDownLatch neverFlipped= new CountDownLatch(1);//BLOCK FOREVER
            try {
                neverFlipped.await();
            } catch(InterruptedException iruptedX) {//OUR CANCEL OF HARNESS...
                if (wrapIruptedX)
                    throw new MWf4JWrapException(iruptedX);//PROPAGATE THIS HIDDEN!
                throw iruptedX;
            }
            return NoReturn.INSTANCE;//NEVER REACHED...
        }
    }
 
    static class CancelHarness extends TestExtensionPoint 
    {
        CancelHarness(String linkVar) {
            super("cancel");
            myLinkVar = linkVar;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            Future<?> harnessLink = getLinkOrFail(harness.getVariables(),myLinkVar);
            System.out.println("Cancelling harness="+myLinkVar);
            harnessLink.cancel(true);
            return next();
        }
        private final String myLinkVar;
    }

    static class NilReturnThenTilInterrupted implements Callable<NoReturn>
    {
        int iterations=0;
        NilReturnThenTilInterrupted() {
        }
        public NoReturn call() throws Exception {
            iterations++;
            if (iterations>1) {
                CountDownLatch neverFlipped= new CountDownLatch(1);
                neverFlipped.await();//BLOCK FOREVER
            }
            return null;//Trigger TIMEOUT Exception
        }
    }

    static class TimeoutChecker extends TestExtensionPoint
    {
        TimeoutChecker() {
            super("catch-timeout");
        }
        protected ControlFlowStatement runInner(Harness harness) {
            assertTrue(MDC.latestException() instanceof TimeoutException,"is timeout exception");
            assertFalse(harness.getVariables().containsKey(ERROR_VAR),"errorKey setup");
            return next();
        }
    }

    static class RequestHandlerBuilder implements ActionLookupMethod<String>
    {
        public Action create(String rq, Harness harness) {
            final String T= "."+rq;
            Sequence a = new SequenceAction("handle"+T);
            a.add(touch("verify"+T))
             .add(touch("save"+T))
             .add(touch("ack"+T))
             .add(touch("starttimer"+T))
             .add(touch("notify"+T));
            return a;
        }
    }

    static class ForeverFeed implements Callable<String>
    {
        private final String[] rqArray=new String[]{"NEW","CXL","CXR"};
        private int cp;
        ForeverFeed() {
        }
        public String call() {
            int index = cp%rqArray.length;
            cp++;
            zzzzz(10);
            return rqArray[index];
        }
    }

    static class BoundedFeed implements Callable<String>
    {
        private String o1="NEW",o2="CXL";
        private boolean allowNull=false,iruptOnNull=false;

        BoundedFeed(boolean nullOk, boolean stopOnNull) {
            allowNull = nullOk;
            iruptOnNull = stopOnNull;
        }
        public String call() throws Exception {
            String rq= o1;
            if (rq==null) {
                rq= o2;
                o2= null;
            } else {
                o1 = null;
            }
            if (rq==null) {
                if (iruptOnNull)
                    throw new InterruptedException("DFD");
                if (!allowNull) 
                    Validate.stateNotNull(rq,"pending order-request");
            }
            zzzzz(10);
            return rq;
        }
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    /**
     * Verify a cancel via the Future returned by a launch works as expected.
     * Launch a slave harness with listen that blocks (nothing ever returned).
     * Then use the Future turned by the original launch to cancel the slave
     * harness main loop -- indirectly by triggering an InterruptedException.
     * The listen *should* exit normally as if stopped gracefully.
     **/
    private void verifyListenWithHarnessCancel(ListenAction<?> out)
    {
        Variables vars = iniDATAMAP();

        Sequence listenLoop = new SequenceAction("listenLoop");
        listenLoop.add(out);
        listenLoop.add(touch("listenShutdown"));//'normal' exit implies this is called...

        LaunchAction launchListenLoop = newLauncher("listenLoop");
        launchListenLoop.addAction(listenLoop);
        launchListenLoop.setLinkSaveRef(new Reference("listenLoop.harnessLink"));//for cancel

        Sequence main= new SequenceAction("main");
        main.add(launchListenLoop);//launch listen slave harness
        main.add(sleep1("wait1s#1"));//wait for listen to start and get nice-n-stuck
        main.add(new CancelHarness("listenLoop.harnessLink"));//cancel the slave harness
        main.add(sleep1("wait1s#2"));//give listenLoop some time to work thru

        runTASK(main);
        assertTrue(wasPerformed("listenShutdown",1),"clean listen shutdown");
        assertNull(vars.get(ERROR_VAR),"error var set when is clean stop");
    }

    public void testStopListenThruHarnessCancel_1_0_0()
    {
        ListenAction<NoReturn> out = new ListenAction<NoReturn>("readUntilCancel");
        out.setListener(new NoReturnTilInterrupted(false));//ASIS iruptedX
        out.setLookupService(new NoActionBuilder());
        out.setErrorKey(ERROR_VAR);
        out.setBreakSupport(never());
        
        verifyListenWithHarnessCancel(out);
    }

    public void testStopListenThruHarnessCancelAlt_1_0_0()
    {
        ListenAction<NoReturn> out = new ListenAction<NoReturn>("readUntilCancel");
        out.setListener(new NoReturnTilInterrupted(true));//WRAP iruptedX
        out.setLookupService(new NoActionBuilder());
        out.setErrorKey(ERROR_VAR);
        out.setBreakSupport(never());

        verifyListenWithHarnessCancel(out);
    }

    public void testKeepListeningAfterNoneFatalException_1_0_0()
    {
        ListenAction<NoReturn> out = new ListenAction<NoReturn>("readUntilCancel");
        out.setListener(new NilReturnThenTilInterrupted());//TIMEOUT+ASIS iruptedX
        out.setLookupService(new NoActionBuilder());
        out.setErrorKey(ERROR_VAR);
        out.setBreakSupport(new TimeoutChecker());
        
        out.setHaltIfError(false);
        out.setQuiet(false);

        verifyListenWithHarnessCancel(out);
        assertTrue(wasPerformed("catch-timeout"),"catch-timeout performed");
    }

    @Test(dependsOnMethods={"testStopListenThruHarnessCancel_1_0_0"})
    public void testHappyPathForMainAsProcessor_1_0_0()
    {
        ListenAction<String> out = new ListenAction<String>("INBOX");
        out.setListener(new ForeverFeed());
        out.setLookupService(new RequestHandlerBuilder());

        LaunchAction launch= newLauncher("listenLoop");
        launch.addAction(out);
        launch.setLinkSaveRef("harnessLink");
        
        Sequence main = new SequenceAction("main")
                            .add(launch)
                            .add(sleep(50L))/*Let some processing occur*/
                            .add(new CancelHarness("harnessLink"))
                            .add(sleep(200L));
        runTASK(main);
        assertTrue(werePerformed("save.NEW|notify.NEW|verify.CXL|ack.CXL"),"some orders processed");
    }

    static class HappyPathCheck extends TestExtensionPoint {
        private final Variables theVars;
        HappyPathCheck(Variables vars) {
            super("checkHappyPath");
            theVars = vars;
        }
        private void verifyEnded(String thrName) {
            Future<?> f = getLinkOrFail(theVars,thrName);
            assertTrue(f.isDone(),thrName+" done");
            assertFalse(f.isCancelled(),thrName+" cancelled");
        }
        private void verifyTimeout() {
            Object o = theVars.get(ERROR_VAR);
            assertTrue(o instanceof TimeoutException,"listen stopped due to timeout");
            TimeoutException x = (TimeoutException)o;
            x.printStackTrace(System.err);
        }
        protected ControlFlowStatement runInner(Harness harness) {
            verifyEnded("INBOX.Handle");//NB: both *die* via timeout errors...
            verifyEnded("OMS.Handle");
            verifyTimeout();
            return next();
        }
    }

    @Test(dependsOnMethods={"testStopListenThruHarnessCancel_1_0_0"})
    public void testHappyPathForOtherAsProcessor_1_0_0()
    {
        Variables vars = iniDATAMAP();

        ListenAction<String> out = new ListenAction<String>("INBOX");
        out.setListener(new BoundedFeed(true,false));//2 loops then Timeout
        out.setLookupService(new RequestHandlerBuilder());
        out.setBreakSupport(new TimeoutChecker());
        out.setErrorKey(ERROR_VAR);//Expected to be set by Timeout

        LaunchAction launchInbox = newLauncher("launchINBOX");
        launchInbox.addAction(out);
        launchInbox.setLinkSaveRef("INBOX.Handle");

        LaunchAction launchOMS = newLauncher("launchProcessorChain");
        launchOMS.useForeverHarnessType(true);//Wait for continuations from INBOX
        launchOMS.addAction(launchInbox);//Spawn inbox from here (not main)
        launchOMS.setLinkSaveRef(new Reference("OMS.Handle"));

        Sequence main = new SequenceAction("main")
                            .add(launchOMS)
                            .add(sleep1("wait4Timeouts"))
                            .add(new HappyPathCheck(vars));
        runTASK(newHARNESS("MyOMS",main));
        assertTrue(werePerformed("notify.NEW|notify.CXL|catch-timeout|checkHappyPath"));
    }
}


/* end-of-ListenActionTest.java */
