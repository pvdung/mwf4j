/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.Callable;
import  java.util.concurrent.Future;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;

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

public final class CalledFuture<T> extends CloneableSkeleton 
    implements Callable<T>, Declarable
{
    public CalledFuture(Future<? extends T> worker)
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

    public void freeze(Fixture environ)
    {
        myImpl = Declarables.freeze(environ,myImpl);
    }

    @SuppressWarnings("unchecked")
    public Object clone()
    {
        CalledFuture copy = (CalledFuture)super.clone();
        copy.myImpl = LocalSystem.newCopyOrSame(myImpl);
        return copy;
    }

    private Future<? extends T> myImpl;
}


/* end-of-CalledFuture.java */
