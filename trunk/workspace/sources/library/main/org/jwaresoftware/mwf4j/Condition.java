/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Interface for application-supplied tests used for branching,
 * compensation, and 'do-again' decisions.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    api,infra
 * @see       org.jwaresoftware.mwf4j.bal.IfElseAction IfThenAction
 * @see       org.jwaresoftware.mwf4j.bal.WhileAction WhileAction
 **/

public interface Condition
{
    boolean evaluate(Harness harness);
}


/* end-of-Condition.java */
