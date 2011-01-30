/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Pair;
import  org.jwaresoftware.gestalt.reveal.Identified;
import  org.jwaresoftware.gestalt.reveal.Named;

import  org.jwaresoftware.mwf4j.What;

/**
 * Common struct definition of fields used to track a variable and its
 * {@linkplain StoreType store type} implementation. Defaults to saving
 * the named item (key) into the current harness's data map. We consider
 * references with blank names (null or all whitespace) to be undefined.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public final class Reference extends Pair<String,StoreType> implements Named, Identified
{
    public final static StoreType getDefaultDataType()
    {
        return StoreType.DATAMAP;
    }

    public Reference()
    {
        resetThis();
    }

    public Reference(Reference from)
    {
        Validate.notNull(from,What.REFERENCE);
        initThisFrom(from);
    }

    public Reference(String key, StoreType storeType)
    {
        setName(key);
        setStoreType(storeType);
    }

    public Reference(String key)
    {
        this(key,getDefaultDataType());
    }

    public boolean isUndefined()
    {
        return Strings.isBlank(getName());
    }

    public String getName()
    {
        return get1();
    }

    public String getId()
    {
        return getName();
    }

    @Override
    public String set1(String key)
    {
        Validate.notBlank(key,What.KEY);
        return super.set1(key);
    }

    public final void setName(String key)
    {
        set1(key);
    }

    public StoreType getStoreType()
    {
        return get2();
    }

    @Override
    public StoreType set2(StoreType storeType)
    {
        Validate.notNull(storeType,What.TYPE);
        return super.set2(storeType);
    }

    public final void setStoreType(StoreType storeType)
    {
        set2(storeType);
    }

    public void copyFrom(Reference other)
    {
        Validate.notNull(other,What.REFERENCE);
        initThisFrom(other);        
    }

    private void resetThis()
    {
        ini(null,getDefaultDataType());
    }

    private void initThisFrom(Reference other)
    {
        ini(other.get1(),other.get2());
    }
}


/* end-of-Reference.java */
