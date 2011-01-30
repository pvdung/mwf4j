/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Effect;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.starters.ActionDependentSkeleton;

/**
 * Helper that encapsulates the reusable handling code for looping or
 * iterating statements that have a "max iterations" constraint. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public final class LimitSupport extends ActionDependentSkeleton implements Resettable
{
    public LimitSupport(Action owner)
    {
        super(owner);
        this.reset();
    }

    public void setHaltIfMax(boolean flag)
    {
        myHaltIfMaxFlag = flag;
    }

    public void setMaxIterations(int max)
    {
        myLimit = max>=0 ? max : Integer.MAX_VALUE;
    }

    public void setQuiet(boolean flag)
    {
        myQuietFlag = flag;
    }

    public final void setUseContinuation(boolean flag)
    {
        myUseThrowableFlag = !flag;
    }

    public boolean isFailAlways()
    {
        return myLimit<=0 && myHaltIfMaxFlag==true;
    }

    public ControlFlowStatement test(final int loopCount, Harness harness)
    {
        ControlFlowStatement next=null;

        if (loopCount>=myLimit) {
            Diagnostics.ForBAL.info("Max iterations ({}) break triggered for action='{}'",String.valueOf(loopCount),getOwner());
            next = new EndStatement();//NB: just a marker to caller unless haltIfMax
 
            if (myHaltIfMaxFlag || !myQuietFlag) {
                TooManyIterationsException overflowX = new TooManyIterationsException(loopCount,myLimit);
                String summation = overflowX.getMessage();
                if (!myQuietFlag) {
                    Effect effect = myHaltIfMaxFlag ? Effect.ABORT : Effect.IGNORE;
                    harness.getIssueHandler().problemOccured(summation, effect, overflowX, getOwner());
                    summation = null;
                }
                if (myHaltIfMaxFlag) {
                    if (myUseThrowableFlag)
                        throw overflowX;//Forced-Unwind!
                    next = new ThrowStatement(getOwner(),overflowX,summation);
                }
            }
        }
        return next;
    }

    public void reset()
    {
        reset(true,false,BAL.getUseHaltContinuationsFlag());
    }

    public void reset(boolean failIfMax, boolean quiet, boolean useContinuation)
    {
        myLimit = Integer.MAX_VALUE;//Unlimited
        myHaltIfMaxFlag = failIfMax;
        myQuietFlag = quiet;
        myUseThrowableFlag = !useContinuation;
    }

    private int myLimit;
    private boolean myHaltIfMaxFlag;
    private boolean myQuietFlag;
    private boolean myUseThrowableFlag;//checked iff haltIfMax==true
}


/* end-of-LimitSupport.java */
