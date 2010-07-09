/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Collection;
import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that will iterate over a collection of objects (assumed to
 * be 'data' objects) and call an application-supplied action for
 * each. Each data object is stored in either the execution's harness
 * datamap or in the current thread's data stash. Application must 
 * supply the key used for storing the data object temporarily.
 * <p/>
 * The body of a foreach will be asked to create a new statement
 * for each loop iteration unless you disable this with the
 * {@linkplain #setCopy(boolean) copy flag}; it's turned ON by default.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       ForEachStatement
 * @see       WhileAction
 **/

public class ForEachAction extends ActionSkeleton
{
    public ForEachAction()
    {
        this("foreach");
    }

    public ForEachAction(String id)
    {
        super(id);
        myCursorKey=BAL.getCursorKey(getId());
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

    public void setBody(Action body)
    {
        Validate.notNull(body,What.ACTION);
        myBody = body;
    }

    public void setDataset(Callable<Collection<?>> getter)
    {
        Validate.notNull(getter,What.GET_METHOD);
        myGetter = getter;
    }

    public void setDataset(Collection<?> dataset)
    {
        Validate.notNull(dataset,What.DATA);
        myGetter = new GivebackValue<Collection<?>>(dataset);
    }

    public void setCopy(boolean flag)
    {
        myCopyFlag = flag;
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        verifyReady();
        ForEachStatement statement = new ForEachStatement(this,next);
        return finish(statement);
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof ForEachStatement,"statement kindof foreach");
        ForEachStatement foreach = (ForEachStatement)statement;
        foreach.setCursorKey(myCursorKey);
        foreach.setCursorStoreType(myCursorStoreType);
        foreach.setGetter(myGetter);
        if (myBody!=null) {
            if (myCopyFlag) {
                foreach.setBodyFactory(myBody);
            } else {
                foreach.setBody(myBody.makeStatement(foreach));
            }
        } else {
            foreach.setBody(new EmptyStatement(foreach));
        }
    }

    private String myCursorKey;
    private Action myBody;
    private StoreType myCursorStoreType = BAL.getCursorStoreType();
    private boolean myCopyFlag = BAL.getNewStatementPerLoopFlag();
    private Callable<Collection<?>> myGetter = ForEachStatement.GivebackEMPTY_LIST;

}


/* end-of-ForEachAction.java */
