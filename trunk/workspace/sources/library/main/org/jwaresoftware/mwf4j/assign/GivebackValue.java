/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;
import  org.jwaresoftware.mwf4j.helpers.Declarables;

/**
 * Giveback implementation that just returns a payload that was 
 * assigned at construction. Useful for giving back a hard-coded
 * constant or literal values.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after frozen)
 * @.group    impl,infra,helper
 **/

public final class GivebackValue<T> extends CloneableSkeleton implements Giveback<T>, Declarable
{
    public GivebackValue()
    {
        this(null);
    }

    public GivebackValue(T payload)
    {
        myPayload = payload;
    }

    public T call()
    {
        return myPayload;
    }

    public void freeze(Fixture environ)
    {
        myPayload = Declarables.freeze(environ,myPayload);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone()
    {
        GivebackValue copy = (GivebackValue)super.clone();
        copy.myPayload = LocalSystem.newCopyOrSame(myPayload);
        return copy;
    }

    private T myPayload;
}


/* end-of-GivebackValue.java */
