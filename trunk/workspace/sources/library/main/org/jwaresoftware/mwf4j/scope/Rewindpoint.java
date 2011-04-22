/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.reveal.Describable;
import  org.jwaresoftware.gestalt.reveal.Named;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Entity;
import org.jwaresoftware.mwf4j.behaviors.ControlFlowStatementDependent;

/**
 * Lightweight reference for a {@linkplain RewindCursor rewind cursor}. Used to
 * expose rewind points to application, for lookup, matching, etc.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Rewindpoint extends Entity, Named, Describable<String>, 
    ControlFlowStatementDependent
{
    boolean matches(Rewindpoint rwpoint);

    /** Null-proxy for a rewind point in MWf4J. **/
    public static final Rewindpoint nullINSTANCE= new Rewindpoint() {
        public ControlFlowStatement getOwner() {
            return ControlFlowStatement.nullINSTANCE;
        }
        public String getId() {
            return Strings.EMPTY;
        }
        public String getName() {
            return Strings.EMPTY;
        }
        public String getDescription() {
            return Strings.NODESC;
        }
        public String toString() {
            return getDescription();
        }
        public boolean matches(Rewindpoint rwpoint) {
            return rwpoint==this;
        }
    };
}


/* end-of-Rewindpoint.java */
