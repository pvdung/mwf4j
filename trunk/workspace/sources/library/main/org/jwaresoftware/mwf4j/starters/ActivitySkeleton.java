/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Activity;
import  org.jwaresoftware.mwf4j.What;

/**
 * Starter implementation for the {@linkplain Activity} interface that  
 * works for many common application build workflows. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   guarded
 * @.group    impl,infra
 **/

public abstract class ActivitySkeleton extends ExecutableSkeleton implements Activity
{
    protected ActivitySkeleton(String id)
    {
        super(id);
    }



    protected ActivitySkeleton()
    {
        super();
    }



    protected void setDefinition(Action definition)
    {
        Validate.notNull(definition,What.ACTION);
        myDefinition = definition;
    }



    protected Action getDefinition()
    {
        return myDefinition;
    }



    public ControlFlowStatement firstStatement()
    {
        ControlFlowStatement end = new ActivityEndStatement();
        return getDefinition().makeStatement(end);
    }



    protected String typeCN()
    {
        return "activity";
    }



    private Action myDefinition;
}


/* end-of-ActivitySkeleton.java */
