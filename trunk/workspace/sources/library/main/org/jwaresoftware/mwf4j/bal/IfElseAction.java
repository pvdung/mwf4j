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

/**
 * Action that will execute one of two branch alternatives based 
 * on the result of a supplied test. Classic "if-then-else" construct.
 * If <em>no</em> parameters are given, this action functions
 * as a no-op empty statement and passes control to the continuation
 * supplied to the statement factory method.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for buildStatement)
 * @.group    infra,impl
 * @see       BranchStatement
 * @see       Condition
 **/

public class IfElseAction extends IfAction
{
    public IfElseAction()
    {
        this("ifelse");
    }

    public IfElseAction(String id)
    {
        super(id);
    }

    public void setElse(Action elseAction)
    {
        myElseBranch = elseAction;
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isA(statement,BranchStatement.class,What.STATEMENT);
        BranchStatement decision = (BranchStatement)statement;
        decision.setCheckDeclarables(isCheckDeclarables());
        Action kontinue = new GivebackStatement(decision.next());
        if (myThenBranch!=null) {
            decision.setTrue(myThenBranch);
        } else {
            decision.setTrue(kontinue);
        }
        if (myElseBranch!=null) {
            decision.setFalse(myElseBranch);
        } else {
            decision.setFalse(kontinue);
        }
    }

    private Action myElseBranch;
}


/* end-of-IfElseAction.java */
