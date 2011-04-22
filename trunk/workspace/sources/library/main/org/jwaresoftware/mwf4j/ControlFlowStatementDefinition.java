/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.reveal.Identified;

/**
 * Declarative override definition for a {@linkplain ControlFlowStatement}.
 * Whether the definition is static (fixed) or dynamic is implementation
 * defined. However, if you provide a external definition to a statement,
 * that definition should provide an identifier that the statement can use
 * to "mark" itself in any subsequent (and disconnected) diagnostic output.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface ControlFlowStatementDefinition extends Identified
{
    /**
     * (Re)Configure a control flow statement based on this definition's
     * current state. Typically this method is triggered indirectly via the 
     * statement's own reconfigure method which is the preferred way to ask
     * a statement to "prepare for (re)execution". Because a definition
     * contains the application-supplied overrides for a statement's attributes,
     * a statement will call this method to coordinate with the definition 
     * and apply those settings when it's most appropriate.
     * @param statement statement to configure (non-null)
     * @param environ fixture from which definition you retrieve 
     *           configuration if needed (non-null)
     * @throws java.lang.IllegalArgumentException if the definition does not
     *         recognize the incoming statement's type.
     **/
    void configureStatement(ControlFlowStatement statement, Fixture environ);
}


/* end-of-ControlFlowStatementDefinition.java */
