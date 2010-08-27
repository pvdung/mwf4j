/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.helpers.CheckUnwound;
import  org.jwaresoftware.mwf4j.scope.Scopes;

/**
 * Action that unstalls an unwindable that will note whether its statement
 * was unwound.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    test,helper
 **/

public final class InstallCheckUnwind extends TestExtensionPoint
{
    public InstallCheckUnwind(String id)
    {
        super(id);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        Scopes.addUnwind(new CheckUnwound(getId()));
        return next();
    }
}


/* end-of-InstallCheckUnwind.java */
