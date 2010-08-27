/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Iterator;
import  java.util.List;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;

/**
 * Control flow statement that iterates through statements returned
 * by an ordered list of supplied actions. The action statements
 * are run in order and then this statement's continuation statement
 * is returned (to run).
 * <p/>
 * Works as an {@linkplain EmptyStatement empty statement} if no
 * actions linked. 
 * <p/>
 * This statement <em>actually runs</em> the next up component
 * statement directly (instead of passing back). Reason for this is
 * twofold: limit "bounces" between statement calls of sequence
 * versus the contained statement (the sequence-statement is really
 * an immediate proxy for contained statement), and more importantly, 
 * to support variations like tryeach and protected where the 
 * execution of the contained statement is being managed.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       SequenceAction
 **/

public class SequenceStatement extends BALStatement implements Unwindable, Resettable
{
    static final Iterator<ControlFlowStatement> NONE = Empties.newIterator();

    public SequenceStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myUnwindSupport = new ReentrantSupport(this,true,this);
    }

    public void setMembers(List<Action> actions)
    {
        Validate.notNull(actions,What.ACTIONS);
        myMembers = new StatementIterator(this,actions);
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next;
        if (myMembers.hasNext()) {
            myUnwindSupport.loop(harness);
            myCount++;
            next = harness.runParticipant(myMembers.next());//NB:continuation|get next...
        } else {
            next = next();
            myUnwindSupport.finished(harness);
        }
        return next;
    }

    protected final Iterator<ControlFlowStatement> getMembers()
    {
        return myMembers;
    }

    private void resetThis()
    {
        myMembers = NONE;
        myCount = 0;
        myUnwindSupport.reset(this);
    }

    public void reconfigure()
    {
        reset();
        super.reconfigure();
        verifyReady();
    }

    public void unwind(Harness harness)
    {
        resetThis();
    }

    public void reset()
    {
        resetThis();
    }

    protected StringBuilder addToString(StringBuilder sb) 
    {
        return super.addToString(sb).append("|called=").append(myCount);
    }



    private int myCount;
    private Iterator<ControlFlowStatement> myMembers = NONE;
    ReentrantSupport myUnwindSupport;//NB:visible to BAL subclasses ONLY!
}


/* end-of-SequenceStatement.java */
