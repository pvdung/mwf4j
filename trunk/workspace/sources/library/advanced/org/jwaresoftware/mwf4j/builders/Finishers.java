/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  java.util.ArrayDeque;
import  java.util.Deque;
import  java.util.concurrent.LinkedBlockingDeque;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.What;

/**
 * Tracks a stack of builder element {@linkplain Finisher finishers}. Typically
 * a single root builder will control a single stack of finishers. Each inner
 * (or otherwise dependent) builder, will manipulate this single stack even
 * if they are independent builders from the root. Note that this object IS 
 * NOT safe for concurrent access/modification from multiple threads. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    impl,extras,helper
 * @see       BALBuilder
 * @see       Finisher
 **/

public final class Finishers
{
    public Finishers()
    {
        myFinishers = new ArrayDeque<Finisher>();
    }

    public Finishers(int capacity)
    {
        myFinishers = new LinkedBlockingDeque<Finisher>(capacity);
    }

    public void push(Finisher finisher)
    {
        Validate.notNull(finisher, What.FINISHER);
        myFinishers.push(finisher);
    }

    public Finisher pop()
    {
        return myFinishers.pop();
    }

    public Finisher peek()
    {
        return myFinishers.peek();
    }

    public Finisher topOrFail()
    {
        Finisher top = peek();
        Validate.notNull(top,"finisher on top");
        return top;
    }

    public <T> T getUnderConstruction(Class<T> ofType) 
    {
        return topOrFail().getUnderConstruction(ofType);
    }

    public boolean isEmpty()
    {
        return myFinishers.isEmpty();
    }

    public boolean isFlat()
    {
        return size()==1 && (peek() instanceof RootFinisher);
    }

    public int size()
    {
        return myFinishers.size();
    }

    private boolean _onTop(Finisher finisher)
    {
        return finisher!=null && myFinishers.peek()==finisher;
    }

    public boolean onTop(Finisher finisher)
    {
        return _onTop(finisher);
    }

    public void popIfTop(Finisher finisher) throws IllegalStateException
    {
        Validate.stateIsTrue(_onTop(finisher),"finisher on top");
        myFinishers.pop();
    }

    void clear()
    {
        myFinishers.clear();
    }

    private Deque<Finisher> myFinishers;
}


/* end-of-Finishers.java */
