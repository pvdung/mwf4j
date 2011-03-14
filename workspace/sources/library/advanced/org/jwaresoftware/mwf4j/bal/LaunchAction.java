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
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Utility action that will launch one or more other independent actions
 * each within an isolated spawned harness. Unlike the {@linkplain ForkAction}
 * a launch is "fire-and-forget"; once the linked actions have been started
 * there is no further tracking by the launch task. To retain access to the
 * spawned harnesses you must supply a futures reference variable;  this
 * variable will be updated with an ordered list of Future objects that
 * control the harnesses. The order of the Future objects match the order
 * of the launched sub-actions.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       ListenAction
 **/

public class LaunchAction extends ActionSkeleton
{
    public LaunchAction()
    {
        this("launch");
    }

    public LaunchAction(String id)
    {
        super(id);
    }

    public void addAction(Action action)
    {
        Validate.notNull(action,What.ACTION);
        myActions.add(action);
    }

    public void setActions(Collection<Action> actions)
    {
        Validate.notNull(actions,What.ACTIONS);
        myActions.clear();
        myActions.addAll(actions);
    }

    public void setMDCPropagator(MDC.Propagator propagator)
    {
        Validate.notNull(propagator,What.CALLBACK);
        myMdcClipboard = propagator;
    }

    public void setLinkSaveRef(Reference ref)
    {
        Validate.notNull(ref,What.REFERENCE);
        myFuturesRef = ref;
    }

    public final void setLinkSaveRef(String varName)
    {
        Validate.notNull(varName,What.REFERENCE);
        myFuturesRef = new Reference(varName);
    }

    public void useForeverHarnessType(boolean flag)
    {
        myForeverFlag = flag;
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next,Fixture environ)
    {
        return new LaunchStatement(next);
    }

    public void configureStatement(ControlFlowStatement statement,Fixture environ)
    {
        Validate.isA(statement,LaunchStatement.class,What.STATEMENT);
        LaunchStatement launch = (LaunchStatement)statement;
        launch.setActions(myActions);
        if (myMdcClipboard!=null) launch.setMDCPropagator(myMdcClipboard);
        if (myFuturesRef!=null) launch.setFuturesRef(myFuturesRef);
        if (myForeverFlag!=null) launch.useForeverHarness(myForeverFlag);
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotEmpty(myActions,What.ACTIONS);
    }

    private Collection<Action> myActions= LocalSystem.newList();
    private MDC.Propagator myMdcClipboard;//OPTIONAL
    private Reference myFuturesRef;//OPTIONAL
    private Boolean myForeverFlag;//OPTIONAL
}


/* end-of-LaunchAction.java */
