/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.helpers.TestUnwinder;
import  org.jwaresoftware.mwf4j.scope.Scopes;

/**
 * Statement for {@linkplain AddTestUnwindAction} action. Separated to allow safe 
 * use of 'addunwind' in multi-threaded tests.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 * @see       TestUnwinder
 **/

public final class AddTestUnwindStatement extends LiteLiteStatementSkeleton
{
    public AddTestUnwindStatement(String id, String unwinderNam,
            Action owner, ControlFlowStatement next)
    {
        super(id,owner,next);
        this.unwinderName = unwinderNam;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        Scopes.addUnwind(new TestUnwinder(unwinderName));
        return next();
    }

    private String unwinderName;
}


/* end-of-AddTestUnwindStatement.java */
