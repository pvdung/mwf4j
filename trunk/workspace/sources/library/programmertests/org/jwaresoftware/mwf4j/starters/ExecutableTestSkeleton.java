/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  static org.testng.Assert.assertTrue;

import  java.util.Arrays;
import  java.util.List;
import  java.util.concurrent.Callable;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.AfterMethod;
import  org.testng.annotations.BeforeMethod;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.bootstrap.Fixture;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.harness.SimpleHarness;
import  org.jwaresoftware.mwf4j.harness.SlaveHarness;
import  org.jwaresoftware.mwf4j.helpers.VariablesHashMap;

/**
 * Starting implementation for any test suite that needs to perform
 * and activity and/or manipulate the shared MWf4J environment.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 * @see       TestFixture
 **/

public abstract class ExecutableTestSkeleton
{
    protected Fixture.Implementation SYSTEM;

    /** Useful does-nothing-but-inherited-behavior activity implementation. **/
    public static class PlainTask extends ActivitySkeleton {
        public PlainTask(String id) {
            super(id);
        }
        public PlainTask() {
            super(TestFixture.currentTestName());
        }
        public PlainTask(Action main) {
            this();
            setDefinition(main);
        }
    }

    /** Slight variant of simple harness that records calls to error handler. **/
    public static class PlainHarness extends SimpleHarness {
        public PlainHarness(Activity activity, Fixture.Implementation fixture) {
            super(activity,fixture);
            //Diagnostics.ForFlow.info("Created "+What.idFor(this)+"/activity="+activity.getId());
        }
        public void doError(Throwable cause) {
            MDC.put("errorHandler."+getOwner().getId(), cause.getMessage());
            super.doError(cause);
        }
    }

//  ---------------------------------------------------------------------------------------
//  Harness preparation, helper factory, and execution methods
//  ---------------------------------------------------------------------------------------

    @BeforeMethod
    protected void setUp() throws Exception {
        SYSTEM= TestFixture.setUp();
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        TestFixture.tearDown();
    }

    protected Variables iniDATAMAP()
    {
        Variables shared= new VariablesHashMap();
        SYSTEM.setServiceInstance(MWf4J.ServiceIds.VARIABLES,shared,null);
        return shared;
    }

    protected final void iniStatementCount()
    {
        TestFixture.iniStatementCount();
    }

    protected final void iniPerformedList()
    {
        TestFixture.iniPerformedList();
    }

    protected final void clrPerformed()
    {
        TestFixture.clrPerformed();
    }

    protected final static List<String> dmpPerformed(String prefix, TraceSupport breadcrumbs)
    {
        if (prefix==null) prefix="PERFORMED";
        List<String> names;
        names = TestFixture.getExited();
        if (names==null) names = Empties.STRING_LIST;
        if (breadcrumbs==null)
            LocalSystem.show(prefix+"-LEAVE: "+Arrays.toString(names.toArray()));
        else 
            breadcrumbs.write("{}-LEAVE: {}",prefix,Arrays.toString(names.toArray()));

        names = TestFixture.getPerformed();
        if (names==null) names = Empties.STRING_LIST;
        if (breadcrumbs==null)
            LocalSystem.show(prefix+"-ENTER: "+Arrays.toString(names.toArray()));
        else 
            breadcrumbs.write("{}-ENTER: {}",prefix,Arrays.toString(names.toArray()));
        
        return names;
    }

    protected final static List<String> dmpPerformed(String prefix)
    {
        return dmpPerformed(prefix,null);
    }

    protected final static void dmpPerformed(String label, List<String> names, TraceSupport breadcrumbs)
    {
        if (label==null) label="PERFORMED";
        if (names==null) names= Empties.STRING_LIST;
        if (breadcrumbs==null)
            LocalSystem.show(label+": "+Arrays.toString(names.toArray()));
        else 
            breadcrumbs.write("{}: {}",label,Arrays.toString(names.toArray()));
    }

    protected Activity newTASK()
    {
        return new PlainTask();
    }

    protected Activity newTASK(Action main)
    {
        return new PlainTask(main);
    }
    
    protected Activity newTASK(String id, Action main)
    {
        PlainTask task = new PlainTask(id);
        task.setDefinition(main);
        return task;
    }

    protected Harness newHARNESS()
    {
        return new PlainHarness(newTASK(),SYSTEM);
    }

    protected Harness newHARNESS(Action main)
    {
        return new PlainHarness(newTASK(main),SYSTEM);
    }

    protected Harness newHARNESS(String id, Action main)
    {
        return new PlainHarness(newTASK(id,main),SYSTEM);
    }

    protected Harness newHARNESS(Harness parent, ControlFlowStatement firstStatement)
    {
        return new SlaveHarness(parent,firstStatement);
    }

    protected Harness newHARNESS(Activity work)
    {
        return new PlainHarness(work,SYSTEM);
    }

    protected static final List<String> runTASK(Harness h)
    {
        List<String> names;
        try {
            h.run();
        } finally {
            names = dmpPerformed(null);
        }
        return names;
    }
 
    protected final List<String> runTASK(Action main)
    {
        return runTASK(newHARNESS(main));
    }

    protected final List<String> runTASK(Activity work)
    {
        return runTASK(newHARNESS(work));
    }

    protected static void runTASK(final Callable<Harness> harnessFactory, long timeout) throws Exception
    {
        final int NUMTHREADS=4;
        final CountDownLatch start= new CountDownLatch(1);
        final CountDownLatch latch= new CountDownLatch(NUMTHREADS);
        Runnable test = new Runnable() {
            public void run() {
                final String id = Thread.currentThread().getName();
                LocalSystem.show("### THREAD '"+id+"' started");
                try { start.await(); } catch(Exception X) { }
                try {
                    Harness h = harnessFactory.call();
                    try {
                        h.run();
                    } finally {
                        dmpPerformed("PERFORMED(THR="+id+")");
                    }
                } catch(Exception unXpected) {
                    throw new RuntimeException(unXpected);
                }
                LocalSystem.show("### THREAD '"+id+"' success!");
                latch.countDown();
            }
        };
        for (int i=0;i<NUMTHREADS;i++) {
            Thread t= new Thread(test,"TT."+i);
            t.start();
        }
        timeout = Math.max(timeout,1L);
        start.countDown();
        assertTrue(latch.await(timeout,TimeUnit.SECONDS),"all threads successful");
    }

//  ---------------------------------------------------------------------------------------
//  Statement execution verification methods
//  ---------------------------------------------------------------------------------------

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

    protected final boolean wasUnwound(String statementName)
    {
        return TestFixture.wasUnwound(statementName);
    }

    protected final boolean wereUnwoundInOrder(String statementNames)
    {
        return TestFixture.wereUnwoundInOrder(statementNames,'|');
    }


//  ---------------------------------------------------------------------------------------
//  Useful helper functions for test cases
//  ---------------------------------------------------------------------------------------

    public static final void zzzzz(long millis)
    {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException iruptedX) {/*burp*/}
    }
}


/* end-of-ExecutableTestSkeleton.java */
