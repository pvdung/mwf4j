/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

/**
 * Cause of breaks out of a looping or blocking activity like a queue read
 * or barrier wait.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public enum BreakType
{
    TIMEOUT, INTERRUPTED, UNCAUGHT;
}


/* end-of-BreakType.java */
