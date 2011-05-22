/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.loop;

import  java.util.Collection;
import  java.util.Collections;
import  java.util.concurrent.Callable;

import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;

/**
 * Callable stub that returns an empty immutable collection every time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,extras,helper
 **/

public final class EmptyCollectionCallback<T> extends CloneableSkeleton
    implements Callable<Collection<T>>, Declarable
{
    public EmptyCollectionCallback()
    {
        super();
    }

    public Collection<T> call()
    {
        return Collections.emptyList();
    }

    public void freeze(Fixture environ)
    {
    }
}


/* end-of-EmptyCollectionCallback.java */
