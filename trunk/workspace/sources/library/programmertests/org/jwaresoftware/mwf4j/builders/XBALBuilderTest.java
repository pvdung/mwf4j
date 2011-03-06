/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.builders;

import  org.testng.annotations.BeforeMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Action;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.TestFixture;
import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.starters.ExecutableTestSkeleton;

import  static org.jwaresoftware.mwf4j.builders.XBALBuilder.*;

/**
 * Test suite for {@linkplain BALBuilder}. Uses {@linkplain XBALBuilder}
 * to test extensibility and other test-specific DSL features.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","builders","advanced"})
public final class XBALBuilderTest extends ExecutableTestSkeleton
{
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        iniStatementCount();
    }

    private String me()
    {
        return TestFixture.currentTestName();
    }

    private String getString(Harness h, String name)
    {
        return h.getConfiguration().getString(name);
    }

    @Test(groups={"builders-baseline"})
    public void testAnonymousEmptyBuilder_1_0_0()
    {
        runTASK(action().finish());
    }

    @Test(groups={"builders-baseline"})
    public void testNamedEmptyBuilder_1_0_0()
    {
        XBALBuilder out = action(me());
        assertEquals(out.getId(),me(),"id");
        runTASK(out.finish());
    }

    @Test(groups={"builders-baseline"})
    public void testRootLevelSequenceOfEmptyStatements_1_0_0()
    {
        Action out = action("plain")
                       .touch("a")
                       .empty()
                       .touch("c")
                       .empty()
                       .touch("e")
                       .finish();
        runTASK(out);

        assertEquals(getStatementCount(),3,"touch count");
        assertTrue(werePerformedInOrder("a|c|e"),"ordering");
    }

    @Test(groups={"builders-baseline"})
    public void testSimpleVarAssign_1_0_0()
    {
        final String me = me();
        Variables vars= iniDATAMAP();
        Action out = action()
                      .set("a","Hello")
                      .set("t",true)
                      .set("f", false)
                      .set("i",10)
                      .set("L",101L)
                      .set("pi",Math.PI)
                      .set("I",this)
                      .set(var("id"),me)
                      .set(variable("ID"),me)
                      .finish();
        
        runTASK(out);
        
        assertSame(vars.get("I"),this,"var(I)");
        assertEquals(vars.get("a",String.class),"Hello","var(a)");
        assertSame(vars.get("t",Boolean.class),Boolean.TRUE,"var(t)");
        assertSame(vars.get("f",Boolean.class),Boolean.FALSE,"var(f)");
        assertEquals(vars.get("i",Integer.class),Integer.valueOf(10),"var(i)");
        assertEquals(vars.get("L",Long.class),Long.valueOf(101L),"var(L)");
        assertEquals(vars.get("pi",Double.class),Math.PI,"var(pi)");
        assertEquals(vars.get("id",String.class),me,"var(id)");
        assertSame(vars.get("ID",String.class),me,"var(ID)");
    }

    @Test(dependsOnMethods={"testSimpleVarAssign_1_0_0"})
    public void testAdvancedVarAssign_1_0_0()
    {
        Variables vars = iniDATAMAP();
        vars.put("IDFactory", IDFactory.class);
        Action out = action()
                       .set("oid",get("IDFactory.newOid()"))
                       .set("oid.copy1",var("oid"))
                       .set("oid.copy2",ref("oid"))
                       .finish();
        runTASK(out);
        Object oid = vars.get("oid");
        LocalSystem.show("oid=",oid);
        assertTrue(oid instanceof Long, "oid is installed");
        assertSame(vars.get("oid.copy1"),oid,"oid.copy1 (var)");
        assertSame(vars.get("oid.copy2"),oid,"oid.copy2 (ref)");
    }

    public void testSimplePropertyAssign_1_0_0()
    {
        Action out = action()
                      .set(property("DEBUG"),true)
                      .set(property("name"),"Harry")
                      .set(env("maxwait"),13)
                      .finish();

        Harness h = newHARNESS(out);
        runTASK(h);

        assertEquals(getString(h,"DEBUG"),"true","p(DEBUG)");
        assertEquals(getString(h,"name"),"Harry","p(name)");
        assertEquals(getString(h,"maxwait"),"13","p(maxwait)");
    }

    /**
     * Verify following ordering:<pre>
     *    a,
     *    b=[
     *    ],
     *    c=[
     *      c.1
     *    ],
     *    d,
     *    e=[
     *      e.1
     *      e.2
     *    ]
     *    f
     * </pre>
     */
    public void testSimpleNestedBlocksA_1_0_0()
    {
        Variables vars = iniDATAMAP();
        Action out = action("ALevel")
                      .touch("a")
                      .block(/*b*/)
                          .end()
                      .block("c")
                         .set("iah",1)
                         .touch("c.1")
                         .end()
                      .touch("d")
                      .block("e")
                          .set("iah",2)
                          .touch("e.1")
                          .touch("e.2")
                          .end()
                      .touch("f")
                      .finish();
        runTASK(out);
        assertEquals(getStatementCount(),6,"touch count");
        assertTrue(werePerformedInOrder("a|c.1|d|e.1|e.2|f"),"ordering");
        assertEquals(vars.get("iah",Number.class),Integer.valueOf(2),"iah");
    }

    /**
     * Verify following ordering:<pre>
     *   i=[
     *    i.1=[
     *     i.1.1
     *    ],
     *    i.2=[
     *     i.2.1
     *    ],
     *    i.3=[
     *     i.3.1
     *    ],
     *    i.4=[
     *     i.4.1
     *    ],
     *    i.5=[
     *     i.5.1
     *    ],
     *   ]
     * </pre>
     */
    public void testSimpleNestedBlocksB_1_0_0()
    {
        Action out = action("BLevel")
                       .block("i")
                         .block("i.1")
                           .touch("i.1.1")
                           .end("i.1")
                         .block("i.2")
                           .touch("i.2.1")
                           .end("i.2")
                         .block("i.3")
                           .touch("i.3.1")
                           .end("i.3")
                         .block("i.4")
                           .touch("i.4.1")
                           .end("i.4")
                         .block("i.5")
                           .touch("i.5.1")
                           .end("i.5")
                         .end("i")
                       .finish();
        runTASK(out);
        assertEquals(getStatementCount(),5,"touch count");
        assertTrue(werePerformedInOrder("i.1.1|i.2.1|i.3.1|i.4.1|i.5.1"),"ordering");
    }

    /**
     * Verify following ordering: <pre>
     *   i=[
     *    i.1,
     *    i.2,
     *    i.3=[
     *     i.3.1
     *    ],
     *    i.4=[
     *     i.4.1,
     *     i.4.2,
     *     i.4.3=[
     *      i.4.3.1
     *     ],
     *    i.5,
     *    i.6=[
     *     i.6.1,
     *     i.6.2=[
     *      i.6.2.1,
     *      i.6.2.2=[
     *       i.6.2.2.1,
     *       i.6.2.2.2=[ *EMPTY*
     *       ]
     *      ],
     *     ],
     *     i.6.3
     *    ],
     *    i.7
     *   ]
     * </pre>
     */
    public void testSimpleNestedBlocksC_1_0_0()
    {
        Action out = action("3Level")
                       .block("i")
                         .touch("i.1")
                         .touch("i.2")
                         .block("i.3")
                           .touch("i.3.1")
                           .end("i.3")
                         .block("i.4")
                           .touch("i.4.1")
                           .touch("i.4.2")
                           .block("i.4.3")
                             .touch("i.4.3.1")
                             .end("i.4.3")
                           .end("i.4")
                           .touch("i.5")
                           .block("i.6")
                             .touch("i.6.1")
                             .block("i.6.2")
                               .touch("i.6.2.1")
                               .block("i.6.2.2")
                                 .touch("i.6.2.2.1")
                                 .block("i.6.2.2.2")
                                   .end("i.6.2.2.2")
                                 .end("i.6.2.2")
                               .end("i.6.2")
                             .touch("i.6.3")
                             .end("i.6")
                             .touch("i.7")
                         .end("i")
                       .finish();
        runTASK(out);
        assertEquals(getStatementCount(),12,"touch count");
        assertTrue(werePerformedInOrder("i.1|i.2|i.3.1|i.4.1|i.4.2|i.4.3.1|i.5|i.6.1|i.6.2.1|i.6.2.2.1|i.6.3|i.7"),"ordering");
    }

    public void testPrototypeFlag_1_0_0()
    {
        Action out = action("xyz",MULTIUSE).touch("hi").finish();

        runTASK(out);
        assertEquals(getStatementCount(),1,"touch count @1");
        assertTrue(wasPerformed("hi"));

        runTASK(out);
        assertEquals(getStatementCount(),2,"touch count @2");
        assertTrue(wasPerformed("hi",2));
    }

    public void testTryEach_1_0_0()
    {
        Action out = action("a",TRYEACH,PROTECT)
                      .error("argh")
                      .touch("after-argh@1")
                      .finish();
        runTASK(out);
        assertEquals(getStatementCount(),1,"touch count @outer");
        assertTrue(wasPerformed("after-argh@1"),"touch after throw");

        iniStatementCount();
        out = action("b")
                .block(PROTECT,TRYEACH)
                  .error("argh")
                  .touch("after-argh@2")
                  .end()
                .touch("cleanup")
                .finish();
        runTASK(out);
        assertEquals(getStatementCount(),2,"touch count @inner");
        assertTrue(wasPerformed("after-argh@2"),"touch after throw");
        assertTrue(wasPerformed("cleanup"),"cleanup called");
    }

}


/* end-of-XBALBuilderTest.java */
