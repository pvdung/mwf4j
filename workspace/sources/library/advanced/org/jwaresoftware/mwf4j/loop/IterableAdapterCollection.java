/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.AbstractCollection;
import  java.util.Collections;
import  java.util.Iterator;
import  java.util.List;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;
import  org.jwaresoftware.mwf4j.What;

/**
 * Adapter that wraps a pre-existing iterator or iterable object to a simple
 * {@linkplain AbstractCollection collection} interface.
 * <p/>
 * <b>Usage note #1:</b> this adapter builds a collection of items by first
 * iterating <em>in its entirety</em> the passed in iterator. Which means this
 * class is appropriate only for iterators backed by small bounded collections
 * of stuff.
 * <p/>
 * <b>Usage note #2:</b> this adapter is a <em>readonly</em> collection. You
 * cannot add elements to it. It's intended as a helper to an loop or iterating
 * action to extract the elements from a generic "Iterable" source.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    impl,extras,helper
 **/

public final class IterableAdapterCollection<E> extends AbstractCollection<E>
{
    public IterableAdapterCollection(Iterable<E> underlying)
    {
        Validate.notNull(underlying,What.CALLBACK);
        mySource = underlying;
    }

    public IterableAdapterCollection(final Iterator<E> underlying)
    {
        Validate.notNull(underlying,What.CALLBACK);
        mySource = new Iterable<E>() {
            public Iterator<E> iterator() {
                return underlying;
            }
        };
        loadThis();
    }

    public Iterator<E> iterator()
    {
        loadThis();
        return myData.iterator();
    }

    public int size()
    {
        loadThis();
        return myData.size();
    }

    private synchronized void loadThis()
    {
        if (myData==null) {
            List<E> captured = LocalSystem.newList(24);
            for (E next:mySource) {
                if (next!=null) 
                    captured.add(next);
            }
            myData = Collections.unmodifiableList(captured);
            mySource = null;
        }
    }

    private Iterable<E> mySource;
    private List<E> myData;
}


/* end-of-IterableAdapterCollection.java */
