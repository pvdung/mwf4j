/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Helper statement that will create another opaque statement for
 * execution if and only if called. Use when the underlying action
 * is expensive to create and it might not be used (for example
 * as part of a branch or compensating activity). Also useful to
 * ensure a <em>NEW</em> target statement is created for every
 * invocation (for example as part of a compensating activity that
 * whose context can change over time or from a slave harness).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class IfCalledMakeStatement extends BALTransientStatement
{
    public IfCalledMakeStatement(Action target, ControlFlowStatement next)
    {
        super(next);
        setMade(target);
    }

    public IfCalledMakeStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public void setMade(Action target)
    {
        Validate.notNull(target,What.ACTION);
        myTarget = target;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        Validate.stateNotNull(myTarget,What.ACTION);//Do here in case anonymous
        return myTarget.makeStatement(next());
    }

    private Action myTarget;
}


/* end-of-IfCalledMakeStatement.java */
