/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  static org.testng.Assert.assertTrue;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Statement for {@linkplain CheckUnwound} action. Separated to allow safe 
 * use of 'checkunwound' in multi-threaded tests.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 * @see       CheckUnwound
 **/

public class CheckUnwoundStatement extends LiteLiteStatementSkeleton
{
    public CheckUnwoundStatement(String id, String unwinderNam,
            Action owner, ControlFlowStatement next)
    {
        super(id,owner,next);
        this.unwinderName = unwinderNam;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        boolean pass = TestFixture.wasUnwound(unwinderName);
        if (!pass) breadcrumbs().signaling("Failed assertion: "+unwinderName+" unwound");
        assertTrue(pass,unwinderName+" unwound");
        return next();
    }

    private String unwinderName;
}


/* end-of-CheckUnwoundStatement.java */
