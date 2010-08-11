/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  static org.testng.Assert.*;

import  java.util.Map;
import  java.util.Random;

import  org.testng.annotations.AfterMethod;
import  org.testng.annotations.BeforeMethod;
import  org.testng.annotations.Test;

import  org.jwaresoftware.gestalt.bootstrap.Fixture;
import  org.jwaresoftware.gestalt.fixture.FixtureProperties;
import  org.jwaresoftware.gestalt.fixture.standard.FromPropertiesFixture;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import org.jwaresoftware.mwf4j.harness.SimpleHarness;
import  org.jwaresoftware.mwf4j.helpers.VariablesHashMap;
import  org.jwaresoftware.mwf4j.starters.ActivitySkeleton;
import  org.jwaresoftware.testng.TestLabel;

/**
 * Starting test suite skeleton for give and save back tests.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","assignment"})
public abstract class AssignHelperTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Support constants and types
//  ---------------------------------------------------------------------------------------

    protected Fixture.Implementation SYSTEM;
    protected static Random RANDOM = new Random(LocalSystem.currentTimeNanos());

    public static class Task extends ActivitySkeleton {
        public Task(String id)  { super(id); }
    }

//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    @BeforeMethod
    protected void setUp() throws Exception {
        LocalSystem.setProperty("ojg.ns", MWf4J.NS);
        SYSTEM= new FromPropertiesFixture(new FixtureProperties.FromLocalSystem());
    }

    @AfterMethod
    protected void tearDown() throws Exception {
        LocalSystem.resetProperties();
        MDC.clr();
    }

    protected static final int rInt() {
        return RANDOM.nextInt(50000);
    }

    protected Map<String,Object> iniForGetData()
    {
        Map<String,Object> shared= new VariablesHashMap();
        SYSTEM.setServiceInstance(MWf4J.ServiceIds.VARIABLES,shared,null);
        addSimple(shared);

        Harness h = new SimpleHarness(new Task(TestLabel.Stash.get()),SYSTEM);
        MDC.pshHarness("TESTBENCH",h);
        assertSame(MDC.currentHarness(),h,"installed-harness");
        assertSame(MDC.currentVariables(),shared,"installed-datamap");

        return shared;
    }

    /**
     * Install some basic literal values and standard collection objects.
     **/
    final void addSimple(Map<String,Object> datamap)
    {
        datamap.put("tv.show","Kick Buttowski");//non-object
        datamap.put("amigos", new String[]{"ed","edd","eddy"});//array
        datamap.put("version","1.0.4");//atomic
        datamap.put("NOW",LocalSystem.currentTimeNanos());//non-string
        Map<String,Object> pc = LocalSystem.newMap();
        pc.put("os","Windows7"); 
        pc.put("memory","8GB");
        pc.put("users",datamap.get("amigos"));//array inside map
        datamap.put("PC",pc);//map
    }

    protected void iniForGetProperty()
    {
        Harness h = new SimpleHarness(new Task(TestLabel.Stash.get()),SYSTEM);
        MDC.pshHarness("TESTBENCH",h);
        assertSame(MDC.currentHarness(),h,"installed-harness");
        assertNotNull(MDC.currentConfiguration(),"installed-configuration");
    }
}


/* end-of-AssignHelperTestSkeleton.java */
