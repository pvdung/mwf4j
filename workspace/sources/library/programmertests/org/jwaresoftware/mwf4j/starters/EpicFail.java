/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;

/**
 * Test action that does nothing but return a fail statement. Used
 * for "should not get here" situations.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    helper,test
 **/

public final class EpicFail extends ActionSkeleton
{
    public EpicFail()
    {
        super("epicfail");
    }

    public void configure(ControlFlowStatement own)
    {
        Validate.isTrue(own instanceof FailStatement,"statement kindof FailStatement");
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        FailStatement thrower = new FailStatement();
        thrower.reconfigure();
        return thrower;
    }
}


/* end-of-EpicFail.java */
