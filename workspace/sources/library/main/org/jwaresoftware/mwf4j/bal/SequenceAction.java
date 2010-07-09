/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.List;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Protector;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Implementation for an ordered sequence of sibling actions. User can
 * setup for a single-use sequence (underlying action list is passed
 * directly to control statement for use), or for a long-lived
 * multi-use sequence (each statement is created against a snapshot
 * of the current list of actions). Default use is for a single-use case.
 * <p/>
 * There is some built-in error handling for this sequence. To force each
 * sub-action to be run even if one or more of them throw <em>any</em>
 * type of runtime exception, turn on the {@linkplain #setTryEach(boolean) tryeach}
 * flag. To ignore any generated runtime exceptions, turn off the 
 * {@linkplain #setHaltIfError(boolean) haltiferror} flag (note that this
 * flag is interpreted <em>after</em> the tryeach option, so the sequence
 * would halt (or not) <em>after</em> all sub-actions have been run for
 * instance). In both of these cases (tryeach=yes and/or haltiferror=no), 
 * you can also tell the sequence to notify the incoming harness problem 
 * handler by turning the {@linkplain #setQuiet(boolean) quiet} option off.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after configured for makeStatement)
 * @.group    infra,impl
 **/

public class SequenceAction extends ActionSkeleton implements Sequence, Protector
{
    private static final List<Action> NONE= Empties.newList();


    public enum Mode {
        SINGLE, MULTIPLE;
        public static Mode findOrNull(String name) {
            Mode m = null;
            if (name!=null) {
                try { m = Mode.valueOf(name.toUpperCase()); }
                catch(IllegalArgumentException unknown) {/*burp*/}
            }
            return m;
        }
    }

    public SequenceAction()
    {
        this("sequence");
    }

    public SequenceAction(String id)
    {
        super(id);
    }

    public void setMode(Mode mode)
    {
        Validate.notNull(mode,What.CRITERIA);
        myUseMode = mode;
    }

    public Sequence add(Action action)
    {
        Validate.notNull(action,What.ACTION);
        if (myActions==NONE) {
            myActions = LocalSystem.newList(8);
        }
        myActions.add(action);
        return this;
    }

    public int size()
    {
        return myActions.size();
    }

    public boolean isEmpty()
    {
        return myActions.isEmpty();
    }

    public final void setTryEach(boolean flag)
    {
        myTryEachFlag = flag;
    }

    public final void setHaltIfError(boolean flag)
    {
        myHaltIfErrorFlag = flag;
    }

    public void setQuiet(boolean flag)
    {
        myQuietFlag = flag;
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myHaltContinuationFlag = flag;
    }

    public void configure(ControlFlowStatement statement)
    {
        Validate.isTrue(statement instanceof SequenceStatement,"statement kindof sequence");
        ((SequenceStatement)statement).setMembers(getActions());
        if (statement instanceof Protector) {
            Protector control = (Protector)statement;
            control.setHaltIfError(myHaltIfErrorFlag);
            control.setQuiet(myQuietFlag);
            control.setUseHaltContinuation(myHaltContinuationFlag);
        }
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        verifyReady();
        SequenceStatement statement;
        if (myTryEachFlag) {
            statement = new TryEachSequenceStatement(this,next);
        } else if (!myHaltIfErrorFlag) {
            statement = new ProtectedSequenceStatement(this,next);
        } else {
            statement = new SequenceStatement(this,next);
        }        
        return finish(statement);
    }

    private List<Action> getActions()
    {
        List<Action> actions = myActions;
        if (Mode.MULTIPLE.equals(myUseMode)) {
            actions = LocalSystem.newListCopy(myActions);
        }
        return actions;
    }


    private List<Action> myActions = NONE;
    private Mode myUseMode = Mode.SINGLE;
    private boolean myHaltIfErrorFlag = true;
    private boolean myTryEachFlag = false;
    private boolean myQuietFlag = true;
    private boolean myHaltContinuationFlag = BAL.getUseHaltContinuationsFlag();
}


/* end-of-SequenceAction.java */
