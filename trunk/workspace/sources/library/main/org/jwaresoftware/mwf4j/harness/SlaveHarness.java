/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Feedback;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Specialized harness that a primary activity can use to launch dependent
 * async sub-activities from their own threads-of-execution. Any failure
 * occuring on a slave harness results in a failure on the master harness
 * (as if the statement were executing there; we use a terminal adjustment
 * against the master harness).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (same as superclass and single for configuration)
 * @.group    infra,impl,helper
 * @see       org.jwaresoftware.mwf4j.bal.IfCalledMakeStatement IfCalledMakeStatement
 **/

public final class SlaveHarness extends SpawnedHarnessSkeleton
{
    public SlaveHarness(Harness master, ControlFlowStatement first)
    {
        this(master,first,new RetryDef());
    }

    public SlaveHarness(Harness master, ControlFlowStatement first, RetryDef errorNotifyConfig)
    {
        super(master,first);
        Validate.notNull(errorNotifyConfig,What.CONFIG);
        myNotifyConfig = errorNotifyConfig;
    }

    public String typeCN()
    {
        return "slave";
    }

    private void waitOneAdjustmentTurn()
    {
        try {
            Thread.sleep(myNotifyConfig.getRetryWait().toMillis());
        } catch(InterruptedException iruptedX) {/*fallthrough*/}
    }

    RuntimeException handleUncaughtError(RuntimeException issue)
    {
        super.handleUncaughtError(issue);

        boolean notified=false;
        RuntimeException returned=null;
        if (myParent.isRunning()) {
            int tryCount = 1+myNotifyConfig.getRetryCount();
            Adjustment signal = new RethrowErrorAdjustment(issue,this);
            do {
                try {
                    myParent.applyAdjustment(signal);
                    notified=true;
                } catch(IllegalStateException stoppedOrBlockedX) {
                    if (!myParent.isRunning())
                        tryCount = 0;
                    if (tryCount>1) 
                        waitOneAdjustmentTurn();
                }
            } while (!notified && --tryCount>0);
        }
        if (!notified) {
            Feedback.ForCore.error("Slave harness unable to notify parent harness of fatal issue!",issue);
            returned=issue;//DONT LOSE IT...(though might be ignored anyway)
        }
        return returned;
    }


    private final RetryDef myNotifyConfig;
}


/* end-of-SlaveHarness.java */
