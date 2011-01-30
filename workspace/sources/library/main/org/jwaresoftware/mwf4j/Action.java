/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

/**
 * The definition of one or more related steps (a single conceptual action) 
 * in some {@linkplain Activity activity}.  
 * Actions can be atomic (indivisible) or composite (sub-actions); synchronous
 * or asynchrous. Technically, actions are not "the thing what gets executed"
 * when an activity is run; instead, every action functions as a 
 * {@linkplain #makeStatement factory} for the runnable {@linkplain
 * ControlFlowStatement control statement(s)} that actually do the work 
 * when the action is "run" as part of an activity by a {@linkplain Harness
 * harness}. You use actions as a way to <em>define the piece of work  
 * and to create a harness-executable representation of that work</em>.
 * <p/>
 * A single action instance can be "re-run" within its lifetime if its
 * statement {@linkplain #makeStatement factory method} is triggered 
 * multiple times. Whether you can reconfigure an action to a different
 * definition between calls to run is implementation defined. Therefore, 
 * to be safe, your actions should transfer <em>ALL</em> state that its
 * control statement needs to function <em>to the statement itself</em>  
 * before returning from the factory method. In general, the only reason
 * an initiated control statement links back to its action, is to fetch
 * its immutable identifier for diagnostic reasons.
 * <p/>
 * It is also possible for a single action instance to be used by multiple,
 * other actions <em>at the same time</em> (for example, another composite
 * action). While if and/or how this works this is largely application-dependent,
 * it's possible an application will try to use a single action instance in  
 * distinct activities, possibly concurrently. So if your action is a 
 * memory-based store for shared context, you must document its limitations 
 * under such use cases accordingly.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
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
