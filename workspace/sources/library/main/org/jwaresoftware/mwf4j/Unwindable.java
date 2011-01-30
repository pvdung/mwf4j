/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;


/**
 * Control flow statements can implement this mixin interface to request the
 * activity run harness notify them if a premature stop for the activity has
 * occured. Gives "loop-back" or long-lived type statements a chance to
 * cleanup. Implementors should ignore request if no cleanup needed or cleanup
 * has been done.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public interface Unwindable
{
    void unwind(Harness harness);
}


/* end-of-Unwindable.java */
