/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.scope;

import  java.util.IdentityHashMap;
import  java.util.List;
import  java.util.Map;

import  org.jwaresoftware.gestalt.Effect;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Throwables;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.Unwindable;
import  org.jwaresoftware.mwf4j.What;

/**
 * Simple POJO implementation of the {@linkplain Scope} interface. Note
 * that a scope's name is optional (uses the empty string by default).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    infra,impl,helper
 **/

public class ScopeBean implements Scope
{
    public ScopeBean()
    {
    }

    public ScopeBean(String name)
    {
        if (name!=null) {
            myName = name;
        }
    }

    public String getName() 
    {
        return myName;
    }

    public final void addUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myUnwinds) {
            myUnwinds.put(participant,Boolean.TRUE);
        }
    }

    public final void removeUnwind(Unwindable participant)
    {
        Validate.notNull(participant,What.CALLBACK);
        synchronized(myUnwinds) {
            myUnwinds.remove(participant);
        }
    }

    public void unwindAll(Harness harness)
    {
        List<Unwindable> unwinds;
        synchronized(myUnwinds) {
            unwinds = LocalSystem.newList(myUnwinds.keySet());
            myUnwinds.clear();
        }
        for (Unwindable next:unwinds) {//Do EACH (ignore barfage)
            try {
                next.unwind(harness);
            } catch(RuntimeException rtX) {
                String what = Throwables.getTypedMessage(rtX);
                harness.getIssueHandler().problemOccured(what,Effect.IGNORE,rtX);
            }
        }
    }

    public void doEnter(Harness harness)
    {
        //Nothing by default
    }

    public void doLeave(Harness harness)
    {
        //Nothing by default
    }

    public String toString()
    {
        String myname = getName();
        return Strings.isEmpty(myname) ? What.idFor(this) : myname;
    }


    private String myName=Strings.EMPTY;
    private Map<Unwindable,Boolean> myUnwinds = new IdentityHashMap<Unwindable,Boolean>();
}


/* end-of-ScopeBean.java */
