/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;

/**
 * Adapter to convert a {@linkplain Runnable} to a {@linkplain Callable}.
 * Will call underlying runnable and return <i>null</i> unconditionally.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class CalledRunnable<T> implements Callable<T>
{
    public CalledRunnable(Runnable worker)
    {
        Validate.notNull(worker,What.CALLBACK);
        myImpl = worker;
    }

    public T call() throws Exception
    {
        myImpl.run();
        return null;
    }

    public String toString() 
    {
        return Strings.valueOf(myImpl);
    }

    private final Runnable myImpl;
}


/* end-of-CalledRunnable.java */
