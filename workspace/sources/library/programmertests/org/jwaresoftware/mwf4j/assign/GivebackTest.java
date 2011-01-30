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
        assertNotNull(GivebackVar.fromGet("fu"));
        assertNotNull(GivebackVar.fromGet("fu","phffht"));
        assertNotNull(GivebackVar.fromEval("fu"));
        assertNotNull(GivebackVar.fromEval("fu","phffht"));
        assertNotNull(GivebackVar.fromEval("fu",false));
        assertNotNull(GivebackVar.fromEval("fu","phffht",false));
    }

    public void testGivebackVarDatamapObject()
    {
        Map<String,Object> loot = iniForGetData();
        Long now = (Long)loot.get("NOW");
        assertNotNull(now,"installed-NOW");
        assertSame(GivebackVar.fromGet("NOW").call(),now,"get('NOW')");
        assertEquals(GivebackVar.fromGet("version",null).call(),"1.0.4","get('version')");
        assertSame(GivebackVar.fromGet("tv.show").call(),loot.get("tv.show"),"get('tv.show')");
        assertSame(GivebackVar.fromGet("err",null).call(),loot.get("err"),"get('err')");
        assertTrue(Map.class.isInstance(GivebackVar.fromGet("PC").call()),"get('PC') returned a Map");
    }

    public void testGivebackVarArrayIndex()
    {
        Map<String,Object> loot = iniForGetData();
        assertEquals(((String[])loot.get("amigos"))[2],"eddy","amigos[2]");
        GivebackVar<String> out = GivebackVar.fromEval("amigos[2]");
        assertEquals(out.call(),"eddy","get('amigos[2]')");
        out = GivebackVar.fromEval("amigos.0");
        assertEquals(out.call(),"ed","get('amigos.0')");
    }

    public void testGivebackDefaultForOutOfBoundsVarArrayIndex()
    {
        iniForGetData();
        GivebackVar<String> out = GivebackVar.fromEval("amigos[999]","bob",false);
        assertEquals(out.call(),"bob","get('amigos[999]','bob')");
    }

    @SuppressWarnings("unchecked")
    public void testGivebackVarMapElements()
    {
        Map<String,Object> loot = iniForGetData();
        final Map<String,Object> PC = (Map<String,Object>)loot.get("PC");
        GivebackVar<String> out = GivebackVar.fromEval("PC['os']",true);
        assertEquals(out.call(),PC.get("os"),"get('PC{os}')");
        out = new GivebackVar<String>("PC.memory");
        assertEquals(out.call(),PC.get("memory"),"get('PC.memory')");
        assertEquals(GivebackVar.fromEval("PC.users.1").call(),"edd","get('PC.users.1')");
        assertEquals(GivebackVar.fromEval("PC.users[0]").call(),"ed","get('PC.users[0]')");
    }

    public void testGivebackVarObjectField()
    {
        Map<String,Object> loot = iniForGetData();
        Throwable thr = ((RollupIssue)loot.get("err")).currentIssues().get(1).getCause();
        assertNotNull(thr,"err.issues[1].cause");
        GivebackVar<Throwable> gv = new GivebackVar<Throwable>("err.issues[1].cause");
        assertSame(gv.call(),thr,"get('err.issues[1].cause')");
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testFailIfInvalidVarExpr()
    {
        iniForGetData();
        Object o = GivebackVar.fromEval("amigos[100").call();
        System.err.println("IT('amigos[100'): "+o);
        fail("Should not survive a get of invalid expression");
    }

    @Test(expectedExceptions= {MWf4JException.class})
    public void testFailIfGetMissingVarBean()
    {
        Map<String,Object> loot = iniForGetData();
        assertFalse(loot.containsKey("no-such-element"));
        GivebackVar.fromEval("no-such-element").call();
        fail("Should not survive a get of missing bean");
    }

    public void testGivebackDefaultIfInvalidVarExprAndNotHaltIfError()
    {
        iniForGetData();
        GivebackVar<String> out = GivebackVar.fromEval("amigos[100","EDDIE",false);
        Object o = out.call();
        System.out.println("IT('amigos[100'): "+o);
        assertEquals(o,"EDDIE","get('amigos[100','EDDIE')");
    }

    public void testGivebackNullIfGetMissingVarBeanAndNotHaltIfError()
    {
        String key;
        assertFalse(iniForGetData().containsKey("object"));
        key = "object."+rInt();
        Object b = GivebackVar.fromEval(key,false).call();
        System.out.println("IT("+key+"): "+b);
        assertNull(b,"<no-such-object> is null");
        key = "n"+rInt();
        Object a = GivebackVar.fromEval(key,false).call();
        System.out.println("IT("+key+"): "+a);
        assertNull(a,"<random> key is null");
        a = GivebackVar.fromEvalOfOptional(key).call();
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
        GivebackFrom<Long> out = new GivebackFrom<Long>();
        assertSame(out.call(),now,"out.call()");
    }
    
    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackPredefinedFromElementIfNoKeyInstalled()
    {
        Map<String,Object> loot = iniForGetData();
        assertFalse(loot.containsKey(GivebackFrom.ITEM_NAME));
        new GivebackFrom<Long>().call();
        fail("Should not be able to retrieve unless '"+GivebackFrom.ITEM_NAME+"' defined");
    }

    @SuppressWarnings("unchecked")
    public void testGivebackAdhocMapEntries()
    {
        Map<String,Object> loot = LocalSystem.newMap();
        addSimple(loot);
        final Map<String,Object> PC = (Map<String,Object>)loot.get("PC");
        GivebackMapEntry<String> out = new GivebackMapEntry<String>(loot,"PC['os']");
        assertEquals(out.call(),PC.get("os"),"get('PC{os}')");
        out = new GivebackMapEntry<String>(loot,"PC.memory","ERROR",true);
        assertEquals(out.call(),PC.get("memory"),"get('PC.memory')");
        out = new GivebackMapEntry<String>(loot,"version");
        assertEquals(out.call(),loot.get("version"),"get('version')");
        out = new GivebackMapEntry<String>(loot,"no-such-object-there","DEFAULTED",false);
        assertEquals(out.call(),"DEFAULTED","get('<missing-key>')");
        assertEquals(new GivebackMapEntry<Long>(loot,"NOW").call(),loot.get("NOW"),"get('NOW')");
    }

    public void testGivebackStatement()
    {
        GivebackStatement out = new GivebackStatement(ControlFlowStatement.nullINSTANCE);
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//once
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//twice, same obj
        assertNotNull(out.getId());
        assertSame(out.makeStatement(null),ControlFlowStatement.nullINSTANCE);
        out = new GivebackStatement("PHFFHT",ControlFlowStatement.nullINSTANCE);
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//once
        assertSame(out.call(),ControlFlowStatement.nullINSTANCE);//twice, same obj
        assertSame(out.makeStatement(null),ControlFlowStatement.nullINSTANCE);
        assertEquals(out.getId(),"PHFFHT");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackVarIfNoCurrentHarness()
    {
        new GivebackVar<String>("fubar").call();
        fail("Should not be able to giveback var from no HARNESS");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackFromIfNoCurrentHarness()
    {
        new GivebackFrom<String>().call();
        fail("Should not be able to giveback from no HARNESS");
    }

    @Test(expectedExceptions={GivebackException.class})
    public void testFailGivebackFromIfNoFromInstalled()
    {
        iniForGetData();
        new GivebackFrom<String>().call();
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
}


/* end-of-GivebackTest.java */
