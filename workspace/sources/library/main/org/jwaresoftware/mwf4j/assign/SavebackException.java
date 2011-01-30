/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Exception thrown when a saveback cannot assign its information
 * to specified source. Use to differentiate from all other MWf4J issues.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl,helper
 **/

public final class SavebackException extends MWf4JException
{
    /** Unable to eval saveback '&lt;selector&gt;'. */
    SavebackException(String selector,Throwable cause)
    {
        super("Unable to eval saveback '"+selector+"'",cause);
    }
}


/* end-of-SavebackException.java */
