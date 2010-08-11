/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.behaviors.DependentHarness;

/**
 * Mixin interface for any dependent harness that is spawned by another 
 * into its own thread-of-execution.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (same as superclass)
 * @.group    infra,impl
 * @see       SpawnedHarnessFactory
 **/

public interface SpawnedHarness extends DependentHarness
{
    void setMDCInitializer(MDC.Propagator initializer);
}


/* end-of-SpawnedHarness.java */
