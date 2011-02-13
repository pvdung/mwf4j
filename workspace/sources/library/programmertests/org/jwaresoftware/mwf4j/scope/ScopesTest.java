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
import  org.jwaresoftware.gestalt.helpers.Pair;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.bal.ActionTestSkeleton;
import  org.jwaresoftware.mwf4j.bal.ForkAction;
import  org.jwaresoftware.mwf4j.bal.TryCatchAction;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;
import  org.jwaresoftware.mwf4j.helpers.TestUnwinder;
import  org.jwaresoftware.mwf4j.starters.AddTestUnwindAction;
import  org.jwaresoftware.mwf4j.starters.BarfStatement;
import  org.jwaresoftware.mwf4j.starters.CheckUnwound;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.FailStatement;
import  org.jwaresoftware.mwf4j.starters.TestExtensionPoint;
import  org.jwaresoftware.mwf4j.starters.TestStatement;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Test suite for {@linkplain Scopes} and related classes including all
 * default POJO implementations of misc&#46; interfaces.
 * <p/>
 * We also include tests that test the basics of sequence and trycatch
 * actions with complex scopes in effect.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","scope"})
public final class ScopesTest extends ActionTestSkeleton
{
    private static final String _STUT= "__statement_under_test";

    static final class CheckEnterLeaveScope extends ScopeBean {
        CheckEnterLeaveScope(ControlFlowStatement owner, String name) {
            super(owner,name);
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
            public Scope newScope(ControlFlowStatement owner,String name) {
                return new CheckEnterLeaveScope(owner,name);
            }
        };
    }

