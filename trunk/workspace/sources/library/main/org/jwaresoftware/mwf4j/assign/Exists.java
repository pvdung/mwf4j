/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;

/**
 * Condition that checks if a named item exists within the context of a 
 * running harness. The "context" might be the harness's variables map,
 * its configuration, the local system, or something else. Basically
 * any information you can get to via a {@linkplain Giveback Giveback} 
 * interface.
 * <p/>
 * Any unhandled exception the giveback signals is wrapped by a outer
 * {@linkplain GivebackException}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class Exists implements Condition
{
    public Exists(Giveback<?> getmethod)
    {
        Validate.notNull(getmethod,What.GET_METHOD);
        myGetMethod = getmethod;
    }

    public Exists(String keyOrExpr)
    {
        this(GivebackVar.fromEvalOfOptional(keyOrExpr,Object.class));
    }

    public boolean evaluate(Harness harness)
    {
        try {
            return isThere(harness);
        } catch(GivebackException getX) {
            throw getX;
        } catch(Exception unXpected) {
            throw new GivebackException(unXpected);//for badly written giveback!
        }
    }

    private boolean isThere(Harness harness) throws Exception
    {
        try {
            MDC.pshHarness(this,harness);
            return myGetMethod.call()!=null;
        } finally {
            MDC.popHarness(this,harness);
        }
    }

    private Giveback<?> myGetMethod;
}


/* end-of-Exists.java */
