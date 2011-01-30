/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ActionDependent;
import  org.jwaresoftware.mwf4j.What;

/**
 * Starting implementation for action-dependent implementations. Tracks the
 * owner action attribute and provides a basic identifier utility function,
 * {@linkplain #getWhatId()} that is based on the dependent's owner's 
 * identity.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class ActionDependentSkeleton implements ActionDependent
{
    protected ActionDependentSkeleton()
    {
        super();
    }

    protected ActionDependentSkeleton(Action owner)
    {
        this();
        initOwner(owner);
    }

    public final Action getOwner()
    {
        return myOwner;
    }

    public void setOwner(Action action)
    {
        Validate.notNull(action,What.ACTION);
        initOwner(action);
    }

    protected final void initOwner(Action action)
    {
        myOwner = action;
    }

    protected final String getWhatId()
    {
        return What.getNonBlankId(getOwner());
    }

    private Action myOwner;//NB: can be NULL
}


/* end-of-ActionDependentSkeleton.java */
