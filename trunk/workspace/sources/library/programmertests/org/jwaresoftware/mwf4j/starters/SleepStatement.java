/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Statement that blocks its parent thread for specified amount of time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 * @see       SleepAction
 **/

public class SleepStatement extends LiteLiteStatementSkeleton
{
    public SleepStatement(String id, Action owner, ControlFlowStatement next)
    {
        super(id,owner,next);
    }

    public void setMillis(long millis)
    {
        myMillis = millis;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        assert myMillis > 0L;
        try {
            Thread.sleep(myMillis);
        } catch(InterruptedException iruptedX) {
            breadcrumbs().caught(iruptedX);
        }
        return next();
    }

    private long myMillis;
}


/* end-of-SleepStatement.java */
