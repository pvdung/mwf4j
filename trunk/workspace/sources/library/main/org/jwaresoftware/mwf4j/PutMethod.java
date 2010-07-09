/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  java.util.concurrent.Callable;

/**
 * Weird complement to standard {@linkplain Callable} that can receive
 * the output returned by a call. The 'path' parameter to the 
 * {@linkplain #put(String, Object) put() method} is an 
 * implementation-specific descriptor for "where" the "what" is to be 
 * put; for instance, a put method that saves to harness variables can
 * accept an EL expression that describes the target object/field for
 * the data.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public interface PutMethod<T>
{
    boolean put(String path, T payload);
    boolean putNull(String path);
}


/* end-of-PutMethod.java */
