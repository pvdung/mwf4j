/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.messages.catalog.StandardMessages;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Rewind cursor that you can use to track loop indices, timestamps, and other
 * simple number-based cursors. Only the NUMBER is tracked by this cursor;
 * we expect all other context is maintained by the owning control statement.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public final class NumberRewindCursor extends RewindpointSkeleton implements RewindCursor
{
    public final static String nameFrom(ControlFlowStatement owner, int index)
    {
        String basename=null;
        if (owner!=null) {//eg 'Sequence@12','TryEachSequence@04'
            basename = What.typeCN(owner,"Statement");
        }
        return nameFrom(basename,index);
    }

    public final static String nameFrom(String basename, int index)
    {
        String name;
        if (basename!=null) {//eg 'handleOrder@12', 'Sequence@08'
            name = String.format("%1$s@%2$02d",basename,index);
        } else {
            name = "@"+index;
        }
        return name;
    }

    public NumberRewindCursor(ControlFlowStatement owner, Number number, String name)
    {
        super(owner,name);
        Validate.isA(owner,Rewindable.class,What.OWNER);
        myNumber = number;
    }

    public NumberRewindCursor(ControlFlowStatement owner, Number number)
    {
        this(owner,number,null);
    }

    public Rewindpoint getReadonlyView()
    {
        return new RewindpointWrap(this);
    }

    public ControlFlowStatement doRewind(Harness harness)
    {
        assert getOwner() instanceof Rewindable;//shutup findbugs
        return ((Rewindable)getOwner()).rewind(this,harness);
    }

    public Number getNumber()
    {
        return myNumber;
    }

    public Number getNumberOrFail()
    {
        Validate.fieldNotNull(myNumber,"number");
        return myNumber;
    }

    public final int getInt()
    {
        return getNumberOrFail().intValue();
    }

    public final long getLong()
    {
        return getNumberOrFail().longValue();
    }

    public int compareTo(long number)
    {
        long mynumber = myNumber.longValue();
        return (mynumber==number) ? 0 : ((mynumber>number) ? 1 : -1);
    }

    public int compareTo(RewindCursor other)
    {
        if (other==null)
            throw new NullPointerException();//Yik...but per Comparable contract!
        if (other instanceof NumberRewindCursor) {
            NumberRewindCursor othercursor = (NumberRewindCursor)other;
            return compareTo(othercursor.getLong());
        }
        String message = StandardMessages.IncompatibleTypeFound(other.getClass().getSimpleName());
        throw new ClassCastException(message);
    }

    private final Number myNumber;
}


/* end-of-NumberRewindCursor.java */
