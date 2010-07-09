/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;

/**
 * Test action that does nothing but increment the MDC-based statement 
 * counter by one and update the MDC 'names' list with a unique timestamp.
 * Whenever a call to {@linkplain #makeStatement makeStatment} is used, 
 * the new test statement is given a unique timestamp-based id which it 
 * will save to MDC 'names' list when run (will same same id if run multiple
 * times). Useful to test actions that can differentiate between a single 
 * reused statement and a statement factory (e.g. loops).
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    helper,test
 * @see       TestStatement
 **/

public final class StampAction extends ActionSkeleton
{
    public StampAction()
    {
        this("stamp");
    }

    public StampAction(String id)
    {
        super(id);
        updateStamp();
    }

    public String updateStamp()
    {
        myTime = getId()+":"+String.valueOf(LocalSystem.currentTimeNanos());
        return getLastStamp();
    }

    public final String getLastStamp()
    {
        return myTime;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof TestStatement,"statement kindof TestStatement");
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        TestStatement statement = new TestStatement(this,next);
        statement.setId(updateStamp());
        return finish(statement);
    }

    private String myTime;
}


/* end-of-StampAction.java */
