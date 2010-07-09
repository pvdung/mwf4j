/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Implementation of an empty (no-op) action.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl
 **/

public final class EmptyAction extends ActionSkeleton
{
    public EmptyAction()
    {
        super();
    }

    public EmptyAction(String id)
    {
        super(id);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        ControlFlowStatement empty = new EmptyStatement(this,next);
        return finish(empty);
    }

    public void configure(ControlFlowStatement owned)
    {
        //nothing
    }
}


/* end-of-EmptyAction.java */
