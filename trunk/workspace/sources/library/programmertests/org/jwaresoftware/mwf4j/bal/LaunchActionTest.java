/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;

import  org.testng.annotations.BeforeMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Test suite for {@linkplain LaunchAction} and its related classes.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class LaunchActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    private LaunchAction newOUT(String id)
    {
        LaunchAction out = id==null 
            ? new LaunchAction() 
            : new LaunchAction(id);
            
        MDC.Propagator fixtureClipboard = new MDC.SimplePropagator
            (TestFixture.STMT_NAMELIST, TestFixture.STMT_EXITED_NAMELIST);
        out.setMDCPropagtor(fixtureClipboard);

        return out;
    }

    private LaunchAction newOUT()
    {
        return newOUT(null);
    }

    @BeforeMethod
    protected void setUp() throws Exception
    {
        super.setUp();
        iniPerformedList();
    }

    private Collection<Action> branches(Action... branches)
    {
        Collection<Action> set = LocalSystem.newList();
        for (Action branch:branches) {
            set.add(branch);
        }
        return set;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testSimpleLaunchBulk_1_0_0()
    {
        LaunchAction out = newOUT();
        out.setActions(branches(touch("T1"),touch("T2"),touch("T3")));
        Sequence main = new SequenceAction().add(out).add(sleep1("zzz"));
        runTASK(main);
        assertTrue(werePerformed("T1|T2|T3"),"all actions launched");
    }
    
    public void testSimpleLaunchPiecemeal_1_0_0()
    {
        LaunchAction out = newOUT("piecemeal");
        out.addAction(touch("T1"));
        out.addAction(touch("T2"));
        out.addAction(touch("T3"));
        Sequence main = new SequenceAction().add(out).add(sleep1("zzz"));
        runTASK(main);
        assertTrue(werePerformed("T1|T2|T3"),"all actions launched");
    }
}


/* end-of-LaunchActionTest.java */
