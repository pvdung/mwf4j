/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;

/**
 * Adapts an {@linkplain Action action} to the harness-friendly standalone 
 * {@linkplain Activity activity} interface. Use to inline execute an
 * arbitrary action to its completion before returning (requires a harness).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (as guarded as linked action and its statement factory)
 * @.group    impl,helper
 * @see       org.jwaresoftware.mwf4j.harness.NestedIsolatedHarness NestedHarness
 **/

public final class ActionToActivityAdapter implements Activity
{
    public ActionToActivityAdapter(Action definition)
    {
        this(definition, new ActivityEndStatement());
    }

    public ActionToActivityAdapter(Action definition, ControlFlowStatement end)
    {
        Validate.neitherNull(definition,What.ACTION,end,What.STATEMENT);
        myDefinition  = definition;
        myEnd = end;
    }

    public ActionToActivityAdapter()//Effectively a no-op stub activity!
    {
        myDefinition = null;
    }

    public String getId()
    {
        return myDefinition!=null ? myDefinition.getId() : Strings.EMPTY;
    }

    public ControlFlowStatement firstStatement(Fixture environ)
    {
        return myDefinition!=null ? myDefinition.buildStatement(myEnd,environ) : myEnd;
    }

    private Action myDefinition;
    private ControlFlowStatement myEnd;
}


/* end-of-ActionToActivityAdapter.java */
