/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  org.jwaresoftware.gestalt.Throwables;

import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Exception thrown when a provided closure (callable, future, runnable,
 * barrier, whatever) signals an exception. The MWf4J framework will wrap 
 * that exception with this unchecked one (with original as its cause).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl,helper
 **/

public class ClosureException extends MWf4JException implements Throwables.CheckedWrapper
{
    public ClosureException(Throwable cause)
    {
        super(cause);
    }

    public ClosureException(String message, Throwable cause)
    {
        super(message,cause);
    }

    public ClosureException(String message)
    {
        super(message);
    }
}


/* end-of-ClosureException.java */
