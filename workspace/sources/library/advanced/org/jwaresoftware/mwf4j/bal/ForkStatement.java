/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.Executor;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import org.jwaresoftware.mwf4j.harness.SlaveHarness;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Statement that forks one or more independent statements and optionally
 * waits for one or all of the branched statements to complete. It's possible
 * to create a fork that acts like a "fire-and-forget" operation by setting
 * the join type to {@linkplain JoinType#NONE}. Note that this statement
 * automatically creates a join continuation if needed; the owning fork
 * action does not need to handle both forks and join configuration!
 * <p/>
 * If you setup a separate join action (to be run before the 'normal' 
 * continuation as passed by the harness), this action will still get triggered
 * even if you're setup the fork as a fire-and-forget. The harness will
 * run the body after this fork returns (and potentially before any of the
 * branched actions complete, or even start). 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       LaunchStatement
 **/

public class ForkStatement extends BALProtectorStatement
{
    public ForkStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    public void setBranches(Collection<Action> branches)
    {
        Validate.notNull(branches,What.ACTIONS);
        myBranches = branches;
    }

    public void setJoinType(JoinType mode)
    {
        Validate.notNull(mode,What.CRITERIA);
        myJoinInstr = mode;
    }

    public void setMDCPropagtor(MDC.Propagator propagator)
    {
        Validate.notNull(propagator,What.CALLBACK);
        myMdcClipboard = propagator;
    }

    public void setJoinBreakSupport(RetryDef retryDef, Action notifyAction)
    {
        Validate.notNull(retryDef,What.CRITERIA);
        myTimeoutRetries = retryDef;
        Validate.isFalse(retryDef.getRetryWait().isUndefined(),"timeout undefined");
        myBreakNotify = notifyAction;
    }

    public void setJoinBody(Action body)
    {
        Validate.notNull(body,What.BODY);
        myJoinBody = body;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        CountDownLatch barrier = makeBarrier();
        ControlFlowStatement next = makeJoin(barrier,harness);
        Executor runService = harness.getExecutorService();
        final String myId = getWhatId();

        if (myMdcClipboard!=null) {
            myMdcClipboard.copy();
        }
        for (Action branch:myBranches) {
            NotifyJoinStatement notifyJoin = new NotifyJoinStatement(branch,myId,barrier);
            notifyJoin.verifyReady();

            SlaveHarness branchHarness = new SlaveHarness(harness,branch.makeStatement(notifyJoin));
            if (myMdcClipboard!=null) {
                branchHarness.setMDCInitializer(myMdcClipboard);
            }
            runService.execute(branchHarness);
        }//foreach-branch

        return next;
    }

    private CountDownLatch makeBarrier()
    {
        final int numParticipants = myBranches.size();
        CountDownLatch barrier;
        switch(myJoinInstr) {
            case ALL: {
                barrier = new CountDownLatch(numParticipants);
                break;
            }
            case ANY: {
                barrier = new CountDownLatch(1); 
                break;
            }
            default: { //NONE 
                barrier = new CountDownLatch(1);
                barrier.countDown();//FLIP IT => NO WAITING!
            }
        }
        return barrier;
    }

    private ControlFlowStatement makeJoin(CountDownLatch barrier, Harness harness)
    {
        ControlFlowStatement postJoinContinuation = next();
        if (isNoWait()) {
            return BALHelper.makeInstanceOfBody(this,harness,postJoinContinuation,myJoinBody);
        }
        JoinStatement join = new JoinStatement(getOwner(),postJoinContinuation);
        join.copyFrom(this);
        join.setBarrier(barrier);
        if (myTimeoutRetries!=null) join.setBreakSupport(myTimeoutRetries,myBreakNotify);
        if (myJoinBody!=null) join.setBody(myJoinBody);
        join.verifyReady();
        return join;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotEmpty(myBranches,"participants");
    }

    private boolean isNoWait()
    {
        return JoinType.NONE.equals(myJoinInstr);
    }


    private Collection<Action> myBranches;
    private JoinType myJoinInstr = JoinType.ALL;
    private RetryDef myTimeoutRetries;//OPTIONAL
    private Action myBreakNotify;//OPTIONAL
    private Action myJoinBody;//OPTIONAL
    private MDC.Propagator myMdcClipboard;//OPTIONAL
}


/* end-of-ForkStatement.java */
