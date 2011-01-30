/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Lookup method that when given a <i>selector</i> like an event or a 
 * class FQN, returns either a new or a reusable matching action.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface ActionLookupMethod<S>
{
    Action create(S selector, Harness harness) throws MWf4JException;
}


/* end-of-ActionLookupMethod.java */
