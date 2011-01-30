/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.atomic.AtomicBoolean;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.LongLivedCondition;

/**
 * Test condition that always returns <i>false</i> after being started
 * and called once. Use to verify that long-lived conditions are properly
 * started by controlling statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    test,helper
 **/

public final class OnceAfterStarted implements LongLivedCondition
{
    public void start(long now)
    {
        Validate.stateIsTrue(myInited.compareAndSet(false,true),"started ONCE");
    }

    public final void start()
    {
        start(LocalSystem.currentTimeMillis());
    }

    public boolean evaluate(Harness ignored)
    {
        Validate.stateIsTrue(myInited.get(),"started");
        return myFlag.compareAndSet(false,true);
    }

    private AtomicBoolean myInited = new AtomicBoolean();
    private AtomicBoolean myFlag = new AtomicBoolean();
}


/* end-of-OnceAfterStarted.java */
