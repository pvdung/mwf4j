/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;

import  org.jwaresoftware.mwf4j.MDC;

/**
 * Giveback that returns the value of a predefined harness variable with the
 * name or expression path of that variable stored under the fixed key 
 * "{@linkplain #ITEM_NAME &#46;giveback}". Once the actual source variable's
 * information is retrieved, this giveback behaves identically to 
 * {@linkplain GivebackVar}.
 * <pre>
 * sequence.add(new CallAction&lt;Order&gt;("getorder",new GivebackFrom(),"order",DATAMAP));
 * ...
 * vars.put(".giveback","newSingleOrder.internalForm");
 * </pre>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras,helper
 **/

public final class GivebackFrom<T> extends GivebackMapEntrySkeleton<T>
{
    public final static String ITEM_NAME = ".giveback";

    public GivebackFrom(Class<? extends T> ofType)
    {
        super(ofType);
    }

    protected Map<String,Object> getDataMap()
    {
        return MDC.currentVariables();
    }

    protected String getSelector()
    {
        return MDC.currentVariables().getOrFail(ITEM_NAME,String.class);
    }
}


/* end-of-GivebackFrom.java */
