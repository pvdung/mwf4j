/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Extension of a simple {@linkplain Condition condition} that
 * support semantics for use as part of a long-lived action's
 * exception compensation scheme. Note a long-lived condition is
 * NOT safe for use by multiple statements from different threads.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
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
