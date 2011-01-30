/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.scope.RewindCursor;
import  org.jwaresoftware.mwf4j.scope.Scope;
import  org.jwaresoftware.mwf4j.scope.Scopes;
import  org.jwaresoftware.mwf4j.starters.StatementDependentSkeleton;

/**
 * Helper that encapsulates the reusable 'do-once' management code for 
 * reentrant statements like the {@linkplain ForEachStateemnt} or 
 * {@linkplain WhileStatement}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public final class ReentrantSupport extends StatementDependentSkeleton implements Resettable
{
    public ReentrantSupport(ControlFlowStatement owner, boolean scopedFlag)
    {
        super(owner);
        myScopedFlag = scopedFlag;
    }

    public ReentrantSupport(ControlFlowStatement owner, boolean scopedFlag, Unwindable unwinder)
    {
        this(owner,scopedFlag);
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
            //NB: ORDERING IS IMPORTANT HERE!!
            if (myScopedFlag) {
                myScope=Scopes.enter(getOwner(),harness);
            }
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
            //NB: ORDERING IS IMPORTANT HERE!!
            if (myUnwinder!=null) {
                harness.removeUnwind(myUnwinder);
            }
            if (myScopedFlag) {
                Scopes.leave(getOwner(),harness);
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
        myScope=null;
    }

    public Scope scope()
    {
        return myScope;
    }

    public void addRewindpoint(RewindCursor cursor)
    {
        Validate.fieldNotNull(myScope,What.SCOPE);
        myScope.addRewindpoint(cursor);
    }

    public void removeRewindpoint(RewindCursor cursor)
    {
        Validate.fieldNotNull(myScope,What.SCOPE);
        myScope.removeRewindpoint(cursor);
    }


    private boolean myCalledLatch;
    private Unwindable myUnwinder;
    private final boolean myScopedFlag;
    private Scope myScope;
}


/* end-of-ReentrantSupport.java */
