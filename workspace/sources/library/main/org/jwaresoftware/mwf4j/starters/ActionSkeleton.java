/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Starting implementation for an action. Tracks the action's id
 * and adds two template methods {@linkplain #finish} and 
 * {@linkplain #verifyReady()}
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    infra,impl
 **/

public abstract class ActionSkeleton implements Action
{
    protected ActionSkeleton()
    {
        super();
    }

    protected ActionSkeleton(String id)
    {
        myId = Strings.trimToEmpty(id);
    }

    public String getId()
    {
        return myId;
    }

    protected void setId(String id)
    {
        Validate.notNull(id,What.ID);
        myId = Strings.trimToEmpty(id);
    }

    protected ControlFlowStatement finish(ControlFlowStatement made)
    {
        made.reconfigure();
        return made;
    }

    protected void verifyReady()
    {
        //nothing by default; used by actions with independent setters
    }

    public String toString()
    {
        return What.subidFor(this,getId(),"Action");
    }


    private String myId= Strings.EMPTY;
}


/* end-of-ActionSkeleton.java */
