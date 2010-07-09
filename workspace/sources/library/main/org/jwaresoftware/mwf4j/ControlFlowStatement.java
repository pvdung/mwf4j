/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;


/**
 * A statement whose execution results in a choice being made as to which
 * of one or more paths should be followed. A control statement instance
 * should belong to one action at most, during its lifetime. 
 * <p/>
 * A control statement can spawn dynamic continuation statements which it 
 * must 'pass back' to the owning action for queuing and <em>eventual</em>
 * execution (the statement should never assume the continuation will
 * occur immediately on return from its own execution unless that is a 
 * contract established by the statement type with all actions). A
 * control statement should also not assume it is the <em>only</em> 
 * running statement associated with its action!
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public interface ControlFlowStatement extends ActionDependent
{
    boolean isTerminal();
    boolean isAnonymous();
    ControlFlowStatement run(Harness harness);
    void reconfigure();
    Action getOwner();
    ControlFlowStatement next();

    /** Null proxy for a control flow statement (end). **/
    public static final ControlFlowStatement nullINSTANCE= new ControlFlowStatement() 
    {
        public Action getOwner() 
            { return null; }
        public boolean isTerminal()
            { return true; }
        public boolean isAnonymous() 
            { return true; }
        public ControlFlowStatement run(Harness harness) 
            { return this; }
        public void reconfigure()
            { }
        public ControlFlowStatement next()
            { return this; }
        public String toString()
            { return ""; }
    };
}


/* end-of-ControlFlowStatement.java */
