/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Synonyms;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;
import  org.jwaresoftware.mwf4j.helpers.Declarables;

/**
 * Condition that checks if a named item exists within the context of a 
 * running harness. The "context" might be the harness's variables map,
 * its configuration, the local system, or something else. Basically
 * any information you can get to via a {@linkplain Giveback Giveback} 
 * interface.
 * <p/>
 * Any unhandled exception the giveback signals is wrapped by a outer
 * {@linkplain GivebackException}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras,helper
 **/

public final class EvaluateVar extends CloneableSkeleton implements Condition, Declarable
{
    public interface ResultTest 
    {
        boolean evaluate(Object result);
    }
    
    public static final ResultTest IsNull = new ResultTest() {
        public boolean evaluate(Object result) {
            return result==null;
        }
    };

    public static final ResultTest NotNull = new ResultTest(){
        public boolean evaluate(Object result) {
            return result!=null;
        }
    };

    public static final ResultTest IsTrue = new ResultTest() {
        public boolean evaluate(Object result) {
            return Boolean.TRUE.equals(Synonyms.Booleans.query(result));
        }
    };

    public static final ResultTest IsFalse = new ResultTest() {
        public boolean evaluate(Object result) {
            return Boolean.FALSE.equals(Synonyms.Booleans.query(result));
        }
    };

    public EvaluateVar(Giveback<?> getmethod, ResultTest test)
    {
        Validate.neitherNull(getmethod,What.GET_METHOD,test,What.CRITERIA);
        myGetMethod = getmethod;
        myTest = test;
    }

    public EvaluateVar(String keyOrExpr, ResultTest test)
    {
        this(GivebackVar.fromEvalOfOptional(keyOrExpr,Object.class),test);
    }

    public boolean evaluate(Harness harness)
    {
        try {
            return isPass(harness);
        } catch(GivebackException getX) {
            throw getX;
        } catch(Exception unXpected) {
            throw new GivebackException(unXpected);//for badly written giveback!
        }
    }

    public void freeze(Fixture environ)
    {
        Declarables.freezeAll(environ,myGetMethod,myTest);
    }

    private boolean isPass(Harness harness) throws Exception
    {
        try {
            MDC.pshHarness(this,harness);
            return myTest.evaluate(myGetMethod.call());
        } finally {
            MDC.popHarness(this,harness);
        }
    }

    public Object clone()
    {
        EvaluateVar copy = (EvaluateVar)super.clone();
        copy.myGetMethod = LocalSystem.newCopyOrSame(myGetMethod);
        copy.myTest = LocalSystem.newCopyOrSame(myTest);
        return copy;
    }

    private Giveback<?> myGetMethod;
    private ResultTest myTest;
}


/* end-of-EvaluateVar.java */
