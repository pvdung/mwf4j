/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

/**
 * Mixin interface for any component that restricts the number of times it
 * can execute. Typically used for bounded loops, rewinds, error handlers,
 * etc.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 **/

public interface CallBounded
{
    void setHaltIfMax(boolean flag);
    void setMaxIterations(int max);
    void setUseHaltContinuation(boolean flag);
}


/* end-of-CallBounded.java */
