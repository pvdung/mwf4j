/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.StatementSkeleton;

/**
 * Statement that just um 'barfs' when run; throws an InternalError.
 * You're allowed to specify a 'barf-message'. Useful for triggering
 * uncaught (non-runtime-exception) handlers.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple (once setup)
 * @.group    impl,helper
 **/

public final class FailStatement extends StatementSkeleton
{
    public FailStatement()
    {
        super(null);
    }

    public FailStatement(String message)
    {
        this();
        setMessage(message);
    }

    public void setMessage(String message)
    {
        myFeedback = Strings.trimToEmpty(message);
    }

    public String getMessage()
    {
        return myFeedback;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        throw new InternalError(myFeedback);
    }

    private String myFeedback = "DEATH TO ALL WHO DARE EAT MY LUNCH!";
}


/* end-of-FailStatement.java */
