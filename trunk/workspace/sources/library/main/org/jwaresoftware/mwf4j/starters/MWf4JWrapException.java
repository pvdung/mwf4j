/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * The specific MWf4J checked exception wrapper. Only reason for being is
 * to wrap a checked exception for export (or throwing) from a method with
 * no checked signature. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 **/

public final class MWf4JWrapException extends MWf4JException implements Throwables.CheckedWrapper
{
    public MWf4JWrapException(Throwable cause)
    {
        super(cause);
    }
}


/* end-of-MWf4JWrapException.java */
