/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.Fixture;

/**
 * Mixin interface for a component that can alter itself based on 
 * declared fixture information like properties, variables, services, etc.
 * Typically the linked {@linkplain #freeze(Fixture) freeze} method is
 * triggered <em>just before this component</em> is used or run. In order
 * to support prototypes and other multi-use descriptors (like Actions) we
 * require that declarables also be publicly cloneable. This requirement
 * ensures that factories and builders can create independent instances of
 * declarable members using the standard 'clone' APIs.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,helper
 * @see       DeclarableEnabled
 **/

public interface Declarable extends Cloneable
{
    void freeze(Fixture environ);
    Object clone();
}


/* end-of-Declarable.java */
