/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  java.rmi.server.UID;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.starters.StatementDependentSkeleton;

/**
 * Starting point for simple POJO implementations of the {@linkplain Rewindpoint}
 * interface. Implements the tracking of the string attributes (id, name) and
 * the owner statement reference. Also implements matching and equality based
 * on the rewindpoint's identifier.
 * <p/>
 * If you do not assign an identifier explicitly, this class will use the standard
 * {@linkplain UID} class to generate a VM-unique identifier that is combined with
 * the owning statement's class leaf name.
 * <p/>
 * To create a "rewindpoint matcher" you can extend this class and override the
 * {@linkplain #matches(Rewindpoint) matches} method to do a custom comparison
 * that does not rely on the matcher's own identifier (nonsensical in this use
 * case). You should also use the void constructor to avoid including unused
 * information in this matcher's id.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra
 * @see       RewindpointWrap
 **/

abstract class RewindpointSkeleton extends StatementDependentSkeleton implements Rewindpoint
{
    protected RewindpointSkeleton(ControlFlowStatement owner, String id, String name)
    {
        super(owner);
        if (id==null)
            id = String.valueOf(new UID());
        Validate.notBlank(id,What.ID);
        myCrTimeNanos = LocalSystem.currentTimeNanos();
        myId = What.subidFor(owner,"Statement")+"|"+id;//FORCE incl owner id
        myName = (name==null) ? id : name;
    }

    protected RewindpointSkeleton(ControlFlowStatement owner)
    {
        this(owner,null,null);
    }

    protected RewindpointSkeleton(ControlFlowStatement owner, String name)
    {
        this(owner,null,name);
    }

    protected RewindpointSkeleton(String id)
    {
        super(ControlFlowStatement.nullINSTANCE);
        if (id==null)
            id = String.valueOf(new UID());
        myId = id;
        myCrTimeNanos = LocalSystem.currentTimeNanos();
        myName = id;
    }

    public final String getId()
    {
        return myId;
    }

    public final String getName()
    {
        return myName;
    }

    public String getDescription()
    {
        return getName();
    }

    public String toString()
    {
        return getDescription();
    }

    public boolean matches(Rewindpoint mark)
    {
        if (mark==null) return false;
        if (mark==this) return true;
        return Strings.equal(getId(),mark.getId());
    }

    public boolean equals(Object other)
    {
        if (other==null) return false;
        if (other==this) return true;
        if (getClass().equals(other.getClass())) {
            Rewindpoint otherrwp = (Rewindpoint)other;
            return Strings.equal(getId(),otherrwp.getId());
        }
        return false;
    }

    public int hashCode()
    {
        return getId().hashCode();
    }

    protected final long getCreationNanoTime()
    {
        return myCrTimeNanos;
    }

    private String myId, myName;
    private final long myCrTimeNanos;
}


/* end-of-RewindpointSkeleton.java */
