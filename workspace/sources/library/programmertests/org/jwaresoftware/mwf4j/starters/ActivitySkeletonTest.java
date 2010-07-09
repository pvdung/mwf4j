/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.MDC;

/**
 * Test suite for {@linkplain ActivitySkeleton}.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline"})
public final class ActivitySkeletonTest extends ExecutableTestSkeleton
{
    public static class Task extends ActivitySkeleton {
        public Task(String id)  {
            super(id); 
        }
    }
    
    public static class Anon extends ActivitySkeleton {
        public Anon() { 
            super();
        }
        public void setId(String id) {
            super.setId(id);
        }
        public void doError(Throwable cause) {
            assertSame(MDC.uncaughtError(),cause,"MDC.uncaughtError");
            MDC.put("handler."+getId(), cause.getMessage());
        }
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testChangeIdLater_1_0_0()
    {
        Anon out = new Anon();
        assertNotNull(out.diagnostics(),"diagnostics");
        assertNotNull(out.getId(),"name");
        assertFalse("ANON".equals(out.getId()));
        out.setId("ANON");
        assertEquals(out.getId(),"ANON","name");
        
    }
}


/* end-of-ActivitySkeletonTest.java */
