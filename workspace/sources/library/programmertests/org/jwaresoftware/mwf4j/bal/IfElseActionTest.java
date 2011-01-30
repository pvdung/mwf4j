/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.helpers.False;
import  org.jwaresoftware.mwf4j.helpers.True;
import  org.jwaresoftware.mwf4j.starters.EpicFail;
import  org.jwaresoftware.mwf4j.starters.TouchAction;

/**
 * Test suite for {@linkplain IfElseAction} and its associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class IfElseActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected IfElseAction newOUT(String id)
    {
        IfElseAction out = id==null 
            ? new IfElseAction() 
            : new IfElseAction(id);
        return out;
    }
    
    protected IfElseAction newOUT()
    {
        return newOUT(null);
    }

    private IfElseAction yes(String id, Action yAction)
    {
        IfElseAction yes= newOUT(id);
        yes.setTest(True.INSTANCE);
        yes.setThen(yAction);
        yes.setElse(new EpicFail());
        return yes;
    }

    private IfElseAction noo(String id, Action nAction)
    {
        IfElseAction noo= newOUT(id);
        noo.setTest(False.INSTANCE);
        noo.setThen(new EpicFail());
        noo.setElse(nAction);
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
        IfElseAction out = newOUT();
        newHARNESS(out).run();
        //just making sure we get past 'run' with no issues
    }

    public void testTrueByDefault_1_0_0()
    {
        iniStatementCount();
        IfElseAction out = newOUT();
        out.setThen(new TouchAction("yay-TRUE!"));
        out.setElse(new EpicFail());
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("yay-TRUE!"),"then branch performed");
    }

    public void testTrueTest_1_0_0()
    {
        iniStatementCount();
        IfElseAction out = newOUT("true");
        out.setTest(new True());
        out.setThen(new TouchAction("yay!"));
        out.setElse(new EpicFail());
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("yay!"),"then branch performed");
    }

    public void testFalseTest_1_0_0()
    {
        iniStatementCount();
        IfElseAction out = newOUT("false");
        out.setTest(new False());
        out.setElse(new TouchAction("grinn"));
        out.setThen(new EpicFail());
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("grinn"),"else branch performed");
    }

    /**
     * Verify following:<pre>
     * if(y)=
     *   if(n)=
     *     *ERROR*
     *   else=
     *     *HELLO*
     * else=
     *   *ERROR*
     * </pre>
     */
    public void testIfTrueIfFalseThenFailElseSayHelloElseFail_1_0_0()
    {
        iniStatementCount();
        IfElseAction out = yes("i",noo("j",touch("HelloWorld")));
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("HelloWorld"),"if/else branch performed");
    }
    
    public void testIfTrueIfFalseThenFailElseSayHelloElseFailWithSequences_1_0_0()
    {
        iniStatementCount();
        IfElseAction out;
        out = yes("i",body("i").add(noo("j",body("j").add(touch("HelloWorld")))));
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("HelloWorld"),"branch performed");
    }

    /**
     * Verify following:<pre>
     *  if(y)
     *    if(y)
     *      if(y)
     *        if(y)
     *          if(y)
     *           *HELLO*
     *          else
     *           *ERROR*
     *        else
     *         *ERROR*
     *      else
     *        *ERROR*
     *    else
     *       *ERROR*
     * </pre>
     */
    public void test1NestedIfThenElse_1_0_0()
    {
        iniStatementCount();
        IfElseAction out = yes("i",yes("j",yes("k",yes("l",yes("m",touch("Hello-M"))))));
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("Hello-M"),"branch performed");
    }
    
    /**
     * Verify following:<pre>
     *  if-i(n)
     *    *ERROR*
     *  else
     *   if-j(n)
     *     *ERROR*
     *   else
     *     if-k(y)
     *        if-l(n)
     *          *ERROR*
     *        else
     *          if-m(y)
     *            *HELLO*
     *          else
     *           *ERROR*
     *     else
     *       *ERROR*
     * </pre>
     */
    public void test2NestedIfThenElse_1_0_0()
    {
        iniStatementCount();
        IfElseAction out= noo("i",noo("j",yes("k",noo("l",yes("m",touch("Hello-M"))))));
        runTASK(out);
        assertEquals(getStatementCount(),1,"calls to test statement");
        assertTrue(wasPerformed("Hello-M"),"branch performed");
    }
}


/* end-of-IfThenActionTest.java */
