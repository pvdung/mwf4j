/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j.assign;

import  org.apache.commons.jexl2.JexlEngine;
import  org.apache.commons.jexl2.introspection.Uberspect;
import  org.apache.commons.jexl2.introspection.UberspectImpl;

import  org.apache.commons.logging.Log;
import  org.apache.commons.logging.LogFactory;

import  org.jwaresoftware.mwf4j.Diagnostics;
import  org.jwaresoftware.mwf4j.Feedback;
import  org.jwaresoftware.mwf4j.Harness;
import  org.jwaresoftware.mwf4j.MDC;
import  org.jwaresoftware.mwf4j.MWf4J;

/**
 * Local modifications to the standard JexlEngine creation for MWf4J. Always given  
 * precedence to a per-harness specified engine instance (preferred mechanism if  
 * reuse of an engine is important). Otherwise, creates a new STRICT engine instance
 * for each call to {@linkplain #getEngine() or #newEngine()}. Patterned after 
 * JexlEngine's own default constructor.
 *
 * @since     JWare/MWf4j 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   multiple
 * @.group    impl,helper
 * @see       MWf4J.ServiceIds#JEXLENGINE
 **/
final class MyJexl
{
    private static final Log _LOG = LogFactory.getLog(Diagnostics.ForCore.getName()+".Jexl");
    private static final Uberspect _UBERSPECT = new UberspectImpl(_LOG);
    private MyJexl() { /*only static apis*/ }


    static final JexlEngine newEngine()
    {
        JexlEngine je = new JexlEngine(_UBERSPECT,null,null,_LOG);
        je.setLenient(false);
        return je;
    }


    static final JexlEngine getEngine()
    {
        JexlEngine engine = null;
        try {
            Harness harness = MDC.currentHarness();
            engine= harness.getServiceInstanceOrNull(MWf4J.ServiceIds.JEXLENGINE, JexlEngine.class, harness.getIssueHandler());
        } catch(RuntimeException spiX) {
            Feedback.ForCore.warn("Error looking for jexlEngine as JexlEngine.class type; using default",spiX);
        }
        if (engine==null) {
            engine = newEngine();
        }
        return engine;
    }
}

/* end-of-MyJexl.java */
