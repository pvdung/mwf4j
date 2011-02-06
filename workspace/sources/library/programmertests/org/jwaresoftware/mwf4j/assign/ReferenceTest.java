/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

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
    public void testVoidCtorIsUndefined() {
        Reference out = new Reference();
        assertTrue(out.isUndefined(),"isUndefined");
        assertNotNull(out.getStoreType(), "storeType");
        Reference cpy = new Reference(out);
        assertTrue(out.isUndefined(),"cpy.isUndefined");
        assertSame(out.getStoreType(), cpy.getStoreType(),"cpy.storeType");
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

    @Test (expectedExceptions= {IllegalArgumentException.class})
    public void testFailSetKeyNull()
    {
        Reference out = new Reference();
        out.setName(null);
    }

    @Test (expectedExceptions= {IllegalArgumentException.class})
    public void testFailSetKeyBlank()
    {
        Reference out = new Reference();
        out.setName("  ");
    }

    @Test (expectedExceptions= {IllegalArgumentException.class})
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

    public void testUndefine()
    {
        Reference out = new Reference("d");
        assertFalse(out.isUndefined(),"undefined");
        out.reset();
        assertTrue(out.isUndefined(),"undefined");
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
}


/* end-of-ReferenceTest.java */
