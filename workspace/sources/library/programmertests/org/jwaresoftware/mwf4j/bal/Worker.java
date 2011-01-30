/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.util.Map;
import  java.util.concurrent.Callable;
import  java.util.concurrent.FutureTask;
import  java.util.concurrent.RunnableFuture;

import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.Named;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;

/**
 * Nonsense work to be done from action's thread. Use as closure 
 * for testing inline call-out actions.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public class Worker implements Named,Callable<Map<String,Object>>
{
    public Worker(String name, Action owner) {
        Validate.notNull(name,What.NAME);
        myOwnerId = Strings.trimToEmpty(owner.getId());
        myMap = LocalSystem.newMap();
        myMap.put("workerid", name);
        myMap.put("ownerid",myOwnerId);
    }

    public static final RunnableFuture<Map<String,Object>> asFuture(Worker w) {
        return new FutureTask<Map<String,Object>>(w);
    }

    public final String getName() {
        return (String)getDataMap().get("workerid");
    }

    public final String getFQName() {
        return myOwnerId+"."+getName();
    }

    public final Map<String,Object> getDataMap() {
        return myMap;
    }

    protected void doWork() {
        final String me = getName();
        Diagnostics.ForBAL.info("Worker''{}' running for action='{}'",me,myOwnerId);
        MDC.put("workerid",me);
    }

    public Map<String,Object> call() {
        myMap.put("starttime",System.nanoTime());
        Thread thr = Thread.currentThread();
        myMap.put("threadid", thr.getId());
        myMap.put("threadname", thr.getName());
        myMap.remove("endtime");
        doWork();
        myMap.put("endtime",System.nanoTime());
        return myMap;
    }

    private final Map<String,Object> myMap;
    private final String myOwnerId;
}


/* end-of-Worker.java */
