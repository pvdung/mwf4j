/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;

/**
 * Will match a rewindpoint by name equality. Expects the target name
 * to be unique <em>within the entire universe</em> of rewind points for
 * the current harness. Null and blank or all-whitespace names are not 
 * permitted.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,extras,helper
 **/

public final class ByNameRewindpointMatcher extends RewindpointSkeleton
{
    public ByNameRewindpointMatcher(String markName)
    {
        super();
        setMark(markName);
    }

    public ByNameRewindpointMatcher(String id, String markName)
    {
        super(id);
        setMark(markName);
    }

    public void setMark(String markName)
    {
        Validate.notBlank(markName,What.NAME);
        myMarkName = markName;
    }

    @Override
    public boolean matches(Rewindpoint candidate)
    {
        return myMarkName.equals(candidate.getName());
    }

    private String myMarkName;
}


/* end-of-ByNameRewindpointMatcher.java */
