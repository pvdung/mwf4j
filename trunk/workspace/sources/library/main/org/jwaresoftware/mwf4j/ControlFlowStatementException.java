/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Exception generated when there is an issue with MWf4J statement
 * implementation itself. Not meant for application-sourced
 * errors.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    api,infra
 **/

public class ControlFlowStatementException extends MWf4JException
{
    public ControlFlowStatementException(String message)
    {
        super(message);
    }

    public ControlFlowStatementException(Throwable cause)
    {
        super(cause);
    }
}


/* end-of-ControlFlowStatementException.java */
