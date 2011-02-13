/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Exception thrown when a giveback cannot extract its information
 * from specified source. Use to differentiate from all other MWf4J issues.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl,helper
 **/

public final class GivebackException extends MWf4JException
{
    /** Unable to eval giveback '&lt;selector&gt;'. */
    GivebackException(String selector,Throwable cause)
    {
        super("Unable to eval giveback '"+selector+"'",cause);
    }

    GivebackException(Throwable cause)
    {
        super(cause);
    }

    private GivebackException(String testmessage)
    {
        super(testmessage);
    }

    public static GivebackException from(String message)
    {
        return new GivebackException(message);
    }
}


/* end-of-GivebackException.java */
