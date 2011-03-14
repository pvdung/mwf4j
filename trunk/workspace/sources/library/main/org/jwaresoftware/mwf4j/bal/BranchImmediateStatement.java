/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Variation of the standard {@linkplain BranchStatement branch} 
 * that runs the chosen statement immediately (does not return for
 * subsequent, possibly unordered re: containing sequence, execution.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class BranchImmediateStatement extends BranchStatement
{
    public BranchImmediateStatement(ControlFlowStatement next)
    {
        super(next);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement choice = super.runInner(harness);
        return choice.run(harness);
    }
}


/* end-of-BranchImmediateStatement.java */
