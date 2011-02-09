/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Handle;
import  org.jwaresoftware.gestalt.helpers.Quietly;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that throws an exception; used for testing and other
 * task/flow diagnostics.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for makeStatement)
 * @.group    infra,impl
 * @see       ThrowStatement
 **/

public final class ThrowAction extends ActionSkeleton
{
    public ThrowAction()
    {
        this("throw");
    }

    public ThrowAction(Exception cause)
    {
        this();
        setCause(cause);
    }

    public ThrowAction(String id)
    {
        super(id);
    }

    public ThrowAction(String id, Exception cause)
    {
        this(id);
        setCause(cause);
    }

    public void setCause(Exception cause)
    {
        Validate.notNull(cause,What.EXCEPTION);
        myCause = cause;
    }

    Exception getCause()
    {
        return myCause;
    }

    public void setCause(String classname)
    {
        myCauseFQN = classname;
    }

    public void setAnnoucement(String announcement)
    {
        myAnnouncement = announcement;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,ThrowStatement.class,What.STATEMENT);
        ((ThrowStatement)statement).setCause(getCauseNoNull());
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        ThrowStatement statement = new ThrowStatement(this);
        return finish(statement);
    }

    private Exception getCauseNoNull()
    {
        Exception cause = myCause;
        if (cause==null) {
            if (myCauseFQN!=null) {
                Handle<Throwable> whups= new Handle<Throwable>();
                if (myAnnouncement==null) {
                    cause = Quietly.VM.newInstanceOrNull(myCauseFQN,Exception.class,whups);
                } else {
                    cause = Quietly.VM.newInstanceOrNull(myCauseFQN,Exception.class,myAnnouncement,whups);
                }
                if (cause==null && !whups.isNull()) {
                    cause = new MWf4JException(whups.get());
                }
            }
            if (cause==null) {
                cause = new MWf4JException(getClass().getSimpleName());
            }
        }
        return cause;
    }

    private Exception myCause;
    private String myCauseFQN;
    private String myAnnouncement;
}


/* end-of-ThrowAction.java */
