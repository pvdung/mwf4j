/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.behaviors;

/**
 * Mixin interface for any component that depends on other components that 
 * can contain declared references whose resolution should be deferred 
 * until (just before) use. Note that the enabled component itself 
 * <em>may not</em> be directly {@linkplain Declarable declarable}. Also
 * the application (or context) needs to activate declarable processing
 * <em>explicitly</em> in most cases. This allows programmatic uses of 
 * MWf4J framework that do not involve declarative configuration to bypass
 * the overhead that such pre-processing would add to statement prep-time.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2011 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,impl,helper
 * @see       org.jwaresoftware.mwf4j.helpers.Declarables Declarables
 **/

public interface DeclarableEnabled
{
    /** Default setting for declarable resolution: OFF. */
    public static final boolean DEFAULT_CHECK_DECLARABLES_SETTING=false;

    /**
     * Tells this object <em>explicitly</em> whether to process
     * declarables or not. Use to define a setting in preference to
     * any default setting.
     * @param flag <i>true</i> or <i>false</i> to set processing
     **/
    void setCheckDeclarables(boolean flag);


    /**
     * Returns whether this object will process declarables. If this
     * object's flag was never set explicitly, this method will return
     * whatever the default setting is for its context.
     * @return <i>true</i> if declarables processed; else <i>false</i>
     **/
    boolean isCheckDeclarables();

    
    /**
     * Returns this object's <em>own</em> setting for whether to resolve
     * declarables. Note that <i>null</i> means, this object will use its
     * context default if needed.
     * @return this object's flag or <i>null</i> if never set explicitly
     **/
    Boolean getCheckDeclarables();
}


/* end-of-DeclarableEnabled.java */
