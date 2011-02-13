/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import  org.jwaresoftware.mwf4j.assign.GivebackVar;
import  org.jwaresoftware.mwf4j.assign.GivebackMDC;
import  org.jwaresoftware.mwf4j.assign.GivebackProperty;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Simple assignment action of a preset value of some type. Really a 
 * glorified wrapper around the generic {@linkplain CallAction} that 
 * expresses that class' API is a simpler pre-canned form. Still can
 * support a callback 'getter' for complex values, but expected
 * use is for simple "a = b" or "a = data.field" type assignments.
 * <pre>
 * AssignAction&lt;?&gt; set = new AssignAction&lt;String&gt;();
 * set.setFrom("java.version",StoreType.SYSTEM,String.class);
 * set.setTo("config.baseVersion",StoreType.OBJECT);
 * ...
 * SequenceAction block = new SequenceAction();
 * block.add(new AssignAction&lt;String&gt;("FIX.version","4.2"))
 *      .add(new AssignAction&lt;Long&gt;("startTime",DATAMAP,"config.bootTime",OBJECT,Long.class);
 * </pre>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for makeStatement)
 * @.group    infra,impl
 * @see       GivebackValue
 * @see       GivebackVar
 **/

public final class AssignAction<T> extends CallAction<T>
{
    public AssignAction(String id, String toKey, StoreType toStoreType, T dataValue) //set
    {
        super(id, new GivebackValue<T>(dataValue),toKey,toStoreType);
    }

    public AssignAction(String toKey, T dataValue)
    {
        this("set",toKey,BAL.getDataStoreType(),dataValue);
    }

    public AssignAction(String toKey, StoreType toStoreType, T dataValue)
    {
        this("set",toKey,toStoreType,dataValue);
    }

    public AssignAction(String id, String toKey, StoreType toStoreType, String fromKey, StoreType fromStoreType, Class<? extends T> ofType) //copy
    {
        super(id);
        setTo(toKey,toStoreType);
        setFrom(fromKey,fromStoreType,ofType);
    }

    public AssignAction(String toKey, StoreType toStoreType, String fromKey, StoreType fromStoreType, Class<? extends T> ofType)
    {
        this("set",toKey,toStoreType,fromKey,fromStoreType,ofType);
    }

    public AssignAction(Reference toKey, Reference fromKey, Class<? extends T> ofType)
    {
        super("set",toKey);
        if (fromKey!=null && fromKey.isDefined()) {
            setFrom(fromKey.getName(),fromKey.getStoreType(),ofType);
        }
    }

    public AssignAction(String id)
    {
        super(id);
    }

    public AssignAction()
    {
        this("set");
    }

    public final void setTo(String toKey)
    {
        setTo(toKey,BAL.getDataStoreType());
    }
 
    public final void setTo(String toKey, StoreType toStoreType)
    {
        setToKey(toKey);
        setToStoreType(toStoreType);
    }

    public final void setFrom(T dataValue)
    {
        setGetter(new GivebackValue<T>(dataValue));
    }

    public final void setFrom(String fromKey, Class<? extends T> ofType)
    {
        setFrom(fromKey,BAL.getDataStoreType(),null,true,ofType);
    }

    public final void setFrom(String fromKey, StoreType fromStoreType, Class<? extends T> ofType)
    {
        setFrom(fromKey,fromStoreType,null,true,ofType);
    }

    @SuppressWarnings("unchecked")
    public final void setFrom(String fromKeyOrExpr, StoreType fromStoreType, T fallbackValue, boolean failIfError, Class<? extends T> ofType)
    {
        validateIsTypeChecked(fromStoreType,ofType);
        setGetterRequiredReturnType(ofType);

        Callable<T> getter = null;
        switch(fromStoreType) {
            case DATAMAP: {
                getter = GivebackVar.fromGet(fromKeyOrExpr,fallbackValue,ofType);
                break;
            }
            case OBJECT: {
                getter = GivebackVar.fromEval(fromKeyOrExpr,fallbackValue,ofType,failIfError);
                break;
            }
            case PROPERTY: {
                getter = (Callable<T>)GivebackProperty.fromHarness(fromKeyOrExpr,(String)fallbackValue);//OK-- checked
                break;
            }
            case SYSTEM: {
                getter = (Callable<T>)GivebackProperty.fromSystem(fromKeyOrExpr,(String)fallbackValue); //OK-- checked
                break;
            }
            case THREAD: {
                getter = new GivebackMDC<T>(fromKeyOrExpr,ofType,fallbackValue);
                break;
            }
            default: {
                getter = new GivebackValue<T>(fallbackValue);
            }
        }
        setGetter(getter);
    }

    private void validateIsTypeChecked(StoreType fromType, Class<? extends T> ofType)
    {
        boolean strings = (StoreType.PROPERTY.equals(fromType) || StoreType.SYSTEM.equals(fromType));
        if (strings) {
            Validate.notNull(ofType,What.CLASS_TYPE);
            Validate.isTrue(String.class.isAssignableFrom(ofType),ofType.getSimpleName()+" isa String");
        }
    }
}


/* end-of-AssignAction.java */
