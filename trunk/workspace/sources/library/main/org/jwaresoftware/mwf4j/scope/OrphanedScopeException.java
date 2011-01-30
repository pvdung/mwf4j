/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MWf4JException;
import  org.jwaresoftware.mwf4j.What;

/**
 * Exception used to indicate one or more orphaned scopes.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    infra,impl,helper
 **/

public class OrphanedScopeException extends MWf4JException
{
    public OrphanedScopeException(Harness harness, int count)
    {
        super("Detected at least "+count+" orphaned scopes for H="+What.idFor(harness,harness.typeCN()));
    }
}


/* end-of-OrphanedScopeException.java */
