/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Action that verifies a collection of statements have been executed in a
 * particular order. Useful for multi-threaded or multi-harness testing to
 * verify processes aren't interfering with each other.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public final class CheckPerformedInOrder extends ActionSkeleton
{
    public CheckPerformedInOrder(String id, String statementNameList)
    {
        super(id);
        Validate.notNull(statementNameList,"statement-ids");
        this.statementNames = statementNameList;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,CheckPerformedInOrderStatement.class,What.STATEMENT);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        CheckPerformedInOrderStatement check = 
            new CheckPerformedInOrderStatement(getId(),statementNames,
                                               this,next);
        return finish(check);
    }

    private final String statementNames;
}


/* end-of-CheckPerformedInOrder.java */
