/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Action that will initiate execution of N other actions (branches) in their own
 * threads-of-execution and wait for one or all of them to complete. You 
 * can also setup a fork to fire-and-forget (no waiting for <em>any</em>
 * of the branched actions to complete) although that flow might be better
 * implemented using a single {@linkplain LaunchAction}.
 * <p/>
 * You can also specify a join action that is executed after the join
 * point has been met (one, all, none of branches completed) but <em>before</em>
 * the action's iteration-assigned continuation.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       ForkStatement
 * @see       JoinType
 * @see       JoinStatement
 **/

public class ForkAction extends BALProtectorAction
{
    public ForkAction()
    {
        this("fork");
    }

    public ForkAction(String id)
    {
        super(id);
    }

    public void addBranch(Action action)
    {
        Validate.notNull(action,What.ACTION);
        myBranches.add(action);
    }

    public void setBranches(Collection<Action> branches)
    {
        Validate.notNull(branches,What.ACTIONS);
        myBranches.clear();
        myBranches.addAll(branches);
    }

    public void setJoinType(JoinType type)
    {
        Validate.notNull(type,What.CRITERIA);
        myJoinType = type;
    }

    public void setMDCPropagtor(MDC.Propagator propagator)
    {
        Validate.notNull(propagator,What.CALLBACK);
        myMdcClipboard = propagator;
    }

    public void setJoinAction(Action body)
    {
        Validate.notNull(body,What.BODY);
        myJoinBody = body;
    }

    public void setJoinBreakSupport(RetryDef retryDef, Action notifyAction)
    {
        Validate.notNull(retryDef,What.CRITERIA);
        myRetryDef = retryDef;
        myBreakNotify = notifyAction;
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        if (statement instanceof JoinStatement) return;//I am owner for these too!
        Validate.isTrue(statement instanceof ForkStatement,"statement kindof fork");
        ForkStatement fork = (ForkStatement)statement;
        fork.setBranches(myBranches);
        fork.setJoinType(myJoinType);
        if (myRetryDef!=null) fork.setJoinBreakSupport(myRetryDef,myBreakNotify);
        if (myJoinBody!=null) fork.setJoinBody(myJoinBody);
        if (myMdcClipboard!=null) fork.setMDCPropagtor(myMdcClipboard);
        fork.copyFrom(myProtectSupport);
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new ForkStatement(next);
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotEmpty(myBranches,What.ACTIONS);
    }

    private Collection<Action> myBranches= LocalSystem.newList();
    private JoinType myJoinType = JoinType.ALL;
    private Action myJoinBody;//OPTIONAL
    private MDC.Propagator myMdcClipboard;//OPTIONAL
    private RetryDef myRetryDef;
    private Action myBreakNotify;
}


/* end-of-ForkAction.java */
