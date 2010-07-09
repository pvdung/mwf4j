/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.TimeUnit;

import  org.apache.commons.lang.Validate;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.behaviors.Traceable;
import  org.jwaresoftware.mwf4j.helpers.WaitDef;

/**
 * Variation of {@linkplain TraceSupport} for long-lived (and very frequently
 * called) components like adhoc continuations. Will only echo the standard
 * enter/leave message at specified intervals.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class LongLivedTraceSupport extends TraceSupport implements Resettable
{
    public static final WaitDef DEFAULT_ECHO_INTERVAL= 
        new WaitDef(15L,TimeUnit.SECONDS);


    public LongLivedTraceSupport(final Traceable link)
    {
        this(link,DEFAULT_ECHO_INTERVAL);
    }


    public LongLivedTraceSupport(final Traceable link, WaitDef interval)
    {
        super(link);
        Validate.notNull(interval,What.CRITERIA);
        myInterval = interval.toMillis();
        this.reset();
    }


    public void reset()
    {
        myFirstTimeFlag=true;//latch: clr on 1st call into 'doEnter'
        myEchoLeaveFlag=false;
        myWhenLastEchoed=0L;
    }


    public void doEnter(Harness h)
    {
        if (isEnabled()) {
            long now = LocalSystem.currentTimeMillis();
            if (myFirstTimeFlag) {
                myFirstTimeFlag= !myFirstTimeFlag;
                enteringThis();
                myWhenLastEchoed = now;
                myEchoLeaveFlag= true;
            }
            else {
                if (now-myWhenLastEchoed>=myInterval) {
                    enteringThis();
                    myWhenLastEchoed=now;
                    myEchoLeaveFlag= true;
                }
            }
        }
    }


    public void doNexxt(Object next, Harness h)
    {
        if (myEchoLeaveFlag) {
            super.doNexxt(next,h);
        }
    }


    public void doLeave(Harness h)
    {
        if (myEchoLeaveFlag) {
            myEchoLeaveFlag= false;
            leavingThis();
        }
    }


    private final long myInterval;
    private boolean myEchoLeaveFlag;
    private boolean myFirstTimeFlag;
    private long myWhenLastEchoed;

}


/* end-of-LongLivedTraceSupport.java */
