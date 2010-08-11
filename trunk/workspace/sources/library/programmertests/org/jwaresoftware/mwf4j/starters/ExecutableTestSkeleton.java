/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.Arrays;
import  java.util.List;

import  org.testng.annotations.AfterMethod;
import  org.testng.annotations.BeforeMethod;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.bootstrap.Fixture;

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
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
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
        }
        public void doError(Throwable cause) {
            MDC.put("errorHandler."+getOwner().getId(), cause.getMessage());
            super.doError(cause);
        }
    }

//  ---------------------------------------------------------------------------------------
//  Harness preparation and verification methods
//  ---------------------------------------------------------------------------------------

    @BeforeMethod
    protected void setUp() throws Exception {
        SYSTEM= TestFixture.setUp();
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        TestFixture.tearDown();
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

    protected Variables iniDATAMAP()
    {
        Variables shared= new VariablesHashMap();
        SYSTEM.setServiceInstance(MWf4J.ServiceIds.VARIABLES,shared,null);
        return shared;
    }

    protected final List<String> runTASK(Harness h)
    {
        List<String> names;
        try {
            h.run();
        } finally {
            names = TestFixture.getExited();
            if (names==null) names = Empties.STRING_LIST;
            System.out.println("PERFORMED-LEAVE: "+Arrays.toString(names.toArray()));

            names = TestFixture.getPerformed();
            if (names==null) names = Empties.STRING_LIST;
            System.out.println("PERFORMED-ENTER: "+Arrays.toString(names.toArray()));
        }
        return names;
    }
 
    protected final List<String> runTASK(Action main)
    {
        return runTASK(newHARNESS(main));
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
