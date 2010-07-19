/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Action that blocks its parent thread for specified amount of time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public final class SleepAction extends TestExtensionPoint
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

    protected ControlFlowStatement runInner(Harness harness)
    {
        try {
            Thread.sleep(myMillis);
        } catch(InterruptedException iruptedX) {
            breadcrumbs().caught(iruptedX);
        }
        return next();
    }

    private long myMillis;
}


/* end-of-SleepAction.java */
