/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.gestalt.Strings;

/**
 * Common attributes of marker id types for builders.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras
 **/

public class ID
{
    private String myValue;

    ID(String value) 
    {
        myValue = value;
    }

    public final String value() 
    {
        return myValue;
    }

    public final boolean isNull() 
    {
        return value()==null;
    }

    public final boolean isEmpty()
    {
        return Strings.isEmpty(value());
    }

    public final boolean isBlank() 
    {
        return Strings.isBlank(value());
    }
}

/* end-of-ID.java */
