/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.Future;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.PutMethod;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.helpers.ClosureException;

/**
 * Starting point for application statements that actually do
 * something with data generated by other application work statements.
 * To use this class "as-is", you have to supply two strategy helper
 * objects: the "get-method" that retrieves the data (this method
 * can be a closure like a Callable or Future, or a direct 
 * "lump-o-data", aka payload); and the "set-method" that stores the
 * data. We provide several pre-canned set-methods in the form of
 * Saveback* helpers to save data to the harness, system properties,
 * the MDC, etc. To create a 'void' assignment that basically calls
 * a closure only, set the statement's put-method to a saveback
 * discard instance.
 * <p/>
 * To create a custom statement that gets/sets its own way, you must
 * override both {@linkplain #getPayload} and {@linkplain #consumePayload}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       AssignAction
 * @see       CallAction
 * @see       AsyncCallAction
 * @see       org.jwaresoftware.mwf4j.assign.Giveback Givebacks
 * @.impl     Must work out-of-box for both Callable and Future
 **/

public class AssignmentStatement<T> extends BALStatement implements Resettable
{
    public AssignmentStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    public AssignmentStatement(Action owner)
    {
        this(owner,new EndStatement(owner));
    }

    public void setToKey(String key)
    {
        Validate.notNull(key,What.KEY);
        myToKey = key;
    }

    protected final String getToKey()
    {
        return myToKey;
    }

    public void setGetter(Callable<T> getmethod)
    {
        Validate.notNull(getmethod,What.GET_METHOD);
        myGetter = getmethod;
    }

    public void setGetter(Future<T> getmethod)
    {
        Validate.notNull(getmethod,What.GET_METHOD);
        myGetter = getmethod;
    }

    public final void setPayload(final T payload)
    {
        setGetter(new GivebackValue<T>(payload));
    }

    protected final Object getGetter()
    {
        return myGetter;
    }

    public void setPutter(PutMethod<T> putmethod)
    {
        Validate.notNull(putmethod,What.SET_METHOD);
        mySetter = putmethod;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        try {
            consumePayload(getPayload(harness),harness);
        } catch(RuntimeException rtX) {
            breadcrumbs().caught(rtX);
            throw rtX;
        } catch(Exception anyX) {
            breadcrumbs().caught(anyX);
            throw new ClosureException("Unable to complete assignment '"+getWhatId()+"'",anyX);
        }
        return next();
    }

    protected void consumePayload(T payload, Harness harness)
    {
        Validate.fieldNotNull(mySetter,What.SET_METHOD);
        String underKey = getToKey();
        if (payload!=null) {
            if (!mySetter.put(underKey, payload)) {
                breadcrumbs().write("Unable to complete 'put' of assignment!");
            }
        } else {
            if (!mySetter.putNull(underKey)) {
                breadcrumbs().write("Unable to complete 'putNull' of assignment!");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private T getPayload(Harness harness) throws Exception
    {
        Validate.fieldNotNull(myGetter,What.GET_METHOD);
        T payload;
        try {
            MDC.pshHarness(this,harness);
            if (myGetter instanceof Callable) {
                payload = ((Callable<T>)myGetter).call();
            } else {
                payload = ((Future<T>)myGetter).get();//Blocks!
            }
        } finally {
            MDC.popHarness(this,harness);
        }
        return payload;//NB: can be NULL legally!
    }

    protected StringBuilder addToString(StringBuilder sb) 
    {
        sb = super.addToString(sb);
        if (myToKey!=null) {
            sb.append("|to=").append(myToKey);
        }
        return sb;
    }

    private void resetThis()
    {
        myToKey = null;
        myGetter = null;
        mySetter = null;
    }

    public void reset()
    {
        resetThis();
    }

    public void reconfigure()
    {
        reset();
        super.reconfigure();
        verifyReady();
    }


    private String myToKey;
    private Object myGetter;//OPTIONAL but MUST override getPayload to ignore!
    private PutMethod<T> mySetter;//OPTIONAL but MUST override consumePayload to ignore!
}


/* end-of-AssignmentStatement.java */
