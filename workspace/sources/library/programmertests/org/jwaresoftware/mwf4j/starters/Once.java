/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.atomic.AtomicBoolean;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Test condition that always returns <i>false</i> after being called once.
 * First time always returns <i>true</i> implying "continue"; all calls 
 * thereafter return <i>false</i>. Works if called from multiple threads too.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    test,helper
 **/

public final class Once implements Condition
{
    public boolean evaluate(Harness ignored)
    {
        return myFlag.compareAndSet(false,true);
    }

    private AtomicBoolean myFlag = new AtomicBoolean();
}


/* end-of-Once.java */
