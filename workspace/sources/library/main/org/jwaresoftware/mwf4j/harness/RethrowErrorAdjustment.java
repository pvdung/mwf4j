/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Adjustment that signals an exception against a running harness. Useful to 
 * pass through exceptions thrown on a slave harness back to the master
 * harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/
public final class RethrowErrorAdjustment extends ActionSkeleton implements Adjustment
{
    public RethrowErrorAdjustment(RuntimeException issue, Harness harness)
    {
        super("rethrow");
        myRethrowStatement = new RethrowStatement(issue,harness);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        return myRethrowStatement;
    }

    public boolean isTerminal() 
    {
        return true;
    }

    public void configure(ControlFlowStatement statement) 
    {
        //nothing to configure (done at construction)
    }

    private final ControlFlowStatement myRethrowStatement;
}

/* end-of-RethrowErrorAdjustment.java */
