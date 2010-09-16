/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  java.util.List;
import  java.util.Queue;
import  java.util.concurrent.atomic.AtomicBoolean;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Strings;
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
import  org.jwaresoftware.mwf4j.scope.Scope;
import  org.jwaresoftware.mwf4j.scope.Scopes;

/**
 * Common implementation of the {@linkplain Harness harness} interface for
 * a simple primary and a couple special-purpose secondary harnesses. Actions,
 * statements, conditions and other components must use a harness to access 
 * various services like the continuation and unwind queues during execution. 
 * Subclasses are expected to implement lookup or retrieval of the harness'
 * (ultimate) owner, variables, and other runtime services.
 * <p/>
 * Implementation note: if you change the implementation of this class, you
 * <em>MUST manually verify</em> that change's effect on the various subclasses 
 * especially the dependent classes like {@linkplain SlaveHarness} and 
 * {@linkplain ForeverHarness}. Both the BAL and UC test suites must pass
 * before you commit the change. Also, if you override {@linkplain #doEnter()}
 * and/or {@linkplain #doLeave()} you MUST call the inherited versions at
 * some point.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (guarded for continuation, unwinds, &amp; adjustments management)
 * @.group    infra,impl,helper
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
        sb.append("OWNER='")
          .append(What.getNonBlankId(getOwner()))
          .append("' [HRNES=")
          .append(typeCN())
          .append("] against fixture '")
          .append(super.getName())
          .append("'");
        return sb.toString();
    }


    public final void run()
    {
        doEnter();
        try {
            ControlFlowStatement next = adjusted(firstStatement());
            while (!next.isTerminal()) {
                next = adjusted(perform(next));
            }
        } catch(RuntimeException rtX) {
            rtX = handleUncaughtError(rtX);
            if (rtX!=null) {
                myAbortedFlag=true;
                throw rtX;
            }
        } finally {
            doLeave();
        }
    }

    public final boolean isRunning()
    {
        return myRunningFlag.get();
    }

    public final boolean isAborted()
    {
        return myAbortedFlag;
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



    void queueContinuations(ControlFlowStatement directed)
    {
        Validate.notNull(directed,What.CONTINUATION);
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
        Scopes.addUnwind(participant);
    }



    public void removeUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        Scopes.removeUnwind(participant);
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
        doUnwind();
        MDC.psh(MWf4J.MDCKeys.UNCAUGHT_ERROR,cause);
        try {
            doError(cause);
        } finally {
            MDC.pop(MWf4J.MDCKeys.UNCAUGHT_ERROR);
        }
        return cause;
    }



    final void doUnwind()
    {
        Scopes.unwind(this);
    }



    protected void doEnter()
    {
        Validate.stateIsFalse(isRunning(),"running");
        resetThis();
        myAbortedFlag = false;
        myScope = Scopes.enter(this);
        if (getOwner() instanceof Executable) {
            ((Executable)getOwner()).doEnter(this);
        }
        myRunningFlag.set(true);
    }



    void resetThis()
    {
        myContinuations.clear();
        myQueue.clear();
        myScope = null;
    }



    protected void doLeave()
    {
        Validate.stateIsTrue(isRunning(),"running");
        if (getOwner() instanceof Executable) {
            ((Executable)getOwner()).doLeave(this);
        }
        Scopes.leave(this);
        resetThis();
        myRunningFlag.set(false);
    }



    protected void doError(Throwable cause)
    {
        Diagnostics.ForCore.warn("Unhandled "+typeCN()+" harness exception for "+getName(),cause);
        Thread thr = Thread.currentThread();
        String yah = ""+thr.getId()+":"+Strings.trimToEmpty(thr.getName());
        getIssueHandler().problemOccured("Unhandled run error ON "+typeCN()+" [THREAD="+yah+"]",Effect.ABORT,cause);
    }



    private ControlFlowStatement adjusted(final ControlFlowStatement pending)
    {
        ControlFlowStatement next=pending;
        synchronized(myAdjustFlagGuard) {
            if (myAdjustFlag!=null)  {
                myAdjustFlag= null;//NB: DO HERE in case of ERROR!
                next= adjustmentStatement(pending);
            }
        }
        return next;
    }



    private ControlFlowStatement adjustmentStatement(ControlFlowStatement next)
    {
        assert myAdjustmentAction!=null : "adjustment defined";
        if (myAdjustmentAction.isTerminal()) {
            Diagnostics.ForCore.warn("Received terminal adjustment for {}",getName());
            doUnwind();
        }
        ControlFlowStatement adjustment = myAdjustmentAction.makeStatement(next);
        myAdjustmentAction = null;
        return adjustment;
    }


    
    protected final Scope getScope()
    {
        return myScope;
    }


    private AtomicBoolean myRunningFlag= new AtomicBoolean();
    private boolean myAbortedFlag;
    final List<ControlFlowStatement> myContinuations = LocalSystem.newList();
    final Queue<ControlFlowStatement> myQueue = LocalSystem.newLinkedList();
    private Scope myScope;

    private Boolean myAdjustFlag;//NB:can be null so need guard
    private final Object myAdjustFlagGuard = new int[0];
    private Adjustment myAdjustmentAction;//NB:controlled by guard too!
}


/* end-of-HarnessSkeleton.java */
