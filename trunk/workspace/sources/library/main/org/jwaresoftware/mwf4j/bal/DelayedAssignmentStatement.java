/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.RunnableFuture;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.LongLivedTraceSupport;

/**
 * Control flow statement that first queues up its worker for execution
 * (via Executor provider) and then waits for the worker to either 
 * fail, be cancelled, or complete. The worker's payload is stashed by
 * the assigned put-method object (inherited).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       AsyncCallAction
 * @see       Harness#getExecutorService()
 **/

public class DelayedAssignmentStatement<T> extends AssignmentStatement<T>
{
    public DelayedAssignmentStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        initBreadcrumbs(new LongLivedTraceSupport(new TraceLink()));
    }


    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next = null;
        try {
            if (!myLaunchedFlag) {
                harness.getExecutorService().execute(getDelayedGetter());
                myLaunchedFlag = true;
                next = next();
                harness.addContinuation(this);
            } else {
                RunnableFuture<T> getter = getDelayedGetter();
                if (!getter.isDone()) {
                    next = nextIteration(harness);
                } else {
                    if (!getter.isCancelled()) {
                        T payload = getter.get();
                        consumePayload(payload,harness);
                    }
                    next = new EndStatement();
                }
            }
        } catch(Exception anyX) {
            breadcrumbs().caught(anyX);
            next = new ThrowStatement(getOwner(),anyX);
        }
        return next;
    }


    private void resetThis()
    {
        myLaunchedFlag=false;
    }


    public void reconfigure()
    {
        resetThis();
        super.reconfigure();
        verifyReady();
    }


    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateIsTrue(getGetter() instanceof RunnableFuture<?>, 
            "get-method is a runnable future");
    }


    protected ControlFlowStatement nextIteration(Harness harness)
    {
        return this;
    }


    @SuppressWarnings("unchecked")
    private RunnableFuture<T> getDelayedGetter()
    {
        return (RunnableFuture<T>)getGetter();
    }


    protected StringBuilder addToString(StringBuilder sb) 
    {
        char waiting = myLaunchedFlag ? 'Y' : 'N';
        return super.addToString(sb).append("|wait=").append(waiting);
    }


    private boolean myLaunchedFlag=false;//latch: set on 1st call into 'run'
}


/* end-of-DelayedAssignmentStatement.java */
