/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.atomic.AtomicBoolean;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.LongLivedCondition;
import  org.jwaresoftware.mwf4j.What;

/**
 * Tweak to the standard {@linkplain WhileStatement while statement} that 
 * converts it to a <i>do while</i> which runs body at least once (the test 
 * is done 'at end' from user's perspective). Works only because of knowledge  
 * of how theWhileStatement is implemented. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (latch condition is guarded)
 * @.group    infra,impl
 **/

public final class DoWhileStatement extends WhileStatement
{
    /**
     * Condition proxy that will allow the first call through always.
     * After that the 'real' condition is evaluated. Make sure we
     * function correctly in multi-threaded use of underlying condition.
     **/
    static class TrueLatchCondition implements LongLivedCondition 
    {
        TrueLatchCondition(Condition test) {
            myImpl = test;
        }
        public void start() {
            BALHelper.activate(myImpl);
        }
        public void start(long timezero) {
            BALHelper.activate(myImpl,timezero);
        }
        public boolean evaluate(Harness harness) {
            boolean result;
            if (myIgnoreFlag.compareAndSet(true,false)) {
                result= true;
            } else {
                result= myImpl.evaluate(harness);
            }
            return result;
        }
        private final Condition myImpl;
        private AtomicBoolean myIgnoreFlag=new AtomicBoolean(true);//LATCH 
    }

    public DoWhileStatement(ControlFlowStatement next)
    {
        super(next);
    }

    public void setTest(Condition test)
    {
        Validate.notNull(test,What.CRITERIA);
        super.setTest(new TrueLatchCondition(test));
    }
}


/* end-of-DoWhileStatement.java */
