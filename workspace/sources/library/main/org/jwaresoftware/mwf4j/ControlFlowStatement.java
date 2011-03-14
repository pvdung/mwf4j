/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;


/**
 * A statement whose execution results in a choice being made as to which
 * of one or more paths should be followed. Within the context of an ongoing
 * {@linkplain Activity activity}, think of a control statement as the
 * ultimate, useful decider of whether a piece of work gets done or not. 
 * (Note that a decision to "do the work" does not imply that the statement
 * itself does the work or even knows what the work is; see {@linkplain 
 * org.jwaresoftware.mwf4j.starters.ExtensionPoint extension point}.)
 * <p/> 
 * A control statement is typically created by an {@linkplain Action action}
 * to which it can be linked for the rest of its lifetime or not (typically
 * not). Note however, that a control statement can spawn completely 
 * independent continuation statements which it passes to its execution 
 * {@linkplain Harness harness} for queuing and <em>eventual</em> execution. 
 * (The statement should never assume the continuation will occur immediately
 * on return from its own execution unless that is a contract established by
 * the statement type with all harnesses. Also, a control statement should
 * never assume it is the <em>only</em> running statement associated with
 * its action or harness!)
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface ControlFlowStatement
{
    boolean isTerminal();
    boolean isAnonymous();
    void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides);
    ControlFlowStatement run(Harness harness);
    ControlFlowStatement next();
    String getWhatId();

    /** Null proxy for a control flow statement (terminal and anonymous
     *  attributes are true always). **/
    public static final ControlFlowStatement nullINSTANCE= new ControlFlowStatement() 
    {
        public boolean isTerminal()
            { return true; }
        public boolean isAnonymous() 
            { return true; }
        public ControlFlowStatement run(Harness harness) 
            { return this; }
        public void reconfigure(Fixture environ, ControlFlowStatementDefinition overrides)
            { }
        public ControlFlowStatement next()
            { return this; }
        public String getWhatId()
            { return ""; }
        public String toString()
            { return ""; }
    };
}


/* end-of-ControlFlowStatement.java */
