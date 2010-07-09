/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;

/**
 * Test action that does nothing but return a finish later statement (which
 * just posts a no-op end statement for continuation testing).
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
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

    public void configure(ControlFlowStatement own)
    {
        Validate.isTrue(own instanceof FinishLaterStatement,"statement kindof FinishLaterStatement");
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        FinishLaterStatement statement = new FinishLaterStatement(this, next);
        return finish(statement);
    }
}


/* end-of-FinishLaterAction.java */
