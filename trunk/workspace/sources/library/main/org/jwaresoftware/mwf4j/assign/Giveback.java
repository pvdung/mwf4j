/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.concurrent.Callable;

/**
 * Marker interface for standard MWf4J assignment RHS implemented 
 * as standard {@linkplain Callable Callables}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/
public interface Giveback<T> extends Callable<T>
{
}


/* end-of-Giveback.java */
