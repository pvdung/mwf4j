/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;

/**
 * Giveback implementation that just returns the value of a 
 * predefined MDC element. Between calls the value of the 
 * element can change (it's up to caller to assure
 * concurrency semantics).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class GivebackMDC<T> implements Giveback<T>
{
    public GivebackMDC(String key, Class<T> ofType)
    {
        this(key,ofType,null);
    }

    public GivebackMDC(String key, Class<T> ofType, T fallbackValue)
    {
        Validate.notBlank(key,What.ITEM_ID);
        Validate.notNull(ofType,What.CLASS_TYPE);
        myKey = key;
        myOfType = ofType;
        myFallbackValue = fallbackValue;
    }

    public T call()
    {
        try {
            T value = MDC.get(myKey,myOfType);
            return (value!=null) ? value : myFallbackValue;
        } catch(RuntimeException ccX) {//class-cast-exception
            throw new GivebackException(myKey,ccX);
        }
    }

    private final String myKey;
    private final T myFallbackValue;
    private final Class<? extends T> myOfType;
}


/* end-of-GivebackMDC.java */
