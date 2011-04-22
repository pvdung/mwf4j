/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.jwaresoftware.gestalt.Validate;
import  org.jwaresoftware.gestalt.reveal.CloneableSkeleton;
import  org.jwaresoftware.gestalt.system.LocalSystem;

import  org.jwaresoftware.mwf4j.Fixture;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.What;
import  org.jwaresoftware.mwf4j.behaviors.Declarable;
import  org.jwaresoftware.mwf4j.helpers.Declarables;

/**
 * Giveback that returns the value of a predefined harness or (local)
 * system property. Between calls the value of the property can change
 * (it's up to user to assure concurrency semantics). For harness-based
 * configuration properties, we expect the harness has been set in the
 * current thread's MDC; see {@linkplain MDC#currentHarness()}. If 
 * property is not present, this giveback returns your supplied
 * <i>fallback</i> when asked for its value. The default
 * constructors (no 'mode' specified), assume you want to read
 * harness-based properties.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010-2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   special (multiple after frozen)
 * @.group    impl,infra,helper
 * @see       MDC#currentConfiguration()
 **/

public final class GivebackProperty extends CloneableSkeleton implements Giveback<String>, Declarable
{
    /** Marker used to differentiate between harness and system based properties. **/
    public static enum Source {
        SYSTEM, HARNESS;
    }

    public final static GivebackProperty fromSystem(String property, String fallbackValue)
    {
        return new GivebackProperty(property,fallbackValue,Source.SYSTEM);
    }

    public final static GivebackProperty fromHarness(String property)
    {
        return new GivebackProperty(property,null,Source.HARNESS);
    }

    public final static GivebackProperty fromHarness(String property, String fallbackValue)
    {
        return new GivebackProperty(property,fallbackValue,Source.HARNESS);
    }

    public GivebackProperty(String property, String fallbackValue, Source source)
    {
        Validate.notBlank(property,What.PROPERTY);
        myProperty = property;
        myFallbackValue = fallbackValue;
        myFromSystemFlag = Source.SYSTEM.equals(source);
    }

    public GivebackProperty(String property, String fallbackValue)
    {
        this(property,fallbackValue,Source.HARNESS);
    }

    public GivebackProperty(String property)
    {
        this(property,null);
    }

    public String call()
    {
        String value;
        try {
            if (myFromSystemFlag) {
                value = LocalSystem.getProperty(myProperty,myFallbackValue);
            } else {
                value = MDC.currentConfiguration().getString(myProperty,myFallbackValue);
            }
        } catch(RuntimeException stateX) {
            throw new GivebackException(myProperty,stateX);
        }
        return value;
    }

    public void freeze(Fixture environ)
    {
        myProperty = Declarables.freeze(environ,myProperty);
        myFallbackValue = Declarables.freeze(environ,myFallbackValue);
    }


    private String myProperty;
    private String myFallbackValue;
    private final boolean myFromSystemFlag;
}


/* end-of-GivebackProperty.java */
