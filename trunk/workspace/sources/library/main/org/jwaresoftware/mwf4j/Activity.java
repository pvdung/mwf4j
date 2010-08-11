/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * One or more actions to be run in the context of a user-supplied harness.
 * Dynamic actions generated as a by-product of running the original root
 * action (the activity's definition), can be slotted for execution against
 * the owning activity's harness as continuation control statements. The
 * activity does not return from the harness's run method until all
 * primary statements, adjustments, and continuations have completed.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 * @see       Harness
 * @see       Adjustment
 **/

public interface Activity extends Entity
{
    ControlFlowStatement firstStatement();
}


/* end-of-Activity.java */
