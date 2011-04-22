/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.concurrent.ConcurrentMap;

import  org.apache.commons.jexl2.JexlEngine;
import  org.apache.commons.jexl2.MapContext;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackMapEntrySkeleton.Mode;

/**
 * Assignment helper that just saves the provided payload to the current 
 * harness's variables (directly or via an expression to get at nested items).
 * Note that the default mode 'EXPRESSION' can handle direct-to-variable setting
 * also; however, for this functionality you get more per-call overhead due to 
 * the creation of the Jexl interpreter. So, for simple map puts, it's  
 * better to create a {@linkplain #toMap() direct saveback data} instance. 
 * Throws a {@linkplain SavebackException saveback exception} if the harness
 * is unable to save the value for any reason. Null puts are interpreted as
 * removes for generic maps and null assignments for expressions.
 * <p/>
 * <pre>
 *   SavebackVar&lt;Long&gt; longSaver = new SavebackVar&lt;Long&gt;();
 *   longSaver.put("mybean.array[index].timestamp",System.currentTimeMillis());
 * </pre>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,infra,helper
 **/

public final class SavebackVar<T> implements PutMethod<T>
{
//  ---------------------------------------------------------------------------------------
//  Easy factory methods named for intended lookup:
//  ---------------------------------------------------------------------------------------

    public final static<T> SavebackVar<T> toMap()
    {
        return new SavebackVar<T>(Mode.DIRECT,true);
    }

    public final static<T> SavebackVar<T> toObject()
    {
        return new SavebackVar<T>(Mode.EXPRESSION,true);
    }

    public final static<T> SavebackVar<T> toObject(boolean failIfError)
    {
        return new SavebackVar<T>(Mode.EXPRESSION,failIfError);
    }

//  ---------------------------------------------------------------------------------------
//  Implementation:
//  ---------------------------------------------------------------------------------------

    public SavebackVar(Mode mode, boolean failIfError)
    {
        myDirectFlag = Mode.DIRECT.equals(mode);
        myHaltIfErrorFlag = failIfError;
    }

    public SavebackVar(boolean failIfError)
    {
        this(Mode.EXPRESSION,failIfError);
    }

    public SavebackVar()
    {
        this(Mode.EXPRESSION,true);
    }

    public SavebackVar(ConcurrentMap<String,Object> local)
    {
        this();
        Validate.notNull(local,What.VARIABLES);
        myDataMap = local;
    }

    public boolean put(final String keyOrExpr, T payload)
    {
        if (!myDirectFlag && payload==null) {
            return putNull(keyOrExpr);
        }
        Validate.notBlank(keyOrExpr,What.ITEM_ID);
        boolean clean=true;
        try {
            ConcurrentMap<String,Object> datamap = getDataMap();
            if (myDirectFlag) {
                datamap.put(keyOrExpr, payload);
            } else {
                JexlEngine je = MyJexl.getEngine();
                je.setProperty(new MapContext(datamap),datamap,keyOrExpr,payload);
            }
        } catch(RuntimeException setX) {
            if (myHaltIfErrorFlag) {
                throw new SavebackException(keyOrExpr,setX);
            }
            clean = false;
            if (Diagnostics.ForFlow.isWarnEnabled())
                Diagnostics.ForFlow.warn("Unable to eval putback '"+keyOrExpr+"'",setX);
        }
        return clean;
    }

    public boolean putNull(final String keyOrExpr)
    {
        Validate.notBlank(keyOrExpr,What.ITEM_ID);
        boolean clean=true;
        try {
            ConcurrentMap<String,Object> datamap = getDataMap();
            if (myDirectFlag) {
                datamap.remove(keyOrExpr);
            } else {
                JexlEngine je = MyJexl.getEngine();
                je.createExpression(keyOrExpr+" = null").evaluate(new MapContext(datamap));
            }
        } catch(RuntimeException setX) {
            if (myHaltIfErrorFlag) {
                throw new SavebackException(keyOrExpr,setX);
            }
            clean = false;
            if (Diagnostics.ForFlow.isWarnEnabled())
                Diagnostics.ForFlow.warn("Unable to eval setnull '"+keyOrExpr+"'",setX);
        }
        return clean;
    }

    private ConcurrentMap<String,Object> getDataMap()
    {
        return myDataMap==null ? MDC.currentVariables() : myDataMap;
    }


    private final boolean myDirectFlag;
    private final boolean myHaltIfErrorFlag;
    private ConcurrentMap<String,Object> myDataMap;
}


/* end-of-SavebackVar.java */
