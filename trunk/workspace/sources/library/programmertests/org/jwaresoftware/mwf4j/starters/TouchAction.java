/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Test action that does nothing but increment the MDC stash
 * counter by one and return. The name of the action (if given) is
 * also recorded on the MDC's names list.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    helper,test
 * @see       TestStatement
 **/

public final class TouchAction extends ActionSkeleton
{
    public TouchAction()
    {
        this("touch");
    }

    public TouchAction(String id)
    {
        super(id);
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,TestStatement.class,What.STATEMENT);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        TestStatement statement = new TestStatement(this,next);
        return finish(statement);
    }
}


/* end-of-TouchAction.java */