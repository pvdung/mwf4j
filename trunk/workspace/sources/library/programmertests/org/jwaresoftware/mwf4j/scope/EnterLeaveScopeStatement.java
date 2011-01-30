/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.LiteLiteStatementSkeleton;

/**
 * Statement that either enters of leaves an application-named scope.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public final class EnterLeaveScopeStatement extends LiteLiteStatementSkeleton
{
    public EnterLeaveScopeStatement(String scopeNam, boolean enter, 
            ControlFlowStatement link, Action owner, ControlFlowStatement next)
    {
        super(scopeNam+(enter ? ".install" : ".uninstall"),owner,next);
        myScopeId = scopeNam;
        myLink = link;
        myEnterFlag = enter;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        if (myEnterFlag) {
            Scopes.enter(myLink,myScopeId,harness);
        } else {
            Scopes.leave(myLink,harness);
        }
        return next();
    }

    private final boolean myEnterFlag;
    private String myScopeId;
    private ControlFlowStatement myLink;
}


/* end-of-EnterLeaveScopeStatement.java */
