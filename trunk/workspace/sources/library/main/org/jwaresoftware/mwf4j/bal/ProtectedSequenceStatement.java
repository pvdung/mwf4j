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
 * Extension of a normal sequence statement that will capture and ignore <em>any</em>
 * exception thrown by a sub-action. The sequence is stopped immediately if a sub-
 * action throws and error and the run method returns the continuation setup at
 * construction time. This has the effect of aborting the sequence and blindly
 * continuing.
 * <p/>
 * The {@linkplain #setQuiet(boolean) quiet flag} determines whether the incoming
 * harness's problem handler is notified of any captured exceptions (<em>before</em>
 * the sequence is aborted).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       TryEachSequenceStatement
 **/

public class ProtectedSequenceStatement extends SequenceStatement implements Protector
{
    public ProtectedSequenceStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myTrySupport = new TrySupport(getOwner(),false,false);
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
                ThrowStatement lastThrown = (ThrowStatement)next;
                next = myTrySupport.handle(next(),lastThrown,harness);//NB: abort;move past me... 
            }
        } else {
            next = next();//NB: move past me...
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
        myTrySupport.reset(false,false,true);
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

    private TrySupport myTrySupport;
}


/* end-of-ProtectedSequenceStatement.java */
