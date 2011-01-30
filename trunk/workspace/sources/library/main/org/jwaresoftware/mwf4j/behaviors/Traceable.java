/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.slf4j.Logger;

import  org.jwaresoftware.gestalt.reveal.Identified;

/**
 * Mixin interface that lets tracing and history components work with
 * any source object. Assumes an SLF4J framework-based implemenation for
 * feedback.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    api,helper
 * @see       org.jwaresoftware.mwf4j.starters.TraceSupport TraceSupport
 **/

public interface Traceable extends Identified
{
    /**
     * Returns a marker name for same-type components participating
     * in the execution. For instance, all control flow statements
     * might return "statement" while all actions might return "action".
     **/
    String typeCN();

    
    /**
     * Returns the SLF4J logger the tracing component should use by default.
     * Never returns <i>null</i>; typically shared by same-type components.
     **/
    Logger logger();


    /** 
     * Returns a descriptive string of this object for use in the trace 
     * stream. Note that this string is often NOT the same as the regular 
     * Object&#46;toString's output!
     **/
    String toString();
}


/* end-of-Traceable.java */
