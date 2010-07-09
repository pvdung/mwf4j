/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.Executor;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Feedback;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.bal.ThrowStatement;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Specialized harness that a primary harness can use to handle async
 * activity separately. Typically the child harness is run from its own
 * thread-of-execution. Useful for things like forks and automatic 
 * (re)joins or parallel flows.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (guarded for continuation, unwinds, &amp; adjustments management)
 * @.group    impl,helper
 **/

public final class ChildHarness extends HarnessSkeleton
{ 
    static class CaughtErrorNotification extends ActionSkeleton implements Adjustment
    {
        CaughtErrorNotification(RuntimeException issue) {
            super("rethrow");
            throwStatement = new ThrowStatement(Action.anonINSTANCE,issue,
                    "Caught issue on child harness "+getId());
        }
        public ControlFlowStatement makeStatement(ControlFlowStatement next) {
            return throwStatement;
        }
        public boolean isTerminal() {
            return true;
        }
        public void configure(ControlFlowStatement statement) {
        }
        private final ControlFlowStatement throwStatement;
    }

    public ChildHarness(Harness parent, ControlFlowStatement first)
    {
        this(parent,first,new RetryDef());
    }

    public ChildHarness(Harness parent, ControlFlowStatement first, RetryDef config)
    {
        super(parent);
        Validate.neitherNull(config,What.CONFIG,first,What.STATEMENT);
        myParent = parent;
        myFirst = first;
        myConfig = config;
    }

    public Activity getOwner()
    {
        return myParent.getOwner();
    }

    public Variables getVariables()
    {
        return myParent.getVariables();
    }

    public Executor getExecutorService()
    {
        return myParent.getExecutorService();
    }


    protected ControlFlowStatement firstStatement()
    {
        return myFirst;
    }

    private void waitOneAdjustmentTurn()
    {
        try {
            Thread.sleep(myConfig.getRetryWait().toMillis());
        } catch(InterruptedException iruptedX) {/*fallthrough*/}
    }

    RuntimeException handleUncaughtError(RuntimeException issue)
    {
        super.handleUncaughtError(issue);

        boolean notified=false;
        RuntimeException returned=null;
        if (myParent.isRunning()) {
            int tryCount = 1+myConfig.getRetryCount();
            Adjustment signal = new CaughtErrorNotification(issue);
            do {
                try {
                    myParent.applyAdjustment(signal);
                    notified=true;
                } catch(IllegalStateException stoppedOrBlockedX) {
                    if (tryCount>1) 
                        waitOneAdjustmentTurn();
                }
            } while (!notified && --tryCount>0);
        }
        if (!notified) {
            Feedback.ForCore.error("Unable to notify parent harness of issue?!",issue);
            returned=issue;//DONT LOSE IT...(though might be ignored anyway)
        }
        return returned;
    }


    private final Harness myParent;
    private ControlFlowStatement myFirst;
    private final RetryDef myConfig;
}


/* end-of-ChildHarness.java */
