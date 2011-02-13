/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.FutureTask;
import  java.util.concurrent.RunnableFuture;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Action that runs a user-supplied closure from a separate
 * thread-of-execution <em>at a later time</em>. You can also
 * stash the closure's results in the harness's data map 
 * or configuration by setting a {@linkplain #setToKey(String)
 * result key}. Note that the existence of a key triggers the 
 * attempt to store the results-- <em>even if that result is 
 * NULL</em>.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for makeStatement)
 * @.group    infra,impl
 * @see       CallAction
 * @see       DelayedAssignmentStatement
 * @see       org.jwaresoftware.mwf4j.assign.Giveback Givebacks
 **/

public class AsyncCallAction<T> extends SavebackAction<T>
{
    public AsyncCallAction(String id, RunnableFuture<? extends T> worker, String resultKey)
    {
        super(id,resultKey,BAL.getDataStoreType());
        setWorker(worker);
    }

    public AsyncCallAction()
    {
        this("asynccall");
    }

    public AsyncCallAction(String id)
    {
        super(id);
    }

    public void setWorker(final RunnableFuture<? extends T> worker)
    {
        Validate.notNull(worker,What.CALLBACK);
        myWorker = worker;
    }

    public void setWorker(final Callable<T> worker)
    {
        Validate.notNull(worker,What.CALLBACK);
        myWorker = new FutureTask<T>(worker);
    }

    protected void verifyReady() 
    {
        super.verifyReady();
        Validate.fieldNotNull(myWorker,What.GET_METHOD);
    }

    @SuppressWarnings("unchecked")
    public void configure(ControlFlowStatement statement)
    {
        super.configure(statement);
        AssignmentStatement<T> assignment = (AssignmentStatement<T>)statement;
        assignment.setGetter(myWorker);
    }

    protected AssignmentStatement<T> newAssignmentStatement(ControlFlowStatement next)
    {
        DelayedAssignmentStatement<T> assignment = new DelayedAssignmentStatement<T>(this,next);
        return assignment;
    }

    private RunnableFuture<? extends T> myWorker;//REQUIRED
}


/* end-of-AsyncCallAction.java */
