/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.ProblemHandler;
import  org.jwaresoftware.gestalt.bootstrap.FixtureWrap;
import  org.jwaresoftware.gestalt.bootstrap.Fixture.Implementation;
import  org.jwaresoftware.gestalt.fixture.StringResolver;

/**
 * A fixture you can use for static lookup purposes. Trying to force cast this
 * to a harness will trigger niblet-filled barfage.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    aimpl,helper,test
 **/

public final class LocalSystemFixture extends FixtureWrap implements Fixture
{
    public LocalSystemFixture(Implementation impl)
    {
        super(impl);
        if (impl instanceof StringResolver) {
            myResolver = (StringResolver)impl;
        }
    }

    public final String interpolate(String inputString)
    {
        return interpolate(inputString,getIssueHandler());
    }

    public Variables getVariables()
    {
        throw new UnsupportedOperationException("LocalSystemFixture.getVariables");
    }

    public String interpolate(String inputString, ProblemHandler issueHandler)
    {
        return myResolver!=null ? myResolver.interpolate(inputString, issueHandler) : inputString;
    }

    private StringResolver myResolver;
}


/* end-of-LocalSystemFixture.java */
