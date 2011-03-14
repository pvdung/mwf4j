/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.gestalt.reveal.Identified;

/**
 * Mixin interface for any component that supports a public setter for its
 * unique identifier.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra,helper
 **/

public interface Markable extends Identified
{
    void setId(String id);
}


/* end-of-Markable.java */
