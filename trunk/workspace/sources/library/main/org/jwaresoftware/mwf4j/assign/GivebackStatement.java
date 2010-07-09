/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Giveback that returns a precanned statement as-is. Can double as an 
 * action adapter whose {@linkplain makeStatement} factory method also 
 * returns the prebuilt statement as-is. Note that the <em>same
 * un-reset statement</em> instance is returned for every 'call' and 
 * every 'makeStatement'!
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class GivebackStatement implements Giveback<ControlFlowStatement>, Action
{
    public GivebackStatement(ControlFlowStatement statement)
    {
        Validate.notNull(statement,What.STATEMENT);
        myStatement = statement;
    }

    public GivebackStatement(String id, ControlFlowStatement statement)
    {
        this(statement);
        myId = Strings.trimToEmpty(id);
    }

    public ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        return myStatement;
    }

    public void configure(ControlFlowStatement statement)
    {
        //nothing;
    }

    public ControlFlowStatement call()
    {
        return makeStatement(ControlFlowStatement.nullINSTANCE);
    }

    public String getId()
    {
        return myId;
    }

    private String myId= Strings.EMPTY;
    private final ControlFlowStatement myStatement;
}


/* end-of-GivebackStatement.java */
