/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  java.util.concurrent.locks.Condition;
import  java.util.concurrent.locks.Lock;
import  java.util.concurrent.locks.ReentrantLock;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.MWf4JWrapException;

/**
 * Specialized harness that sits and receives continuations indefinitely.
 * Typically you run a forever harness from its own thread-of-execution and
 * feed it via a listen statement on a different harness. You signal an 
 * "end" to the forever harness using an adjustment or interrupt signal 
 * against its thread.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (same as superclass and single for configuration)
 * @.group    infra,impl,helper
 **/

public final class ForeverHarness extends SpawnedHarnessSkeleton
{
    public ForeverHarness(Harness master, ControlFlowStatement first)
    {
        super(master,first);
    }

    public void setBackLogDepth(int maxDepth)
    {
        Validate.isFalse(maxDepth<=0,"backlog.size<=0");
        myMaxContinues = maxDepth;
    }

    public String typeCN()
    {
        return "forever";
    }

    public void addContinuation(ControlFlowStatement participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        myLock.lock();
        try {
            while (numContinues >= myMaxContinues)
                notBackLogged.await();
            myContinuations.add(participant);
            numContinues++;
            notIdle.signal();
        } catch (InterruptedException iruptedX) {
            throw new MWf4JWrapException(iruptedX);
        } finally {
            myLock.unlock();
        }
    }

    void queueContinuations(ControlFlowStatement directed)
    {
        Validate.notNull(directed,What.CONTINUATION);
        myQueue.add(directed);
        myLock.lock();
        try {
            if (!directed.isTerminal())
                numContinues++;
            while (numContinues == 0)
                notIdle.await();
            for (ControlFlowStatement derived : myContinuations) {
                myQueue.add(derived);
            }
            myContinuations.clear();
            numContinues = 0;
            notBackLogged.signal();
        } catch (InterruptedException iruptedX) {
            throw new MWf4JWrapException(iruptedX);
        } finally {
            myLock.unlock();
        }
    }

    public void applyAdjustment(Adjustment adjustment)
    {
        super.applyAdjustment(adjustment);
        myLock.lock();
        try {
            numContinues++;
            notIdle.signal();
        } finally {
            myLock.unlock();
        }
    }

    void resetThis()
    {
        numContinues=0;
        super.resetThis();
    }


    private int numContinues, myMaxContinues=Integer.MAX_VALUE;
    private final Lock myLock = new ReentrantLock();
    private final Condition notIdle = myLock.newCondition();
    private final Condition notBackLogged = myLock.newCondition();
}


/* end-of-ForeverHarness.java */
