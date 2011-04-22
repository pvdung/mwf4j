/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.gestalt.reveal.Identified;

import  org.jwaresoftware.mwf4j.Sequence;

/**
 * Strategy for terminating a compound inner element being built by a 
 * {@linkplain BALBuilder}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,extras,helper
 * @see       Finishers
 * @see       RootFinisher
 **/

public interface Finisher extends Identified
{
    String getId();
    BALBuilder<?> finish(BALBuilder<?> outer, Sequence collected);
    <T> T getUnderConstruction(Class<T> ofType);
}


/* end-of-Finisher.java */
