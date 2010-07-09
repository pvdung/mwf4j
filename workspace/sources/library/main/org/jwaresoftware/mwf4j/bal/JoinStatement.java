/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.concurrent.atomic.AtomicInteger;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Resettable;

/**
 * ---- (( INSERT DOCUMENTATION )) ----
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 **/

public class JoinStatement extends BALStatement implements Resettable
{
    public enum Mode { ALL, ANY };

    public JoinStatement(Action owner, ControlFlowStatement next)
    {
        super(owner,next);
    }

    public void setBody(Action body)
    {
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    public void setNumberParticipants(int count)
    {
        Validate.isTrue(count>0,"parties.size > 0");
        myNumParticipants = count;
    }

    public void setMode(Mode mode)
    {
        Validate.notNull(mode,What.CRITERIA);
        myMode = mode;
    }

    protected synchronized ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement next = null;
        int completions = myCompletions.incrementAndGet();
        if (completions==myReleaseNum) {
            next = myBody.makeStatement(next());
            breadcrumbs().write("Release criteria met for join '{}' [num={}]",getWhatId(),myReleaseNum);
        } else {
            next = new EndStatement();
        }
        if (completions==myNumParticipants) {
            breadcrumbs().write("All participants completed for join '{}'",getWhatId());
        }
        return next;
    }

    private void resetThis()
    {
        myMode = Mode.ALL;
        myNumParticipants = 0;
        myReleaseNum = -1;
        myCompletions.set(0);
        myBody = null;
    }

    public void reconfigure()
    {
        reset();
        super.reconfigure();
        verifyReady();
        if (Mode.ALL.equals(myMode)) {
            myReleaseNum = myNumParticipants;
        } else {
            myReleaseNum = 1;
        }
    }

    public void reset()
    {
        resetThis();
    }

    protected void verifyReady()
    {
        super.verifyReady();
        Validate.fieldNotNull(myBody,"join continuation");
        Validate.stateIsTrue(myNumParticipants>0, "participants.size>0");
    }

    protected void verifyNotBroken(Harness harness)
    {
        if (myBrokenKey!=null) {
            Boolean flag = harness.getVariables().getFlag(myBrokenKey);
            if (Boolean.TRUE.equals(flag)) {
                
            }
        }
    }

    private Mode myMode= Mode.ALL;
    private int myNumParticipants;
    private int myReleaseNum;
    private Action myBody;
    private AtomicInteger myCompletions= new AtomicInteger();
    private String myBrokenKey;
}


/* end-of-JoinStatement.java */
