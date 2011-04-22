/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.fixture.StringResolver;
import  org.jwaresoftware.gestalt.bootstrap.Fixture.Implementation;

/**
 * Application-supplied support structure from which various MWf4J objects 
 * can extract configuration, utility services like declarable reference
 * resolution, the active {@linkplain Variables variables} map, and other 
 * bits of information. A fixture differs from a {@linkplain Harness} in
 * that it is not necessarily the <em>run-time</em> controller that is
 * linked to the final executing statement(s); in most cases it is, but
 * there is no requirement to enforce this link. All harnesses are also
 * MWf4J fixtures by definition.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Fixture extends Implementation, StringResolver
{
    
    /**
     * Returns the underlying variables map for this fixture. For harnesses
     * this is the live variables that all control flow statements manipulate
     * and read. Never returns <i>null</i>.
     **/
    Variables getVariables();

    
    /**
     * Shortcut to the inherited interpolate function that uses this
     * fixture's own issue handler to report problems. A convenience.
     * @param inputString the string to resolve
     * @return the resolved string or inputString if nothing to change
     **/
    String interpolate(String inputString);
}


/* end-of-Fixture.java */
