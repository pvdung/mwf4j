/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.starters;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;

/**
 * Test suite for {@linkplain ExtensionPoint} and typical usage expected.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline"})
public final class ExtensionPointTest extends ExecutableTestSkeleton
{
    /** Action implementation that reflects typical BAL factory method template. **/
    static final class CustomAction extends ExtensionPoint
    {
        CustomAction(String id) {
            super(id);
        }
        CustomAction(ExtensionPoint different) {
            super(different);
        }
        public void doEnter(Harness h) {
            super.doEnter(h);
            TestFixture.addPerformed(getId());
        }
        protected ControlFlowStatement runInner(Harness harness) {
            return next();
        }
    }

    private CustomAction newOUT(String id)
    {
        return new CustomAction(id);
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testBaseline()
    {
        runTASK(newOUT("helloworld"));
        assertTrue(TestFixture.wasPerformed("helloworld",1),"wasPerformed");
    }
    
    public void testFlowStatementApi_1_0_0()
    {
        CustomAction out = newOUT("void");
        assertSame(out.getOwner(),out,"owner");
        assertFalse(out.isAnonymous(),"anonymous");
        assertFalse(out.isTerminal(),"terminal");
        assertSame(out.next(),ControlFlowStatement.nullINSTANCE,"initial-next");
        ControlFlowStatement myEnd = new FailStatement();
        assertSame(out.makeStatement(myEnd),out,"makeStatement('end')");
        assertSame(out.next(),myEnd,"next-after-made");
        out.configure(out);
        assertSame(out.next(),myEnd,"next-after-made-and-configured");
    }
    
    @Test(expectedExceptions={IllegalStateException.class})
    public void testFailIfConfigureOtherStatement_1_0_0()
    {
        newOUT("nada").configure(ControlFlowStatement.nullINSTANCE);
        fail("Should not be able to configure arbitrary statements");
    }
    
    @Test(expectedExceptions={IllegalStateException.class})
    public void testFailIfSetOwnerToOtherThanSelf_1_0_0()
    {
        newOUT("setOwned").setOwner(Action.anonINSTANCE);
        fail("Should not be able to configure different owner action");
    }

    public void testConfigureAttrAfterVoidCtor_1_0_0()
    {
        CustomAction out = newOUT("void");
        out.setOwner(out);//weird but allowed due to public API-ed-ness
        assertEquals(out.getId(),"void");
        out.setId("diff");
        assertEquals(out.getId(),"diff","getId[after]");
    }

    static class Prototype extends ExtensionPoint {
        Prototype() {
            super("_from_prototype");
        }
        protected ControlFlowStatement runInner(Harness harness) {
            return next();
        }
    }

    public void testInitFromPrototypeTemplate_1_0_0()
    {
        Prototype prototype = new Prototype();
        CustomAction out = new CustomAction(prototype);
        assertEquals(out.getId(),prototype.getId(),"copied id");
        runTASK(out);
        assertTrue(TestFixture.wasPerformed(out.getId()),"wasPerformed");
    }
}


/* end-of-ExtensionPointTest.java */
