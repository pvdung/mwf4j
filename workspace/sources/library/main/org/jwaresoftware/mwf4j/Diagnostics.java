/**
 * $Id$
@JAVA_SOURCE_HEADER@
 **/

package org.jwaresoftware.mwf4j;

import  org.slf4j.ext.XLogger;
import  org.slf4j.ext.XLoggerFactory;

/**
 * MWf4j specific diagnostic loggers.
 *
 * @since     JWare/MWf4J 1.0.0
 * @author    ssmc, &copy;2010 <a href="@Module_WEBSITE@">SSMC</a>
 * @version   @Module_VERSION@
 * @.safety   n/a
 * @.group    api,infra
 **/

public class Diagnostics extends org.jwaresoftware.gestalt.Diagnostics
{
    public static final String GROUPING_MWF4J = GROUPING_ROOT+Feedback.NSSUFFIX;
    public static final XLogger MWF4J = XLoggerFactory.getXLogger(GROUPING_MWF4J);

    public static final String GROUPING_CORE = GROUPING_SERVICES+Feedback.NSSUFFIX;
    public static final XLogger ForCore = XLoggerFactory.getXLogger(GROUPING_CORE);

    public static final String GROUPING_FLOW = GROUPING_CORE+".Flow";
    public static final XLogger ForFlow = XLoggerFactory.getXLogger(GROUPING_FLOW);

    public static final String GROUPING_BAL = GROUPING_FLOW+".BAL";
    public static final XLogger ForBAL = XLoggerFactory.getXLogger(GROUPING_BAL);

    protected Diagnostics() {}
}


/* end-of-Diagnostics.java */
