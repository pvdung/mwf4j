/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  java.lang.ref.SoftReference;
import  java.util.Collection;
import  java.util.List;
import  java.util.Map;

import  org.apache.commons.lang.ObjectUtils;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.fixture.FixtureProperties;
import  org.jwaresoftware.gestalt.helpers.PerThreadStash;
import  org.jwaresoftware.gestalt.system.LocalSystem;

/**
 * MWf4J specific per-thread mapped diagnostic context (MDC). All MWf4J 
 * closures should rely on this variation of the per-thread stash 
 * to obtain active execution context including harness, configuration,
 * and datamap information. Note that we expect mostly callbacks and
 * closures to use the MDC to obtain activity related harness information.
 * Other components like statements and actions have the relevant harness
 * <em>given</em> to them via the appropriate methods, so there should be
 * no need to rely on the thread context generally.
 * <p/>
 * While our BAL activities will ensure the root harness is installed
 * in the MDC, statements that call closures explicitly or create dynamic
 * helper statements should explicitly psh/pop their harnesses if that
 * is in their contract with the closure or callback. Statements should
 * NOT assume their activity has done this work for them (or that they're
 * being called from a single root activity directly).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 **/

public final class MDC extends PerThreadStash
{
    /** Key we use to stash stack o' harness references. Keep testable- not private only. **/
    final static String HREF_STACK = MWf4J.NS+".currentHarness...HREFs+";


    /**
     * Helper to track the same harness nested from different run scopes.
     * This ensures one unbalanced scope cannot accidently pop the harness
     * installed at another level. Note that the same source can appear at
     * different levels of stack for recursiveness support.
     * 
     * @since  JWare/MWf4j 1.0.0
     * @.group impl,helper
     * @.impl  Make sure can unit test within package.
     */
    static final class HRef
    {
        private final int mySourceHash;
        private final SoftReference<Harness> myPtr;

        HRef(Object from, Harness harness)
        {
            Validate.neitherNull(from,What.SOURCE,harness,What.HARNESS);
            mySourceHash = System.identityHashCode(from);//NB:*not* unique but unchanging!
            myPtr = new SoftReference<Harness>(harness);//JVM can zap but not likely if activity running
        }
        public boolean equals(Object other) 
        {
            if (other==null) return false;
            if (other==this) return true;
            boolean eq=false;
            if (HRef.class.equals(other.getClass())) {
                HRef otherptr = (HRef)other;
                eq = mySourceHash==otherptr.mySourceHash
                     && ObjectUtils.equals(myPtr.get(),otherptr.myPtr.get()) ;
            }
            return eq;
        }
        public int hashCode() 
        {
            return mySourceHash + 39*ObjectUtils.hashCode(myPtr.get());
        }
    }

    // --------------------------------------------------------------------
    // Managing harness references for helpers, closures, etc.
    // --------------------------------------------------------------------

    public static void pshHarness(Object from, Harness harness)
    {
        psh(HREF_STACK,new HRef(from,harness));
    }

    public static void popHarness(Object from, Harness harness)
    {
        popif(HREF_STACK,new HRef(from,harness));
    }

    public static Harness currentHarness()
    {
        HRef href = getOrFail(HREF_STACK,HRef.class);
        Harness harness = href.myPtr.get();
        if (harness==null) {
            throw new MWf4JUnknownStateException("MWf4J harness has been purged by JVM!");
        }
        return harness;
    }

    public static String currentHarnessTypeOrEmpty()
    {
        HRef href = get(HREF_STACK,HRef.class);
        if (href!=null) {
            Harness h = href.myPtr.get();
            if (h!=null) 
                return h.typeCN();
        }
        return "";
    }

    public static Variables currentVariables()
    {
        return currentHarness().getVariables();
    }

    public static FixtureProperties currentConfiguration()
    {
        return currentHarness().getConfiguration();
    }

    // --------------------------------------------------------------------
    // For root unwind handlers and testability of harness management
    // --------------------------------------------------------------------

    public static int harnessDepth()
    {
        return size(HREF_STACK);
    }

    public static void assertNoHarnessInstalled()
    {
        Collection<?> c = coll(HREF_STACK,Object.class);
        Validate.stateIsTrue(c==null || c.isEmpty(), 
                    "no installed MWf4J harnesses");
    }

