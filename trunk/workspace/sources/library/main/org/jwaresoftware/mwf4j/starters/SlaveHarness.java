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
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.bal.ThrowStatement;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Specialized harness that a primary activity can use to launch independent
 * async sub-activities. Typically the slave harness is run from its own
 * thread-of-execution. Useful for things like implementation-only forks  
 * or parallel flows. Any failure occuring on a slave harness results in
 * a failure on the master harness (as if the statement were executing
 * there). We use a terminal adjustment against the master harness.
 * <p/>
 * Usage note: if you setup an MDC initializer for a slave harness, <em>you</em>
 * need to call the initializer's copy method to setup what MDC items are
 * to be copied. The harness cannot know when is the correct time to call
 * the copy. Once the slave harness's run method is triggered (presumably
 * in it's own thread-of-execution), the initializer's paste method will
 * be called.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (same as superclass)
 * @.group    impl,helper
 **/

public final class SlaveHarness extends HarnessSkeleton
{ 
    /**
     * The adjustment that we send to the master harness in the event of
     * an uncaught exception on a slave harness.
     *
     * @since     JWare/MWf4J 1.0.0
     * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
     * @version   @Module_VERSION@
     * @.safety   special (single while constructed, guarded during run)
     * @.group    impl,helper
     **/
    static class CaughtErrorNotification extends ActionSkeleton implements Adjustment
    {
        CaughtErrorNotification(RuntimeException issue) {
            super("rethrow");
            throwStatement = new ThrowStatement(Action.anonINSTANCE,issue,
                    "Caught issue on slave harness "+getId());
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


    public SlaveHarness(Harness master, ControlFlowStatement first)
    {
        this(master,first,new RetryDef());
    }

    public SlaveHarness(Harness master, ControlFlowStatement first, RetryDef config)
    {
        super(master);
        Validate.neitherNull(config,What.CONFIG,first,What.STATEMENT);
        myMaster = master;
        myFirst = first;
        myConfig = config;
    }

    public void setMDCInitializer(MDC.Propagator initializer)
    {
        Validate.notNull(initializer,What.CALLBACK);
        myMDCInitializer = initializer;
    }


    public Executor getExecutorService()
    {
        return myMaster.getExecutorService();
    }

    public Activity getOwner()
    {
        return myMaster.getOwner();
    }

    public Variables getVariables()
    {
        return myMaster.getVariables();
    }


    protected void doEnter()
    {
        myMDCInitializer.paste();//DO *BEFORE* CALLING INHERITED
        super.doEnter();
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
        if (myMaster.isRunning()) {
            int tryCount = 1+myConfig.getRetryCount();
            Adjustment signal = new CaughtErrorNotification(issue);
            do {
                try {
                    myMaster.applyAdjustment(signal);
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


    private final Harness myMaster;
    private ControlFlowStatement myFirst;
    private final RetryDef myConfig;
    private MDC.Propagator myMDCInitializer=MDC.Propagator.nullINSTANCE;
}


/* end-of-SlaveHarness.java */
