/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.concurrent.BlockingQueue;

import  org.jwaresoftware.gestalt.Validate;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.helpers.WaitDef;

/**
 * Giveback that returns the head of a supplied queue, waiting if 
 * necessary until an element becomes available or a prescribed timeout
 * period expires. Intended for use with async activities like listeners
 * and consumers. Subclasses can customize two methods that define what
 * gets returned (or done) in the event of a timeout (well defined), or
 * an interruption (usually the result of a cancel action against this
 * helper's Future).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public class GivebackNext<T> implements Giveback<T>
{
    public GivebackNext(BlockingQueue<T> q, T eofMark)
    {
        Validate.notNull(q,What.SOURCE);
        myQueue=q;
        myTimeout=null;
        myEOF=eofMark;
    }

    public GivebackNext(BlockingQueue<T> q)
    {
        this(q,(T)null);
    }

    public GivebackNext(BlockingQueue<T> q, WaitDef timeout, T eofMark)
    {
        Validate.notNull(q,What.SOURCE);
        myQueue=q;
        myTimeout=timeout;
        myEOF=eofMark;
    }

    public GivebackNext(BlockingQueue<T> q, WaitDef timeout)
    {
        this(q,timeout,null);
    }

    public T call() throws Exception
    {
        T payload = null;
        try {
            if (myTimeout!=null) {
                payload = myQueue.poll(myTimeout.getLength(), myTimeout.getUOM());
                if (payload==null) {
                    payload = getTimedOutPayload();
                }
            } else { 
                payload = myQueue.take();
            }
        } catch(InterruptedException iruptedX) {
            payload = getEOFPayload();
            if (payload==null)
                throw iruptedX;
        }
        return payload;
    }

    public T call(Harness ignored) throws Exception
    {
        return call();
    }

    protected T getEOFPayload()
    {
        return myEOF;
    }

    protected T getTimedOutPayload()
    {
        return getEOFPayload();
    }


    private final BlockingQueue<T> myQueue;
    private final WaitDef myTimeout;
    private final T myEOF;
}


/* end-of-GivebackNext.java */
