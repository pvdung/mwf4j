/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Protector;

/**
 * Extension of a normal sequence statement that will run each nested action
 * even if it has to capture (and delay) thrown exceptions for one or more
 * action. On completion of the sequence, this statement will return a
 * {@linkplain ThrowStatement throw statement} continuation if at least one nested 
 * action failed and the sequence's {@linkplain #setHaltIfError(boolean) haltIfError}
 * option is enabled; otherwise, it will return the continuation setup at
 * construction time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class TryEachSequenceStatement extends SequenceStatement implements Protector
{
    public TryEachSequenceStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myTrySupport = new TrySupport(getOwner());
    }

    public final void setHaltIfError(boolean flag)
    {
        myTrySupport.setHaltIfError(flag);
    }

    public final void setQuiet(boolean flag)
    {
        myTrySupport.setQuiet(flag);
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myTrySupport.setUseContinuation(flag);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next;
        if (getMembers().hasNext()) {
            myUnwindSupport.loop(harness);
            next = harness.runParticipant(protect(getMembers().next()));
            if (next instanceof ThrowStatement) {
                ThrowStatement signal = (ThrowStatement)next;
                signal.setNextThrown(lastThrown);
                lastThrown = signal;
                next = this;
            }
        } else {
            next = next();
            if (lastThrown!=null) {
                next = myTrySupport.handle(next, lastThrown, harness);
            }
            myUnwindSupport.finished(harness);
        }
        return next;
    }

    private ControlFlowStatement protect(ControlFlowStatement statement)
    {
        return BALHelper.protect(getOwner(),statement);
    }

    private void resetThis()
    {
        lastThrown = null;
        myTrySupport.reset();
    }

    public void unwind(Harness harness)
    {
        resetThis();
        super.unwind(harness);
    }

    public void reset()
    {
        resetThis();
        super.reset();
    }


    private ThrowStatement lastThrown;
    private TrySupport myTrySupport;
}


/* end-of-TryEachSequenceStatement.java */
