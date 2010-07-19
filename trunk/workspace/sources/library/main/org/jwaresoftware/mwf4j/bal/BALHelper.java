/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.reveal.Identified;
import  org.jwaresoftware.gestalt.reveal.Named;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.LongLivedCondition;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.assign.SavebackException;
import  org.jwaresoftware.mwf4j.assign.SavebackVar;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Collection of various package-level utilities, reuse snippets, and constants.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl,helper
 **/

final class BALHelper
{
    static final ControlFlowStatement protect(Action owner, ControlFlowStatement statement)
    {
        if (!(statement instanceof ProtectedStatement)) {
            statement = new ProtectedStatement(owner,statement);
        }
        return statement;
    }


    static final String nameFrom(Object o, String fallbackName)
    {
        String name = fallbackName;
        if (o instanceof Named) {
            name = ((Named)o).getName();
        } else if (o instanceof Identified) {
            name = ((Identified)o).getId();
        }
        return name;
    }



    static final void psh(Exception error)
    {
        MDC.psh(MWf4J.MDCKeys.LATEST_ERROR,error);
    }

    static final void pop(Exception error)
    {
        MDC.pop(MWf4J.MDCKeys.LATEST_ERROR,Exception.class);
    }



    static final boolean putData(String key, Object data, StoreType how, Harness harness)
    {
        boolean done=true;
        switch (how) {
            case DATAMAP: {
                harness.getVariables().put(key,data); 
                break;
            }
            case THREAD: {
                MDC.put(key,data); 
                break;
            }
            case PROPERTY: {
                harness.getConfiguration().getOverrides().setString(key,String.valueOf(data)); 
                break;
            }
            case OBJECT: {
                new SavebackVar<Object>(harness.getVariables()).put(key,data); 
                break;
            }
            default: {
                done=false;
            }
        }
        return done;
    }

    static final boolean clrData(String key, StoreType how, Harness harness)
    {
        boolean done=true;
        switch (how) {
            case DATAMAP: {
                harness.getVariables().remove(key); 
                break;
            }
            case THREAD: {
                MDC.clr(key); 
                break;
            }
            case PROPERTY: {
                harness.getConfiguration().getOverrides().unsetProperty(key);
                break;
            }
            case OBJECT: {
                try {  new SavebackVar<Object>(harness.getVariables()).putNull(key);
                } catch(SavebackException anyX) {
                    if (Diagnostics.ForFlow.isInfoEnabled())
                        Diagnostics.ForFlow.catching(anyX);
                    done=false;
                }
                break;
            }
            default: {
                done=false;
            }
        }
        return done;
    }

 
    

    static final void activate(Condition test)
    {
        if (test instanceof LongLivedCondition) {
            ((LongLivedCondition)test).start(LocalSystem.currentTimeMillis());
        }
    }

    static final void activate(Condition test, long startTime)
    {
        if (test instanceof LongLivedCondition) {
            ((LongLivedCondition)test).start(startTime);
        }
    }



    static final ControlFlowStatement makeIterationOfBody(ControlFlowStatement parent, Harness harness, ControlFlowStatement bodyInstance, Action bodyFactory)
    {
        ControlFlowStatement bodyContinuation = bodyInstance;
        if (bodyFactory!=null) {
            try {
                MDC.pshHarness(parent,harness);
                bodyContinuation = bodyFactory.makeStatement(parent);
            } finally {
                MDC.popHarness(parent,harness);
            }
        }
        return bodyContinuation;
    }



    static final ControlFlowStatement makeInstanceOfBody(ControlFlowStatement source, Harness harness, ControlFlowStatement next, Action bodyFactory)
    {
        ControlFlowStatement bodyContinuation = next;
        if (bodyFactory!=null) {
            try {
                MDC.pshHarness(source,harness);
                bodyContinuation = bodyFactory.makeStatement(next);
            } finally {
                MDC.popHarness(source,harness);
            }
        }
        return bodyContinuation;
    }



    BALHelper() { }
}


/* end-of-BALHelper.java */
