/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.ServiceProviderException;
import  org.jwaresoftware.gestalt.Strings;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.starters.StatementSkeleton;

/**
 * Statement that just um 'barfs' when run. You're allowed to
 * specify a 'barf-message'. Useful for forcing error conditions.
 * A barf statement is not terminal by the way-- it just would
 * never get to a point where the running harness receives 
 * the next statement to perform (due to the barfage).
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple (once setup)
 * @.group    impl,helper
 **/

public class BarfStatement extends StatementSkeleton
{
    public final static String DEFAULT_MESSAGE= "BWAHAHAHA!";

    public BarfStatement()
    {
        super(null);
    }

    public BarfStatement(Action owner)
    {
        super(null);
        initOwner(owner);
    }

    public BarfStatement(Action owner, String message)
    {
        this(owner);
        setMessage(message);
    }
    
    public BarfStatement(Action owner, ControlFlowStatement next)//For use with UnknownAction!
    {
        super(next);
        initOwner(owner);
    }

    public BarfStatement(String message)
    {
        this();
        setMessage(message);
    }

    protected final void initOwner(Action owner)
    {
        myOwner = (owner!=null) ? owner : Action.anonINSTANCE;
    }

    public void setMessage(String message)
    {
        myFeedback = Strings.trimToEmpty(message);
    }

    public String getMessage()
    {
        return myFeedback;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        throw new ServiceProviderException(myFeedback);
    }

    public Action getOwner()
    {
        return myOwner;
    }

    public boolean isAnonymous()
    {
        Action a = getOwner();
        return (a==null || a==Action.anonINSTANCE);
    }

    private Action myOwner= Action.anonINSTANCE;
    private String myFeedback = DEFAULT_MESSAGE;
}


/* end-of-BarfStatement.java */
