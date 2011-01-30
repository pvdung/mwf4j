/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.reveal.Identified;

/**
 * Collection of <em>internal</em> field and parameter names
 * for use with internal MWf4j error messages.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public class What extends org.jwaresoftware.gestalt.What
{
    public final static String HARNESS      = "harness";
    public final static String SCOPE        = "scope";
    public final static String VARIABLE     = "variable";
    public final static String VARIABLES    = "variables";
    public final static String STATEMENT    = "control-flow-statement";
    public final static String STATEMENTS   = "control-flow-statements";
    public final static String CONTINUATION = "continuation statement";
    public final static String ACTION       = "action";
    public final static String ACTIONS      = "actions";
    public final static String ACTIVITY     = "activity";
    public final static String EXCEPTION    = "exception";
    public final static String KEY          = "key";
    public final static String CALLBACK     = "callback";
    public final static String BODY         = "body";
    public final static String CURSOR       = "cursor";
    public final static String CONFIG       = "configuration";
    public final static String BARRIER      = "barrier";
    public final static String REFERENCE    = "reference";
    public final static String OWNER        = "owner";

    public static final String getNonBlankId(final Identified object)
    {
        String id = Strings.UNDEFINED;
        if (object!=null) {
            String oid = object.getId();
            if (!Strings.isBlank(oid)) {
                id = oid;
            } else {
                id += "@"+System.identityHashCode(object);
            }
        }
        return id;
    }

    protected What() { }
}


/* end-of-What.java */
