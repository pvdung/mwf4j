/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Empty statement; does nothing, zippo, zilch, nada, bumpkus.
 * An empty statement is NOT terminal; it queues up whatever is
 * passed as the next statement unconditionally.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl
 **/

public final class EmptyStatement extends BALStatement
{
    public EmptyStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public EmptyStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        return next();
    }
}


/* end-of-EmptyStatement.java */
