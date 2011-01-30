/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Exception thrown when a looping or iterating statement has been
 * executed at least one time too many.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl,helper
 **/

public class TooManyIterationsException extends MWf4JException
{
    public TooManyIterationsException(int count, int limit)
    {
        super("Too many iterations("+count+") detected; max expected is "+limit);
    }
    
    public TooManyIterationsException(String message)
    {
        super(message);
    }
}


/* end-of-TooManyIterationsException.java */
