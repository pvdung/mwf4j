/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Pair;

import org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;
import  org.jwaresoftware.mwf4j.starters.TestStatement;

/**
 * Action that either enters or leaves an application defined scope. You
 * must supply the shared statement that links the enter/leave as a single
 * scope.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    test,helper
 **/

public final class EnterLeaveScopeAction extends ActionSkeleton
{
    public EnterLeaveScopeAction(String id, boolean enter, ControlFlowStatement link)
    {
        super(id);
        Validate.notBlank(getId(),What.ID);
        Validate.notNull(link,What.STATEMENT);
        myLink = link;
        myEnterFlag = enter;
    }

    public EnterLeaveScopeAction(String id)
    {
        super(id);
        Validate.notBlank(getId(), What.ID);
        myLink = new TestStatement(null,ControlFlowStatement.nullINSTANCE);
        myEnterFlag = true;
    }

    public static final Pair<Action,Action> newPair(String scopeNam)
    {
        Pair<Action,Action> actions=new Pair<Action,Action>();
        EnterLeaveScopeAction enter = new EnterLeaveScopeAction(scopeNam);
        actions.set1(enter);
        actions.set2(new EnterLeaveScopeAction(scopeNam,false,enter.getLink()));
        return actions;
    }

    public final ControlFlowStatement getLink()
    {
        return myLink;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,EnterLeaveScopeStatement.class,What.STATEMENT);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        EnterLeaveScopeStatement enterleave= 
            new EnterLeaveScopeStatement(getId(),myEnterFlag,myLink,this,next);
        return finish(enterleave);
    }

    private final ControlFlowStatement myLink;
    private final boolean myEnterFlag;
}


/* end-of-EnterLeaveScopeAction.java */
