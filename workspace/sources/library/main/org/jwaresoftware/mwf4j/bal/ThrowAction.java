/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that throws an exception; used for testing and other
 * task/flow diagnostics.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
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

    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof ThrowStatement,"statement kindof throw");
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
            cause = new MWf4JException(getClass().getSimpleName());
        }
        return cause;
    }

    private Exception myCause;
}


/* end-of-ThrowAction.java */
