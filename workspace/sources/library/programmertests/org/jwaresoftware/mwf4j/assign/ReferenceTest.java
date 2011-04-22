/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  java.util.Collections;
import  java.util.List;
import  java.util.Map;

import  org.jwaresoftware.gestalt.system.LocalSystem;
import  org.jwaresoftware.mwf4j.LocalSystemHarness;

import  org.testng.annotations.AfterMethod;
import  org.testng.annotations.Test;
import  static org.testng.Assert.*;

/**
 * Test suite for {@linkplain Reference}.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,test
 **/

@Test(groups= {"mwf4j","baseline","assignment"})
public final class ReferenceTest
{

    @AfterMethod
    protected void tearDown() throws Exception {
        LocalSystem.unsetProperties();
    }

    public void testVoidCtorIsUndefined() {
        Reference out = new Reference();
        assertTrue(out.isUndefined(),"isUndefined");
        assertNotNull(out.getStoreType(), "storeType");
        Reference cpy = new Reference(out);
        assertTrue(out.isUndefined(),"cpy.isUndefined");
        assertSame(out.getStoreType(), cpy.getStoreType(),"cpy.storeType");
        out = new Reference(StoreType.NONE);
        assertTrue(out.isUndefined(),"isUndefined");
        assertSame(out.getStoreType(),StoreType.NONE);
    }

    public void testDefaultStoreTypeIsDatamap()
    {
        Reference out = new Reference();
        assertSame(out.getStoreType(),StoreType.DATAMAP,"storeType");
        out = new Reference("a");
        assertSame(out.getStoreType(),StoreType.DATAMAP,"storeType");
        Reference cpy = new Reference(out);
        assertSame(cpy.getStoreType(),StoreType.DATAMAP,"cpy.storeType");
    }

    public void testCustomKey()
    {
        Reference out = new Reference("b");
        assertTrue(out.isDefined(),"isDefined");
        assertFalse(out.isUndefined(),"isUndefined");
        assertEquals(out.getId(),"b","id");
        assertEquals(out.getId(),"b","name");
        out.setName("fu");
        assertEquals(out.getId(),"fu","id");
        assertEquals(out.getId(),"fu","name");
    }

    public void testCustomStoreType()
    {
        Reference out = new Reference("c",StoreType.PROPERTY);
        assertSame(out.getStoreType(),StoreType.PROPERTY,"storeType");
        assertEquals(out.getId(), "c","id");
        Reference cpy = new Reference(out);
        assertSame(cpy.getStoreType(),StoreType.PROPERTY,"cpy.storeType");
        assertEquals(cpy.getId(), "c","cpy.id");
        out.setStoreType(StoreType.NONE);
        assertSame(out.getStoreType(),StoreType.NONE,"storeType[2]");
        assertSame(cpy.getStoreType(),StoreType.PROPERTY,"cpy.storeType[2]");
    }