    static final class BarfOnEnterScope extends ScopeBean {
        BarfOnEnterScope(ControlFlowStatement owner) {
            super(owner);
        }
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
            public Scope newScope(ControlFlowStatement owner, String name) {
                return new BarfOnEnterScope(owner);
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

    private static ForkAction fork(boolean carry)
    {
        ForkAction fork = new ForkAction("run'em");
        if (carry) {
            MDC.Propagator fixtureClipboard = new MDC.CopyPropagator
                (TestFixture.STMT_NAMELIST, TestFixture.STMT_EXITED_NAMELIST, TestFixture.STMT_UNWIND_NAMELIST);
            fork.setMDCPropagtor(fixtureClipboard);
        }
        fork.setJoinBreakSupport(new RetryDef(1,_1SEC*3L),null);//Don't hang forever if something barfs
        return fork;
    }

    private TryCatchAction trycatch(Action body, Action always)
    {
        TryCatchAction trycatch = new TryCatchAction();
        trycatch.setHaltIfError(false);
        trycatch.setBody(body);
        trycatch.setAlways(always);
        return trycatch;
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

    public void testScopeKeyBeanCompare_1_0_0()
    {
        ControlFlowStatement stmt1= new TestStatement(null,ControlFlowStatement.nullINSTANCE);
        ControlFlowStatement stmt2= new TestStatement(null,ControlFlowStatement.nullINSTANCE);
        ScopeKey out1= ScopeFactory.newKey(stmt1);
        assertNotNull(out1, "key1 created");
        assertSame(out1.getOwner(), stmt1, "owner1==assigned");
        ScopeKey out2= ScopeFactory.newKey(stmt2);
        assertNotNull(out2, "key2 created");
        assertSame(out2.getOwner(), stmt2, "owner2==assigned");
        assertNotSame(out1,out2);
        assertFalse(out1.equals(out2),"out1.equals(out2)");
        assertFalse(out2.equals(out1),"out2.equals(out1)");
        String _lstack= TestFixture.currentTestName();
        MDC.pshifmissing(_lstack, out1);
        MDC.pshifmissing(_lstack, out2);
        assertEquals(MDC.size(_lstack),2);
        assertTrue(MDC.has(_lstack,out1),"out1 recognized as being on MDC stack");
        ScopeKey out1p = ScopeFactory.newKey(stmt1);
        assertNotSame(out1p,out1);
        assertEquals(out1p,out1,"equality based on statement");
        assertTrue(MDC.has(_lstack,out1p),"out1 recognized as being on MDC stack");
    }

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
        TestUnwinder uw0,uw1,uw2,uwx;
        Harness h = newHARNESS();
        Scope s0 = Scopes.enter((st0=newStatement("do")),"do",h);
        Scopes.addUnwind((uw0= new TestUnwinder("do")));
        Scope s1 = Scopes.enter((st1=newStatement("try")),"try",h);
        Scopes.addUnwind((uw1= new TestUnwinder("try")));
        Scope s2 = Scopes.enter((st2=newStatement("for")),"for",h);
        s2.addUnwind((uw2= new TestUnwinder("for")));//should be same!
        s1.addUnwind((uwx= new TestUnwinder("err")));
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
        st2.getOwner();//shutup eclipse
        assertNotSame(s2,old_s2,"new scope allocated per callback");
        Scopes.addUnwind((uw2= new TestUnwinder("for2")));
        Scopes.unwindUpTo(st0,h);
        assertFalse(uwx.unwound(),"'err'.doUnwind NOT called");
        assertTrue(uw2.unwound(),"'for2'.doUnwind() called");
        assertTrue(h.getVariables().getFlag("for2.leave"),"'for2'.doLeave() called");
        assertTrue(uw1.unwound(),"'try'.doUnwind() called");
        assertTrue(h.getVariables().getFlag("try.leave"),"'try'.doLeave() called");
        assertEquals(MDC.size(Scopes._STACK),n-2,"two popped");
        assertSame(s0,Scopes.nearestOrFail());

        s0.doUnwind(h);
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

    private static class OrphanUnwind extends TestExtensionPoint {//ONLY FOR 1 THREAD USE!
        OrphanUnwind() {
            super("orphan");
        }
        OrphanUnwind(String id) {
            super(id);
        }
        protected ControlFlowStatement runInner(Harness harness) {
            Scopes.enter(this,"orphan",harness);//Never 'leave' explicitly
            Scopes.addUnwind(new TestUnwinder(getId()));
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

    private static class OrphanSequence extends TestExtensionPoint {//ONLY FOR 1 THREAD USE!
        OrphanSequence() {
            super("flow");
            ControlFlowStatement o4= new BarfStatement("ADIOS CRUEL WORLD!!");
            ControlFlowStatement o3= new OrphanUnwind("s2").makeStatement(o4);
            ControlFlowStatement o2= new OrphanUnwind("s1").makeStatement(o3);
            ControlFlowStatement o1= new AddTestUnwindAction("top").makeStatement(o2);
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

    @Test(dependsOnMethods={"testTypicalHappyNestingPath_1_0_0"})
    public void testMultithreadedTypicalPathA_1_0_0()
    {
        iniPerformedList();
        ScopeFactory.setProviderInstance(CheckEnterLeaveScope.factoryInstance);
        ForkAction fork = fork(true);
        Sequence t1 = block("t1").add(touch("a")).add(block("b").add(touch("b.1"))).add(touch("c"));
        fork.addBranch(t1);
        Sequence t2 = block("t2").add(touch("1")).add(block("2").add(touch("2.a"))).add(touch("3"));
        fork.addBranch(t2);
        Sequence t3 = block("t3").add(touch("X")).add(block("Y").add(touch("Y.a"))).add(touch("Z"));
        fork.addBranch(t3);

        runTASK(fork);
        assertTrue(werePerformedAndExited("a|b.1|c|1|2.a|3|X|Y.a|Z"),"all actions run");
    }

    /**
     * Verify this from independent threads (use fork):<pre>
     *  Touch(aaa)
     *  Enter new scope named(bbb)
     *  Touch(ccc)
     *  Sequence[
     *    Touch(ddd)
     *    Try(haltiferror=no)
     *      Sequence[
     *        Sequence[
     *          Verify ccc executed ONCE
     *          Touch(eee)
     *          Verify aaa executed ONCE
     *          Touch(aaa)
     *          Verify eee executed ONCE
     *        ]
     *        Install check unwinder(fff)
     *        Barf
     *      ]
     *    Always
     *      Sequence[
     *        Verify ddd executed ONCE
     *        Verify eee executed ONCE
     *        Verify aaa executed TWICE
     *        Verify unwound(fff)
     *        Touch(ddd)
     *      ]
     *  ]
     *  Verify ddd executed TWICE
     *  Unwind(bbb)
     *  </pre>
     */
    @Test(dependsOnMethods={"testTypicalHappyNestingPath_1_0_0"})
    public void testMultithreadedTypicalPathB_1_0_0()
    {
        Sequence xxx = block("@").add(checkdone("ccc")).add(touch("eee")).add(checkdone("aaa")).add(touch("aaa")).add(checkdone("aaa",2)).add(checkdone("eee"));
        Sequence yyy = block("#").add(xxx).add(new AddTestUnwindAction("uuu")).add(error("xx1"));
        Sequence zzz = block("%").add(checkdone("ddd")).add(checkdone("eee")).add(checkdone("aaa",2)).add(new CheckUnwound("fff","uuu")).add(touch("ddd"));
        Sequence ddd = block("$").add(touch("ddd")).add(trycatch(yyy,zzz));
        Pair<Action,Action> enterleave= EnterLeaveScopeAction.newPair("bbb");
        Sequence tst = block("!").add(touch("aaa")).add(enterleave.get1()).add(touch("ccc")).add(ddd).add(checkdone("ddd",2)).add(enterleave.get2());

        runTASK(tst);//verify our chain works before blasting off in new threads
        clrPerformed();

        ForkAction fork = fork(false);
        fork.addBranch(tst);
        fork.addBranch(tst);
        fork.addBranch(tst);
        fork.addBranch(tst);
        runTASK(fork);
    }
}


/* end-of-ScopesTest.java */
