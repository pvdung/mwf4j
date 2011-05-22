/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.Iterator;
import  java.util.NoSuchElementException;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

/**
 * Test suite for {@linkplain IntRangeCollection} utility class.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","builders","advanced"})
public final class IntRangeCollectionTest
{
    public void testIncreasingBy1_StartPosEndPositive()
    {
        IntRangeCollection out = new IntRangeCollection(5, 10, 1);
        LocalSystem.show(out);//[5, 6, 7, 8, 9]
        assertEquals(out.size(),5,"size");
        int x=5;
        for(Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x++;
        }
    }

    public void testIncreasingBy1_StartZroEndPositive()
    {
        IntRangeCollection out = new IntRangeCollection(0, 10, 1);
        LocalSystem.show(out);//[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
        assertEquals(out.size(),10,"size");
        int x=0;
        for(Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x++;
        }
    }

    public void testIncreasingBy1_StartNegEndPositive()
    {
        IntRangeCollection out = new IntRangeCollection(-10,0,1);
        LocalSystem.show(out);//[-10, -9, -8, -7, -6, -5, -4, -3, -2, -1]
        assertEquals(out.size(),10,"size");
        int x= -10;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x++;
        }
    }

    public void testIncreasingByN_StartPosEndPositive()
    {
        IntRangeCollection out = new IntRangeCollection(1,10,2);
        LocalSystem.show(out);//[1, 3, 5, 7, 9]
        assertEquals(out.size(),5,"size");
        int x= 1;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x += 2;
        }
    }

    public void testIncreasingByN_StartNegEndPositive()
    {
        IntRangeCollection out = new IntRangeCollection(-1,10,2);
        LocalSystem.show(out);//[-1, 1, 3, 5, 7, 9]
        assertEquals(out.size(),6,"size");
        int x= -1;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x += 2;
        }
    }


    public void testIncreasingByN_StartNegEndNegative()
    {
        IntRangeCollection out = new IntRangeCollection(-23,-10,3);
        LocalSystem.show(out);//[-23, -20, -17, -14, -11]
        assertEquals(out.size(),5,"size");
        int x= -23;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x += 3;
        }
    }

    public void testDecreasingBy1_StartZroEndNegative()
    {
        IntRangeCollection out = new IntRangeCollection(0,-10,-1);
        LocalSystem.show(out);//[0, -1, -2, -3, -4, -5, -6, -7, -8, -9]
        assertEquals(out.size(),10,"size");
        int x= 0;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x--;
        }
    }

    public void testDecreasingByN_StartNegEndNegative()
    {
        IntRangeCollection out = new IntRangeCollection(-21,-42,-3);
        LocalSystem.show(out);//[-21, -24, -27, -30, -33, -36, -39]
        assertEquals(out.size(),7,"size");
        int x= -21;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x += -3;
        }
    }

    public void testDecreasingByN_StartNegEndNegativeEdgeCase()
    {
        IntRangeCollection out = new IntRangeCollection(-5,-13,-2);
        LocalSystem.show(out);//[-5, -7, -9, -11]
        assertEquals(out.size(),4,"size");
        int x= -5;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x += -2;
        }
    }

    public void testDecreasingByN_StartPosEndNegative()
    {
        IntRangeCollection out = new IntRangeCollection(3,-12,-2);
        LocalSystem.show(out);//[3, 1, -1, -3, -5, -7, -9, -11]
        assertEquals(out.size(),8,"size");
        int x= 3;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x += -2;
        }
    }

    public void testEmptySeries()
    {
        IntRangeCollection out = new IntRangeCollection(0,0,1);
        LocalSystem.show(out);//[]
        assertEquals(out.size(),0,"size");
        assertTrue(out.isEmpty(),"isEmpty");
        for (Integer i:out) {
            fail("Should not be able to iterate empty set?"+i);
        }
    }

    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailInfiniteLoop_NeverIncreasing()
    {
        new IntRangeCollection(1,10,-1);
        fail("Should not be able to create never increasing loop");
    }


    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailInfiniteLoop_NeverDecreasing()
    {
        new IntRangeCollection(10,0,2);
        fail("Should not be able to create never decreasing loop");
    }

    @Test(expectedExceptions={IllegalArgumentException.class})
    public void testFailInfiniteLoop_NoDelta()
    {
        new IntRangeCollection(1,10,0);//no delta
        fail("Should not be able to create loop with zero delta");
    }

    @Test(expectedExceptions={NoSuchElementException.class})
    public void testFailNoSuchElement()
    {
        IntRangeCollection out = new IntRangeCollection(0,10,1);
        Iterator<Integer> itr = out.iterator();
        int n= out.size();
        int i=0;
        try {
            while (itr.hasNext()) { itr.next(); i++; }
            itr.next();
        } catch(NoSuchElementException Xpected) {
            assertEquals(i,n,"right exception point");
            throw Xpected;
        }
    }

    @Test(expectedExceptions={UnsupportedOperationException.class}, dependsOnMethods={"testFailNoSuchElement"})
    public void testFailIfTryRemoveWithIterator()
    {
        IntRangeCollection out = new IntRangeCollection(0,10,1);
        Iterator<Integer> itr = out.iterator();
        if (itr.hasNext()) {
            itr.remove();
        } else {
            fail("Should not be empty!");
        }
    }

    @Test(dependsOnMethods={"testIncreasingBy1_StartZroEndPositive"})
    public void testNewByStringShortcut_StartZroEndPositive()
    {
        IntRangeCollection out = new IntRangeCollection("0,10");
        LocalSystem.show(out);//[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
        assertEquals(out.size(),10,"size");
        int x=0;
        for(Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x++;
        }
    }

    @Test(dependsOnMethods={"testDecreasingBy1_StartZroEndNegative"})
    public void testNewByStringShortcut_StartZroEndNegative_SpacesIgnored()
    {
        IntRangeCollection out = new IntRangeCollection(" 0,  -10,-1 ");//make sure spaces ignored!
        LocalSystem.show(out);//[0, -1, -2, -3, -4, -5, -6, -7, -8, -9]
        assertEquals(out.size(),10,"size");
        int x= 0;
        for (Integer i:out) {
            assertEquals(i,Integer.valueOf(x),"i");
            x--;
        }
    }

}


/* end-of-IntRangeCollectionTest.java */
