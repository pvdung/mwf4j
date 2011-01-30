/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Test statement to verify basic bits about incoming harness on 'run' handling.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public class TestStatement extends LiteLiteStatementSkeleton
{
    public TestStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    public TestStatement(String id, Action owner, ControlFlowStatement next)
    {
        this(owner,next);
        setId(id);
    }

    protected void doAssertions(Harness wrt)
    {
        assertNotNull(wrt,"harness");
        assertNotNull(wrt.getOwner(),"owner-activity");
        assertNotNull(wrt.getVariables(),"vars");
        assertNotNull(wrt.getConfiguration().getString("java.version"),"java.version");
        assertNotNull(wrt.getExecutorService(),"executor");
    }

    protected ControlFlowStatement runInner(Harness wrt)
    {
        doAssertions(wrt);
        return next();
    }
}


/* end-of-TestStatement.java */
