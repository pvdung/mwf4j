/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Markable;
 
/**
 * Starting implementation for custom application work adapted to MWf4J
 * action and statement interfaces. Basically an extension point is a 
 * your custom logic and computation that's executed by one of the simple 
 * pre-canned control flow statements as a continuation. Extension points
 * are an alternative to callables and futures if you prefer to integrate 
 * your service logic directly into the MWf4J framework.
 * <p/>
 * Extension points are actions that return themselves ("this") from 
 * the standard {@code buildStatement} factory method. To create a usable
 * extension point, you must implement the inherited abstract method 
 * {@code runInner} with your functionality. The inherited statement
 * {@code reconfigure} method for extension points are a no-op as the action 
 * itself contains all configuration details (nothing is passed on to an 
 * independent statement). 
 * <p/>
 * <b>WARNING:</b> By definition, extension points are <em>single use</em>
 * actions. Because a continuation statement is <em>supplied to</em> an 
 * extension point (via its action {@code buildStatement}), you cannot reuse
 * a single instance across multiple (possible concurrent) calls to {@code buildStatement},
 * as each call alters the internal state of the one extension point. 
 * Therefore, do not use extension points from containers like sequences 
 * or loops that might reasonably expect to use actions in forked or other 
 * multi-threaded flows. To be safe, you should even avoid (re)using a 
 * single extension point multiple times within a single threaded activity.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single (including ALL subclasses)
 * @.group    infra,impl
 * @see       ActionSkeleton
 **/

public abstract class ExtensionPoint extends StatementSkeleton implements Action, Markable
{
    protected ExtensionPoint()
    {
        super(ControlFlowStatement.nullINSTANCE);
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

    @Override
    public void setId(String id)
    {
        Validate.notNull(id,What.ID);
        myId = Strings.trimToEmpty(id);
    }

    public String getWhatId()
    {
        String wid = super.getWhatId();
        return Strings.isEmpty(wid) ? getId() : getId()+"/"+wid;
    }


    public final ControlFlowStatement buildStatement(ControlFlowStatement next, Fixture environ)
    {
        initNextStatement(next);
        if (isCheckDeclarables()) {
            doFreeze(environ);
        }
        verifyReady();
        return this;
    }

    public final void configureStatement(ControlFlowStatement statement, Fixture environ)//DON'T USE!
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
