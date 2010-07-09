/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  java.util.Collection;
import  java.util.List;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.bootstrap.Fixture;
import  org.jwaresoftware.gestalt.fixture.FixtureProperties;
import  org.jwaresoftware.gestalt.fixture.standard.FromPropertiesFixture;
import  org.jwaresoftware.gestalt.helpers.Handle;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.testng.TestLabel;

/**
 * Common constants and helper methods for MWf4J tests and test suites.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public final class TestFixture
{
    public final static String STMT_COUNTER  = "mwf4j.statement.count";
    public final static String STMT_NAMELIST = "mwf4j.statement.names";


    public static Fixture.Implementation setUp()
    {
        LocalSystem.setProperty("ojg.ns", "mwf4j");
        LocalSystem.setProperty("mwf4j.environment.type","DEV");
        LocalSystem.setProperty("mwf4j.logger.diagnostics",Diagnostics.GROUPING_CORE);
        return new FromPropertiesFixture(new FixtureProperties.FromLocalSystem());
    }


    public static void tearDown()
    {
        LocalSystem.clrUnderlay();
        LocalSystem.resetProperties();
        MDC.clr();
    }


    public static final String currentTestName()
    {
        return TestLabel.Stash.get();
    }


    public static final void iniStatementCount()
    {
        MDC.put(STMT_COUNTER,Handle.newZeroInteger());
    }


    @SuppressWarnings("unchecked")
    public static final int getStatementCount()
    {
        Handle<Integer> ncalls = (Handle<Integer>)MDC.get(STMT_COUNTER);
        return ncalls==null ? 0 : ncalls.get().intValue();
    }


    @SuppressWarnings("unchecked")
    public static final void incStatementCount()
    {
        Object o = MDC.get(STMT_COUNTER);
        if (o instanceof Handle<?>) {
            Handle<Integer> h = (Handle<Integer>)o;
            h.set(h.get()+1);
        }
    }


    public static final void addPerformed(String statementName)
    {
        MDC.add(STMT_NAMELIST, statementName);
    }


    @SuppressWarnings("unchecked")
    public static final boolean wasPerformed(String statementName) 
    {
        Object o = MDC.get(STMT_NAMELIST);
        if (o instanceof Collection<?>) {
            return ((Collection)o).contains(statementName);
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public static final boolean wasPerformed(String statementName, int count) 
    {
        Object o = MDC.get(STMT_NAMELIST);
        if (o instanceof Collection<?>) {
            Collection<String> c = (Collection<String>)o;
            int n = 0;
            for (String next:c) {
                if (statementName.equals(next)) {
                    n++;
                }
            }
            if (n==count) {
                return true;
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public static final boolean werePerformed(String statementNames, char delim) 
    {
        Object o = MDC.get(STMT_NAMELIST);
        if (o instanceof Collection<?>) {
            Collection<String> c = (Collection<String>)o;
            String[] ordering = Strings.split(statementNames,delim);
            if (c.size()==ordering.length) {
                int ith=0;
                for (String next:c) {
                    if (!Strings.equal(ordering[ith++],next)) 
                        return false;
                }
                return true;
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public static final List<String> getPerformed()
    {
        return MDC.get(STMT_NAMELIST,List.class);
    }

    
    public static final void clrPerformed()
    {
        MDC.clr(STMT_NAMELIST);
    }


    private TestFixture() { /*only static helper API*/ }
}


/* end-of-TestFixture.java */
