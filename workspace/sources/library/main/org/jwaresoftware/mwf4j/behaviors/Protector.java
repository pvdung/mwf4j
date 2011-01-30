/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

/**
 * Mixin interface for any component that we can direct to <em>NOT</em>
 * abort (immediately) in the event of a runtime exception. Typically 
 * implemented by composite actions on behalf of the associated statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 **/

public interface Protector
{
    void setHaltIfError(boolean flag);
    void setQuiet(boolean flag);
    void setUseHaltContinuation(boolean flag);
}


/* end-of-Protector.java */
