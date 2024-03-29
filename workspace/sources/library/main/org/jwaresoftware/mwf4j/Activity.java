/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * One or more {@linkplain ControlFlowStatement statements} to be run in 
 * the context of a user-supplied {@linkplain Harness harness}. 
 * Typically an activity starts with a single <i>first statement</i>. This 
 * first statement is often obtained from a single {@linkplain Action action} 
 * called the activity's <i>definition</i> although you can create the first 
 * statement using some other means. It is from the first statement 
 * that the activity progresses by passing back continuation statements (the 
 * next steps or parallel steps) to the controlling harness. Eventually 
 * there are no more statements to execute <em>for that harness</em>, the 
 * harness stops, and the MWf4J system considers that activity to be 
 * completed (successfully or otherwise). If the original activity spawned
 * separate independent harnesses with their own activities it's possible 
 * that those additional activities are still running; however, the
 * original harness activity is done. 
 * <p/>
 * The statements that comprise an activity do not have to be formally 
 * sequential. During the process, you can also post dynamically generated 
 * statements against the harness as {@linkplain Adjustment adjustments} or 
 * asynchronous continuations. Note that the activity does not complete 
 * until all primary statements, adjustments, and async continuations have
 * completed.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 * @see       Harness
 * @see       Adjustment
 **/

public interface Activity extends Entity
{
    ControlFlowStatement firstStatement(Fixture environ);
}


/* end-of-Activity.java */
