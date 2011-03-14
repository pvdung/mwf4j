/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;

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
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isA(statement,TestStatement.class,What.STATEMENT);
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        TestStatement statement = new TestStatement(this,next);
        statement.setId(updateStamp());
        return statement;
    }

    private String myTime;
}


/* end-of-StampAction.java */
