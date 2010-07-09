/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Set;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Pair;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.behaviors.Protector;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that lets you install a set of error handler and cleanup actions
 * that are executed in response to exceptions thrown by a nested body action.
 * Bascially an implementation of the common 'try-catch-finally' Java or C 
 * construct.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for makeStatement)
 * @.group    infra,impl
 **/

public class TryCatchAction extends ActionSkeleton implements Protector
{
    public TryCatchAction()
    {
        this("trycatch");
    }

    public TryCatchAction(String id)
    {
        super(id);
    }

    public final void setHaltIfError(boolean flag)
    {
        myHaltIfErrorFlag = flag;
    }

    public void setBody(Action body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    public void setAlways(Action always)
    {
        Validate.notNull(always,What.ACTION);
        myAlways = always;
    }

    public void addIfError(Class<? extends Exception> forType, Action handler)
    {
        Validate.neitherNull(forType,What.CLASS_TYPE,handler,What.ACTION);
        Pair<Class<? extends Exception>,Action> def = new Pair<Class<? extends Exception>,Action>(forType,handler);
        boolean updated=false;
        for (Pair<Class<? extends Exception>,Action> next:myHandlers) {
            if (next.equals1(forType)) {
                next.set2(handler);
                updated=true;
                break;
            }
        }
        if (!updated) {
            myHandlers.add(def);
        }
    }

    public final void setQuiet(boolean flag)
    {
        myQuietFlag = flag;
    }

    public void setUnmask(boolean flag)
    {
        myUnmaskWrappersFlag = flag;
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myHaltContinuationFlag = flag;
    }

    public void setErrorKey(String key)
    {
        Validate.notBlank(key,What.KEY);
        myErrorKey = key;
    }

    public void setErrorStoreType(StoreType storeType)
    {
        Validate.notNull(storeType,What.TYPE);
        myErrorStoreType = storeType;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof TryCatchStatement,"statement kindof trycatch");
        TryCatchStatement trycatch = (TryCatchStatement)statement;
        if (myBody!=null) {
            trycatch.setBody(myBody);
        } else {
            trycatch.setBody(new EmptyAction());
        }
        if (myAlways!=null) {
            trycatch.setAlways(myAlways);
        }
        if (!myHandlers.isEmpty()) {
            trycatch.addIfError(myHandlers);
        }
        trycatch.setHaltIfError(myHaltIfErrorFlag);
        trycatch.setQuiet(myQuietFlag);
        trycatch.setUseHaltContinuation(myHaltContinuationFlag);
        if (myUnmaskWrappersFlag!=null) {
            trycatch.setUnmask(myUnmaskWrappersFlag);
        }
        if (myErrorKey!=null) {
            trycatch.setErrorKey(myErrorKey, myErrorStoreType);
        }
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        verifyReady();
        TryCatchStatement trycatch = new TryCatchStatement(this,next);
        trycatch.setHaltIfError(myHaltIfErrorFlag);
        trycatch.setQuiet(myQuietFlag);
        return finish(trycatch);
    }

    private boolean myHaltIfErrorFlag = true;
    private boolean myQuietFlag = false;
    private Action myBody;
    private Action myAlways;
    private boolean myHaltContinuationFlag = BAL.getUseHaltContinuationsFlag();
    private Set<Pair<Class<? extends Exception>,Action>> myHandlers= LocalSystem.newSet();
    private Boolean myUnmaskWrappersFlag;
    private String myErrorKey;
    private StoreType myErrorStoreType= BAL.getDataStoreType();
}


/* end-of-TryCatchAction.java */
