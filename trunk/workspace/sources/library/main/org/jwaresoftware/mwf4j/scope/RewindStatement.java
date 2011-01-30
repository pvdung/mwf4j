/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.StatementSkeleton;

/**
 * Transient helper statement that you can use to <em>complete</em> a more
 * complex rewind statement or a {@linkplain RewindAdjustment rewind adjustment}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 * @see       RewindAdjustment
 **/

public final class RewindStatement extends StatementSkeleton
{
    public RewindStatement(Action owner, Rewindpoint mark)
    {
        super(owner,ControlFlowStatement.nullINSTANCE);
        setCursor(mark);
    }

    public void setCursor(Rewindpoint mark)
    {
        Validate.notNull(mark,What.CURSOR);
        myMark = mark;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Scopes.findOrFail(myMark);
    }

    public ControlFlowStatement runInner(Harness harness)
    {
        return Scopes.rewindFrom(myMark,harness);
    }

    private Rewindpoint myMark;
}


/* end-of-RewindStatement.java */
