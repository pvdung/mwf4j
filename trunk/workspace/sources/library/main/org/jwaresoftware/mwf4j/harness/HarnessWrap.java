/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  java.util.concurrent.ExecutorService;

import  org.jwaresoftware.gestalt.ProblemHandler;
import  org.jwaresoftware.gestalt.bootstrap.FixtureWrap;

import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.Adjustment;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.Variables;

/**
 * Adapter that lets you customize one or two methods of an existing
 * harness while passing through the majority of the public API to a
 * pre-existing instance.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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

    public ExecutorService getExecutorService()
    {
        return getTargetH().getExecutorService();
    }

    public Fixture staticView()
    {
        return getTargetH().staticView();
    }

    public String interpolate(String inputString, ProblemHandler callback)
    {
        return getTargetH().interpolate(inputString,callback);
    }

    public String interpolate(String inputString)
    {
        return getTargetH().interpolate(inputString);
    }

    public void run()
    {
        getTargetH().run();
    }

    public boolean isRunning()
    {
        return getTargetH().isRunning();
    }

    public boolean isAborted()
    {
        return getTargetH().isAborted();
    }

    public String typeCN()
    {
        return getTargetH().typeCN();
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
