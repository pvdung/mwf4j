/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.starters.ActionDependentSkeleton;

/**
 * Helper that encapsulates the reusable 'do-once' management code for 
 * reentrant statements like the {@linkplain ForEachStateemnt} or 
 * {@linkplain WhileStatement}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public final class ReentrantSupport extends ActionDependentSkeleton implements Resettable
{
    public ReentrantSupport(Action owner)
    {
        super(owner);
    }

    public ReentrantSupport(Action owner, Unwindable unwinder)
    {
        super(owner);
        myUnwinder = unwinder;
    }

    public void setUnwinder(Unwindable unwinder)
    {
        Validate.stateIsFalse(myCalledLatch,"activated");
        myUnwinder = unwinder;
    }

    public boolean loop(Harness harness)
    {
        boolean firstTime=false;
        if (!myCalledLatch) {
            myCalledLatch=true;
            if (myUnwinder!=null) {
                harness.addUnwind(myUnwinder);
            }
            firstTime=true;
        }
        return firstTime;
    }

    public final boolean calledOnce()
    {
        return myCalledLatch;
    }

    public boolean finished(Harness harness)
    {
        boolean undo=false;
        if (myCalledLatch) {
            undo=true;
            if (myUnwinder!=null) {
                harness.removeUnwind(myUnwinder);
            }
        }
        reset();
        return undo;
    }

    public void reset()
    {
        reset(null);
    }

    public void reset(Unwindable unwinder)
    {
        myCalledLatch=false;
        myUnwinder=unwinder;
    }

    private boolean myCalledLatch;
    private Unwindable myUnwinder;
}


/* end-of-ReentrantSupport.java */
