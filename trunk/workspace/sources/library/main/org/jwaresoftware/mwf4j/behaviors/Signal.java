/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;

/**
 * Mixin interface for a control flow statement (or other participant) that
 * will throw an exception when run. Allows your BAL extensions to plug into
 * the {@linkplain org.jwaresoftware.mwf4j.bal.TryCatchAction try-catch action} 
 * handling like the standard {@linkplain org.jwaresoftware.mwf4j.bal.ThrowStatement
 * throw statement}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,infra,helper
 **/

public interface Signal extends ControlFlowStatement
{
    public static final int NO_SIGNAL_POSITION= -1;
    Exception getCause();
    int getPosition();
}


/* end-of-Signal.java */
