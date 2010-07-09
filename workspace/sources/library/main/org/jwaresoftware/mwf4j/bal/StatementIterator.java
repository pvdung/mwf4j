/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.Iterator;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.EmptyIterator;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Iterator for a composite action's members. Returns a new statement
 * created by each member action on call to 'next' method.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

class StatementIterator extends EmptyIterator<ControlFlowStatement>
{
    /**
     * @param barrier the control point where we return 'back' to until iteration done
     * @param actions set of actions to iterate over (non-null)
     **/
    StatementIterator(ControlFlowStatement barrier, Collection<Action> actions)
    {
        Validate.neitherNull(actions,What.ACTIONS,barrier,What.STATEMENT);
        myActions = actions.iterator();
        myBarrier = barrier;
    }

    public boolean hasNext()
    {
        return myActions.hasNext();
    }

    public ControlFlowStatement next()
    {
        return myActions.next().makeStatement(myBarrier);
    }


    private Iterator<Action> myActions;
    private final ControlFlowStatement myBarrier;
}


/* end-of-StatementIterator.java */
