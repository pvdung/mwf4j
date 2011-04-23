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
import  org.jwaresoftware.mwf4j.behaviors.ControlFlowStatementDependent;

/**
 * Lightweight reference for a {@linkplain RewindCursor rewind cursor}. Used to
 * expose rewind points to application, for lookup, matching, etc. Note that a 
 * rewindpoint's id is not necessarily the same as its name. Whereas an
 * <em>active</em> rewindpoint's id should be unique within its harness'
 * context, its name does not have to be (so you can, for example, have two
 * distinct rewindpoints that point to the same named thing). You can use a
 * rewindpoint's {@linkplain #ofType() ofType} value to make different rewindpoint
 * implementations represent a single underlying source type. Usually the
 * ofType is the same as the rewindpoint's own class (see {@linkplain RewindpointWrap}
 * for an example of a different approach).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra,helper
 **/

public interface Rewindpoint extends Entity, Named, Describable<String>, 
    ControlFlowStatementDependent
{
    boolean matches(Rewindpoint rwpoint);
    Class<?> ofType();

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
        public Class<?> ofType() {
            return getClass();
        }
    };
}


/* end-of-Rewindpoint.java */
