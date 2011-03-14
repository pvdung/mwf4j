/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;

/**
 * Giveback that returns the value of a predefined harness variable. Can support
 * keys as 'expressions' for extracting member data from composite data elements.
 * Expects harness has been set in the current thread's MDC under 
 * {@linkplain MDC#currentHarness()}. If a harness is not present or requested variable
 * not present, this giveback returns <i>null</i> when asked for value. The default
 * constructors (no 'mode' specified) assume you want to read
 * data using expression (which also covers simple get-by-key lookups).
 * <p/>
 * Underlying expression interpreter is the Jakarta Commons JEXL library.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 * @see       MDC#currentVariablesOrNull()
 **/

public final class GivebackVar<T> extends GivebackMapEntrySkeleton<T>
{
//  ---------------------------------------------------------------------------------------
//  Easy factory methods named for intended lookup (easier than big ctor):
//  ---------------------------------------------------------------------------------------

    public final static<T> GivebackVar<T> fromGet(String key, Class<? extends T> ofType)
    {
        return new GivebackVar<T>(key,null,ofType,true,Mode.DIRECT);
    }

    public final static<T> GivebackVar<T> fromGet(String key, T fallbackValue, Class<? extends T> ofType)
    {
        return new GivebackVar<T>(key,fallbackValue,ofType,true,Mode.DIRECT);
    }

    public final static<T> GivebackVar<T> fromGet(String key, T fallbackValue, Class<? extends T> ofType, boolean failIfError)
    {
        return new GivebackVar<T>(key,fallbackValue,ofType,failIfError,Mode.DIRECT);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, Class<? extends T>ofType)
    {
        return new GivebackVar<T>(keyOrExpr,null,ofType,true,Mode.EXPRESSION);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, T fallbackValue, Class<? extends T> ofType)
    {
        return new GivebackVar<T>(keyOrExpr,fallbackValue,ofType,true,Mode.EXPRESSION);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, Class<? extends T> ofType, boolean failIfError)
    {
        return new GivebackVar<T>(keyOrExpr,null,ofType,failIfError,Mode.EXPRESSION);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, T fallbackValue, Class<? extends T> ofType, boolean failIfError)
    {
        return new GivebackVar<T>(keyOrExpr,fallbackValue,ofType,failIfError,Mode.EXPRESSION);
    }
    
    public final static<T> GivebackVar<T> fromEvalOfOptional(String keyOrExpr, Class<? extends T> ofType)
    {
        return new GivebackVar<T>(keyOrExpr,null,ofType,false,true,Mode.EXPRESSION);
    }

// --- Eases Testing:
    
    public final static GivebackVar<String> fromGetS(String key)
    {
        return fromGet(key,String.class);
    }

    public final static GivebackVar<String> fromEvalS(String keyOrExpr)
    {
        return fromEval(keyOrExpr,String.class);
    }


//  ---------------------------------------------------------------------------------------
//  Implementation:
//  ---------------------------------------------------------------------------------------

    public GivebackVar(String keyOrExpr, Class<? extends T> ofType) //Ez for testing and IoC defaults
    {
        super(ofType);
        Validate.notBlank(keyOrExpr,What.ITEM_ID);
        myKeyOrExpr = keyOrExpr;
    }

    public GivebackVar(String keyOrExpr, T fallbackValue, Class<? extends T> ofType, boolean failIfError, boolean quiet, Mode mode)
    {
        super(ofType,fallbackValue,failIfError,quiet,mode);
        Validate.notBlank(keyOrExpr,What.ITEM_ID);
        myKeyOrExpr = keyOrExpr;
    }

    public GivebackVar(String keyOrExpr, T fallbackValue, Class<? extends T> ofType, boolean failIfError, Mode mode)
    {
        this(keyOrExpr,fallbackValue,ofType,failIfError,false,mode);
    }

    protected String getSelector()
    {
        return myKeyOrExpr;
    }

    protected Map<String,Object> getDataMap()
    {
        Map<String,Object> mdc = MDC.currentVariablesOrNull();
        if (mdc==null) return Empties.newMap();
        return mdc;
    }


    private final String myKeyOrExpr;
}


/* end-of-GivebackVar.java */
