/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.system.Defaults;
import  org.jwaresoftware.gestalt.system.LocalDefaults;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Service facade to BAL-specific configuration defaults. If you never need
 * to change the BAL defaults wholesale, you will never use the provider
 * interface defined here. If you do want to change a base setting, you can
 * install your own implementation of the provider interface <em>before you
 * use any of the BAL-related functionality (including builders defined in
 * the advanced package)</em>.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @.impl     Needs completion: create local class instance that implements
 *            our default values. Point local INSTANCE to that. Use static
 *            API to wrap calls to INSTANCE.xxx methods. Create two methods
 *            setServiceProvider and unsetServiceProvider for application
 *            control.
 **/

public final class BAL
{
    /** Maximum number of times in-a-row that you can call and End statement
     *  or adjustment before it signals a potential infinite loop error
     *  condition. Equals {@value}.*/
    public static final int MAX_END_LOOPS = 5;


    public interface DefaultsSPI extends Defaults
    {
        StoreType getDataStoreType();
        String getCursorKey(String eid);
        StoreType getCursorStoreType();
        boolean getNewStatementPerLoopFlag();
        boolean getUseHaltContinuationsFlag();
        String getListsDelimiter();
        String getNumberCursorNameTemplate();
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

    public final static String getListsDelimiter(Fixture environ, String fallback)
    {
        String output=null;
        if (environ!=null) {
            output = environ.getConfiguration().getString("lists.delimiter");
        }
        if (output==null) {
            output = LocalDefaults.getListsDelimiter(fallback);
        }
        return output;
    }

    public final static String getListsDelimiter(Fixture environ)
    {
        return getListsDelimiter(environ,LocalDefaults.DEFAULT_DELIMITER);
    }
}


/* end-of-BAL.java */
