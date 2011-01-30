/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.helpers;

import  java.util.Calendar;
import  java.util.Date;
import  java.util.Properties;
import  java.util.concurrent.ConcurrentHashMap;

import  org.jwaresoftware.gestalt.Errors;
import  org.jwaresoftware.gestalt.Strings;
import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.helpers.Empties;
import  org.jwaresoftware.gestalt.helpers.Numbers;
import  org.jwaresoftware.gestalt.helpers.PropertyStrings;
import  org.jwaresoftware.gestalt.helpers.Synonyms;

import  org.jwaresoftware.mwf4j.Variables;
import  org.jwaresoftware.mwf4j.What;

/**
 * Default implementation of the MWf4J {@linkplain Variables} interface. Based on
 * the JRE's own ConcurrentHashMap for the storage and concurrency management.
 * For 'orFail' getters, if the requested variable is missing <em>or null</em>
 * this hash map will throw an IllegalStateException always. For 'ofType' getters,
 * this method will throw a ClassCastException if variable exists but is of an
 * incompatible type (note that <i>null</i> values are always returned). Null 
 * keys are considered illegal by all getter methods.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   single
 * @.group    impl,infra
 **/

public final class VariablesHashMap extends ConcurrentHashMap<String,Object> implements Variables
{
    public VariablesHashMap()
    {
        super(23,0.8f);
    }

    public VariablesHashMap(int initialCapacity)
    {
        super(initialCapacity);
    }

    public VariablesHashMap(int initialCapacity, float loadFactor)
    {
        super(initialCapacity,loadFactor);
    }


    @Override
    public Object get(Object name)
    {
        Validate.notNull(name,What.KEY);//IlA not NPE
        return super.get(name);
    }

