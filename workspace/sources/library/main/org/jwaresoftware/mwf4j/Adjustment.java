/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Special action that is used to apply an adhoc adjustment to a running
 * activity via its harness. The harness expects a <em>terminal</em> adjustment's
 * statement to stop the execution loop IMMEDIATELY so it initiates its
 * exception termination processing (for instance unwinds are triggered).
 * Non-terminal adjustments have their statements executed before existing
 * primary statements (including the first statement if needed) and queued
 * continuations.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Adjustment extends Action
{
    /**
     * Returns <i>true</i> if and only if the adjustment wants the harness 
     * to abort execution immediately. Returning true implies there should be
     * <em>NO</em> continuation with the existing harness primary statements
     * including pending continuations! In response to a terminal adjustment, 
     * a harness will typically unwind any registered unwindables before 
     * stopping its main statement execution loop.  Examples of terminal 
     * adjustments include a "forced quit", a "cancel", or a "rewind" operation.
     **/
    boolean isTerminal();
}


/* end-of-Adjustment.java */
