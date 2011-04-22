/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;
import  org.jwaresoftware.mwf4j.helpers.Declarables;

/**
 * Giveback implementation that just returns the value of a 
 * predefined MDC element. Between calls the value of the 
 * element can change (it's up to caller to assure
 * concurrency semantics).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,infra,helper
 **/

public final class GivebackMDC<T> extends CloneableSkeleton implements Giveback<T>, Declarable
{
    public GivebackMDC(String key, Class<T> ofType)
    {
        this(key,ofType,null);
    }

    public GivebackMDC(String key, Class<? extends T> ofType, T fallbackValue)
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

    @Override
    public void freeze(Fixture environ)
    {
        myFallbackValue = Declarables.freeze(environ,myFallbackValue);
        myKey = Declarables.freeze(environ,myKey);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object clone()
    {
        GivebackMDC copy = (GivebackMDC)super.clone();
        copy.myFallbackValue = LocalSystem.newCopyOrSame(myFallbackValue);
        return copy;
    }


    private String myKey;
    private T myFallbackValue;
    private final Class<? extends T> myOfType;
}


/* end-of-GivebackMDC.java */
