/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;

/**
 * Giveback that returns the value of a predefined harness variable. Can support
 * keys as 'expressions' for extracting member data from composite data elements.
 * Expects harness has been set in the current thread's MDC under 
 * {@linkplain MDC#currentHarness()}. If harness not present or requested variable
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
 * @see       MDC#currentVariables()
 **/

public final class GivebackVar<T> extends GivebackMapEntrySkeleton<T>
{
//  ---------------------------------------------------------------------------------------
//  Easy factory methods named for intended lookup (easier than big ctor):
//  ---------------------------------------------------------------------------------------

    public final static<T> GivebackVar<T> fromGet(String key)
    {
        return new GivebackVar<T>(key,null,true,Mode.DIRECT);
    }

    public final static<T> GivebackVar<T> fromGet(String key, T fallbackValue)
    {
        return new GivebackVar<T>(key,fallbackValue,true,Mode.DIRECT);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr)
    {
        return new GivebackVar<T>(keyOrExpr,null,true,Mode.EXPRESSION);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, T fallbackValue)
    {
        return new GivebackVar<T>(keyOrExpr,fallbackValue,true,Mode.EXPRESSION);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, boolean failIfError)
    {
        return new GivebackVar<T>(keyOrExpr,null,failIfError,Mode.EXPRESSION);
    }

    public final static<T> GivebackVar<T> fromEval(String keyOrExpr, T fallbackValue, boolean failIfError)
    {
        return new GivebackVar<T>(keyOrExpr,fallbackValue,failIfError,Mode.EXPRESSION);
    }
    
    public final static<T> GivebackVar<T> fromEvalOfOptional(String keyOrExpr)
    {
        return new GivebackVar<T>(keyOrExpr,null,false,true,Mode.EXPRESSION);
    }

//  ---------------------------------------------------------------------------------------
//  Implementation:
//  ---------------------------------------------------------------------------------------

    public GivebackVar(String keyOrExpr) //Ez for testing and IoC defaults
    {
        Validate.notBlank(keyOrExpr,What.ITEM_ID);
        myKeyOrExpr = keyOrExpr;
    }

    public GivebackVar(String keyOrExpr, T fallbackValue, boolean failIfError, boolean quiet, Mode mode)
    {
        super(fallbackValue,failIfError,quiet,mode);
        Validate.notBlank(keyOrExpr,What.ITEM_ID);
        myKeyOrExpr = keyOrExpr;
    }

    public GivebackVar(String keyOrExpr, T fallbackValue, boolean failIfError, Mode mode)
    {
        this(keyOrExpr,fallbackValue,failIfError,false,mode);
    }

    protected String getSelector()
    {
        return myKeyOrExpr;
    }

    protected Map<String,Object> getDataMap()
    {
        return MDC.currentVariables();
    }


    private final String myKeyOrExpr;
}


/* end-of-GivebackVar.java */
