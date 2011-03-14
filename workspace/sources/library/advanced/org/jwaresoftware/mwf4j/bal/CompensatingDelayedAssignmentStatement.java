/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackStatement;

/**
 * Variation of a delayed assignment action that can run a 'compensate' action
 * if the value getter is taking a long time (or some other compensate-requiring
 * condition is met).
 * <p/>
 * The compensating action can either trigger a stop (barf, end, etc.) or let
 * the linked assignment action continue waiting.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       org.jwaresoftware.mwf4j.assign.GivebackStatement GivebackStatementAction
 **/

public class CompensatingDelayedAssignmentStatement<T> extends DelayedAssignmentStatement<T>
{
    public CompensatingDelayedAssignmentStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public void setCompensate(Condition test, Action compensateAction)
    {
        Validate.neitherNull(test,What.CRITERIA,compensateAction,What.ACTION);
        myTest = test;
        myTrueBranch = compensateAction;
    }

    protected ControlFlowStatement nextIteration(Harness harness)
    {
        if (testStatement==null) {
            testStatement = new BranchImmediateStatement(this);
            testStatement.setTest(myTest);
            testStatement.setTrue(myTrueBranch);
            testStatement.setFalse(new GivebackStatement(this));
            testStatement.verifyReady();
            BALHelper.activate(myTest);
        }
        return testStatement;
    }

    public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        testStatement=null;
        super.reconfigure(environ,overrides);
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myTest,What.CRITERIA);
    }


    private Action myTrueBranch;
    private Condition myTest;
    private BranchStatement testStatement;
}


/* end-of-CompensatingDelayedAssignmentStatement.java */
