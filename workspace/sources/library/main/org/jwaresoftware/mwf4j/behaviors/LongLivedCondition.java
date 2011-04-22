/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import org.jwaresoftware.mwf4j.Condition;

/**
 * Extension of a simple {@linkplain Condition condition} that
 * supports semantics for use as part of a long-lived action's
 * exception compensation scheme. Note that a long-lived condition is
 * NOT safe for use by multiple statements from different threads.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public interface LongLivedCondition extends Condition
{
    void start();
    void start(long timezero);
}


/* end-of-LongLivedCondition.java */
