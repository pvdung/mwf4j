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
 * Test condition that returns <i>true</i> if any condition from a given
 * set returns <i>true</i>. Otherwise, returns <i>false</i> always.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class Or extends CloneableSkeleton implements Condition, Declarable
{
    public Or(Condition...conditions)
    {
        Validate.noneNull(conditions,What.CRITERIA);
        myTests = conditions;
    }

    public boolean evaluate(Harness harness)
    {
        for (Condition test:myTests) {
            if (test.evaluate(harness))
                return true;
        }
        return false;
    }

    public Object clone()
    {
        Or copy = (Or)super.clone();
        copy.myTests = LocalSystem.newCopyOrSameArray(myTests);
        return copy;
    }

    public void freeze(Fixture environ)
    {
        Declarables.freezeAll(environ,(Object[])myTests);
    }

    private Condition[] myTests;
}


/* end-of-Or.java */
