/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  java.util.Collections;
import  java.util.Iterator;
import  java.util.List;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Pair;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;

/**
 * Service facade that manages a stack of {@linkplain Scope scopes} for the
 * current thread. The scope "nearest" (or last pushed) is the active scope
 * for operations like unwinds, etc. A control flow statement that needs 
 * unwinding in case of an exception (e.g. sequences, loops, etc.), should
 * activate a scope and register their unwindable helpers when they're first
 * activated. 
 * <p/>
 * Scopes also provide base support for <i>rewinding</i> of executed actions. 
 * The application can retrieve a set of registered rewind points
 * (only available for active scopes). If one of these rewind points is used,
 * the existing scopes are unwound up to that point's own scope, and the
 * owning statement re-run from the point described by the rewind point. Note
 * that <em>what</em> exactly a rewind entails is control statement specific;
 * for example, a sequence might let you rewind to any of its component 
 * actions, whereas a loop forces you to rerun the entire thing as if no
 * iterations had been done.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (data accessed is tied to thread-local)
 * @.group    infra,impl,helper
 * @see       MDC
 **/

public final class Scopes
{
    /** Key we use to stash stack o' scope references. Keep testable- not private only. **/
    final static String _STACK = MWf4J.NS+".currentHarness...SCOPEs+";

    public static Scope enter(ControlFlowStatement statement, String name, Harness harness)
    {
        Validate.neitherNull(statement,What.STATEMENT,harness,What.HARNESS);
        ScopeKey key = ScopeFactory.newKey(statement);
        MDC.pshifmissing(_STACK,key);
        try {
            Scope block = ScopeFactory.newScope(statement,name);
            key.setScope(block);
            block.doEnter(harness);
            return block;
        } catch(RuntimeException rtX) {
            MDC.popif(_STACK,key);
            throw rtX;
        }
    }

    public static Scope enter(ControlFlowStatement statement, Harness harness)
    {
        return enter(statement,null,harness);
    }

    public static void leave(ControlFlowStatement statement, Harness harness)
    {
        Validate.neitherNull(statement,What.STATEMENT,harness,What.HARNESS);
        ScopeKey testKey = ScopeFactory.newKey(statement); 
        ScopeKey lastKey = MDC.get(_STACK,ScopeKey.class);
        if (testKey.equals(lastKey)) {
            Scope block = lastKey.getScope();
            Validate.fieldNotNull(block,What.SCOPE);
            block.doLeave(harness);
        }
        MDC.popif(_STACK,lastKey);//NB: will fail if null | not last
    }

    public static Scope nearestOrNull()
    {
        Scope block = null;
        ScopeKey lastKey = MDC.get(_STACK,ScopeKey.class);
        if (lastKey!=null) {
            block = lastKey.getScope();
        }
        return block;
    }

    public static Scope nearestOrFail()
    {
        Scope block = nearestOrNull();
        Validate.fieldNotNull(block,What.SCOPE);
        return block;
    }

    public static List<Scope> copyOf(Harness harness, boolean reverse)
    {
        Validate.stateIsTrue(harness==null || MDC.currentHarness()==harness,"harness is nearest");
        List<Scope> list= LocalSystem.newList(MDC.size(_STACK));
        Iterator<ScopeKey> qitr= MDC.itr(_STACK, ScopeKey.class);
        if (qitr!=null) {
            for (;qitr.hasNext();) {
                Scope block = qitr.next().getScope();
                Validate.fieldNotNull(block,What.SCOPE);
                list.add(block);
            }
            if (reverse && !list.isEmpty())
                Collections.reverse(list);//OLDEST first...
        }
        return list;
    }

    public static void addUnwind(Unwindable participant)
    {
        nearestOrFail().addUnwind(participant);
    }

    public static void removeUnwind(Unwindable participant)
    {
        nearestOrFail().removeUnwind(participant);
    }

