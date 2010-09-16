/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Errors;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;

/**
 * Starting implementation for a closure that expects to extract
 * the current action harness from the surrounding thread's
 * {@linkplain MDC}. Note that an instance of this class alters instance
 * state during a call -- so it is NOT safe for use from multiple threads
 * concurrently.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class ClosureSkeleton
{
    protected ClosureSkeleton()
    {
        super();
    }

    protected void prepare()
    {
        this.harness = MDC.currentHarness();
        this.vars = harness.getVariables();
    }

    protected void cleanup()
    {
        this.harness = null;
        this.vars = null;
    }

    protected final Variables myVars()
    {
        Validate.fieldNotNull(vars,What.VARIABLES);
        return this.vars;
    }

    protected final Harness myHarness()
    {
        Validate.fieldNotNull(harness,What.HARNESS);
        return this.harness;
    }

    protected final void assertVarExists(String varname)
    {
        if (!myVars().containsKey(varname)) {
            throw new IllegalStateException(Errors.BAD_STATE+"{var '"+varname+"' should exist}");
        }
    }
 
    protected Harness harness;
    protected Variables vars;
}


/* end-of-ClosureSkeleton.java */
