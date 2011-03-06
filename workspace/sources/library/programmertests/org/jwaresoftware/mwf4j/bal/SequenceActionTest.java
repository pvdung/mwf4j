/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Deque;
import  java.util.concurrent.Callable;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.helpers.Pair;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.scope.EnterLeaveScopeAction;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.FinishLaterAction;
import  org.jwaresoftware.mwf4j.starters.TestStatement;
import  org.jwaresoftware.mwf4j.starters.TouchAction;
import  org.jwaresoftware.mwf4j.starters.UnknownAction;

/**
 * Test suite for {@linkplain SequenceAction} and its associated statements.
 * <p/>
 * Note other statements+actions should always include a "as part of a sequence"
 * and "as part of a heavily nested sequence" test case in their own suites. 
 * This suite (being at top o' pile) relies on simple echo and touch actions only.
 * <p/>
 * Note that we test the rewindablility of sequence thoroughly in the 
 * {@linkplain RewindAction rewind action} test suite to avoid the dependency
 * on that action and others here.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class SequenceActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    private SequenceAction newOUT(String id)
    {
        SequenceAction out = id==null ? new SequenceAction() : new SequenceAction(id);
        out.setQuiet(false);
        return out;
    }
    
    private SequenceAction newOUT()
    {
        return newOUT(null);
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    
    public void testEmptySequence_1_0_0()
    {
        Sequence out = newOUT();
        assertTrue(out.isEmpty(),"isempty");
        assertEquals(out.size(),0,"size");
        assertNull(out.lastAdded(),"lastAdded");
        newHARNESS(out).run();
    }

    public void testEmptySequenceCopyMode_1_0_0()
    {
        SequenceAction out = newOUT("empty");
        out.setMode(SequenceAction.Mode.MULTIPLE);
        newHARNESS(out).run();
        assertTrue(out.isEmpty(),"isempty");
    }


    public void testSimpleSequenceOfEmptyStatements_1_0_0()
    {
        iniStatementCount();
        Sequence out = newOUT();
        
        out.add(new UnknownAction("1st",TestStatement.class));
        out.add(new EmptyAction("2nd"));
        assertTrue(out.lastAdded() instanceof EmptyAction,"lastAdded=empty");
        out.add(new EmptyAction("3rd"));
        out.add(new TouchAction("4th"));
        assertTrue(out.lastAdded() instanceof TouchAction,"lastAdded=touch");
        runTASK(out);

        assertEquals(getStatementCount(),2,"calls to test statements");
        assertTrue(wasPerformed("1st"),"1st performed");
        assertTrue(wasPerformed("4th"),"4th performed");
    }
    
    /**
     * Verify following ordering:<pre>
     *   i=[
     *    i.1,
     *    i.2=[ *EMPTY*
     *    ],
     *    i.3,
     *    i.4=[ *EMPTY*
     *    ],
     *    i.5,
     *   ]
     * </pre>
     */
    public void test1NestedLevel_1_0_0()
    {
        iniStatementCount();
        Sequence out= newOUT("i");
        out.add(touch("i.1"))
           .add(newOUT("i.2"))
           .add(touch("i.3"))
           .add(newOUT("i.4"))
           .add(touch("i.5"));

        runTASK(out);

        assertEquals(getStatementCount(),3,"calls to test statements");
        assertTrue(werePerformedInOrder("i.1|i.3|i.5"),"ordering");
    }

    /**
     * Verify following ordering:<pre>
     *   i=[
     *    i.1=[
     *     i.1.1
     *    ],
     *    i.2=[
     *     i.2.1
     *    ],
     *    i.3=[
     *     i.3.1
     *    ],
     *    i.4=[
     *     i.4.1
     *    ],
     *    i.5=[
     *     i.5.1
     *    ],
     *   ]
     * </pre>
     */
    public void test2NestedLevel_1_0_0()
    {
        iniStatementCount();
        Sequence out= newOUT("i");
        out.add(newOUT("i.1").add(touch("i.1.1")))
           .add(newOUT("i.2").add(touch("i.2.1")))
           .add(newOUT("i.3").add(touch("i.3.1")))
           .add(newOUT("i.4").add(touch("i.4.1")))
           .add(newOUT("i.5").add(touch("i.5.1")));

        runTASK(out);

        assertEquals(getStatementCount(),5,"calls to test statements");
        assertTrue(werePerformedInOrder("i.1.1|i.2.1|i.3.1|i.4.1|i.5.1"),"ordering");
    }

    /**
     * Verify following ordering: <pre>
     *   i=[
     *    i.1,
     *    i.2,
     *    i.3=[
     *     i.3.1
     *    ],
     *    i.4=[
     *     i.4.1,
     *     i.4.2,
     *     i.4.3=[
     *      i.4.3.1
     *     ],
     *    i.5,
     *    i.6=[
     *     i.6.1,
     *     i.6.2=[
     *      i.6.2.1,
     *      i.6.2.2=[
     *       i.6.2.2.1,
     *       i.6.2.2.2=[ *EMPTY*
     *       ]
     *      ],
     *     ],
     *     i.6.3
     *    ],
     *    i.7
     *   ]
     * </pre>
     */
    public void test3NestedLevel_1_0_0()
    {
        iniStatementCount();
        Sequence out= newOUT("i");
        out.add(touch("i.1"))
           .add(touch("i.2"))
           .add(newOUT("i.3").add(touch("i.3.1")))
           .add(newOUT("i.4").add(touch("i.4.1")).add(touch("i.4.2")).add(newOUT("i.4.3").add(touch("i.4.3.1"))))
           .add(touch("i.5"))
           .add(newOUT("i.6").add(touch("i.6.1")).add(newOUT("i.6.2").add(touch("i.6.2.1")).add(newOUT("i.6.2.2").add(touch("i.6.2.2.1")).add(newOUT("i.6.2.2.2")))).add(touch("i.6.3")))
           .add(touch("i.7"));
        
        runTASK(out);
        assertEquals(getStatementCount(),12,"calls to test statements");
        assertTrue(werePerformedInOrder("i.1|i.2|i.3.1|i.4.1|i.4.2|i.4.3.1|i.5|i.6.1|i.6.2.1|i.6.2.2.1|i.6.3|i.7"),"ordering");
    }

    public void testMultiplePassesForSameAction_1_0_0()
    {
        iniStatementCount();
        SequenceAction out = newOUT("multipass");

        out.setMode(SequenceAction.Mode.MULTIPLE);
        out.add(new TouchAction("1st"));

        newHARNESS(out).run();
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("1st"));

        newHARNESS(out).run();
        assertEquals(getStatementCount(),2,"calls to test statement");
        assertTrue(wasPerformed("1st",2));
    }

    public void testTryEachHappyPath_1_0_0()
    {
        iniStatementCount();
        SequenceAction out = newOUT();

        out.setTryEach(true);
        out.add(touch("touch")).add(new EmptyAction()).add(touch("touch"));
        newHARNESS(out).run();
        assertEquals(getStatementCount(),2,"calls to test statement");
        assertTrue(wasPerformed("touch",2),"touch x2");
    }

    public void testTryEachAndNoHaltIfError_1_0_0()
    {
        iniStatementCount();
        SequenceAction out = newOUT("tryall-nobarf");

        out.setTryEach(true);
        out.setHaltIfError(false);
        out.add(new ThrowAction()).add(new TouchAction("after"));

        runTASK(out);

        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("after"),"touch after throw");
    }

    @Test (expectedExceptions= {MWf4JException.class})
    public void testTryEachButHaltIfError_1_0_0()
    {
        iniStatementCount();
        SequenceAction out = newOUT("tryall-dobarf");

        out.setTryEach(true);
        out.add(new ThrowAction()).add(new TouchAction("after"));

        try {
            runTASK(out);
        } finally {
            assertEquals(getStatementCount(),1,"calls to test statement");
            assertTrue(wasPerformed("after"),"touch after throw");
        }
    }

    public void testContinueOnErrorHappyPath_1_0_0()
    {
        iniStatementCount();
        SequenceAction out = newOUT();

        out.setHaltIfError(false);
        out.add(new EmptyAction()).add(new EmptyAction()).add(touch("end"));
        runTASK(out);

        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("end"),"end called");
    }

    public void testContinueOnError_1_0_0()
    {
        iniStatementCount();
        SequenceAction out = newOUT("continu");

        out.setHaltIfError(false);
        out.add(new ThrowAction()).add(new EpicFail()).add(touch("after"));

        runTASK(out);
        assertEquals(getStatementCount(),0,"calls to test statement");
    }

    /**
     * Verify following with try+halt flags setup: <pre>
     *   m=[(halt=Y)
     *    m.1,
     *    m.2=[ (halt=N,tryeach=N)
     *     m.2.1,
     *     m.2.2-*THROW*,
     *     *FAIL*
     *    ],
     *    m.3,
     *    m.4,
     *    m.5=[ (halt=N,tryeach=Y)
     *     m.5.1,
     *     m.5.2-*THROW*,
     *     m.5.3-*THROW*,
     *     m.5.4,
     *     m.5.5=[ (halt=Y)
     *      m.5.5.1-*THROW*,
     *      *FAIL*
     *     ],
     *    ],
     *    m.6,
     *    m.7
     *   ]
     * </pre>
     */
    public void testContinueOnError1NestedLevel_1_0_0()
    {
        iniStatementCount();
        
        SequenceAction m2 = newOUT("m.2");
        m2.setHaltIfError(false);
        m2.add(touch("m.2.1")).add(new ThrowAction("m.2.2-ERR")).add(new EpicFail());

        SequenceAction m5 = newOUT("m.5");
        m5.setTryEach(true);
        m5.setHaltIfError(false);
        m5.add(touch("m.5.1")).add(error("m.5.2-ERR")).add(error("m.5.3-ERR")).add(touch("m.5.4"));
        m5.add(newOUT("m.5.5").add(error("m.5.5.1-ERR")).add(new EpicFail()));

        SequenceAction out   = newOUT("m");
        out.add(touch("m.1"));
        out.add(m2);
        out.add(touch("m.3"));
        out.add(new EmptyAction("m.4"));
        out.add(m5);
        out.add(new FinishLaterAction("m.6"));
        out.add(touch("m.7"));

        runTASK(out);
        assertEquals(getStatementCount(),6,"calls to test statement");
        assertTrue(werePerformedInOrder("m.1|m.2.1|m.3|m.5.1|m.5.4|m.6|m.7"),"ordering");
    }

    @Test (expectedExceptions= {MWf4JException.class})
    public void testUnwindPlainSequence_1_0_0()
    {
        iniStatementCount();

        SequenceAction out = newOUT();
        out.add(touch("before")).add(new ThrowAction("b-ERR")).add(new EpicFail());

        try {
            runTASK(out);
            fail("Should not get past throw action");
        } catch(MWf4JException Xpected) {
            assertEquals(getStatementCount(),1,"calls to test statement");
            assertTrue(wasPerformed("before"),"touch before throw");
            throw Xpected;
        }
    }

    @Test (expectedExceptions= {RunFailedException.class})
    public void testUnwindTryEachSequence_1_0_0()
    {
        iniStatementCount();

        ThrowAction throWacka = new ThrowAction("a-ERR");
        throWacka.setCause(new IllegalStateException("WhackaWhackoWhooWhoo"));
        ThrowAction throPeeep = new ThrowAction("c-ERR");
        throPeeep.setCause(new NumberFormatException("NoNoNoPeeeping"));

        SequenceAction out = newOUT();
        out.setTryEach(true);
        out.setUseHaltContinuation(false);
        out.add(throWacka).add(touch("after")).add(throPeeep);

        try {
            runTASK(out);
            fail("Should not get past throw action");
        } catch(RunFailedException Xpected) {
            assertEquals(getStatementCount(),1,"calls to test statement");
            assertTrue(wasPerformed("after"),"touch after throw");

            Throwable firstX = Xpected.getCause();
            assertTrue(firstX.getMessage().indexOf("WhooWhoo")>0,"right exception");
            Deque<Throwable> thrown = Xpected.copyOfCauses();
            assertEquals(thrown.size(),2,"num.causes");
            assertSame(thrown.pop(),throWacka.getCause(),"1st = wacka");
            assertSame(thrown.pop(),throPeeep.getCause(),"2nd = peeep");
            throw Xpected;
        }
    }

    /**
     * Verify a set of nested sequence from multiple independent threads to
     * ensure sequence scoping works as expected.<pre>
     * !=[
     *   aaa
     *   {=bbb
     *     chk.aaa
     *     $=[
     *       %=[
     *         ccc
     *         $=[
     *           ddd
     *           chk.ddd
     *           ddd
     *           chk.ddd(2)
     *         ]
     *         chk.ccc
     *         chk.aaa
     *       ]
     *       bbb
     *       ccc
     *       chk.ccc(2)
     *       chk.bbb
     *     ]
     *   }
     *   chk.ddd
     *   chk.ccc
     *   chk.bbb
     * ]
     * </pre>
     */
    public void testMultithreadedNestedScopes_1_0_0() throws Exception
    {
        //Just make some nested sequences that should work per-thread
        Sequence ddd = newOUT("$").add(touch("ddd")).add(checkdone("ddd",1)).add(touch("ddd")).add(checkdone("ddd",2));
        Sequence ccc = newOUT("%").add(touch("ccc")).add(ddd).add(checkdone("ccc")).add(checkdone("aaa"));
        Sequence bbb = newOUT("$").add(ccc).add(touch("bbb")).add(touch("ccc")).add(checkdone("ccc",2)).add(checkdone("bbb"));
        Pair<Action,Action> enterleave= EnterLeaveScopeAction.newPair("xxx");
        final Sequence out = block("!").add(touch("aaa")).add(enterleave.get1()).add(checkdone("aaa"))
                                .add(bbb).add(enterleave.get2()).add(checkdone("ddd")).add(checkdone("ccc"))
                                .add(checkdone("bbb"));

        Callable<Harness> harnessFactory = new Callable<Harness>() {
            public Harness call() {
                String tn = Thread.currentThread().getName();
                return newHARNESS("main."+tn,out);
            }
        };

        runTASK(harnessFactory,3L);
    }
}


/* end-of-SequenceActionTest.java */
