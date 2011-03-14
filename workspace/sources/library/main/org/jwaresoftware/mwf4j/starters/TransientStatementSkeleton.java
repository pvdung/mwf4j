/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Fixture;

/**
 * Starting implementation for a statement that is transient in nature-- 
 * i&#46;e&#46; is NOT associated with an independent definition (or Action)
 * and is most often used as part of another statement's or action's
 * implementation.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class TransientStatementSkeleton extends StatementSkeleton
{
    protected TransientStatementSkeleton()
    {
        super();
    }

    protected TransientStatementSkeleton(ControlFlowStatement next)
    {
        super(next);
    }

    public final void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        verifyReady();
    }
}


/* end-of-TransientStatementSkeleton.java */
