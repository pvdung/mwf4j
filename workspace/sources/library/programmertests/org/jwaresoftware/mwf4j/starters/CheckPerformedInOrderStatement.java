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
 * Statement for {@linkplain CheckPerformedInOrder} action. Separated to  
 * allow safe use of 'checkdone' in multi-threaded tests.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 * @see       CheckPerformedInOrder
 **/

public final class CheckPerformedInOrderStatement extends LiteLiteStatementSkeleton
{
    public CheckPerformedInOrderStatement(String id, String statementNams,
            Action owner, ControlFlowStatement next)
    {
        super(id,owner,next);
        this.statementNames = statementNams;
        setEnterLeaveMarked(false);//DON'T INCLUDE SELF IN TEST!!!
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        boolean pass;
        pass= TestFixture.werePerformedInOrder(statementNames,'|',true);
        if (!pass) {
            String tn = Thread.currentThread().getId()+":"+Thread.currentThread().getName();
            ExecutableTestSkeleton.dmpPerformed(tn,TestFixture.getPerformed(),breadcrumbs());
            breadcrumbs().signaling("'"+statementNames+"' NOT run in order");
        }
        assertTrue(pass,"ran as '"+statementNames+"'");
        return next();
    }

    private final String statementNames;
}


/* end-of-CheckPerformedInOrderStatement.java */
