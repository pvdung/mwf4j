/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.StatementSkeleton;

/**
 * Activity ending or terminal statement; does nothing and loops 
 * back on self forever.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

final class ActivityEndStatement extends StatementSkeleton
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

    public void reconfigure()
    {
    }
}


/* end-of-ActivityEndStatement.java */
