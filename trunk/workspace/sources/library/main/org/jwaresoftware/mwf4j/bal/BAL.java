/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Service facade to factories for all BAL-known actions and activity types. 
 * This facade gives us the benefit (mostly) of interface factories (which can
 * be intercepted with proxies) while keeping "lookup" boilerplate and
 * container-specific code out of the main library source.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public final class BAL
{
    public interface ConfigSPI
    {
        StoreType getDataStoreType();
        String getCursorKey(String eid);
        StoreType getCursorStoreType();
        boolean getMakeStatementPerLoopFlag();
        boolean getUseHaltContinuationsFlag();
    }

    public final static StoreType getDataStoreType()
    {
        return StoreType.DATAMAP;//Flat assignment to variables!
    }

    public final static String getCursorKey(String eid)
    {
        return MWf4J.getCursorKey(eid);
    }

    public final static StoreType getCursorStoreType()
    {
        return StoreType.DATAMAP;//Flat assignment into variables!
    }
    
    public final static boolean getNewStatementPerLoopFlag()
    {
        return true;//Safest option- copy per iteration!
    }

    public final static boolean getUseHaltContinuationsFlag()
    {
        return false;//Allow natural 'unwind' process to occur!
    }
}


/* end-of-BAL.java */
