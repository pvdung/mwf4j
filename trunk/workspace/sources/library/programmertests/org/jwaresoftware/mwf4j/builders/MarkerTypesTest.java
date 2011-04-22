/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

/**
 * Test suite for misc maker types like {@linkplain ID} and {@linkplain Flag}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","builders","advanced","builders-baseline"})
public final class MarkerTypesTest
{
    public void testUnnamedEmptyID()
    {
        ID out = new ID(null);
        assertNull(out.value(),"value");
        assertTrue(out.isNull(),"isnull");
        assertTrue(out.isEmpty(),"isempty");
        assertTrue(out.isBlank(),"isblank");
    }

    public void testNonEmptyID()
    {
        ID out = new ID("007");
        assertEquals(out.value(),"007","value");
        assertFalse(out.isNull(),"isnull");
        assertFalse(out.isEmpty(),"isempty");
        assertFalse(out.isBlank(),"isblank");
    }

    public void testEmptyID()
    {
        ID out = new ID("");
        assertEquals(out.value(),"","value");
        assertFalse(out.isNull(),"isnull");
        assertTrue(out.isEmpty(),"isempty");
        assertTrue(out.isBlank(),"isblank");
    }

    public void testBlankButNotEmptyID()
    {
        ID out = new ID("  ");
        assertEquals(out.value(),"  ","value");
        assertFalse(out.isNull(),"isnull");
        assertFalse(out.isEmpty(),"isempty");
        assertTrue(out.isBlank(),"isblank");
    }

    private void verifyUndefinedFlag(Flag out)
    {
        assertNull(out.value(),"value");
        assertTrue(out.isUndefined(),"isundefined");
        assertTrue(out.on(),"on");
        assertFalse(out.on(false),"on('false')");
        assertTrue(out.on(true),"on('true')");
        assertTrue(out.off(),"off");
        assertFalse(out.off(false),"off('false')");
        assertTrue(out.off(true),"off('true')");
    }

    public void testUndefinedFlag()
    {
        Flag out = new Flag();
        assertNotNull(out.getName(),"name");
        verifyUndefinedFlag(out);
    }

    public void testNamedUndefinedFlag()
    {
        Flag out = new Flag("n/d");
        assertEquals(out.getName(),"n/d","name");
        verifyUndefinedFlag(out);
    }

    private void verifyTrueFlag(Flag out)
    {
        assertNotNull(out.value(),"value");
        assertFalse(out.isUndefined(),"isundefined");
        assertTrue(out.on(),"on");
        assertTrue(out.on(false),"on('false')");
        assertTrue(out.on(true),"on('true')");
        assertFalse(out.off(),"off");
        assertFalse(out.off(true),"off('false')");
        assertFalse(out.off(false),"off('true')");
    }

    public void testTrueFlag()
    {
        Flag out = new Flag(true);
        assertTrue(out.getName().isEmpty(),"name.isempty");
        assertEquals(out.toString(),"=true","toString");
        verifyTrueFlag(out);
    }

    public void testNamedTrueFlag()
    {
        Flag out = new Flag("debug",true);
        assertEquals(out.getName(),"debug","name");
        verifyTrueFlag(out);
    }

    private void verifyFalseFlag(Flag out)
    {
        assertNotNull(out.value(),"value");
        assertFalse(out.isUndefined(),"isundefined");
        assertFalse(out.on(),"on");
        assertFalse(out.on(false),"on('false')");
        assertFalse(out.on(true),"on('true')");
        assertTrue(out.off(),"off");
        assertTrue(out.off(true),"off('false')");
        assertTrue(out.off(false),"off('true')");
    }

    public void testFalseFlag()
    {
        Flag out = new Flag(false);
        assertTrue(out.getName().isEmpty(),"name.isempty");
        verifyFalseFlag(out);
    }

    public void testNamedFalseFlag()
    {
        Flag out = new Flag("scrub-all",false);
        assertEquals(out.getName(),"scrub-all","name");
        assertEquals(out.toString(),"scrub-all=false","toString()");
        verifyFalseFlag(out);
    }

    public void testEquivalentFlags()
    {
        Flag out1 = new Flag("out",true);
        assertFalse(out1.equals(null),"out.equals(null)");
        assertTrue(out1.equals(out1),"out.equals(out)");
        assertFalse(out1.equals(Boolean.TRUE),"out.equals(Boolean.TRUE)");

        Flag out2 = new Flag("out",true);
        Flag out3 = new Flag("out3",true);
        Flag out4 = new Flag("out",false);
        Flag out5 = new Flag(true);

        assertEquals(out1,out2,"out1==out2");
        assertEquals(out2,out1,"out2==out1");
        assertEquals(out1.hashCode(),out2.hashCode(),"hashCode");
        assertFalse(out1.equals(out3),"out1.equals(out3)");
        assertFalse(out1.equals(out4),"out1.equals(out4)");
        assertFalse(out4.equals(out3),"out4.equals(out3)");
        assertFalse(out2.equals(out5),"out2.equals(out5)");
    }
}


/* end-of-MarkerTypesTest.java */
