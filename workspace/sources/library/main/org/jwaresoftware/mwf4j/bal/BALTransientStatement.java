/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;

/**
 * Starting implementation for a statement that is transient in nature-- 
 * i&#46;e&#46; is rarely associated with a shell action and is most often
 * used as part of another statement's or an action's implementation.  
 * Because transient statements aren't controlled directly by default, their 
 * 'reconfigure' method does nothing by default (it does <em>NOT</em> call
 * its linked action to configure it as that action most likely does not know
 * about the transient statement).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public abstract class BALTransientStatement extends BALStatement
{
    protected BALTransientStatement(Action action, ControlFlowStatement next)
    {
        super(action,next);
    }

    protected BALTransientStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public void reconfigure()
    {
        //nothing (do NOT call action to configure even if one linked)
    }
}


/* end-of-BALTransientStatement.java */
