/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Action that verifies a preceeding statement has been executed at least
 * once. Useful for multi-threaded or multi-harness testing to verify one
 * process isn't interfering with another.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public final class CheckPerformed extends ActionSkeleton
{
    public CheckPerformed(String id, String statementNam)
    {
        this(id,statementNam,0);
    }

    public CheckPerformed(String id, String statementNam, int count)
    {
        super(id);
        Validate.notNull(statementNam,"statement-id");
        this.statementName = statementNam;
        xpectedCount = count;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,CheckPerformedStatement.class,What.STATEMENT);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        CheckPerformedStatement check = 
            new CheckPerformedStatement(getId(),statementName,xpectedCount,
                                        this,next);
        return finish(check);
    }

    private final String statementName;
    private final int xpectedCount;
}


/* end-of-CheckPerformed.java */
