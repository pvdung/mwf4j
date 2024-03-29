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
 * Action that unstalls an unwindable that will note whether it was ever
 * called to test fixture.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   SINGLE
 * @.group    impl,test,helper
 **/

public final class AddTestUnwindAction extends ActionSkeleton
{
    public AddTestUnwindAction(String id)
    {
        this(id,id);
    }

    public AddTestUnwindAction(String id, String unwinderNam)
    {
        super(id);
        Validate.notBlank(unwinderNam,What.NAME);
        this.unwinderName = unwinderNam;
    }

    public void configureStatement(ControlFlowStatement statement, Fixture environ)
    {
        Validate.isA(statement,AddTestUnwindStatement.class,What.STATEMENT);
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new AddTestUnwindStatement(getId(),unwinderName,this,next);
    }

    private final String unwinderName;
}


/* end-of-AddTestUnwindAction.java */
