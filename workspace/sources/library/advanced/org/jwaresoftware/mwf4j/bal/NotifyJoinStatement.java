/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.CountDownLatch;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Statement that notifies a waiting join that its owning action has completed.
 * Typically used as the N-1 step in a forked action's sequence (next step would
 * be the end statement for the slave harness. We expect the controlling parent
 * harness to monitor the countdown latch.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public class NotifyJoinStatement extends BALStatement
{
    protected NotifyJoinStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        //resetThis();
    }

    public NotifyJoinStatement(Action owner, String forkId, CountDownLatch barrier)
    {
        this(owner,new EndStatement(owner));
        setForkId(forkId);
        setBarrier(barrier);
    }

    public void setBarrier(CountDownLatch barrier)
    {
        Validate.notNull(barrier,What.BARRIER);
        myBarrier = barrier;
    }

    public void setForkId(String forkId)
    {
        Validate.notNull(forkId,What.ID);
        myForkId = forkId;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        Validate.stateNotNull(myBarrier,What.BARRIER);
        myBarrier.countDown();
        notifyCompleted(harness);

        ControlFlowStatement next = next();
        resetThis();
        return next;
    }

    protected void notifyCompleted(Harness harness)
    {
        breadcrumbs().write("Notified join-point '{}' from '{}'",myForkId,getWhatId());
    }

    private void resetThis()
    {
        myBarrier = null;
        myForkId = Strings.UNKNOWN;
    }

    public void reconfigure()
    {
        resetThis();
        super.reconfigure();
        verifyReady();
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myBarrier,What.BARRIER);
    }

    private CountDownLatch myBarrier;
    private String myForkId;
}


/* end-of-NotifyJoinStatement.java */
