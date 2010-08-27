/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.Harness;

/**
 * MWf4j entity that has a single "run" type method from which
 * it performs its primary responsibility. Implementation interface.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Executable
{
    void doEnter(Harness harness);
    void doLeave(Harness harness);
}


/* end-of-Executable.java */
