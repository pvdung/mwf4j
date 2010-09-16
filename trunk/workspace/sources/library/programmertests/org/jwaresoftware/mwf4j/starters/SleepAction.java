/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Action that blocks its parent thread for specified amount of time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple (once configured)
 * @.group    impl,test,helper
 * @see       SleepStatement
 **/

public final class SleepAction extends ActionSkeleton
{
    public final static String idFrom(long millis)
    {
        return "sleep{"+millis+"ms}";
    }

    public SleepAction()
    {
        this(TimeUnit.SECONDS.toMillis(2L));
    }

    public SleepAction(long millis)
    {
        super(idFrom(millis));
        myMillis = millis;
    }

    public SleepAction(String id, long millis)
    {
        super(id);
        myMillis = millis;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,SleepStatement.class,What.STATEMENT);
        ((SleepStatement)statement).setMillis(myMillis);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        SleepStatement sleep = new SleepStatement(getId(),this,next);
        return finish(sleep);
    }

    private long myMillis;
}


/* end-of-SleepAction.java */
