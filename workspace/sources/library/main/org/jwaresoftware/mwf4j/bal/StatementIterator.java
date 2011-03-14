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
import  org.jwaresoftware.mwf4j.What;

/**
 * Iterator for a composite action's members. Returns a new statement
 * created by each member action on call to 'next' method.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

class StatementIterator extends EmptyIterator<Action>
{
    /**
     * @param barrier the control point where we return 'back' to until iteration done
     * @param actions set of actions to iterate over (non-null)
     **/
    StatementIterator(Collection<Action> actions)
    {
        Validate.notNull(actions,What.ACTIONS);
        myActions = actions.iterator();
    }

    public boolean hasNext()
    {
        return myActions.hasNext();
    }

    public Action next()
    {
        Action action = myActions.next();
        Validate.resultNotNull(action, What.ACTION);
        return action;
    }

    private Iterator<Action> myActions;
}


/* end-of-StatementIterator.java */
