/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Activity ending or terminal statement; does nothing and loops back on
 * self forever.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

final class ActivityEndStatement extends TransientStatementSkeleton
{
    ActivityEndStatement()
    {
        super(null);
    }

    public boolean isTerminal()
    {
        return true;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        return this;
    }
}


/* end-of-ActivityEndStatement.java */
