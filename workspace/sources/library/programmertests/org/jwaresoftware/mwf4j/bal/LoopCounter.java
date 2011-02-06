/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

/**
 * Bean that lets you track how many times a looping type statement is
 * called. Useful for actions like ForEachAction and WhileAction.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

public final class LoopCounter
{
    int _last= -99;

    public LoopCounter() {
        super();
    }

    public void setI(Integer loopCount) {
        if (loopCount!=null) //null=>'cleanup'
            _last = loopCount;
    }

    public Integer getI() {
        return _last;
    }

    public int size() {
        return _last>=0 ? _last+1 : 0;
    }

    public String toString() {
        return String.valueOf(size());
    }
}


/* end-of-LoopCounter.java */
