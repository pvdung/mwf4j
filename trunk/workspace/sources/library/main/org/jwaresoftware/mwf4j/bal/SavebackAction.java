/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Feedback;
import  org.jwaresoftware.mwf4j.Fixture;
import org.jwaresoftware.mwf4j.assign.PutMethod;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.assign.SavebackVar;
import  org.jwaresoftware.mwf4j.assign.SavebackDiscard;
import  org.jwaresoftware.mwf4j.assign.SavebackMDC;
import  org.jwaresoftware.mwf4j.assign.SavebackProperty;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Action that either generates or obtains a piece of data that must be
 * assigned to or saved to one of the common MWf4J {@linkplain StoreType stores}.
 * Typically the data is returned by a closure of some kind like a Callable
 * or Future but just as typical is for the action itself to produce the
 * data. Subclasses must provide an {@linkplain AssignmentStatement
 * assignment statement} or something derived from that class to do the 
 * actual data reference assignment.
 * <p/>
 * To get this action to store the data, you <em>must</em> specify
 * a {@linkplain #setToKey to key} and optionally a {@linkplain StoreType 
 * store type}. If you do not specify a store type, data is stored to the 
 * controlling harness's variables; see the precanned MWf4J 
 * {@linkplain PutMethod saving strategies} for more information.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       SavebackVar
 * @see       SavebackProperty
 * @see       SavebackMDC
 **/

public abstract class SavebackAction<T> extends ActionSkeleton
{
    protected SavebackAction(String id)
    {
        super(id);
    }

    protected SavebackAction(String id, String toKey, StoreType toStoreType)
    {
        this(id);
        myToKey.set(toKey,toStoreType);
    }

    protected SavebackAction(String id, Reference toKey)
    {
        this(id);
        myToKey.copyFrom(toKey);
    }

    public void setToKey(String key)
    {
        myToKey.setName(key);
    }

    public void setToStoreType(StoreType type)
    {
        myToKey.setStoreType(type);
    }

    public void setTo(Reference toKey)
    {
        myToKey.set(toKey);
    }

    @SuppressWarnings("unchecked")
    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isTrue(statement instanceof AssignmentStatement<?>,"statement kindof assign");
        AssignmentStatement<T> assignment = (AssignmentStatement<T>)statement;
        assignment.setCheckDeclarables(isCheckDeclarables());
        assignment.setPutter(newPutMethod(myToKey));
        if (myToKey.isDefined()) {
            assignment.setToKey(myToKey.getName());
        }
    }


    /**
     * Factory method that figures out where to put results if anywhere;  
     * never returns <i>null</i>. Default algorithm assumes if no 'toKey'
     * was defined we can ignore or discard the data. Based on the MWf4J
     * standard saveback helpers.
     * @param to this action's store-to reference (non-null)
     * @return usable put method (non-null)
     **/
    protected PutMethod<T> newPutMethod(Reference to)
    {
        PutMethod<T> target = new SavebackDiscard<T>();
        if (to.isDefined()) {
            switch(to.getStoreType()) {
                case DATAMAP: {
                    target = SavebackVar.toMap();          break;
                }
                case OBJECT: {
                    target = SavebackVar.toObject();       break;
                }
                case THREAD: {
                    target = new SavebackMDC<T>();         break;
                }
                case PROPERTY: {
                    target = SavebackProperty.toHarness(); break;
                }
                case SYSTEM: {
                    target = SavebackProperty.toSystem();  break;
                }
                default: {
                    Feedback.ForBAL.warn("No store type defined for 'to' target \"{}\""+
                                         "; will be discarded",myToKey);
                }
            }
        }
        return target;
    }

    private Reference myToKey= new Reference();//OPTIONAL; works with default assignment statement mechanism
}


/* end-of-SavebackAction.java */
