/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.apache.commons.lang.ObjectUtils;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Pair;
import  org.jwaresoftware.gestalt.reveal.Identified;
import  org.jwaresoftware.gestalt.reveal.Named;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;

/**
 * Common struct definition of fields used to track a variable's name and 
 * its {@linkplain StoreType store type} implementation. Defaults to saving
 * the named item (key) into the current harness's data map. We consider
 * references with blank names (null or all whitespace) to be undefined.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,infra,helper
 **/

public final class Reference extends Pair<String,StoreType> 
    implements Named, Identified, Resettable, Cloneable, Comparable<Reference>, Declarable
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

    public Reference(StoreType storeType)
    {
        Validate.notNull(storeType,What.TYPE);
        ini(null,storeType);
    }
 
    public boolean isUndefined()
    {
        return Strings.isBlank(getName());
    }

    public boolean isDefined()
    {
        return !isUndefined();
    }

    public void reset()
    {
        resetThis();
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

    public void copyFrom(String key, StoreType storeType)
    {
        if (storeType==null) {
            storeType = getDefaultDataType();
        }
        if (Strings.isBlank(key)) {
            ini(null,storeType);
        } else {
            ini(key,storeType);
        }
    }

    public final void set(Reference other)
    {
        Validate.notNull(other,What.REFERENCE);
        set(other.get1(),other.get2());
    }

    public static Reference newFrom(Reference from)
    {
        return from==null ? null : new Reference(from);
    }

    private void resetThis()
    {
        ini(null,getDefaultDataType());
    }

    @Override
    public boolean equals(Object other)
    {
        if (other==this)
            return true;
        boolean is = false;
        if (other!=null && getClass().equals(other.getClass())) {
            Reference otherref= (Reference)other;
            if (isUndefined() && otherref.isUndefined()) {
                is= true;//IGNORE storeType!
            } else { 
                is= Strings.equal(getId(),otherref.getId()) &&
                    ObjectUtils.equals(getStoreType(),otherref.getStoreType());
            }
        }
        return is;
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(getId());
    }

    @Override
    public Object clone()
    {
        try {
            return super.clone();
        } catch(CloneNotSupportedException clnX) {
            throw new InternalError();
        }
    }

    @Override
    public int compareTo(Reference other)
    {
        int typeCmp = getStoreType().compareTo(other.getStoreType());//groups by type
        if (other==this)
            return 0;
        if (other==null)
            throw new NullPointerException();//Yik, but per contract!
        if (isUndefined())
            return other.isUndefined() ? typeCmp : -1;
        if (other.isUndefined())
            return 1;
        if (typeCmp!=0)
            return typeCmp;
        return getName().compareTo(other.getName());
    }

    private void initThisFrom(Reference other)
    {
        ini(other.get1(),other.get2());
    }

    @Override
    public void freeze(Fixture environ)
    {
        if (isDefined()) {
            super.set1(environ.interpolate(super.get1()));
        }
    }
}


/* end-of-Reference.java */
