/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.gestalt.Flags;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.reveal.Named;

/**
 * Common attributes of marker flags for builders.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras,helper
 **/

public class Flag implements Named
{
    private final static String UNNAMED=Strings.EMPTY;

    Flag()
    {
        myValue = null;
        myName  = UNNAMED;
    }

    Flag(boolean value) 
    {
        myValue = Boolean.valueOf(value);
        myName  = UNNAMED;
    }

    Flag(String name)
    {
        myValue = null;
        myName  = name;
    }

    Flag(String name, boolean value)
    {
        myValue = Boolean.valueOf(value);
        myName  = name;
    }

    public final Boolean value()
    {
        return myValue;
    }

    public final String getName() 
    {
        return myName;
    }

    public final boolean isUndefined() 
    {
        return value()==null;
    }

    public final boolean on(boolean dflt)
    {
        return isUndefined() ? dflt : Boolean.TRUE.equals(value());
    }

    public final boolean on()
    {
        return on(true);
    }

    public final boolean off(boolean dflt)
    {
        return isUndefined() ? dflt : Boolean.FALSE.equals(value());
    }

    public final boolean off()
    {
        return off(true);
    }

    public String toString()
    {
        return myName + "=" + myValue;
    }

    public boolean equals(Object other) 
    {
        if (other==this) return true;
        if (other==null) return false;
        if (getClass().equals(other.getClass())) {
            Flag otherflag = (Flag)other;
            return Flags.equal(myValue,otherflag.myValue) && myName.equals(otherflag.myName);
        }
        return false;
    }

    public int hashCode()
    {
        int hc0 = myValue==null ? 17 : myValue.hashCode();
        return myName.hashCode() + hc0;
    }

    private final String  myName;
    private final Boolean myValue;
}

/* end-of-Flag.java */
