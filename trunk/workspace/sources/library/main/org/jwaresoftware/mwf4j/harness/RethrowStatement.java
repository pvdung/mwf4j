/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.gestalt.Effect;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Signal;

/**
 * Rethrow statement executed on depended-on harness (as put there by a 
 * dependent harness's adjustment). Note that we don't embed this activity
 * into the adjustment itself to ensure it gets executed by harness. A
 * single rethrow statement instance can be re-run any number of times. Its
 * information (exception, message, etc&#46;) is fixed ONCE at construction
 * time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 * @see       RethrowErrorAdjustment
 **/

final class RethrowStatement implements ControlFlowStatement, Signal
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

    public String getWhatId()
    {
        return "rethrow";
    }

    public boolean isTerminal() 
    {
        return false;
    }

    public boolean isAnonymous()
    {
        return true;
    }

    public ControlFlowStatement next()
    {
        return ControlFlowStatement.nullINSTANCE;
    }

    public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        //nothing; fixed at construction time
    }

    public Exception getCause()
    {
        return theRethrown;
    }

    public int getPosition()
    {
        return NO_SIGNAL_POSITION;
    }

    private final RuntimeException theRethrown;
    private final String mySummary;
}


/* end-of-RethrowStatement.java */
