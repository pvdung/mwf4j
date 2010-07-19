/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.concurrent.Executor;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;
import  org.jwaresoftware.mwf4j.starters.SlaveHarness;

/**
 * Statement that launches one or more independent statements into separate
 * slave harness. Unlike a {@linkplain ForkStatement} a launch statement is
 * "fire-and-forget"; once the actions have been launched (via the harness's
 * executor service), this statement continues to the next assigned statement
 * unconditionally.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class LaunchStatement extends BALStatement
{
    public LaunchStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        resetThis();
    }

    public void setActions(Collection<Action> actions)
    {
        Validate.notNull(actions,What.ACTIONS);
        myActions = actions;
    }

    public void setMDCPropagtor(MDC.Propagator propagator)
    {
        Validate.notNull(propagator,What.CALLBACK);
        myMdcClipboard = propagator;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotEmpty(myActions,"actions");
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        Executor runService = harness.getExecutorService();
        if (myMdcClipboard!=null) {
            myMdcClipboard.copy();
        }
        ControlFlowStatement end= new EndStatement();
        for (Action branch:myActions) {
            SlaveHarness branchHarness = new SlaveHarness(harness,branch.makeStatement(end),RetryDef.newNoRetry());
            if (myMdcClipboard!=null) {
                branchHarness.setMDCInitializer(myMdcClipboard);
            }
            runService.execute(branchHarness);

        }
        return next();
    }

    private void resetThis()
    {
        myActions = null;
        myMdcClipboard = null;
    }

    public void reconfigure()
    {
        resetThis();
        super.reconfigure();
        verifyReady();
    }

    private Collection<Action> myActions;
    private MDC.Propagator myMdcClipboard;//OPTIONAL
}


/* end-of-LaunchStatement.java */
