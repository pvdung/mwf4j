/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.assign.Reference;
import  org.jwaresoftware.mwf4j.behaviors.Protector;

/**
 * Starting implementation for BAL related control flow statements that 
 * also implement the {@linkplain Protector} interface using a common
 * {@linkplain TrySupport} helper instance. For application-public protectable
 * statements (not statements that do protection as implementation detail
 * only).
 * <p/>
 * Implementation note: this abstract class defines a Resettable compatible
 * reset method at the protected level. If a subclass wants to be publicly
 * resettable it can implement the interface and promote the inherited
 * method to the public visiblity level.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,helper
 **/

public abstract class BALProtectorStatement extends BALStatement implements Protector
{
    protected BALProtectorStatement(ControlFlowStatement next)
    {
        super(next);
        myTrySupport = new TrySupport(Action.anonINSTANCE);
    }

    protected BALProtectorStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
        myTrySupport = new TrySupport(getOwner());
    }

    public final void setHaltIfError(boolean flag)//NB: covers timeout & interruption & retries
    {
        myTrySupport.setHaltIfError(flag);
    }

    public final void setQuiet(boolean flag)
    {
        myTrySupport.setQuiet(flag);
    }

    public final void setUseHaltContinuation(boolean flag)
    {
        myTrySupport.setUseContinuation(flag);
    }

    public final void setError(Reference ref)
    {
        if (ref==null) {
            myError.reset();
        } else {
            myError.copyFrom(ref);
        }
    }

    void resetThis() //NB: for inherited use only within BAL
    {
        myTrySupport.reset();
        myError.reset();
    }

    protected void reset()
    {
        resetThis();
    }

    public void reconfigure()
    {
        reset();
        super.reconfigure();
        verifyReady();
    }

    public void copyFrom(BALProtectorStatement from)
    {
        Validate.notNull(from,What.STATEMENT);
        myTrySupport.copyFrom(from.myTrySupport);
        myError.copyFrom(from.myError);
    }

    public void copyFrom(ProtectorFields from)
    {
        Validate.notNull(from,What.ACTION);
        myTrySupport.copyFrom(from);
        myError.copyFrom(from.errorKey,from.errorStoreType);
    }

    protected final TrySupport myTrySupport;
    protected final Reference myError= new Reference();//OPTIONAL
}


/* end-of-BALProtectorStatement.java */
