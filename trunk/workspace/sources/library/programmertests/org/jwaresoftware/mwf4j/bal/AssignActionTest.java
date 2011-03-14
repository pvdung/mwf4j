/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.bal;

import  java.io.File;
import  java.util.List;
import  java.util.Map;
import  java.util.concurrent.Callable;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.Empties;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.PutMethod;
import  org.jwaresoftware.mwf4j.Sequence;
import  org.jwaresoftware.mwf4j.assign.StoreType;

/**
 * Test suite for {@linkplain AssignAction} and its associated statements.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","bal"})
public final class AssignActionTest extends ActionTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    private <T> AssignAction<T> setv(String id, T value)
    {
        return new AssignAction<T>(id,value);
    }

    private <T> AssignAction<T> sete(String id, String expr, Class<? extends T> ofType)
    {
        AssignAction<T> out = new AssignAction<T>(id);
        out.setTo(id);
        out.setFrom(expr,StoreType.OBJECT,ofType);
        return out;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testAssignVariableValue_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        assertFalse(vars.containsKey("start"));
        runTASK(new AssignAction<Long>("start", 10L));
        assertEquals(vars.get("start"),Long.valueOf(10L),"'start' stored");
    }

    public void testAssignCompositeKeyVariableValue_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        Map<String,Object> config = LocalSystem.newMap(); 
        vars.put("config",config);
        runTASK(new AssignAction<Long>("config.starttime",10L));
        assertEquals(vars.get("config.starttime"),Long.valueOf(10L),"'config.starttime' stored");
        assertNull(config.get("starttime"));
    }

    public void testAssignObjectValue_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        Map<String,Object> config = LocalSystem.newMap(); 
        vars.put("config",config);
        runTASK(new AssignAction<Long>("config['starttime']",StoreType.OBJECT,10L));
        assertEquals(config.get("starttime"),Long.valueOf(10L),"config['starttime'] stored");
        assertNull(vars.get("config.starttime"));
    }

    public void testAssignPropertyValue_1_0_0()
    {
        LocalSystem.setProperty("java.version","ERROR");
        runTASK(new AssignAction<String>("java.version",StoreType.PROPERTY,"12.5.b27"));
        assertEquals(SYSTEM.getConfiguration().getProperty("java.version"),"12.5.b27","java.version stored");
        assertEquals(LocalSystem.getProperty("java.version"),"ERROR");
        LocalSystem.unsetProperty("java.version");
    }

    public void testAssignLocalSystemPropertyValue_1_0_0()
    {
        LocalSystem.setProperty("java.version","ERROR");
        runTASK(new AssignAction<String>("java.version",StoreType.SYSTEM,"07.1.b52"));
        assertEquals(LocalSystem.getProperty("java.version"),"07.1.b52","java.version stored");
        LocalSystem.unsetProperty("java.version");
    }

    public void testAssignMDCValue_1_0_0()
    {
        assertFalse(MDC.has("messageType"));
        runTASK(new AssignAction<Integer>("messageType",StoreType.THREAD,5));
        assertEquals(MDC.get("messageType",Integer.class),Integer.valueOf(5),"MDC value stored");
    }

    public void testAssignByRef_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        vars.put("orig",Integer.valueOf(123));
        AssignAction<Integer> out = new AssignAction<Integer>(ref("copy"),ref("orig",StoreType.OBJECT),Integer.class);
        runTASK(out);
        assertSame(vars.get("copy"),vars.get("orig"),"copy");
        out.setFrom("copy",Integer.class);
        out.setTo(ref("othercopy"));
        runTASK(out);
        assertSame(vars.get("othercopy"),vars.get("orig"),"othercopy");
    }

    public void testCopyVarValue_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        assertFalse(vars.containsKey("a"));
        Double pi = Math.PI;
        vars.put("pi",pi);
        runTASK(new AssignAction<Double>("a",StoreType.DATAMAP,"pi",StoreType.DATAMAP,Double.class));
        assertSame(vars.get("a"),pi,"pi variable copied to 'a'");
    }
    
    public void testCopyDefaultIfSourceNotPresent_1_0_0()
    {
        final File JAVA_HOME= new File(LocalSystem.getProperty("java.home"));
        Map<String,Object> vars = iniDATAMAP();
        assertFalse(vars.containsKey("defaults") || vars.containsKey("defaults.root"));
        assertFalse(vars.containsKey("defaults.root.path"));
        assertFalse(MDC.has("root.path"));

        AssignAction<File> out = new AssignAction<File>("iniroot");
        out.setTo("root.path",StoreType.THREAD);
        out.setFrom("defaults.root.path",StoreType.OBJECT,JAVA_HOME,false,File.class);
        runTASK(out);
        assertSame(MDC.get("root.path",File.class),JAVA_HOME,"default to JAVA_HOME");
    }

    public void testCopyPropertyToProperty_1_0_0()
    {
        LocalSystem.setProperty("mwf4j.debug","ERROR");
        SYSTEM.getConfiguration().getOverrides().setFlag("mwf4j.debug",true);
        assertNull(SYSTEM.getConfiguration().getProperty("DEBUG"));
        
        AssignAction<String> out = new AssignAction<String>();
        out.setTo("DEBUG",StoreType.PROPERTY);
        out.setFrom("mwf4j.debug",StoreType.PROPERTY,String.class);
        runTASK(out);
        assertEquals(SYSTEM.getConfiguration().getProperty("DEBUG"),"true");
    }

    public void testCopyLocalSystemPropertyToObject_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        Map<String,Object> conf = LocalSystem.newMap();
        vars.put("config", conf);

        LocalSystem.setProperty("mwf4j.debug","YeS");
        SYSTEM.getConfiguration().getOverrides().setString("mwf4j.debug","ERROR");
        assertEquals(SYSTEM.getConfiguration().getString("mwf4j.debug"),"ERROR");

        AssignAction<String> out = new AssignAction<String>();
        out.setTo("config['DEBUG']",StoreType.OBJECT);
        out.setFrom("mwf4j.debug",StoreType.SYSTEM,String.class);
        runTASK(out);
        assertEquals(conf.get("DEBUG"),"YeS","'mwf4j.debug' flag set in conf map");
    }

    @SuppressWarnings("unchecked")
    static Class<List<String>> LoS() { //Class<? extends List<?>> any | //List.ofStrings.class
       // Validate.isTrue(List.class.isAssignableFrom(any),"isa List<?>"); Strings.LoS_Type
        return (Class<List<String>>)(Class<?>)List.class;
    }

    public void testCopyLiteralUsingSetFrom_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        AssignAction<List<String>> out = new AssignAction<List<String>>();
        out.setTo("emptyList");
        out.setFrom("xxx",StoreType.NONE,Empties.STRING_LIST,true,LoS());
        runTASK(out);
        assertSame(vars.get("emptyList"),Empties.STRING_LIST,"emptyList assigned");
        List<String> otherEmptyList = LocalSystem.newList();
        out.setFrom(otherEmptyList);
        runTASK(out);
        assertSame(vars.get("emptyList"),otherEmptyList,"emptyList RE-assigned");
    }

    public void testCopyMDCToVar_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        MDC.put("defaults.label", "MWf4J v1.0.0");
        AssignAction<String> out = new AssignAction<String>();
        out.setTo("label");
        out.setFrom("defaults.label",StoreType.THREAD,"UNKNOWN",false,String.class);
        runTASK(out);
        assertEquals(vars.get("label"),"MWf4J v1.0.0","label copied from MDC");
    }

    public void testNoStoreTypeWithKeyIgnored_1_0_0()
    {
        final String KEY = String.valueOf(LocalSystem.currentTimeNanos());
        Map<String,Object> vars = iniDATAMAP();
        runTASK(new AssignAction<Object>("phffht",KEY,StoreType.NONE,Empties.NO_NAME));
        assertFalse(vars.containsKey(KEY));
        assertNull(LocalSystem.getProperty(KEY));
        assertNull(SYSTEM.getConfiguration().getString(KEY));
        assertFalse(MDC.has(KEY));
    }

    /**
     * Verify the following:<pre>
     * a=1
     * b=4
     * c=a+b
     * d=c+b+10
     * e=ERROR
     * </pre>
     **/
    public void testAssignExprBulk_1_0_0()
    {
        Map<String,Object> vars = iniDATAMAP();
        Sequence all= new SequenceAction()
           .add(setv("a",1))
           .add(setv("b",4))
           .add(sete("c","a+b",Integer.class))
           .add(sete("d","c+b+10",Integer.class))
           .add(setv("e","ERROR"));
        runTASK(all);
        assertEquals(vars.get("a"),Integer.valueOf(1),"a");
        assertEquals(vars.get("b"),Integer.valueOf(4),"b");
        assertEquals(vars.get("c"),Integer.valueOf(5),"c");
        assertEquals(vars.get("d"),Integer.valueOf(19),"d");
        assertEquals(vars.get("e"),"ERROR","e");
    }

    /*
     * Verify we can safely pass sub-types into the assign APIs. 
     */
    static class Type1 {
        Type1() { }
    }
    static class Type2 extends Type1 {
        Type2() { }
        static class Calr implements Callable<Type2>  {
            public Type2 call() { return new Type2(); }
        }
        static class Putr implements PutMethod<Type1> {
            public boolean put(String path, Type1 payload) { return false; }
            public boolean putNull(String path) { return false; }
        }
    }

    public void testAssignSubtypes_1_0_0()
    {
        AssignmentStatement<Type1> out = new AssignmentStatement<Type1>();
        out.setGetter(new Type2.Calr());
        out.setPutter(new Type2.Putr());
        out.setPayload(new Type2());
    }
}


/* end-of-AssignActionTest.java */
