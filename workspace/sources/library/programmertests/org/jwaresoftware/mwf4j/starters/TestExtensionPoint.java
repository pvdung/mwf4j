/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Starting point for test-related extension points. Always increments
 * the test fixture's executed statement count and adds itself to the
 * named list of executed statements (unconditionally).
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test,helper
 **/

public abstract class TestExtensionPoint extends CloneableExtensionPoint
{
    protected TestExtensionPoint(String id)
    {
        super(id);
    }

    protected TestExtensionPoint(ExtensionPoint different) 
    {
        super(different);
    }

    public void doEnter(Harness h) 
    {
        super.doEnter(h);
        if (wantsEnterLeaveMarked()) {
            TestFixture.incStatementCount();
            TestFixture.addPerformed(getId());
        }
    }
    
    public void doLeave(Harness h)
    {
        if (wantsEnterLeaveMarked()) {
            TestFixture.addExited(getId());
        }
        super.doLeave(h);
    }

    public final void setEnterLeaveMarked(boolean flag)
    {
        myEnterLeaveFlag=flag;
    }

    protected final boolean wantsEnterLeaveMarked()
    {
        return myEnterLeaveFlag;
    }

    private boolean myEnterLeaveFlag=true;//notify on enter+leave
}


/* end-of-TestExtensionPoint.java */
