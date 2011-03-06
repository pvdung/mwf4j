/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * Special action that is itself an ordered sequence of sibling actions.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Sequence extends Action
{
    Sequence add(Action action);
    int size();
    boolean isEmpty();
    Action lastAdded();
}


/* end-of-Sequence.java */
