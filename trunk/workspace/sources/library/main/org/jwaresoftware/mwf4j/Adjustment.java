/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Special action that is used to apply an adhoc adjustment to a running
 * activity via its harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Adjustment extends Action
{
    /**
     * Returns <i>true</i> if and only if the adjustment will abort
     * immediately. Returning true implies there is absolutely <em>NO</em>
     * continuation with the existing harness primary statements. A harness 
     * will typically unwind any registered unwindables in response to a 
     * terminal adjustment. Note however, queued continuations <em>are</em>
     * still called (this allows async functions to cleanup if needed). We
     * expect that whatever process triggers the terminal adjustment has
     * also cancelled (or queued for cancellation) other related async
     * processes (like event listeners, etc&#46;). Examples of terminal
     * adjustments include a "forced quit", a "cancel", or a "rewind" 
     * operation.
     **/
    boolean isTerminal();
}


/* end-of-Adjustment.java */
