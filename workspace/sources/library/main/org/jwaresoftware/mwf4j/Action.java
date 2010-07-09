/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * A single component of an {@linkplain Activity}. Actions can be 
 * atomic (indivisible) or composite.
 * <p/>
 * A single action instance can be 're-executed' within its lifetime
 * if its {@linkplain #makeStatement makeStatement} factory method is 
 * triggered multiple times. Whether the action can be reconfigured 
 * with new attribute values (which are then passed through to subsequent 
 * new statements) is implementation defined. Therefore, <em>ALL</em> 
 * per-call dependent state should be transfered or stored within the
 * generated control statement. 
 * <p/>
 * It may also be possible for a single action instance to be shared  
 * by multiple actions <em>at the same time</em>. While this is largely
 * system-dependent, if an action is used as a memory-based store for
 * shared context, it's possible it will participate in distinct actions,
 * possibly concurrently.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface Action extends Entity
{
    ControlFlowStatement makeStatement(ControlFlowStatement next);
    void configure(ControlFlowStatement own);

    public static final Action anonINSTANCE= new Action() 
    {
        public String getId()
            { return ""; }
        public ControlFlowStatement makeStatement(ControlFlowStatement next) 
            { return ControlFlowStatement.nullINSTANCE; }
        public void configure(ControlFlowStatement statement) 
            { }
        public String toString()
            { return "anon"; }
    };
}


/* end-of-Action.java */
