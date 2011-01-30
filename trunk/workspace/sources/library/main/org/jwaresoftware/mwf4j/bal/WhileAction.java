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
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.behaviors.CallBounded;
import  org.jwaresoftware.mwf4j.helpers.False;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that will (re)run another application-supplied action as long as
 * a given condition returns <i>true</i>. You can guard against run-away
 * loops with the {@linkplain #setHaltIfMax haltIfMax option}.
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
        myCursorKey=BAL.getCursorKey(getId());
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

    public void setCursor(String key)
    {
        Validate.notBlank(key,What.CURSOR);
        myCursorKey = key;
    }

    public void setCursorStoreType(StoreType type)
    {
        Validate.notNull(type,What.TYPE);
        myCursorStoreType = type;
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
        Validate.isTrue(statement instanceof WhileStatement,"statement kindof while");
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
        if (myCursorKey!=null) {
            loop.setCursorKey(myCursorKey);
            loop.setCursorStoreType(myCursorStoreType);
        }
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
    private String myCursorKey;
    private StoreType myCursorStoreType = BAL.getCursorStoreType();
}


/* end-of-WhileAction.java */
