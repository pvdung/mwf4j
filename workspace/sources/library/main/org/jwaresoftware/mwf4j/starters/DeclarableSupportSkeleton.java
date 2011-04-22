/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.behaviors.DeclarableEnabled;

/**
 * Starting implementation for a component that needs to support declarable
 * members without itself being declarable. Implements the 
 * {@linkplain DeclarableEnabled} interface and provides some utility
 * methods to manipulate if/when declarables are processed.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public abstract class DeclarableSupportSkeleton implements DeclarableEnabled
{
    protected DeclarableSupportSkeleton()
    {
        super();
    }

    public final void setCheckDeclarables(boolean flag)
    {
        myFreezeFlag = flag;
    }

    public boolean isCheckDeclarables()
    {
        return myFreezeFlag==null ? DEFAULT_CHECK_DECLARABLES_SETTING : myFreezeFlag;
    }

    public final Boolean getCheckDeclarables()
    {
        return myFreezeFlag;
    }

    protected void initCheckDeclarables()
    {
        myFreezeFlag = null;
    }

    protected final <T> T copyMember(T incoming)
    {
        T returned = incoming;
        if (isCheckDeclarables()) {
            returned = LocalSystem.newCopyOrSame(incoming);
        }
        return returned;
    }

    private Boolean myFreezeFlag;//UNDEFINED by default
}


/* end-of-DeclarableSupportSkeleton.java */
