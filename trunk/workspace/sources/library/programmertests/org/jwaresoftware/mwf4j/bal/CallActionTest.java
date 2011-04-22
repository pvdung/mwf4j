/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Map;
import  java.util.concurrent.Callable;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.assign.GivebackStatement;
import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import org.jwaresoftware.mwf4j.assign.PutMethod;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.helpers.ClosureException;
import  org.jwaresoftware.mwf4j.helpers.NoReturn;

/**
 * Test suite for {@linkplain CallAction} and its associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class CallActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected CallAction<Map<String,Object>> newOUT(String id)
    {
        CallAction<Map<String,Object>> out = id==null 
            ? new CallAction<Map<String,Object>>() 
            : new CallAction<Map<String,Object>>(id);
        return out;
    }
    
    protected CallAction<Map<String,Object>> newOUT()
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
        CallAction<Map<String,Object>> c1 = new CallAction<Map<String,Object>>();
        assertFalse(Strings.isWhitespace(c1.getId()),"id is whitespace");
        Worker work = new Worker("ctor",c1);
        c1.setGetter(work);
        assertNotNull(c1.buildStatement(end,environ),"c1.newStatement");
        CallAction<Map<String,Object>> c2 = new CallAction<Map<String,Object>>("2nd");
        assertEquals(c2.getId(),"2nd","id[2nd]");
        c2.setGetter(work);
        c2.setToStoreType(StoreType.THREAD);
        assertNotNull(c2.buildStatement(end,environ),"c2.newStatement");
        CallAction<Map<String,Object>> c3 = new CallAction<Map<String,Object>>("3rd",work,"ctor.2.results",StoreType.DATAMAP);
        assertEquals(c3.getId(),"3rd","id[3rd]");
        assertNotNull(c3.buildStatement(end,environ),"c3.newStatement");
        CallAction<NoReturn> c4 = new CallAction<NoReturn>("4th",new Runnable() {
            public void run() { System.out.println("burp"); }
        });
        assertEquals(c4.getId(),"4th","id[4th]");
        assertNotNull(c4.buildStatement(end,environ),"c4.newStatement");
        CallAction<Map<String,Object>> c5 = new CallAction<Map<String,Object>>("5th",ref("ctor.5.results"));
        c5.setGetter(Worker.asFuture(work));
        assertNotNull(c5.buildStatement(end,environ),"c5newStatement");
    }

    public void testHappyPathNoResult_1_0_0()
    {
        CallAction<Map<String,Object>> out = newOUT();
        Worker work = new Worker("nada",out);
        out.setGetter(work);
        assertFalse(MDC.has("workerid"),"worker leftovers");
        newHARNESS(out).run();
        assertNotNull(work.getDataMap().get("endtime"),"worker.endtime");
        assertEquals(MDC.get("workerid"),"nada","stashed workerid");
    }

    @SuppressWarnings("unchecked")
    public void testHappyPathSaveResultToMDC_1_0_0()
    {
        CallAction<Map<String,Object>> out = newOUT("stash");
        Worker work = new Worker("run",out);
        out.setGetter(work);
        out.setToStoreType(StoreType.THREAD);
        out.setToKey(".result");
        assertFalse(MDC.has(".result"));
        newHARNESS(out).run();
        assertTrue(MDC.has(".result"));
        Object it = MDC.get(".result");
        assertTrue(it instanceof Map<?,?>,"it kindof Map");
        assertSame(it,work.getDataMap(),"stashed result objectref");
        Map<String,Object> m = (Map<String,Object>)it;
        assertEquals(m.get("workerid"),"run","result.workerid");
        assertNotNull(m.get("endtime"),"worker.endtime");
    }

    @SuppressWarnings("unchecked")
    public void testHappyPathSaveResultToVars_1_0_0()
    {
        Map<String,Object> shared= iniDATAMAP();
        CallAction<Map<String,Object>> out = newOUT("save");
        Worker work = new Worker("run",out);
        out.setGetter(work);
        out.setToStoreType(StoreType.DATAMAP);
        out.setToKey(".result");
        newHARNESS(out).run();
        Object it = shared.get(".result");
        assertTrue(it instanceof Map<?,?>,"result saved and kindof Map");
        assertSame(it,work.getDataMap(),"saved result objectref");
        Map<String,Object> m = (Map<String,Object>)it;
        assertEquals(m.get("workerid"),"run","result.workerid");
        assertNotNull(m.get("endtime"),"worker.endtime");
    }

    static class Failer implements Callable<NoReturn>, PutMethod<NoReturn> 
    {
        Failer() {  }
        public NoReturn call() throws Exception {
            throw new Exception("Failer doing its job (NON_RUNTIME)");//Force wrapping!
        }
        public boolean put(String path, NoReturn payload) {
            throw new UnsupportedOperationException("Failer doing its job (RUNTIME)");
        }
        public boolean putNull(String path) {
            throw new UnsupportedOperationException("Failer doing its job (RUNTIME)");
        }
    }

    @Test(expectedExceptions={ClosureException.class})
    public void testFailGetPayload_1_0_0()
    {
        CallAction<NoReturn> out = new CallAction<NoReturn>("upchuck");
        out.setToKey(".baddbadd");
        out.setToStoreType(StoreType.THREAD);
        out.setGetter(new Failer());
        try {
            runTASK(out);
            fail("Should not be able to complete call to 'Failer'");
        } catch(ClosureException Xpected) {
            System.err.println(Xpected);
            assertTrue(Xpected.getCause().getMessage().contains("(NON_RUNTIME)"),"right error");
            throw Xpected;
        } finally {
            assertFalse(MDC.has(".baddbadd"));
        }
    }

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions={UnsupportedOperationException.class})
    public void testFailConsumePayload_1_0_0()
    {
        CallAction<NoReturn> call = new CallAction<NoReturn>("barfola");
        call.setToKey(".deadbeef");
        call.setToStoreType(StoreType.THREAD);
        call.setGetter(new GivebackValue<NoReturn>(NoReturn.INSTANCE));
        AssignmentStatement<NoReturn> assignment = (AssignmentStatement<NoReturn>)call.buildStatement(new EndStatement(),newENVIRON());
        assignment.setPutter(new Failer());
        try {
            runTASK(new GivebackStatement("barfola",assignment));
            fail("Should not be able to complete put from 'Failer'");
        } catch(UnsupportedOperationException Xpected) {
            System.err.println(Xpected);
            assertTrue(Xpected.getMessage().contains("(RUNTIME)"),"right error");
            throw Xpected;
        } finally {
            assertFalse(MDC.has(".deadbeef"));
        }
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailSetUnknownStoreType_1_0_0() 
    {
        CallAction<Map<String,Object>> out = newOUT();
        out.setToStoreType(null);
        fail("Should not be able to set storetype=<null>");
    }

    static class Putter extends AssignmentStatement<Map<String,Object>>
    {
        Putter() {
        }
        @Override
        protected void consumePayload(Map<String,Object> payload, Harness harness) {
            Validate.fieldNotNull(getToKey(),"to-key");
            MDC.put(getToKey(), payload);
        }
        @Override
        public void setGetter(Callable<? extends Map<String,Object>> g) {
            if (g!=null) {
                super.setGetter(g);
            }
        }
    }

    public void testUseForDirectAssignment_1_0_0()
    {
        Map<String,Object> seekrits= LocalSystem.newMap();
        seekrits.put("who","Kick Buttowski");
        seekrits.put("what","Suburban Daredevil!");
        seekrits.put("home","http://en.wikipedia.org/wiki/Kick_Buttowski");
        Harness harness = newHARNESS();
        CallAction<Map<String,Object>> action = newOUT("assign");
        action.setToKey(".kb");
        Putter out = new Putter();
        out.reconfigure(harness,action);//Uses 'action' to trigger callback to 'configure'
        out.setPayload(seekrits);
        out.run(harness).run(null);//end|throw
        assertSame(MDC.get(".kb"),seekrits,"results set");
    }

    public static abstract class NST implements Map<String,Object> {        
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailWrongReturnType_1_0_0()
    {
        CallAction<Map<String,Object>> out = newOUT("badset");
        out.setTo(ref(".output"));
        out.setGetterRequiredReturnType(NST.class);
        out.setGetter(new Callable<Map<String,Object>>() {
            public Map<String,Object> call() {
                return LocalSystem.newMap();
            } });
        try {
            newHARNESS(out).run();
            fail("Should not be able to retrieve wrong typed data?!");
        } catch(IllegalArgumentException Xpected) {
            assertTrue(Xpected.getMessage().contains("'data' is kindof "),"exception");
            throw Xpected;
        }
    }

    public void testSaveNull_1_0_0()
    {
        Map<String,Object> vars= iniDATAMAP();
        vars.put(".result", "ERROR!");
        CallAction<String> out = new CallAction<String>("eraser",new Runnable() {
            public void run() {}
        });
        out.setToKey(".result");
        newHARNESS(out).run();
        assertNull(vars.get(".result"),".result");
        assertFalse(vars.containsKey(".result"),"'.result' item exists");
    }
}


/* end-of-CallActionTest.java */
