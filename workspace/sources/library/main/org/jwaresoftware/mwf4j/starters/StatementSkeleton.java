/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.slf4j.Logger;

import  org.jwaresoftware.gestalt.reveal.Identified;
import  org.jwaresoftware.gestalt.system.LocalSystem;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDefinition;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Executable;
import  org.jwaresoftware.mwf4j.behaviors.Traceable;

/**
 * Starting implementation for most MWf4J control flow statements. Tracks
 * the statement's next statement attribute and provides template methods
 * for {@linkplain #reconfigure}, {@linkplain #run(Harness) run}, and 
 * {@linkplain #toString() toString}.
 * <p/>
 * The template reconfigure method will ask the supplied override definition
 * to apply itself and then call this statement's {@linkplain #verifyReady} 
 * method. Note that the statement will do a lite "reset" of its current
 * configuration <em>before</em> is calls the definition to apply itself.
 * This means that factories that build statements that also use definition
 * overrides, should be careful what attributes they set in the construction
 * vs the configuration phase. Some settings (like declarable checks) will 
 * be <em>reset</em> before any (re)configuration to ensure a predictable 
 * baseline for the statement.
 * <p/>
 * The template run method will log (at 'finest' trace level) entry,
 * exit, and abnormal error events. Subclasses must implement the abstract 
 * {@linkplain #runInner(Harness) runInner} method and optionally the
 * {@linkplain #verifyReady} and {@linkplain #doError doError} methods. 
 * Note that this skeleton DOES define the {@linkplain #isTerminal} method
 * to return <i>false</i> (the usual for all types of statements except 
 * end ones).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public abstract class StatementSkeleton extends DeclarableSupportSkeleton 
    implements Executable, ControlFlowStatement
{
    /** 
     * Link that permits statement subclasses some control over trace feedback.
     * @since   JWare/MWf4J 1.0.0
     * @author  ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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


    public ControlFlowStatement next()
    {
        return nextStatement;
    }


    public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
    {
        applyDefinition(overrides,environ);
        if (isCheckDeclarables()) {
            doFreeze(environ);
        }
        verifyReady();
    }


    protected void applyDefinition(ControlFlowStatementDefinition overrides, Fixture environ)
    {
        if (overrides!=null) {
            initCheckDeclarables();
            setWhatId(overrides);
            overrides.configureStatement(this,environ);
        }
    }

    protected boolean doFreeze(Fixture environ)
    {
        return isCheckDeclarables();
    }


    public boolean isTerminal()
    {
        return false;
    }


    public boolean isAnonymous()
    {
        return myWhatId==null;
    }


    protected final void initNextStatement(ControlFlowStatement next)//Used by extensions-points
    {
        nextStatement = next;
    }


    protected final void setWhatId(String what)
    {
        myWhatId = what;
    }

    protected final void setWhatId(final Identified from)
    {
        setWhatId(What.getNonBlankId(from));
    }


    /**
     * Validate that this statement is ready for execution. Made public to ensure
     * secondary use of statements (as continuation outside of formal action-&gt;statement)
     * can also include validation call.
     **/
    public void verifyReady()
    {
        //nothing by default; extend to ensure all required attributes defined!
    }


    public String getWhatId()
    {
        return myWhatId!=null ? myWhatId : Strings.EMPTY;
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
        sb.append("[H=").append(MDC.currentHarnessTypeOrEmpty());
        sb.append("|S=");
        try { addToString(sb); } catch(Exception e) {sb.append("toString.ERROR!");}
        sb.append("]");
        return sb.toString();
    }


    protected abstract ControlFlowStatement runInner(Harness harness);


    public ControlFlowStatement run(final Harness harness) //NB: enforces a breadcrumbs trail...
    {
        breadcrumbs().doEnter(harness);
        doEnter(harness);
        try {
            ControlFlowStatement next= runInner(harness);
            breadcrumbs().doNexxt(next,harness);
            return next;
        } catch(RuntimeException rtX) {
            doError(harness,rtX);
            throw rtX;
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


    public void doError(Harness h, Throwable issue)
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


    private String myWhatId;
    private ControlFlowStatement nextStatement;//NB: can be NULL
    private TraceSupport breadcrumbs = new TraceSupport(new TraceLink());
}


/* end-of-StatementSkeleton.java */
