/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

/**
 * Starting implementation point for extensions that you can clone for use
 * from different threads. A specific extension point is <em>still</em> only 
 * usable from a single thread; however, container actions like sequences
 * can create deep copies of their member actions automagically <em>and run 
 * each of those independent copies</em> from its own thread of execution.
 * Implementations can extend the (now) public {@linkplain #clone() clone
 * method} which by default simply calls the inherited Object clone method.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single (including ALL subclasses)
 * @.group    infra,impl
 **/

public abstract class CloneableExtensionPoint extends ExtensionPoint implements Cloneable
{
    protected CloneableExtensionPoint()
    {
        super();
    }

    protected CloneableExtensionPoint(String id)
    {
        super(id);
    }

    protected CloneableExtensionPoint(ExtensionPoint other)
    {
        super(other);
    }

    public Object clone()
    {
        try {
            return super.clone();
        } catch(CloneNotSupportedException clnX) {
            throw new InternalError();
        }
    }
}


/* end-of-CloneableExtensionPoint.java */
