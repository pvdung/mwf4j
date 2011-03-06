/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.assign.GivebackMapEntry;

/**
 * ---- (( INSERT DOCUMENTATION )) ----
 *
 * @since     JWare/PROJ 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a,single,multiple,special
 * @.group    api,infra,impl,helper,test
 **/

public class ConditionBuilder
{
    
    /**
     * Resolves inputs like: {@code order.category==CXL}.
     * @param expr input expression (possibly with unresolved references)
     * @return <i>true</i> if expression understood and returned true, <i>false</i> otherwise
     **/
    public static Condition fromEval(final String expr)
    {
        return new Condition() {
            public boolean evaluate(Harness h) {
                String exprfinal = h.interpolate(expr,null);
                return new GivebackMapEntry<Boolean>(h.getVariables(),exprfinal,Boolean.FALSE,Boolean.class,false).call();
            }
        };
    }
}


/* end-of-ConditionBuilder.java */
