/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

/**
 * Giveback implementation that just returns a payload that was 
 * assigned at construction. Useful for giving back a hard-coded
 * constant or literal values.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class GivebackValue<T> implements Giveback<T>
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

    private final T myPayload;
}


/* end-of-GivebackValue.java */
