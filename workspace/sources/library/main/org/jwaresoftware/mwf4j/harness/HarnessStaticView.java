/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.gestalt.ProblemHandler;
import  org.jwaresoftware.gestalt.bootstrap.FixtureWrap;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Variables;

/**
 * Readonly view onto an existing {@linkplain Harness harness}. Use for 
 * passthru of harness to factory methods that expect a {@linkplain Fixture}
 * but are <em>not</em> allowed to modify or trigger harness-specific 
 * functionality like unwinds and continuations.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public class HarnessStaticView extends FixtureWrap implements Fixture
{
    public HarnessStaticView(Harness harness)
    {
        super(harness);
    }

    public final String interpolate(String value)
    {
        return interpolate(value,getIssueHandler());
    }

    public String interpolate(String value, ProblemHandler issueHandler)
    {
        return getTarget(Harness.class).interpolate(value,issueHandler);
    }

    public Variables getVariables()
    {
        return getTarget(Harness.class).getVariables();
    }
}


/* end-of-HarnessStaticView.java */
