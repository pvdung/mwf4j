/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Protector;
import  org.jwaresoftware.mwf4j.behaviors.Signal;

/**
 * Extension of a normal sequence statement that will run each nested action
 * even if it has to capture (and delay) thrown exceptions for one or more
 * action. On completion of the sequence, this statement will return a
 * {@linkplain ThrowStatement throw statement} continuation if at least one nested 
 * action failed and the sequence's {@linkplain #setHaltIfError(boolean) haltIfError}
 * option is enabled; otherwise, it will return the continuation setup at
 * construction time.
 * <p/>
 * If a sequence is rewinded to an index that precedes or equals a failed
 * statement position, that failure is wiped as if it never happened. The
 * assumption is that the retry (if successful) overrides the first failed
 * attempt for that and any subsequent statement.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class TryEachSequenceStatement extends SequenceStatement implements Protector
{
    public TryEachSequenceStatement(ControlFlowStatement next)
    {
        super(next);
        myTrySupport = new TrySupport(this);
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

    private ControlFlowStatement protect(ControlFlowStatement statement)
    {
        return BALHelper.protect(statement);
    }

    @Override
    ControlFlowStatement runMember(int index, Harness harness, ControlFlowStatement member)
    {
        ControlFlowStatement next = harness.runParticipant(protect(member));
        if (next instanceof Signal) {
            ThrowStatement signal = TrySupport.convert((Signal)next);
            signal.setNextThrown(lastThrown);
            signal.setPosition(index);
            lastThrown = signal;
            next = this;
        }
        return next;
    }

    @Override
    final void resetThis()
    {
        super.resetThis();
        lastThrown = null;
        myTrySupport.reset();
    }

    @Override
    final ControlFlowStatement finishThis(Harness harness, ControlFlowStatement next)
    {
        if (lastThrown!=null) {
            next = myTrySupport.handle(next,lastThrown,harness,breadcrumbs());
        }
        return super.finishThis(harness,next);
    }

    @Override
    void rewindThis(int index, Harness harness)
    {
        if (lastThrown!=null) {
            if (lastThrown.getPosition()>=index) 
                lastThrown=null;//Wiped
        }
        super.rewindThis(index,harness);
    }

    private ThrowStatement lastThrown;
    private TrySupport myTrySupport;
}


/* end-of-TryEachSequenceStatement.java */
