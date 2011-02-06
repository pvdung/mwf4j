/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.behaviors.CallBounded;
import  org.jwaresoftware.mwf4j.helpers.False;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that will (re)run another application-supplied action as long as
 * a given condition returns <i>true</i>. You can guard against run-away
 * loops with the {@linkplain #setHaltIfMax haltIfMax option}. You can also
 * setup an loop cursor to capture the 0-based iteration index.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       WhileStatement
 * @see       ForEachAction
 **/

public class WhileAction extends ActionSkeleton implements CallBounded
{
    public WhileAction()
    {
        this("while");
    }

    public WhileAction(String id)
    {
        super(id);
    }

    public void setTest(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        myTest = test;
    }

    public void setBody(Action body)
    {
        Validate.notNull(body,What.ACTION);
        myBody = body;
    }

    public void setCopy(boolean flag)
    {
        myCopyFlag = flag;
    }

    public void setMaxIterations(int max)
    {
        Validate.isFalse(max<0,"max >= 0");
        myLimit = Integer.valueOf(max);
    }

    public final void setHaltIfMax(boolean flag)
    {
        myHaltIfMaxFlag = flag;
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myHaltContinuationFlag = flag;
    }

    public final void enableCursor()
    {
        setCursorKey(BAL.getCursorKey(getId()));
    }

    public void setCursorKey(String key)
    {
        Validate.notBlank(key,What.CURSOR);
        myCursor.setName(key);
    }

    public void setCursor(Reference key)
    {
        myCursor.copyFrom(key);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        verifyReady();
        WhileStatement statement = newWhileStatement(next);
        Validate.resultNotNull(statement,What.STATEMENT);
        return finish(statement);
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isA(statement,WhileStatement.class,What.STATEMENT);
        WhileStatement loop = (WhileStatement)statement;
        loop.setTest(myTest);
        if (myBody!=null) {
            if (myCopyFlag) {
                loop.setBodyFactory(myBody);
            } else {
                loop.setBody(myBody.makeStatement(loop));
            }
        } else {
            loop.setBody(new EmptyStatement(loop)); 
        }
        loop.setCursor(myCursor);
        if (myLimit!=null) {
            loop.setMaxIterations(myLimit);
            loop.setHaltIfMax(myHaltIfMaxFlag);
            loop.setUseHaltContinuation(myHaltContinuationFlag);
        }
    }

    protected WhileStatement newWhileStatement(ControlFlowStatement next)
    {
        return new WhileStatement(this,next);
    }


    private Action myBody;
    private boolean myCopyFlag=BAL.getNewStatementPerLoopFlag();
    private Condition myTest = False.INSTANCE;
    private boolean myHaltIfMaxFlag = false;
    private Integer myLimit = null;//Undefined => none
    private boolean myHaltContinuationFlag = BAL.getUseHaltContinuationsFlag();
    private Reference myCursor = new Reference();//OPTIONAL
}


/* end-of-WhileAction.java */
