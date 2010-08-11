/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.List;
import  java.util.concurrent.ExecutorService;
import  java.util.concurrent.Future;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.harness.SpawnedHarness;
import  org.jwaresoftware.mwf4j.harness.SpawnedHarnessFactory;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Statement that launches one or more independent statements into separate
 * spawned harnesses. Unlike a {@linkplain ForkStatement} a launch statement is
 * "fire-and-forget"; once the actions have been launched (via the harness's
 * executor service), this statement continues to the next assigned statement
 * unconditionally. By default, a launch statement creates a dependent slave
 * harness (any failure on this harness, though independent, is reflected back
 * to the parent or master harness). You can alter this default by setting
 * the statement's {@linkplain #setForever(boolean) forever flag}. When enabled
 * this flag causes the launch statement to create only independent 
 * {@linkplain Forever forever harnesses} that sit "forever" listening for 
 * continuations after the initial action has completed. Unhandled errors on 
 * a forever harness are <em>not</em> propagated back to the parent.
 * <p/> 
 * Usage notes: you have the option to define a variable to hold the Future
 * references created by the launch statement. You can use this variable to 
 * shutdown (gracefully) the launched harnesses later. Note that the branch
 * action's statement factory method (makeStatement) is run <em>from the
 * branched thread-of-execution</em> not this launch statement's thread. So
 * any MDC context that your action needs should be copied via an MDC
 * propagator  object; see {@linkplain #setMDCPropagator}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       ListenStatement
 * @see       SpawnedHarness
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

    public void setMDCPropagator(MDC.Propagator propagator)
    {
        Validate.notNull(propagator,What.CALLBACK);
        myMdcClipboard = propagator;
    }

    public void setFuturesRef(Reference ref)
    {
        myFuturesRef = ref;
    }

    public void useForeverHarness(boolean flag)
    {
        myForeverFlag = flag;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotEmpty(myActions,"actions");
    }

    protected boolean isForever()
    {
        return myForeverFlag;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ExecutorService runService = harness.getExecutorService();
        if (myMdcClipboard!=null) {
            myMdcClipboard.copy();
        }
        List<Future<?>> futures = LocalSystem.newList();
        for (Action branch:myActions) {
            Harness branchHarness = newBranchHarness(harness,branch);
            futures.add(runService.submit(branchHarness));
        }
        if (myFuturesRef!=null) {
            BALHelper.putData(myFuturesRef,futures,harness);
        }
        return next();
    }

    private void resetThis()
    {
        myActions = null;
        myMdcClipboard = null;
        myFuturesRef = null;
    }

    public void reconfigure()
    {
        resetThis();
        super.reconfigure();
        verifyReady();
        initHarnessFactory();
    }

    private void initHarnessFactory()
    {
        if (isForever()) {
            myHarnessFactory = SpawnedHarnessFactory.Forever.INSTANCE;
        } else {
            myHarnessFactory = SpawnedHarnessFactory.Default.INSTANCE;
        }
    }

    protected Harness newBranchHarness(Harness master, Action branch)
    {
        ControlFlowStatement first = new IfCalledMakeStatement(branch,new EndStatement());
        SpawnedHarness branchHarness = myHarnessFactory.newHarness(master,first,RetryDef.newNoRetry());
        if (myMdcClipboard!=null) {
            branchHarness.setMDCInitializer(myMdcClipboard);
        }
        return branchHarness;
    }


    private static SpawnedHarnessFactory myHarnessFactory;
    private Collection<Action> myActions;
    private MDC.Propagator myMdcClipboard;//OPTIONAL
    private Reference myFuturesRef;//OPTIONAL:key+storeType
    private boolean myForeverFlag;//FALSE=>slave
}


/* end-of-LaunchStatement.java */
