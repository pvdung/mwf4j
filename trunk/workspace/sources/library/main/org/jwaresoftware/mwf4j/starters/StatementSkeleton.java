/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.slf4j.Logger;

import  org.jwaresoftware.gestalt.system.LocalSystem;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.behaviors.Executable;
import  org.jwaresoftware.mwf4j.behaviors.Traceable;

/**
 * Starting implementation for control flow statements. Tracks the 
 * owner action and next statement attributes and provides a template method
 * for {@linkplain #run(Harness) run()} that logs (at 'finest' trace level)
 * entry and exit. Subclasses must implement the abstract 
 * {@linkplain #runInner(Harness) runInner} method and optionally the
 * {@linkplain #verifyReady()} method. Note that this skeleton DOES define
 * the {@linkplain #isTerminal()} method to return <i>false</i> (the usual
 * for all types of statements except end ones}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class StatementSkeleton extends ActionDependentSkeleton 
    implements Executable, ControlFlowStatement
{
    /** 
     * Link that permits any subclasses some control over trace feedback.
     * @since JWare/MWf4J 1.0.0
     * @author  ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
     * @version @Module_VERSION@
     * @.safety single
     * @.group  impl,helper
     **/
    public class TraceLink implements Traceable
    {
        public TraceLink() {
            super();
        }
        public String toString() {
            return StatementSkeleton.this.toString();
        }
        public String typeCN() {
            return "statement";
        }
        public Logger logger() {
            return Diagnostics.ForFlow;
        }
        public String getId() {
            return getWhatId();
        }
    }


    protected StatementSkeleton()
    {
        super();
    }


    protected StatementSkeleton(ControlFlowStatement next)
    {
        super();
        initNextStatement(next);
    }


    protected StatementSkeleton(Action owner, ControlFlowStatement next)
    {
        super(owner);
        initNextStatement(next);
    }


    public ControlFlowStatement next()
    {
        return nextStatement;
    }


    public void reconfigure()
    {
        Action action = getOwner();
        if (action!=null) {
            action.configure(this);
        }
    }


    public boolean isTerminal()
    {
        return false;
    }


    public boolean isAnonymous()
    {
        return getOwner()==null;
    }


    protected final void initNextStatement(ControlFlowStatement next)//Used by extensions-points
    {
        nextStatement = next;
    }


    
    /**
     * Validate that this statement is ready for execution. Made public to ensure
     * secondary use of statements (as continuation outside of formal action-&gt;statement)
     * can also include validation call.
     **/
    public void verifyReady()
    {
        //nothing by default
    }


    protected StringBuilder addToString(StringBuilder sb) 
    {
        sb.append(getWhatId());
        return sb;
    }


    public String toString()
    {
        StringBuilder sb = LocalSystem.newSmallStringBuilder();
        sb.append(getClass().getSimpleName()).append('@').append(System.identityHashCode(this));
        sb.append("[A=");
        try { addToString(sb); } catch(Exception e) {sb.append("toString.ERROR!");}
        sb.append("]");
        return sb.toString();
    }


    protected abstract ControlFlowStatement runInner(Harness harness);


    public ControlFlowStatement run(Harness harness) //NB: enforces a breadcrumbs trail...
    {
        breadcrumbs().doEnter(harness);
        doEnter(harness);
        try {
            ControlFlowStatement next= runInner(harness);
            breadcrumbs().doNexxt(next,harness);
            return next;
        } finally {
            doLeave(harness);
            breadcrumbs().doLeave(harness);
        }
    }


    public void doEnter(Harness h)
    {
        //Nothing by default
    }


    public void doLeave(Harness h)
    {
        //Nothing by default
    }


    protected final TraceSupport breadcrumbs()
    {
        return breadcrumbs;
    }


    protected final void initBreadcrumbs(TraceSupport bc)
    {
        Validate.notNull(bc,"trace-support");
        breadcrumbs = bc;
    }


    private ControlFlowStatement nextStatement;
    private TraceSupport breadcrumbs = new TraceSupport(new TraceLink());
}


/* end-of-StatementSkeleton.java */
