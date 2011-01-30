/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;


import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.behaviors.Protector;
import  org.jwaresoftware.mwf4j.starters.ActionSkeleton;

/**
 * Starting implementation for BAL related actions that also implement the
 * {@linkplain Protector} interface and optional error variable tracking.
 * Extracts the related fields into a {@linkplain ProtectorFields} struct
 * for easy passthru to statements (don't need to pass reference to 'this'
 * itself).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class BALProtectorAction extends ActionSkeleton implements Protector
{
    protected BALProtectorAction(String id)
    {
        super(id);
    }

    public final void setHaltIfError(boolean flag)
    {
        myProtectSupport.setHaltIfError(flag);
    }

    public final void setQuiet(boolean flag)
    {
        myProtectSupport.setQuiet(flag);
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myProtectSupport.setUseHaltContinuation(flag);
    }

    public final void setErrorKey(String key)
    {
        Validate.notBlank(key,What.KEY);
        myProtectSupport.setErrorKey(key);
    }

    public final void setErrorStoreType(StoreType storeType)
    {
        Validate.notNull(storeType,What.TYPE);
        myProtectSupport.setErrorStoreType(storeType);
    }

    protected final ProtectorFields myProtectSupport = new ProtectorFields();
}


/* end-of-BALProtectorAction.java */
