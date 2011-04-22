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
 * Starting implementation for an {@linkplain Action}. Tracks the action's id
 * and adds a template implementation for {@linkplain #buildStatement}.
 * Subclasses should provide implementation details for {@linkplain #createStatement}
 * and {@linkplain Action#configureStatement configureStatement}. And,
 * optionally, subclasses can fillin {@linkplain #verifyReady} which is
 * called before the new statement's reconfigure method is triggered.
 * <p/>
 * <b>Usage note 1:</b> If a factory or builder wants to selectively activate
 * the "declarables support" option for an action skeleton instance, it needs
 * to turn on/off the flag <em>BEFORE</em> an other options are set. So for
 * instance, call {@code setCheckDeclarables} after constructor is called. This 
 * restriction reduces the amount of "copy" overhead that subclasses need to
 * worry about; for example, if declarables are not part of your application,
 * the action subclasses do not need to clone incoming parameter objects to
 * ensure they're independent copies that can be frozen based on the harness
 * setup at a later time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public abstract class ActionSkeleton extends DeclarableSupportSkeleton implements Action, Markable
{
    protected ActionSkeleton()
    {
        super();
    }

    protected ActionSkeleton(String id)
    {
        myId = Strings.trimToEmpty(id);
    }


    public String getId()
    {
        return myId;
    }

    @Override
    public final void setId(String id)
    {
        Validate.notNull(id,What.ID);
        myId = Strings.trimToEmpty(id);
    }

    public ControlFlowStatement buildStatement(ControlFlowStatement next, Fixture environ)
    {
        maybeConfigure(environ);
        verifyReady();
        ControlFlowStatement statement = createStatement(next,environ);
        Validate.resultNotNull(statement,What.STATEMENT);
        statement.reconfigure(environ,this);
        return statement;
    }


    /**
     * Factory method to create the specific type of statement this action
     * describes. Any subclass that uses the standard buildStatement template
     * method <em>must</em> override this method. The method is not abstract
     * so that subclasses that do not use the standard buildStatement template
     * are not forced to implement this method (to do what we do here).
     * @param next continuation (non-null)
     * @param environ fixture from which you retrieve configuration if needed (non-null)
     * @return new statement -- NEVER null!
     * @throws UnsupportedOperationException always if called
     **/
    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        throw new UnsupportedOperationException("actionSkeleton.createStatement");
    }


    protected void maybeConfigure(Fixture environ)
    {
        //nothing by default; extend to extract settings from environ (which => single thread)
    }


    protected void verifyReady()
    {
        //nothing by default; extend to ensure all required attributes defined!
    }


    public String toString()
    {
        return What.subidFor(this,"Action",getId());
    }


    private String myId= Strings.EMPTY;
}


/* end-of-ActionSkeleton.java */
