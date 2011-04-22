/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackStatement;
import  org.jwaresoftware.mwf4j.helpers.True;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that will execute another if and only if a supplied condition
 * returns true. Classic "if-then" construct. If <em>no</em> condition
 * is given, this action defaults to an <em>always true</em> condition
 * which will always execute its linked action. If no action is given
 * a harmless empty action is used as the default with continuation that
 * was supplied to the if action's statement factory method.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for buildStatement)
 * @.group    infra,impl
 * @see       BranchStatement
 * @see       Condition
 **/

public class IfAction extends ActionSkeleton
{
    public IfAction()
    {
        this("if");
    }

    public IfAction(String id)
    {
        super(id);
    }

    public void setTest(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        myTest = test;
    }

    public void setThen(Action thenAction)
    {
        myThenBranch = thenAction;
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        BranchStatement statement= new BranchStatement(next);
        statement.setTest(copyMember(myTest));//NB: done here on purpose!
        return statement;
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isTrue(statement instanceof BranchStatement,"statement kindof branch");
        BranchStatement decision = (BranchStatement)statement;
        decision.setCheckDeclarables(isCheckDeclarables());
        Action kontinue = new GivebackStatement(decision.next());
        decision.setFalse(kontinue);
        if (myThenBranch!=null) {
            decision.setTrue(myThenBranch);
        } else {
            decision.setTrue(kontinue);
        }
    }

    Condition myTest=True.INSTANCE;
    Action myThenBranch;
}


/* end-of-IfAction.java */
