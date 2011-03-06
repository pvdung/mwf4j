/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.gestalt.system.LocalSystem;

/**
 * Test factory object for use as a target object in variables map.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    test,helper
 **/

public final class IDFactory
{
    public final static Long newOid()
    {
        return LocalSystem.currentTimeNanos();
    }

    IDFactory() { }
}


/* end-of-IDFactory.java */
