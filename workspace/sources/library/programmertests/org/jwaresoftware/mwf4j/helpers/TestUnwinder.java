/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  static org.testng.Assert.assertFalse;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Unwindable;

/**
 * Unwindable that marks the harness with stamp that the unwind has executed. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    test,helper
 **/

public final class TestUnwinder implements Unwindable 
{
    public TestUnwinder(String id) 
    {
        Validate.notNull(id,"id");
        myId = id;
    }

    public void unwind(Harness harness) 
    {
        assertFalse(unwound(),myId+".unwound");//NB:ONCE!
        TestFixture.addUnwound(myId);
        harness.getVariables().put(myId+".unwound", Boolean.TRUE);
        myFlag=true;
    }

    public boolean unwound()
    {
        return myFlag;
    }

    private final String myId;
    private boolean myFlag;//Latch
}


/* end-of-TestUnwinder.java */
