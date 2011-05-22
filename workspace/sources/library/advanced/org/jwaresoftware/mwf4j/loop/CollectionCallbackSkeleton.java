/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  org.jwaresoftware.mwf4j.behaviors.Declarable;
import  org.jwaresoftware.mwf4j.starters.CalledClosureSkeleton;

/**
 * Starting implementation for collection callbacks in this package. Handles
 * the required public clone and call methods for subclasses.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    impl,extras,helper
 **/

public abstract class CollectionCallbackSkeleton<T> extends CalledClosureSkeleton<T> 
    implements Declarable
{
    protected CollectionCallbackSkeleton()
    {
        super();
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


/* end-of-CollectionCallbackSkeleton.java */
