/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.testng.annotations.AfterMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.LocalSystemHarness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Test suite for {@linkplain ActionSkeleton} and typical template statement
 * build method usage for BAL.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline"})
public final class ActionSkeletonTest
{
    /** Action implementation that implements typical BAL factory method template. **/
    static final class TypicalAction extends ActionSkeleton
    {
        private ControlFlowStatement lastMade;
        TypicalAction() {
            super();
        }
        TypicalAction(String id) {
            super(id);
        }
        protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ) {
            lastMade = new TestStatement(this,next);
            return lastMade;
        }
        public void configureStatement(ControlFlowStatement made, Fixture environ) {
            MDC.put(".made","a."+getId());
            assertSame(made,lastMade,"configured");
            assertFalse(made.isAnonymous());
            assertFalse(made.isTerminal());
        }
        protected void verifyReady() {
            super.verifyReady();
            MDC.put(".ready","a."+getId());
        }
    }

//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    @AfterMethod
    protected void tearDown() throws Exception {
        TestFixture.tearDown();
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testBaseline()
    {
        ActionSkeleton out = new TypicalAction();
        assertNotNull(out.getId(),"id");
        String aname = String.valueOf(LocalSystem.currentTimeNanos());
        out.setId(aname);
        assertEquals(out.getId(),aname,"id");
        out = new TypicalAction("BLEECH");
        assertEquals(out.getId(),"BLEECH","id");
    }

    public void testBALTemplateFlow_1_0_0()
    {
        ActionSkeleton out = new TypicalAction("BAL");
        ControlFlowStatement continuation = out.buildStatement(ControlFlowStatement.nullINSTANCE,null);
        assertNotSame(continuation,ControlFlowStatement.nullINSTANCE);
        assertSame(continuation.next(),ControlFlowStatement.nullINSTANCE,"next()");
        assertEquals(MDC.get(".made"),"a.BAL","configure breadcrumbs");
        assertEquals(MDC.get(".ready"),"a.BAL","verify breadcrumbs");
    }

    public void testANONInstance_1_0_0()
    {
        final Harness stubHarness = new LocalSystemHarness();
        Action out = Action.anonINSTANCE;
        assertEquals(out.getId(),"","id");
        assertEquals(out.toString(),"anon","toString");
        ControlFlowStatement once = out.buildStatement(new FailStatement(),stubHarness);
        assertTrue(once.isAnonymous(),"returned statement is anonymous");
        assertSame(once.run(stubHarness),once,"buildStatement.run()");
        once.reconfigure(stubHarness,null);
        assertSame(once.run(stubHarness),once,"buildStatement.run()");
        assertTrue("".equals(once.getWhatId()),"owner null or anon");
    }
}


/* end-of-ActionSkeletonTest.java */
