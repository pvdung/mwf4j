/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.starters.ActionDependentSkeleton;
import  org.jwaresoftware.mwf4j.starters.TraceSupport;

/**
 * Helper that encapsulates the reusable handling code for protected statements 
 * like the {@linkplain TryEachSequenceStatement} or {@linkplain TryCatchStatement}.
 * Also useful to propagate a checked exception from a statements run handling
 * in a single consistent manner.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public final class TrySupport extends ActionDependentSkeleton implements Resettable
{
    public TrySupport(Action owner)
    {
        super(owner);
        this.reset();
    }

    public TrySupport(Action owner, boolean failIfError, boolean quiet)
    {
        this(owner,failIfError,quiet,true);
    }

    public TrySupport(Action owner, boolean failIfError, boolean quiet, boolean useContinuation)
    {
        super(owner);
        reset(failIfError,quiet,useContinuation);
    }

    public final void setHaltIfError(boolean flag)
    {
        myHaltIfErrorFlag = flag;
    }

    public final boolean isHaltIfError()
    {
        return myHaltIfErrorFlag;
    }

    public final void setQuiet(boolean flag)
    {
        myQuietFlag = flag;
    }

    public final void setUseContinuation(boolean flag)
    {
        myUseThrowableFlag = !flag;
    }

    public ControlFlowStatement handleInner(ControlFlowStatement next, ThrowStatement pendingThrow, Harness harness)
    {
        if (myHaltIfErrorFlag || !myQuietFlag) {
            RunFailedException rX = new RunFailedException(pendingThrow);
            String summation = rX.getMessage();
            Effect effect = myHaltIfErrorFlag ? Effect.ABORT : Effect.IGNORE;
            if (!myQuietFlag) {
                harness.getIssueHandler().problemOccured(summation, effect, rX, getOwner());
                summation = null;
            }
            if (myHaltIfErrorFlag) {
                if (myUseThrowableFlag)
                    throw rX;//Forced-Unwind!
                next = new ThrowStatement(getOwner(),rX,summation);
            }
        }
        return next;
    }

    public ControlFlowStatement handle(ControlFlowStatement next, ThrowStatement pendingThrow, Harness harness, TraceSupport bc)
    {
        bc.caught(pendingThrow.getCause());
        return handleInner(next,pendingThrow,harness);
    }

    public ControlFlowStatement handle(ControlFlowStatement next, ThrowStatement pendingThrow, Harness harness)
    {
        Diagnostics.ForFlow.info("** Captured exception in statement '{}': {}",getWhatId(),Throwables.getTypedMessage(pendingThrow.getCause()));
        return handleInner(next,pendingThrow,harness);
    }

    public void reset()
    {
        reset(true,false,BAL.getUseHaltContinuationsFlag());
    }

    public void reset(boolean failIfError, boolean quiet, boolean useContinuation)
    {
        myHaltIfErrorFlag = failIfError;
        myQuietFlag = quiet;
        myUseThrowableFlag = !useContinuation;
    }

    public void reset(boolean failIfError, boolean quiet)
    {
        reset(failIfError,quiet,BAL.getUseHaltContinuationsFlag());
    }

    public void copyFrom(TrySupport from)
    {
        Validate.notNull(from,What.CRITERIA);
        myHaltIfErrorFlag = from.myHaltIfErrorFlag;
        myQuietFlag = from.myQuietFlag;
        myUseThrowableFlag = from.myUseThrowableFlag;
    }

    public void copyFrom(ProtectorFields from)
    {
        Validate.notNull(from,What.CRITERIA);
        myHaltIfErrorFlag = from.haltIfErrorFlag;
        myQuietFlag = from.quietFlag;
        myUseThrowableFlag = !from.haltContinuationFlag;
    }

    private boolean myHaltIfErrorFlag;
    private boolean myQuietFlag;
    private boolean myUseThrowableFlag;//checked iff haltIfError==true
}


/* end-of-TrySupport.java */
