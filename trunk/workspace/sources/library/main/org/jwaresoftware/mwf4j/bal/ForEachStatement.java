/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.Iterator;
import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;

/**
 * Control flow statement that calls another statement for each
 * item contained in a supplied collection. A required "cursor" value
 * is updated with each iteration to the collection item returned.
 * Note that a single preset statement is set once and that same 
 * instance is called N-times. An alternative is to set a "body action
 * factory" that is asked to supply a new statement for each iteration.
 * For the default {@linkplain ForEachAction} the "factory" option is 
 * the default because only simply stateless actions can work 
 * repeatedly without a reset of some kind.
 * <p/>
 * Implementation caveat: The collection holding the dataset needs to 
 * exist as long as <em>the entire foreach statement</em> does. This 
 * means the collection may need to be long-lived if each iteration 
 * takes a while to complete. The collection also need to be immutable
 * for the duration of the foreach statement; otherwise, the source
 * iterator can signal an unexpected concurrent modification error. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class ForEachStatement extends BALStatement implements Unwindable, Resettable
{
    static Callable<Collection<?>> GivebackEMPTY_LIST= new Callable<Collection<?>>() {
        public Collection<?> call() {
            return Empties.LIST;
        }
    };

    public ForEachStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myUnwindSupport = new ReentrantSupport(getOwner(),this);
    }

    public void setGetter(Callable<Collection<?>> getter)
    {
        Validate.notNull(getter,What.CALLBACK);
        myGetter = getter;
    }

    public void setCursorKey(String key)
    {
        Validate.notNull(key,What.CURSOR);
        myCursorKey = key;
    }

    public void setCursorStoreType(StoreType type)
    {
        Validate.notNull(type,What.TYPE);
        myCursorStoreType = type;
    }

    public void setBody(ControlFlowStatement body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    public void setBodyFactory(final Action factory) 
    {
        Validate.notNull(factory,What.FACTORY_METHOD);
        myBodyFactory = factory;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next=null;
        if (myWorker==null) {
            try {
                MDC.pshHarness(this,harness);
                myWorker = myGetter.call().iterator();
            } catch(Exception getX) {
                next= new ThrowStatement(getOwner(),getX,"Unable to get foreach data iterator");
                resetThis();
            } finally {
                MDC.popHarness(this,harness);
            }
        }
        if (next==null) {
            if (myWorker.hasNext()) {
                myUnwindSupport.loop(harness);
                Object data = myWorker.next();
                next = getIterationOfBody(data,harness);
            } else {
                next = next(); 
                unwindThis(harness,myUnwindSupport.finished(harness));
            }
        }
        return next;
    }

    private void resetThis()
    {
        myWorker=null;
        myGetter= GivebackEMPTY_LIST;
        myBody=null;
        myBodyFactory=null;
        myCursorKey=null;
        myCursorStoreType= BAL.getCursorStoreType();
        myUnwindSupport.reset(this);
    }

    public void reconfigure()
    {
        reset();
        super.reconfigure();
        verifyReady();
    }

    private void unwindThis(Harness harness, boolean clrData)
    {
        if (clrData) BALHelper.clrData(myCursorKey,myCursorStoreType,harness);
        resetThis();
    }

    public void unwind(Harness harness)
    {
        Validate.stateNotNull(myCursorKey,What.CURSOR);
        unwindThis(harness,true);
    }

    public void reset()
    {
        resetThis();
    }

    protected ControlFlowStatement getIterationOfBody(Object data, Harness harness)
    {
        BALHelper.putData(myCursorKey,data,myCursorStoreType,harness);//NB: *before* factory call!
        ControlFlowStatement bodyContinuation = myBody;
        if (myBodyFactory!=null) {
            try {
                MDC.pshHarness(this,harness);
                bodyContinuation = myBodyFactory.makeStatement(this);
            } finally {
                MDC.popHarness(this,harness);
            }
        }
        return bodyContinuation;
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateIsTrue(myBody!=null || myBodyFactory!=null,
                "body or body-factory has been defined");
        Validate.fieldNotNull(myCursorKey,What.KEY);
    }

 
    private Callable<Collection<?>> myGetter= GivebackEMPTY_LIST;
    private Iterator<?> myWorker;
    private ControlFlowStatement myBody;
    private Action myBodyFactory;
    private String myCursorKey;
    private StoreType myCursorStoreType= BAL.getCursorStoreType();
    private ReentrantSupport myUnwindSupport;
}


/* end-of-ForEachStatement.java */
