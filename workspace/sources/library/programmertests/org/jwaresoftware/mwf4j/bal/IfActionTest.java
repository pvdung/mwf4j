/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.helpers.False;
import  org.jwaresoftware.mwf4j.helpers.TooLong;
import  org.jwaresoftware.mwf4j.helpers.True;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Test suite for {@linkplain IfAction} and its associated statements.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class IfActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected IfAction newOUT(String id)
    {
        IfAction out = id==null 
            ? new IfAction() 
            : new IfAction(id);
        return out;
    }
    
    protected IfAction newOUT()
    {
        return newOUT(null);
    }

    protected Condition newTooLongTrue()
    {
        TooLong c = new TooLong(5,TimeUnit.SECONDS);
        c.start(LocalSystem.currentTimeMillis() - 6000L);
        return c;
    }

    private IfAction yes(String id, Action body)
    {
        IfAction yes= newOUT(id);
        yes.setTest(True.INSTANCE);
        yes.setThen(body);
        return yes;
    }

    private IfAction noo(String id)
    {
        IfAction noo= newOUT(id);
        noo.setTest(False.INSTANCE);
        noo.setThen(new EpicFail());
        return noo;
    }
    
    private SequenceAction body(String id)
    {
        return new SequenceAction(id);
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testUnconfiguredIsEmptyStatement_1_0_0()
    {
        IfAction out = newOUT();
        runTASK(out);
        //getting here OK with no issues is 'pass' criteria...
    }

    public void testTrueByDefault_1_0_0()
    {
        iniStatementCount();
        IfAction out = newOUT();
        out.setThen(new TouchAction("smile"));
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("smile"),"then branch performed");
    }

    public void testTrueTest_1_0_0()
    {
        iniStatementCount();
        IfAction out = newOUT("true");
        out.setTest(newTooLongTrue());
        out.setThen(new TouchAction("abort"));
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("abort"),"then branch performed");
    }

    public void testFalseTest_1_0_0()
    {
        iniStatementCount();
        IfAction out = newOUT("false");
        out.setTest(False.INSTANCE);
        out.setThen(new EpicFail());
        runTASK(out);
        //getting here OK is the 'pass' criteria...
    }

    /**
     * Verify following order:
     * <pre>
     *   h
     *   i=if(y)=[
     *     i.1
     *     i.2=if(y)=[
     *       ii.1
     *       ii.2=if(y)=[
     *        iii.1
     *        iii.2
     *        iii.3=if(y)=[
     *         iv.1
     *        ],
     *        iii.4=if(n)=[
     *          *FAIL*
     *        ],
     *       ],
     *       ii.3
     *     ],
     *     i.3,
     *     i.4=if(n)=[
     *      *FAIL*
     *     ],
     *   ],
     *   j
     * </pre>
     */
    public void test1NestedIfThen_1_0_0()
    {
        iniStatementCount();

        SequenceAction out= new SequenceAction();
        out.add(touch("h"))
           .add(yes("i",body("i")
                   .add(touch("i.1"))
                   .add(yes("i.2",body("i.2")
                           .add(touch("ii.1"))
                           .add(yes("ii.2",body("ii.2")
                                   .add(touch("iii.1"))
                                   .add(touch("iii.2"))
                                   .add(yes("iii.3",body("iii.3")
                                           .add(touch("iv.1"))))
                                   .add(noo("iii.4"))))
                           .add(touch("ii.3"))))
                   .add(touch("i.3"))
                   .add(noo("i.4"))))
           .add(touch("j"));

        runTASK(out);

        assertEquals(getStatementCount(),9,"calls to test statement");
        assertTrue(werePerformedInOrder("h|i.1|ii.1|iii.1|iii.2|iv.1|ii.3|i.3|j"),"ordering");
    }
}


/* end-of-IfActionTest.java */
