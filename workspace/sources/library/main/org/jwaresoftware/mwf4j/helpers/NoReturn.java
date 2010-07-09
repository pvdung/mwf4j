/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.io.Serializable;

import  org.jwaresoftware.gestalt.Strings;

/**
 * Marker class for use to indicate a callable that returns nothing.
 * For use like: <pre>
 * public class DropOrderFile implements Callable&lt;NoReturn&gt; {
 *   public NoReturn call() {
 *      <i>...[work work work]</i>
 *   }
 * }
 * </pre>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl
 * @see       org.jwaresoftware.mwf4j.assign.SavebackDiscard
 **/

public final class NoReturn implements Comparable<NoReturn>, Serializable
{
    private static final long serialVersionUID = -138356113051038531L;

    public final static NoReturn INSTANCE = new NoReturn();

    public NoReturn() {
        super();
    }

    public String toString() {
        return Strings.NULL;
    }

    public boolean equals(Object object) {
        if (object==null) { return false; }
        if (object==this) { return true;  }
        return getClass().equals(object.getClass());
    }

    public int hashCode() {
        return getClass().hashCode();
    }
    
    public int compareTo(NoReturn othernull) {
        if (othernull!=null) {
            if (!othernull.getClass().equals(getClass())) {
                throw new ClassCastException();
            }
            return 0;
        }
        return 1;
    }

    private Object readResolve() {
        return INSTANCE;
    }
}


/* end-of-NoReturn.java */
