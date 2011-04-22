/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;

/**
 * Adapter to convert a {@linkplain Runnable} to a {@linkplain Callable}.
 * Will call underlying runnable and return <i>null</i> unconditionally.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class CalledRunnable<T> extends CloneableSkeleton 
    implements Callable<T>, Declarable
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

    public void freeze(Fixture environ)
    {
        myImpl = Declarables.freeze(environ,myImpl);
    }

    @SuppressWarnings("unchecked")
    public Object clone()
    {
        CalledRunnable copy = (CalledRunnable)super.clone();
        copy.myImpl = LocalSystem.newCopyOrSame(myImpl);
        return copy;
    }

    private Runnable myImpl;
}


/* end-of-CalledRunnable.java */
