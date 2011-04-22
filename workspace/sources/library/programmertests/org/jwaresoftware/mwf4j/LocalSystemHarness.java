/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.mwf4j.harness.SimpleHarness;
import  org.jwaresoftware.mwf4j.starters.ActionToActivityAdapter;

/**
 * A harness you can use for static lookup purposes. There is no formal
 * activity (actually a no-op stub only) for this harness.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper,test
 * @see       LocalSystemFixture
 **/

public final class LocalSystemHarness extends SimpleHarness
{
    public LocalSystemHarness()
    {
        super(new ActionToActivityAdapter(),TestFixture.setUp());
    }
}


/* end-of-LocalSystemHarness.java */
