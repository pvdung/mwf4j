/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.PutMethod;
import  org.jwaresoftware.mwf4j.What;

/**
 * Put method that saves information to the current shared MDC. Throws
 * a {@linkplain SavebackException saveback exception} if MDC unable to 
 * put for any reason (including bad inputs). Null puts are interpreted
 * as MDC clears for the named items.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class SavebackMDC<T> implements PutMethod<T>
{
    public SavebackMDC()
    {
        super();
    }

    public boolean put(final String key, T value)
    {
        Validate.notNull(key,What.KEY);
        try {
            MDC.put(key,value);
        } catch(RuntimeException mdcX) {
            throw new SavebackException(key, mdcX);
        }
        return true;
    }

    public boolean putNull(final String key)
    {
        Validate.notNull(key,What.KEY);
        MDC.clr(key);
        return true;
    }
}


/* end-of-SavebackMDC.java */
