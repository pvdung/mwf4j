/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.testng.annotations.AfterMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.ServiceProviderException;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.helpers.CheckUnwound;
import  org.jwaresoftware.mwf4j.starters.BarfStatement;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.ExecutableTestSkeleton;
import  org.jwaresoftware.mwf4j.starters.FailStatement;
import  org.jwaresoftware.mwf4j.starters.TestExtensionPoint;
import  org.jwaresoftware.mwf4j.starters.TestStatement;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Test suite for {@linkplain Scopes} and related classes including all
 * default POJO implementations of misc&#46; interfaces.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","scope"})
public final class ScopesTest extends ExecutableTestSkeleton
{
    private static final String _STUT= "__statement_under_test";

    static final class CheckEnterLeaveScope extends ScopeBean {
        CheckEnterLeaveScope(String name) {
            super(name);
        }
        public void doEnter(Harness harness) {
            harness.getVariables().put(getName()+".enter",Boolean.TRUE);
            super.doEnter(harness);
        }
        public void doLeave(Harness harness) {
            harness.getVariables().put(getName()+".leave",Boolean.TRUE);
        }
        static ScopeFactory.SPI factoryInstance= new ScopeFactory.SPI() {
            public ScopeKey newKey(ControlFlowStatement owner) {
                return new ScopeKeyBean(owner);
            }
            public Scope newScope(String name) {
                return new CheckEnterLeaveScope(name);
            }
        };
    }

    static final class BarfOnEnterScope extends ScopeBean {
        public void doEnter(Harness harness) {
            ControlFlowStatement mine = harness.getVariables().get(_STUT,ControlFlowStatement.class);
            if (mine!=null)
                assertTrue(MDC.has(Scopes._STACK, ScopeFactory.newKey(mine)),"enqueued before inited");
            else 
                assertTrue(MDC.get(Scopes._STACK,ScopeKey.class).getOwner() instanceof HarnessRunStatement);
            throw new MWf4JException("BarfOnEnter");
        }
        static ScopeFactory.SPI factoryInstance= new ScopeFactory.SPI() {
            public ScopeKey newKey(ControlFlowStatement owner) {
                return new ScopeKeyBean(owner);
            }
            public Scope newScope(String name) {
                return new BarfOnEnterScope();
            }
        };
    }

    @AfterMethod
    protected void tearDown() throws Exception 
    {
        ScopeFactory.unsetProviderInstance();
        super.tearDown();
    }

    private static ControlFlowStatement newStatement(String id)
    {
        return new TestStatement(Action.anonINSTANCE,ControlFlowStatement.nullINSTANCE);
    }

    private static void assumeNoScopeStack()
    {
        boolean condition = !MDC.has(Scopes._STACK) || MDC.size(Scopes._STACK)==0;
        assertTrue(condition,"no scopes stack");
    }

    private void assertEnterLeave(String id, Harness h)
    {
        id = Strings.trimToEmpty(id);
        assertTrue(h.getVariables().getFlag(id+".enter"),"scope="+id+".doEnter() called");
        assertTrue(h.getVariables().getFlag(id+".leave"),"scope="+id+".doLeave() called");
    }

