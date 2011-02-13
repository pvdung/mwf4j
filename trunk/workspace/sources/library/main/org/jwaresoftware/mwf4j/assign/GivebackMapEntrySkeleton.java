/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;

import  org.apache.commons.jexl2.JexlEngine;
import  org.apache.commons.jexl2.MapContext;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.What;

/**
 * Giveback implementation that returns the value of a map entry. The source 
 * of the map must be defined by subclasses. Can support keys as 'expressions'
 * for extracting member data from composite data elements. Between calls 
 * the value of the variable can change (it's up to caller to assure
 * concurrency semantics). The default constructors (no 'mode' specified) 
 * assume you want to read data using expressions (which also covers simple
 * get-by-key lookups).
 * <p/>
 * Underlying expression interpreter is the Jakarta Commons JEXL library.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public abstract class GivebackMapEntrySkeleton<T> implements Giveback<T>
{
    /** Marker used to differentiate between plain map get and expression evaluation. **/
    public static enum Mode {
        DIRECT, EXPRESSION;
    }

//  ---------------------------------------------------------------------------------------
//  Implementation:
//  ---------------------------------------------------------------------------------------

    protected GivebackMapEntrySkeleton(Class<? extends T> ofType)
    {
        this(ofType,null,true,Mode.EXPRESSION);
    }

    protected GivebackMapEntrySkeleton(Class<? extends T> ofType, T fallbackValue, boolean failIfError, boolean quiet, Mode mode)
    {
        Validate.notNull(ofType,What.CLASS_TYPE);
        myOfType = ofType;
        myDirectFlag = Mode.DIRECT.equals(mode);
        myFallbackValue = fallbackValue;
        myHaltIfErrorFlag = failIfError;
        myQuietFlag = quiet;
    }

    protected GivebackMapEntrySkeleton(Class<? extends T> ofType, T fallbackValue, boolean failIfError)
    {
        this(ofType,fallbackValue,failIfError,Mode.EXPRESSION);
    }

    protected GivebackMapEntrySkeleton(Class<? extends T> ofType, T fallbackValue, boolean failIfError, Mode mode)
    {
        this(ofType,fallbackValue,failIfError,false,mode);
    }

    public T call()
    {
        String selector = getSelectorOrFail();
        return read(selector);
    }

    @SuppressWarnings("unchecked")
    protected final T read(final String selector)
    {
        T gotten = null;
        try {
            Map<String,Object> datamap = getDataMap();
            Object raw=null;
            if (myDirectFlag) {
                raw = datamap.get(selector);//NB: no way to check this type-cast here
            } else {
                JexlEngine je = MyJexl.getEngine();
                raw = je.createExpression(selector).evaluate(new MapContext(datamap));
            }
            Validate.fieldIsAOrNull(raw,myOfType,What.DATA);
            gotten = (T)raw;
        } catch(RuntimeException getX) {
            if (myHaltIfErrorFlag) {
                throw new GivebackException(selector,getX);
            }
            gotten = null;
            if (!myQuietFlag && Diagnostics.ForFlow.isWarnEnabled())
                Diagnostics.ForFlow.warn("Unable to evaluate giveback '"+selector+"'",getX);
        }
        return gotten==null ? myFallbackValue : gotten;
    }


    /** Returns the map of source key-value objects for reading (only). 
     *  Never returns <i>null</i>. **/
    protected abstract Map<String,Object> getDataMap();


    /** Returns the lookup key or path for the giveback item. 
     *  Never returns <i>null</i> or blank. **/
    protected abstract String getSelector();


    private String getSelectorOrFail()
    {
        try {
            return getSelector();
        } catch(RuntimeException rtX) {
            throw new GivebackException(rtX);
        }
    }


    private T myFallbackValue;
    private final Class<? extends T> myOfType;
    private final boolean myDirectFlag;
    private final boolean myHaltIfErrorFlag;
    private final boolean myQuietFlag;
}


/* end-of-GivebackMapEntrySkeleton.java */
