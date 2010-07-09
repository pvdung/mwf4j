/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.mwf4j.PutMethod;

/**
 * Put method that does nothing on put. Basically lets you "void"
 * a callback that requires a put-method. Completely ignores 
 * inputs (even if <em>null</em>).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class SavebackDiscard<T> implements PutMethod<T>
{
    public SavebackDiscard()
    {
        super();
    }

    public boolean put(String to, T what)
    {
        return true;
    }

    public boolean putNull(String to)
    {
        return true;
    }
}


/* end-of-SavebackDiscard.java */
