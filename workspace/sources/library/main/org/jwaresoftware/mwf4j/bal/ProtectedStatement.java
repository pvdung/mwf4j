/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Protected;

/**
 * Flow statement that executes a predefined other statement
 * and converts and thrown runtime exceptions to a {@linkplain ThrowStatement}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 * @see       TryEachSequenceStatement
 * @see       TryCatchStatement
 **/

public final class ProtectedStatement extends BALTransientStatement implements Protected
{
    public ProtectedStatement(ControlFlowStatement body)
    {
        super();
        Validate.notNull(body,What.BODY);
        myBody = body;
    }

    protected ControlFlowStatement runInner(Harness harness)
    {
        ControlFlowStatement continuation;
        try {
            continuation = harness.runParticipant(myBody);
        } catch(RuntimeException rtX) {
            continuation = new ThrowStatement(rtX);
        }
        return continuation;
    }

    private ControlFlowStatement myBody;
}


/* end-of-ProtectedStatement.java */
