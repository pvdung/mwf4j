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
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Utility action that will launch one or more other independent actions
 * each within an isolated slave harness. Unlike the {@linkplain ForkAction}
 * a launch is "fire-and-forget"; once the linked actions have been started
 * there is no further tracking by the launch task.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
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

    public void setMDCPropagtor(MDC.Propagator propagator)
    {
        Validate.notNull(propagator,What.CALLBACK);
        myMdcClipboard = propagator;
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        verifyReady();
        return finish(new LaunchStatement(this,next));
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof LaunchStatement,"statement kindof launch");
        LaunchStatement launch = (LaunchStatement)statement;
        launch.setActions(myActions);
        if (myMdcClipboard!=null) launch.setMDCPropagtor(myMdcClipboard);
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotEmpty(myActions,What.ACTIONS);
    }

    private Collection<Action> myActions= LocalSystem.newList();
    private MDC.Propagator myMdcClipboard;//OPTIONAL
}


/* end-of-LaunchAction.java */
