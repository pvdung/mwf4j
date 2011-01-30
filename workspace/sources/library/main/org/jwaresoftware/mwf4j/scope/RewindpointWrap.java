/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Readonly view that enforces access to ONLY the Rewindpoint API.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra,helper
 **/

public final class RewindpointWrap implements Rewindpoint
{
    public RewindpointWrap(Rewindpoint rwpoint) {
        Validate.notNull(rwpoint,What.SOURCE);
        myImpl = rwpoint;
    }
    public ControlFlowStatement getOwner() {
        return myImpl.getOwner();
    }
    public String getId() {
        return myImpl.getId();
    }
    public String getName() {
        return myImpl.getName();
    }
    public String getDescription() {
        return myImpl.getDescription();
    }
    public String toString() {
        return myImpl.toString();
    }
    public boolean matches(Rewindpoint rwpoint) {
        return myImpl.matches(rwpoint);
    }
    public boolean equals(Object other) {
        return myImpl.equals(other);
    }
    public int hashCode() {
        return myImpl.hashCode();
    }

    private final Rewindpoint myImpl;
}


/* end-of-RewindpointWrap.java */
