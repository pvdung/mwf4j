/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

/**
 * Closure that will be triggered via a standard {@linkplain Runnable}
 * interface.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class RunnableClosureSkeleton extends ClosureSkeleton implements Runnable
{
    protected RunnableClosureSkeleton()
    {
        super();
    }

    @Override
    public final void run()
    {
        prepare();
        try {
            runInner();
        } finally {
            cleanup();
        }
    }

    protected abstract void runInner();
}


/* end-of-CalledClosureSkeleton.java */