    public static void assertHarnessDepth(int depth)
    {
        int curdepth = harnessDepth();
        Validate.stateIsTrue(curdepth==depth, 
                    "{MWf4J harness depth should be '%1$d' but is '%2$d'}",depth,curdepth);
    }


    // --------------------------------------------------------------------
    // Tracking exceptions for try-catch handlers (local|root)
    // --------------------------------------------------------------------

    public static Throwable uncaughtError() 
    {
        return get(MWf4J.MDCKeys.UNCAUGHT_ERROR, Throwable.class);
    }

    public static Exception latestException()
    {
        return get(MWf4J.MDCKeys.LATEST_ERROR, Exception.class);
    }


    // --------------------------------------------------------------------
    // Managing dynamic indentation levels (unique per grouping)
    // --------------------------------------------------------------------

    private static String indentation(int level)
    {
        if (level<=0) return "";
        StringBuilder sb = new StringBuilder(level*2);
        for (int i=0;i<level;i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public static final String enterIndent(String what)
    {
        int level = indentIncreAndGet(what);
        return indentation(level);
    }

    public static final String leaveIndent(String what)
    {
        int level = indentGetAndDecre(what);
        return indentation(level);
    }

    public static final String currentIndent(String what)
    {
        int level = indentGet(what);
        return indentation(level);
    }


    // --------------------------------------------------------------------
    // Managing selective copy of bits between master and slave harnesses
    // --------------------------------------------------------------------

    /**
     * Interface for simple utility that moves values between two MDCs 
     * (typically master-slave or parent-child). The copy must be done 
     * from the source MDC's thread; the paste from the targets'.
     *
     * @since     JWare/MWf4J 1.0.0
     * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
     * @version   @Module_VERSION@
     * @.safety   n/a
     * @.group    impl,infra
     **/
    public interface Propagator
    {
        Map<String,Object> copy();
        void paste(Map<String,Object> snapshot);
        void paste();

        /** Functioning null-proxy. Does compatible no-ops for all methods. **/
        public final static Propagator nullINSTANCE = new Propagator() 
        {
            public Map<String,Object> copy() 
                { return Empties.newMap(); }
            public void paste(Map<String,Object> snapshot) 
                { /*empty*/ }
            public void paste() 
                { /*empty*/ }
        };
    }


    /**
     * Really simple propagator of values between two MDC contexts. Assumes
     * the application knows what it's doing and does absolutely <em>NO</em>
     * checking against what's copied. Useful for test benches and to copy
     * simple immutable types from one thread to another.
     * <p/>
     * Usage note: you must setup all the MDC keys to be copied <em>before</em>
     * calling copy. Otherwise, the saved snapshot will <em>not</em> contain
     * your additional keys. Also note that, every call to {@linkplain #copy()}
     * overwrites the propagator's snapshot.
     *
     * @since     JWare/MWf4J 1.0.0
     * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
     * @version   @Module_VERSION@
     * @.safety   single
     * @.group    impl,helper
     **/
    public final static class SimplePropagator implements Propagator
    {
        private final List<String> myCopyNames;
        private Map<String,Object> mySnapshot;

        public SimplePropagator(String...names) 
        {
            Validate.notEmpty(names,What.CRITERIA);
            myCopyNames= LocalSystem.newList(Math.max(names.length,10));
            for (String next:names) {
                myCopyNames.add(next);
            }
        }

        public void addName(String name)
        {
            Validate.notBlank(name,What.NAME);
            myCopyNames.add(name);
        }

        public Map<String,Object> copy()
        {
            Map<String,Object> snapshot = LocalSystem.newMap();
            Map<String,Object> stash = getOrNull();//NB: in source thread!
            if (stash!=null) {
                for (String name:myCopyNames) {
                    if (stash.containsKey(name)) {
                        snapshot.put(name,stash.get(name));
                    }
                }
            }
            mySnapshot = snapshot;
            return snapshot;
        }

        public void paste(Map<String,Object> snapshot)
        {
            Validate.notNull(snapshot,What.CRITERIA);
            if (!snapshot.isEmpty()) {
                Map<String,Object> stash = get();//NB: in target thread!
                stash.putAll(snapshot);
            }
        }
        
        public void paste()
        {
            paste(mySnapshot);
        }
    }


    private MDC() {
        //only static APIs
    }
}


/* end-of-MDC.java */
