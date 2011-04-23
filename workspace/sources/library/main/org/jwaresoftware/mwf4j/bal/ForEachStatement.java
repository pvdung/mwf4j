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
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.helpers.ClosureException;
import  org.jwaresoftware.mwf4j.scope.CursorNames;
import  org.jwaresoftware.mwf4j.scope.NumberRewindCursor;
import  org.jwaresoftware.mwf4j.scope.RewindCursor;
import  org.jwaresoftware.mwf4j.scope.Rewindable;

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
 * <b>Usage note #1:</b> the collection holding the dataset needs to 
 * exist as long as <em>the entire foreach statement</em> does. This 
 * means the collection may need to be long-lived if each iteration 
 * takes a while to complete. The collection also needs to be immutable
 * for the duration of the foreach statement; otherwise, the source
 * iterator can signal an unexpected concurrent modification error.
 * <p/>
 * <b>Usage note #2:</b> the statement supports a rudimentary integer-based
 * rewind capability. The "count" starts at zero and is incremented by one
 * for each item returned by the collection. If a rewind is triggered, this
 * statement will <em>re-request a new application-supplied
 * collection and skip ahead to the rewind index</em>. Note that for the
 * skip ahead the statement will neither update the cursor nor call the
 * callback action.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,infra
 **/

public class ForEachStatement extends BALStatement implements Unwindable, Resettable, Rewindable
{
    static final Callable<Collection<?>> GivebackEMPTY_LIST= new Callable<Collection<?>>() {
        public Collection<?> call() {
            return Empties.LIST;
        }
    };

    public ForEachStatement(ControlFlowStatement next)
    {
        super(next);
        myUnwindSupport = new ReentrantSupport(this,true,this);
    }

    public void setGetter(Callable<Collection<?>> getter)
    {
        Validate.notNull(getter,What.CALLBACK);
        myGetter = getter;
    }

    public void setCursor(Reference key)
    {
        Validate.notNull(key,What.CURSOR);
        myCursor.copyFrom(key);
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
                next= new ThrowStatement(getX,"Unable to get foreach data iterator");
                resetThis();
            } finally {
                MDC.popHarness(this,harness);
            }
        }
        if (next==null) {
            if (myWorker.hasNext()) {
                myUnwindSupport.loop(harness);
                myUnwindSupport.addRewindpoint(newRewindpoint(++myIndex));
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
        myIndex= -1;
        myGetter= GivebackEMPTY_LIST;
        myBody=null;
        myBodyFactory=null;
        myCursor.reset();
        myUnwindSupport.reset(this);
    }

    public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        reset();
        super.reconfigure(environ,overrides);
    }

    private void unwindThis(Harness harness, boolean clrData)
    {
        if (clrData) BALHelper.clrData(myCursor,harness);
        resetThis();
    }

    public void unwind(Harness harness)
    {
        Validate.stateIsFalse(myCursor.isUndefined(),What.CURSOR_UNDEF);
        unwindThis(harness,true);
    }

    public void reset()
    {
        resetThis();
    }

    protected ControlFlowStatement getIterationOfBody(Object data, Harness harness)
    {
        BALHelper.putData(myCursor,data,harness);//NB: *before* factory call!
        return BALHelper.makeIterationOfBody(this,harness,myBody,myBodyFactory);
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateIsTrue(myBody!=null || myBodyFactory!=null,
                "body or body-factory has been defined");
        Validate.stateIsFalse(myCursor.isUndefined(),What.CURSOR_UNDEF);
    }

    public ControlFlowStatement rewind(RewindCursor to, Harness harness)
    {
        Validate.stateNotNull(myWorker,What.ENABLED);
        Validate.isA(to,NumberRewindCursor.class,What.CURSOR);
        int index = ((NumberRewindCursor)to).getInt();
        Validate.isTrue(0<=index && myIndex>=index, "valid rewind index["+index+"]");
        rewindThis(index,harness);
        return this;
    }

    private void rewindThis(final int index, Harness harness)
    {
        try {
            MDC.pshHarness(this,harness);
            myWorker = myGetter.call().iterator();
            int stopindex=index;
            while((stopindex-->0) && myWorker.hasNext()) { myWorker.next(); } //SKIP-AHEAD
            myIndex=index;
        } catch(Exception getX) {
            throw new ClosureException("Unable to rewind foreach data iterator",getX);
        } finally {
            MDC.popHarness(this,harness);
        }
    }

    private NumberRewindCursor newRewindpoint(int index)
    {
        String aid = getWhatId();//NB: make it something determinate for testability!
        return new NumberRewindCursor(this,index,CursorNames.nameFrom(aid,index));
    }


    private Callable<Collection<?>> myGetter= GivebackEMPTY_LIST;
    private Iterator<?> myWorker;
    private ControlFlowStatement myBody;
    private Action myBodyFactory;
    private Reference myCursor= new Reference();//REQUIRED!
    private ReentrantSupport myUnwindSupport;
    private int myIndex= -1;//NB: for rewind use ONLY!
}


/* end-of-ForEachStatement.java */
