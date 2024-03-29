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
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;
import  org.jwaresoftware.mwf4j.scope.CursorNames;
import  org.jwaresoftware.mwf4j.scope.NumberRewindCursor;
import  org.jwaresoftware.mwf4j.scope.RewindCursor;
import  org.jwaresoftware.mwf4j.scope.Rewindable;

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
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,infra
 * @see       SequenceAction
 **/

public class SequenceStatement extends BALStatement implements Unwindable, Resettable, Rewindable
{
    private static final List<Action> NO_MEMBERS= Empties.newList();
    private static final Iterator<Action> NO_FEED = Empties.newIterator();
    

    public SequenceStatement(ControlFlowStatement next)
    {
        super(next);
        myUnwindSupport = new ReentrantSupport(this,true,this);
    }

    public void setMembers(List<Action> actions)
    {
        Validate.notNull(actions,What.ACTIONS);
        myMembers = actions;
        myMemberFeed = new StatementIterator(actions);
    }

    public void setCursorFormat(String template)
    {
        myCursorFormat = template;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next;
        if (myMemberFeed.hasNext()) {
            Action nextUp = myMemberFeed.next();
            myUnwindSupport.loop(harness);
            myUnwindSupport.addRewindpoint(newRewindpoint(myCount));
            next = runMember(myCount++,harness,nextUp.buildStatement(this,harness.staticView()));
        } else {
            next = finishThis(harness,next());
        }
        return next;
    }

    ControlFlowStatement finishThis(Harness harness, ControlFlowStatement next)
    {
        myUnwindSupport.finished(harness);
        return next;
    }

    void resetThis()
    {
        myMembers = NO_MEMBERS;
        myMemberFeed = NO_FEED;
        myCount = 0;
        myUnwindSupport.reset(this);
    }

    ControlFlowStatement runMember(int index, Harness harness, ControlFlowStatement member)
    {
        return harness.runParticipant(member);
    }

    public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        reset();
        super.reconfigure(environ,overrides);
    }

    public void unwind(Harness harness)
    {
        breadcrumbs().doUnwind(harness);
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

    public ControlFlowStatement rewind(RewindCursor to, Harness harness)
    {
        Validate.isA(to,NumberRewindCursor.class,What.CURSOR);
        int index = ((NumberRewindCursor)to).getInt();
        Validate.isTrue(0<=index && index<myMembers.size(), "valid rewind index["+index+"]");
        rewindThis(index,harness);
        return this;
    }

    void rewindThis(int index, Harness harness)
    {
        myCount = index;
        myMemberFeed = new StatementIterator(myMembers.subList(index, myMembers.size()));
    }

    protected NumberRewindCursor newRewindpoint(int index)
    {
        String aid = getWhatId();//NB: make it something determinate for testability!
        return new NumberRewindCursor(this,myCount,CursorNames.nameFrom(myCursorFormat,aid,index));
    }


    private int myCount;
    private List<Action> myMembers = NO_MEMBERS;
    private Iterator<Action> myMemberFeed = NO_FEED;
    ReentrantSupport myUnwindSupport;//NB:visible to BAL subclasses ONLY!
    private String myCursorFormat;//OPTIONAL
}


/* end-of-SequenceStatement.java */
