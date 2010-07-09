/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.What;

/**
 * Struct that describes how many times a function or other executing 
 * entity should retry a failed attempt at "something". Includes optional
 * sleep time definition for between retries (defaults to one(1) retry 
 * with one(1s) second between tries).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class RetryDef
{
    public RetryDef()
    {
        super();
    }
 
    public RetryDef(int retries, long millis)
    {
        setRetryCount(retries);
        setRetryWait(new WaitDef(millis));
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

    public void setRetryWait(WaitDef def)
    {
        Validate.notNull(def,What.CRITERIA);
        myRetryTimeout = def;
    }

    public WaitDef getRetryWait()
    {
        return myRetryTimeout;
    }

    private int myRetryCount = 1;
    private WaitDef myRetryTimeout= new WaitDef(1L,TimeUnit.SECONDS);
}


/* end-of-RetryDef.java */
