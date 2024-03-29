/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.Future;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.helpers.CalledFuture;
import  org.jwaresoftware.mwf4j.helpers.CalledRunnable;

/**
 * Action that calls a user-supplied closure like a Callable, Runnable,
 * or Future. The action's generated statement combines the "call" as 
 * well as an optional "result assignment" step (indicated by setting the
 * {@linkplain #setToKey(String) result key}). Note that for Futures
 * this action uses the BLOCKING 'get' method, so for long running
 * jobs, you should either use the {@linkplain AsyncCallAction async
 * call action} instead or run this action's enclosing action from 
 * its own thread. This action must be parameterized on the result
 * type; for example: CallAction&lt;MyRecordType&gt;. For direct copying
 * between properties or variable values, use the {@linkplain AssignAction
 * assign action} instead.
 * <p/>
 * To get the action to store the closure's result, you can specify
 * a result key and a {@linkplain StoreType store-type}. Typically results
 * are stored to the controlling harness's variables map; read 
 * {@linkplain SavebackAction saveback} for more information.
 * <p/>
 * Note that the assigned callable worker can be self-referential; 
 * namely it can call back to a method within the call action (subclass).
 * For instance, you can create a call action subclass that automatically
 * sets its own worker to a loop back to a new "this" method that
 * gets an authorization credential from the caller.
 * 
 * <pre>
 *   public class AskAction extends CallAction&lt;String&gt; 
 *   {
 *     public AskAction(final String message, String updateVar) {
 *       super("ask", 
 *             new Callable&lt;String&gt;() {
 *               public String call() {
 *                  return askUser(message);
 *               }
 *             },
 *             updateVar,
 *             StoreType.DATAMAP);
 *     }
 *     protected String askUser(String message) {
 *       ...
 *     }
 *   ...
 *   AskAction ask = new AskAction("Override data checks?","data.checkFlag");
 * </pre>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for buildStatement)
 * @.group    infra,impl
 * @see       AsyncCallAction
 * @see       org.jwaresoftware.mwf4j.assign.Giveback Givebacks
 **/

public class CallAction<T> extends SavebackAction<T>
{
    public CallAction(String id, Callable<? extends T> getmethod, String resultKey, StoreType resultStoreType)
    {
        super(id,resultKey,resultStoreType);
        setGetter(getmethod);
    }

    public CallAction(String id, Reference resultKey)
    {
        super(id,resultKey);
    }

    public CallAction(String id)
    {
        super(id);
    }

    public CallAction()
    {
        this("call");
    }

    public CallAction(String id, final Runnable voidmethod)
    {
        super(id);
        myGetter = new CalledRunnable<T>(voidmethod);
    }

    public void setGetter(Callable<? extends T> getmethod)
    {
        Validate.notNull(getmethod,What.CALLBACK);
        myGetter = getmethod;
    }

    public void setGetter(final Future<T> getmethod)
    {
        Validate.notNull(getmethod,What.CALLBACK);
        myGetter = new CalledFuture<T>(getmethod);
    }

    public void setGetterRequiredReturnType(Class<? extends T> ofKind)
    {
        Validate.notNull(ofKind,What.CLASS_TYPE);
        myPayloadKind = ofKind;
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.fieldNotNull(myGetter,What.GET_METHOD);
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new AssignmentStatement<T>(next);
    }

    @SuppressWarnings("unchecked")
    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        super.configureStatement(statement,environ);
        AssignmentStatement<T> assignment = (AssignmentStatement<T>)statement;
        assignment.setGetter(copyMember(myGetter));
        assignment.setGetterRequiredReturnType(myPayloadKind);
    }


    private Callable<? extends T> myGetter;//REQUIRED
    private Class<? extends T> myPayloadKind;//OPTIONAL
}


/* end-of-CallAction.java */
