/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.Strings;

/**
 * MWf4J global constants.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public class MWf4J
{
    /** Default namespace prefix for MWf4J components and configuration. */
    public static final String NS="mwf4j";


    /**
     * ID list of MWf4J related services. Some of these services are provided
     * by MWf4J (at different times), but the majority, when defined, are done so 
     * by the application as a way to customize the standard MWf4J behavior. For
     * instance, you can supply your own custom executor service for use by activity
     * chains; otherwise MWf4J uses the default JVM thread-pool executor service.
     * Note that the services are named such that you can use the names directly
     * within container configuration files in JSON or XML.
     *
     * @since     JWare/MWf4J 1.0.0
     * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
     * @version   @Module_VERSION@
     * @.safety   n/a
     * @.group    impl,infra
     * @see       org.jwaresoftware.mwf4j.harness.SimpleHarness SimpleHarness
     **/
    public static final class ServiceIds
    {
        /** Optional executor service instance for per-harness activities. **/
        public final static String EXECUTOR   = NS+"_Harness_Executor";

        /** Optional concurrent map for use during a single harness run. **/
        public final static String VARIABLES  = NS+"_Harness_Variables";

        /** Optional resolver for general-purpose strings (typical RHS). **/
        public final static String STRING_RESOLVER  = NS+"_Harness_StringResolver";

        /** Optional JexlEngine instance for BAL activities. **/
        public final static String JEXLENGINE = NS+"_Harness_JexlEngine";


        private ServiceIds() { /*access only constants*/ }
    }


    /**
     * Names of objects that MWf4J stores as MDC variables to pass back to
     * the application. Often these variables are used to present
     * information to application-supplied hooks (like error handlers).
     *
     * @since     JWare/MWf4J 1.0.0
     * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
     * @version   @Module_VERSION@
     * @.safety   multiple
     * @.group    api,helper
     **/
    public static final class MDCKeys
    {
        /** When a uncaught error unwinds all the way back to the activity's
         *  harness, this variable is set to that error while the activity's
         *  uncaught exception handler is invoked. NOT used for any other
         *  scenario.
         **/
        public final static String UNCAUGHT_ERROR = NS+".thread.uncaughtError";

        /** When a protected action (like a try-catch) detects an exception,
         *  this variable STACK is updated so that the latest detected
         *  error is at the top of the stack. With cascading or composite
         *  exceptions, it's possible that this stack will contain multiple
         *  items.
         **/
        public final static String LATEST_ERROR = NS+".thread.latestError";

        private MDCKeys() { /*access only constants*/ }
    }



    /**
     * Returns the fallback value of a MWf4J-based loop cursor. Typically
     * used by actions that can create cursors but don't require the 
     * application to define one explicitly. Almost always appended to the
     * owning entity's id to avoid unintended overwrites.
     * @see MWf4J#getCursorKey(String) getCursorKey(String)
     **/
    public final static String getCursorKey()
    {
        return ".cursor.data";
    }

    /**
     * Generates a cursor key based on the incoming entity id and the 
     * fallback cursor value for MWf4J.
     * @param eid entity identifier
     * @return new cursor key (never null or empty)
     **/
    public final static String getCursorKey(String eid)
    {
        return Strings.trimToEmpty(eid)+getCursorKey();
    }


    
    protected MWf4J() {}
}


/* end-of-MWf4J.java */
