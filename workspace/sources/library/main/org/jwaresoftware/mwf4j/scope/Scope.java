/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  java.util.Set;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.reveal.Named;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDependent;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.behaviors.Executable;

/**
 * Shell that describes a local scope of execution for one or more control
 * flow statements. A scope is OWNED by one and only one statement. Typical
 * properties and/or functions associated with a scope include unwindables
 * (for error handling), rewindables (for redo support), and transient fixture
 * overrides (for local variable support).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    infra,impl,helper
 **/

public interface Scope extends Named, Executable, ControlFlowStatementDependent
{
    String getName();
    void addUnwind(Unwindable participant);
    void removeUnwind(Unwindable participant);
    void doUnwind(Harness harness);

    void addRewindpoint(RewindCursor cursor);
    void removeRewindpoint(RewindCursor cursor);
    Set<Rewindpoint> copyOfRewindpoints();
    ControlFlowStatement doRewind(Rewindpoint marker, Harness harness);


    /** Null proxy for a MWf4J scopes. **/
    public static final Scope nullINSTANCE= new Scope()
    {
        public String getName() {
            return Strings.EMPTY;
        }
        public ControlFlowStatement getOwner() {
            return ControlFlowStatement.nullINSTANCE;
        }
        public void doEnter(Harness harness) {
        }
        public void doLeave(Harness harness) {
        }
        public void addUnwind(Unwindable participant) {
        }
        public void removeUnwind(Unwindable participant) { 
        }
        public void doUnwind(Harness harness) {
        }
        public String toString() {
            return Strings.ND;
        }
        public void addRewindpoint(RewindCursor cursor) {
        }
        public void removeRewindpoint(RewindCursor cursor) {
        }
        public Set<Rewindpoint> copyOfRewindpoints() {
            return Empties.newSet();
        }
        public ControlFlowStatement doRewind(Rewindpoint marker, Harness harness) {
            return ControlFlowStatement.nullINSTANCE;
        }
    };
}


/* end-of-Scope.java */
