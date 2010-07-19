/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Comparator;
import  java.util.Set;
import  java.util.TreeMap;

import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Pair;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Control flow statement that implements the classic 'try-catch-finally' form to 
 * allow application to capture and deal with exception conditions. This 
 * implementation lets you define multiple <em>distinct</em> error handler actions
 * (distinction by exception class) and a single always action.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class TryCatchStatement extends BALProtectorStatement
{
    enum Phase {BODY,ONERROR,ALWAYS,NEXT,ABORT};
 
    public TryCatchStatement(Action owner, ControlFlowStatement next) 
    {
        super(owner,next);
    }

    public void setBody(Action body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body.makeStatement(this);
    }

    public void addIfError(Set<Pair<Class<? extends Exception>,Action>> catchers)
    {
        Validate.notNull(catchers,What.CALLBACK);
        myCatchers = catchers;
        if (catchers.size()==1) {
            Pair<Class<? extends Exception>,Action> only = catchers.iterator().next();
            if (RuntimeException.class.equals(only.get1())) {
                myOnlyCatcher = only.get2();
            }
        }
    }

    public void setAlways(Action always)
    {
        Validate.notNull(always,What.ACTION);
        myAlways = always;
    }

    public void setUnmask(boolean flag)
    {
        myUnmaskFlag = flag;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement continuation=null;
        switch (myPhase) {
            case BODY: {
                continuation = harness.runParticipant(protect(bodyContinued));
                if (continuation instanceof ThrowStatement) {
                    pendingThrow = (ThrowStatement)continuation;
                    Action handler = getCaughtHandler(pendingThrow.getCause());
                    if (handler!=null) {
                        bestCatch = handler.makeStatement(this);
                        myPhase = Phase.ONERROR;
                        continuation = this;
                    } else {
                        bodyContinued = null;
                        myPhase = (myAlways!=null) ? Phase.ALWAYS : Phase.ABORT;
                        continuation = this;
                    }
                } else if (continuation!=this) {
                    bodyContinued = continuation;
                    continuation = this;
                } else {
                    myPhase = (myAlways!=null) ? Phase.ALWAYS : Phase.NEXT;
                }
                break;
            }
            case NEXT: {
                continuation = next();
                break;
            }
            case ONERROR: {
                assert bestCatch!=null : "ONERROR statement queued up";
                ControlFlowStatement onerror = errorContinued;
                if (onerror==null) {
                    onerror = bestCatch;
                }
                pshError(harness);
                try {
                    continuation = harness.runParticipant(protect(onerror));
                } finally {
                    popError(harness);
                }
                if (continuation instanceof ThrowStatement) {
                    saveUnexpectedError(continuation);
                    myPhase = (myAlways!=null) ? Phase.ALWAYS : Phase.ABORT;//ALWAYS ALWAYS
                    continuation = this;
                } else if (continuation!=this) {
                    errorContinued = continuation;
                    continuation = this;
                } else {
                    myPhase = (myAlways!=null) ? Phase.ALWAYS : Phase.ABORT;
                    assert continuation==this : "Next step after onerror is 'ME'";
                }
                break;
            }
           case ALWAYS: {
                assert myAlways!=null : "ALWAYS triggered iff there is an always";
                ControlFlowStatement always = alwaysContinued;
                if (always==null) {
                    always = myAlways.makeStatement(this);
                }
                continuation = harness.runParticipant(protect(always));
                if (continuation instanceof ThrowStatement) {
                    saveUnexpectedError(continuation);
                    myPhase = Phase.ABORT;
                    continuation = this;
                } else if (continuation!=this) {
                    alwaysContinued = continuation;
                    continuation = this;
                } else {
                    myPhase = (pendingThrow!=null) ? Phase.ABORT : Phase.NEXT;
                    assert continuation==this : "Next step after always is 'ME'";
                }
                break;
            }
            case ABORT: {
                assert pendingThrow!=null : "ABORT triggered iff there is a delayed (re)throw";
                unmaskedAllThrownIfWanted(pendingThrow);
                continuation = myTrySupport.handle(next(), pendingThrow, harness);
                break;
            }        
        }
        assert continuation!=null : "trycatch continuation defined non-null";
        return continuation;
    }

    private ControlFlowStatement protect(ControlFlowStatement statement)
    {
        return BALHelper.protect(getOwner(),statement);
    }

    final void resetThis()
    {
        myPhase = Phase.BODY;
        pendingThrow = null;
        bestCatch = null;
        alwaysContinued = null;
        bodyContinued = null;
        errorContinued = null;
        super.resetThis();
    }

    public void reconfigure() 
    {
        super.reconfigure();
        bodyContinued = myBody;
    }

    public void verifyReady()
    {
        super.verifyReady();
        Validate.stateNotNull(myBody,"try-catch body");
        Validate.stateIsTrue(myPhase==Phase.BODY, "phase=BODY");
    }

    private void pshError(Harness harness)
    {
        Exception ourError= unmask(pendingThrow.getCause());
        BALHelper.psh(ourError);
        if (myErrorKey!=null) {
            BALHelper.putData(myErrorKey,ourError,myErrorStoreType,harness);
        }
    }

    private void popError(Harness harness)
    {
        Exception ourError= pendingThrow.getCause();
        BALHelper.pop(ourError);
    }

    private void saveUnexpectedError(ControlFlowStatement continuation)
    {
        ThrowStatement newerThrown = (ThrowStatement)continuation;
        newerThrown.setNextThrown(pendingThrow);
        pendingThrow = newerThrown;
        myTrySupport.setHaltIfError(true);//FORCE-THIS!
    }

    static class ClassHierarchyComparator implements Comparator<Class<? extends Exception>>, java.io.Serializable
    {
        public int compare(Class<? extends Exception> o1, Class<? extends Exception> o2) {
            int cmp=0;
            if (o1.isAssignableFrom(o2)) {//Runtime > UnsupportedOperation (=> unsupported near 0)
                cmp= 1;
            } else if (o2.isAssignableFrom(o1)) {//?
                cmp= -1;
            }
            return cmp;
        }
        public boolean equals(Object other) {
            return (other instanceof ClassHierarchyComparator);
        }
        public int hashCode() {
            return ClassHierarchyComparator.class.getName().hashCode();
        }
    }

    private Action getCaughtHandler(final Exception generated)
    {
        if (myOnlyCatcher!=null) {
            return myOnlyCatcher;
        }
        if (myCatchers!=null) {
            final Class<? extends Exception> classOfGenerated = unmask(generated).getClass();
            TreeMap<Class<? extends Exception>,Action> possibles=null;
            for (Pair<Class<? extends Exception>,Action> next:myCatchers) {
                if (next.equals1(classOfGenerated)) {
                    return next.get2();
                }
                if (next.get1().isAssignableFrom(classOfGenerated)) {
                    if (possibles==null) {
                        possibles = new TreeMap<Class<? extends Exception>,Action>(new ClassHierarchyComparator());
                    }
                    possibles.put(next.get1(),next.get2());
                }
            }
            if (possibles!=null) {
                return possibles.firstEntry().getValue();//NB:most specific one
            }
        }
        return null;
    }

    private Exception unmask(Exception generated)
    {
        Exception unmasked = generated;
        Throwable cause = null;
        if (generated instanceof Throwables.CheckedWrapper/*ALWAYS*/ || myUnmaskFlag) {
            cause = Throwables.getCauseIfVMWrapper(generated,myUnmaskFlag);
        }
        if (cause instanceof Exception) {
            unmasked = (Exception)cause;
        }
        return unmasked;
    }

    private void unmaskedAllThrownIfWanted(ThrowStatement thrown)
    {
        if (myUnmaskFlag) {
            do {
                Exception cause = thrown.getCause();
                Exception unmasked = unmask(cause);
                if (unmasked!=cause) {
                    thrown.setCause(unmasked);
                }
            } while ((thrown=thrown.nextThrown())!=null);
        }
    }

    protected StringBuilder addToString(StringBuilder sb) 
    {
        char caught= pendingThrow!=null ? 'Y' : 'N';
        return super.addToString(sb).append("|phase=").append(myPhase).append("|caught=").append(caught);
    }


    private ControlFlowStatement myBody;//REQUIRED
    private Set<Pair<Class<? extends Exception>,Action>> myCatchers;//OPTIONAL
    private Action myAlways;//OPTIONAL
    private Action myOnlyCatcher;//NB:shortcut for common case of just one catch action!
    private boolean myUnmaskFlag = false;

    //These are updated as we proceed thru the try-catch-finally sequence of actions ---------
    private Phase myPhase=Phase.BODY;
    private ControlFlowStatement bodyContinued, alwaysContinued, errorContinued;
    private ControlFlowStatement bestCatch;
    private ThrowStatement pendingThrow;
}


/* end-of-TryCatchStatement.java */
