/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.Harness;

/**
 * Mixin implementation interface for any MWf4J entity that has a single 
 * "run" type method from which it performs its primary responsibility. This
 * mixin defines three template methods for use around the run method: 
 * doEnter called just before the main run "work", doError called when
 * there is an unexpected termination of run method, and doLeave called just
 * after the run "work" is finished (successfully or not). Note that the
 * the doLeave method may or may not be called if an error occured. Read
 * each specific implementation/use of the Execution interface for specifics.
 * <p/>
 * This Executable interface can also function as a callback contract between
 * a primary "runnable" service and two or more observing objects. The 
 * three methods supply well defined synchronization points between the
 * running process and the observers.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Executable
{
    void doEnter(Harness harness);
    void doLeave(Harness harness);
    void doError(Harness harness, Throwable issue);
}


/* end-of-Executable.java */
