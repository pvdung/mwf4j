/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Adjustment that signals an exception against a running harness. Useful to 
 * pass through exceptions thrown on a slave harness back to the master
 * harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 * @see       RethrowStatement
 **/

public final class RethrowErrorAdjustment extends ActionSkeleton implements Adjustment
{
    public RethrowErrorAdjustment(RuntimeException issue, Harness harness)
    {
        super("rethrow");
        myRethrowStatement = new RethrowStatement(issue,harness);
    }

    public boolean isTerminal() 
    {
        return true;
    }

    public ControlFlowStatement buildStatement(ControlFlowStatement next, Fixture environ)
    {
        return myRethrowStatement;
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        throw new UnsupportedOperationException("rethrowAdjustment.configure");
    }

    private final ControlFlowStatement myRethrowStatement;
}

/* end-of-RethrowErrorAdjustment.java */
