/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Memento to describe context of a rewind point for a given statement. A
 * rewindable statement can register one or more rewind cursors with its
 * active scope so that the controlling service or application can gain
 * access for selection. Note that <em>technically</em> a control flow 
 * statement can support rewinding without exposing its cursors; however,
 * such an implementation is proprietary as none of the existing rewind-aware
 * components would know how to trigger it.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra,helper
 * @see       Scopes#rewindFrom(Rewindpoint, Harness) Scopes.rewindFrom()
 **/

public interface RewindCursor extends Rewindpoint, Comparable<RewindCursor>
{
    ControlFlowStatement doRewind(Harness harness);
    Rewindpoint getReadonlyView();

    /** Null-proxy for a rewind cursor in MWf4J. **/
    public static final RewindCursor nullINSTANCE= new RewindCursor() {
        public Rewindpoint getReadonlyView() {
            return Rewindpoint.nullINSTANCE;
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
        public ControlFlowStatement getOwner() {
            return Rewindpoint.nullINSTANCE.getOwner();
        }
        public ControlFlowStatement doRewind(Harness harness) {
            return getOwner();
        }
        public boolean matches(Rewindpoint rwpoint) {
            return rwpoint==this || rwpoint==Rewindpoint.nullINSTANCE;
        }
        public int compareTo(RewindCursor other) {
            if (other==null)
                throw new NullPointerException();
            return other==this ? 0 : -1;
        }
        public Class<?> ofType() {
            return getClass();
        }
    };
}


/* end-of-RewindCursor.java */
