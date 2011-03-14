/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Statement that will run a user-supplied test and continue with one
 * of two choices (test = true =&gt; 'true' continuation; otherwise
 * =&gt; 'false' continuation). Note that <em>both</em> continuations
 * must be defined before this statement is run.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       org.jwaresoftware.mwf4j.Condition Condition
 * @see       IfElseAction
 **/

public class BranchStatement extends BALStatement
{
    public BranchStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public void setTest(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        myTest = test;
    }

    public void setTrue(Action trueAction)
    {
        Validate.notNull(trueAction,What.ACTION);
        myThen = trueAction;
    }

    public void setFalse(Action falseAction)
    {
        Validate.notNull(falseAction,What.ACTION);
        myElse = falseAction;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next = next();
        if (myTest.evaluate(harness)) {
            if (breadcrumbs().isEnabled())
                breadcrumbs().write("Branching 'Y' for action {}",getWhatId());
            next = myThen.buildStatement(next,harness.staticView());
        } else {
            if (breadcrumbs().isEnabled())
                breadcrumbs().write("Branching 'N' for action {}",getWhatId());
            next = myElse.buildStatement(next,harness.staticView());
        }
        return next;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myTest, What.CRITERIA);
        Validate.stateIsTrue(myThen!=null && myElse!=null, 
            "both true and false continuations have been defined");
    }

    private Condition myTest;
    private Action myThen;
    private Action myElse;
}


/* end-of-BranchStatement.java */
