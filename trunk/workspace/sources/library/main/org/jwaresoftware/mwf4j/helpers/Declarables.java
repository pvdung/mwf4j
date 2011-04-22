/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.Collection;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;

/**
 * Utilities to help statements and other components resolve their own 
 * just-before-use {@linkplain Declarable declared} fields. Unlike a 
 * general purpose get/set property based configuration,
 * {@linkplain org.jwaresoftware.mwf4j.behaviors.DeclarableSupport items
 * that support declared members} <em>select explicitly</em> which of
 * their members can be declared.
 * <p/>
 * <b>Example Usage:</b><pre>
 * private void doFreeze(Fixture environ) {
 *   mySourcePath = Declarables.freeze(environ,mySourcePath)
 *   Declarables.freeze(environ,myLHS,myRHS,myTest);
 * }
 * </pre>
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class Declarables
{
    public static void freezeAll(Fixture environ, Object...objects)
    {
        //NB: ALLOW 'null' for an item slot!
        if (objects!=null) {
            for (Object candidate:objects) {
                if (candidate instanceof Declarable) {
                    Declarable next = (Declarable)candidate;
                    next.freeze(environ);
                }
            }
        }
    }

    public static void freezeAllIn(Fixture environ, Collection<?> objects)
    {
        //NB: ALLOW 'null' for an item slot!
        if (objects!=null && !objects.isEmpty()) {
            for (Object candidate:objects) {
                if (candidate instanceof Declarable) {
                    Declarable next = (Declarable)candidate;
                    next.freeze(environ);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T freeze(Fixture environ, T original)
    {
        T resolved = original;
        if (original!=null) {
            if (original instanceof String) {
                resolved = (T)environ.interpolate(original.toString());
            } else if (original instanceof Declarable) {
                ((Declarable)original).freeze(environ);
            } else if (original instanceof Collection) {
                freezeAllIn(environ,(Collection)original);
            } else if (original.getClass().isArray()) {
                freezeAll(environ,(Object[])original);
            }
        }
        return resolved;
    }

    private Declarables() { }
}


/* end-of-Wireables.java */
