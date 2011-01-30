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
 * Helper statement that ensures a supplied action is run to its completion
 * before returning <em>its</em> continuation. Useful to ensure a supplied
 * handler (e.g. error or compensation) is run to completion within a single
 * statement's scope. Uses a nested harness to do the heavy lifting.
 *
 * @since    JWare/MWf4J 1.0.0
 * @author   ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version  @Module_VERSION@
 * @.safety  special (same as superclass)
 * @.group   infra,impl,helper
 * @see      org.jwaresoftware.mwf4j.harness.NestedHarnes NestedHarness
 **/

public final class InlineStatement extends BALStatement
{
    public InlineStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public InlineStatement(Action owner, Action body, ControlFlowStatement next)
    {
        super(owner,next);
        setBody(body);
    }

    public void setBody(Action body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        BALHelper.runInline(myBody,harness);
        return next();
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myBody,What.BODY);
    }

    public void reconfigure()
    {
        super.reconfigure();
        verifyReady();
    }

    private Action myBody;
}


/* end-of-InlineStatement.java */
