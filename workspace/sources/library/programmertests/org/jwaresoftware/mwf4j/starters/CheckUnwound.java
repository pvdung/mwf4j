/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;

/**
 * Action that verifies an expected unwind has been executed. Useful for 
 * multi-threaded, multi-harness, and exception handling testing to verify
 * handlers and cleanup performed as expected.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 * @see       org.jwaresoftware.mwf4j.helpers.TestUnwinder TestUnwinder
 **/

public final class CheckUnwound extends ActionSkeleton
{
    public CheckUnwound(String id, String unwinderNam) 
    {
        super(id);
        Validate.notNull(unwinderNam,"unwinder-id");
        this.unwinderName = unwinderNam;
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isA(statement,CheckUnwoundStatement.class,What.STATEMENT);
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new CheckUnwoundStatement(getId(),unwinderName,this,next);
    }

    private final String unwinderName;
}


/* end-of-CheckUnwound.java */
