/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.starters.StatementSkeleton;

/**
 * Starting implementation for BAL related control flow statements. Mostly
 * ensures feedback goes to expected loggers. Otherwise, identical to the
 * inherited common superclass.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class BALStatement extends StatementSkeleton
{
    protected BALStatement(ControlFlowStatement next)
    {
        super(next);
    }


    protected BALStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }
}


/* end-of-BALStatement.java */
