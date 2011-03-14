/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.bal.BAL;

/**
 * Test action that does nothing but echo the value of a stored
 * bit of information. The stored information can be part of the
 * execution harness' datamap or the current thread MDC. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    helper,test
 * @see       EchoStatement
 **/

public class EchoAction extends ActionSkeleton
{
    public EchoAction()
    {
        this("echo");
    }

    public EchoAction(String id)
    {
        this(id,BAL.getCursorKey(id));
    }

    public EchoAction(String id, String cursor)
    {
        super(id);
        Validate.notBlank(cursor,"cursor-name");
        myCursor = cursor;
    }

    public EchoAction(String id, boolean inclFlag)
    {
        this(id,BAL.getCursorKey(id),inclFlag);
    }

    public EchoAction(String id, String cursor, boolean inclFlag)
    {
        this(id,cursor);
        myInclFlag = Boolean.valueOf(inclFlag);
    }

    public EchoAction(String id, String cursor, StoreType storeType)
    {
        this(id,cursor);
        myStoreType = storeType;
    }

    public EchoAction(String id, String cursor, boolean inclFlag, StoreType storeType)
    {
        this(id,cursor,storeType);
        myInclFlag = Boolean.valueOf(inclFlag);
    }

    public void configureStatement(ControlFlowStatement gen, Fixture environ)
    {
        Validate.isTrue(gen instanceof EchoStatement,"statement kindof EchoStatement");
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        EchoStatement statement = newEchoStatementInstance(next);
        statement.setCursor(myCursor);
        if (myStoreType!=null) {
            statement.setStoreLocation(myStoreType);
        }
        if (myInclFlag!=null) {
            statement.setIncludeActionName(myInclFlag);
        }
        return statement;
    }

    protected EchoStatement newEchoStatementInstance(ControlFlowStatement next)
    {
        return new EchoStatement(this,next);
    }

    protected StoreType myStoreType;
    protected String myCursor;
    protected Boolean myInclFlag;
}


/* end-of-EchoAction.java */
