/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;

/**
 * Giveback implementation that just returns the value of a predefined 
 * element in a source data map. Between calls the value of the element can
 * change (it's up to caller to assure concurrency semantics). Stored
 * item's class must be compatible with the class type specified in the
 * parameterized type.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class GivebackMapEntry<T> extends GivebackMapEntrySkeleton<T>
{
    public GivebackMapEntry(Map<String,Object> params, String key, Class<? extends T>ofType)
    {
        super(ofType);
        init(params,key);
    }

    public GivebackMapEntry(Map<String,Object> params, String key, T fallbackValue, Class<? extends T>ofType, boolean failIfError)
    {
        super(ofType,fallbackValue,failIfError);
        init(params,key);
    }

    protected Map<String,Object> getDataMap()
    {
        return myDatamap;
    }

    protected String getSelector()
    {
        return myKey;
    }

    private void init(Map<String,Object> params, String key)
    {
        Validate.notNull(params,What.PROPERTIES); 
        myDatamap = params;
        Validate.notBlank(key,What.ITEM_ID);
        myKey = key;
    }

    private String myKey;
    private Map<String,Object> myDatamap;
}


/* end-of-GivebackMapEntry.java */
