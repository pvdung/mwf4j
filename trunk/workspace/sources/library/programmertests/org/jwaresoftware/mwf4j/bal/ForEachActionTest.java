/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Arrays;
import  java.util.Collection;
import  java.util.Iterator;
import  java.util.List;
import  java.util.Map;
import  java.util.concurrent.Callable;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.starters.ClosureSkeleton;
import  org.jwaresoftware.mwf4j.starters.EchoAction;
import  org.jwaresoftware.mwf4j.starters.EchoStatement;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Test suite for the {@linkplain ForEachAction} and its associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class ForEachActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected ForEachAction newOUT(String id)
    {
        ForEachAction out = id==null 
            ? new ForEachAction() 
            : new ForEachAction(id);
        return out;
    }
    
    protected ForEachAction newOUT()
    {
        return newOUT(null);
    }

    private IfElseAction decision(String id, Condition c, Action yAction, Action nAction)
    {
        IfElseAction decision= new IfElseAction(id);
        decision.setTest(c);
        decision.setThen(yAction);
        decision.setElse(nAction);
        return decision;
    }

    private <T> AssignAction<T> sete(String id, String to, String expr)
    {
        AssignAction<T> out = new AssignAction<T>(id);
        out.setTo(to);
        out.setFrom(expr,StoreType.OBJECT);
        return out;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testUnconfiguredIsEmptyStatement_1_0_0()
    {
        ForEachAction out = newOUT();
        runTASK(out);
        //just making sure we get past 'run' with no issues
    }

    private List<String> listOfThree() {
        List<String> names = LocalSystem.newList();
        names.add("larry");
        names.add("curly");
        names.add("moe");
        return names;
    }

    private List<String> listOfN(String prefix, final int count) {
        List<String> names = LocalSystem.newList(count);
        for (int i=0;i<count;i++) {
            names.add(""+prefix+i);
        }
        return names;
    }

    private void verifyHappyPath(List<String> names, int numberStatements)
    {
        assertEquals(getStatementCount(),numberStatements,"calls to test statements");
        for (int i=0;i<names.size();i++) {
            String what = names.get(i);
            assertTrue(wasPerformed(what),what+" iteration performed");
        }
    }

    private void verifyHappyPath(List<String> names)
    {
        verifyHappyPath(names,names.size());
    }

    public void testHappyPathUseDatamap_1_0_0()
    {
        iniStatementCount();
        List<String> names = listOfThree();
        assertTrue(names.size()>1,"iteration greater than ONCE");

        ForEachAction out = newOUT("foreach");
        out.setCopy(false);
        out.setCursor("stooge");
        out.setDataset(names);
        out.setBody(new EchoAction("echo","stooge"));
        newHARNESS(out).run();

        verifyHappyPath(names);
    }

    public void testHappyPathUseMDC_1_0_0()
    {
        iniStatementCount();
        List<String> names = listOfThree();

        ForEachAction out = newOUT("foreach-mdc");
        out.setCopy(false);
        out.setCursor("i");
        out.setDataset(new GivebackValue<Collection<?>>(names));
        out.setBody(new EchoAction("echo","i",StoreType.THREAD));
        out.setCursorStoreType(StoreType.THREAD);
        newHARNESS(out).run();

        assertFalse(MDC.has("i"),"iteration datakey still in MDC");
        verifyHappyPath(names);
    }

    private static class FailingGetter implements Callable<Collection<?>> {
        FailingGetter() {
            super();
        }
        public Collection<?> call() throws Exception {
            throw new Exception("FailingGetter: BARFOLA!");//NB: NOT runtime=> force MWf4j wrapping!!
        }
    }

    @Test(expectedExceptions={MWf4JException.class})
    public void testCleanupIfGetDatasetFails_1_0_0()
    {
        ForEachAction out= newOUT();
        out.setDataset(new FailingGetter());
        out.setBody(new EpicFail());
        out.setCopy(false);
        try {
            newHARNESS(out).run();
        } finally {
            assertEquals(MDC.harnessDepth(),0,"harnesses in MDC");
        }
    }

    private static class HarnessDependentGetter extends ClosureSkeleton implements Callable<Collection<?>> {
        private final String myKey;
        HarnessDependentGetter(String key) {
            myKey = key;
        }
        @SuppressWarnings("unchecked")
        public Collection<?> call() throws Exception {
            prepare();
            try {
                return (List<String>)harness.getVariables().get(myKey);
            } finally {
                cleanup();
            }
        }
    }

    public void testHarnessAvailToGetter_1_0_0()
    {
        Map<String,Object> shared= iniDATAMAP();
        iniStatementCount();

        List<String> stuf = listOfThree();
        shared.put("stuf",stuf);

        ForEachAction out= newOUT("x");    //Using default cursor name algo!
        out.setDataset(new HarnessDependentGetter("stuf"));
        out.setBody(new EchoAction("x"));  //Match default cursor name algo!
        out.setCopy(false);
        newHARNESS(out).run();

        verifyHappyPath(stuf);
    }

    private static class EchoUnique extends EchoAction {
        private int n=0;
        private int barfIndex= -1;
        EchoUnique(String id, String cursor) {
            super(id,cursor);
        }
        EchoUnique(int stopIndex) {
            super("foreach");
            barfIndex=stopIndex;
        }
        protected EchoStatement newEchoStatementInstance(ControlFlowStatement next) {
            if (n==barfIndex) 
                throw new RuntimeException("FailingFactory: BARFOLA @"+n+"!");
            return new UniqueStatement(this,n++,next);
        }
    }

    private static class UniqueStatement extends EchoStatement {
        static final String STATEMENTS = "statements";
        private final int myIndex;
        UniqueStatement(Action owner, int counter, ControlFlowStatement next) {
            super(owner,next);
            myIndex = counter;
        }
        protected ControlFlowStatement runInner(Harness harness) {
            ControlFlowStatement next= super.runInner(harness);
            String uniqId = ""+myIndex;
            MDC.add(STATEMENTS,uniqId,Boolean.TRUE);
            breadcrumbs().write("Unique statement: {},{}",myId,uniqId);
            return next;
        }
    }

    @SuppressWarnings("unchecked")
    public void testHappyPathForUniqueStatementInstances_1_0_0()
    {
        iniStatementCount();
        List<String> names = listOfThree();

        ForEachAction out = newOUT("foreach-unique");
        out.setCursor("stooge");
        out.setDataset(names);
        out.setBody(new EchoUnique("echo","stooge"));
        runTASK(out);

        verifyHappyPath(names);

        assertEquals(MDC.size(UniqueStatement.STATEMENTS),names.size(),"calls to unique statements");
        Iterator<?> itr= MDC.itr(UniqueStatement.STATEMENTS);
        for (;itr.hasNext();) {
            Map.Entry<String,Object> e = (Map.Entry<String,Object>)itr.next();
            int index = Integer.valueOf(e.getKey());
            assertNotNull(names.set(index,null),"statement @"+index+" run ONCE");
        }
    }

    @Test(expectedExceptions={RuntimeException.class})
    public void testCleanupIfFactoryFails_1_0_0()
    {
        ForEachAction out= newOUT();
        out.setDataset(listOfThree());
        out.setBody(new EchoUnique(1));//BARFOLA
        try {
            runTASK(out);
        } catch(RuntimeException X) {
            assertTrue(X.getMessage().contains("BARFOLA @1!"),"right exception");
            throw X;
        } finally {
            assertEquals(MDC.size(UniqueStatement.STATEMENTS),1,"calls to unique statements");
            assertEquals(MDC.harnessDepth(),0,"harnesses in MDC");
        }
        fail("Should not get past barfing body factory");
    }

    public void testHappyPathForNestedSequence_1_0_0()
    {
        iniStatementCount();
        List<String> names = listOfThree();

        SequenceAction body = new SequenceAction("foreachseq-body");
        body.add(new TouchAction("action1"));
        body.add(new EchoAction("foreachseq"));
        final int xpectedStatementCount = names.size()*body.size();

        ForEachAction out = newOUT("foreachseq");
        out.setDataset(new GivebackValue<Collection<?>>(names));
        out.setBody(body);
        runTASK(out);
        
        verifyHappyPath(names,xpectedStatementCount);
        assertTrue(wasPerformed("action1",names.size()),"number of nested 'action1' calls");
    }

    private static class IsWeekend implements Condition {
        public boolean evaluate(Harness h) {
            String dow = h.getVariables().getStringOrFail("dayofweek");
            return "Sat".equals(dow) || "Sun".equals(dow);
        }
    }

    private static class IsWeekday implements Condition {
        private final IsWeekend isweekend= new IsWeekend();
        public boolean evaluate(Harness h) {
            return !isweekend.evaluate(h);
        }
    }
 
    /**
     * Verify following:<pre>
     * d=foreach(dayofweek={Mon,Tue,Wed,Thu,Fri,Sat,Sun})=[
     *   d.1=xxx
     *   d.2=if(isweekday)
     *         d.2.1=xxx
     *       else
     *         d.2.2=xxx
     *   d.3=xxx
     * ]
     * </pre>
     */
    public void test1NestedLevel_1_0_0()
    {
        final String[] DOW= Strings.split("Mon,Tue,Wed,Thu,Fri,Sat,Sun",",");
        iniStatementCount();
        Map<String,Object> shared= iniDATAMAP();
        Sequence seq = new SequenceAction("do")
                            .add(sete("a","wkend","(0+0)"))
                            .add(sete("b","wkday","(0+0)"))
                            .add(sete("c","count","(0+0)"));
        ForEachAction out= newOUT();
        out.setCursor("dayofweek");
        out.setDataset(Arrays.asList(DOW));
        out.setBody(new SequenceAction("d")
            .add(new EchoAction("d.1","dayofweek",true))
            .add(decision("d.2",
                    new IsWeekday(),
                    sete("d.2.1","wkday","wkday+1"),
                    sete("d.2.2","wkend","wkend+1")))
            .add(sete("d.3","count","count+1")));
        seq.add(out).add(touch("e"));
        runTASK(seq);
        assertEquals(getStatementCount(),8,"calls to test statement");
        assertEquals(shared.get("wkend"),Integer.valueOf(2),"wkend.len");
        assertEquals(shared.get("wkday"),Integer.valueOf(5),"wkday.len");
        assertEquals(shared.get("count"),Integer.valueOf(7),"total.len");
    }

    /**
     * Verify that nested foreach blocks work as expected. 
     * Setup is for something like:<pre>
     *  foreach i=0,3
     *    sequence
     *        action-1.0
     *        foreach j=0,2 ('action-1.1')
     *            sequence
     *                action-2.0
     *                action-2.1
     *                foreach k=0,3 ('action-2.2')
     *                  action-3.0
     *                action-2.3
     *        action-1.2
     * </pre>
     **/
    public void testNestedHappyPaths_1_0_0()
    {
        iniStatementCount();
        
        ForEachAction action2_2= newOUT("foreach-2.2");
        action2_2.setCursor("k");
        action2_2.setDataset(listOfN("k",3));
        action2_2.setBody(new EchoAction("action-3.0","k",true));
        action2_2.setCopy(false);

        SequenceAction action1_1Body= new SequenceAction("action1_1.body");
        action1_1Body.add(new EchoAction("action-2.0","j",true));
        action1_1Body.add(new EchoAction("action-2.1","j",StoreType.DATAMAP));
        action1_1Body.add(action2_2);
        action1_1Body.add(new EchoAction("action-2.3","j",true));
        
        ForEachAction action1_1= newOUT("foreach-1.1");
        action1_1.setCursor("j");
        action1_1.setDataset(listOfN("j",2));
        action1_1.setBody(action1_1Body);
        action1_1.setCursorStoreType(StoreType.DATAMAP);
        
        SequenceAction action0_0Body = new SequenceAction("action0_0.body");
        action0_0Body.add(new EchoAction("action-1.0","i",true));
        action0_0Body.add(action1_1);
        action0_0Body.add(new EchoAction("action-1.2","i",true));
        
        ForEachAction out = newOUT("foreach-0.0");//action-0.0
        out.setCursor("i");
        out.setBody(action0_0Body);
        out.setDataset(listOfN("i",3));
        
        runTASK(out);
        assertTrue(wasPerformed("action-3.0[k0]",6),"action-3.0[k=0] performed 6 times");
        assertTrue(wasPerformed("action-3.0[k2]",6),"action-3.0[k=2] performed 6 times");
        assertTrue(wasPerformed("action-2.3[j0]",3),"action-2.3[j=0] performed 3 times");
        assertTrue(wasPerformed("action-1.2[i0]",1),"action-1.2[i=0] performed 1 times");
        assertTrue(wasPerformed("action-1.2[i2]",1),"action-1.2[i=2] performed 1 times");
    }
}


/* end-of-ForEachActionTest.java */
