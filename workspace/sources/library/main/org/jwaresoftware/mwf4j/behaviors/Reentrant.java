/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;

/**
 * Mixin interface for any composite or aggregation statement that has to
 * setup its controlled statements to "come back" to it after their own 
 * execution. Typically the reentrant will make the controlled statements
 * with itself as the ultimate continuation. Such statements often want
 * to be unwound by the activity if an uncaught exception forces an abort
 * of the entire run. The unwind might do something as simple as clearing
 * transient execution state or resetting the composite for re-execution.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 **/

public interface Reentrant extends Unwindable
{
    boolean checkFirstIteration(Harness harness);
    boolean willUnwind();
    void addUnwind();
    void removeUnwind();
}


/* end-of-Reentrant.java */