    private void assertUnwound(String id, Harness h)
    {
        id = Strings.trimToEmpty(id);
        assertTrue(h.getVariables().getFlag(id+".unwound"),id+".doUnwind() called");
        assertTrue(wasUnwound(id));
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testTypicalHappyNestingPath_1_0_0()
    {
        ScopeFactory.setProviderInstance(CheckEnterLeaveScope.factoryInstance);
        ControlFlowStatement st0,st1,st2;
        Harness harness = newHARNESS();

        Scope s0 = Scopes.enter((st0=newStatement("0")),harness);
        assertSame(Scopes.nearestOrFail(),s0,"isNearest('s0')");
        assertTrue(harness.getVariables().getFlag(".enter"),"s0.doEnter() called");
        assertNull(harness.getVariables().get(".leave"),"s0.doLeave() NOT called");
        
        Scope s1 =  Scopes.enter((st1=newStatement("1")),"s1",harness);
        assertNotSame(s1,s0,"s1!=s0");
        assertSame(Scopes.nearestOrFail(),s1,"isNearest('s1')");
        assertTrue(harness.getVariables().getFlag("s1.enter"),"s1.doEnter() called");
        assertNull(harness.getVariables().get("s1.leave"),"s1.doLeave() NOT called");

        Scope s2 =  Scopes.enter((st2=newStatement("2")),"s2",harness);
        assertNotSame(s2,s0,"s2!=s0");
        assertNotSame(s2,s1,"s2!=s1");
        assertSame(Scopes.nearestOrFail(),s2,"isNearest('s2')");
        assertTrue(harness.getVariables().getFlag("s2.enter"),"s2.doEnter() called");
        assertNull(harness.getVariables().get("s2.leave"),"s2.doLeave() NOT called");

        Scopes.leave(st2,harness);
        assertTrue(harness.getVariables().getFlag("s2.leave"),"s2.doLeave() called");
        Scopes.leave(st1,harness);
        assertTrue(harness.getVariables().getFlag("s1.leave"),"s1.doLeave() called");
        Scopes.leave(st0,harness);
        assertTrue(harness.getVariables().getFlag(".leave"),"s0.doLeave() called");
    }

    public void testCleanupIfScopeCreationFails_1_0_0()
    {
        assumeNoScopeStack();
        Harness harness = newHARNESS();
        ControlFlowStatement statement = new FailStatement();
        harness.getVariables().put(_STUT, statement);
        ScopeFactory.setProviderInstance(BarfOnEnterScope.factoryInstance);
        try {
            Scopes.enter(statement, harness);
            fail("Should not be able to create a new 'BarfOnEnterScope'");
        } catch(MWf4JException Xpected) {
            assertTrue(Xpected.getMessage().contains("BarfOnEnter"),"isa BarfOnEnter");
            assumeNoScopeStack();
        }
    }

    public void testDetectCorruptedStackOnLeave_1_0_0()
    {
        ControlFlowStatement st= newStatement("fu");
        Harness h = newHARNESS();
        Scope s = Scopes.enter(st,h);
        ScopeKey peekKey = MDC.get(Scopes._STACK,ScopeKey.class);
        peekKey.setScope(null);//corrupt it
        try {
            Scopes.leave(st,h);
            fail("Should not be able to leave from missing scope!");
        } catch(IllegalStateException Xpected) {
            Xpected.printStackTrace(System.err);
            assertTrue(Xpected.getMessage().contains("'scope' is null"),"right error");
        }
        peekKey.setScope(s);//put it back
        Scopes.leave(st,h);
    }

    public void testUnwind_1_0_0()
    {
        assumeNoScopeStack();

        ScopeFactory.setProviderInstance(CheckEnterLeaveScope.factoryInstance);
        ControlFlowStatement st0,st1,st2;
        CheckUnwound uw0,uw1,uw2,uwx;
        Harness h = newHARNESS();
        Scope s0 = Scopes.enter((st0=newStatement("do")),"do",h);
        Scopes.addUnwind((uw0= new CheckUnwound("do")));
        Scope s1 = Scopes.enter((st1=newStatement("try")),"try",h);
        Scopes.addUnwind((uw1= new CheckUnwound("try")));
        Scope s2 = Scopes.enter((st2=newStatement("for")),"for",h);
        s2.addUnwind((uw2= new CheckUnwound("for")));//should be same!
        s1.addUnwind((uwx= new CheckUnwound("err")));
        assertSame(s2,Scopes.nearestOrFail());
        
        final int n = MDC.size(Scopes._STACK);
        Scopes.unwindUpTo(st1,h);
        assertTrue(uw2.unwound(),"'for'.doUnwind() called");
        assertTrue(h.getVariables().getFlag("for.leave"),"'for'.doLeave() called");
        assertEquals(MDC.size(Scopes._STACK),n-1,"one popped");
        Scopes.removeUnwind(uwx);
        assertFalse(uwx.unwound(),"'err'.doUnwind NOT called");
        assertSame(s1,Scopes.nearestOrFail());

        Scope old_s2 = s2;
        s2 = Scopes.enter((st2=newStatement("for2")),"for2",h);
        assertNotSame(s2,old_s2,"new scope allocated per callback");
        Scopes.addUnwind((uw2= new CheckUnwound("for2")));
        Scopes.unwindUpTo(st0,h);
        assertFalse(uwx.unwound(),"'err'.doUnwind NOT called");
        assertTrue(uw2.unwound(),"'for2'.doUnwind() called");
        assertTrue(h.getVariables().getFlag("for2.leave"),"'for2'.doLeave() called");
        assertTrue(uw1.unwound(),"'try'.doUnwind() called");
        assertTrue(h.getVariables().getFlag("try.leave"),"'try'.doLeave() called");
        assertEquals(MDC.size(Scopes._STACK),n-2,"two popped");
        assertSame(s0,Scopes.nearestOrFail());

        s0.unwindAll(h);
        assertTrue(uw0.unwound(),"'do'.doUnwind() called");
        assertNull(h.getVariables().getFlag("do.leave"),"'do'.doLeave() NOT called");
        assertEquals(MDC.size(Scopes._STACK),n-2,"scope.unwindAll does not touch stack!");
        Scopes.leave(st0,h);
        assertTrue(h.getVariables().getFlag("do.leave"),"'do'.doLeave() called");

        assumeNoScopeStack();
    }

    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailEnterNullStatement_1_0_0()
    {
        Scopes.enter(null,newHARNESS());
        fail("Should not be able to enter into 'NULL' statement!");
    }

    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailEnterNullHarness_1_0_0()
    {
        Scopes.enter(newStatement("fu"),null);
        fail("Should not be able to enter with 'NULL' harness!");
    }

    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailLeaveNullStatement_1_0_0()
    {
        Scopes.leave(null,newHARNESS());
        fail("Should not be able to leave from 'NULL' statement!");
    }

    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailLeaveNullHarness_1_0_0()
    {
        Scopes.leave(newStatement("fu"),null);
        fail("Should not be able to leave from 'NULL' harness!");
    }

    public void testTypicalHarnessRunHappyPath_1_0_0()
    {
        ScopeFactory.setProviderInstance(CheckEnterLeaveScope.factoryInstance);
        Harness h = newHARNESS(new TouchAction("main"));
        runTASK(h);
        String hId = Scopes.scopeNameOf(h);
        assertEnterLeave(hId,h);
    }

    private static class OrphanUnwind extends TestExtensionPoint {
        OrphanUnwind() {
            super("orphan");
        }
        OrphanUnwind(String id) {
            super(id);
        }
        protected ControlFlowStatement runInner(Harness harness) {
            Scopes.enter(this,"orphan",harness);//Never 'leave' explicitly
            Scopes.addUnwind(new CheckUnwound(getId()));
            return next();
        }
    }

    @Test(dependsOnMethods={"testTypicalHarnessRunHappyPath_1_0_0"})
    public void testHarnessLeaveWillLeaveOrphaned_1_0_0()
    {
        ScopeFactory.setProviderInstance(CheckEnterLeaveScope.factoryInstance);
        Harness h = newHARNESS(new OrphanUnwind());
        runTASK(h);
        final String hId = Scopes.scopeNameOf(h);
        assertEnterLeave("orphan",h);
        assertNull(h.getVariables().getFlag("orphan.unwound"),"orphan.doUnwind NOT called");
        assertEnterLeave(hId,h);
    }

    public void testErrorInHarnessEnterCleansUpStack_1_0_0()
    {
        MDC.assertNoHarnessInstalled();
        assumeNoScopeStack();
        ScopeFactory.setProviderInstance(BarfOnEnterScope.factoryInstance);
        Harness h = newHARNESS(new EpicFail()/*should never get executed*/);
        try {
            runTASK(h);
            fail("Should not be able to get past Scopes.enter");
        } catch(MWf4JException Xpected) {
            assertTrue(Xpected.getMessage().contains("BarfOnEnter"),"isa BarfOnEnter");
            assumeNoScopeStack();
            MDC.assertNoHarnessInstalled();
        }
    }

    private static class HarnessUnwind extends TestExtensionPoint {
        HarnessUnwind(String id) {
            super(id);
        }
        protected ControlFlowStatement runInner(Harness harness) {
            Scopes.addUnwind(new CheckUnwound(getId()));//Should link to harness scope!
            return next();
        }
    }

    private static class OrphanSequence extends TestExtensionPoint { 
        OrphanSequence() {
            super("flow");
            ControlFlowStatement o4= new BarfStatement("ADIOS CRUEL WORLD!!");
            ControlFlowStatement o3= new OrphanUnwind("s2").makeStatement(o4);
            ControlFlowStatement o2= new OrphanUnwind("s1").makeStatement(o3);
            ControlFlowStatement o1= new HarnessUnwind("top").makeStatement(o2);
            myStatements = o1;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            return myStatements;
        }
        private ControlFlowStatement myStatements;
    }

    public void testHarnessUnwindForUncaughtError_1_0_0()
    {
        assumeNoScopeStack();
        Harness h = newHARNESS(new OrphanSequence());
        try {
            runTASK(h);
            fail("Should not get past 'CRUEL WORLD' failure");
        } catch (ServiceProviderException Xpected) {
            assertTrue(Xpected.getMessage().contains("ADIOS CRUEL WORLD"),"is Xpected barf");
            assertUnwound("s1",h);
            assertUnwound("s2",h);
            assertUnwound("top",h);
            assertTrue(wereUnwoundInOrder("s2|s1|top"),"Unwound in expected order 's2->s1->top'");
            assumeNoScopeStack();
        }
    }
}


/* end-of-ScopesTest.java */
