/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.Callable;
import  java.util.concurrent.Future;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;

/**
 * Adapter to convert a {@linkplain Future} to a {@linkplain Callable}.
 * Will trigger the future's <em>blocking</em> get() method!
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class CalledFuture<T> implements Callable<T>
{
    public CalledFuture(Future<T> worker)
    {
        Validate.notNull(worker,What.CALLBACK);
        myImpl = worker;
    }

    public T call() throws Exception
    {
        return myImpl.get();
    }

    public String toString() 
    {
        return Strings.valueOf(myImpl);
    }

    private final Future<T> myImpl;
}


/* end-of-CalledFuture.java */
