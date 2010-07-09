/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;


/**
 * Test statement to verify basic bits about incoming harness on 'run' handling.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public class TestStatement extends StatementSkeleton
{
    public TestStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    protected void doAssertions(Harness wrt)
    {
        assertNotNull(wrt,"harness");
        assertNotNull(wrt.getOwner(),"owner-activity");
        assertNotNull(wrt.getVariables(),"vars");
        assertNotNull(wrt.getConfiguration().getString("java.version"),"java.version");
        assertNotNull(wrt.getExecutorService(),"executor");
    }

    public void doEnter(Harness wrt)
    {
        super.doEnter(wrt);
        TestFixture.incStatementCount();
        addPerformedIfNamed();
    }

    protected final void addPerformedIfNamed()
    {
        String name = myId;
        if (name==null) {
            Action owner = getOwner();
            if (owner!=null && !Strings.isEmpty(owner.getId())) {
                name = owner.getId();
            }
        }
        if (name!=null) {
            TestFixture.addPerformed(name);//Must work for multiple calls to same-named statement!
        }
    }

    protected ControlFlowStatement runInner(Harness wrt)
    {
        doAssertions(wrt);
        return next();
    }

    void setId(String id)
    {
        myId = id;
    }

    protected String myId=null;
}


/* end-of-TestStatement.java */
