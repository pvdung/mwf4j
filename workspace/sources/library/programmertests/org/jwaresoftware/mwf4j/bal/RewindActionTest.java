/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.atomic.AtomicInteger;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.scope.ByNameRewindpointMatcher;
import  org.jwaresoftware.mwf4j.scope.NumberRewindCursor;

/**
 * Test suite for {@linkplain RewindAction} and associated components.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal","advanced"})
public final class RewindActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    private RewindAction newOUT(String id)
    {
        return id==null ? new RewindAction() : new RewindAction(id);
    }


    private Action rewind(String seqid, int index, int retries)
    {
        //Kinda convoluted-- but we want to make sure we can determine the cursor's name
        RewindAction rewind = newOUT("rewind."+seqid+"@"+index);
        rewind.setRewindpointMatcher(new ByNameRewindpointMatcher("rwm."+seqid+"."+index, 
                                     NumberRewindCursor.nameFrom(seqid,index)));
        rewind.setMaxIterations(retries);
        rewind.setHaltIfMax(false);
        rewind.setCallCounter(rewind.getId()+".callnum");
        return rewind;
    }

    private Action unset(String seqid, int index)
    {
        String var = "rewind."+seqid+"@"+index+".callnum";
        AssignAction<AtomicInteger> unset = new AssignAction<AtomicInteger>(var, null);
        return unset;
    }

    private Sequence newRewindSequenceOUT(String seqid)
    {
        final Sequence out = block(seqid)
                                .add(touch("a"))
                                .add(touch("b"))
                                .add(rewind(seqid,0,1))
                                .add(unset(seqid,0))
                                .add(touch("e"))
                                .add(rewind(seqid,2,2))
                                .add(touch("g"))
                                .add(rewind(seqid,5,1))
                                .add(touch("i"))
                                .add(checkdoneorder("order","a|b|a|b|e|a|b|e|a|b|e|g|g|i"));
        return out;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testPlainSequenceRewindpoints_1_0_0() throws Exception
    {
        iniStatementCount();
        final Sequence out = newRewindSequenceOUT("main");
        runTASK(out);
    }


    @Test(dependsOnMethods={"testPlainSequenceRewindpoints_1_0_0"})
    public void testMultithreadedSequenceRewindpoints_1_0_0() throws Exception
    {
        Callable<Harness> harnessFactory = new Callable<Harness>() {
            public Harness call() {
                String tn = Thread.currentThread().getName();
                return newHARNESS("main."+tn,newRewindSequenceOUT("main"));
            }
        };

        runTASK(harnessFactory,2L);
    }
}


/* end-of-RewindActionTest.java */
