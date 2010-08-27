/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Marker interface of an entity that relies on a flow statement to implement
 * its responsibilities.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface ControlFlowStatementDependent
{
    ControlFlowStatement getOwner();
}


/* end-of-ControlFlowStatementDependent.java */
