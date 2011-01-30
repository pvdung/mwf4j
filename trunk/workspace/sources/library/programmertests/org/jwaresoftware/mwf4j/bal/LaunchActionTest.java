/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.List;
import  java.util.concurrent.Future;

import  org.testng.annotations.BeforeMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Variables;

/**
 * Test suite for {@linkplain LaunchAction} and its related classes. Not much
 * here; see {@linkplain ListenActionTest} for additional uses of launch.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","bal","advanced"})
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
        out.setMDCPropagator(fixtureClipboard);

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

    @SuppressWarnings("unchecked")
    final static Future<?> getLinkOrFail(Variables theVars,String linkVar)
    {
        List<Future<?>> l = theVars.get(linkVar,List.class);
        assertNotNull(l, linkVar+" futureRef List<?>");
        assertEquals(l.size(),1,linkVar+" futureRefs.size");
        Future<?> f = l.get(0);
        assertNotNull(f, linkVar);
        return f;
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

    @Test(dependsOnMethods={"testSimpleLaunchBulk_1_0_0"})
    public void testSavebackFutureRefs_1_0_0()
    {
        Variables vars = iniDATAMAP();
        LaunchAction out = newOUT();
        out.addAction(touch("T1"));
        out.setLinkSaveRef("harness.Link");
        runTASK(new SequenceAction().add(out).add(sleep1("zzz")));
        Future<?> lnk = getLinkOrFail(vars,"harness.Link");
        assertTrue(lnk.isDone(),"harness.isDone()");
    }
}


/* end-of-LaunchActionTest.java */
