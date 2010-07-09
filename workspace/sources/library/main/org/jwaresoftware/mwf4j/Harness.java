/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  java.util.concurrent.Executor;

import  org.jwaresoftware.gestalt.bootstrap.Fixture;

/**
 * Per root action invocation wrapper around the incoming fixture that
 * control statements and other actions can use to post dynamic
 * continuations, gain access to their parent activity (which can change
 * per invocation), and other such goodies. While a harness is defined as
 * having a single owning activity, there is nothing to prevent a harness
 * from spawing "child or sub harnesses" with their own controlling 
 * activities (which might or might not be the same as the original).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 * @see       MDC#currentHarness()
 **/

public interface Harness extends Runnable, Fixture.Implementation
{
    Activity getOwner();
    Variables getVariables();
    Executor getExecutorService();
    void run();
    boolean isRunning();
    void addContinuation(ControlFlowStatement participant);
    void addUnwind(Unwindable participant);
    void removeUnwind(Unwindable participant);
    ControlFlowStatement runParticipant(ControlFlowStatement participant);
    void applyAdjustment(Adjustment action);
}


/* end-of-Harness.java */
