/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.Collection;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.helpers.Declarables;

/**
 * Adapter that converts a simple number series into a collection of Integer
 * objects. Use as a get-method for a looping statement. Safe for concurrent
 * use from multiple threads.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after frozen)
 * @.group    impl,extras,helper
 * @see       IntRangeCollection
 **/

public final class IntRangeToCollectionCallback extends CollectionCallbackSkeleton<Collection<Integer>>
{
    public IntRangeToCollectionCallback(int start, int end, int incr)
    {
        myFirst = start;
        myEnd = end;
        myIncr = incr;
        myShorthand = null;
    }

    public IntRangeToCollectionCallback(String shorthand)
    {
        myShorthand = shorthand;
        myFirst = 0;
        myEnd = 1;
        myIncr = -1;//NB:triggers error if used!
    }

    protected Collection<Integer> callInner()
    {
        return (myShorthand==null) 
            ? new IntRangeCollection(myFirst,myEnd,myIncr)
            : new IntRangeCollection(myShorthand);//NB: new everytime!
    }

    public void freeze(Fixture environ)
    {
        myShorthand = Declarables.freeze(environ,myShorthand);
    }

    private String myShorthand;
    private final int myFirst, myEnd, myIncr;
}


/* end-of-IntRangeToCollectionCallback.java */
