/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  java.util.concurrent.ExecutorService;

import  org.jwaresoftware.gestalt.ProblemHandler;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;

/**
 * Specialized harness that a primary activity can use to launch dependent
 * async sub-activities from their own threads-of-execution. Useful
 * for things like implementation-only forks or parallel flows.
 * <p/>
 * <b>Usage note 1:</b> if you setup an MDC initializer for a spawned harness, 
 * <em>you</em> need to call the initializer's copy method to setup what MDC 
 * items are to be copied as the harness cannot know when is the correct 
 * time to call the copy. Once the spawned harness's run method is triggered
 * (presumably in its own thread-of-execution), the initializer's paste method
 * will be called before any statements are run. 
 * <p>
 * <b>Usage note 2:</b> while a spawned harness can support re-running
 * hypothetically, it is not required nor expected and our implementation
 * does not support it. The biggest obstacle for re-running is the initial
 * statement that you provide to the harness. To re-run we would have to
 * either receive a new first statement or reconfigure the existing
 * statement properly; a spawned harness is unable to perform either  
 * of these actions on its own (from its run method) without making a few 
 * assumptions like the statement is self-contained and is not adhoc so it 
 * can be reconfigured.
 * <p>
 * <b>Usage note 3:</b> because a spawned harness has a run-cycle just like
 * a primary harness, it will notify it's owner (which is same as it's
 * parent harness's owner), on doEnter, doLeave, doError if necessary. This
 * means the activity will be notified <em>of each related harness's</em>
 * events. Of course, this is only relevant if the activity implements the
 * standard {@linkplain org.jwaresoftware.mwf4j.behaviors.Executable Executable} 
 * interface.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (same as superclass)
 * @.group    infra,impl,helper
 **/

public abstract class SpawnedHarnessSkeleton extends HarnessSkeleton implements SpawnedHarness
{
    protected SpawnedHarnessSkeleton(Harness parent, ControlFlowStatement first)
    {
        super(parent);
        Validate.notNull(first,What.STATEMENT);
        myParent = parent;
        myFirst = first;
    }

    public void setMDCInitializer(MDC.Propagator initializer)
    {
        Validate.notNull(initializer,What.CALLBACK);
        myMDCInitializer = initializer;
    }

    public ExecutorService getExecutorService()
    {
        return myParent.getExecutorService();
    }

    public Activity getOwner()
    {
        return myParent.getOwner();
    }

    public Variables getVariables()
    {
        return myParent.getVariables();
    }

    public String interpolate(String inputString, ProblemHandler callback)
    {
        return myParent.interpolate(inputString,callback);
    }

    public Harness getHarnessDependentOn()
    {
        return myParent;
    }

    protected void doEnter()
    {
        myMDCInitializer.paste();//DO *BEFORE* CALLING INHERITED
        super.doEnter();
    }

    protected ControlFlowStatement firstStatement()
    {
        return myFirst;
    }

    final Harness myParent;
    private ControlFlowStatement myFirst;
    private MDC.Propagator myMDCInitializer=MDC.Propagator.nullINSTANCE;
}


/* end-of-SpawnedHarnessSkeleton.java */
