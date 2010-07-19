/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.ServiceProviderException;

/**
 * MWf4j marker runtime exception used when other, more specific, 
 * exception not available. Also can use to wrap checked exception if
 * subclass implements Throwables&#46;CheckedWrapper.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
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
