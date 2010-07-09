/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.util.concurrent.Executor;

import  org.jwaresoftware.gestalt.bootstrap.FixtureWrap;

import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.Variables;

/**
 * Adapter that lets you customize one or two methods of an existing
 * harness while passing through the majority of the public API. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (guarded for continuation, unwinds, &amp; adjustments management)
 * @.group    impl,helper
 **/

public class HarnessWrap extends FixtureWrap implements Harness
{
    public HarnessWrap(Harness underlying)
    {
        super(underlying);
    }

    protected final Harness getTargetH()
    {
        return getTarget(Harness.class);
    }

    public Activity getOwner()
    {
        return getTargetH().getOwner();
    }

    public Variables getVariables()
    {
        return getTargetH().getVariables();
    }

    public Executor getExecutorService()
    {
        return getTargetH().getExecutorService();
    }

    public void run()
    {
        getTargetH().run();
    }

    public boolean isRunning()
    {
        return getTargetH().isRunning();
    }

    public void addContinuation(ControlFlowStatement participant)
    {
        getTargetH().addContinuation(participant);
    }

    public void addUnwind(Unwindable participant)
    {
        getTargetH().addUnwind(participant);
    }

    public void removeUnwind(Unwindable participant)
    {
        getTargetH().removeUnwind(participant);
    }

    public ControlFlowStatement runParticipant(ControlFlowStatement participant)
    {
        return getTargetH().runParticipant(participant);
    }

    public void applyAdjustment(Adjustment action)
    {
        getTargetH().applyAdjustment(action);
    }
}


/* end-of-HarnessWrap.java */
