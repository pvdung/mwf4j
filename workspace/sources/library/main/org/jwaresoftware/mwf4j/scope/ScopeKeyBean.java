/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.StatementDependentSkeleton;

/**
 * Simple POJO implementation of the {@linkplain ScopeKey} interface. Note
 * that a key's owning statement must be non-NULL but its linked scope is
 * optional.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public class ScopeKeyBean extends StatementDependentSkeleton implements ScopeKey
{
    public ScopeKeyBean(ControlFlowStatement owner) 
    {
        super(owner);
        Validate.notNull(owner,What.STATEMENT);
    }

    public Object clone() 
    {
        try {
            return super.clone();
        } catch(CloneNotSupportedException clnX) {
            throw new InternalError();
        }
    }

    public Scope getScope()
    {
        return myScope;
    }

    public void setScope(Scope block)
    {
        myScope = block;
    }

    public boolean equals(Object other) 
    {
        if (other==null)
            return false;
        if (other==this)
            return true;
        if (other.getClass().equals(getClass())) {
            Object otherOwner = ((ScopeKey)other).getOwner();
            return getOwner().equals(otherOwner);
        }
        return false;
    }

    public int hashCode()
    {
        return getOwner().hashCode();
    }

    private Scope myScope;//OPTIONAL
}


/* end-of-ScopeKeyBean.java */
