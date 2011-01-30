/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;
 
/**
 * Starting implementation for custom 'work' action nodes that function as
 * their own statements. Basically an extension point is the continuation
 * that's executed by a "real" control flow statement. Extension points
 * contain the application or service logic outside of MWf4J and is an
 * alternative to callables and futures if you want to maintain the simple
 * Action vs Statement interface to the world.
 * <p/>
 * Extension points return themselves ('this') from the standard 
 * 'makeStatement' factory method always. Subclasses need to implement the
 * inherited abstract method 'runInner' with their functionality. The 
 * inherited 'configure' method is made into a no-op as the action itself
 * contains all configuration details (nothing is passed on to an independent
 * statement). 
 * <p/>
 * Extension points are by definition <em>SINGLE USE</em> or safe for 
 * (re)use from a single thread of control. Because the 'next' continuation
 * attribute for an extension point will change for each reuse via
 * 'makeStatement' and internal states can change for 'runInner' you should
 * not use a single extension point instance from multiple threads. Therefore,
 * do not use extension points from containers like sequences and loops
 * that might create copies for use in forked or other multi-threaded cases.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single (including ALL subclasses)
 * @.group    infra,impl
 **/

public abstract class ExtensionPoint extends StatementSkeleton implements Action
{
    protected ExtensionPoint()
    {
        super(ControlFlowStatement.nullINSTANCE);
        initOwner(this);
    }

    protected ExtensionPoint(String id)
    {
        this();
        myId = Strings.trimToEmpty(id);
    }

    protected ExtensionPoint(ExtensionPoint other)//prototype friendly ctor
    {
        this();
        Validate.notNull(other,What.ACTION);
        setId(other.getId());
    }

    public String getId()
    {
        return myId;
    }

    protected void setId(String id)
    {
        Validate.notNull(id,What.ID);
        myId = Strings.trimToEmpty(id);
    }

    public void setOwner(Action owner)
    {
        Validate.stateIsTrue(owner==this, "owner==this");
        super.setOwner(owner);
    }

    public final ControlFlowStatement makeStatement(ControlFlowStatement next)
    {
        initNextStatement(next);
        verifyReady();
        return this;
    }

    public final void configure(ControlFlowStatement statement)//DON'T USE!
    {
        Validate.stateIsTrue(statement==this,"statement==this");
    }

    protected StringBuilder addToString(StringBuilder sb) 
    {
        return sb.append(Strings.THIS).append("/").append(getId());
    }


    private String myId= Strings.EMPTY;
}


/* end-of-ExtensionPoint.java */
