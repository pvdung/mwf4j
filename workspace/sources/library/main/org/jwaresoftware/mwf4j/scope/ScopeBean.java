/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  java.util.IdentityHashMap;
import  java.util.List;
import  java.util.Map;
import  java.util.Set;
import  java.util.SortedSet;
import  java.util.TreeSet;
import  java.util.concurrent.atomic.AtomicBoolean;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.StatementDependentSkeleton;

/**
 * Simple POJO implementation of the {@linkplain Scope} interface. Note
 * that a scope's owning statement must be non-NULL but its name is optional
 * (uses the empty string by default).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    infra,impl,helper
 **/

public class ScopeBean extends StatementDependentSkeleton implements Scope
{
    public ScopeBean(ControlFlowStatement owner)
    {
        super(owner);
        Validate.notNull(owner,What.STATEMENT);
    }

    public ScopeBean(ControlFlowStatement owner, String name)
    {
        this(owner);
        if (name!=null) {
            myName = name;
        }
    }

    public String getName() 
    {
        return myName;
    }

    public final void addUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myUnwinds) {
            myUnwinds.put(participant,Boolean.TRUE);
        }
    }

    public final void removeUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myUnwinds) {
            myUnwinds.remove(participant);
        }
    }

    public void doUnwind(Harness harness)
    {
        Validate.stateIsTrue(myEnabledFlag.get()==true,"enabled");
        List<Unwindable> unwinds;
        synchronized(myUnwinds) {
            unwinds = LocalSystem.newList(myUnwinds.keySet());
            myUnwinds.clear();
        }
        for (Unwindable next:unwinds) {//Do EACH (ignore barfage)
            try {
                next.unwind(harness);
            } catch(RuntimeException rtX) {
                String what = Throwables.getTypedMessage(rtX);
                harness.getIssueHandler().problemOccured(what,Effect.IGNORE,rtX);
            }
        }
    }

    public void doEnter(Harness harness)
    {
        myEnabledFlag.set(true);
    }

    public void doLeave(Harness harness)
    {
        myEnabledFlag.set(false);
        synchronized(myRewindCursors) {
            myRewindCursors.clear();
        }
        synchronized(myUnwinds) {
            myUnwinds.clear();
        }
    }

    public String toString()
    {
        String myname = getName();
        return Strings.isEmpty(myname) ? What.subidFor(this,"Bean") : myname;
    }

    public void addRewindpoint(RewindCursor cursor)
    {
        Validate.notNull(cursor,What.CURSOR);
        Validate.isTrue(cursor.getOwner()==getOwner(), "scoped cursor");
        Validate.stateIsTrue(myEnabledFlag.get()==true,"enabled");
        synchronized(myRewindCursors) {
            Validate.isTrue(myRewindCursors.add(cursor),"cursor not present");
        }
    }

    public Set<Rewindpoint> copyOfRewindpoints()
    {
        Validate.stateIsTrue(myEnabledFlag.get()==true,"enabled");
        Set<Rewindpoint> set= LocalSystem.newSet();
        synchronized(myRewindCursors) {
            for (RewindCursor cursor:myRewindCursors) {
                set.add(cursor.getReadonlyView());
            }
        }
        return set;
    }

    protected final void popRewindpoint(RewindCursor cursor)
    {
        SortedSet<RewindCursor> equalAndLater = myRewindCursors.tailSet(cursor);
        equalAndLater.clear();
    }

    public void removeRewindpoint(RewindCursor cursor)
    {
        Validate.notNull(cursor,What.CURSOR);
        Validate.stateIsTrue(myEnabledFlag.get()==true,"enabled");
        synchronized(myRewindCursors) {
            popRewindpoint(cursor);
        }
    }

    public ControlFlowStatement doRewind(Rewindpoint marker, Harness harness)
    {
        Validate.neitherNull(marker,What.CURSOR,harness,What.HARNESS);
        Validate.stateIsTrue(myEnabledFlag.get()==true,"enabled");

        RewindCursor match=null;
        synchronized(myRewindCursors) {
            for (RewindCursor cursor:myRewindCursors) {
                if (cursor.matches(marker)) {
                    match = cursor;
                    popRewindpoint(cursor);
                    break;
                }
            }
        }
        Validate.stateIsTrue(match!=null,"rewindpoint in scope");
        assert match!=null;//Shutup findbugs
        return match.doRewind(harness);
    }


    private String myName=Strings.EMPTY;
    private AtomicBoolean myEnabledFlag= new AtomicBoolean();
    private Map<Unwindable,Boolean> myUnwinds = new IdentityHashMap<Unwindable,Boolean>();
    private SortedSet<RewindCursor> myRewindCursors = new TreeSet<RewindCursor>();
}


/* end-of-ScopeBean.java */
