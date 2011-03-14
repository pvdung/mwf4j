/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Markable;

/**
 * Starting implementation for an {@linkplain Action}. Tracks the action's id
 * and adds a template implementation for {@linkplain #buildStatement}.
 * Subclasses should provide implementation details for {@linkplain #createStatement}
 * and {@linkplain Action#configureStatement configureStatement}. And,
 * optionally, subclasses can fillin {@linkplain #verifyReady} which is
 * called before the new statement's reconfigure method is triggered.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public abstract class ActionSkeleton implements Action, Markable
{
    protected ActionSkeleton()
    {
        super();
    }

    protected ActionSkeleton(String id)
    {
        myId = Strings.trimToEmpty(id);
    }


    public String getId()
    {
        return myId;
    }

    @Override
    public final void setId(String id)
    {
        Validate.notNull(id,What.ID);
        myId = Strings.trimToEmpty(id);
    }

    public ControlFlowStatement buildStatement(ControlFlowStatement next, Fixture environ)
    {
        verifyReady();
        ControlFlowStatement statement = createStatement(next,environ);
        Validate.resultNotNull(statement,What.STATEMENT);
        statement.reconfigure(environ,this);
        return statement;
    }


    /**
     * Factory method to create the specific type of statement this action
     * describes. Any subclass that uses the standard buildStatement template
     * method <em>must</em> override this method.
     * @param next continuation (non-null)
     * @return new statement (non-null)
     **/
    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        throw new UnsupportedOperationException("actionSkeleton.createStatement");
    }


    protected void verifyReady()
    {
        //nothing by default; extend to ensure all required attributes defined!
    }


    public String toString()
    {
        return What.subidFor(this,"Action",getId());
    }


    private String myId= Strings.EMPTY;
}


/* end-of-ActionSkeleton.java */
