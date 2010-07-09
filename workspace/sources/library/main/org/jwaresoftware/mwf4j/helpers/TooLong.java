/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.LongLivedCondition;
import  org.jwaresoftware.mwf4j.What;

/**
 * Condition that checks current system time (millis) to see
 * if a certain duration has been passed.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public class TooLong implements LongLivedCondition
{
    public TooLong(long duration, TimeUnit uom)
    {
        Validate.notNull(uom,What.CRITERIA);
        myDuration = TimeUnit.MILLISECONDS.convert(duration,uom);
    }

    public void start(long starttime)
    {
        Validate.isFalse(starttime<0L,"start-time negative");
        myStartTime = Long.valueOf(starttime);
    }

    public void start() 
    {
        start(LocalSystem.currentTimeMillis());
    }

    public boolean evaluate(Harness context)
    {
        Validate.stateNotNull(myStartTime,"start-time");
        long now = LocalSystem.currentTimeMillis();
        return (now-myStartTime>myDuration);
    }

    private Long myStartTime;
    private long myDuration;
}


/* end-of-TooLong.java */
