/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Feedback;

/**
 * Nonsense work to be done from a separate thread. Use as closure for
 * testing async actions like join, fork, etc.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public class Waiter extends Worker
{
    public Waiter(String name, Action owner) {
        super(name,owner);
    }

    public void setWaitTime(long wait) {
        Validate.isTrue(wait>0L,"wait is gt 0");
        getDataMap().put("delayms", Long.valueOf(wait));
        myWait = wait;
    }

    @Override
    protected void doWork() {
        super.doWork();
        Diagnostics.ForBAL.info("ZZzzz... for '{}'",getFQName());
        try { Thread.sleep(myWait);
        } catch(Exception ignored) {
            Feedback.ForBAL.error("Worker "+getFQName()+" boo-boo?!",ignored);
        }
    }

    private long myWait = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS);
}


/* end-of-Waiter.java */
