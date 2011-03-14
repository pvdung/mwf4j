/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.atomic.AtomicInteger;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import  org.jwaresoftware.mwf4j.behaviors.CallBounded;
import  org.jwaresoftware.mwf4j.helpers.VariableCreator;
import  org.jwaresoftware.mwf4j.scope.GivebackRewindpoint;
import  org.jwaresoftware.mwf4j.scope.Rewindpoint;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that will trigger a rewind to either a predefined rewind point or
 * a dynamically determined rewind point (default). You can also configure 
 * this action for "retries" by linking it with an independent call counter.
 * To work as expected the call counter must survive across calls to the
 * action (and creation/destruction of associated rewind statements). A
 * simple implementation would simply store the call counter as a variable
 * in the action's assocated runtime harness. Note that both application
 * supplied helpers (counter and rewindpoint callback) MUST be threadsafe
 * if this rewind action is used from multiple threads concurrently.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       RewindStatement
 * @see       GivebackRewindpoint
 * @see       VariableCreator
 **/

public class RewindAction extends ActionSkeleton implements CallBounded
{
    public RewindAction()
    {
        this("rewind");
    }

    public RewindAction(String id)
    {
        super(id);
    }

    public void setRewindpointGetter(Callable<Rewindpoint> getter)
    {
        Validate.notNull(getter,What.CALLBACK);
        myGetRewindpoint = getter;
    }

    public final void setRewindpointMatcher(Rewindpoint matcher)
    {
        Validate.notNull(matcher,What.CALLBACK);
        setRewindpointGetter(new GivebackRewindpoint(matcher));
    }

    public final void setRewindpoint(Rewindpoint mark)
    {
        Validate.notNull(mark,What.CURSOR);
        setRewindpointGetter(new GivebackValue<Rewindpoint>(mark));
    }

    public void setMaxIterations(int max)
    {
        Validate.isFalse(max<0,"max-retries >= 0");
        myLimit = Integer.valueOf(max);
    }

    public final void setHaltIfMax(boolean flag)
    {
        myHaltIfMaxFlag = flag;
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myHaltContinuationFlag = flag;
    }

    public void setCallCounter(Callable<AtomicInteger> callCounter)
    {
        Validate.notNull(callCounter,What.CALLBACK);
        myCallCounter = callCounter;
    }

    public final void setCallCounter(String key)
    {
        Validate.notBlank(key,What.KEY);
        setCallCounter(new VariableCreator<AtomicInteger>(key, AtomicInteger.class));
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new RewindStatement(next);
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isA(statement,RewindStatement.class,What.STATEMENT);
        RewindStatement rewind = (RewindStatement)statement;
        rewind.setRewindpointGetter(myGetRewindpoint);
        if (myLimit!=null) {
            rewind.setMaxIterations(myLimit);
            rewind.setHaltIfMax(myHaltIfMaxFlag);
            rewind.setUseHaltContinuation(myHaltContinuationFlag);
            rewind.setCallCounter(myCallCounter);
        }
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myGetRewindpoint,What.GET_METHOD);
        if (myLimit!=null) {
            Validate.stateNotNull(myCallCounter,"call-counter");
        }
    }
 
    private Callable<Rewindpoint> myGetRewindpoint;
    private boolean myHaltIfMaxFlag = false;
    private Integer myLimit = null;//OPTIONAL; undefined => none
    private boolean myHaltContinuationFlag = BAL.getUseHaltContinuationsFlag();
    private Callable<AtomicInteger> myCallCounter;//OPTIONAL

}


/* end-of-RewindAction.java */
