/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.Harness;

/**
 * Mixin interface for any harness that is itself a dependent of another
 * harness; for example, a spawned harness or a nested harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public interface DependentHarness extends Harness
{
    Harness getHarnessDependentOn();
}


/* end-of-DependentHarness.java */
