/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;
import  java.util.concurrent.TimeoutException;

import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.DependentHarness;

/**
 * Statement that sits-n-waits (blocks) for payloads retrieved by predefined
 * listener (via a Callable interface). Each payload is then matched to an
 * action by a supplied lookup service, a new statement from that action is
 * created then run on this listen harness's parent harness (or a "work harness"
 * supplied at construction). We expect you to use this listen statement is
 * as part of a listen "forever" child or slave harness you launch from the
 * main application/harness. To stop a listening statement you must either
 * have the listener return <i>null</i> (interpreted as a time out event),
 * or interrupt the listening statement's harness (interpreted as a
 * 'graceful request for a continuation to next statement').
 * <p/>
 * Usage note: if you run a listen statement as part of a forked thread,
 * you must be careful about enabling the "halt-if-error" option. If any
 * errors occur, the listen statement will trigger an abort for its harness,
 * which will <em>NOT</em> run the next statement (the join barrier notify).
 * If the fork does not use a barrier (default one does not) then this break
 * will not be detected.
 * <p/>
 * Usage note: to stop a listening statement you can do one of two things.
 * First you can interrupt the listening statement's harness' thread (recommended).
 * The listen statement interprets an interrupt as a "GRACEFUL" break or
 * a request to stop listening. Alternatively, to stop listening you can
 * return <i>null</i> as a payload from your callback; however, the listen
 * statement interprets a null payload as a timeout or unexpected exception
 * and triggers its "halt-if-error" handling. If you've disabled the
 * statement's halt-if-error flag-- NOTHING happens; the statement will
 * log the exception AND CONTINUE LISTENING. So, if you want to return
 * <i>null</i> to stop the listening, you need to ensure the statement's
 * halt-if-error flag is <em>enabled</em>. Note that instead of returning
 * <i>null</i> you can also interrupt the current thread from the callback
 * when you determine you want to stop listening.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       LaunchAction
 **/

public class ListenStatement<T> extends BALProtectorStatement implements Unwindable
{
    public ListenStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myUnwindSupport = new ReentrantSupport(this,false,this);
    }

    public void setListener(Callable<T> listener)
    {
        Validate.notNull(listener, What.CALLBACK);
        myListener = listener;
    }

    public void setLookupService(ActionLookupMethod<T> service)
    {
        Validate.notNull(service,What.SERVICE);
        myFactory = service;
    }

    public void setWorkHarness(Harness harness)
    {
        Validate.notNull(harness,What.HARNESS);
        myWorkHarness = harness;
    }

    public void setBreakSupport(Action breakAction)
    {
        Validate.notNull(breakAction,What.ACTION);
        myBreakAction = breakAction;
    }

    private T getNextPayload(Harness harness) throws Exception
    {
        MDC.pshHarness(this,harness);
        try {
            return myListener.call();
        } finally {
            MDC.popHarness(this,harness);
        }
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next = this;
        final long enteredAt = LocalSystem.currentTimeMillis();
        try {
            myUnwindSupport.loop(harness);
            T payload = getNextPayload(harness);
            if (payload!=null) {
                Action action = myFactory.create(payload, harness);
                Validate.responseNotNull(action,What.ACTION);
                ControlFlowStatement continuation = action.makeStatement(new EndStatement(action));
                getExecutorHarness(harness).addContinuation(continuation);
            } else {
                long roughTimeoutDuration = LocalSystem.currentTimeMillis()-enteredAt;
                throw new TimeoutException("Listen '"+getWhatId()+"' timed out after ~"+roughTimeoutDuration+"MILLISECONDS");
            }
        } catch(InterruptedException iruptedX) {
            breadcrumbs().write("Listen '{}' interrupted; stopping now",getWhatId());
            next = next();
        } catch(Exception otherX) {
            Throwable peek = Throwables.getCauseIfVMWrapper(otherX,true);
            if (peek instanceof InterruptedException) {
                breadcrumbs().write("Listen '{}' interrupted; stopping now",getWhatId());
                next = next();
            } else {
                next = handleBreak(otherX,harness);
            }
        }
        if (next!=this) {
            finishThis(harness);
        }
        return next;
    }

    protected ControlFlowStatement handleBreak(final Exception issue, Harness harness)
    {
        if (breadcrumbs().isEnabled()) {
            String because=Throwables.getTypedMessage(issue);
            breadcrumbs().write("Listen '{}' break detected: {}",getWhatId(),because);
        }
        BALHelper.runBreakAction(issue,myBreakAction,harness);

        final ControlFlowStatement kontinue = new EndStatement();
        if (myErrorKey!=null) {
            BALHelper.putData(myErrorKey,issue,myErrorStoreType,harness);
        }
        ControlFlowStatement next = myTrySupport.handle(kontinue,new ThrowStatement(getOwner(),issue),harness);
        if (next==kontinue) {
            next = this;//NB: keep listening...
            if (myErrorKey!=null) {
                BALHelper.clrData(myErrorKey,myErrorStoreType,harness);
            }
        }
        return next;
    }

    private Harness getExecutorHarness(Harness mine)
    {
        Harness h = myWorkHarness;//Can == mine but is not expected case!
        if (h==null) {
            if (mine instanceof DependentHarness) {
                //Often a listen runs on a slave harness launched from a forever harness.
                h = ((DependentHarness)mine).getHarnessDependentOn();
            }
        }
        Validate.stateNotNull(h,What.HARNESS);
        return h;
    }

    final void resetThis()
    {
        super.resetThis();
        myListener=null;
        myFactory=null;
        myWorkHarness=null;
        myBreakAction=null;
        myUnwindSupport.reset(this);
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myListener,What.GET_METHOD);
        Validate.stateNotNull(myFactory,What.FACTORY_METHOD);
    }

    private void finishThis(Harness harness)
    {
        myUnwindSupport.finished(harness);
        resetThis();
    }

    public void unwind(Harness harness)
    {
        resetThis();
    }

    private ReentrantSupport myUnwindSupport;
    private Callable<T> myListener;
    private ActionLookupMethod<T> myFactory;
    private Harness myWorkHarness;//OPTIONAL
    private Action myBreakAction;//OPTIONAL
}


/* end-of-ListenStatement.java */
