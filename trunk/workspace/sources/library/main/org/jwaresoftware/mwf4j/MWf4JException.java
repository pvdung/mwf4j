/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.ServiceProviderException;

/**
 * MWf4j marker runtime exception used when another, more specific, 
 * exception is not available. You can also use this type of exception to
 * wrap a checked exception if subclass implements 
 * Throwables&#46;CheckedWrapper.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    api,infra
 **/

public class MWf4JException extends ServiceProviderException
{
    public MWf4JException()
    {
        super();
    }

    public MWf4JException(Throwable cause)
    {
        super(cause);
    }

    public MWf4JException(String message)
    {
        super(message);
    }

    public MWf4JException(String message, Throwable cause)
    {
        super(message,cause);
    }
}


/* end-of-MWf4JException.java */
