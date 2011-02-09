/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Signal;
import  org.jwaresoftware.mwf4j.starters.MWf4JWrapException;

/**
 * Flow statement that on execution throws a given exception. Useful
 * for delayed signals (like when a 'tryeach' is in effect for a 
 * sequence). Note that a throw statement is <em>not</em> considered
 * terminal.
 * <p/>
 * Ensures the thrown exception is-a RuntimeException. Next statement
 * (if every queried) is always and {@linkplain EndStatement end
 * statement}. For index-based composites, you can associate a position
 * marker with each throw which you can use to determine the whether a
 * rewind affects delayed signals.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       RunFailedException
 **/

public final class ThrowStatement extends BALStatement implements Signal
{
    public ThrowStatement(Action owner, Exception cause)
    {
        super(owner,null);
        Validate.notNull(cause,What.EXCEPTION);
        setCause(cause);
    }

    ThrowStatement(Action owner)
    {
        super(owner,null);
    }

    public ThrowStatement(Action owner, Exception cause, String announcement)
    {
        this(owner,cause);
        myAnnouncement = announcement; 
    }

    public ThrowStatement(Signal other, Action link)
    {
        Validate.notNull(other,What.SOURCE);
        Exception cause = other.getCause();
        Validate.notNull(cause,What.EXCEPTION);
        Action owner = other.getOwner();
        if (other.isAnonymous() && link!=null) owner = link;
        initOwner(owner);
        setCause(cause);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        if (myAnnouncement!=null) {
            harness.getIssueHandler().problemOccured(myAnnouncement, Effect.ABORT, theCause);
        }
        RuntimeException signal;
        if (theCause instanceof RuntimeException) {
            signal = (RuntimeException)theCause;
        } else {
            signal = new MWf4JWrapException(theCause);
        }
        breadcrumbs().throwing(signal);
        throw signal;
    }

    @Override
    public final Exception getCause()
    {
        return theCause;
    }

    final void setCause(Exception cause)
    {
        theCause = cause;
    }

    public void setNextThrown(ThrowStatement lastThrown)
    {
        if (lastThrown!=null) {
            length += lastThrown.getLength();
        }
        nextThrown = lastThrown;
    }

    public ThrowStatement nextThrown()
    {
        return this.nextThrown;
    }

    public void setPosition(int internalMark)
    {
        myPosition = internalMark;
    }

    @Override
    public final int getPosition()
    {
        return myPosition;
    }

    public void reconfigure()
    {
        nextThrown = null;
        length = 1;
        myPosition = -1;
        super.reconfigure();
    }

    final void replaceCause(Exception wrapperCause)
    {
        theCause = wrapperCause;
        if (nextThrown!=null) {
            nextThrown.replaceCause(wrapperCause);
        }
    }

    final int getLength()
    {
        return length;
    }

    protected StringBuilder addToString(StringBuilder sb)
    {
        String message = Throwables.getTypedMessage(getCause());
        sb = super.addToString(sb);
        if (getLength()>1) sb.append("|depth=").append(getLength());
        sb.append("|root='").append(Strings.abbreviate(message,30)).append("'");
        return sb;
    }

    public ControlFlowStatement next()
    {
        return new EndStatement(getOwner());
    }

    private Exception theCause;
    private ThrowStatement nextThrown;
    private int length=1;
    private String myAnnouncement;//OPTIONAL
    private int myPosition= -1;//OPTIONAL
}


/* end-of-ThrowStatement.java */
