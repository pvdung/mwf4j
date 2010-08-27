/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.ControlFlowStatementDependent;

/**
 * Per-thread unique key for a scope-generating statement. Used to determine
 * if a scope already exists for a particular statement. A scope key's
 * linked scope <em>has no bearing on the key itself</em>; only the statement
 * does. It's even permitted (though not be our default implementation) for
 * a single key to have different linked scopes (or none at all) during its
 * lifetime.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public interface ScopeKey extends ControlFlowStatementDependent, Cloneable
{
    ControlFlowStatement getOwner();
    Object clone();
    boolean equals(Object other);
    Scope getScope();
    void setScope(Scope block);

    public static final ScopeKey nullINSTANCE= new ScopeKey() {
        public Object clone() {
            return this;
        }
        public boolean equals(Object other) {
            return other==this;
        }
        public int hashCode() {
            return getClass().hashCode();
        }
        public ControlFlowStatement getOwner() {
            return ControlFlowStatement.nullINSTANCE;
        }
        public String toString() {
            return Strings.ND;
        }
        public Scope getScope() {
            return Scope.nullINSTANCE;
        }
        public void setScope(Scope block) {
            throw new UnsupportedOperationException("setScope");
        }
    };
}


/* end-of-ScopeKey.java */
