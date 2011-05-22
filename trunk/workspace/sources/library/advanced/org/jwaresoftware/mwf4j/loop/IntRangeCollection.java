/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.AbstractCollection;
import  java.util.Iterator;
import  java.util.NoSuchElementException;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Numbers;

import  org.jwaresoftware.mwf4j.What;

/**
 * Collection adapter for a number sequence. You specify the start,
 * stop, and delta (to next 'element').
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    impl,extras,helper
 * @see       IntRangeToCollectionCallback
 **/

public final class IntRangeCollection extends AbstractCollection<Integer>
{
    /**
     * Initializes a new collection adapter for given number series. Will
     * verify the parameters work together for a valid empty, increasing,  
     * or decreasing series.
     * @param start first number in series
     * @param end last-delta number in series
     * @param delta series increment
     * @throws IllegalArgumentException if unable to build valid series with inputs
     **/
    public IntRangeCollection(int start, int end, int delta)
    {
        verifySeries(start,end,delta);
        this.myStart = start;
        this.myEnd   = end;
        this.myDelta = delta;
        this.isDecreasing = myStart>myEnd;
    }

    /**
     * Initialize a new collection adapter using a shorthand string 
     * description of the number series. String of form: 
     * <i>"&lt;start&gt;,&lt;end&gt;[,&lt;delta&gt;]"</i>; example:
     * "0,10,1" or "0,-10,-1". If you omit the third parameter, this
     * collection assumes a delta of positive one.
     * @param shorthand input string (non-blank, wellformed)
     **/
    public IntRangeCollection(String shorthand)
    {
        Validate.notBlank(shorthand,What.CRITERIA);
        String[] parts = Strings.split(shorthand,",");
        Validate.isTrue(parts.length==2||parts.length==3,"wellformed 'in' shorthand");
        int delta=1;
        myStart = Numbers.toInteger(parts[0].trim());
        myEnd = Numbers.toInteger(parts[1].trim());
        if (parts.length==3) delta = Numbers.toInteger(parts[2].trim());
        myDelta = delta;
        this.isDecreasing = myStart>myEnd;
        verifySeries(myStart,myEnd,myDelta);
    }

    public Iterator<Integer> iterator()
    {
        return new Iterator<Integer>() {
            private int _index;
            {
                _index= myStart;
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
            public boolean hasNext() {
                return isDecreasing ? (_index>myEnd) : (_index<myEnd);
            }
            public Integer next()
            {
                if (this.hasNext()) {
                    Integer output = Integer.valueOf(_index);
                    _index += myDelta;
                    return output;
                }
                throw new NoSuchElementException();
            }
        };
    }

    public int size()
    {
        int delta = Math.abs(myDelta);
        int round = delta-1;
        int n;
        if (isDecreasing) {
            n = (Math.abs(myStart-myEnd)+round)/delta;
        } else {
            n = ((myEnd-myStart+round)/delta);
        }
        return n;
    }

    private static void verifySeries(int start, int end, int delta)
    {
        if (start!=end) {
            Validate.isFalse(delta==0,"delta is 0 (infinite loop)");
            if (end>start)
                Validate.isTrue(delta>0,"valid increasing series");
            else
                Validate.isTrue(delta<0,"valid decreasing series");
        }
    }

    private final int myStart, myEnd, myDelta;
    private final boolean isDecreasing;
}

/* end-of-IntRangeCollection.java */

