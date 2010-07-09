/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.slf4j.Logger;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Executable;

/**
 * Starting implementation for executable objects within typical MWf4J system.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class ExecutableSkeleton implements Executable
{
    protected static final String NS = MWf4J.NS+".";

    protected ExecutableSkeleton()
    {
        super();
    }

    protected ExecutableSkeleton(String id)
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

    public void doEnter(Harness h)
    {
        Validate.fieldNotNull(h,What.HARNESS);
        diagnostics().trace("Enter {} {}",typeCN(),getId());
    }

    public void doLeave(Harness h)
    {
        diagnostics().trace("Leave {} {}",typeCN(),getId());
    }

    protected Logger diagnostics()
    {
        return Diagnostics.ForBAL;//Most common use for MWf4J
    }

    protected String typeCN()
    {
        return "executable";
    }

    protected String myId= Empties.STRING;
}


/* end-of-ExecutableSkeleton.java */
