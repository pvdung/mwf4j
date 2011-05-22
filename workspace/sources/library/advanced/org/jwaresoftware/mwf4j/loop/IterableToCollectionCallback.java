/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.Collection;
import  java.util.Iterator;
import  java.util.Map;
import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.messages.catalog.OJGMessages;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.GivebackMDC;
import  org.jwaresoftware.mwf4j.assign.GivebackVar;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.helpers.ClosureException;

/**
 * Adapter that converts a reference to an Iterable, an Iterator, or a Map, to
 * a collection of objects. Use as a get-method for a looping statement. Safe
 * for concurrent use from multiple threads. 
 * <p/>
 * <b>Usage note #1:</b> for Maps, this callback will return a collection
 * adapter around the map's <em>entries</em> as in {@code Map.Entry}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras,helper
 **/

public final class IterableToCollectionCallback<T> extends CollectionCallbackSkeleton<Collection<T>>
{
    public IterableToCollectionCallback(String refId)
    {
        Validate.notBlank(refId,What.REFERENCE);
        myFrom.setName(refId);
    }

    public IterableToCollectionCallback(Reference ref)
    {
        Validate.notNull(ref,What.REFERENCE);
        myFrom.copyFrom(ref);
    }

    protected Collection<T> callInner()
    {
        Object from = getFrom();
        if (Iterable.class.isInstance(from)) {
            return new IterableAdapterCollection<T>(toIterable(from));
        }
        if (Iterator.class.isInstance(from)) {
            return new IterableAdapterCollection<T>(toIterator(from));
        }
        if (Map.class.isInstance(from)) {
            return new IterableAdapterCollection<T>(toIterable(Map.class.cast(from)));
        }
        Throwable cause = new ClassCastException(OJGMessages.ConversionFailed(What.idFor(from),"Iterable.type"));
        throw new ClosureException(cause);
    }

    @SuppressWarnings("unchecked")
    private Iterable<T> toIterable(Object from)
    {
        Iterable<T> any = (Iterable<T>)from;
        return any;
    }

    @SuppressWarnings("unchecked")
    private Iterator<T> toIterator(Object from)
    {
        Iterator<T> any = (Iterator<T>)from;
        return any;
    }

    @SuppressWarnings("unchecked")
    private Iterable<T> toIterable(Map<?,?> from)
    {
        Iterable<T> any = (Iterable<T>)from.entrySet();
        return any;
    }

    private Object getFrom()
    {
        Callable<Object> getmethod=null;
        switch(myFrom.getStoreType()) {
            case DATAMAP: {
                assertVarExists(myFrom.getId());
                getmethod = GivebackVar.fromGet(myFrom.getId(),Object.class);
                break;
            }
            case OBJECT: {
                getmethod = GivebackVar.fromEval(myFrom.getId(),Object.class);
                break;
            }
            case THREAD: {
                getmethod = new GivebackMDC<Object>(myFrom.getId(),Object.class);
                break;
            }
            default: {
                throw new ClosureException(OJGMessages.IncompatibleTypeFound(myFrom.toString()));
            }
        }
        try {
            return getmethod.call();
        } catch(RuntimeException asisX) {
            throw asisX;
        } catch(Exception othrX) {
            throw new ClosureException(othrX);
        }
    }

    public void freeze(Fixture environ)
    {
        myFrom.freeze(environ);
    }

    public Object clone()
    {
        IterableToCollectionCallback<?> other = IterableToCollectionCallback.class.cast(super.clone());
        other.myFrom = (Reference)myFrom.clone();
        return other;
    }

    private Reference myFrom = new Reference();
}


/* end-of-IterableToCollectionCallback.java */
