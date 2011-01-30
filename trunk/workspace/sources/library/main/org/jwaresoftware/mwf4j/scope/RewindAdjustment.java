/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Adjustment that will trigger a rewind to a predefined point that you 
 * supply at construction. Expects to be used ONCE by a single harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public final class RewindAdjustment extends ActionSkeleton implements Adjustment
{
    public RewindAdjustment(Rewindpoint mark)
    {
        this("rewind",mark);
    }

    public RewindAdjustment(String id, Rewindpoint mark)
    {
        super(id);
        Validate.notNull(mark,What.CURSOR);
        myContinuation = new RewindStatement(this,mark);
    }

    public boolean isTerminal()
    {
        return false;
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement notused)
    {
        return myContinuation;
    }

    public void configure(ControlFlowStatement statement)
    {
        throw new UnsupportedOperationException("rewindAdjustment.configure");
    }

    private ControlFlowStatement myContinuation;
}


/* end-of-RewindAdjustment.java */
