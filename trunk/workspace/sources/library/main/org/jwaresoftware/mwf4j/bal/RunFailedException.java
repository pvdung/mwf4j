/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.ArrayDeque;
import  java.util.Deque;

import  org.apache.commons.lang.Validate;

import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Exception thrown by a protected statement or action on completion
 * and there has been at least one failure. This exception allows client
 * to either lump all captured exceptions together or extract the raw
 * sourced exceptions one-by-one. Tightly coupled with the ThrowStatement
 * BAL statement.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl,helper
 **/

public final class RunFailedException extends MWf4JException
{
    public RunFailedException(ThrowStatement lastThrowStatement)
    {
        Validate.notNull(lastThrowStatement,"throw-statement");
        copyCauses(lastThrowStatement);
        lastThrowStatement.replaceCause(this);//Does them all!
    }

    private void copyCauses(ThrowStatement thrown)
    {
        theErrors = new ArrayDeque<Throwable>(thrown.getLength());
        do {
            theErrors.push(causeFrom(thrown));
            thrown = thrown.nextThrown();
        } while(thrown!=null);
    }

    public Throwable getCause()
    {
        return theErrors.peek();
    }

    public String getMessage()
    {
        StringBuilder sb = LocalSystem.newLargeStringBuilder();
        for (Throwable e:theErrors) {
            sb.append("((").append(Throwables.getTypedMessage(e)).append("))");
        }
        return sb.toString();
    }

    public Deque<Throwable> copyOfCauses()
    {
        return new ArrayDeque<Throwable>(theErrors);
    }

    private Throwable causeFrom(ThrowStatement thrown)
    {
        Throwable cause = thrown.getCause();
        if (cause instanceof Throwables.CheckedWrapper) {
            Throwable underlying = cause.getCause();
            if (underlying!=null) {
                cause = underlying;
            }
        }
        return cause;
    }

    private Deque<Throwable> theErrors;
}


/* end-of-RunFailedException.java */