    @Test(dependsOnMethods={"testCustomStoreType"})
    public void testCopyFromNullsMakesNewUndefined()
    {
        Reference out = new Reference("w");
        assertTrue(out.isDefined());
        out.copyFrom(null,null);
        assertFalse(out.isDefined(),"copyFrom(null,null)");
        assertTrue(out.isUndefined());
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailSetKeyNull()
    {
        Reference out = new Reference();
        out.setName(null);
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailSetKeyBlank()
    {
        Reference out = new Reference();
        out.setName("  ");
    }

    @Test(expectedExceptions= {IllegalArgumentException.class})
    public void testFailSetStoreTypeNull()
    {
        Reference out = new Reference("d");
        out.setStoreType(null);
    }

    @Test(dependsOnMethods={"testCustomStoreType"})
    public void testReinitFromOther()
    {
        Reference oth = new Reference("my.property",StoreType.SYSTEM);
        Reference out = new Reference("error",StoreType.NONE);
        out.copyFrom(oth);
        assertSame(out.getStoreType(),StoreType.SYSTEM,"storeType");
        assertEquals(out.getId(), "my.property","id");
    }

    @Test(dependsOnMethods={"testCustomStoreType"})
    public void testReinitFromIndivFields()
    {
        Reference out = new Reference("error",StoreType.NONE);
        out.copyFrom("my.property",StoreType.SYSTEM);
        assertSame(out.getStoreType(),StoreType.SYSTEM,"storeType");
        assertEquals(out.getId(), "my.property","id");
    }

    @Test(dependsOnMethods={"testVoidCtorIsUndefined"})
    public void setReinitFromOtherRef()
    {
        Reference out = new Reference();
        assertTrue(out.isUndefined());
        out.set(new Reference("DEBUG",StoreType.SYSTEM));
        assertEquals(out.getId(),"DEBUG","name");
        assertSame(out.getStoreType(),StoreType.SYSTEM,"storeType");
    }

    public void testUndefine()
    {
        Reference out = new Reference("d");
        assertTrue(out.isDefined(),"defined");
        assertFalse(out.isUndefined(),"undefined");
        out.reset();
        assertTrue(out.isUndefined(),"undefined");
        assertFalse(out.isDefined(),"defined");
    }

    public void testNewFromApi()
    {
        Reference src;
        Reference out= Reference.newFrom(null);
        assertNull(out,"newFrom(null)");
        src = new Reference();
        out = Reference.newFrom(src);
        assertTrue(out.isUndefined());
        src = new Reference("f");
        out = Reference.newFrom(src);
        assertEquals(out,src,"cpy");
    }

    public void testSetKeyAndTypeAtOnce()
    {
        Reference out = new Reference();
        out.set("flip.e",StoreType.SYSTEM);
        assertEquals(out.getName(),"flip.e","name");
        assertSame(out.getStoreType(),StoreType.SYSTEM,"storeType");
        try {
            out.set(null,StoreType.OBJECT);
            fail("Should not be able to set to 'null' key");
        } catch(IllegalArgumentException Xpected) {
            assertTrue(Xpected.getMessage().contains("'key' is null or blank"));
        }
        try {
            out.set("flip.z",null);
            fail("Should not be able to set to 'null' storeType");
        } catch(IllegalArgumentException Xpected) {
            assertTrue(Xpected.getMessage().contains("'type' is null"));
        }
    }

    public void testUsefulAsCollectionComponents_1_0_0()
    {
        Reference unde = new Reference();
        Reference aaaa = new Reference("a");
        List<Reference> all= LocalSystem.newList();
        all.add(unde);//undefined
        all.add(aaaa);
        all.add(new Reference("a",StoreType.SYSTEM));
        all.add(Reference.class.cast(new Reference("a",StoreType.DATAMAP).clone()));
        all.add(new Reference(StoreType.THREAD));//undefined
        all.add(new Reference("z",StoreType.THREAD));
        all.add(Reference.newFrom(unde));//undefined
        all.add(new Reference("z"));
        all.add(aaaa);//same ref
        
        List<Reference> q= LocalSystem.newList();
        q.add(new Reference("--"));
        
        Collections.sort(all);

        StringBuffer sb = LocalSystem.newStringBuffer();
        sb.append("REF-COLL [");
        for (Reference ref:all) {
            sb.append(ref.isUndefined() ? 'U' : 'D');
            sb.append(ref.getStoreType().name().charAt(0));
            sb.append(ref.isDefined() ? ("="+ref.getId()) : "?");
            sb.append("|");            
        }
        sb.append("]");
        LocalSystem.show(sb.toString());

        assertTrue(all.indexOf(unde)<all.indexOf(new Reference("a")),"undefined < defined");
        assertEquals(Collections.frequency(all,Reference.class.cast(unde.clone())),3,"undefined.size");
        assertTrue(Collections.disjoint(all,q),"disjoint(all,q)");

        q.add(unde);
        all.removeAll(q);
        assertEquals(Collections.frequency(all,unde),0,"undefined.size");
        
        Map<Reference,String> map = LocalSystem.newMap();
        map.put(aaaa, "Earth");
        map.put(new Reference(aaaa.getId(),StoreType.PROPERTY), "Mars");
        map.put(new Reference("e"), "Venus");
        assertEquals(map.get(new Reference(aaaa.getName())),"Earth","lookup by 'a'[DATAMAP]");
        map.remove(aaaa);
        assertEquals(map.get(new Reference(aaaa.getId(),StoreType.PROPERTY)),"Mars","lookup by 'a'[PROPERTY]");
        assertTrue(map.containsKey(new Reference("e")),"'e'[DATAMAP] item still exists");
        assertNull(map.get(aaaa),"lookup by 'a'[DATAMAP]");
    }

    public void testWireable_1_0_0()
    {
        LocalSystemHarness environ = new LocalSystemHarness();
        LocalSystem.setProperty("today","WEDNESDAY");
        Reference r = new Reference("label.${today}");
        r.freeze(environ);
        assertEquals(r.getName(),"label.WEDNESDAY","name=label.${today}");
        r.setName("NoLinks");
        r.freeze(environ);
        assertEquals(r.getName(),"NoLinks","name=NoLinks");
        r = new Reference();
        assertTrue(r.isUndefined());
        r.freeze(environ);
        assertTrue(r.isUndefined());
    }
}


/* end-of-ReferenceTest.java */
