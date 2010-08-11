/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.TimeoutException;

import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.BreakType;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Statement that sits and waits (blocks current thread) for one or
 * all participants in a fork action to complete. Whether we're waiting
 * for one or all participants is determined by the incoming countdown
 * (as defined by {@linkplain ForkAction}).
 * <p/>
 * You can configure a join to timeout after waiting for a specified period.
 * If the {@linkplain #setHaltIfError haltIfError flag} is enabled, a 
 * timeout causes a failure to occur on this statement's harness. Similarly,
 * if the current thread is interrupted, the haltIfError flag determines
 * if the join continues (silently) or signals an error.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class JoinStatement extends BALProtectorStatement implements Unwindable
{
    public JoinStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myUnwindSupport = new ReentrantSupport(getOwner(),this);
    }

    public void setBarrier(CountDownLatch barrier)
    {
        Validate.notNull(barrier,What.BARRIER);
        myBarrier = barrier;
    }

    public void setBreakSupport(RetryDef retryDef, Action breakAction)
    {
        Validate.notNull(retryDef,What.CRITERIA);
        myRetries = retryDef;
        Validate.isFalse(myRetries.getRetryWait().isUndefined(),"timeout undefined");
        myBreakAction = breakAction;//NB:optional
    }

    public void setBody(Action body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next=null;
        try {
            myUnwindSupport.loop(harness);
            boolean ok = true;
            if (myRetries==null) myBarrier.await();
            else ok = myBarrier.await(myRetries.getRetryWait().getLength(),myRetries.getRetryWait().getUOM());
            if (ok) {
                breadcrumbs().write("Release criteria met for join '{}' [count={}]",getWhatId(),myReleaseNum);
                next = BALHelper.makeInstanceOfBody(this,harness,next(),myBody);
            } else {
                Exception expiredX = new TimeoutException("Join '"+getWhatId()+"' timed out after "+myRetries.getRetryWait()); 
                next = handleBreak(BreakType.TIMEOUT,expiredX,harness);
            }
        } catch(InterruptedException iruptedX) {
            next = handleBreak(BreakType.INTERRUPTED,iruptedX,harness);
        }
        if (next!=this) {
            finishThis(harness);
        }
        return next;
    }

    protected ControlFlowStatement handleBreak(BreakType type, final Exception issue, Harness harness)
    {
        if (breadcrumbs().isEnabled()) {
            String because=Throwables.getTypedMessage(issue);
            breadcrumbs().write("Join for '{}' {} break detected: {}",getWhatId(),type,because);
        }
        BALHelper.runBreakAction(issue,myBreakAction,harness);

        ControlFlowStatement next;
        if (myRetries!=null && myRetries.decrementAndGetRetryCount()>0) {
            if (breadcrumbs().isEnabled()) {
                breadcrumbs().write("Join '{}' retrying (retries left={})",getWhatId(),myRetries.getRetryCount());
            }
            next = this;
        } else {
            final ControlFlowStatement kontinue = new EndStatement();
            if (myErrorKey!=null) {
                BALHelper.putData(myErrorKey,issue,myErrorStoreType,harness);
            }
            next = myTrySupport.handle(kontinue,new ThrowStatement(getOwner(),issue),harness);
            if (next==kontinue) {
                next= BALHelper.makeInstanceOfBody(this,harness,next(),myBody);
            }
        }
        return next;
    }

    final void resetThis()
    {
        myReleaseNum = -1;
        myBody = null;
        myBarrier = null;
        myRetries = null;
        myBreakAction = null;
        myUnwindSupport.reset(this);
        super.resetThis();
    }

    private void finishThis(Harness harness)
    {
        myUnwindSupport.finished(harness);
        resetThis();
    }

    public void unwind(Harness harness)
    {
        resetThis();
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myBarrier,What.BARRIER);
        myReleaseNum = (int)myBarrier.getCount();
        Validate.stateIsTrue(myReleaseNum>0, "barrier.size>0");
    }


    private Action myBody;//OPTIONAL
    private CountDownLatch myBarrier;
    private RetryDef myRetries;//OPTIONAL
    private Action myBreakAction;//OPTIONAL
    private ReentrantSupport myUnwindSupport;
    private int myReleaseNum;
}


/* end-of-JoinStatement.java */
