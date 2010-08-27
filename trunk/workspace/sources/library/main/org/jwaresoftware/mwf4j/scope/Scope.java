/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.reveal.Named;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.behaviors.Executable;

/**
 * Shell that describes a local scope of execution for one or more control
 * flow statements. A scope is OWNED to one and only one statement. Typical
 * properties and/or functions associated with a scope include unwindables
 * (for error handling), rewindables (for redo support), and transient fixture
 * overrides.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    infra,impl,helper
 **/

public interface Scope extends Named, Executable
{
    String getName();
    void addUnwind(Unwindable participant);
    void removeUnwind(Unwindable participant);
    void unwindAll(Harness harness);

    /** Null proxy for a MWf4J scope. **/
    public static final Scope nullINSTANCE= new Scope()
    {
        public void doEnter(Harness harness) {
        }
        public void doLeave(Harness harness) {
        }
        public String getName() {
            return Strings.EMPTY;
        }
        public void addUnwind(Unwindable participant) {
        }
        public void removeUnwind(Unwindable participant) { 
        }
        public void unwindAll(Harness harness) {
        }
        public String toString() {
            return Strings.ND;
        }
    };
}


/* end-of-Scope.java */
