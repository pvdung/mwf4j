/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Protector;
import  org.jwaresoftware.mwf4j.behaviors.Signal;

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
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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

    private ControlFlowStatement protect(ControlFlowStatement statement)
    {
        return BALHelper.protect(getOwner(),statement);
    }

    @Override
    ControlFlowStatement runMember(int index, Harness harness, ControlFlowStatement member)
    {
        ControlFlowStatement next = harness.runParticipant(protect(member));
        if (next instanceof Signal) {
            ThrowStatement lastThrown = TrySupport.convert((Signal)next,getOwner());
            lastThrown.setPosition(index);
            next = myTrySupport.handle(next(),lastThrown,harness,breadcrumbs());//NB: abort;move past me... 
        }
        return next;
    }

    @Override
    final void resetThis()
    {
        super.resetThis();
        myTrySupport.reset(false,false,true);
    }


    private TrySupport myTrySupport;
}


/* end-of-ProtectedSequenceStatement.java */