    @Override
    public Object put(String name, Object value)
    {
        Validate.neitherNull(name,What.KEY,value,What.VALUE);//IlA not NPE
        return super.put(name,value);
    }



    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> ofType)
    {
        Validate.notNull(ofType,"of-class");
        Object o = get(name);
        if (o==null)
            return null;
        if (ofType.isInstance(o)) {
            return (T)o;
        }
        String what = ofType.getSimpleName()+", is kindof "+o.getClass().getSimpleName();
        throw new ClassCastException(Errors.BAD_STATE+"{variable '"+name+"' is not kindof "+what+"}");
    }

    public <T> T getOrFail(String name, Class<T> ofType)
    {
        T value = get(name,ofType);
        Validate.stateNotNull(value,name);
        return value;
    }

    public Object getOrFail(String name)
    {
        Object o = get(name);
        if (o==null && !containsKey(name)) {
            Validate.stateNotNull(o, name);
        }
        return o;
    }


    public String getStringOrNull(String name)
    {
        Validate.notNull(name,What.KEY);
        Object data = get(name);
        return (data==null) ? null : Strings.valueOf(data);
    }

    public final String getString(String name)
    {
        String string = getStringOrNull(name);
        return (string==null && containsKey(name)) ? Strings.NULL : string;
    }

    public final String getString(String name, String dfault)
    {
        String string = getStringOrNull(name);
        return (string==null) ? dfault : string;
    }

    public final String getStringOrFail(String name)
    {
        String string = getStringOrNull(name);
        Validate.stateNotNull(string,name);
        return string;
    }

    public final String getStringOrEmpty(String name)
    {
        return getString(name,Strings.EMPTY);
    }



    public Boolean getFlag(String name)
    {
        Object data = get(name);
        if (data==null || data instanceof Boolean) {
            return (Boolean)data;
        }
        String string = Strings.valueOf(data);
        return Synonyms.Booleans.match(string);
    }

    public final Boolean getFlagOrFail(String name)
    {
        Boolean flag = getFlag(name);
        Validate.stateNotNull(flag,name);
        return flag;
    }

    public final Boolean getFlag(String name, Boolean dfault)
    {
        Boolean flag = getFlag(name);
        return (flag==null) ? dfault : flag;
    }



    public Integer getInteger(String name)
    {
        Object data = get(name);
        Integer num;
        if (data==null) {
            num = null;
        } else if (data instanceof String) {//90%
            num = Numbers.toInteger(String.valueOf(data),null);
        } else if (data instanceof Integer) {//99%
            num = (Integer)data;
        } else if (data instanceof Number) {
            num = ((Number)data).intValue();
        } else {
            num = Numbers.toInteger(Strings.valueOf(data),null);
        }
        return num;
    }

    public final Integer getIntegerOrFail(String name)
    {
        Integer num = getInteger(name);
        Validate.stateNotNull(num,name);
        return num;
    }

    public final Integer getInteger(String name, Integer dfault)
    {
        Integer num = getInteger(name);
        return (num==null) ? dfault : num;
    }



    public Long getLong(String name)
    {
        Object data = get(name);
        Long num;
        if (data==null) {
            num = null;
        } else if (data instanceof String) {//90%
            num = Numbers.toLong(String.valueOf(data),null);
        } else if (data instanceof Long) {//99%
            num = (Long)data;
        } else if (data instanceof Number) {
            num = ((Number)data).longValue();
        } else {
            num = Numbers.toLong(Strings.valueOf(data),null);
        }
        return num;
    }

    public final Long getLongOrFail(String name)
    {
        Long num = getLong(name);
        Validate.stateNotNull(num,name);
        return num;
    }

    public final Long getLong(String name, Long dfault)
    {
        Long num = getLong(name);
        return (num==null) ? dfault : num;
    }



    public Double getDouble(String name)
    {
        Object data = get(name);
        Double num;
        if (data==null) {
            num = null;
        } else if (data instanceof String) {//90%
            num = Numbers.toDouble(String.valueOf(data),null);
        } else if (data instanceof Double) {//99%
            num = (Double)data;
        } else if (data instanceof Number) {
            num = ((Number)data).doubleValue();
        } else {
            num = Numbers.toDouble(Strings.valueOf(data),null);
        }
        return num;
    }

    public final Double getDoubleOrFail(String name)
    {
        Double num = getDouble(name);
        Validate.stateNotNull(num,name);
        return num;
    }

    public final Double getDouble(String name, Double dfault)
    {
        Double num = getDouble(name);
        return (num==null) ? dfault : num;
    }



    public Long getTimestamp(String name)
    {
        Object data = get(name);
        if (data==null || data instanceof Long) {
            return (Long)data;
        }
        if (data instanceof Date) {
            return ((Date)data).getTime();
        }
        if (data instanceof Calendar) {
            return ((Calendar)data).getTimeInMillis();
        }
        return Numbers.toDatetimeFromISO(Strings.valueOf(data),null);
    }

    public final Long getTimestampOrFail(String name)
    {
        Long msecs = getTimestamp(name);
        Validate.stateNotNull(msecs,name);
        return msecs;
    }

    public final Long getTimestamp(String name, Long dfault)
    {
        Long msecs = getTimestamp(name);
        return (msecs==null) ? dfault : msecs;
    }



    public Long getDatestamp(String name)
    {
        Object data = get(name);
        if (data==null || data instanceof Long) {
            return (Long)data;
        }
        if (data instanceof Date) {
            return ((Date)data).getTime();
        }
        if (data instanceof Calendar) {
            return ((Calendar)data).getTimeInMillis();
        }
        return Numbers.toDateFromISO(Strings.valueOf(data),null);
    }

    public final Long getDatestampOrFail(String name)
    {
        Long msecs = getDatestamp(name);
        Validate.stateNotNull(msecs,name);
        return msecs;
    }

    public final Long getDatestamp(String name, Long dfault)
    {
        Long msecs = getDatestamp(name);
        return (msecs==null) ? dfault : msecs;
    }



    public String[] getStrings(String name)
    {
        String string = getStringOrNull(name);
        return PropertyStrings.decodeList(string,null);
    }

    public final String[] getStringsOrEmpty(String name)
    {
        String[] strings = getStrings(name);
        return (strings==null) ? Empties.STRING_ARRAY : strings;
    }

    public String[] getStrings(String name, String delims)
    {
        String string = getStringOrNull(name);
        return PropertyStrings.decodeList(string,delims);
    }

    public final String[] getStringsOrEmpty(String name, String delims)
    {
        String[] strings = getStrings(name,delims);
        return (strings==null) ? Empties.STRING_ARRAY : strings;
    }



    public Properties getPairs(String name, String delim)
    {
        String string = getStringOrNull(name);
        return PropertyStrings.createPairs(string,delim,name);
    }

    public final Properties getPairs(String name)
    {
        return getPairs(name,null);
    }

    public Properties getPairsOrFail(String name, String delim)
    {
        String string = getStringOrNull(name);
        Validate.stateNotNull(string,name);
        Properties properties = PropertyStrings.createPairs(string,delim,name);
        Validate.stateNotNull(properties,name);
        return properties;
    }

    public final Properties getPairsOrFail(String name)
    {
        return getPairsOrFail(name,null);
    }

    public final Properties getPairsOrEmpty(String name, String delim)
    {
        Properties properties = getPairs(name,delim);
        return (properties==null) ? Empties.PROPERTIES : properties;
    }

    public final Properties getPairsOrEmpty(String name)
    {
        return getPairsOrEmpty(name,null);
    }
}


/* end-of-VariablesHashMap.java */
