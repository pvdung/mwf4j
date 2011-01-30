/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.Callable;

import  org.jwaresoftware.mwf4j.assign.GivebackValue;
import  org.jwaresoftware.mwf4j.assign.GivebackVar;
import  org.jwaresoftware.mwf4j.assign.GivebackMDC;
import  org.jwaresoftware.mwf4j.assign.GivebackProperty;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Simple assignment action of a preset value of some type. Really a 
 * glorified wrapper around the generic {@linkplain CallAction} that 
 * expresses that class' API is a simpler pre-canned form. Still can
 * support a callback 'getter' for complex values, but expected
 * use is for simple "a = b" or "a = data.field" type assignments.
 * <pre>
 * AssignAction set = new AssignAction();
 * set.setFrom("java.version",StoreType.SYSTEM);
 * set.setTo("config.baseVersion",StoreType.OBJECT);
 * ...
 * SequenceAction block = new SequenceAction();
 * block.add(new AssignAction<String>("FIX.version","4.2"))
 *      .add(new AssignAction<Long>("startTime",StoreType.DATAMAP,"config.bootTime",StoreType.OBJECT));
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
        this("set",toKey,StoreType.DATAMAP,dataValue);
    }

    public AssignAction(String toKey, StoreType toStoreType, T dataValue)
    {
        this("set",toKey,toStoreType,dataValue);
    }

    public AssignAction(String id, String toKey, StoreType toStoreType, String fromKey, StoreType fromStoreType) //copy
    {
        super(id);
        setTo(toKey,toStoreType);
        setFrom(fromKey,fromStoreType);
    }

    public AssignAction(String toKey, StoreType toStoreType, String fromKey, StoreType fromStoreType)
    {
        this("set",toKey,toStoreType,fromKey,fromStoreType);
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
        setTo(toKey,StoreType.DATAMAP);
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

    public final void setFrom(String fromKey, StoreType fromStoreType)
    {
        setFrom(fromKey,fromStoreType,null,true);
    }
 
    @SuppressWarnings("unchecked")
    public final void setFrom(String fromKeyOrExpr, StoreType fromType, T fallbackValue, boolean failIfError)
    {
        Callable<T> getter = null;
        switch(fromType) {
            case DATAMAP: {
                getter = GivebackVar.fromGet(fromKeyOrExpr,fallbackValue);
                break;
            }
            case OBJECT: {
                getter = GivebackVar.fromEval(fromKeyOrExpr,fallbackValue,failIfError);
                break;
            }
            case PROPERTY: {
                getter = (Callable<T>)GivebackProperty.fromHarness(fromKeyOrExpr,(String)fallbackValue);//Hmm...
                break;
            }
            case SYSTEM: {
                getter = (Callable<T>)GivebackProperty.fromSystem(fromKeyOrExpr,(String)fallbackValue);//Hmm...
                break;
            }
            case THREAD: {
                getter = (Callable<T>)new GivebackMDC<Object>(fromKeyOrExpr,Object.class,fallbackValue);//Hmm...
                break;
            }
            default: {
                getter = new GivebackValue<T>(fallbackValue);
            }
        }
        setGetter(getter);
    }
}


/* end-of-AssignAction.java */
