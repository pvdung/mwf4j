/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;
 
import  java.util.Collection;
import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Entity;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.EvaluateVar;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.helpers.And;
import  org.jwaresoftware.mwf4j.helpers.False;
import  org.jwaresoftware.mwf4j.helpers.Not;
import  org.jwaresoftware.mwf4j.helpers.Or;
import  org.jwaresoftware.mwf4j.helpers.True;
import  org.jwaresoftware.mwf4j.loop.DelimitedStringToCollectionCallback;
import  org.jwaresoftware.mwf4j.loop.IntRangeToCollectionCallback;

/**
 * Starting implementation for the various builders for MWf4J. The main
 * focus is cutting down on the source that must be written to create an
 * output action or activity object (typically for testing). Expects
 * you to rely HEAVILY on the 'import static ....*' functionality.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,extras,helper
 **/

public abstract class BuilderSkeleton implements Entity
{
    public static final StoreType VARIABLE= StoreType.DATAMAP;
    public static final StoreType PROPERTY= StoreType.PROPERTY;
    public static final StoreType ONTHREAD= StoreType.THREAD;

    public static final Flag PROTECTED = new Flag("haltIfError",true);
    public static final Flag TRYEACH = new Flag("tryEach",true);
    public static final Flag MULTIUSE = new Flag("multiple",true);
    public static final Flag DECLARABLES  = new Flag("declarable",true);
    public static final Flag NO_DECLARABLES  = new Flag("declarable",false);
    public static final Flag HALTIFMAX = new Flag("haltIfMax",true);
    public static final Flag NO_HALTIFMAX = new Flag("haltIfMax",false);

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

    public final static class MDCID extends ID {
        MDCID(String name) {
            super(name);
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

    public final static MDCID mdc(String name)
    {
        return new MDCID(name);
    }

    public final static Reference get(String name)
    {
        return ref(name,StoreType.OBJECT);//For now...
    }



    public final static Condition all(Condition...tests)
    {
        Validate.notNull(tests,What.CRITERIA);
        return new And(tests);
    }

    public final static Condition any(Condition...tests)
    {
        Validate.notNull(tests,What.CRITERIA);
        return new Or(tests);
    }

    public final static Condition not(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        return new Not(test);
    }

    public final static Condition notnull(String keyOrExpr)
    {
        Validate.notBlank(keyOrExpr,What.CRITERIA);
        return new EvaluateVar(keyOrExpr,EvaluateVar.NotNull);
    }

    public final static Condition istrue()
    {
        return True.INSTANCE;
    }

    public final static Condition istrue(String keyOrExpr)
    {
        Validate.notBlank(keyOrExpr,What.CRITERIA);
        return new EvaluateVar(keyOrExpr,EvaluateVar.IsTrue);
    }

    public final static Condition isfalse()
    {
        return False.INSTANCE;
    }

    public final static Condition isfalse(String keyOrExpr)
    {
        Validate.notBlank(keyOrExpr,What.CRITERIA);
        return new EvaluateVar(keyOrExpr,EvaluateVar.IsFalse);
    }

    public final static Condition isnull(String keyOrExpr)
    {
        Validate.notBlank(keyOrExpr,What.CRITERIA);
        return new EvaluateVar(keyOrExpr,EvaluateVar.IsNull);
    }



    public final static Callable<Collection<String>> in(String delimitedList)
    {
        return new DelimitedStringToCollectionCallback(delimitedList);
    }

    public final static Callable<Collection<String>> in(String delimitedList, String delims)
    {
        return new DelimitedStringToCollectionCallback(delimitedList, delims);
    }

    public final static Callable<Collection<Integer>> in(int start, int end, int delta)
    {
        return new IntRangeToCollectionCallback(start,end,delta);
    }

    public final static Callable<Collection<Integer>> in(int start, int end)
    {
        return new IntRangeToCollectionCallback(start,end,1);
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
        myId= (id==null) ? Strings.EMPTY : id;
    }

    protected final void validateNotNull(ID id, String what)
    {
        Validate.notNull(id,what);
        Validate.isFalse(id.isNull(),what);
    }

    private String myId= Strings.EMPTY;
}


/* end-of-BuilderSkeleton.java */
