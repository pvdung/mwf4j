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
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Statement that keeps returning a copy of a prescribed body statement
 * if a given opaque condition is <i>true</i>. Test is done <em>before</em>
 * the body is executed the first time. You can setup the statement
 * to guard against runaway loops with a "max iteration" value. If this
 * max is hit, the 'haltIfMax' flag determines whether the statement
 * just continues with next statement or signals an error.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class WhileStatement extends BALStatement implements Unwindable
{
    public WhileStatement(Action owner, ControlFlowStatement next) 
    {
        super(owner,next);
        myMaxLoopsSupport = new LimitSupport(getOwner());
        myUnwindSupport = new ReentrantSupport(this,true,this);
    }

    public void setTest(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        myTest = test;
    }

    public void setBody(ControlFlowStatement body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    public void setBodyFactory(final Action factory)
    {
        Validate.notNull(factory,What.FACTORY_METHOD);
        myBodyFactory = factory;
    }

    public void setCursorKey(String key)
    {
        Validate.notNull(key,What.CURSOR);
        myCursorKey = key;
    }

    public void setCursorStoreType(StoreType type)
    {
        Validate.notNull(type,What.TYPE);
        myCursorStoreType = type;
    }

    public void setHaltIfMax(boolean flag)
    {
        myMaxLoopsSupport.setHaltIfMax(flag);
    }

    public void setMaxIterations(int max)
    {
        myMaxLoopsSupport.setMaxIterations(max);
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myMaxLoopsSupport.setUseContinuation(flag);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next=checkIteration(harness);
        if (next==null) {
            if (myTest.evaluate(harness)) {
                next = getIterationOfBody(harness);
            } else {
                next = next();
                myUnwindSupport.finished(harness);
                unwindThis(harness);
            }
        } else {
            if (next.isTerminal()) {
                next = next();//NB:'End' used as a marker for stop...
            }
            myUnwindSupport.finished(harness);
            unwindThis(harness);
        }
        return next;
    }

    private ControlFlowStatement checkIteration(Harness harness)
    {
        ControlFlowStatement stopStatement = myMaxLoopsSupport.test(myLoopCount+1,harness);
        if (stopStatement==null) {
            if (myUnwindSupport.loop(harness)) {
                BALHelper.activate(myTest);
            }
        }
        return stopStatement;
    }

    private void resetThis()
    {
        myLoopCount= -1;
        myBody=null;
        myBodyFactory=null;
        myTest=null;
        myCursorKey=null;
        myCursorStoreType= BAL.getCursorStoreType();
        myUnwindSupport.reset(this);
        myMaxLoopsSupport.reset();
    }

    public void reconfigure()
    {
        resetThis();
        super.reconfigure();
        verifyReady();
    }

    private void unwindThis(Harness harness)
    {
        if (myCursorKey!=null && myLoopCount>=0) {
            BALHelper.clrData(myCursorKey,myCursorStoreType,harness);
        }
        resetThis();
    }

    public void unwind(Harness harness)
    {
        unwindThis(harness);
    }

    protected ControlFlowStatement getIterationOfBody(Harness harness)
    {
        myLoopCount++;
        if (myCursorKey!=null) {
            BALHelper.putData(myCursorKey,Integer.valueOf(myLoopCount),myCursorStoreType,harness);//NB: *before* factory call!
        }
        return BALHelper.makeIterationOfBody(this,harness,myBody,myBodyFactory);
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myTest,What.CRITERIA);
        Validate.stateIsTrue(myBody!=null || myBodyFactory!=null,
                "body or body-factory has been defined");
        if (breadcrumbs().isEnabled()) {
            if (myMaxLoopsSupport.isFailAlways()) {
                breadcrumbs().write("Setup to ALWAYS fail while-loop: {}",getWhatId());
            }
        }
    }

    protected StringBuilder addToString(StringBuilder sb) 
    {
        return super.addToString(sb).append("|loopnum=").append(myLoopCount);
    }


    private Condition myTest;
    private ControlFlowStatement myBody;
    private Action myBodyFactory;
    private int myLoopCount;
    private String myCursorKey;//OPTIONAL; holds 0-based loop count
    private StoreType myCursorStoreType= BAL.getCursorStoreType();
    private LimitSupport myMaxLoopsSupport;
    private ReentrantSupport myUnwindSupport;
}


/* end-of-WhileStatement.java */
