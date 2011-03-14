/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Map;
import  java.util.concurrent.RunnableFuture;
import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Test suite for {@linkplain AsyncCallAction} and its associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class AsyncCallActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected AsyncCallAction<Map<String,Object>> newOUT(String id)
    {
        AsyncCallAction<Map<String,Object>> out = id==null 
            ? new AsyncCallAction<Map<String,Object>>() 
            : new AsyncCallAction<Map<String,Object>>(id);
        return out;
    }
    
    protected AsyncCallAction<Map<String,Object>> newOUT()
    {
        return newOUT(null);
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------


    public void testTriggerAllConstructors_1_0_0()
    {
        EndStatement end = new EndStatement();
        Fixture environ = newENVIRON();
        AsyncCallAction<Map<String,Object>> c1 = new AsyncCallAction<Map<String,Object>>();
        assertFalse(Strings.isWhitespace(c1.getId()),"id is whitespace");
        Waiter work = new Waiter("ctor",c1);
        RunnableFuture<Map<String,Object>> workf = Worker.asFuture(work); 
        c1.setWorker(workf);
        assertNotNull(c1.buildStatement(end,environ),"c1.newStatement");
        AsyncCallAction<Map<String,Object>> c2 = new AsyncCallAction<Map<String,Object>>("2nd");
        assertEquals(c2.getId(),"2nd","id[2nd]");
        c2.setWorker(work);
        assertNotNull(c2.buildStatement(end,environ),"c2.newStatement");
        AsyncCallAction<Map<String,Object>> c3 = new AsyncCallAction<Map<String,Object>>("3rd",workf,"ctor.results");
        assertEquals(c3.getId(),"3rd","id[3rd]");
        assertNotNull(c3.buildStatement(end,environ),"c3.newStatement");
    }

    public void testHappyPathNoResult_1_0_0()
    {
        AsyncCallAction<Map<String,Object>> out = newOUT("happypath");
        Waiter work = new Waiter("run",out);
        work.setWaitTime(TimeUnit.MILLISECONDS.convert(1,TimeUnit.SECONDS));
        out.setWorker(work);
        assertFalse(MDC.has("workerid"),"worker leftovers");
        newHARNESS(out).run();//should not return until all continuations completed!
        assertNotNull(work.getDataMap().get("endtime"),"worker.endtime");
        assertFalse(MDC.has("workerid"),"worker run in mythread!");
        Long tid = Thread.currentThread().getId();
        assertFalse(tid.equals(work.getDataMap().get("threadid")), "thread ids same");
    }

    @SuppressWarnings("unchecked")
    public void testHappyPathSaveResultToDataMap_1_0_0()
    {
        Map<String,Object> shared= iniDATAMAP();
        AsyncCallAction<Map<String,Object>> out = newOUT("saver");
        Waiter work = new Waiter("run",out);
        out.setWorker(Worker.asFuture(work));
        out.setToKey(".result");
        out.setToStoreType(StoreType.DATAMAP);
        newHARNESS(out).run();
        Object it = shared.get(".result");
        assertTrue(it instanceof Map<?,?>,"result saved and kindof Map");
        assertSame(it,work.getDataMap(),"saved result objectref");
        Map<String,Object> m = (Map<String,Object>)it;
        assertEquals(m.get("workerid"),"run","result.workerid");
        assertNotNull(m.get("endtime"),"worker.endtime");
    }
}


/* end-of-AsyncCallActionTest.java */
