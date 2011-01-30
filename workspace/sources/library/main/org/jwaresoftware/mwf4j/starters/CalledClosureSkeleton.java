/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.Callable;

/**
 * Closure that will be triggered via a standard {@linkplain Callable}
 * interface.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class CalledClosureSkeleton<T> extends ClosureSkeleton implements Callable<T>
{
    protected CalledClosureSkeleton()
    {
        super();
    }

    @Override
    public final T call() throws Exception
    {
        prepare();
        try {
            return callInner();
        } finally {
            cleanup();
        }
    }

    protected abstract T callInner() throws Exception;
}


/* end-of-CalledClosureSkeleton.java */
