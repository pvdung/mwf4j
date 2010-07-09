/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * End or terminal statement; does nothing and loops back on 
 * self forever.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl
 **/

public final class EndStatement extends BALStatement
{
    public EndStatement()
    {
        super(null);
    }

    public EndStatement(Action owner)
    {
        super(owner,null);
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


/* end-of-EndStatement.java */
