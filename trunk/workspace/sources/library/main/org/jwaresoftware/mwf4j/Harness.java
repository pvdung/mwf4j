/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  java.util.concurrent.ExecutorService;

/**
 * Per root-action invocation wrapper around the incoming fixture that
 * control statements and other actions can use to post dynamic
 * continuations, post adjustments, gain access to their [grand]parent activity 
 * (which can change per invocation), and other such goodies. While a harness 
 * is defined as having a single owning activity, there is nothing to prevent
 * an executed action from spawing "child or sub harnesses" with their own
 * controlling activities (which might or might not be the same as the original).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 * @see       MDC#currentHarness()
 **/

public interface Harness extends Runnable, Fixture
{
    Variables getVariables();
    ExecutorService getExecutorService();
    void run();
    boolean isRunning();
    boolean isAborted();
    void addContinuation(ControlFlowStatement participant);
    void addUnwind(Unwindable participant);
    void removeUnwind(Unwindable participant);
    ControlFlowStatement runParticipant(ControlFlowStatement participant);
    void applyAdjustment(Adjustment action);
    String interpolate(String inputString);
    Activity getOwner();
    String typeCN();
    Fixture staticView();
}


/* end-of-Harness.java */
