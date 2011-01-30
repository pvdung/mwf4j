/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

/**
 * Giveback implementation that just returns a <i>null</i> value.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class GivebackNull<T> implements Giveback<T>
{
    public GivebackNull()
    {
        super();
    }

    public T call()
    {
        return null;
    }
}


/* end-of-GivebackNull.java */
