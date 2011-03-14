/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.fixture.StringResolver;
import  org.jwaresoftware.gestalt.bootstrap.Fixture.Implementation;

/**
 * Supplied support structure from which various MWf4J objects can extract
 * configuration, utility services like delayed object reference resolution,
 * and other bits of information. A fixture differs from a 
 * {@linkplain Harness} in that it is not necessarily the <em>run-time</em>
 * controller that is linked to the final executing statement(s). However,
 * all harnesses are by definition also MWf4J fixtures.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Fixture extends Implementation, StringResolver
{
    Variables getVariables();
    String interpolate(String inputString);//shortcut to use own issue handler!
}


/* end-of-Fixture.java */
