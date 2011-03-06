/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;
 
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Entity;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Starting implementation for the various builders for MWf4J. The main
 * focus is cutting down on the source that must be written to create an
 * output action or activity object.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,infra
 **/

public abstract class BuilderSkeleton implements Entity
{
    public static final StoreType VARIABLE= StoreType.DATAMAP;
    public static final StoreType PROPERTY= StoreType.PROPERTY;
    public static final StoreType ONTHREAD= StoreType.THREAD;

    public static final Flag PROTECT   = new Flag("haltIfError",true);
    public static final Flag TRYEACH   = new Flag("tryEach",true);
    public static final Flag MULTIUSE  = new Flag("multiple",true);

    public final static class Property extends ID {
        Property(String name) {
            super(name);
        }
    }

    public final static class Variable extends ID {
        Variable(String nameOrExpr) {
            super(nameOrExpr);
        }
    }



    public final static ID id(String value)
    {
        return new ID(value);
    }

    public final static Property env(String name)
    {
        return new Property(name);
    }

    public final static Property property(String name)
    {
        return new Property(name);
    }

    public final static Variable var(String name)
    {
        return new Variable(name);
    }

    public final static Variable variable(String name)
    {
        return new Variable(name);
    }

    public final static Reference ref(String name)
    {
        return new Reference(name);
    }

    public final static Reference ref(String name, StoreType type)
    {
        return new Reference(name,type);
    }

    public final static Reference get(String name)
    {
        return ref(name,StoreType.OBJECT);//For now...
    }




    protected BuilderSkeleton()
    {
    }

    protected BuilderSkeleton(String id)
    {
        setId(id);
    }

    public final String getId()
    {
        return myId;
    }

    protected void setId(String id)
    {
        myId= id;
    }

    protected final void validateNotNull(ID id, String what)
    {
        Validate.notNull(id,what);
        Validate.isFalse(id.isNull(),what);
    }

    private String myId= Strings.EMPTY;
}


/* end-of-BuilderSkeleton.java */
