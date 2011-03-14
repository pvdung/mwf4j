/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;

/**
 * Test action that does nothing but return a finish later statement (which
 * just posts a no-op end statement for continuation testing).
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    helper,test
 **/

public final class FinishLaterAction extends ActionSkeleton
{
    public FinishLaterAction()
    {
        this("later");
    }

    public FinishLaterAction(String id)
    {
        super(id);
    }

    public void configureStatement(ControlFlowStatement owned, Fixture environ)
    {
        Validate.isA(owned,FinishLaterStatement.class,What.STATEMENT);
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new FinishLaterStatement(this, next);
    }
}


/* end-of-FinishLaterAction.java */
