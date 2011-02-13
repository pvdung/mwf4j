/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.PutMethod;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackProperty.Source;

/**
 * Assignment helper that just saves a value to either the local system
 * properties or the current harness's configuration override properties
 * (the default). Throws a {@linkplain SavebackException saveback exception} 
 * if the System or harness is unable to save property value for any reason.
 * Null puts are interpreted as unsets for the named items.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 * @see       MDC#currentConfiguration()
 **/

public final class SavebackProperty<T> implements PutMethod<T>
{
//  ---------------------------------------------------------------------------------------
//  Easy factory methods named for intended lookup:
//  ---------------------------------------------------------------------------------------

    public final static<T> SavebackProperty<T> toSystem()
    {
        return new SavebackProperty<T>(Source.SYSTEM);
    }

    public final static<T> SavebackProperty<T> toHarness()
    {
        return new SavebackProperty<T>();
    }

//  ---------------------------------------------------------------------------------------
//  Implementation:
//  ---------------------------------------------------------------------------------------

    public SavebackProperty(Source source)
    {
        Validate.notNull(source,What.SOURCE);
        myToSystemFlag = Source.SYSTEM.equals(source);
    }

    public SavebackProperty()
    {
        this(Source.HARNESS);
    }

    public boolean put(final String property, T value)
    {
        Validate.notBlank(property,What.PROPERTY);
        try {
            String string = Strings.valueOf(value);
            if (myToSystemFlag) {
                LocalSystem.setProperty(property,string);
            } else {
                MDC.currentConfiguration().getOverrides().setProperty(property,string);
            }
        } catch(RuntimeException setX) {
            throw new SavebackException(property,setX);
        }
        return true;
    }

    public boolean putNull(final String property)
    {
        Validate.notBlank(property,What.PROPERTY);
        try {
            if (myToSystemFlag) {
                LocalSystem.unsetProperty(property);
            } else {
                MDC.currentConfiguration().getOverrides().unsetProperty(property);
            }
        } catch(RuntimeException setX) {
            throw new SavebackException(property,setX);
        }
        return true;
    }

    private final boolean myToSystemFlag;
}


/* end-of-SavebackProperty.java */
