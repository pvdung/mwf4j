/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Exception generated when there is an issue with MWf4J statement
 * implementation itself. Not meant for application-sourced
 * errors.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    api,infra
 **/

public class PossibleInfiniteLoopException extends MWf4JException
{
    public PossibleInfiniteLoopException(String message)
    {
        super(message);
    }

    public PossibleInfiniteLoopException(Throwable cause)
    {
        super(cause);
    }
}


/* end-of-ControlFlowStatementException.java */
