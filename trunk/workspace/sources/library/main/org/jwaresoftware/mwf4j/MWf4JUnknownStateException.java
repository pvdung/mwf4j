/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Exception thrown when a critical MWf4J context has been altered and
 * the application needs to either abort and/or restart the current 
 * MWf4J activity (whether that's actually an '{@linkplain Activity}'
 * is undefined). 
 * <p/>
 * An example of a funky MWf4J state is if the current harness as installed
 * in the MDC, "goes away" due to the JVM's attempt to avoid a critical 
 * "out-of-memory" error. If the JVM's efforts work, the next closure to
 * access the MDC for the harness will be stuck. The application can rewind
 * to a point where it can reinstall the harness, restart the entire 
 * activity, or um die.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    api,infra
 **/

public final class MWf4JUnknownStateException extends MWf4JException
{
    public MWf4JUnknownStateException(String message)
    {
        super(message);
    }
}


/* end-of-MWf4JUnknownStateException.java */
