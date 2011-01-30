/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.atomic.AtomicInteger;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Test condition that returns <i>false</i> after being called an
 * application defined "N-1" times where "N" is a number greater or equal
 * to zero. If the condition is triggered more that its limit it will
 * generate a {@linkplain UnexpectedCallException custom exception}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    test,helper
 **/

public final class NIterations implements Condition
{
    public static final class UnexpectedCallException extends RuntimeException {
        UnexpectedCallException(int limit) {
            super("Condition has been called at least "+(limit+1)+" times");
        }
    }
    public NIterations(final int max) 
    {
        Validate.isFalse(max<0,"max is < 0");
        myLimit = max;
    }

    public boolean evaluate(Harness ignored)
    {
        boolean kontinue = myCalls.incrementAndGet()<=myLimit;
        if (!kontinue) {
            if (++myOverflow>0) 
                throw new UnexpectedCallException(myLimit);
        }
        return kontinue;
    }

    private final int myLimit;
    private AtomicInteger myCalls = new AtomicInteger();
    private int myOverflow= -1;
}


/* end-of-NIterations.java */
