/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.harness;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.helpers.RetryDef;

/**
 * Factory for spawned dependent harness objects. Expected to produce instances or 
 * derivatives of {@linkplain SpawnHarness}. Two implementations are provided
 * for the known MWf4J spawnable harness types.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    infra,impl,helper
 **/

public interface SpawnedHarnessFactory
{
    SpawnedHarness newHarness(Harness master, ControlFlowStatement first);
    SpawnedHarness newHarness(Harness master, ControlFlowStatement first, RetryDef errorNotifyConfig);


    /**
     * Implementation of a factory class for {@linkplain SlaveHarness}.
     *
     * @since   JWare/MWf4J 1.0.0
     * @author  ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
     * @version @Module_VERSION@
     * @.safety multiple
     * @.group  impl,helper
     *
     */
    public static class Default implements SpawnedHarnessFactory
    {
        public SpawnedHarness newHarness(Harness master, ControlFlowStatement first) {
            return new SlaveHarness(master,first);
        }
        public SpawnedHarness newHarness(Harness master, ControlFlowStatement first, RetryDef notifyConfig) {
            return new SlaveHarness(master,first,notifyConfig);
        }
        public final static SpawnedHarnessFactory INSTANCE= new Default();
    }


    /**
     * Implementation of a factory class for {@linkplain ForeverHarness}.
     *
     * @since   JWare/MWf4J 1.0.0
     * @author  ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
     * @version @Module_VERSION@
     * @.safety multiple
     * @.group  impl,helper
     *
     */
    public static class Forever implements SpawnedHarnessFactory
    {
        public SpawnedHarness newHarness(Harness master, ControlFlowStatement first) {
            return new ForeverHarness(master,first);
        }
        public SpawnedHarness newHarness(Harness master, ControlFlowStatement first, RetryDef errorNotifyConfig) {
            return new ForeverHarness(master,first);
        }
        public final static SpawnedHarnessFactory INSTANCE= new Forever();
    }
}


/* end-of-SpawnedHarnessFactory.java */
