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
 * Statement for {@linkplain CheckPerformed} action. Separated to allow safe 
 * use of 'checkdone' in multi-threaded tests.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 * @see       CheckPerformed
 **/

public class CheckPerformedStatement extends LiteLiteStatementSkeleton
{
    public CheckPerformedStatement(String id, String statementNam, int count,
            Action owner, ControlFlowStatement next)
    {
        super(id,owner,next);
        this.statementName = statementNam;
        xpectedCount = count;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        boolean pass;
        //String tn = Thread.currentThread().getId()+":"+Thread.currentThread().getName();
        //TestFixture.print(TestFixture.getPerformed(),tn);
        if (xpectedCount<=0) {
            pass= TestFixture.wasPerformed(statementName);
            if (!pass) breadcrumbs().signaling(statementName+" NOT run");
            assertTrue(pass,statementName+" run");
        } else { 
            pass= TestFixture.wasPerformed(statementName,xpectedCount);
            if (!pass) breadcrumbs().signaling(statementName+" NOT run "+xpectedCount+" times");
            assertTrue(pass,statementName+" run "+xpectedCount+" times");
        }
        return next();
    }

    private final String statementName;
    private final int xpectedCount;
}


/* end-of-CheckPerformedStatement.java */
