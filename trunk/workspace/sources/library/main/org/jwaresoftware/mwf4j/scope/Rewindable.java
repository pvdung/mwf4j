/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * ---- (( INSERT DOCUMENTATION )) ----
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 **/

public interface Rewindable
{
    ControlFlowStatement rewind(RewindCursor to, Harness harness);
}


/* end-of-Rewindable.java */
