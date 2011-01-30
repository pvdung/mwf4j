/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.concurrent.TimeUnit;

import  org.jwaresoftware.gestalt.Validate;

/**
 * Struct that describes how long a waiting task or function should
 * um wait. Basically keeps the "how long" specifier together with 
 * its matching time unit. Has bias towards milliseconds as that is
 * what most time-aware JRE APIs assume.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 **/

public final class WaitDef
{
    public WaitDef()
    {
        this(0L,TimeUnit.MILLISECONDS);
    }
 
    public WaitDef(long msecs) 
    {
        this(msecs,TimeUnit.MILLISECONDS);
    }

    public WaitDef(long length, TimeUnit uom)
    {
        Validate.notNull(uom,"timeunit");
        myLength = length;
        myUOM = uom;
    }

    public long getLength()
    {
        return myLength;
    }

    public boolean isUndefined()
    {
        return myLength<0L;
    }

    public boolean isForever()
    {
        return myLength==0L;
    }

    public TimeUnit getUOM()
    {
        return myUOM;
    }

    public long toMillis()
    {
        return getUOM().toMillis(getLength());
    }

    public long toNanos()
    {
        return getUOM().toNanos(getLength());
    }

    public String toString()
    {
        return ""+myLength+" "+myUOM.toString().toLowerCase();
    }

    private final long myLength;
    private final TimeUnit myUOM;
}


/* end-of-WaitDef.java */
