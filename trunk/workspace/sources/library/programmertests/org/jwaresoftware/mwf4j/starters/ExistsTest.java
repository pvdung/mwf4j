/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.Map;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.assign.Exists;
import  org.jwaresoftware.mwf4j.assign.Giveback;
import  org.jwaresoftware.mwf4j.assign.GivebackException;
import  org.jwaresoftware.mwf4j.helpers.NoReturn;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

/**
 * Test suite for {@linkplain Exists}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","assignment"})
public final class ExistsTest extends ExecutableTestSkeleton
{
    public void testPassHappyPath_1_0_0()
    {
        Map<String,Object> vars= iniDATAMAP();
        vars.put("it", "HelloWorld");
        Harness harness = newHARNESS();
        Exists out = new Exists("it");
        assertTrue(out.evaluate(harness),"'it' exists");
        vars.remove("it");
        assertFalse(out.evaluate(harness),"'it' absent");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testWrapsUnexpectedException_1_0_0()
    {
        Exists out = new Exists(new Giveback<NoReturn>() {
            public NoReturn call() {
                throw new UnsupportedOperationException("Bleech");
            }
        });
        out.evaluate(newHARNESS());
        fail("Should not be able to evaluate barfing callback");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testPropagateSourceGivebackException_1_0_0()
    {
        final String MARKER="WowziWowza";
        Exists out = new Exists(new Giveback<NoReturn>() {
           public NoReturn call() {
               throw GivebackException.from(MARKER);
           }
        });
        try {
            out.evaluate(newHARNESS());
            fail("Should not be able to evaluate barfing callback");
        } catch(GivebackException Xpected) {
            assertEquals(Xpected.getMessage(),MARKER,"caught exception");
            throw Xpected;
        }
    }
}


/* end-of-ExistsTest.java */
