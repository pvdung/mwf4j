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
 * per-call dependent state should be transfered or stored within a
 * generated control statement before returning from the factory method.
 * <p/>
 * It may also be possible for a single action instance to be shared  
 * by multiple other actions <em>at the same time</em>. While this is largely
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
    /**
     * Factory method for this action's primary control flow statement. Note
     * that the factory method should ensure the returned statement is ready
     * to be executed immediately. 
     * @param next flow statement that comes <em>after</em> action (non-null)
     * @return control flow statement that reflects the current state of this
     *         action
     * @see #configure
     **/
    ControlFlowStatement makeStatement(ControlFlowStatement next);


    
    /**
     * Callback to (re)configure a previously created control flow statement.
     * Typically this method is called indirectly via the statement's 
     * reconfigure method from the action's factory method. See the MWf4J
     * starter components for examples of this usage pattern.
     * @param own action-generated statement to configure (non-null)
     * @throws java.lang.IllegalArgumentException if the action does not
     *         recognize the incoming statement's type.
     **/
    void configure(ControlFlowStatement own);



    /** 
     * Null-proxy for an anonymous action. Useful to give adhoc statements
     * an action reference. Immutable; id is the blank string.
     */
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
