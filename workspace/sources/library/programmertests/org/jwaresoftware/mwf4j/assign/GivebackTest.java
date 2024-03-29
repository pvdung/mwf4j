/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.diagnosis.IssueItem;
import  org.jwaresoftware.gestalt.diagnosis.RollupIssue;
import  org.jwaresoftware.gestalt.diagnosis.RollupIssueItem;
import  org.jwaresoftware.gestalt.system.GetPropertyMethod;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.ControlFlowStatement;
import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.LocalSystemFixture;
import  org.jwaresoftware.mwf4j.LocalSystemHarness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;
import  org.jwaresoftware.mwf4j.MWf4JException;

/**
 * Test suite for the various "give-back" callable adapters in the
 * MWf4J library. These givebacks are almost always used to supply 
 * data or cursor values to an assignment statement.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","assignment"})
public final class GivebackTest extends AssignHelperTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected Map<String,Object> iniForGetData()
    {
        Map<String,Object> shared= super.iniForGetData();
        shared.put("label.LCL", "Hola mundo!");
        addComposite("err",shared);
        return shared;
    }

    /**
     * Install something we can 'dig' into via non-trivial expressions.
     **/
    private void addComposite(String itemKey, Map<String,Object> datamap)
    {
        IssueItem i1 = new IssueItem("NOT_ENTITLED","Caller not entitled to: [CEO_SALARY, MGR_BONUS]");
        RollupIssueItem bean = new RollupIssueItem(i1);
        Throwable x = new RuntimeException("Unrecogized pay identifier: 'Discover'");
        x.fillInStackTrace();
        IssueItem i2 = new IssueItem("BAD_PAY_SYST","Unknown payment type [Visa,Amex,CASH,Paypal]");
        i2.setCause(x);
        bean.addLast(i2);
        datamap.put(itemKey,bean);
    }

    private Fixture newFIXTURE()
    {
        return new LocalSystemFixture(SYSTEM);
    }

    private static final String LABEL = "${label.${dayofweek}}";
    private void initForFreeze()
    {
        LocalSystem.setProperty(LABEL,"ERROR!");
        LocalSystem.setProperty("dayofweek","FRI");
        LocalSystem.setProperty("label.FRI","HelloWorld");
        SYSTEM.getConfiguration().getOverrides().setProperty("DFLT_MSG","WackaWooWoo");
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testGivebackLiteralObject()
    {
        GivebackValue<Object> gv = new GivebackValue<Object>();
        assertNull(gv.call(),"isnull");
        gv = new GivebackValue<Object>(this);
        assertSame(gv.call(),this,"isthis");
    }

    public void testGivebackLiteralNumber()
    {
        GivebackValue<Long> gv = new GivebackValue<Long>();
        assertNull(gv.call(),"isnull");
        gv = new GivebackValue<Long>(123L);
        assertEquals(gv.call(),Long.valueOf(123L),"is-123");
    }

    public void testGivebackVarDataFactoryCtors()
    {
        assertNotNull(GivebackVar.fromGet("fu",String.class));
        assertNotNull(GivebackVar.fromGet("fu","phffht",String.class));
        assertNotNull(GivebackVar.fromGet("fu","zoink!",String.class,true));
        assertNotNull(GivebackVar.fromEval("fu",String.class));
        assertNotNull(GivebackVar.fromEval("fu",Long.valueOf(1L),Long.class));
        assertNotNull(GivebackVar.fromEval("fu",String.class,false));
        assertNotNull(GivebackVar.fromEval("fu","phffht",String.class,false));
    }

    public void testGivebackVarDatamapObject()
    {
        Map<String,Object> loot = iniForGetData();
        Long now = (Long)loot.get("NOW");
        assertNotNull(now,"installed-NOW");
        assertSame(GivebackVar.fromGet("NOW",Long.class).call(),now,"get('NOW')");
        assertEquals(GivebackVar.fromGet("version",null,String.class).call(),"1.0.4","get('version')");
        assertSame(GivebackVar.fromGetS("tv.show").call(),loot.get("tv.show"),"get('tv.show')");
        assertSame(GivebackVar.fromGet("err",null,RollupIssue.class).call(),loot.get("err"),"get('err')");
        assertTrue(Map.class.isInstance(GivebackVar.fromGet("PC",Map.class).call()),"get('PC') returned a Map");
    }

    public void testGivebackVarArrayIndex()
    {
        Map<String,Object> loot = iniForGetData();
        assertEquals(((String[])loot.get("amigos"))[2],"eddy","amigos[2]");
        GivebackVar<String> out = GivebackVar.fromEval("amigos[2]",String.class);
        assertEquals(out.call(),"eddy","get('amigos[2]')");
        out = GivebackVar.fromEvalS("amigos.0");
        assertEquals(out.call(),"ed","get('amigos.0')");
    }

    public void testGivebackDefaultForOutOfBoundsVarArrayIndex()
    {
        iniForGetData();
        GivebackVar<String> out = GivebackVar.fromEval("amigos[999]","bob",String.class,false);
        assertEquals(out.call(),"bob","get('amigos[999]','bob')");
    }

    @SuppressWarnings("unchecked")
    public void testGivebackVarMapElements()
    {
        Map<String,Object> loot = iniForGetData();
        final Map<String,Object> PC = (Map<String,Object>)loot.get("PC");
        GivebackVar<String> out = GivebackVar.fromEval("PC['os']",String.class,true);
        assertEquals(out.call(),PC.get("os"),"get('PC{os}')");
        out = new GivebackVar<String>("PC.memory",String.class);
        assertEquals(out.call(),PC.get("memory"),"get('PC.memory')");
        assertEquals(GivebackVar.fromEvalS("PC.users.1").call(),"edd","get('PC.users.1')");
        assertEquals(GivebackVar.fromEvalS("PC.users[0]").call(),"ed","get('PC.users[0]')");
    }

    public void testGivebackVarObjectField()
    {
        Map<String,Object> loot = iniForGetData();
        Throwable thr = ((RollupIssue)loot.get("err")).currentIssues().get(1).getCause();
        assertNotNull(thr,"err.issues[1].cause");
        GivebackVar<Throwable> gv = new GivebackVar<Throwable>("err.issues[1].cause",Throwable.class);
        assertSame(gv.call(),thr,"get('err.issues[1].cause')");
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testFailIfInvalidVarExpr()
    {
        iniForGetData();
        Object o = GivebackVar.fromEval("amigos[100",Object.class).call();
        System.err.println("IT('amigos[100'): "+o);
        fail("Should not survive a get of invalid expression");
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testFailIfGetMissingVarBean()
    {
        Map<String,Object> loot = iniForGetData();
        assertFalse(loot.containsKey("no-such-element"));
        GivebackVar.fromEval("no-such-element",Object.class).call();
        fail("Should not survive a get of missing bean");
    }

    public void testGivebackDefaultIfInvalidVarExprAndNotHaltIfError()
    {
        iniForGetData();
        GivebackVar<String> out = GivebackVar.fromEval("amigos[100","EDDIE",String.class,false);
        Object o = out.call();
        System.out.println("IT('amigos[100'): "+o);
        assertEquals(o,"EDDIE","get('amigos[100','EDDIE')");
    }

    public void testGivebackNullIfGetMissingVarBeanAndNotHaltIfError()
    {
        String key;
        assertFalse(iniForGetData().containsKey("object"));
        key = "object."+rInt();
        Object b = GivebackVar.fromEval(key,Object.class,false).call();
        System.out.println("IT("+key+"): "+b);
        assertNull(b,"<no-such-object> is null");
        key = "n"+rInt();
        Object a = GivebackVar.fromEval(key,Object.class,false).call();
        System.out.println("IT("+key+"): "+a);
        assertNull(a,"<random> key is null");
        a = GivebackVar.fromEvalOfOptional(key,Object.class).call();
        assertNull(a,"<random> key is null");
    }

    public void testGivebackNull()
    {
        assertNull(new GivebackNull<Object>().call(),"isnull[obj]");
        assertNull(new GivebackNull<Number>().call(),"isnull[num]");
    }

    public void testGivebackLocalSystemProperty()
    {
        assertEquals(GivebackProperty.fromSystem("java.version",null).call(),LocalSystem.getProperty("java.version"),"java.version");
        LocalSystem.setProperty("java.version", "123456789.D");
        assertEquals(new GivebackProperty("java.version","ERROR",GivebackProperty.Source.SYSTEM).call(),"123456789.D","java.version");
    }

    public void testGivebackProperty()
    {
        //remember our ojg.ns is setup as 'mwf4j' which creates a auto-preference for get/set
        iniForGetProperty();
        assertEquals(GivebackProperty.fromHarness("java.version").call(),LocalSystem.getProperty("java.version"),"java.version");
        LocalSystem.setProperty("java.version", "123456789.H");
        assertEquals(GivebackProperty.fromHarness("java.version","ERROR").call(),"123456789.H","java.version");
        LocalSystem.unsetProperty("java.version");
        assertEquals(new GivebackProperty("java.version").call(),LocalSystem.getProperty("java.version"),"java.version");
        LocalSystem.setProperty(MWf4J.NS+".java.version", "012345");
        assertEquals(GivebackProperty.fromHarness("java.version","ERROR").call(),"012345","[mwf4j.]java.version");
    }

    public void testGivebackMDC()
    {
        assertFalse(MDC.has("ickyblorf.flag"));
        assertNull(new GivebackMDC<Object>("ickyblorf.flag",Object.class).call());
        Long num = Long.valueOf(25L);
        GivebackMDC<Long> out = new GivebackMDC<Long>("icky.number",Long.class); 
        MDC.put("icky.number", num);
        assertSame(out.call(),num,"icky.number");
        MDC.clr("icky.number");
        assertNull(out.call(),"icky.number(2)");
    }
    
    public void testGivebackMDCDefaultIfMissing()
    {
        assertFalse(MDC.has("icky.number"));
        Long def = Empties.LONG;
        GivebackMDC<Long> out = new GivebackMDC<Long>("icky.number",Long.class,def); 
        assertSame(out.call(),def,"default for 'icky.number'");
    }
    
    public void testGivebackPredefinedFromElement()
    {
        Map<String,Object> loot = iniForGetData();
        Long now = (Long)loot.get("NOW");
        assertNotNull(now,"installed-NOW");
        loot.put(GivebackFrom.ITEM_NAME, "NOW");
        GivebackFrom<Long> out = new GivebackFrom<Long>(Long.class);
        assertSame(out.call(),now,"out.call()");
    }
    
    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackPredefinedFromElementIfNoKeyInstalled()
    {
        Map<String,Object> loot = iniForGetData();
        assertFalse(loot.containsKey(GivebackFrom.ITEM_NAME));
        new GivebackFrom<Long>(Long.class).call();
        fail("Should not be able to retrieve unless '"+GivebackFrom.ITEM_NAME+"' defined");
    }

    @SuppressWarnings("unchecked")
    public void testGivebackAdhocMapEntries()
    {
        Map<String,Object> loot = LocalSystem.newMap();
        addSimple(loot);
        final Map<String,Object> PC = (Map<String,Object>)loot.get("PC");
        GivebackMapEntry<String> out = new GivebackMapEntry<String>(loot,"PC['os']",String.class);
        assertEquals(out.call(),PC.get("os"),"get('PC{os}')");
        out = new GivebackMapEntry<String>(loot,"PC.memory","ERROR",String.class,true);
        assertEquals(out.call(),PC.get("memory"),"get('PC.memory')");
        out = new GivebackMapEntry<String>(loot,"version",String.class);
        assertEquals(out.call(),loot.get("version"),"get('version')");
        out = new GivebackMapEntry<String>(loot,"no-such-object-there","DEFAULTED",String.class,false);
        assertEquals(out.call(),"DEFAULTED","get('<missing-key>')");
        assertEquals(new GivebackMapEntry<Long>(loot,"NOW",Long.class).call(),loot.get("NOW"),"get('NOW')");
    }

    public void testGivebackStatement()
    {
        Fixture environ = new LocalSystemHarness();
        GivebackStatement out = new GivebackStatement(ControlFlowStatement.nullINSTANCE);
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//once
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//twice, same obj
        assertNotNull(out.getId());
        assertSame(out.buildStatement(null,environ),ControlFlowStatement.nullINSTANCE);
        out = new GivebackStatement("PHFFHT",ControlFlowStatement.nullINSTANCE);
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//once
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//twice, same obj
        assertSame(out.buildStatement(null,environ),ControlFlowStatement.nullINSTANCE);
        assertEquals(out.getId(),"PHFFHT");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackVarIfNoCurrentHarness()
    {
        new GivebackVar<String>("fubar",String.class).call();
        fail("Should not be able to giveback var from no HARNESS");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackFromIfNoCurrentHarness()
    {
        new GivebackFrom<String>(String.class).call();
        fail("Should not be able to giveback from no HARNESS");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackFromIfNoFromInstalled()
    {
        iniForGetData();
        new GivebackFrom<String>(String.class).call();
        fail("Should not be able to giveback from no VALUE");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackMDCIfBadTypeCast()
    {
        MDC.put("i", Integer.valueOf(10));
        new GivebackMDC<String>("i",String.class).call();
        fail("Should not be able to cast Integer to String?!");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackPropertyIfBadGetMethod()
    {
        final String MISSING = "__"+LocalSystem.currentTimeNanos();
        assertNull(LocalSystem.getProperty("wackywackywoowoo"));
        assertEquals(GivebackProperty.fromSystem("wackywackywoowoo",MISSING).call(),MISSING);
        LocalSystem.setUnderlay(new GetPropertyMethod() {
            public String getProperty(String propertyname) {
                throw new IllegalArgumentException("Phffht, no stinkin' "+MISSING);
            }
        });
        try {
            GivebackProperty.fromSystem("wackywackywoowoo","ERROR").call();
            fail("Should not be able to give back 'wackywackywoowoo'");
        } finally {
            LocalSystem.clrUnderlay();
        }
    }

    @SuppressWarnings("unchecked")
    public void testFreezeDeclaredLiteral_1_0_0()
    {
        initForFreeze();
        GivebackValue<String> out = new GivebackValue<String>(LABEL);
        assertEquals(out.call(),LABEL,"get("+LABEL+")-before-freeze");
        GivebackValue<String> cpy = (GivebackValue<String>)out.clone();
        out.freeze(newFIXTURE());
        assertEquals(out.call(),"HelloWorld","get("+LABEL+")-after-freeze");
        assertEquals(cpy.call(),LABEL,"get("+LABEL+")-after-freeze-for-copy");
    }

    private static final String DFLT_MSG = "${$p:DFLT_MSG?ERROR}";

    @SuppressWarnings("unchecked")
    public void testFreezeDeclaredVarKeys_1_0_0()
    {
        Map<String,Object> vars = iniForGetData();
        initForFreeze();
        SYSTEM.getConfiguration().getOverrides().setProperty("label.FRI","label.LCL");
        GivebackVar<String> out = GivebackVar.fromGet(LABEL,DFLT_MSG,String.class,false);
        assertEquals(out.call(),DFLT_MSG,"get()-before-freeze");
        GivebackVar<String> cpy = (GivebackVar)out.clone();
        out.freeze(newFIXTURE());
        assertEquals(out.call(),"Hola mundo!","get()-after-freeze");
        vars.remove("label.LCL");
        assertEquals(out.call(),"WackaWooWoo","get()-after-freeze [default]");
        assertEquals(cpy.call(),DFLT_MSG,"get()-before-freeze-for-copy");
    }

    public void testFreezePropertyNames_1_0_0()
    {
        iniForGetProperty();
        initForFreeze();
        GivebackProperty out = GivebackProperty.fromHarness("label.${dayofweek}",DFLT_MSG);
        GivebackProperty cpy = (GivebackProperty)out.clone();
        assertEquals(cpy.call(),DFLT_MSG,"get()-before-freeze");
        out.freeze(MDC.currentHarness());
        assertEquals(out.call(),"HelloWorld","get(label.FRI)-after-freeze");
        LocalSystem.unsetProperty("label.FRI");
        assertEquals(out.call(),"WackaWooWoo","get()-after-freeze [default]");
        assertEquals(cpy.call(),DFLT_MSG,"get()-after-freeze-for-copy");
    }

    @SuppressWarnings("unchecked")
    public void testFreezeMapEntryKeys_1_0_0()
    {
        Map<String,Object> vars = iniForGetData();
        initForFreeze();
        SYSTEM.getConfiguration().getOverrides().setProperty("label.FRI","label.LCL");
        GivebackMapEntry<String> out = new GivebackMapEntry<String>(vars,LABEL,DFLT_MSG,String.class,false);
        GivebackMapEntry<String> cpy = (GivebackMapEntry<String>)out.clone();
        assertEquals(out.call(),DFLT_MSG,"get()-before-freeze");
        out.freeze(newFIXTURE());
        assertEquals(out.call(),"Hola mundo!","get()-after-freeze");
        vars.remove("label.LCL");
        assertEquals(out.call(),"WackaWooWoo","get()-after-freeze [default]");
        assertEquals(cpy.call(),DFLT_MSG,"get()-before-freeze-for-copy");
    }
}


/* end-of-GivebackTest.java */
