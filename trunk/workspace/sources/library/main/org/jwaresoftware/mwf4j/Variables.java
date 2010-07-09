/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  java.util.concurrent.ConcurrentMap;

import  org.jwaresoftware.gestalt.fixture.GetTypedMethods;

/**
 * MWf4J interface to a generic map of harness-linked data. In addition to the 
 * opaque 'get' and 'put' methods, there are several convenient conversion
 * getter methods provided for common simple types like numbers, dates, etc.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    api,infra
 **/

public interface Variables extends ConcurrentMap<String,Object>, GetTypedMethods
{
    <T> T get(String name, Class<T> ofType);
    <T> T getOrFail(String name, Class<T> ofType);
    Object getOrFail(String name);
    String getStringOrNull(String name);
}


/* end-of-Variables.java */