    private static void leaveUpTo(ControlFlowStatement statement, Harness harness, boolean unwindFirst)
    {
        Validate.notNull(statement,What.STATEMENT);
        final ScopeKey stopKey = ScopeFactory.newKey(statement);
        Validate.stateIsTrue(MDC.has(_STACK,stopKey), "scope present and on stack");
        ScopeKey nextKey=null;
        do {
            nextKey = MDC.get(_STACK,ScopeKey.class);//NB: isa peek
            if (stopKey.equals(nextKey)) break;
            Validate.stateIsFalse(nextKey==null, "scope stack corrupted");
            assert nextKey!=null;//NB:shutup findbugs!
            MDC.pop(_STACK);
            Scope block = nextKey.getScope();
            Validate.fieldNotNull(block,What.SCOPE);
            if (unwindFirst) 
                block.doUnwind(harness);
            block.doLeave(harness);
        } while (true);
    }

    public static void unwindUpTo(ControlFlowStatement statement, Harness harness)
    {
        leaveUpTo(statement,harness,true);
    }

    private static Pair<Integer,ScopeKey> orphanedCount(Harness harness)
    {
        Pair<Integer,ScopeKey> ret= new Pair<Integer,ScopeKey>(-1,null);
        int orphaned= -1;//NB: => error in state!
        Iterator<ScopeKey> qitr = MDC.itr(_STACK,ScopeKey.class);
        if (qitr!=null) {
            ControlFlowStatement match = new HarnessRunStatement(harness);
            orphaned=0;
            for (;qitr.hasNext();) {
                ScopeKey nextKey = qitr.next();
                if (match.equals(nextKey.getOwner())) {
                    ret.set2(nextKey);
                    break;
                }
                orphaned++;
            }
        }
        if (ret.get2()!=null) {
            ret.set1(orphaned);
        }
        return ret;
    }

    final static String scopeNameOf(Harness harness)
    {
        return What.idFor(harness);
    }

    public static Scope enter(Harness harness)
    {
        MDC.pshHarness(harness.getOwner(),harness);
        try {
            HarnessRunStatement marker = new HarnessRunStatement(harness);
            return enter(marker,scopeNameOf(harness),harness);
        } catch(RuntimeException rtX) {
            MDC.popHarness(harness.getOwner(),harness);
            throw rtX;
        }
    }

    public static void leave(Harness harness)
    {
        Validate.notNull(harness,What.HARNESS);
        Pair<Integer,ScopeKey> ret = orphanedCount(harness);
        ScopeKey harnessKey = ret.get2();
        if (ret.get1()>0) {
            OrphanedScopeException issue= new OrphanedScopeException(harness,ret.get1());
            harness.getIssueHandler().problemOccured(issue.getMessage(),Effect.WORKAROUND,issue);
            leaveUpTo(harnessKey.getOwner(),harness,false);
        }
        if (harnessKey!=null) {
            harnessKey.getScope().doLeave(harness);
            MDC.popif(_STACK,harnessKey);
        }
        MDC.popHarness(harness.getOwner(),harness);
    }

    public static void unwind(Harness harness)
    {
        Validate.notNull(harness,What.HARNESS);
        Pair<Integer,ScopeKey> ret = orphanedCount(harness);
        ScopeKey harnessKey = ret.get2();
        if (ret.get1()>0) {
            unwindUpTo(harnessKey.getOwner(),harness);
        }
        if (harnessKey!=null) {
            harnessKey.getScope().doUnwind(harness);
        }
    }

    public static Scope findOrFail(Rewindpoint marker) 
    {
        Validate.notNull(marker,What.CURSOR);
        Validate.notNull(marker.getOwner(),What.STATEMENT);
        Scope block=null;
        Iterator<ScopeKey> qitr = MDC.itr(_STACK,ScopeKey.class);
        if (qitr!=null) {
            final ControlFlowStatement target = marker.getOwner();
            for (;qitr.hasNext();) {
                ScopeKey nextKey = qitr.next();
                if (target.equals(nextKey.getOwner())) {
                    block = nextKey.getScope();
                    Validate.fieldNotNull(block,What.SCOPE);
                    break;
                }
            }
        }
        return block;
    }

    public static ControlFlowStatement rewindFrom(Rewindpoint marker, Harness harness)
    {
        Validate.neitherNull(marker,What.CURSOR,harness,What.HARNESS);
        Scope block = findOrFail(marker);
        unwindUpTo(block.getOwner(),harness);
        return block.doRewind(marker,harness);
    }
}


/* end-of-Scopes.java */