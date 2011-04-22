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
import  org.jwaresoftware.mwf4j.MDC;
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
// ---------------------------------------------------------------------------------------------------------
// ---------------------------------------- [ Misc Setup Methods ] -----------------------------------------
// ---------------------------------------------------------------------------------------------------------

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

    private void setString(Harness h, String name, String value)
    {
        h.getConfiguration().getOverrides().setString(name, value);
    }

    private void setProperty(String name, String value)
    {
        LocalSystem.setProperty(name,value);
    }

// ---------------------------------------------------------------------------------------------------------
// ------------------------------------------- [ The Test Cases ] ------------------------------------------
// ---------------------------------------------------------------------------------------------------------

    @Test(groups={"builders-baseline"})
    public void testAnonymousEmptyBuilder_1_0_0()
    {
        runTASK(action().build());
    }

    @Test(groups={"builders-baseline"})
    public void testNamedEmptyBuilder_1_0_0()
    {
        XBALBuilder out = action(me());
        assertEquals(out.getId(),me(),"id");
        runTASK(out.build());
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
                       .build();
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
                      .set("f",false)
                      .set("i",10)
                      .set("L",101L)
                      .set("pi",Math.PI)
                      .set("I",this)
                      .set(var("id"),me)
                      .set(variable("ID"),me)
                      .build();

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
                       .build();
        runTASK(out);
        Object oid = vars.get("oid");
        LocalSystem.show("oid=",oid);
        assertTrue(oid instanceof Long, "oid is installed");
        assertSame(vars.get("oid.copy1"),oid,"oid.copy1 (var)");
        assertSame(vars.get("oid.copy2"),oid,"oid.copy2 (ref)");
    }

    @Test(dependsOnMethods={"testSimpleVarAssign_1_0_0"})
    public void testDeclaredVarAssign_1_0_0()
    {
        Variables vars = iniDATAMAP();
        Action out = action("test",DECLARABLES)
                       .set("rev","${java.version}")
                       .set("buildnum","0011")
                       .set("rev-dist","${$system:java.version}")
                       .set("rev.label","V_${$var:rev?ERROR}.build${$v:buildnum}")
                       .build();

        Harness h = newHARNESS(out);
        setString(h,"java.version","123.456");

        runTASK(h);

        Object rev = vars.get("rev");
        LocalSystem.show("rev=",rev);
        assertEquals(rev,"123.456","rev");
        assertEquals(vars.get("rev.label"),"V_123.456.build0011","rev.label");
        assertEquals(vars.get("rev-dist"),System.getProperty("java.version"),"rev-dist");
    }

    @Test(groups={"builders-baseline"})
    public void testSimplePropertyAssign_1_0_0()
    {
        assertNull(LocalSystem.getProperty("DEBUG"));
        Action out = action()
                      .set(property("DEBUG"),true)
                      .set(property("name"),"Harry")
                      .set(env("maxwait"),13)
                      .set(mdc("SESSIONID"),1010L)
                      .build();

        Harness h = newHARNESS(out);
        runTASK(h);

        assertEquals(getString(h,"DEBUG"),"true","p(DEBUG)");
        assertEquals(getString(h,"name"),"Harry","p(name)");
        assertEquals(getString(h,"maxwait"),"13","p(maxwait)");
        assertEquals(MDC.get("SESSIONID",Long.class),Long.valueOf(1010L),"SESSIONID");
        assertNull(LocalSystem.getProperty("DEBUG"),"LocalSystem('DEBUG')");
    }

    @Test(dependsOnMethods={"testSimplePropertyAssign_1_0_0"})
    public void testDeclaredPropertyAssign_1_0_0()
    {
        Variables vars = iniDATAMAP();
        Action out = action("test",DECLARABLES)
                        .set("rev","${java.version}")
                        .set("wait0","${WAIT}")
                        .set("debug","${debug.flag}")
                        .set("waits","${$p:timeout.ms?0s}")
                        .build();
        Harness harness = newHARNESS(out);
        setProperty("DEBUG","ERROR");
        setProperty("TIMEOUT","4s");
        setProperty("WAIT","${TIMEOUT}");
        setProperty("debug.flag","ERROR");
        setString(harness,"DEBUG","on");
        setString(harness,"debug.flag","${DEBUG}");
        setString(harness,"timeout.ms","${TIMEOUT}");
        runTASK(harness);
        Object rev = vars.get("rev");
        LocalSystem.show("rev=",rev);
        assertEquals(rev,System.getProperty("java.version"),"rev");
        assertEquals(vars.get("wait0"),"4s","wait0");
        assertEquals(vars.get("debug"),"on","debug");
        assertEquals(vars.get("waits"),"4s","waits");
    }

    public void testTurnOnOffDeclarableSupportAsYouGo_1_0_0()
    {
        final String Xpected = LocalSystem.getProperty("java.version");
        assertNotNull(Xpected);
        Variables vars = iniDATAMAP();
        Action out = action()
                        .set("rev0","${java.version}")
                        .block(DECLARABLES)
                          .set("rev1","${java.version}")
                          .end()
                        .block(NO_DECLARABLES)
                          .set("rev2","${java.version}")
                          .end()
                        .block(DECLARABLES)
                          .set("rev3","${java.version}")
                          .end()
                        .build();
        runTASK(out);
        assertEquals(vars.get("rev0"),"${java.version}","rev0{unresolved}");
        assertEquals(vars.get("rev1"),Xpected,"rev1{resolved}");
        assertEquals(vars.get("rev2"),"${java.version}","rev2{unresolved}");
        assertEquals(vars.get("rev3"),Xpected,"rev3{resolved}");
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
    @Test(groups={"builders-baseline"},dependsOnMethods={"testSimpleVarAssign_1_0_0"})
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
                      .build();
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
    @Test(groups={"builders-baseline"},dependsOnMethods={"testSimpleVarAssign_1_0_0"})
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
                       .build();
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
    @Test(dependsOnMethods={"testSimpleNestedBlocksA_1_0_0"})
    public void testSimpleNestedBlocksC_1_0_0()
    {
        Action out = action("CLevel")
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
                       .build();
        runTASK(out);
        assertEquals(getStatementCount(),12,"touch count");
        assertTrue(werePerformedInOrder("i.1|i.2|i.3.1|i.4.1|i.4.2|i.4.3.1|i.5|i.6.1|i.6.2.1|i.6.2.2.1|i.6.3|i.7"),"ordering");
    }

    public void testPrototypeFlag_1_0_0()
    {
        Action out = action(MULTIUSE).touch("hi").build();

        runTASK(out);
        assertEquals(getStatementCount(),1,"touch count @1");
        assertTrue(wasPerformed("hi"));

        runTASK(out);
        assertEquals(getStatementCount(),2,"touch count @2");
        assertTrue(wasPerformed("hi",2));
    }

    public void testTryEach_1_0_0()
    {
        Action out = action(TRYEACH,PROTECTED)
                      .error("argh")
                      .touch("after-argh@1")
                      .build();
        runTASK(out);
        assertEquals(getStatementCount(),1,"touch count @outer");
        assertTrue(wasPerformed("after-argh@1"),"touch after throw");

        iniStatementCount();
        out = action("b")
                .block(PROTECTED,TRYEACH)
                  .error("argh")
                  .touch("after-argh@2")
                  .end()
                .touch("cleanup")
                .build();
        runTASK(out);
        assertEquals(getStatementCount(),2,"touch count @inner");
        assertTrue(wasPerformed("after-argh@2"),"touch after throw");
        assertTrue(wasPerformed("cleanup"),"cleanup called");
    }

    public void testSimpleFlatIff_1_0_0()
    {
        Action out = action()
                         .iff(any(istrue("foo"),notnull("foo")))
                            .error("ARGH!")
                            .endif()
                         .set("foo")
                         .set("LEN",1000)
                         .iff(all(notnull("LEN"),istrue("foo"),istrue("LEN==1000")))
                             .touch("OKEY!")
                             .endif()
                         .build();
        runTASK(out);
        assertEquals(getStatementCount(),1,"touch count");
        assertTrue(wasPerformed("OKEY!"),"istrue worked");
    }

    @Test(dependsOnMethods={"testSimpleFlatIff_1_0_0"})
    public void testSimpleNestedIff_1_0_0()
    {
        Action out = action()
                        .iff(isnull("time"))
                            .set("time",LocalSystem.currentTimeNanos())
                            .touch("a")
                            .iff(notnull("time"))
                                .touch("b")
                                .nil("time")
                                .iff(not(not(isnull("time"))))
                                  .touch("c")
                                .endif()
                            .endif()
                        .end()//NB:make sure end() same as endif()
                        .build();
        runTASK(out);
        assertEquals(getStatementCount(),3,"touch count");
        assertTrue(werePerformedInOrder("a|b|c"),"performed a|b|c");
    }

    public void testSimpleFlatIfElse_1_0_0()
    {
        Variables vars = iniDATAMAP();
        Action out = action()
                        .set("flag")
                        .ife(istrue("flag"))
                            .set("iah",10)
                            .touch("OKEY!")
                            .otherwise()
                              .error("BLEECH")
                              .endif()
                        .build();
        runTASK(out);
        assertTrue(wasPerformed("OKEY!"),"if block run");
        assertEquals(vars.get("iah",Number.class),Integer.valueOf(10),"iah");
    }

    @Test(dependsOnMethods={"testSimpleFlatIfElse_1_0_0"})
    public void testSimpleNestedIfElse_1_0_0()
    {
        Variables vars = iniDATAMAP();
        Action out = action("main")
                        .set("started")
                        .set("n",0)
                        .ife(isfalse("started"))
                            .never()
                        .otherwise()
                            .set("n",get("n+1"))
                            .block("x")
                                .ife(istrue("n==1"))
                                    .set("pi",3.14)
                                    .touch("OKEY!")
                                    .otherwise()
                                        .never()
                                    .endif()
                                .touch("OKEY!")
                                .end("x")
                            .touch("OKEY!")
                            .end()//NB:make sure end() same as endif()
                        .build();
        runTASK(out);
        assertTrue(wasPerformed("OKEY!",3),"touched('OKEY!')");
        assertNotNull(vars.get("pi",Double.class),"pi");
    }

    @Test(expectedExceptions={IllegalStateException.class})
    public void testFailIfEndWithoutInner_1_0_0()
    {
        action()
            .set("about-to-barf")
            .end(); //Nothing to end...
        fail("Should not get past unmatched 'end()'");
    }

    @Test(expectedExceptions={IllegalStateException.class})
    public void testFailIfEndNotTopmostInner_1_0_0()
    {
        try {
            action()
                .block("a")
                  .set("about-to-barf")
                  .end("b"); //Wrong block named to end!
            fail("Should not get past mismatched 'end()'");
        } catch(IllegalStateException Xpected) {
            String error = Xpected.getMessage();
            LocalSystem.showerror("Barfage => ", Xpected);
            assertTrue(error.indexOf("'innerblock.id is b' should be true")>0,"Xpected error");
            throw Xpected;
        }
    }

    @Test(expectedExceptions={IllegalStateException.class})
    public void testFailBuildUnendedIff_1_0_0()
    {
        try {
            action("x")
                .iff(istrue())
                    .never()
                .build();//Should not work due to dangling inner-block
            fail("Should not be able to build malformed builder");
        } catch(IllegalStateException Xpected) {
            LocalSystem.showerror("Barfage => ", Xpected);
            throw Xpected;
        }
    }
}


/* end-of-XBALBuilderTest.java */
