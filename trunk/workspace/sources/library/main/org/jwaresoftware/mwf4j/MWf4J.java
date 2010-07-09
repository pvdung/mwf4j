/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.Strings;

/**
 * MWf4J global constants.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public class MWf4J
{
    public static final String NS="mwf4j";

    /**
     * ID list of MWf4J related services. Some of these services are provided
     * by MWf4J (at different times), but the majority when defined are done so 
     * by the application as a way to customize the standard MWf4J behavior. For
     * instance, you can supply your own custom executor service for use by activity
     * chains; otherwise MWf4J uses the default JVM thread-pool executor service.
     * Note that the services are named such that you can use the names directly
     * within container configuration files in JSON or XML.
     *
     * @since     JWare/MWf4j 1.0.0
     * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
     * @version   @Module_VERSION@
     * @.safety   n/a
     * @.group    impl,infra
     **/
    public static final class ServiceIds
    {
        /** Optional executor service instance for BAL activities. **/
        public final static String EXECUTOR   = NS+"_Harness_Executor";

        /** Optional concurrent map for a single BAL activity run. **/
        public final static String VARIABLES   = NS+"_Harness_Variables";

        /** Optional JexlEngine instance for BAL activities. **/
        public final static String JEXLENGINE = NS+"_Harness_JexlEngine";


        private ServiceIds() { /*access only constants*/ }
    }


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
     * owning entity's id.
     **/
    public final static String getCursorKey()
    {
        return ".cursor.data";
    }

    /**
     * Generates a cursor key based on the incoming entity id and the 
     * fallback cursory value for MWf4J.
     * @param id entity identifier
     * @return new cursor key (never null or empty)
     **/
    public final static String getCursorKey(String id)
    {
        return Strings.trimToEmpty(id)+getCursorKey();
    }


    
    protected MWf4J() {}
}


/* end-of-MWf4J.java */
