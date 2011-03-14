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
import  org.jwaresoftware.gestalt.bootstrap.Fixture.Implementation;
import  org.jwaresoftware.gestalt.bootstrap.FixtureWrap;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.scope.Scope;
import  org.jwaresoftware.mwf4j.scope.Scopes;
import  org.jwaresoftware.mwf4j.starters.ExecutableSupport;

/**
 * Common implementation of the {@linkplain Harness Harness} interface for
 * a simple primary and a couple special-purpose secondary harnesses. Actions,
 * statements, conditions and other components must use a harness to access 
 * various services like the continuation and unwind queues during execution. 
 * Subclasses are expected to implement the lookup or retrieval of the harness'
 * (ultimate) owner, variables, and other runtime services.
 * <p/>
 * This harness implementation is aware of {@linkplain Scope scopes}. As part
 * of its default {@linkplain #doEnter doEnter} processing, it establishes
 * an outermost scope (outermost for the harness) which it removes on exit
 * of the run method.
 * <p/>
 * <b>Usage note 1</b>: if you change the implementation of this class, you
 * <em>MUST manually verify</em> that change's effect on the various subclasses 
 * especially the dependent classes like {@linkplain SlaveHarness} and 
 * {@linkplain ForeverHarness}. Both the BAL and UC test suites must pass
 * before you commit the change. Also, if you override {@linkplain #doEnter()}
 * and/or {@linkplain #doLeave()} you MUST call the inherited versions at
 * some point.
 * <p/>
 * <b>Usage note 2:</b> A harness's owning activity must be available and
 * valid throughout any execution run of the harness. If an activity 
 * implements the standard MWf4J 
 * {@linkplain org.jwaresoftware.mwf4j.behaviors.Executable Executable}
 * interface, during execution the harness will trigger the various 
 * callback methods at the appropriate time with the following caveats:<ul>
 *   <li>on <b>doEnter</b>: the harness <em>has not started its run loop yet</em> 
 *       but its scope has been established. Callback can install continuations
 *       and ONE adjustment without issues.</li>
 *   <li>on <b>doLeave</b>: the harness <em>has left its run loop</em> although
 *       its scope is still in effect but has been completely unwound. This
 *       callback is triggered even if an abort (doError) has occured. Use 
 *       the harness's {@linkplain #isAborted} method if needed.</li>
 *   <li>on <b>doError</b>: the harness <em>has left its run loop</em> although
 *       its scope is still in effect but has been completely unwound.
 *       The throwable that caused the abort, has been stored in the active
 *       MDC and the top-level harness problem handler has already been
 *       notified.</li>
 * </ul>

 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (guarded for continuation, unwinds, &amp; adjustments management)
 * @.group    infra,impl,helper
 **/

public abstract class HarnessSkeleton extends FixtureWrap implements Harness, Runnable
{
    protected HarnessSkeleton(Implementation fixture)
    {
        super(fixture);
        myViewWrap = newStaticView();
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



    public final Fixture staticView()
    {
        return myViewWrap;
    }

    protected Fixture newStaticView()
    {
        return new HarnessStaticView(this);
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
        return getOwner().firstStatement(staticView());
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
        ExecutableSupport.doEnter(this,getOwner());
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
        ExecutableSupport.doLeave(this,getOwner());
        Scopes.leave(this);
        resetThis();
        myRunningFlag.set(false);
    }



    protected void doError(Throwable cause)
    {
        Diagnostics.ForCore.warn("Unhandled "+typeCN()+" harness exception for "+getName(),cause);
        Thread thr = Thread.currentThread();
        String yah = ""+thr.getId()+":"+Strings.trimToEmpty(thr.getName());
        getIssueHandler().problemOccured("Unhandled run error ON "+typeCN()+" [THREAD="+yah+"]",Effect.ABORT,cause,this);
        ExecutableSupport.doError(this,getOwner(),cause);
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
        ControlFlowStatement adjustment = myAdjustmentAction.buildStatement(next,staticView());
        myAdjustmentAction = null;
        return adjustment;
    }


    
    protected final Scope getScope()
    {
        return myScope;
    }



    public final String interpolate(String inputString)
    {
        return interpolate(inputString,getIssueHandler());
    }


    private AtomicBoolean myRunningFlag= new AtomicBoolean();
    private boolean myAbortedFlag;//NB:kept valid for querying after run returns!
    final List<ControlFlowStatement> myContinuations = LocalSystem.newList();
    final Queue<ControlFlowStatement> myQueue = LocalSystem.newLinkedList();
    private Scope myScope;
    private Fixture myViewWrap;

    private Boolean myAdjustFlag;//NB:can be null so need guard
    private final Object myAdjustFlagGuard = new int[0];
    private Adjustment myAdjustmentAction;//NB:controlled by guard too!
}


/* end-of-HarnessSkeleton.java */
