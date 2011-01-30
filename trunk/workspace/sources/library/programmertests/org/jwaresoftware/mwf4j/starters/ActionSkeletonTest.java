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
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Test suite for {@linkplain ActionSkeleton} and typical template method usage for BAL.
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
    /** Action implementation that reflects typical BAL factory method template. **/
    static final class TypicalAction extends ActionSkeleton
    {
        private ControlFlowStatement lastMade;
        TypicalAction() {
            super();
        }
        TypicalAction(String id) {
            super(id);
        }
        public ControlFlowStatement makeStatement(ControlFlowStatement next) {
            verifyReady();//1- verify state
            lastMade = new TestStatement(this,next);//2- make statement
            return finish(lastMade); //3- configure+return statement
        }
        public void configure(ControlFlowStatement made) {
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
        ControlFlowStatement continuation = out.makeStatement(ControlFlowStatement.nullINSTANCE);
        assertNotSame(continuation,ControlFlowStatement.nullINSTANCE);
        assertSame(continuation.next(),ControlFlowStatement.nullINSTANCE,"next()");
        assertEquals(MDC.get(".made"),"a.BAL","configure breadcrumbs");
        assertEquals(MDC.get(".ready"),"a.BAL","verify breadcrumbs");
    }

    public void testANONInstance_1_0_0()
    {
        Action out = Action.anonINSTANCE;
        assertEquals(out.getId(),"","id");
        assertEquals(out.toString(),"anon","toString");
        ControlFlowStatement once = out.makeStatement(new FailStatement());
        assertTrue(once.isAnonymous(),"returned statement is anonymous");
        assertSame(once.run(null),once,"makeStatement.run()");
        once.reconfigure();
        assertSame(once.run(null),once,"makeStatement.run()");
        assertTrue(once.getOwner()==null || once.getOwner()==out,"owner null or anon");
    }
}


/* end-of-ActionSkeletonTest.java */
