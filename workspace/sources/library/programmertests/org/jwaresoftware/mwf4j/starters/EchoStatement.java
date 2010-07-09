/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.assign.GivebackVar;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.bal.BAL;

/**
 * Capture name of items fed from a iterating or looping action.
 * Item name should be stored in the either the executing harness'
 * datamap or the current thread's MDC.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public class EchoStatement extends TestStatement
{
    public EchoStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    @Override
    public ControlFlowStatement run(Harness harness) 
    {
        setMyId(harness);
        return super.run(harness);
    }

    public void setCursor(String cursor)
    {
        Validate.notBlank(cursor,"cursor-name");
        myKey = cursor;
    }

    public void setStoreLocation(StoreType type)
    {
        Validate.notNull(type,"store-type");
        myType = type;
    }

    public void setIncludeActionName(boolean flag)
    {
        myIncludeFlag = flag;
    }

    private void setMyId(Harness harness) 
    {
        Object o=null;
        if (StoreType.DATAMAP.equals(myType)) {
            o = harness.getVariables().get(myKey);
        } else if (StoreType.THREAD.equals(myType)) {
            o = MDC.get(myKey);
        } else if (StoreType.PROPERTY.equals(myType)) {
            o = harness.getConfiguration().getString(myKey,null);
        } else if (StoreType.OBJECT.equals(myType)) {
            o = GivebackVar.fromEval(myKey,null,false).call();
        }
        myId = o==null ? null : Strings.valueOf(o);
        if (myIncludeFlag) {
            myId = getWhatId()+"["+myId+"]";
        }
    }

    private StoreType myType= BAL.getCursorStoreType();
    private String myKey= BAL.getCursorKey("");
    private boolean myIncludeFlag= false;
}


/* end-of-EchoStatement.java */
