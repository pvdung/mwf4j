/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Test statement that will post an end statement as a continuation 
 * (synchronous) and return next.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group   helper,test
 **/

public final class FinishLaterStatement extends StatementSkeleton
{
    public FinishLaterStatement(Action owner, ControlFlowStatement next) 
    {
        super(next);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement end = ControlFlowStatement.nullINSTANCE;
        harness.addContinuation(end);
        TestFixture.addPerformed(getWhatId());
        return next();
    }
}


/* end-of-FinishLaterStatement.java */
