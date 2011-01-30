/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Map;
import  java.util.concurrent.ConcurrentMap;
import  java.util.concurrent.TimeUnit;

import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

import  org.jwaresoftware.gestalt.system.LocalSystem;
import  org.jwaresoftware.mwf4j.MDC;
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
public final class SavebackTest extends AssignHelperTestSkeleton
{
//  ---------------------------------------------------------------------------------------
//  Data beans to ensure access-by-expression working...
//  ---------------------------------------------------------------------------------------

    public static class Person {
        long _birth=LocalSystem.currentTimeMillis();
        char _gender='*';
        String _name;
        Person() { }
        public void setBirthDate(long dt) { _birth = dt; }
        public long getBirthDate() { return _birth; }
        public void setGender(char c) { _gender = c; }
        public char getGender() { return _gender; }
        public void setName(String s) { _name = s; }
        public String getName() { return _name; }
    }

    public static class Toy {
        private String _name;
        private String _sound="sqweak";
        Toy(String name) { _name = name; }
        public void setSound(String sound) { _sound = sound; }
        public String getSound() { return _sound; }
        public void setName(String s) { _name = s; }
        public String getName() { return _name; }
    }

    public static class Baby extends Person {
        private long _naptime;
        private Map<String,Toy> _toys= LocalSystem.newMap();
        Baby(String name) { _name = name; }
        public void setNapTime(long whenInDay) { _naptime = whenInDay; }
        public long getNapTime() { return _naptime; }
        public Map<String,Toy> getToys() { return _toys; }
    }

    static class Pension {//NOT PUBLIC ON PURPOSE (to test access rules)
        private double _amt;
        Pension(double amt) { _amt = amt; }
        public void setAmount(double amt) { _amt = amt; }
        public double getAmount() { return _amt; }
    }

    public static class Grandma extends Person {
        private String _car;
        private Baby[] _grandkids= new Baby[2];
        private Pension _pension= new Pension(1.0d);
        public Grandma() { _name="grandma"; }
        public void setCar(String model) { _car = model; }
        public String getCar() { return _car; }
        public void setGrandKid(int i, Baby kid) { _grandkids[i] = kid; }
        public Baby getGrandKid(int i) { return _grandkids[i]; }
        public void setGrandKid(Baby[] kids) { _grandkids = kids; }
        public Baby[] getGrandKid() { return _grandkids; }
        public Pension getPension() { return _pension; }
    }

//  ---------------------------------------------------------------------------------------
//  Harness preparation methods
//  ---------------------------------------------------------------------------------------

    protected Map<String,Object> iniForGetData()
    {
        Map<String,Object> shared= super.iniForGetData();
        return shared;
    }

//  ---------------------------------------------------------------------------------------
//  The test cases (1 per method)
//  ---------------------------------------------------------------------------------------

    public void testSavebackDatamapObject()
    {
        Map<String,Object> loot = iniForGetData();
        assertFalse(loot.containsKey("greeting") || loot.containsKey("app.id[0]"));
        SavebackVar<String> out = SavebackVar.toMap();
        out.put("greeting", "Hello World!");
        assertEquals(loot.get("greeting"),"Hello World!","put('greeting')");
        out.put("app.id[0]", "MWf4J");
        assertEquals(loot.get("app.id[0]"),"MWf4J","put('app.id[0]')");
    }

    public void testSavebackDatamapObjectWithExpressionMode()
    {
        Map<String,Object> loot = iniForGetData();
        assertFalse(loot.containsKey("greeting"));
        assertTrue(loot.containsKey("tv.show") && !"Scooby Doo".equals(loot.get("tv.show")));
        assertTrue(Number.class.isInstance(loot.get("NOW")));
        SavebackVar<String> out = SavebackVar.toMap();
        out.put("greeting", "Hello World!");
        assertEquals(loot.get("greeting"),"Hello World!","put('greeting','Hello World!')");
        out.put("tv.show", "Scooby Doo");
        assertEquals(loot.get("tv.show"),"Scooby Doo","put('tv.show','Scooby Doo')");
        out.put("NOW", "_now");
        assertEquals(loot.get("NOW"),"_now","put('NOW','_now')");
    }

