/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  java.util.concurrent.ExecutorService;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.DependentHarness;  
import  org.jwaresoftware.mwf4j.starters.ActionToActivityAdapter;

/**
 * Specialized harness that one action can use to execute another action completely
 * before returning; for instance, your error handling code might want to ensure
 * an application-supplied error handler action is run to completion before
 * continuing (does not want continuations from handler mixing with normal
 * flow on primary harness). We expect you to run a nested harness from the
 * <em>SAME</em> thread-of-execution as its parent (typically the one passed
 * to a statement's run method). Note that a nested harness's owner is the
 * activity being run by the nested harness-- <em>NOT</em> the activity linked
 * to its parent harness!
 * <p/>
 * Note that unlike a {@linkplain SlaveHarness slave harness} a nested harness 
 * does NOT propagate an uncaught error back to its parent. Any such error is 
 * logged but "lost" to the parent.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (same as superclass)
 * @.group    infra,impl,helper
 * @see       ActionToActivityAdapter
 **/

public final class NestedIsolatedHarness extends HarnessSkeleton implements DependentHarness
{
    public NestedIsolatedHarness(Activity activity, Harness parent)
    {
        super(parent);
        Validate.notNull(activity,What.ACTIVITY);
        myParent = parent;
        myActivity = activity;
    }

    public NestedIsolatedHarness(Action action, Harness parent)
    {
        this(new ActionToActivityAdapter(action),parent);
    }

    public ExecutorService getExecutorService()
    {
        return myParent.getExecutorService();
    }

    public Activity getOwner()
    {
        return myActivity;
    }

    public Variables getVariables()
    {
        return myParent.getVariables();
    }

    public Harness getHarnessDependentOn()
    {
        return myParent;
    }

    public String typeCN()
    {
        return "inner";
    }

    private final Harness myParent;
    private Activity myActivity;
}


/* end-of-NestedIsolatedHarness.java */
