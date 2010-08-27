/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Harness started marker statement; does nothing. Must never participate
 * in any "real" execution flow.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

final class HarnessRunStatement implements ControlFlowStatement
{
    HarnessRunStatement(Harness harness)
    {
        super();
        myId = Long.valueOf(System.identityHashCode(harness));//ASSUMES: identity UNIQUE
    }

    public Action getOwner()
    {
        return null;
    }

    public boolean isTerminal()
    {
        return true;
    }

    public boolean isAnonymous()
    {
        return true;
    }

    public ControlFlowStatement run(Harness harness)
    {
        throw new UnsupportedOperationException("harnessMark.run");
    }

    public void reconfigure()
    {
        throw new UnsupportedOperationException("harnessMark.reconfigure");
    }

    public ControlFlowStatement next()
    {
        return null;
    }

    public boolean equals(Object other)
    {
        if (other==this)
            return true;
        if (other==null)
            return false;
        if (getClass().equals(other.getClass()))
            return myId.equals(((HarnessRunStatement)other).myId);
        return false;
    }

    public int hashCode()
    {
        return myId.hashCode(); 
    }

    private final Long myId;
}


/* end-of-HarnessRunStatement.java */
