/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

/**
 * The MWf4J supported ways to read input from or store results to the known
 * environment. Used by assignment statements.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,api,helper
 **/

public enum StoreType
{
    DATAMAP, THREAD, OBJECT, PROPERTY, SYSTEM, NONE;

    public static StoreType findOrNull(String name) {
        StoreType t = null;
        if (name!=null) {
            try { t = StoreType.valueOf(name.toUpperCase()); }
            catch(IllegalArgumentException unknown) {/*burp*/}
        }
        return t;
    }
}


/* end-of-StoreType.java */
