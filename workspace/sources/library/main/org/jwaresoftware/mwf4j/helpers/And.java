/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;

/**
 * Test condition that returns <i>true</i> if a set of other conditions all
 * return <i>true</i>. Otherwise, returns <i>false</i> always. Does a short
 * circuited evaluation -- returns on the first <i>false</i> (will not evaluate
 * any subsequent conditions).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class And extends CloneableSkeleton implements Condition, Declarable
{
    public And(Condition...conditions)
    {
        Validate.noneNull(conditions,What.CRITERIA);
        myTests = conditions;
    }

    public boolean evaluate(Harness harness)
    {
        for (Condition test:myTests) {
            if (!test.evaluate(harness))
                return false;
        }
        return true;
    }

    public Object clone()
    {
        And copy = (And)super.clone();
        copy.myTests = LocalSystem.newCopyOrSameArray(myTests);
        return copy;
    }

    public void freeze(Fixture environ)
    {
        Declarables.freezeAll(environ,(Object[])myTests);
    }

    private Condition[] myTests;
}


/* end-of-And.java */
