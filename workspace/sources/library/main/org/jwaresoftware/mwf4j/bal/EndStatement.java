/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * End or terminal statement; does nothing and loops back on self a fixed
 * number of times before throwing an internal error (potential infinite
 * loop detected). Often used as a "done" marker by other statements that can 
 * produce and/or execute other statements as part of their normal processnig.
 * You should not create shared end statements that are actually re-executed
 * without being reconfigured (see {@linkplain EmptyStatement}). To guard
 * against possible infinite loops (end statements calling itself forever), 
 * all end statements balk after being called five (5) times.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl
 * @see       EmptyStatement
 **/

public final class EndStatement extends BALTransientStatement
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
        if (myLoops++ > BAL.MAX_END_LOOPS)
            throw new PossibleInfiniteLoopException
                ("Potential infinite loop calling END statement '"+getWhatId()+"'");
        return this;
    }

    @Override
    public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        myLoops = 0;
        if (overrides!=null) {
            setWhatId(overrides);
        }
    }

    private int myLoops;
}


/* end-of-EndStatement.java */
