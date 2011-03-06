/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Condition;
import  org.jwaresoftware.mwf4j.bal.*;

/**
 * ---- (( INSERT DOCUMENTATION )) ----
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    infra,impl
 **/

public interface BALFactory
{
    SequenceAction newSequence();
    SequenceAction newSequence(String id);
    Action newEmpty(); 
    Condition newCondition(String expr);

    public final static BALFactory Standard = new BALFactory() {
        public Action newEmpty() {
            return new EmptyAction();
        }
        public SequenceAction newSequence(String id) {
            return id==null ? new SequenceAction() : new SequenceAction(id);
        }
        public SequenceAction newSequence() {
            return newSequence(null);
        }
        public Condition newCondition(String expr) {
            return ConditionBuilder.fromEval(expr);
        }
    };
}


/* end-of-BALFactory.java */
