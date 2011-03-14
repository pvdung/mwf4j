/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  java.lang.reflect.Constructor;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;

/**
 * Test action that unconditionally returns an instance of a user-supplied 
 * statement class. Assumes there is a public constructur for statement class
 * that takes an action and control flow statement reference. Target statement
 * must be enterly defined by this single constructor.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    helper,test
 **/

public final class UnknownAction extends ActionSkeleton
{
    public UnknownAction(String id, Class<? extends ControlFlowStatement> statementClass)
    {
        super(id);
        Validate.notNull(statementClass, "statement-class");
        myStatementClass = statementClass;
        try {
            myCtor = statementClass.getConstructor(Action.class,ControlFlowStatement.class);
        } catch(Exception sigX) {
            throw new Error("Unable to locate <init>(Action,ControlFlowStatement)",sigX);
        }
    }

    public void configureStatement(ControlFlowStatement gen, Fixture environ)
    {
        Validate.isTrue(myStatementClass.isInstance(gen), "statement kindof "+myStatementClass.getSimpleName());
    }

    protected ControlFlowStatement createStatement(ControlFlowStatement next, Fixture environ)
    {
        try {
            return myCtor.newInstance(this,next);
        } catch(Exception ctorX) {
            throw new Error("Unable to create new instance of "+myStatementClass.getSimpleName(),ctorX);
        }
    }

    private Class<? extends ControlFlowStatement> myStatementClass;
    private Constructor<? extends ControlFlowStatement> myCtor;
}


/* end-of-UnknownAction.java */
