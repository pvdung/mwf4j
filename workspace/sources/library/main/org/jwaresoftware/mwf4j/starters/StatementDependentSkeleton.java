/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDependent;
import  org.jwaresoftware.mwf4j.What;

/**
 * Starting implementation for statement-dependent implementations.  
 * Tracks the owner statement attribute.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class StatementDependentSkeleton implements ControlFlowStatementDependent
{
    protected StatementDependentSkeleton()
    {
        super();
    }

    protected StatementDependentSkeleton(ControlFlowStatement owner)
    {
        this();
        initOwner(owner);
    }

    public final ControlFlowStatement getOwner()
    {
        return myOwner;
    }

    public void setOwner(ControlFlowStatement statement)
    {
        Validate.notNull(statement,What.STATEMENT);
        initOwner(statement);
    }

    protected final void initOwner(ControlFlowStatement statement)
    {
        myOwner = statement;
    }

    protected final String getWhatId()
    {
        String what = Strings.UNDEFINED;
        ControlFlowStatement owner = getOwner();
        if (owner!=null) {
            what = What.getNonBlankId(owner.getOwner());
        }
        return what;
    }

    private ControlFlowStatement myOwner;
}


/* end-of-StatementDependentSkeleton.java */
