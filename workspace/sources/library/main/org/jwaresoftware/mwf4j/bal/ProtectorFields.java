/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.StoreType;
import  org.jwaresoftware.mwf4j.behaviors.Protector;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;

/**
 * Common struct definition of fields used to track a {@linkplain Protector}
 * implementation. 
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public class ProtectorFields implements Protector, Resettable
{
    public boolean haltIfErrorFlag;
    public boolean quietFlag;
    public boolean haltContinuationFlag;
    public String errorKey;
    public StoreType errorStoreType;

    public ProtectorFields()
    {
        resetThis();
    }

    public ProtectorFields(ProtectorFields from)
    {
        Validate.notNull(from,What.SOURCE);
        initThisFrom(from);
    }

    public void copyTo(ProtectorFields to)
    {
        Validate.notNull(to,What.TARGET);
        to.initThisFrom(this);
    }

    public void copyTo(Protector to)
    {
        Validate.notNull(to,What.TARGET);
        to.setHaltIfError(haltIfErrorFlag);
        to.setQuiet(quietFlag);
        to.setUseHaltContinuation(haltContinuationFlag);
    }

    public void copyFrom(ProtectorFields from)
    {
        Validate.notNull(from,What.SOURCE);
        initThisFrom(from);
    }

    public void reset()
    {
        resetThis();
    }

    public final void setHaltIfError(boolean flag)
    {
        haltIfErrorFlag = flag;
    }

    public final void setQuiet(boolean flag)
    {
        quietFlag = flag;
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        haltContinuationFlag = !flag;
    }

    public final void setErrorKey(String string)
    {
        errorKey = string;
    }

    public final void setErrorStoreType(StoreType storeType)
    {
        errorStoreType = storeType;
    }

    public final void setErrorKey(String string, StoreType storeType)
    {
        errorKey = string;
        errorStoreType = storeType;
    }

    private void initThisFrom(ProtectorFields from)
    {
        haltIfErrorFlag = from.haltIfErrorFlag;
        quietFlag = from.quietFlag;
        haltContinuationFlag = from.haltContinuationFlag;
        errorKey = from.errorKey;
        errorStoreType = from.errorStoreType;
    }

    private void resetThis()
    {
        haltIfErrorFlag = true;
        quietFlag = false;
        haltContinuationFlag = BAL.getUseHaltContinuationsFlag();
        errorKey = null;
        errorStoreType = BAL.getDataStoreType();
    }
}


/* end-of-ProtectorFields.java */
