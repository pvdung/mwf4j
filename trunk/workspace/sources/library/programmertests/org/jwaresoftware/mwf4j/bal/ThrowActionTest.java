/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Deque;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Test suite for {@linkplain ThrowAction} and its associated statement.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class ThrowActionTest extends ActionTestSkeleton
{
    static final class NotRuntimeException extends Exception {
        NotRuntimeException() {
            super("not a runtime exception");
        }
        NotRuntimeException(String message) {
            super(message);
        }
    }

    private void failIfHere()
    {
        fail("Should not get past a throw action's activity!");
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testThrowNonRuntimeException_1_0_0() 
    {
        Exception test = new NotRuntimeException();

        ThrowAction out = new ThrowAction();
        out.setCause(test);
        try {
            newHARNESS(out).run();
        } catch(MWf4JException wrap) {
            assertEquals(wrap.getCause().getClass(),NotRuntimeException.class,"classof barfage");
            throw wrap;
        }
        failIfHere();
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testThrowDefaultException_1_0_0()
    {
        ThrowAction out = new ThrowAction();
        newHARNESS(out).run();
        failIfHere();
    }

    public void testThrowStatementBaseline_1_0_0()
    {
        ThrowStatement out = new ThrowStatement(new ThrowAction());
        assertEquals(out.getLength(),1,"initial length");
        assertNull(out.nextThrown(),"nextThrown");
        assertNull(out.getCause(),"cause");
        Exception x = new RuntimeException("Green dropped the d*mn ball!?!?!?!");
        out.setCause(x);
        System.out.println("ThrowStatement: "+out);
        assertSame(out.getCause(),x,"cause");
        assertTrue(out.next().isTerminal(),"next() always non-null and isTerminal=true");
    }

    public void testChainedThrowStatements_1_0_0()
    {
        Exception e1st = new RuntimeException("EERROOOOR: 1st convulsion");
        ThrowStatement t1st = new ThrowStatement(new ThrowAction("1st"),e1st);
        System.out.println("T1st(unchained): "+t1st);
        
        Exception e2nd = new NotRuntimeException("EEEEIIIKK: 2nd convulsion");
        ThrowStatement t2nd = new ThrowStatement(new ThrowAction("2nd"),e2nd);
        assertEquals(t2nd.getLength(),1,"2nd's initial length");
        assertNull(t2nd.nextThrown(),"2nd's nextThrown");
        System.out.println("T2nd(unchained): "+t2nd);

        t2nd.setNextThrown(t1st);
        System.out.println("T2nd(chained): "+t2nd);
        assertEquals(t2nd.getLength(),2,"2nd's length after chained");
        assertSame(t2nd.nextThrown(),t1st,"nextThrown");
        assertNull(t1st.nextThrown(),"1st's nextThrown");

        RunFailedException summary = new RunFailedException(t2nd);
        System.out.println("T2nd(chained-post-switch): "+t2nd);
        assertSame(t1st.getCause(),summary,"t1st.cause[after switch]");
        assertSame(t2nd.getCause(),summary,"t2nd.cause[after switch]");

        //Expect fifo ordering of summary stack
        assertSame(summary.getCause(),e1st,"root error = t1st");
        Deque<Throwable> exceptions = summary.copyOfCauses();
        assertEquals(exceptions.size(),2,"Num.causes");
        assertSame(exceptions.pop(),e1st,"1st exception");
        assertSame(exceptions.pop(),e2nd,"2nd exception");
    }

    @Test(expectedExceptions= {NumberFormatException.class})
    public void testCustomExceptionByName_1_0_0()
    {
        ThrowAction out = new ThrowAction("tnf");
        out.setCause(NumberFormatException.class.getName());
        assertNull(out.getCause());
        newHARNESS(out).run();
        failIfHere();
    }

    public void testCustomExceptionByNameWithMessage_1_0_0()
    {
        ThrowAction out = new ThrowAction("trip");
        out.setCause(UnsupportedOperationException.class.getName());
        out.setAnnoucement("I've fallen");
        try {
            newHARNESS(out).run();
        } catch(UnsupportedOperationException Xpected) {
            assertEquals(Xpected.getMessage(),"I've fallen","custom message");
            return;
        }
        failIfHere();
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testWrapUninstantiatedCustomException_1_0_0()
    {
        ThrowAction out= new ThrowAction();
        out.setCause(TypeNotPresentException.class.getName()); //no void ctor!
        try {
            newHARNESS(out).run();
        } catch(MWf4JException Xpected) {
            Throwable Xpected2 = Xpected.getCause();
            assertTrue(Xpected2 instanceof InstantiationException,"is bad ctor Xception");
            throw Xpected;
        }
        failIfHere();
    }

    public void testDirectExceptionOverByName_1_0_0()
    {
        ThrowAction out =  new ThrowAction(new NumberFormatException("JACKPOT"));
        out.setCause(TypeNotPresentException.class.getName());//Should NOT be triggered
        try {
            newHARNESS(out).run();
        } catch(NumberFormatException Xpected) {
            assertEquals(Xpected.getMessage(),"JACKPOT","exception message");
            return;
        }
        failIfHere();
    }
}


/* end-of-ThrowActionTest.java */
