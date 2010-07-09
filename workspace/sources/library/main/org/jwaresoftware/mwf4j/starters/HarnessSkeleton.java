/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.IdentityHashMap;
import  java.util.List;
import  java.util.Map;
import  java.util.Queue;
import  java.util.concurrent.atomic.AtomicBoolean;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.bootstrap.Fixture;
import  org.jwaresoftware.gestalt.bootstrap.FixtureWrap;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Executable;

/**
 * Straightforward implementation of the {@linkplain Harness harness}
 * interface and the default for MWf4J components. Actions, statements,
 * conditions and other components must use a harness to access various
 * services like the continuation and unwind queues during execution. We
 * rely on the supplied fixture service provider lookup mechanism to
 * retrieve all application-supplied overrides for our services. For
 * instance, if the application wants to supply its own variables map then
 * it should install a map (or a map provider) under the name
 * {@linkplain MWf4J.ServiceIds#VARIABLES} of a type compatible with a 
 * concurrent map. Unwindables must self-[un]register as part of their
 * execution.
 * <p/>
 * <b>Usage Note:</b> Most of the getter methods (like getVariables) are
 * expected to be <em>available immediately after construction</em>. So 
 * it's important that the harness creator pre-populate the incoming
 * fixture with all service provider overrides and configuration BEFORE
 * creating the harness. Also, any pre-installed continuations and unwinds
 * are <em>CLEARED</em> on entry to {@linkplain #run()}! Only unwinds and
 * continuations added during execution are processed.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (guarded for continuation, unwinds, &amp; adjustments management)
 * @.group    impl,helper
 **/

public abstract class HarnessSkeleton extends FixtureWrap implements Harness, Runnable
{
    protected HarnessSkeleton(Fixture.Implementation fixture)
    {
        super(fixture);
    }


    public String getName()
    {
        StringBuilder sb = LocalSystem.newSmallStringBuilder();
        sb.append(What.getNonBlankId(getOwner()))
          .append(" against ")
          .append(super.getName());
        return sb.toString();
    }


    public final void run()
    {
        resetThis();
        doEnter();
        try {
            ControlFlowStatement next = adjusted(firstStatement());
            while (!next.isTerminal()) {
                next = adjusted(perform(next));
            }
        } catch(RuntimeException rtX) {
            rtX = handleUncaughtError(rtX);
            if (rtX!=null)
                throw rtX;
        } finally {
            doLeave();
        }
    }

    public final boolean isRunning()
    {
        return myRunningFlag.get();
    }



    private ControlFlowStatement perform(ControlFlowStatement statement)
    {
        ControlFlowStatement continuation = null;
        queueContinuations(runParticipant(statement));
        do {
            continuation = myQueue.remove();
        } while(continuation.isTerminal() && !myQueue.isEmpty());
        return continuation;
    }



    protected ControlFlowStatement firstStatement()
    {
        return getOwner().firstStatement();
    }



    private void queueContinuations(ControlFlowStatement directed)
    {
        Validate.notNull(directed,What.STATEMENT);
        myQueue.add(directed);
        synchronized(myContinuations) {
            if (!myContinuations.isEmpty()) {
                for (ControlFlowStatement derived:myContinuations) {
                    myQueue.add(derived);
                }
                myContinuations.clear();
            }
        }
    }



    public void addContinuation(ControlFlowStatement participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myContinuations) {
            myContinuations.add(participant);
        }
    }



    public void addUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myUnwinds) {
            myUnwinds.put(participant,Boolean.TRUE);
        }
    }



    public void removeUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myUnwinds) {
            myUnwinds.remove(participant);
        }
    }



    public ControlFlowStatement runParticipant(ControlFlowStatement statement)
    {
        Validate.notNull(statement,What.STATEMENT);
        return statement.run(this);
    }



    public void applyAdjustment(Adjustment adjustment)
    {
        Validate.notNull(adjustment,What.ACTION);
        Validate.stateIsTrue(isRunning(),"running");
        synchronized(myAdjustFlagGuard) {
            Validate.stateIsNull(myAdjustFlag,"pending adjustment");
            myAdjustFlag = Boolean.TRUE;
            myAdjustmentAction = adjustment;
        }
        
    }



    RuntimeException handleUncaughtError(RuntimeException cause)
    {
        unwindRegistered();
        MDC.psh(MWf4J.MDCKeys.UNCAUGHT_ERROR,cause);
        try {
            doError(cause);
        } finally {
            MDC.pop(MWf4J.MDCKeys.UNCAUGHT_ERROR);
        }
        return cause;
    }



    final void unwindRegistered()
    {
        List<Unwindable> unwinds;
        synchronized(myUnwinds) {
            unwinds = LocalSystem.newList(myUnwinds.keySet());
            myUnwinds.clear();
        }
        for (Unwindable next:unwinds) {
            next.unwind(this);
        }
    }



    protected void doEnter()
    {
        Validate.stateIsFalse(isRunning(),"running");
        MDC.pshHarness(getOwner(),this);
        if (getOwner() instanceof Executable) {
            ((Executable)getOwner()).doEnter(this);
        }
        myRunningFlag.set(true);
    }



    private void resetThis()
    {
        myContinuations.clear();
        myQueue.clear();
        myUnwinds.clear();
    }



    protected void doLeave()
    {
        Validate.stateIsTrue(isRunning(),"running");
        if (getOwner() instanceof Executable) {
            ((Executable)getOwner()).doLeave(this);
        }
        resetThis();
        MDC.popHarness(getOwner(),this);
        myRunningFlag.set(false);
    }



    protected void doError(Throwable cause)
    {
        Diagnostics.ForCore.warn("Unhandled harness exception",cause);
        getIssueHandler().problemOccured("Unhandled run error",Effect.ABORT,cause);
    }



    private ControlFlowStatement adjusted(final ControlFlowStatement pending)
    {
        ControlFlowStatement next=pending;
        synchronized(myAdjustFlagGuard) {
            if (myAdjustFlag!=null)  {
                myAdjustFlag= null;
                next= adjustmentStatement(pending);
            }
        }
        return next;
    }



    private ControlFlowStatement adjustmentStatement(ControlFlowStatement next)
    {
        assert myAdjustmentAction!=null : "adjustment defined";
        if (myAdjustmentAction.isTerminal()) {
            unwindRegistered();
        }
        ControlFlowStatement adjustment = myAdjustmentAction.makeStatement(next);
        myAdjustmentAction = null;
        return adjustment;
    }



    private AtomicBoolean myRunningFlag= new AtomicBoolean();
    private List<ControlFlowStatement> myContinuations = LocalSystem.newList();
    private Queue<ControlFlowStatement> myQueue = LocalSystem.newLinkedList();
    private Map<Unwindable,Boolean> myUnwinds = new IdentityHashMap<Unwindable,Boolean>();

    private Boolean myAdjustFlag;//NB:can be null so need guard
    private final Object myAdjustFlagGuard = new int[0];
    private Adjustment myAdjustmentAction;//NB:controlled by guard too!
}


/* end-of-HarnessSkeleton.java */
