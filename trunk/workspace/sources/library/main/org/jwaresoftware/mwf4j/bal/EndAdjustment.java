/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementException;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.ExtensionPoint;

/**
 * End or terminal adjustment; does nothing and loops back on self a fixed 
 * number of times before throwing an internal error (potential infinite
 * loop detected). WARNING: meant for use as an adjustment not a statement.
 * See {@linkplain EndStatement} for the equivalent statement.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (for use only to terminal a harness run)
 * @.group    infra,impl,helper
 **/

public final class EndAdjustment extends ExtensionPoint implements Adjustment
{
    public EndAdjustment()
    {
        this("end");
    }

    public EndAdjustment(String id)
    {
        super(id);
    }

    public boolean isTerminal()
    {
        return true;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        if (myLoops++ > BAL.MAX_END_LOOPS)
            throw new ControlFlowStatementException
                ("Potential infinite loop calling END adjustment '"+getWhatId()+"'");
        return this;
    }

    private int myLoops;
}


/* end-of-EndAdjustment.java */
