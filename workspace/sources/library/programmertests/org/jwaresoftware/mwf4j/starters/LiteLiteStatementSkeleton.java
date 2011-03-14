/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Starting point for very lightweight statements linked to lightweight test
 * actions like sleep, echo, touch, etc. By default will add name to test
 * fixture's "entered" and "left" names list. You can disable this behavior
 * by turning OFF the {@linkplain #setEnterLeaveMarked(boolean) mark 
 * enter-leave flag}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public abstract class LiteLiteStatementSkeleton extends StatementSkeleton
{
    public LiteLiteStatementSkeleton(Action owner, ControlFlowStatement next)
    {
        super(next);
        myOwner = owner;
    }

    public LiteLiteStatementSkeleton(String id, Action owner, ControlFlowStatement next)
    {
        this(owner,next);
        setId(id);
    }

    public void doEnter(Harness wrt)
    {
        super.doEnter(wrt);
        if (wantsEnterLeaveMarked()) {
            TestFixture.incStatementCount();
            addPerformedIfNamed();
        }
    }

    public void doLeave(Harness wrt)
    {
        if (wantsEnterLeaveMarked()) {
            addExitedIfNamed();
        }
        super.doLeave(wrt);
    }

    private String getName()
    {
        String name = myId;
        if (name==null) {
            Action owner = getOwner();
            if (owner!=null && !Strings.isEmpty(owner.getId())) {
                name = owner.getId();
            }
        }
        return name;
    }

    protected final void addPerformedIfNamed()
    {
        String name = getName();
        if (name!=null) {
            TestFixture.addPerformed(name);//Must work for multiple calls to same-named statement!
        }
    }

    protected final void addExitedIfNamed()
    {
        String name = getName();
        if (name!=null) {
            TestFixture.addExited(name);//Must work for multiple calls to same-named statement!
        }
    }

    public void setId(String id)
    {
        myId = id;
    }

    public final void setEnterLeaveMarked(boolean flag)
    {
        myEnterLeaveFlag=flag;
    }

    protected final boolean wantsEnterLeaveMarked()
    {
        return myEnterLeaveFlag;
    }

    public Action getOwner()
    {
        return myOwner;
    }

    private Action myOwner;
    protected String myId=null;
    private boolean myEnterLeaveFlag=true;//notify on enter+leave
}


/* end-of-LiteLiteStatementSkeleton.java */
