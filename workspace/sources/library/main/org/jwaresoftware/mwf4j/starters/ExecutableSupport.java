/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Executable;

/**
 * Helper that executes the callback methods of an {@linkplain Executable}
 * instance, ignoring any exceptions that might occur.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class ExecutableSupport
{
    public static boolean doEnter(Harness harness, Object target)
    {
        boolean clean=true;
        if (target instanceof Executable) {
            try {
                ((Executable)target).doEnter(harness);
            } catch(RuntimeException anyX) {
                Diagnostics.ForFlow.catching(anyX);
                clean=false;
            }
        }
        return clean;
    }

    public static boolean doLeave(Harness harness, Object target)
    {
        boolean clean=true;
        if (target instanceof Executable) {
            try {
                ((Executable)target).doLeave(harness);
            } catch(RuntimeException anyX) {
                Diagnostics.ForFlow.catching(anyX);
                clean=false;
            }
        }
        return clean;
    }

    public static boolean doError(Harness harness, Object target, Throwable issue)
    {
        boolean clean=true;
        if (target instanceof Executable) {
            try {
                ((Executable)target).doError(harness,issue);
            } catch(RuntimeException anyX) {
                Diagnostics.ForFlow.catching(anyX);
                clean=false;
            }
        }
        return clean;
    }

    private ExecutableSupport() { } //Only public static APIs
}


/* end-of-ExecutableSupport.java */