    public void testSavebackArrayIndex()
    {
        Map<String,Object> loot = iniForGetData();
        SavebackVar<String> out = SavebackVar.toObject();
        assertFalse("EDDIE".equals(((String[])loot.get("amigos"))[1]));
        out.put("amigos[1]", "EDDIE");
        assertEquals(((String[])loot.get("amigos"))[1],"EDDIE","replaced slot");
        assertEquals(((String[])loot.get("amigos"))[0],"ed","untouched slot");
    }

    @SuppressWarnings("unchecked")
    public void testSavebackMapElements()
    {
        Map<String,Object> loot = iniForGetData();
        final Map<String,Object> PC = (Map<String,Object>)loot.get("PC");
        Long _1TB= Long.valueOf(1000L);
        SavebackVar<Long> out = SavebackVar.toObject(true);
        assertFalse(PC.containsKey("hd"));
        out.put("PC['hd']",_1TB);
        assertEquals(PC.get("hd"),_1TB,"put('PC[hd]','1TB')");
        assertTrue(Long.class.isInstance(PC.get("hd")),"is kindof Number");
        PC.put("ram", new long[]{1L,2L,2L,1L});
        out.put("PC.ram[0]", 4L);
        long[] ram = (long[])PC.get("ram");
        assertEquals(ram[0],4L,"PC.ram[0]");
    }

    public void testSavebackObjectFields() //try the gamut of object accessors...
    {
        Map<String,Object> loot = iniForGetData();
        SavebackVar<String> svstring = new SavebackVar<String>();
        SavebackVar<Long> svlong =  new SavebackVar<Long>();
        SavebackVar<Character> svchar = new SavebackVar<Character>();

        Grandma grandma = new Grandma();
        assertNull(grandma.getCar(),"grandma's car on init");
        SavebackVar<Person> svperson = new SavebackVar<Person>();
        svperson.put("grandma",grandma);
        assertSame(loot.get("grandma"),grandma,"put(grandma)");
        svstring.put("grandma.car", "Corvette(RED)");
        assertEquals(grandma.getCar(),"Corvette(RED)","grandma's car");
        assertSame(loot.get("grandma"),grandma,"put(grandma.car)");

        long _2pm = TimeUnit.MILLISECONDS.convert(14L, TimeUnit.HOURS);
        Baby alan = new Baby("alan");
        assertEquals(alan.getNapTime(),0L,"alan's naptime on init");
        svperson.put("grandma.grandKid[0]",alan);
        assertSame(grandma.getGrandKid(0),alan,"grandkid[0]");
        svlong.put("grandma.grandKid[0].napTime",_2pm);
        assertEquals(alan.getNapTime(),_2pm,"alan's naptime");
        loot.put("alan", alan);
        svchar.put("alan.gender",'M');
        assertEquals(alan.getGender(),'M',"alan's gender");

        Baby alice = new Baby("alice");
        svperson.put("grandma.grandKid[1]",alice);
        assertSame(grandma.getGrandKid(1),alice,"grandkid[1]");
        loot.put("alice",alice);

        Toy toy1 = new Toy("daa");
        SavebackVar<Toy> svtoy = new SavebackVar<Toy>(false);
        svtoy.put("alice.toys['dolly']",toy1);
        assertSame(alice.getToys().get("dolly"),toy1,"alice.toys('dolly')");
        svstring.put("grandma.grandKid[1].toys['dolly'].sound", "MaMa!");
        assertEquals(alice.getToys().get("dolly").getSound(),"MaMa!","dolly's sound");
    }

    @Test(expectedExceptions={MWf4JException.class})
    public void testFailIfNonPublicBeanClass()
    {
        Map<String,Object> loot = iniForGetData();
        Grandma grandma = new Grandma();
        loot.put("grandma",grandma);
        SavebackVar<Double> svdouble = new SavebackVar<Double>();
        svdouble.put("grandma.pension.amount",Double.valueOf(100000d));
        fail("Should not be able to set pension amount as Pension is a non-public class!");
    }

    public void testIgnoreErrorIfHaltIfErrorDisabled()
    {
        Map<String,Object> loot = iniForGetData();
        Grandma grandma = new Grandma();
        loot.put("grandma",grandma);
        final double original = grandma.getPension().getAmount();
        assertFalse(original==100000d);
        SavebackVar<Double> svdouble = new SavebackVar<Double>(false);
        svdouble.put("grandma.pension.amount",Double.valueOf(100000d));
        assertEquals(grandma.getPension().getAmount(),original,"grandma's pension");
    }

