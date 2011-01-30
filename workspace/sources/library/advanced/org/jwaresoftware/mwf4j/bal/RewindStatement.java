/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.atomic.AtomicInteger;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.CallBounded;
import  org.jwaresoftware.mwf4j.helpers.ClosureException;
import  org.jwaresoftware.mwf4j.scope.RewindAdjustment;
import  org.jwaresoftware.mwf4j.scope.Rewindpoint;

/**
 * Statement that will apply an adjustment to its harness to rewind to an 
 * application supplied rewind point. The rewind point is determined at 
 * statement execution time by a supplied callback. You can also setup a
 * rewind to execute a maximum number of times (retries). The retry count
 * is maintained externally as an atomic integer; the application must 
 * supply a callback to retrieve this integer reference.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       RewindAdjustment
 **/

public class RewindStatement extends BALStatement implements CallBounded
{
    private static final int UNKNOWN= -1;

    public RewindStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myMaxCallsSupport = new LimitSupport(getOwner());
    }

    public RewindStatement(Action owner, int retryCount, ControlFlowStatement next)
    {
        this(owner,next);
        myMaxCallsSupport.setMaxIterations(retryCount);
    }

    public void setRewindpointGetter(Callable<Rewindpoint> getter)
    {
        Validate.notNull(getter,What.CALLBACK);
        myGetRewindpoint = getter;
    }

    public void setMaxIterations(int max)
    {
        myMaxCallsSupport.setMaxIterations(max);
    }

    public void setHaltIfMax(boolean flag)
    {
        myMaxCallsSupport.setHaltIfMax(flag);
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myMaxCallsSupport.setUseContinuation(flag);
    }

    public void setCallCounter(Callable<AtomicInteger> callCounter)
    {
        Validate.notNull(callCounter,What.CALLBACK);
        myCallCounter = callCounter;
    }

    public void doEnter(Harness harness) 
    {
        super.doEnter(harness);
        if (isBounded())
            myCallCount = getAndIncrementCallCount();
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next=null;
        if (isBounded()) {
            Validate.stateIsTrue(myCallCount!=UNKNOWN,"callcount defined");
            next = myMaxCallsSupport.test(myCallCount,harness);
        }
        if (next==null) {
            Rewindpoint mark = getRewindpoint();
            harness.applyAdjustment(new RewindAdjustment(mark));
            next = next();
        } else if (next.isTerminal()) {
            next = next();
        }
        return next;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myGetRewindpoint,What.GET_METHOD);
    }

    public void doLeave(Harness harness)
    {
        myCallCount = UNKNOWN;
        super.doLeave(harness);
    }

    public void reconfigure()
    {
        super.reconfigure();
        verifyReady();
    }

    private Rewindpoint getRewindpoint()
    {
        try {
            return myGetRewindpoint.call();
        } catch(Exception callX) {
            String message= "Unable to retrieve rewind point";
            breadcrumbs().signaling(message);
            throw new ClosureException(message,callX);
        }
    }

    private int getAndIncrementCallCount()
    {
        try {
            AtomicInteger nth = myCallCounter.call();
            Validate.responseNotNull(nth,"call-count");
            int callcount = nth.getAndIncrement();
            Validate. responseIsTrue(callcount>=0,"callcount positive number");
            return callcount;
        } catch(RuntimeException rtX) {
            throw rtX;
        } catch(Exception callX) {
            throw new ClosureException("Unable to determine rewind call count",callX);
        }
    }

    private boolean isBounded()
    {
        return myCallCounter!=null;
    }

    private Callable<Rewindpoint> myGetRewindpoint;
    private Callable<AtomicInteger> myCallCounter;
    private int myCallCount= UNKNOWN;//Set on ENTRY to run!
    private LimitSupport myMaxCallsSupport;
}


/* end-of-RewindStatement.java */
