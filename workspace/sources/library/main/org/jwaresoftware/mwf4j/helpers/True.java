/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Harness;

/**
 * Test condition that always returns <i>true</i>.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class True implements Condition
{
    public final static Condition INSTANCE= new True();

    public boolean evaluate(Harness ignored)
    {
        return true;
    }
}


/* end-of-True.java */