    @Test(expectedExceptions={MWf4JException.class})
    public void testFailIfInvalidExpr()
    {
        iniForGetData();
        SavebackVar<Integer> svint = SavebackVar.toObject();
        svint.put("PC['cores'",Integer.valueOf(4));
        fail("Should not be able to put anything to \"PC['cores'\"");
    }

    @SuppressWarnings("unchecked")
    public void testSkipIfInvalidExprAndNotHaltIfError()
    {
        Map<String,Object> loot = iniForGetData();
        final Map<String,Object> PC = (Map<String,Object>)loot.get("PC");
        assertNull(PC.get("cores"));
        SavebackVar<Integer> svint = SavebackVar.toObject(false);
        assertFalse(svint.put("PC['cores'",Integer.valueOf(4)),"Saved to invalid expr");
        assertNull(PC.get("cores"),"PC['cores']");
    }

    public void testSavebackLocalSystemProperty()
    {
        iniForGetProperty();
        String original = LocalSystem.getProperty("java.version");
        assertFalse("12345".equals(original));
        assertTrue(SavebackProperty.toSystem().put("java.version","12345"),"overwrite 'java.version'");
        assertEquals(LocalSystem.getProperty("java.version"),"12345","overwritten('java.version')");
        assertTrue(SavebackProperty.toSystem().putNull("java.version"));
        assertEquals(LocalSystem.getProperty("java.version"),original,"get after putNull");
    }

    public void testSavebackProperty()
    {
        //remember our ojg.ns is setup as 'mwf4j' which creates a auto-prefix for get/set
        iniForGetProperty();
        String original = LocalSystem.getProperty("java.version");
        assertFalse("12345".equals(original));
        assertTrue(SavebackProperty.toHarness().put("java.version","12345"),"overwrite 'java.version'");
        System.out.println("mwf4j.java.version="+LocalSystem.getProperty("mwf4j.java.version"));
        assertEquals(LocalSystem.getProperty("mwf4j.java.version"),"12345","overwritten('java.version')");
        assertTrue(SavebackProperty.toHarness().putNull("java.version"));
        assertNull(LocalSystem.getProperty("mwf4j.java.version"),"get after putNull");
    }

    public void testSavebackDiscarded()
    {
        assertTrue(new SavebackDiscard<Object>().put(null, null));
        assertTrue(new SavebackDiscard<Integer>().put("fu",Integer.valueOf(1)));
        assertTrue(new SavebackDiscard<Object>().putNull(null));
    }

    public void testSavebackMDC()
    {
        assertFalse(MDC.has("icky.flag"));
        new SavebackMDC<String>().put("icky.flag", "BOOM!");
        assertEquals(MDC.get("icky.flag"),"BOOM!","MDC.get('icky.flag')");
        new SavebackMDC<Integer>().put("icky.flag", Integer.valueOf(10));
        assertEquals(MDC.get("icky.flag", Integer.class), Integer.valueOf(10));
        assertFalse(MDC.has("null.flag"));
        new SavebackMDC<Object>().put("null.flag", null);
        assertTrue(MDC.has("null.flag"),"null value is puttable");
        new SavebackMDC<Object>().putNull("null.flag");
        assertFalse(MDC.has("null.flag"),"removed by putNull");
    }

    public void testSavebackToAdhocMap()
    {
        Map<String,Object> shared = iniForGetData();
        ConcurrentMap<String,Object> mydata = LocalSystem.newThreadSafeMap();
        addSimple(mydata);
        Person person = new Person();
        mydata.put("p", person);
        assertNull(shared.get("p"));
        new SavebackVar<Long>(mydata).put("p.birthDate", Long.valueOf(1000L));
        assertEquals(person.getBirthDate(),1000L,"updated birthdate");
        assertNull(shared.get("p.birthDate"));

        assertNull(person.getName());
        new SavebackVar<String>(mydata).put("p.name","Fred");
        assertEquals(person.getName(),"Fred","updated name");
        assertNull(shared.get("p.name"));
        assertTrue(new SavebackVar<String>(mydata).putNull("p.name"));
        assertNull(person.getName(),"after putNull('p.name')");
    }

    @Test(expectedExceptions={SavebackException.class})
    public void testFailSavbackPropertyIfNoCurrentHarness()
    {
        SavebackProperty.toHarness().put("anything","IMPOSSIBLE!");
        fail("Should not be able to saveback to no HARNESS");
    }
}


/* end-of-SavebackTest.java */
