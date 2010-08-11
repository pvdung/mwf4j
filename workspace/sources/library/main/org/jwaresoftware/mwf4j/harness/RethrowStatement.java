/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.gestalt.Effect;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Rethrow statement executed on depended-on harness (as put there by a 
 * dependent harness's adjustment). Note that we don't embed this activity
 * into the adjustment itself to ensure it gets executed by harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

final class RethrowStatement implements ControlFlowStatement
{
    RethrowStatement(RuntimeException issue, Harness harness)
    {
        theRethrown = issue;
        mySummary = "From rethrow: caught runtime issue on slave harness "+harness.getName();
    }

    public ControlFlowStatement run(Harness harness)
    {
        harness.getIssueHandler().problemOccured(mySummary,Effect.ABORT,theRethrown);
        throw theRethrown;
    }

    public boolean isTerminal() 
    {
        return false;
    }

    public Action getOwner()
    {
        return Action.anonINSTANCE;
    }

    public boolean isAnonymous()
    {
        return true;
    }

    public ControlFlowStatement next()
    {
        return ControlFlowStatement.nullINSTANCE;
    }

    public void reconfigure()
    {
        //nothing
    }

    private final RuntimeException theRethrown;
    private final String mySummary;
}


/* end-of-RethrowStatement.java */
