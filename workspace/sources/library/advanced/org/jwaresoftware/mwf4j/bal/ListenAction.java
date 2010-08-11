/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Action that sits-n-waits (blocks) for payloads from a predefined
 * listener (via the Callable interface). The payload is then matched
 * to an appropriate action which is itself handed off by the listener
 * to a specified worker harness (which is typically a
 * {@linkplain org.jwaresoftware.mwf4j.harness.ForeverHarness forever
 * harness} running in its own thread-of-execution.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for makeStatement)
 * @.group    infra,impl
 * @see       LaunchAction
 **/

public class ListenAction<T> extends BALProtectorAction
{
    public ListenAction()
    {
        this("listen");
    }

    public ListenAction(String id)
    {
        super(id);
    }

    public void setListener(Callable<T> listener)
    {
        Validate.notNull(listener,What.CALLBACK);
        myListener = listener;
    }

    public void setLookupService(ActionLookupMethod<T> service)
    {
        Validate.notNull(service,What.SERVICE);
        myLookupService = service;
    }

    public void setHandOffHarness(Harness harness)
    {
        Validate.notNull(harness,What.HARNESS);
        myHandoffHarness = harness;
    }

    public void setBreakSupport(Action breakAction)
    {
        Validate.notNull(breakAction,What.ACTION);
        myBreakAction = breakAction;
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        verifyReady();
        return finish(new ListenStatement<T>(this,next));
    }

    @SuppressWarnings("unchecked")
    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof ListenStatement<?>,"statement kindof listen");
        ListenStatement<T> listen = (ListenStatement<T>)statement;
        listen.setListener(myListener);
        listen.setLookupService(myLookupService);
        listen.copyFrom(myProtectSupport);
        if (myBreakAction!=null)
            listen.setBreakSupport(myBreakAction);
        if (myHandoffHarness!=null)
            listen.setWorkHarness(myHandoffHarness);
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myListener,What.GET_METHOD);
        Validate.stateNotNull(myLookupService,What.FACTORY_METHOD);
    }


    private Callable<T> myListener;
    private ActionLookupMethod<T> myLookupService;
    private Action myBreakAction;//OPTIONAL
    private Harness myHandoffHarness;//OPTIONAL
}


/* end-of-ListenAction.java */
