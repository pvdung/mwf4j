/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.Harness;

/**
 * Mixin implementation interface for any MWf4J entity that has a single 
 * "run" type method from which it performs its primary responsibility. This
 * mixin adds two hooks around that method: just before (doEnter), and just
 * after (doLeave).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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
