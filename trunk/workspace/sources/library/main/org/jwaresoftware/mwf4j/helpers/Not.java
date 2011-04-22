/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;

/**
 * Test condition that returns the <i>inverse</i> result of another
 * test. So if other test returns <i>true</i> this condition returns
 * <i>false,</i>; if test returns <i>false</i> this condition returns
 * <i>true</i>.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class Not extends CloneableSkeleton implements Condition
{
    public Not(Condition condition)
    {
        Validate.notNull(condition,What.CRITERIA);
        myTest = condition;
    }

    public boolean evaluate(Harness harness)
    {
        return !myTest.evaluate(harness);
    }

    public Object clone()
    {
        Not copy = (Not)super.clone();
        copy.myTest = LocalSystem.newCopyOrSame(myTest);
        return copy;
    }

    private Condition myTest;
}


/* end-of-Not.java */
