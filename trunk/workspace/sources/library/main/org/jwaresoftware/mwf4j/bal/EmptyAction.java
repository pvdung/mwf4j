/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Implementation of an empty (no-op) action.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new EmptyStatement(next);
    }

    public void configureStatement(ControlFlowStatement owned, Fixture environ)
    {
        //nothing
    }
}


/* end-of-EmptyAction.java */
