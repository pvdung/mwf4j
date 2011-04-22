/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;

import  org.jwaresoftware.mwf4j.What;

/**
 * Struct that describes how many times a function or other executing 
 * entity should retry a failed attempt at "something". Includes optional
 * sleep time definition for between retries (defaults to one(1) retry 
 * with one(1s) second between tries).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class RetryDef extends CloneableSkeleton
{
    /**
     * Initializes a new retry definition that will retry ONCE 
     * after waiting one second.
     **/
    public RetryDef()
    {
        myRetryCount = 1;
        myRetryTimeout= new WaitDef(1L,TimeUnit.SECONDS);
    }
 
    /**
     * Initializes a new retry definition that will retry the specified
     * number of times with <i>millis</i> millisecond sleep time between
     * each.
     * @param retries number of retries (gte zero)
     * @param millis sleep time in milliseconds BETWEEN retries
     **/
    public RetryDef(int retries, long millis)
    {
        setRetryCount(retries);
        setRetryWait(new WaitDef(millis));
    }

    public static RetryDef newNoRetry()
    {
        return new RetryDef(0,-1L);
    }

    public void setRetryCount(int retries)
    {
        Validate.isFalse(retries<0,"retries<0");
        myRetryCount = retries;
    }

    public int getRetryCount()
    {
        return myRetryCount;
    }

    public int decrementAndGetRetryCount()
    {
        if (myRetryCount>0) myRetryCount--;
        return myRetryCount;
    }

    public void setRetryWait(WaitDef def)
    {
        Validate.notNull(def,What.CRITERIA);
        myRetryTimeout = def;
    }

    public WaitDef getRetryWait()
    {
        return myRetryTimeout;
    }

    private int myRetryCount;
    private WaitDef myRetryTimeout;
}


/* end-of-RetryDef.java */
