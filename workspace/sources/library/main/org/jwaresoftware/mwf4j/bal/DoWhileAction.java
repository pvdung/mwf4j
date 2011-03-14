/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;

/**
 * Variation of the regular {@linkplain WhileAction while action} that works
 * as a <i>do while</i>; i&#46;e&#46; the loop is always executed at least
 * once and THEN the test is conducted.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       DoWhileStatement
 * @see       ForEachAction
 **/

public class DoWhileAction extends WhileAction
{
    public DoWhileAction()
    {
        super("dowhile");
    }

    public DoWhileAction(String id)
    {
        super(id);
    }

    protected WhileStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        return new DoWhileStatement(next);
    }
}


/* end-of-DoWhileAction.java */
