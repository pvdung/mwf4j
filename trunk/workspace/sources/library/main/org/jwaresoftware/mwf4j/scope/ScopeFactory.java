/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.What;

/**
 * Factory service facade for creation of {@linkplain Scope scopes} and 
 * {@linkplain ScopeKey scope keys}. By default, service uses the implementation
 * that creates our own {@linkplain ScopeKeyBean} and {@linkplain ScopeBean}
 * types. Your application can switch out this default for something more
 * controllable; see the {@linkplain #setProviderInstance(SPI) method}. If
 * you customize the factory, you should do this ONCE and AS SOON AS POSSIBLE.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    infra,impl,helper
 **/

public final class ScopeFactory
{
    /**
     * The pluggable provider interface. Your application can replace this
     * interface with its own (for example from a container). See the
     * {@linkplain ScopeFactory#setProviderInstance(SPI)} method.
     *
     * @since   JWare/MWf4J 1.0.0
     * @author  ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
     * @version @Module_VERSION@
     * @.safety n/a
     * @.group  infra,impl,helper
     **/
    public interface SPI
    {
        ScopeKey newKey(ControlFlowStatement owner);
        Scope newScope(ControlFlowStatement owner, String name);


        /** 
         * Null-proxy provider that returns singleton null-proxy bean
         * and key. Only useful as a <em>marker</em>!
         *
         * @since   JWare/MWf4J 1.0.0
         * @author  ssmc, &copy;2009 <a href="@Module_WEBSITE@">SSMC</a>
         * @version @Module_VERSION@
         * @.safety multiple
         * @.group  impl,helper
         * @see     ScopeKey#nullINSTANCE
         * @see     Scope#nullINSTANCE
         */
        public final static class Null implements SPI {
            public ScopeKey newKey(ControlFlowStatement owner) {
                return ScopeKey.nullINSTANCE;
            }
            public Scope newScope(ControlFlowStatement owner, String name) {
                return Scope.nullINSTANCE;
            }
        }


        /** 
         * Our standard provider that returns the builtin  
         * {@linkplain ScopeBean} and {@linkplain ScopeKeyBean}.
         *
         * @since   JWare/MWf4J 1.0.0
         * @author  ssmc, &copy;2009 <a href="@Module_WEBSITE@">SSMC</a>
         * @version @Module_VERSION@
         * @.safety multiple
         * @.group  impl,helper
         */
        public final static class Default implements SPI {
            public ScopeKey newKey(ControlFlowStatement owner) {
                return new ScopeKeyBean(owner);
            }
            public Scope newScope(ControlFlowStatement owner, String name) {
                return new ScopeBean(owner,name);
            }
        }
    }


    private final static SPI nulINSTANCE = new SPI.Null();
    private final static SPI defINSTANCE = new SPI.Default();
    private static SPI INSTANCE = defINSTANCE;


    public final static ScopeKey newKey(ControlFlowStatement statement)
    {
        ScopeKey key = INSTANCE.newKey(statement);
        Validate.resultNotNull(key,What.KEY);
        return key;
    }

    public final static Scope newScope(ControlFlowStatement statement, String name)
    {
        Scope scope = INSTANCE.newScope(statement,name);
        Validate.resultNotNull(scope,"scope");
        return scope;
    }

    public final static void setProviderInstance(SPI customInstance)
    {
        Validate.notNull(customInstance,What.PROVIDER);
        INSTANCE = customInstance;
    }

    public final static void unsetProviderInstance()
    {
        INSTANCE = defINSTANCE;
    }

    public final static void clrProviderInstance()
    {
        INSTANCE = nulINSTANCE;
    }
}


/* end-of-ScopeFactory.java */
