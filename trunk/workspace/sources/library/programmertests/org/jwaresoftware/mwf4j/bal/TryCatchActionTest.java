/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.lang.reflect.InvocationTargetException;
import  java.util.Deque;
import  java.util.List;
import  java.util.Map;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.ServiceProviderException;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.assign.Giveback;
import  org.jwaresoftware.mwf4j.assign.GivebackVar;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.starters.ExtensionPoint;
import org.jwaresoftware.mwf4j.starters.InstallCheckUnwind;
import  org.jwaresoftware.mwf4j.starters.MWf4JWrapException;

/**
 * Test suite for {@linkplain TryCatchAction} and its related classes.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class TryCatchActionTest extends ActionTestSkeleton
{
    static class AException extends IllegalStateException {
        AException(String message) { super(message); }
    }
    static class AAException extends AException {
        AAException(String message) { super(message); }
    }
    static class ABException extends AException {
        ABException(String message) { super(message); }
    }
    static class AAAException extends AAException {
        AAAException(String message) { super(message); }
    }
    static class BException extends Exception { //NOT runtime
        BException(String message) { super(message); }
    }
    
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected Variables iniDATAMAP()
    {
        Variables vars = super.iniDATAMAP();
        vars.put("mydata",LocalSystem.newMap());
        return vars;
    }

    protected TryCatchAction newOUT(String id)
    {
        TryCatchAction out = id==null 
            ? new TryCatchAction() 
            : new TryCatchAction(id);
        return out;
    }
    
    protected TryCatchAction newOUT()
    {
        return newOUT(null);
    }

    private Action checkunwind(String id)
    {
        return new InstallCheckUnwind(id);
    }

    private TryCatchAction trycatch(String id, Action body)
    {
        TryCatchAction out = newOUT(id);
        out.setBody(body);
        return out;
    }

    private TryCatchAction trycatch(String id, Action body, Action always)
    {
        TryCatchAction out = newOUT(id);
        out.setBody(body);
        out.setAlways(always);
        return out;
    }

    private TryCatchAction trycatch(String id, Action body, Action always, Class<? extends Exception> xType, Action error)
    {
        TryCatchAction out = newOUT(id);
        out.setBody(body);
        out.setAlways(always);
        out.addIfError(xType,error);
        return out;
    }

    private TryCatchAction trycatch(String id, Action body, Class<? extends Exception> xType, Action error)
    {
        TryCatchAction out = newOUT(id);
        out.setBody(body);
        out.addIfError(xType,error);
        return out;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testUnconfiguredIsEmptyStatement_1_0_0()
    {
        TryCatchAction out = newOUT();
        newHARNESS(out).run();
        //just making sure we get past 'run' with no issues
    }

    public void testJustBody_1_0_0()
    {
        runTASK(trycatch("a",touch("a.1")));
        assertTrue(wasPerformed("a.1",1),"body called");
    }

    public void testJustAlwaysIfNoErrors_1_0_0()
    {
        TryCatchAction out = newOUT("always");
        out.setAlways(touch("always"));
        newHARNESS(out).run();
        assertTrue(wasPerformed("always",1),"always called");
    }

    public void testBodyAndAlwaysIfNoErrors_1_0_0()
    {
        runTASK(trycatch("it",touch("body"),touch("always")));
        assertTrue(werePerformedInOrder("body|always"),"called in order [body,always]");
    }

    @Test(expectedExceptions= {RunFailedException.class})
    public void testFailIfErrorAndNoHandler_1_0_0()
    {
        TryCatchAction out = trycatch("barf",error("FAIL"),touch("always"));
        try {
            runTASK(out);
            fail("Should not be able to exit from barfing block");
        } catch(RunFailedException Xpected) {
            System.out.println("testFailIfErrorAndNoHandler Error:"+Xpected);
            assertTrue(Xpected.getCause() instanceof ServiceProviderException,"Xpected source error");
            assertTrue(wasPerformed("always"),"called always even with error");
            throw Xpected;
        }
    }

    public void testNestedTryAlwaysCombos_1_0_0()
    {
        TryCatchAction out = trycatch("o",
             new SequenceAction("o.body")
                   .add(touch("o.body[a]"))
                   .add(trycatch("o.body[b]",
                           touch("o.body[b].body"),
                           trycatch("o.body[b].always",
                                   touch("o.body[b].always.body"),
                                   touch("o.body[b].always.always"))))
                   .add(touch("o.body[c]")),
             touch("o.always"));
        runTASK(out);
        assertTrue(werePerformedInOrder("o.body[a]|o.body[b].body|o.body[b].always.body|"+
                                        "o.body[b].always.always|o.body[c]|o.always"),
                  "nested-try-always");
    }

    public void tesIfErrorHandlerBaseline_1_0_0()
    {
        TryCatchAction out = trycatch("o",error("DIE",new RuntimeException("Phffht")), touch("cleanup"));
        try {
            runTASK(out);
            fail("Should not get past generated exception");
        } catch(RunFailedException Xpected) {
            System.out.println(Xpected);
            assertTrue(wasPerformed("cleanup"),"cleanup handler called");
            assertEquals(Xpected.getCause().getMessage(),"Phffht","propagated exception");
        }
    }

    public void testErrorIgnoredIfHaltIsFalse_1_0_0()
    {
        Sequence body = new SequenceAction("y").add(touch("y.1")).add(error("y.2")).add(never());
        TryCatchAction out = trycatch("y",body,touch("cleanup"));
        out.setHaltIfError(false);
        out.setQuiet(false);
        runTASK(out);
        assertTrue(werePerformedInOrder("y.1|cleanup"),"ordering");
    }

    @Test(expectedExceptions={InternalError.class}, dependsOnMethods={"testErrorIgnoredIfHaltIsFalse_1_0_0"})
    public void testDontCatchKindOfErrorThrowables_1_0_0()
    {
        TryCatchAction out= trycatch("DIE",never());
        out.setHaltIfError(false);
        runTASK(out);
        fail("Should not be able to get past kindof Error");
    }

    @Test(expectedExceptions= {RunFailedException.class})
    public void testCallOrderForTryCatchAlways_1_0_0()
    {
        Sequence body = new SequenceAction("body").add(touch("work")).add(error("DIE")).add(never());
        TryCatchAction out= trycatch("c",body,touch("always"),RuntimeException.class,touch("onerror"));
        try {
            runTASK(out);
            fail("Should not get past generated exception");
        } finally {
            assertTrue(werePerformedInOrder("work|onerror|always"),"ordering is work|onerror|always");
        }
    }

    public void testSelectsMostSpecificIfErrorHandlerA_1_0_0()
    {
        TryCatchAction out = newOUT("a");
        out.setBody(error("DIE", new UnsupportedOperationException("Bwahahaha")));
        out.addIfError(RuntimeException.class, never());
        out.addIfError(UnsupportedOperationException.class, touch("uox-cleanup"));
        out.addIfError(IllegalArgumentException.class, never());
        try {
            runTASK(out);
            fail("Should not get past generated exception");
        } catch(RunFailedException Xpected) {
            System.out.println(Xpected);
            assertTrue(wasPerformed("uox-cleanup"),"uox cleanup handler called");
            assertEquals(Xpected.getCause().getMessage(),"Bwahahaha","propagated exception");
        }
    }

    public void testUseMostRecentHandlerDefinedForException_1_0_0()
    {
        TryCatchAction out = newOUT("b");
        out.setUnmask(true);
        out.setBody(error("DIE", new InterruptedException("Aaiiieeee!")));
        out.setUseHaltContinuation(true);
        out.addIfError(InterruptedException.class, never());
        out.addIfError(InterruptedException.class, touch("saved!"));
        try {
            runTASK(out);
            fail("Should not get past generated exception");
        } catch(RunFailedException Xpected) {
            System.out.println(Xpected);
            assertTrue(wasPerformed("saved!"),"'saved!' handler called");
            assertEquals(Xpected.getCause().getMessage(),"Aaiiieeee!","propagated exception");
        }
    }

    @Test(dependsOnMethods={"testSelectsMostSpecificIfErrorHandlerA_1_0_0","testUseMostRecentHandlerDefinedForException_1_0_0"})
    public void testSelectsMostSpecificIfErrorHandlerB_1_0_0()
    {
        TryCatchAction out = newOUT("a");
        out.setBody(error("DIE", new AAAException("Tralalala")));
        out.addIfError(ABException.class, never());
        out.addIfError(RuntimeException.class, never());
        out.addIfError(AAException.class, touch("aa-cleanup"));
        out.addIfError(AException.class, never());
        try {
            runTASK(out);
            fail("Should not get past generated exception");
        } catch(RunFailedException Xpected) {
            System.out.println(Xpected);
            assertTrue(wasPerformed("aa-cleanup"),"aa cleanup handler called");
            assertEquals(Xpected.getCause().getMessage(),"Tralalala","propagated exception");
        }
        clrPerformed();
        out.setBody(error("DIE",new IllegalStateException("GlurbGlurb")));
        out.addIfError(RuntimeException.class,touch("def-cleanup"));
        out.addIfError(AAException.class, never());
        try {
            runTASK(out);
            fail("Should not get past generated exception");
        } catch(RunFailedException Xpected) {
            System.out.println(Xpected);
            assertTrue(wasPerformed("def-cleanup"),"def cleanup handler called");
            assertEquals(Xpected.getCause().getMessage(),"GlurbGlurb","propagated exception");
        }
    }

    public void testHandleErrorContinuation_1_0_0()
    {
        Sequence handler = new SequenceAction("e").add(touch("e.1")).add(touch("e.2"));//NB:not done in one call!
        TryCatchAction out = trycatch("EC",error("X"),MWf4JException.class,handler);
        out.setHaltIfError(false);
        out.setQuiet(true);
        runTASK(out);
        assertTrue(werePerformedInOrder("e.1|e.2"),"handler ordering is e.1|e.2");
    }

    public void testHandleAlwaysContinuation_1_0_0()
    {
        Sequence always = new SequenceAction("a").add(touch("a.1")).add(touch("a.2"));//NB:not done in one call!
        TryCatchAction out = trycatch("AC",error("X"),always);
        out.setHaltIfError(false);
        out.setQuiet(true);
        runTASK(out);
        assertTrue(werePerformedInOrder("a.1|a.2"),"handler ordering is a.1|a.2");
    }

    public void testHandleErrorAndAlwaysContinuation_1_0_0()
    {
        Sequence handler = new SequenceAction("e").add(touch("e.1")).add(touch("e.2"));
        Sequence always = new SequenceAction("a").add(touch("a.1")).add(touch("a.2"));
        TryCatchAction out = trycatch("BC",error("X"),always,MWf4JException.class,handler);
        out.setHaltIfError(false);
        out.setQuiet(true);
        runTASK(out);
        assertTrue(werePerformedInOrder("e.1|e.2|a.1|a.2"),"ordering is e.1|e.2|a.1|a.2");
    }

    @Test(expectedExceptions= {RunFailedException.class})
    public void testFailureInHandlerAbortsButCallsAlwaysStill_1_0_0()
    {
        Sequence handler = new SequenceAction("e").add(touch("e.1")).add(error("GOTCHA!")).add(never());
        TryCatchAction out = trycatch("EC",error("X"),touch("always"),MWf4JException.class,handler);
        out.setHaltIfError(false);
        try {
            runTASK(out);
            fail("Should not get past aborted handler's exception");
        } finally {
            assertTrue(wasPerformed("e.1"),"handler e.1 ran");
            assertTrue(wasPerformed("always",1),"always STILL called");
        }
    }

    @Test(expectedExceptions= {RunFailedException.class})
    public void testFailureInAlwaysAborts_1_0_0()
    {
        Sequence always = new SequenceAction("a").add(touch("always.1")).add(error("GOTCHA!",new AException("AAH"))).add(never());
        TryCatchAction out = trycatch("AC",error("X", new BException("PHFFHT")),always);
        out.setHaltIfError(false);
        out.setUnmask(true);
        try {
            runTASK(out);
            fail("Should not get past aborted always's exception");
        } catch(RunFailedException Xpected) {
            Deque<Throwable> exceptions = Xpected.copyOfCauses();
            assertEquals(exceptions.size(),2,"Num.causes");
            assertTrue(BException.class.isInstance(exceptions.pop()),"1st exception from body");
            assertTrue(AException.class.isInstance(exceptions.pop()),"2nd excetioon from always");
            assertTrue(wasPerformed("always.1"),"first part of always ran");
            throw Xpected;
        }
    }

    public void testUnmaskWrappedExceptions_1_0_0()
    {
        final String marker = "_x"+LocalSystem.currentTimeNanos();
        Throwable real = new BException(marker);
        TryCatchAction out = trycatch("b",error("catch", new InvocationTargetException(real)),
                BException.class, touch("cleanup"));
        out.setUnmask(true);
        try {
            runTASK(out);
            fail("Should not be able to get past generated error");
        }  catch(RunFailedException Xpected) {
            System.out.println(Xpected);
            assertTrue(wasPerformed("cleanup",1),"cleanup called");
            assertEquals(Xpected.getCause().getMessage(),marker,"propagated unmasked exception");
        }
    }

    static class IfErrorHandler extends ExtensionPoint {
        IfErrorHandler(String id, Giveback<Exception> getmethod) {
            super(id);
            myGiveback = getmethod;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            Exception lastThrown = MDC.latestException();
            assertNotNull(lastThrown,"latest exception");
            assertSame(getException(),lastThrown,"captured exception");
            return next();
        }
        public void doEnter(Harness harness) {
            harness.getVariables().put("errorHandler."+getId(), Boolean.TRUE);
            super.doEnter(harness);
            TestFixture.incStatementCount();
            TestFixture.addPerformed(getId());
        }
        private Object getException() {
            try {
                return myGiveback.call();
            } catch(Exception ex) {
                throw new MWf4JWrapException(ex);
            }
        }
        private final Giveback<Exception> myGiveback;
    }

    public void testCaptureOfErrorToHarnessAndFixture_1_0_0()
    {
        Map<String,Object> vars= iniDATAMAP();
        TryCatchAction out = newOUT("t");
        out.setHaltIfError(false);
        out.setQuiet(false);
        out.setBody(error("BLEECH", new BException("BLEECH")));
        out.setErrorKey("mydata.lastError");
        out.setErrorStoreType(StoreType.OBJECT);
        Giveback<Exception> getError = GivebackVar.fromEval("mydata.lastError");
        out.addIfError(Exception.class, new IfErrorHandler("cleanup",getError));

        runTASK(out);

        assertTrue(wasPerformed("cleanup",1),"cleanup called");
        Map<?,?> mydata = (Map<?,?>)vars.get("mydata");
        assertTrue(BException.class.isInstance(mydata.get("lastError")),"saved and instanceof BException");
        assertNull(MDC.latestException(),"MDC.latestException (on exit)");
    }

    @Test(dependsOnMethods={"testFailureInHandlerAbortsButCallsAlwaysStill_1_0_0","testFailureInAlwaysAborts_1_0_0"},expectedExceptions= {RunFailedException.class})
    public void testUnwind_1_0_0()
    {
        Sequence body = new SequenceAction("m").add(touch("m.1")).add(checkunwind("m.2")).add(error("m.e"));
        Sequence iferror = new SequenceAction("e").add(touch("e.1")).add(checkunwind("e.2")).add(error("IFERROR!")).add(never());
        Sequence always = new SequenceAction("a").add(touch("a.1")).add(checkunwind("a.2")).add(error("ALWAYS!")).add(never());
        TryCatchAction out = newOUT("t");
        out.setHaltIfError(false);
        out.setBody(body);
        out.setAlways(always);
        out.addIfError(RuntimeException.class,iferror);
        try {
            runTASK(out);
        } catch(RunFailedException Xpected) {
            assertTrue(werePerformedInRelativeOrder("m.1|e.1|a.1"),"Run in order 'm.1|e.1|a.1'");
            assertTrue(wereUnwoundInOrder("m.2|e.2|a.2"),"Blocks unwound in order 'm.2|e.2|a.2'");
            throw Xpected;
        }
    }

    /**
     * Verify that nested try/catch/always blocks working as expected. 
     * Setup is for something like:<pre>
     *  trycatch('nestedTrycatch') -------------------------- @1
     *    sequence
     *      echo 'action1'
     *      trycatch('action2-trycatch') -------------------- @2
     *        echo 'action2-body'
     *      always
     *        trycatch('action2-always-trycatch') ----------- @3
     *          echo 'action2-always-body'
     *        always
     *          echo 'always3'
     *      echo 'action3'
     *    sequence
     *  always
     *    echo 'always'
     * </pre>
     **/
    public void testNestedNoErrors_1_0_0()
    {
        TryCatchAction action2AlwaysTrycatch = newOUT("action2-always-trycatch");
        action2AlwaysTrycatch.setBody(touch("action2-always-body"));
        action2AlwaysTrycatch.setAlways(touch("always3"));

        TryCatchAction action2Trycatch = newOUT("action2-trycatch");
        action2Trycatch.setBody(touch("action2-body"));
        action2Trycatch.setAlways(action2AlwaysTrycatch);
        
        SequenceAction outBody = new SequenceAction("body");
        outBody.add(touch("action1")).add(action2Trycatch).add(touch("action3"));

        TryCatchAction out = newOUT("nestedTrycatch");
        out.setBody(outBody);
        out.setAlways(touch("always"));

        //EXPECTED ORDER: [action1, action2-body, action2-always-body, always3, action3, always]
        List<?> names = runTASK(out);
        
        assertEquals(names.get(0),"action1","1st action was out.body sequence[0] 'action1'");
        assertEquals(names.get(1),"action2-body","2nd action was out.body sequence[1] 'action2's trycatch-body");
        assertEquals(names.get(2),"action2-always-body","3rd action was action2's always's trycatch-body'");
        assertEquals(names.get(3),"always3","4th action was action2's alway's trycatch-always");
        assertEquals(names.get(4),"action3","5th action was out.body sequence[2] 'action3'");
        assertEquals(names.get(5),"always","6th action was out.'always'");
    }
}


/* end-of-TryCatchActionTest.java */
