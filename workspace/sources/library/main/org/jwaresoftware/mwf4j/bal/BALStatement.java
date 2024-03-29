/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.starters.StatementSkeleton;

/**
 * Starting implementation for BAL related control flow statements. Marker
 * class; otherwise, identical to the inherited skeleton superclass.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class BALStatement extends StatementSkeleton
{
    protected BALStatement()
    {
        super();
    }

    protected BALStatement(ControlFlowStatement next)
    {
        super(next);
    }

    protected BALStatement(Action action, ControlFlowStatement next)
    {
        super(next);
    }
}


/* end-of-BALStatement.java */
